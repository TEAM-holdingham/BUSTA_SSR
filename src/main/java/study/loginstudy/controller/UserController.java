package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.loginstudy.domain.dto.UserProfile;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 기존 코드: HTML 페이지 반환 및 리다이렉트

    @GetMapping("/my-page")
    public String myPage(Model model) {
        User user = userService.getCurrentUser();
        UserProfile userProfile = new UserProfile();
        userProfile.setNickname(user.getNickname());
        userProfile.setLoginId(user.getLoginId());
        userProfile.setIntroduction(user.getIntroduction());
        userProfile.setPhoneNumber(user.getPhoneNumber());
        userProfile.setBirthDate(user.getBirthDate());
        userProfile.setGender(user.getGender());

        model.addAttribute("userProfile", userProfile);
        return "my_page";  // my_page.html 템플릿으로 데이터 전달
    }




    @PostMapping("/update")
    public String updateProfile(UserProfile userProfile, @RequestParam("profilePicture") MultipartFile profilePicture) {
        try {
            userService.updateUserProfile(userProfile, profilePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteProfile() {
        userService.deleteAccount(userService.getCurrentUser().getLoginId());
        return "redirect:/logout";
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<User>> searchFriends(Principal principal, @RequestParam String nickname) {
        String currentUserNickname = userService.getNicknameByLoginId(principal.getName());
        List<User> users = userService.findUsersByNicknameStartingWithExcludingCurrentUser(nickname, currentUserNickname);
        return ResponseEntity.ok(users);
    }

    // 새로 추가된 API: JSON 응답을 위한 메서드들

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiGetProfile() {
        User user = userService.getCurrentUser(); // 현재 로그인된 사용자 정보 가져오기
        UserProfile userProfile = new UserProfile();
        userProfile.setNickname(user.getNickname());
        userProfile.setLoginId(user.getLoginId());
        userProfile.setIntroduction(user.getIntroduction());
        userProfile.setPhoneNumber(String.valueOf(user.getPhoneNumber()));
        userProfile.setBirthDate(user.getBirthDate());
        userProfile.setGender(user.getGender());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("userProfile", userProfile);
        return ResponseEntity.ok(response);  // JSON 형식으로 사용자 프로필 반환
    }

    @PostMapping("/api/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiUpdateProfile(@RequestBody UserProfile userProfile, @RequestParam("profilePicture") MultipartFile profilePicture) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.updateUserProfile(userProfile, profilePicture);
            response.put("status", "success");
            response.put("message", "Profile updated successfully");
        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Failed to update profile");
        }
        return ResponseEntity.ok(response);  // JSON 형식으로 결과 반환
    }

    @PostMapping("/api/delete")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiDeleteProfile() {
        userService.deleteAccount(userService.getCurrentUser().getLoginId());
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Account deleted successfully");
        response.put("redirectUrl", "/logout");  // 리다이렉트 URL 정보 포함
        return ResponseEntity.ok(response);  // JSON 형식으로 결과 반환
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiSearchFriends(Principal principal, @RequestParam String nickname) {
        String currentUserNickname = userService.getNicknameByLoginId(principal.getName());
        List<User> users = userService.findUsersByNicknameStartingWithExcludingCurrentUser(nickname, currentUserNickname);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("users", users);
        return ResponseEntity.ok(response);  // JSON 형식으로 검색 결과 반환
    }
}
