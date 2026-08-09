
package com.healthcare_management_system.Notification.service.Impl;

import com.healthcare_management_system.Notification.dtos.NotificationDTO;
import com.healthcare_management_system.Notification.entity.Notification;
import com.healthcare_management_system.Notification.repository.NotificationRepository;
import com.healthcare_management_system.Notification.service.NotificationService;
import com.healthcare_management_system.enums.NotificationType;
import com.healthcare_management_system.users.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendEmail(NotificationDTO notificationDTO, User user) {
        String recipient = notificationDTO.getRecipient();

        log.info("Starting email notification. Recipient: {}, Subject: {}",
                recipient, notificationDTO.getSubject());
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(recipient);
            helper.setSubject(notificationDTO.getSubject());

            if (notificationDTO.getTemplateName() != null
                    && !notificationDTO.getTemplateName().isBlank()) {

                log.debug("Using email template: {} for recipient: {}",
                        notificationDTO.getTemplateName(), recipient);

                Context context = new Context();

                if (notificationDTO.getTemplateVariables() != null) {
                    context.setVariables(notificationDTO.getTemplateVariables());
                }

                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);

                helper.setText(htmlContent, true);

                log.debug("Email template processed successfully for: {}", recipient);
            } else {
                log.debug("No email template provided. Using message content for: {}", recipient);
                helper.setText(notificationDTO.getMessage(), true);
            }

            log.info("Sending email to: {}", recipient);

            javaMailSender.send(mimeMessage);

            log.info("Email sent successfully to: {}", recipient);

            Notification notificationToSave = Notification.builder()
                    .recipient(recipient)
                    .subject(notificationDTO.getSubject())
                    .message(notificationDTO.getMessage())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepository.save(notificationToSave);

            log.info("Email notification saved successfully. User ID: {}, Recipient: {}",
                    user.getId(), recipient);

        } catch (MessagingException e) {
            log.error("Failed to send email to: {}. Error: {}",
                    recipient, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while processing email notification for: {}",
                    recipient, e);
        }
    }
}

