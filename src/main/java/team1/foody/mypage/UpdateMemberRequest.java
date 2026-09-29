package team1.foody.mypage;

import jakarta.validation.constraints.Size;

// 보내지 않은 항목(null)은 수정하지 않는다.
// 비밀번호를 바꾸려면 currentPassword와 newPassword를 둘 다 보내야 한다.
public record UpdateMemberRequest(
        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
        String nickname,

        String currentPassword,

        @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다.")
        String newPassword
) {
}