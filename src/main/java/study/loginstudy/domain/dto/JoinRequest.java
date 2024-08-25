package study.loginstudy.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import study.loginstudy.domain.UserRole;
import study.loginstudy.domain.entity.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "닉네임이 비어있습니다.")
    private String nickname;

    private String username;

    private String phoneNumber;  // 전화번호를 문자열로 변경

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    private String gender;
    private String job;
    private String home;
    private String school;



    public User toEntity() {
        return User.builder()
                .loginId(this.loginId)
                .password(this.password)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .job(this.job)
                .home(this.home)
                .school(this.school)
                .role(UserRole.USER)
                .username(this.username)
                .build();
    }

    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .nickname(this.nickname)
                .phoneNumber(this.phoneNumber)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .job(this.job)
                .home(this.home)
                .school(this.school)
                .username(this.username)
                .role(UserRole.USER)
                .build();
    }
}
