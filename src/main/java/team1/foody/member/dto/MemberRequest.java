package team1.foody.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원 가입 요청 형태.
 */
public record MemberRequest(

        @NotBlank(message = "아이디 필수")
        String userId,

        @NotBlank(message = "비밀번호는 필수")
        @Size(min = 4, message = "비밀번호는 4자 이상")
        String password,

        @NotBlank(message = "별명은 필수")
        @Size(max = 20, message = "별명은 20자 이하")
        String nickname
) {
}
