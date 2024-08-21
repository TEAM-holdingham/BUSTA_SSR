package study.loginstudy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.Timer;
import study.loginstudy.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface TimerRepository extends JpaRepository<Timer, Long> {
    List<Timer> findByUserAndCompletedFalse(User user);
    Optional<Timer> findTopByUserOrderByStartTimeDesc(User user);
    // 추가적인 쿼리 메소드가 필요하면 여기에 정의할 수 있습니다.
}