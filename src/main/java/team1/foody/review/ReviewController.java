package team1.foody.review;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team1.foody.review.dto.ReviewRequest;
import team1.foody.review.dto.ReviewResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // 리뷰 목록 조회
    @GetMapping("/shops/{shopId}/reviews")
    public List<ReviewResponse> list(
            @PathVariable Long shopId
    ) {
        return reviewService.findByShop(shopId);
    }

    // 리뷰 이미지 업로드
    @PostMapping("/reviews/images")
    public Map<String, String> uploadImage(
            @RequestParam MultipartFile image
    ) throws Exception {
        return Map.of(
                "imageUrl",
                reviewService.uploadImage(image)
        );
    }

    // 리뷰 작성
    @PostMapping("/shops/{shopId}/reviews")
    public ResponseEntity<Long> create(
            @PathVariable Long shopId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Long memberId
    ) {

        Long reviewId = reviewService.create(
                shopId,
                memberId,
                request
        );

        return ResponseEntity.ok(reviewId);
    }

    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> update(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Long memberId
    ) {

        reviewService.update(
                reviewId,
                memberId,
                request
        );

        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Long memberId
    ) {

        reviewService.delete(
                reviewId,
                memberId
        );

        return ResponseEntity.noContent().build();
    }
}