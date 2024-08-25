package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import study.loginstudy.domain.entity.Notification;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.NotificationService;
import study.loginstudy.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementController {

    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping("/announcement-main")
    public String announcementMainPage(Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);

        return "announcement_main";  // announcement_main.html 파일로 이동
    }

    @GetMapping("/announcement-in/{id}")
    public String announcementInPage(@PathVariable Long id, Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        Notification notification = notificationService.getNotificationById(id);
        model.addAttribute("notification", notification);

        return "announcement_in";  // announcement_in.html 파일로 이동
    }
}
