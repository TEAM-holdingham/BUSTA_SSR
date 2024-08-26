package study.loginstudy.service;

import org.springframework.stereotype.Service;
import study.loginstudy.domain.entity.Timer;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.TimerRepository;
import study.loginstudy.repository.UserRepository;

import java.util.Optional;

@Service
public class TimerService {

    private final TimerRepository timerRepository;
    private final UserRepository userRepository;

    public TimerService(TimerRepository timerRepository, UserRepository userRepository) {
        this.timerRepository = timerRepository;
        this.userRepository = userRepository;
    }

    public String getElapsedTimeForUser(String userId) {
        User user = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Optional<Timer> latestTimerOpt = timerRepository.findTopByUserOrderByStartTimeDesc(user);

        return latestTimerOpt.map(Timer::getElapsedTime).orElse("00:00:00");
    }
}
