package team1.foody.member.dto;

import team1.foody.member.Member;

import java.time.LocalDateTime;

/**
 * 회원 응답 형태.
 *
 * 비밀번호를 담지 않으므로 저장 형태를 그대로 반환할 때의 노출이 부재.
 */
public record MemberResponse(
        Long id,
        String userId,
        String nickname,
        LocalDateTime createdAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getUserId(),
                member.getNickname(),
                member.getCreatedAt());
    }
}
