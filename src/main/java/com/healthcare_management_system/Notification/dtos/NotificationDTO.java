package com.healthcare_management_system.Notification.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare_management_system.enums.NotificationType;
import com.healthcare_management_system.users.dtos.UserDTO;
import lombok.*;


import java.time.LocalDateTime;
import java.util.Map;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String subject;
    private String recipient;
    private String message;
    private NotificationType type;
    private UserDTO userDTO;
    private LocalDateTime createdAt;
    private String templateName;
    private Map<String , Object> templateVariables;
}
