package com.healthcare_management_system.Notification.repository;


import com.healthcare_management_system.Notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
