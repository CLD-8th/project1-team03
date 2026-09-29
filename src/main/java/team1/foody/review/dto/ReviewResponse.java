package team1.foody.review.dto;

import team1.foody.review.Review;
import java.time.LocalDateTime;

public record ReviewResponse(Long id, Long memberId, int rating, String content,
                             String imageUrl, LocalDateTime updatedAt) {

    public static ReviewResponse from(Review r) {
        return new ReviewResponse(r.getId(), r.getMemberId(), r.getRating(),
                r.getContent(), r.getImageUrl(), r.getUpdatedAt());
    }
}