package team1.foody.review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team1.foody.FoodListRepository;
import team1.foody.entity.Shop;
import team1.foody.member.MemberRepository;
import team1.foody.review.dto.ReviewRequest;
import team1.foody.review.dto.ReviewResponse;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final FoodListRepository shopRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ReviewService(
            ReviewRepository reviewRepository,
            MemberRepository memberRepository,
            FoodListRepository shopRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.shopRepository = shopRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> findByShop(Long shopId) {

        return reviewRepository
                .findByShopIdOrderByIdDesc(shopId)
                .stream()
                .map(review -> {

                    String nickname = memberRepository
                            .findById(review.getMemberId())
                            .map(member -> member.getNickname())
                            .orElse("알 수 없는 회원");

                    return ReviewResponse.from(
                            review,
                            nickname
                    );
                })
                .toList();
    }

    public Long create(
            Long shopId,
            Long memberId,
            ReviewRequest req
    ) {

        validate(req);

        Review review = new Review(
                shopId,
                memberId,
                req.rating(),
                req.content(),
                req.imageUrl()
        );

        Review savedReview =
                reviewRepository.save(review);

        /*
         * 방금 등록한 리뷰가 AVG 계산에 포함되도록
         * DB에 즉시 반영
         */
        reviewRepository.flush();

        updateShopAverageRating(shopId);

        return savedReview.getId();
    }

    public void update(
            Long reviewId,
            Long memberId,
            ReviewRequest req
    ) {

        validate(req);

        Review review =
                findOwnReview(
                        reviewId,
                        memberId
                );

        review.update(
                req.rating(),
                req.content(),
                req.imageUrl()
        );

        reviewRepository.flush();

        updateShopAverageRating(
                review.getShopId()
        );
    }

    public void delete(
            Long reviewId,
            Long memberId
    ) {

        Review review =
                findOwnReview(
                        reviewId,
                        memberId
                );

        Long shopId =
                review.getShopId();

        reviewRepository.delete(review);

        reviewRepository.flush();

        updateShopAverageRating(shopId);
    }

    public String uploadImage(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "사진 파일이 없습니다."
            );
        }

        File uploadDirectory =
                new File(uploadDir)
                        .getAbsoluteFile();

        if (!uploadDirectory.exists()) {

            boolean created =
                    uploadDirectory.mkdirs();

            if (!created) {
                throw new IllegalStateException(
                        "업로드 폴더를 생성할 수 없습니다: "
                                + uploadDirectory.getAbsolutePath()
                );
            }
        }

        String originalName =
                file.getOriginalFilename();

        String ext = "";

        if (
                originalName != null
                        && originalName.contains(".")
        ) {
            ext = originalName.substring(
                    originalName.lastIndexOf(".")
            );
        }

        String savedName =
                UUID.randomUUID() + ext;

        File destination =
                new File(
                        uploadDirectory,
                        savedName
                );

        file.transferTo(
                destination.toPath()
        );

        return "/uploads/" + savedName;
    }

    private void validate(
            ReviewRequest req
    ) {

        if (
                req.rating() < 1
                        || req.rating() > 5
        ) {
            throw new IllegalArgumentException(
                    "별점은 1~5점입니다."
            );
        }

        if (
                req.content() == null
                        || req.content().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "리뷰 내용을 입력해주세요."
            );
        }
    }

    private Review findOwnReview(
            Long reviewId,
            Long memberId
    ) {

        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "리뷰가 없습니다."
                                        )
                        );

        if (
                !review
                        .getMemberId()
                        .equals(memberId)
        ) {
            throw new IllegalStateException(
                    "본인 리뷰만 수정/삭제할 수 있습니다."
            );
        }

        return review;
    }

    private void updateShopAverageRating(
            Long shopId
    ) {

        Double average =
                reviewRepository
                        .findAverageRatingByShopId(shopId);

        BigDecimal averageRating;

        if (average == null) {

            averageRating =
                    BigDecimal.ZERO
                            .setScale(
                                    1,
                                    RoundingMode.HALF_UP
                            );

        } else {

            averageRating =
                    BigDecimal
                            .valueOf(average)
                            .setScale(
                                    1,
                                    RoundingMode.HALF_UP
                            );
        }

        Shop shop =
                shopRepository
                        .findById(shopId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "가게가 없습니다."
                                        )
                        );

        shop.updateAvgRating(
                averageRating
        );
    }
}