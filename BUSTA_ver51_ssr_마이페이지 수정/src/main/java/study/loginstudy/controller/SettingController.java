package study.loginstudy.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import study.loginstudy.domain.dto.UserProfile;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;

@Controller
@RequestMapping("/settings")
public class SettingController {

    private final UserService userService;

    public SettingController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/main")
    public String settingsPage(Model model, Authentication authentication) {
        // 인증되지 않은 사용자인 경우 처리
        if (authentication == null) {
            model.addAttribute("errorMessage", "인증되지 않은 사용자입니다.");
            return "errorPage/unauthorized";  // 인증되지 않은 경우 에러 페이지로 리다이렉트
        }

        String loginId = authentication.getName();
        User user = userService.findByLoginId(loginId);

        if (user != null) {
            // UserProfile 객체를 생성하여 필요한 사용자 정보를 설정
            UserProfile userProfile = new UserProfile();
            userProfile.setNickname(user.getNickname());
            userProfile.setLoginId(user.getLoginId());
            userProfile.setIntroduction(user.getIntroduction());
            userProfile.setPhoneNumber(user.getPhoneNumber());
            userProfile.setBirthDate(user.getBirthDate());
            userProfile.setGender(user.getGender());

            // 모델에 사용자 프로필 정보를 추가
            model.addAttribute("userProfile", userProfile);

            return "settings_main";  // settings_main.html 템플릿으로 데이터 전달
        } else {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "errorPage/userNotFound";  // 사용자를 찾을 수 없는 경우 에러 페이지로 리다이렉트
        }
    }

}