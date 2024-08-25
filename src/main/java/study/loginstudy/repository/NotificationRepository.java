package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
