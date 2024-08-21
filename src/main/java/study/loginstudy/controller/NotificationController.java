package study.loginstudy.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.dto.NotificationRequest;
import study.loginstudy.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/admin")
    public String sendAdminNotification(@RequestBody NotificationRequest request) {
        String message = "Admin: " + request.getMessage();
        notificationService.sendAdminNotification(request.getMessage());
        return message;
    }

    @PostMapping("/friend-request")
    public String sendFriendRequestNotification(@RequestBody NotificationRequest request) {
        String message = "Friend Request: " + request.getMessage();
        notificationService.sendFriendRequestNotification(request.getMessage());
        return message;
    }
}


