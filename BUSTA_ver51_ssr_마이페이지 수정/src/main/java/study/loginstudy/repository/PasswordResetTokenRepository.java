package study.loginstudy.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
