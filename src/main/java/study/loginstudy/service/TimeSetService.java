package study.loginstudy.service;

import study.loginstudy.domain.entity.TimeSet;
import study.loginstudy.repository.TimeSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TimeSetService {

    @Autowired
    private TimeSetRepository timeSetRepository;


    public TimeSetService(TimeSetRepository timeSetRepository) {
        this.timeSetRepository = timeSetRepository;
    }

    public Optional<TimeSet> getTimeSet(Long userId) {
        Optional<TimeSet> timeSetOpt = timeSetRepository.findByUserId(userId);
        if (timeSetOpt.isPresent()) {
            System.out.println("TimeSet found: " + timeSetOpt.get());
        } else {
            System.out.println("TimeSet not found for user ID: " + userId);
        }
        return timeSetOpt;
    }

    public void saveTimeSet(TimeSet timeSet) {
        timeSetRepository.save(timeSet);
    }
}
