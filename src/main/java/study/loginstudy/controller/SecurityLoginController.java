package study.loginstudy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.UserService;
import org.springframework.web.bind.annotation.RestController; //추가

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
@RestController // controller -> restcontroller
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
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult) {

        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        // 닉네임 중복 체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }
        // password와 passwordCheck가 같은지 체크
        //if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
        //  bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
        //}
        //변경 부분(8.12)
        if (!Objects.equals(joinRequest.getPassword(), joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }


        if(bindingResult.hasErrors()) {
            return "join";
        }

        userService.join2(joinRequest);
        return "redirect:/security-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/info")
    //@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public String userInfo(Model model, Authentication auth) {

        User loginUser = userService.getLoginUserByLoginId(auth.getName());
        model.addAttribute("user", loginUser);

        return "info";
    }

    @GetMapping("/admin")
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/authentication-fail")
    public String authenticationFail() {
        return "errorPage/authenticationFail";
    }

    @GetMapping("/authorization-fail")
    public String authorizationFail() {
        return "errorPage/authorizationFail";
    }

    @GetMapping("/nickname")
    public String getNicknameByLoginId(@RequestParam("loginId") String loginId) {
        try {
            return userService.findNicknameByLoginId(loginId);
        } catch (UserNotFoundException e) {
            return e.getMessage();
        }
    }


    @GetMapping("/my_page")
    public String myPage(Model model, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        User user = userService.findByLoginId(loginId); // 유저 서비스에서 사용자 정보 조회
        model.addAttribute("user", user);
        return "my_page"; // my_page.html 템플릿 반환
    }

    @GetMapping("/confirm-delete")
    public String confirmDelete(Model model, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        model.addAttribute("loginId", loginId);
        return "confirm_delete";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(HttpSession session, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        userService.deleteAccount(loginId);
        session.invalidate();  // 세션 무효화
        return "redirect:/"; // 첫 화면으로 리다이렉트
    }
    // API 엔드포인트 추가

//    @PostMapping("/api/login")
//    public ResponseEntity<?> apiLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
//        try {
//            // Spring Security의 AuthenticationManager를 사용하여 인증
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
//            );
//
//            // 인증 성공 시 SecurityContext에 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // 세션 생성
//            HttpSession session = request.getSession(true);
//
//            // 사용자 정보 조회
//            User user = userService.getLoginUserByLoginId(authentication.getName());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("sessionId", session.getId());
//            response.put("user", user);
//
//            return ResponseEntity.ok(response);
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/password");
//        }
//    }


    @PostMapping("/api/login")
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            // Spring Security의 AuthenticationManager를 사용하여 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword())
            );

            // 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션 생성
            HttpSession session = request.getSession(true);

            // 사용자 정보 조회
            User user = userService.getLoginUserByLoginId(authentication.getName());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 쿠키 설정
            ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                    .httpOnly(true)
                    .secure(true)  // HTTPS를 사용하므로 true로 설정
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)  // 7일 동안 유효
                    .sameSite("None")  // Cross-Site 요청에서도 쿠키 전송
                    .build();

            // 쿠키를 응답 헤더에 추가
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("sessionId", session.getId());
            responseBody.put("user", user);

            return ResponseEntity.ok(responseBody);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/password");
        }
    }

    // 로그인 GET 요청 추가 (8.20)
    @GetMapping("/api/login")
    public ResponseEntity<?> apiLoginWithGet(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password,
            HttpServletRequest request, HttpServletResponse response) {
        try {
            // Spring Security의 AuthenticationManager를 사용하여 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginId, password)
            );

            // 인증 성공 시 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션 생성
            HttpSession session = request.getSession(true);

            // 사용자 정보 조회
            User user = userService.getLoginUserByLoginId(authentication.getName());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 쿠키 설정
            ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
                    .httpOnly(true)
                    .secure(true)  // HTTPS를 사용하므로 true로 설정
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)  // 7일 동안 유효
                    .sameSite("None")  // Cross-Site 요청에서도 쿠키 전송
                    .build();

            // 쿠키를 응답 헤더에 추가
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("sessionId", session.getId());
            responseBody.put("user", user);

            return ResponseEntity.ok(responseBody);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/password");
        }
    }





    @PostMapping("/api/logout")
    public ResponseEntity<Map<String, Object>> apiLogout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        session.invalidate();  // 세션 무효화
        response.put("status", "success");
        response.put("message", "로그아웃 성공");
        return ResponseEntity.ok(response);
    }

    //마이페이지 json 형식으로 반환(08.14)
    @GetMapping("/api/my-page")
    public ResponseEntity<Map<String, Object>> apiMyPage(Authentication authentication) {
        // 로깅 추가
        System.out.println("API my-page 요청 받음");

        Map<String, Object> response = new HashMap<>();

        if (authentication == null) {
            response.put("status", "failure");
            response.put("message", "인증되지 않은 사용자입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String loginId = authentication.getName();
        User user = userService.findByLoginId(loginId);

        if (user != null) {
            response.put("status", "success");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failure");
            response.put("message", "사용자 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //UserInfo json 형식으로 반환(08.14)
    @GetMapping("/api/info")
    public ResponseEntity<Map<String, Object>> apiUserInfo(Authentication auth) {
        Map<String, Object> response = new HashMap<>();

        // 현재 로그인한 사용자의 loginId 가져오기
        String loginId = auth.getName();

        // 유저 서비스에서 사용자 정보 조회
        User loginUser = userService.getLoginUserByLoginId(loginId);

        if (loginUser != null) {
            response.put("status", "success");
            response.put("user", loginUser);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failure");
            response.put("message", "사용자 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //return 값이 loginhome -> json 변환
    @GetMapping(value = {"/api", "/api/"})
    public ResponseEntity<Map<String, Object>> apiLoginHome(Authentication auth) {
        Map<String, Object> response = new HashMap<>();

        if (auth != null) {
            // 현재 로그인한 사용자의 loginId 가져오기
            User loginUser = userService.getLoginUserByLoginId(auth.getName());

            if (loginUser != null) {
                response.put("status", "success");
                response.put("nickname", loginUser.getNickname());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "failure");
                response.put("message", "사용자 정보를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } else {
            response.put("status", "unauthenticated");
            response.put("message", "인증된 사용자가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/api/join")
    public ResponseEntity<Map<String, Object>> apiJoinPage() {
        Map<String, Object> response = new HashMap<>();

        // 새로운 JoinRequest 객체를 생성하여 반환
        JoinRequest joinRequest = new JoinRequest();

        response.put("status", "success");
        response.put("joinRequest", joinRequest);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/join")
    public ResponseEntity<Map<String, Object>> apiJoin(@Valid @RequestBody JoinRequest joinRequest, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        // loginId 중복 체크
        if (userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        // 닉네임 중복 체크
        if (userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }
        // password와 passwordCheck가 같은지 체크
        if (!Objects.equals(joinRequest.getPassword(), joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        // 입력 값에 오류가 있는 경우
        if (bindingResult.hasErrors()) {
            response.put("status", "failure");
            response.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        // 유저 서비스에서 회원가입 처리
        userService.join2(joinRequest);

        response.put("status", "success");
        response.put("message", "회원가입이 성공적으로 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/admin")
// @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> apiAdminPage() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "관리자 페이지에 접근할 수 있습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/authentication-fail")
    public ResponseEntity<Map<String, Object>> apiAuthenticationFail() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failure");
        response.put("message", "인증에 실패했습니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/api/authorization-fail")
    public ResponseEntity<Map<String, Object>> apiAuthorizationFail() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failure");
        response.put("message", "권한이 부족하여 접근할 수 없습니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @GetMapping("/api/nickname")
    public ResponseEntity<Map<String, Object>> apiGetNicknameByLoginId(@RequestParam("loginId") String loginId) {
        Map<String, Object> response = new HashMap<>();
        try {
            String nickname = userService.findNicknameByLoginId(loginId);
            response.put("status", "success");
            response.put("nickname", nickname);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            response.put("status", "failure");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/api/confirm-delete")
    public ResponseEntity<Map<String, Object>> apiConfirmDelete(Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("loginId", loginId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/delete-account")
    public ResponseEntity<Map<String, Object>> apiDeleteAccount(HttpSession session, Authentication authentication) {
        String loginId = authentication.getName(); // 현재 로그인한 사용자 정보 가져오기
        userService.deleteAccount(loginId);
        session.invalidate();  // 세션 무효화

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "계정이 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }



}
