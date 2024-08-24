package study.loginstudy.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.dto.NotificationRequest;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.NotificationService;
import study.loginstudy.service.UserService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
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

    @GetMapping("/main")
    public String notificationMainPage() {
        return "notification_main";
    }



}


