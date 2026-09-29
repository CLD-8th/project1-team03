package team1.foody.mypage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // TODO: 로그인(토큰) 완성되면 X-Member-Id 헤더 대신 토큰에서 회원 id를 꺼내도록 교체

    @GetMapping
    public MyInfoResponse getMyInfo(@RequestHeader("X-Member-Id") Long memberId) {
        return myPageService.getMyInfo(memberId);
    }

    // FR-08: 회원정보 수정
    @PatchMapping
    public MyInfoResponse updateMyInfo(@RequestHeader("X-Member-Id") Long memberId,
                                       @Valid @RequestBody UpdateMemberRequest request) {
        return myPageService.updateMyInfo(memberId, request);
    }

    // FR-09: 내 리뷰 목록
    @GetMapping("/reviews")
    public List<MyReviewResponse> getMyReviews(@RequestHeader("X-Member-Id") Long memberId) {
        return myPageService.getMyReviews(memberId);
    }

    // FR-09: 내 리뷰 수정
    @PatchMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyReview(@RequestHeader("X-Member-Id") Long memberId,
                               @PathVariable Long reviewId,
                               @Valid @RequestBody UpdateReviewRequest request) {
        myPageService.updateMyReview(memberId, reviewId, request);
    }

    // FR-09: 내 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyReview(@RequestHeader("X-Member-Id") Long memberId,
                               @PathVariable Long reviewId) {
        myPageService.deleteMyReview(memberId, reviewId);
    }
}