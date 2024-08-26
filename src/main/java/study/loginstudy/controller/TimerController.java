package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import study.loginstudy.domain.entity.Timer;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.TimerRepository;
import study.loginstudy.repository.UserRepository;
import study.loginstudy.service.TimerService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/timer")
public class TimerController {

    @Autowired
    private TimerRepository timerRepository;

    @Autowired
    private UserRepository userRepository;

    private Timer currentTimer;

    @Autowired
    private TimerService timerService;  // TimerService를 통해 데이터베이스와 상호작용

    // 기존 코드: HTML 페이지 반환 및 단순 문자열 응답

    @GetMapping
    public String showTimerPage() {
        return "timer";  // 타이머 페이지의 파일명 반환 (확장자 제외)
    }

    @PostMapping("/start")
    @ResponseBody
    public String startTimer() {
        LocalDateTime startTime = LocalDateTime.now();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Optional<User> optionalUser = userRepository.findByLoginId(loginId);

        if (!optionalUser.isPresent()) {
            return "User not found";
        }

        User user = optionalUser.get();

        // 최근 타이머를 가져와서 pause_time을 확인
        Optional<Timer> lastTimerOpt = timerRepository.findTopByUserOrderByStartTimeDesc(user);

        if (lastTimerOpt.isPresent()) {
            Timer lastTimer = lastTimerOpt.get();
            if (lastTimer.isPaused()) {
                long pauseDurationSeconds = calculateElapsedTime(lastTimer.getPauseTime(), startTime);
                String formattedPauseDuration = formatElapsedTime(pauseDurationSeconds);
                lastTimer.setPauseDuration(formattedPauseDuration);
                lastTimer.setPaused(false);

                timerRepository.save(lastTimer);
            }
        }

        currentTimer = new Timer();
        currentTimer.setStartTime(startTime);
        currentTimer.setPaused(false);
        currentTimer.setElapsedTime("00:00:00");
        currentTimer.setPauseDuration("00:00:00");
        currentTimer.setUser(user);

        timerRepository.save(currentTimer);
        return "Timer started successfully";
    }

    @PostMapping("/pause")
    @ResponseBody
    public String pauseTimer(@RequestBody Map<String, String> payload) {
        if (currentTimer != null && !currentTimer.isPaused()) {
            LocalDateTime pauseTime = LocalDateTime.now();
            currentTimer.setPaused(true);
            currentTimer.setPauseTime(pauseTime);

            long elapsedSeconds = calculateElapsedTime(currentTimer.getStartTime(), pauseTime);
            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);

            currentTimer.setElapsedTime(formattedElapsedTime);
            currentTimer.setActivityDescription(payload.get("activityDescription"));
            timerRepository.save(currentTimer);

            return "Timer paused successfully";
        } else {
            return "Timer is already paused or not started";
        }
    }

    @PostMapping("/stop")
    @ResponseBody
    public String stopTimer() {
        if (currentTimer != null) {
            currentTimer.setCompleted(true);
            currentTimer.setEndTime(LocalDateTime.now());
            currentTimer.setStopTime(currentTimer.getEndTime());

            timerRepository.save(currentTimer);
            currentTimer = null;
            return "Timer stopped successfully";
        } else {
            return "Timer is not started";
        }
    }

    @PostMapping("/reset")
    @ResponseBody
    public String resetTimer(@RequestBody Map<String, Integer> requestBody) {
        if (currentTimer != null && !currentTimer.isPaused()) {
            int elapsedSeconds = requestBody.getOrDefault("elapsedTime", 0);
            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);

            currentTimer.setElapsedTime(formattedElapsedTime);
            currentTimer.setCompleted(true);
            currentTimer.setEndTime(LocalDateTime.now());

            timerRepository.save(currentTimer);
            currentTimer = null;

            return "Timer reset and elapsed time saved successfully";
        } else {
            return "Timer is not running, no need to reset";
        }
    }

    @GetMapping("/timelaps")
    public String showTimelapsPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        Optional<User> optionalUser = userRepository.findByLoginId(loginId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<Timer> recentTimers = timerRepository.findByUserOrderByStartTimeDesc(user);
            model.addAttribute("recentTimers", recentTimers);
        }

        return "timelaps";  // timelaps.html을 반환
    }



    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @GetMapping("/load")
    public ResponseEntity<Map<String, String>> loadElapsedTime(@RequestParam("userId") String userId) {
        String elapsedTime = timerService.getElapsedTimeForUser(userId);

        Map<String, String> response = new HashMap<>();
        response.put("elapsedTime", elapsedTime);

        return ResponseEntity.ok(response);
    }

