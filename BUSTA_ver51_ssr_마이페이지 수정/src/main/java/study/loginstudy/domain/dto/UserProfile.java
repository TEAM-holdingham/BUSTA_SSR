package study.loginstudy.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import study.loginstudy.domain.UserRole;
import study.loginstudy.domain.entity.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    private String loginId;
    private String nickname;
    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String gender;
    private String introduction; // 추가
    private MultipartFile profilePicture; // 추가

    public String getPassword() {
        return password;
    }

    private String password;

//    // 비밀번호 암호화 X
//    public User toEntity() {
//        return User.builder()
//                .loginId(this.loginId)
//                .nickname(this.nickname)
//                .phoneNumber(this.phoneNumber)
//                .birthDate(this.birthDate)
//                .gender(this.gender)
//                .introduction(this.introduction) // 추가
//                .profilePicture(this.profilePicture) // 추가
//                .build();
//    }


}
