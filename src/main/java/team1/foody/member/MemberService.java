package team1.foody.member;

import team1.foody.member.dto.MemberResponse;
import team1.foody.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 회원 업무 규칙.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse join(String email, String password, String nickname) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일");
        }
        Member saved = memberRepository.save(new Member(email, passwordEncoder.encode(password), nickname));
        return MemberResponse.from(saved);
    }

    /**
     * 정보 수정.
     *
     * 조회한 뒤 값을 바꾸며 저장 호출이 불필요.
     */
    @Transactional
    public MemberResponse update(Long id, String nickname, Long requesterId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("회원 부재"));

        // 본인 계정만 수정 가능.
        if (!id.equals(requesterId)) {
            throw new SecurityException("권한 부재");
        }

        member.changeNickname(nickname);
        return MemberResponse.from(member);
    }

    /**
     * 탈퇴.
     *
     * 본인 계정만 가능.
     * 작성한 글이 있으면 외래 키 제약으로 삭제가 실패.
     */
    @Transactional
    public void delete(Long id, Long requesterId) {
        if (!id.equals(requesterId)) {
            throw new SecurityException("권한 부재");
        }

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("회원 부재"));
        memberRepository.delete(member);
    }

    public Optional<MemberResponse> findById(Long id) {
        return memberRepository.findById(id).map(MemberResponse::from);
    }
}
