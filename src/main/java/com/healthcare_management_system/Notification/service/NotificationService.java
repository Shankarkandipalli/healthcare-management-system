package com.healthcare_management_system.Notification.service;

import com.healthcare_management_system.Notification.dtos.NotificationDTO;
import com.healthcare_management_system.users.entity.User;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);
   // void sendWhatsApp(NotificationDTO notificationDTO, User user);

}
