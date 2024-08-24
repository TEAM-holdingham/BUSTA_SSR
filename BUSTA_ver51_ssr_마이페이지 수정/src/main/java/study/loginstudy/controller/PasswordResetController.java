package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/password-reset")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    // 기존 코드: HTML 페이지 반환 및 단순 문자열 응답
    @PostMapping("/request")
    @ResponseBody
    public String requestPasswordReset(@RequestParam String loginId) {
        userService.sendPasswordResetEmail(loginId);
        return "Password reset email sent.";
    }

    @GetMapping("/reset")
    public String showResetForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password-form";
    }

    @PostMapping("/reset")
    @ResponseBody
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        if (userService.resetPassword(token, newPassword)) {
            return "Password successfully reset.";
        } else {
            return "Invalid or expired token.";
        }
    }

    // 새로 추가된 API: JSON 응답을 위한 메서드들
    @PostMapping("/api/request")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiRequestPasswordReset(@RequestParam String loginId) {
        userService.sendPasswordResetEmail(loginId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Password reset email sent.");
        return ResponseEntity.ok(response);  // JSON 형식으로 성공 메시지 반환
    }

    @GetMapping("/api/reset")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiShowResetForm(@RequestParam String token) {
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("status", "success");
        response.put("message", "Reset form token provided.");
        return ResponseEntity.ok(response);  // JSON 형식으로 토큰과 메시지 반환
    }

    @PostMapping("/api/reset")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiResetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Map<String, String> response = new HashMap<>();

        if (userService.resetPassword(token, newPassword)) {
            response.put("status", "success");
            response.put("message", "Password successfully reset.");
            return ResponseEntity.ok(response);  // JSON 형식으로 성공 메시지 반환
        } else {
            response.put("status", "error");
            response.put("message", "Invalid or expired token.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);  // JSON 형식으로 오류 메시지 반환
        }
    }
}