//    // 새로 추가된 API: JSON 응답을 위한 메서드들
//
//    @GetMapping("/api")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiShowTimerPage() {
//        Map<String, String> response = new HashMap<>();
//        response.put("status", "success");
//        response.put("message", "Timer page loaded");
//        return ResponseEntity.ok(response);  // JSON 형식으로 응답 반환
//    }
//
//    @PostMapping("/api/start")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiStartTimer() {
//        Map<String, String> response = new HashMap<>();
//        LocalDateTime startTime = LocalDateTime.now();
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String loginId = authentication.getName();
//        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
//
//        if (!optionalUser.isPresent()) {
//            response.put("status", "error");
//            response.put("message", "User not found");
//            return ResponseEntity.status(404).body(response);
//        }
//
//        User user = optionalUser.get();
//
//        // 최근 타이머를 가져와서 pause_time을 확인
//        Optional<Timer> lastTimerOpt = timerRepository.findTopByUserOrderByStartTimeDesc(user);
//
//        if (lastTimerOpt.isPresent()) {
//            Timer lastTimer = lastTimerOpt.get();
//            if (lastTimer.isPaused()) {
//                long pauseDurationSeconds = calculateElapsedTime(lastTimer.getPauseTime(), startTime);
//                String formattedPauseDuration = formatElapsedTime(pauseDurationSeconds);
//                lastTimer.setPauseDuration(formattedPauseDuration);
//                lastTimer.setPaused(false);
//
//                timerRepository.save(lastTimer);
//            }
//        }
//
//        currentTimer = new Timer();
//        currentTimer.setStartTime(startTime);
//        currentTimer.setPaused(false);
//        currentTimer.setElapsedTime("00:00:00");
//        currentTimer.setPauseDuration("00:00:00");
//        currentTimer.setUser(user);
//
//        timerRepository.save(currentTimer);
//        response.put("status", "success");
//        response.put("message", "Timer started successfully");
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/api/pause")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiPauseTimer(@RequestBody Map<String, String> payload) {
//        Map<String, String> response = new HashMap<>();
//        if (currentTimer != null && !currentTimer.isPaused()) {
//            LocalDateTime pauseTime = LocalDateTime.now();
//            currentTimer.setPaused(true);
//            currentTimer.setPauseTime(pauseTime);
//
//            long elapsedSeconds = calculateElapsedTime(currentTimer.getStartTime(), pauseTime);
//            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);
//
//            currentTimer.setElapsedTime(formattedElapsedTime);
//            currentTimer.setActivityDescription(payload.get("activityDescription"));
//            timerRepository.save(currentTimer);
//
//            response.put("status", "success");
//            response.put("message", "Timer paused successfully");
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("status", "error");
//            response.put("message", "Timer is already paused or not started");
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/api/stop")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiStopTimer() {
//        Map<String, String> response = new HashMap<>();
//        if (currentTimer != null) {
//            currentTimer.setCompleted(true);
//            currentTimer.setEndTime(LocalDateTime.now());
//            currentTimer.setStopTime(currentTimer.getEndTime());
//
//            timerRepository.save(currentTimer);
//            currentTimer = null;
//
//            response.put("status", "success");
//            response.put("message", "Timer stopped successfully");
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("status", "error");
//            response.put("message", "Timer is not started");
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/api/reset")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> apiResetTimer(@RequestBody Map<String, Integer> requestBody) {
//        Map<String, String> response = new HashMap<>();
//        if (currentTimer != null && !currentTimer.isPaused()) {
//            int elapsedSeconds = requestBody.getOrDefault("elapsedTime", 0);
//            String formattedElapsedTime = formatElapsedTime(elapsedSeconds);
//
//            currentTimer.setElapsedTime(formattedElapsedTime);
//            currentTimer.setCompleted(true);
//            currentTimer.setEndTime(LocalDateTime.now());
//
//            timerRepository.save(currentTimer);
//            currentTimer = null;
//
//            response.put("status", "success");
//            response.put("message", "Timer reset and elapsed time saved successfully");
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("status", "error");
//            response.put("message", "Timer is not running, no need to reset");
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    private String formatElapsedTime(long elapsedTimeSeconds) {
        long hours = elapsedTimeSeconds / 3600;
        long minutes = (elapsedTimeSeconds % 3600) / 60;
        long seconds = elapsedTimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private long calculateElapsedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null ? java.time.Duration.between(startTime, endTime).getSeconds() : 0;
    }
}
