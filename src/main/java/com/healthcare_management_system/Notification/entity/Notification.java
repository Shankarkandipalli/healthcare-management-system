package com.healthcare_management_system.Notification.entity;

import com.healthcare_management_system.enums.NotificationType;
import com.healthcare_management_system.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "notifications")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String recipient;
    private String message;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private final LocalDateTime createdAt = LocalDateTime.now();


}
