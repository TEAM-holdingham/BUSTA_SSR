package study.loginstudy.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String otp;  // 인증번호 필드

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean verified = false;

    // 모든 필드를 포함한 생성자
    public EmailVerificationToken(String token, String loginId, LocalDateTime expiryDate, String otp) {
        this.token = token;
        this.loginId = loginId;
        this.expiryDate = expiryDate;
        this.otp = otp;
    }
}
