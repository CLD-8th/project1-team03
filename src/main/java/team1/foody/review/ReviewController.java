package team1.foody.review;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/shops/{shopId}/reviews")
    public List<ReviewResponse> list(@PathVariable Long shopId) {
        return reviewService.findByShop(shopId);
    }

    // FR-02 사진 업로드 (파일이라 form-data 사용)
    @PostMapping("/reviews/images")
    public Map<String, String> uploadImage(@RequestParam MultipartFile image) throws Exception {
        return Map.of("imageUrl", reviewService.uploadImage(image));
    }

    // FR-01 리뷰 작성 (raw JSON)
    @PostMapping("/shops/{shopId}/reviews")
    public ResponseEntity<Long> create(@PathVariable Long shopId,
                                       @RequestBody ReviewRequest request,
                                       HttpSession session) {
        return ResponseEntity.ok(reviewService.create(shopId, getMemberId(session), request));
    }

    // FR-03 리뷰 수정 (raw JSON)
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> update(@PathVariable Long reviewId,
                                       @RequestBody ReviewRequest request,
                                       HttpSession session) {
        reviewService.update(reviewId, getMemberId(session), request);
        return ResponseEntity.ok().build();
    }

    // FR-04 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, HttpSession session) {
        reviewService.delete(reviewId, getMemberId(session));
        return ResponseEntity.noContent().build();
    }

    // 로그인 연동 전까지 임시 (로그인 기능 붙으면 1L 제거)
    private Long getMemberId(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        return memberId != null ? memberId : 1L;
    }
}