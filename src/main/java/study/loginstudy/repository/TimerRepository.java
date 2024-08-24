package study.loginstudy.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import study.loginstudy.domain.entity.Timer;
import study.loginstudy.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface TimerRepository extends JpaRepository<Timer, Long> {
    List<Timer> findByUserAndCompletedFalse(User user);
    Optional<Timer> findTopByUserOrderByStartTimeDesc(User user);


    // 추가적인 쿼리 메소드가 필요하면 여기에 정의할 수 있습니다.


    List<Timer> findTop5ByUserOrderByStartTimeDesc(User user);

    // If you need a more dynamic approach:
    default List<Timer> findRecentTimers(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        return findByUser(user, pageable);
    }

    // This method needs to be defined in the repository
    List<Timer> findByUser(User user, Pageable pageable);

    List<Timer> findByUserOrderByStartTimeDesc(User user);

}