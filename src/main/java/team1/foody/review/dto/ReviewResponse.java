package team1.foody.review.dto;

import team1.foody.review.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long memberId,
        String nickname,
        int rating,
        String content,
        String imageUrl,
        LocalDateTime updatedAt
) {

    public static ReviewResponse from(
            Review review,
            String nickname
    ) {
        return new ReviewResponse(
                review.getId(),
                review.getMemberId(),
                nickname,
                review.getRating(),
                review.getContent(),
                review.getImageUrl(),
                review.getUpdatedAt()
        );
    }
}