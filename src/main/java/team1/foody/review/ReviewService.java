package team1.foody.review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team1.foody.review.dto.ReviewRequest;
import team1.foody.review.dto.ReviewResponse;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> findByShop(Long shopId) {
        return reviewRepository.findByShopIdOrderByIdDesc(shopId)
                .stream().map(ReviewResponse::from).toList();
    }

    public Long create(Long shopId, Long memberId, ReviewRequest req) {
        validate(req);
        Review review = new Review(shopId, memberId, req.rating(), req.content(), req.imageUrl());
        return reviewRepository.save(review).getId();
    }

    public void update(Long reviewId, Long memberId, ReviewRequest req) {
        validate(req);
        findOwnReview(reviewId, memberId).update(req.rating(), req.content(), req.imageUrl());
    }

    public void delete(Long reviewId, Long memberId) {
        reviewRepository.delete(findOwnReview(reviewId, memberId));
    }

    // 파일 저장 후 화면에서 쓸 URL 경로를 반환
    public String uploadImage(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("사진 파일이 없습니다.");
        new File(uploadDir).mkdirs();

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));   // 예: .png
        }
        String savedName = UUID.randomUUID() + ext;

        file.transferTo(new File(uploadDir, savedName));
        return "/uploads/" + savedName;
    }

    private void validate(ReviewRequest req) {
        if (req.rating() < 1 || req.rating() > 5) throw new IllegalArgumentException("별점은 1~5점입니다.");
        if (req.content() == null || req.content().isBlank()) throw new IllegalArgumentException("리뷰 내용을 입력해주세요.");
    }

    private Review findOwnReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 없습니다."));
        if (!review.getMemberId().equals(memberId))
            throw new IllegalStateException("본인 리뷰만 수정/삭제할 수 있습니다.");
        return review;
    }
}