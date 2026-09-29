package team1.foody.mypage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members/me")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 내 회원정보 조회
     */
    @GetMapping
    public MyInfoResponse getMyInfo(
            @AuthenticationPrincipal Long memberId
    ) {
        return myPageService.getMyInfo(memberId);
    }

    /**
     * FR-08: 회원정보 수정
     */
    @PatchMapping
    public MyInfoResponse updateMyInfo(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody UpdateMemberRequest request
    ) {
        return myPageService.updateMyInfo(
                memberId,
                request
        );
    }

    /**
     * FR-09: 내 리뷰 목록
     */
    @GetMapping("/reviews")
    public List<MyReviewResponse> getMyReviews(
            @AuthenticationPrincipal Long memberId
    ) {
        return myPageService.getMyReviews(memberId);
    }

    /**
     * FR-09: 내 리뷰 수정
     */
    @PatchMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyReview(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        myPageService.updateMyReview(
                memberId,
                reviewId,
                request
        );
    }

    /**
     * FR-09: 내 리뷰 삭제
     */
    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyReview(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long reviewId
    ) {
        myPageService.deleteMyReview(
                memberId,
                reviewId
        );
    }
}