package study.loginstudy.service;



import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import study.loginstudy.domain.entity.Notification;
import study.loginstudy.repository.NotificationRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Transactional
@Service
public class NotificationService {

    private final List<SseEmitter> emitters = new ArrayList<>();
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendNotification(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    public void sendAdminNotification(String message) {
        Notification notification = new Notification();
        notification.setMessage("Admin: " + message);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }


    public void sendFriendRequestNotification(String message) {
        sendNotification("Friend Request: " + message);
    }


}
