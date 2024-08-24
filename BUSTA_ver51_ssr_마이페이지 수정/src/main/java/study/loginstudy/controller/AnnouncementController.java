package study.loginstudy.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementController {

    private final UserService userService;

    @GetMapping("/announcement-main")
    public String announcementMainPage(Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }
        return "announcement_main";  // announcement_main.html 파일로 이동
    }

    @GetMapping("/announcement-in")
    public String announcementInPage(Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }
        return "announcement_in";  // announcement_in.html 파일로 이동
    }
}
