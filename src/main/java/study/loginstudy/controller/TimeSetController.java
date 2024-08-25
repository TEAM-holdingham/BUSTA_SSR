package study.loginstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import study.loginstudy.UserNotFoundException;
import study.loginstudy.domain.entity.TimeSet;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.TimeSetService;
import study.loginstudy.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/time")
public class TimeSetController {

    private final TimeSetService timeSetService;
    private final UserService userService;

    public TimeSetController(TimeSetService timeSetService, UserService userService) {
        this.timeSetService = timeSetService;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        try {
            // 현재 로그인한 사용자 가져오기
            User currentUser = userService.getCurrentUser();

            // 사용자의 TimeSet 가져오기
            Optional<TimeSet> timeSetOptional = timeSetService.getTimeSet(currentUser.getId());

            if (timeSetOptional.isEmpty()) {
                // 사용자가 아직 시간을 설정하지 않은 경우
                return "home_set_time"; // 사용자가 시간을 설정하는 페이지로 이동
            } else {
                // 사용자가 시간을 설정한 경우, home.html로 데이터 전달
                TimeSet timeSet = timeSetOptional.get();
                int targetHours = timeSet.getTargetHours();
                int targetMinutes = timeSet.getTargetMinutes();

                // 남은 시간을 초로 계산
                int remainingTimeInSeconds = (targetHours * 3600) + (targetMinutes * 60);

                // 모델에 데이터 추가하여 View로 전달
                model.addAttribute("hours", targetHours);
                model.addAttribute("minutes", targetMinutes);
                model.addAttribute("remainingTimeInSeconds", remainingTimeInSeconds);

                return "home"; // home.html 템플릿 반환
            }
        } catch (UserNotFoundException e) {
            // 사용자 인증 실패 또는 사용자 정보 불러오기 실패
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
    }

    @PostMapping("/set")
    public String setTime(@RequestParam int hours, @RequestParam int minutes) {
        // 현재 사용자 가져오기
        User currentUser = userService.getCurrentUser();

        // TimeSet 객체 생성
        TimeSet timeSet = new TimeSet();
        timeSet.setUser(currentUser);
        timeSet.setTargetHours(hours);
        timeSet.setTargetMinutes(minutes);
        timeSet.setCreatedTime(LocalDateTime.now());

        // 기존 TimeSet을 업데이트하거나 새로 저장
        timeSetService.saveOrUpdateTimeSet(timeSet);

        // 설정이 완료된 후 home 페이지로 리다이렉트
        return "redirect:/time/home";
    }
}
