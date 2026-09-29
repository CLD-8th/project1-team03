package team1.foody.mypage;

import team1.foody.review.Review;

import java.time.LocalDateTime;

public record MyReviewResponse(
        Long id,
        Long shopId,
        int rating,
        String content,
        String imageUrl,
        LocalDateTime updatedAt
) {

    public static MyReviewResponse from(Review review) {
        return new MyReviewResponse(
                review.getId(),
                review.getShopId(),
                review.getRating(),
                review.getContent(),
                review.getImageUrl(),
                review.getUpdatedAt()
        );
    }
}