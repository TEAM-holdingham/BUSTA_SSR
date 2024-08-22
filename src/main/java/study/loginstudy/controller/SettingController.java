package study.loginstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/settings")
public class SettingController {

    @GetMapping("/main")
    public String settingsMainPage() {
        // settings_main.html 반환
        return "settings_main";
    }
}