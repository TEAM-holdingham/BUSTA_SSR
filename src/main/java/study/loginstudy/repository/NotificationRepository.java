package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByOrderByCreatedAtDesc();

}
