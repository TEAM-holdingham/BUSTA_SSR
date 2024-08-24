package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.loginstudy.domain.dto.EmailMessage;
import study.loginstudy.domain.entity.PasswordResetToken;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.EmailService;
import study.loginstudy.service.UserService;
import study.loginstudy.repository.UserRepository;
import study.loginstudy.repository.PasswordResetTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class EmailController {

    private final EmailService emailService;
//    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @PostMapping("/send-email")
    public ResponseEntity sendEmail(@RequestParam String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("No user found with loginId: " + loginId));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUser(user);

        passwordResetTokenRepository.save(resetToken);

        String resetLink = "http://yourdomain.com/password-reset/reset?token=" + token;
        String emailText = "비밀번호 재설정을 위한 이메일입니다. 아래 링크를 클릭하여 비밀번호를 재설정하세요:\n" + resetLink;

        EmailMessage emailMessage = EmailMessage.builder()
                .to(user.getLoginId())
                .subject("비밀번호 재설정")
                .message(emailText)
                .build();
        emailService.sendMail(emailMessage);

        return new ResponseEntity(HttpStatus.OK);
    }
}
