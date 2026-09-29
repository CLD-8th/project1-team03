package team1.foody.mypage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateReviewRequest(
        @NotNull(message = "별점을 입력해 주세요.")
        @Min(value = 1, message = "별점은 1~5 사이여야 합니다.")
        @Max(value = 5, message = "별점은 1~5 사이여야 합니다.")
        Integer rating,

        @NotBlank(message = "리뷰 내용을 입력해 주세요.")
        String content
) {
}