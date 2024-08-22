package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.UserNotFoundException;
import study.loginstudy.domain.UserRole;
import study.loginstudy.domain.dto.JoinRequest;
import study.loginstudy.domain.dto.LoginRequest;
import study.loginstudy.domain.dto.UserProfile;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

@Controller // RestController -> Controller
@RequiredArgsConstructor
@RequestMapping("/security-login")
public class SecurityLoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @GetMapping(value = {"", "/"})
    public String loginhome(Model model, Authentication auth) {

        if(auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        return "loginhome";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("joinRequest", new JoinRequest());
        return "sign_up_1";
    }

    @GetMapping("/join2")
    public String join2Page(Model model) {
        model.addAttribute("joinRequest", new JoinRequest());
        return "sign_up_2";
    }

    @PostMapping("/join2")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {

        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }

        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }

        if (!Objects.equals(joinRequest.getPassword(), joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("joinRequest", joinRequest);
            return "sign_up_2";
        }

//        userService.join2(joinRequest);
        return "redirect:/security-login/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String apiLogin(@ModelAttribute LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            User user = userService.getLoginUserByLoginId(authentication.getName());

            if (user == null) {
                model.addAttribute("errorMessage", "User not found");
                return "login";
            }

            ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("None")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            model.addAttribute("user", user);
            return "redirect:/security-login/home";
        } catch (AuthenticationException e) {
            model.addAttribute("errorMessage", "Invalid username/password");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            model.addAttribute("nickname", loginUser.getNickname());
        }
        return "home";  // home.html을 반환하도록 설정
    }


    @PostMapping("/logout")
    public String apiLogout(HttpSession session) {
        session.invalidate();  // 세션 무효화
        return "redirect:/";
    }


    @GetMapping("/my-page")
    public String myPage(Model model, Authentication authentication) {
        // 인증되지 않은 사용자인 경우 처리
        if (authentication == null) {
            model.addAttribute("errorMessage", "인증되지 않은 사용자입니다.");
            return "errorPage/unauthorized";  // 인증되지 않은 경우 에러 페이지로 리다이렉트
        }

        String loginId = authentication.getName();
        User user = userService.findByLoginId(loginId);

        if (user != null) {
            // UserProfile 객체를 생성하여 필요한 사용자 정보를 설정
            UserProfile userProfile = new UserProfile();
            userProfile.setNickname(user.getNickname());
            userProfile.setLoginId(user.getLoginId());
            userProfile.setIntroduction(user.getIntroduction());
            userProfile.setPhoneNumber(user.getPhoneNumber());
            userProfile.setBirthDate(user.getBirthDate());
            userProfile.setGender(user.getGender());

            // 모델에 사용자 프로필 정보를 추가
            model.addAttribute("userProfile", userProfile);

            return "my_page";  // my_page.html 템플릿으로 데이터 전달
        } else {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "errorPage/userNotFound";  // 사용자를 찾을 수 없는 경우 에러 페이지로 리다이렉트
        }
    }


    @GetMapping("/goals")
    public String goalsPage(Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }
        return "goals";  // goals.html 파일로 이동
    }
    @GetMapping("/timelaps")
    public String timelapsPage(Model model, Authentication auth) {
        if (auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }
        return "timelaps";  // timelaps.html 파일로 이동
    }





//
//    @GetMapping("/api/info")
//    public String apiUserInfo(Model model, Authentication auth) {
//        String loginId = auth.getName();
//        User loginUser = userService.getLoginUserByLoginId(loginId);
//
//        if (loginUser != null) {
//            model.addAttribute("user", loginUser);
//            return "info";
//        } else {
//            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
//            return "errorPage/userNotFound";
//        }
//    }
//
//    @GetMapping(value = {"/api", "/api/"})
//    public String apiLoginHome(Model model, Authentication auth) {
//        if (auth != null) {
//            User loginUser = userService.getLoginUserByLoginId(auth.getName());
//
//            if (loginUser != null) {
//                model.addAttribute("nickname", loginUser.getNickname());
//                return "loginhome";
//            } else {
//                model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
//                return "errorPage/userNotFound";
//            }
//        } else {
//            model.addAttribute("errorMessage", "인증된 사용자가 없습니다.");
//            return "errorPage/unauthorized";
//        }
//    }
//
//    @GetMapping("/api/join")
//    public String apiJoinPage(Model model) {
//        model.addAttribute("joinRequest", new JoinRequest());
//        return "join";
//    }
//
//    @PostMapping("/api/join")
//    public String apiJoin(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
//
//        if (userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
//            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
//        }
//        if (userService.checkNicknameDuplicate(joinRequest.getNickname())) {
//            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
//        }
//        if (!Objects.equals(joinRequest.getPassword(), joinRequest.getPasswordCheck())) {
//            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
//        }
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("joinRequest", joinRequest);
//            return "join";
//        }
//
//        userService.join2(joinRequest);
//        return "redirect:/security-login";
//    }
//
//    @GetMapping("/api/admin")
//    public String apiAdminPage() {
//        return "admin";
//    }
//
//    @GetMapping("/api/authentication-fail")
//    public String apiAuthenticationFail(Model model) {
//        model.addAttribute("errorMessage", "인증에 실패했습니다.");
//        return "errorPage/authenticationFail";
//    }
//
//    @GetMapping("/api/authorization-fail")
//    public String apiAuthorizationFail(Model model) {
//        model.addAttribute("errorMessage", "권한이 부족하여 접근할 수 없습니다.");
//        return "errorPage/authorizationFail";
//    }
//
//    @GetMapping("/api/nickname")
//    public String apiGetNicknameByLoginId(@RequestParam("loginId") String loginId, Model model) {
//        try {
//            String nickname = userService.findNicknameByLoginId(loginId);
//            model.addAttribute("nickname", nickname);
//            return "nickname";
//        } catch (UserNotFoundException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "errorPage/userNotFound";
//        }
//    }
//
//    @GetMapping("/api/confirm-delete")
//    public String apiConfirmDelete(Model model, Authentication authentication) {
//        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
//        model.addAttribute("loginId", loginId);
//        return "confirm_delete";
//    }
//
//    @PostMapping("/api/delete-account")
//    public String apiDeleteAccount(HttpSession session, Authentication authentication, Model model) {
//        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
//        userService.deleteAccount(loginId);
//        session.invalidate();  // 세션 무효화
//
//        return "redirect:/";
//    }

}
