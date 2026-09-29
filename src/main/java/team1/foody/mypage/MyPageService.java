package team1.foody.mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import team1.foody.member.Member;
import team1.foody.member.MemberRepository;
import team1.foody.review.Review;
import team1.foody.review.ReviewRepository;
import team1.foody.review.ReviewService;
import team1.foody.review.dto.ReviewRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewService reviewService;

    /**
     * 내가 작성한 리뷰 목록
     */
    @Transactional(readOnly = true)
    public List<MyReviewResponse> getMyReviews(
            Long memberId
    ) {

        return reviewRepository
                .findByMemberIdOrderByUpdatedAtDesc(
                        memberId
                )
                .stream()
                .map(MyReviewResponse::from)
                .toList();
    }

    /**
     * 내 회원정보 조회
     */
    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(
            Long memberId
    ) {

        return MyInfoResponse.from(
                findMember(memberId)
        );
    }

    /**
     * 회원정보 수정
     */
    @Transactional
    public MyInfoResponse updateMyInfo(
            Long memberId,
            UpdateMemberRequest request
    ) {

        Member member =
                findMember(memberId);

        /*
         * 닉네임 변경
         */
        if (request.nickname() != null) {

            String nickname =
                    request.nickname().trim();

            if (nickname.isEmpty()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "닉네임은 비워둘 수 없습니다."
                );
            }

            member.updateNickname(
                    nickname
            );
        }

        /*
         * 비밀번호 변경
         */
        if (request.newPassword() != null) {

            if (
                    request.currentPassword() == null
                            ||
                            !passwordEncoder.matches(
                                    request.currentPassword(),
                                    member.getPassword()
                            )
            ) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "현재 비밀번호가 일치하지 않습니다."
                );
            }

            member.updatePassword(
                    passwordEncoder.encode(
                            request.newPassword()
                    )
            );
        }

        return MyInfoResponse.from(
                member
        );
    }

    /**
     * 내 리뷰 수정
     */
    @Transactional
    public void updateMyReview(
            Long memberId,
            Long reviewId,
            UpdateReviewRequest request
    ) {

        /*
         * 먼저 본인 리뷰인지 확인
         */
        findMyReview(
                memberId,
                reviewId
        );

        /*
         * 마이페이지에서는 사진 수정 안 함.
         *
         * imageUrl에 null을 넘기면
         * Review.update()에서 기존 사진을 유지함.
         */
        ReviewRequest reviewRequest =
                new ReviewRequest(
                        request.rating(),
                        request.content(),
                        null
                );

        /*
         * ReviewService를 통해 수정해야
         * 평균 별점도 다시 계산됨.
         */
        reviewService.update(
                reviewId,
                memberId,
                reviewRequest
        );
    }

    /**
     * 내 리뷰 삭제
     */
    @Transactional
    public void deleteMyReview(
            Long memberId,
            Long reviewId
    ) {

        /*
         * 본인 리뷰인지 확인
         */
        findMyReview(
                memberId,
                reviewId
        );

        /*
         * ReviewService를 통해 삭제해야
         * 평균 별점도 다시 계산됨.
         */
        reviewService.delete(
                reviewId,
                memberId
        );
    }

    /**
     * 본인 리뷰 확인
     */
    private Review findMyReview(
            Long memberId,
            Long reviewId
    ) {

        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "리뷰를 찾을 수 없습니다."
                                        )
                        );

        /*
         * 현재 Review 엔티티에는
         * Member 객체가 아니라 memberId가 들어있음.
         */
        if (
                !review
                        .getMemberId()
                        .equals(memberId)
        ) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인이 작성한 리뷰만 수정·삭제할 수 있습니다."
            );
        }

        return review;
    }

    /**
     * 회원 조회
     */
    private Member findMember(
            Long memberId
    ) {

        return memberRepository
                .findById(memberId)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "회원을 찾을 수 없습니다."
                                )
                );
    }
}