package study.loginstudy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import study.loginstudy.domain.dto.EmailMessage;
import study.loginstudy.domain.entity.EmailVerificationToken;
import study.loginstudy.repository.EmailVerificationTokenRepository;
import study.loginstudy.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    private static final long EXPIRATION_TIME = 24; // 24시간

    // 이메일 인증 OTP 전송
    public void sendVerificationEmail(String loginId) {
        System.out.println("Sending verification email to: " + loginId);

        String token = UUID.randomUUID().toString();
        String otp = generateOtp();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, loginId, LocalDateTime.now().plusHours(EXPIRATION_TIME), otp);
        tokenRepository.save(verificationToken);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(loginId)
                .subject("이메일 인증")
                .message("이메일 인증을 위해 아래 인증번호를 입력하세요: " + otp)
                .build();

        emailService.sendMail(emailMessage);
    }

    // 비밀번호 재설정 OTP 전송
    // 새로운 OTP를 생성하기 전에 기존의 OTP를 무효화
    public void sendPasswordResetOtp(String loginId) {
        System.out.println("Sending password reset OTP to: " + loginId);

        // 기존 토큰 무효화
        List<EmailVerificationToken> existingTokens = tokenRepository.findByLoginId(loginId);
        existingTokens.forEach(token -> token.setVerified(true));  // 기존 토큰 무효화
        tokenRepository.saveAll(existingTokens);

        String token = UUID.randomUUID().toString();
        String otp = generateOtp();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, loginId, LocalDateTime.now().plusHours(EXPIRATION_TIME), otp);
        tokenRepository.save(verificationToken);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(loginId)
                .subject("비밀번호 재설정 OTP")
                .message("비밀번호 재설정을 위해 아래 인증번호를 입력하세요: " + otp)
                .build();

        emailService.sendMail(emailMessage);
    }



    // 이메일 인증 OTP 검증
    public boolean verifyEmailToken(String loginId, String otp) {
        System.out.println("Verifying token for loginId: " + loginId + " with OTP: " + otp);

        List<EmailVerificationToken> tokens = tokenRepository.findByLoginId(loginId);

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired OTP: No token found for loginId " + loginId);
        }

        EmailVerificationToken verificationToken = tokens.stream()
                .filter(token -> token.getOtp().equals(otp))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP: OTP does not match"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired OTP: Token expired");
        }

        verificationToken.setVerified(true);
        tokenRepository.save(verificationToken);

        return true;
    }

    // 비밀번호 재설정 OTP 검증
    public boolean verifyPasswordResetOtp(String loginId, String otp) {
        System.out.println("Verifying password reset OTP for loginId: " + loginId + " with OTP: " + otp);

        // 로그인 ID로 해당하는 모든 토큰을 가져오고, 가장 최근에 생성된 토큰만 사용
        List<EmailVerificationToken> tokens = tokenRepository.findByLoginId(loginId);

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired OTP: No token found for loginId " + loginId);
        }

        // 최신 토큰을 선택
        EmailVerificationToken verificationToken = tokens.stream()
                .max(Comparator.comparing(EmailVerificationToken::getExpiryDate))  // 가장 최신의 토큰 선택
                .filter(token -> token.getOtp().equals(otp))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP: OTP does not match or has expired"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired OTP: Token expired");
        }

        verificationToken.setVerified(true);
        tokenRepository.save(verificationToken);

        return true;
    }



    // 이메일 인증 여부 확인
    public boolean isEmailVerified(String loginId) {
        List<EmailVerificationToken> tokens = tokenRepository.findByLoginId(loginId);
        System.out.println("Tokens found for loginId: " + loginId + ": " + tokens);
        tokens.forEach(token -> System.out.println("Token: " + token.getToken() + ", Verified: " + token.isVerified()));
        return tokens.stream().anyMatch(EmailVerificationToken::isVerified);
    }

    // OTP 생성
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // 6자리 랜덤 숫자 생성
        return String.valueOf(otp);
    }
}
