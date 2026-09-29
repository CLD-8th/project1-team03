package team1.foody.member;

import team1.foody.member.dto.MemberRequest;
import team1.foody.member.dto.MemberUpdateRequest;
import team1.foody.member.dto.MemberResponse;
import team1.foody.common.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * 회원 요청 수신.
 *
 * 게시글과 같은 계층 구조를 사용.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> join(@Valid @RequestBody MemberRequest request) {
        MemberResponse created = memberService.join(
                request.email(), request.password(), request.nickname());
        URI location = URI.create("/api/members/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * 정보 수정.
     *
     * 요청자를 확인하지 않으므로 누구나 남의 정보를 바꿀 수 있음.
     * 인가와 권한 확인 주제에서 본인 확인을 추가.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody MemberUpdateRequest request,
                                                 @AuthenticationPrincipal Long requesterId) {
        return ResponseEntity.ok(memberService.update(id, request.nickname(), requesterId));
    }

    /**
     * 탈퇴.
     *
     * 본인 확인이 가능한 시점에 도입.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal Long requesterId) {
        memberService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findById(id)
                .orElseThrow(() -> new NotFoundException("회원 부재")));
    }
}
