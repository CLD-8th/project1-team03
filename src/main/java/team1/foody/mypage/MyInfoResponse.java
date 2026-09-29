package team1.foody.mypage;

import team1.foody.member.Member;

public record MyInfoResponse(Long id, String userId, String nickname) {
    public static MyInfoResponse from(Member member) {
        return new MyInfoResponse(member.getId(), member.getUserId(), member.getNickname());
    }
}