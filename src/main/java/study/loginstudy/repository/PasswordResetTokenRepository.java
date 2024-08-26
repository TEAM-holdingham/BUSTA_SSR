package study.loginstudy.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.PasswordResetToken;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    List<PasswordResetToken> findByLoginId(String loginId);
    Optional<PasswordResetToken> findByToken(String token);
}
