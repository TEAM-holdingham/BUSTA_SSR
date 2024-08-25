package study.loginstudy.service;

import study.loginstudy.domain.entity.TimeSet;
import study.loginstudy.repository.TimeSetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TimeSetService {

    private final TimeSetRepository timeSetRepository;

    @Autowired
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

    public void saveOrUpdateTimeSet(TimeSet timeSet) {
        // Find existing TimeSet for the user
        Optional<TimeSet> existingTimeSet = timeSetRepository.findByUserId(timeSet.getUser().getId());

        if (existingTimeSet.isPresent()) {
            // Update existing TimeSet
            TimeSet existing = existingTimeSet.get();
            existing.setTargetHours(timeSet.getTargetHours());
            existing.setTargetMinutes(timeSet.getTargetMinutes());
            existing.setCreatedTime(timeSet.getCreatedTime()); // Optionally update created time if needed
            timeSetRepository.save(existing);
        } else {
            // Save new TimeSet
            timeSetRepository.save(timeSet);
        }
    }
}
