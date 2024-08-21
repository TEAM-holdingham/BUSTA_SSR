package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.service.EmailVerificationService;
import study.loginstudy.service.UserService;
import study.loginstudy.domain.dto.JoinRequest;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    @PostMapping("/api/sendVerificationEmail")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String loginId = request.get("loginId");
        emailVerificationService.sendVerificationEmail(loginId);
        return ResponseEntity.ok("Verification email sent");
    }

    @GetMapping("/api/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean isVerified = emailVerificationService.verifyEmailToken(token);
        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }

    @PostMapping("/{loginType}/join")
    public ResponseEntity<String> signup(@PathVariable String loginType, @RequestBody JoinRequest joinRequest) {
        try {
            userService.join2(joinRequest);
            return ResponseEntity.ok("Signup successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
