package study.loginstudy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.loginstudy.domain.dto.EmailMessage;
import study.loginstudy.domain.entity.EmailVerificationToken;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.EmailVerificationTokenRepository;
import study.loginstudy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    private static final long EXPIRATION_TIME = 24; // 24시간

    public void sendVerificationEmail(String loginId) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, loginId, LocalDateTime.now().plusHours(EXPIRATION_TIME));
        tokenRepository.save(verificationToken);

        String verificationLink = "http://yourdomain.com/api/verifyEmail?token=" + token;

        EmailMessage emailMessage = EmailMessage.builder()
                .to(loginId)
                .subject("이메일 인증")
                .message("이메일 인증을 위해 아래 링크를 클릭하세요: " + verificationLink)
                .build();

        emailService.sendMail(emailMessage);
    }

    public boolean verifyEmailToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        verificationToken.setVerified(true);
        tokenRepository.save(verificationToken);

        System.out.println("Verification completed for token: " + token);
        return true;
    }

    public boolean isEmailVerified(String loginId) {
        List<EmailVerificationToken> tokens = tokenRepository.findByLoginId(loginId);
        System.out.println("Tokens found for loginId: " + loginId + ": " + tokens);
        tokens.forEach(token -> System.out.println("Token: " + token.getToken() + ", Verified: " + token.isVerified()));
        return tokens.stream().anyMatch(EmailVerificationToken::isVerified);
    }
}
