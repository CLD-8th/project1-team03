package team1.foody.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원 정보 수정 요청 형태.
 *
 * 별명만 바꿀 수 있으며 아이디은 식별에 사용되므로 변경 대상이 아님.
 */
public record MemberUpdateRequest(

        @NotBlank(message = "별명은 필수")
        @Size(max = 20, message = "별명은 20자 이하")
        String nickname
) {
}
