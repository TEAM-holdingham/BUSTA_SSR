package study.loginstudy.controllerAdvice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class LoginControllerAdvice {

    @ModelAttribute
    public void addModel(Model model, HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (requestUri.contains("api")) {
            return;
        }

        if(requestUri.contains("security-login")) {
            model.addAttribute("loginType", "security-login");
            model.addAttribute("pageName", "Security 로그인");
        }
    }
}
