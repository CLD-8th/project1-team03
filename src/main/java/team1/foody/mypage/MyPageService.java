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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<MyReviewResponse> getMyReviews(Long memberId) {
        return reviewRepository.findByMemberId(memberId).stream()
                .map(MyReviewResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(Long memberId) {
        return MyInfoResponse.from(findMember(memberId));
    }

    @Transactional
    public MyInfoResponse updateMyInfo(Long memberId, UpdateMemberRequest request) {
        Member member = findMember(memberId);

        // 닉네임 수정
        if (request.nickname() != null) {
            String nickname = request.nickname().trim();
            if (nickname.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임은 비워둘 수 없습니다.");
            }
            member.updateNickname(nickname);
        }

        // 비밀번호 변경
        if (request.newPassword() != null) {
            if (request.currentPassword() == null
                    || !passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
            }
            member.updatePassword(passwordEncoder.encode(request.newPassword()));
        }

        return MyInfoResponse.from(member);
    }
    @Transactional
    public void updateMyReview(Long memberId, Long reviewId, UpdateReviewRequest request) {
        Review review = findMyReview(memberId, reviewId);
        review.update(request.rating(), request.content());
    }

    @Transactional
    public void deleteMyReview(Long memberId, Long reviewId) {
        Review review = findMyReview(memberId, reviewId);
        reviewRepository.delete(review);
    }

    // 리뷰가 없으면 404, 내 리뷰가 아니면 403
    private Review findMyReview(Long memberId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));
        if (!review.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 수정·삭제할 수 있습니다.");
        }
        return review;
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }
}