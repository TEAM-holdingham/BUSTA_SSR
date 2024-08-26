package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.service.EmailVerificationService;
import study.loginstudy.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/password-reset")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    // 비밀번호 재설정을 위한 OTP 요청
    @PostMapping("/request")
    @ResponseBody
    public String requestPasswordReset(@RequestParam String loginId) {
        emailVerificationService.sendPasswordResetOtp(loginId);
        return "Password reset OTP sent.";
    }

    // 비밀번호 재설정 OTP 검증
    @PostMapping("/verify-otp")
    @ResponseBody
    public String verifyOtp(@RequestParam String loginId, @RequestParam String otp) {
        boolean isValid = emailVerificationService.verifyPasswordResetOtp(loginId, otp);
        return isValid ? "Valid OTP" : "Invalid or expired OTP";
    }

    @GetMapping("/reset")
    public String showResetForm(@RequestParam String loginId, Model model) {
        model.addAttribute("loginId", loginId);
        return "reset-password-form";
    }

    // 비밀번호 재설정 OTP 검증 및 비밀번호 재설정
    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> payload) {
        String loginId = payload.get("loginId");
        String otp = payload.get("otp");
        String newPassword = payload.get("newPassword");
        Map<String, String> response = new HashMap<>();

        System.out.println("Received loginId: " + loginId);
        System.out.println("Received OTP: " + otp);
        System.out.println("New Password: " + newPassword);

        try {
            boolean isValidOtp = emailVerificationService.verifyPasswordResetOtp(loginId, otp);
            if (isValidOtp) {
                if (userService.resetPassword(loginId, newPassword)) {
                    response.put("status", "success");
                    response.put("message", "Password successfully reset.");
                } else {
                    response.put("status", "error");
                    response.put("message", "Failed to reset password.");
                }
            } else {
                response.put("status", "error");
                response.put("message", "Invalid or expired OTP.");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error occurred: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }





    // API 버전
    @PostMapping("/api/request")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiRequestPasswordReset(@RequestParam String loginId) {
        emailVerificationService.sendPasswordResetOtp(loginId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Password reset OTP sent.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/verify-otp")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiVerifyOtp(@RequestParam String loginId, @RequestParam String otp) {
        Map<String, String> response = new HashMap<>();

        boolean isValid = emailVerificationService.verifyPasswordResetOtp(loginId, otp);
        if (isValid) {
            response.put("status", "success");
            response.put("message", "Valid OTP");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid or expired OTP");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/api/reset")
    @ResponseBody
    public ResponseEntity<Map<String, String>> apiResetPassword(@RequestParam String loginId, @RequestParam String newPassword) {
        Map<String, String> response = new HashMap<>();

        if (userService.resetPassword(loginId, newPassword)) {
            response.put("status", "success");
            response.put("message", "Password successfully reset.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid or expired OTP.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
