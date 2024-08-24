package study.loginstudy.repository;



import study.loginstudy.domain.entity.TimeSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeSetRepository extends JpaRepository<TimeSet, Long> {
    Optional<TimeSet> findByUserId(Long userId);
}
