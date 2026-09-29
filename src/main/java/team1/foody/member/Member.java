package team1.foody.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "user_id",
            nullable = false,
            unique = true,
            length = 100
    )
    private String userId;

    @Column(
            nullable = false,
            length = 200
    )
    private String password;

    @Column(
            nullable = false,
            length = 20
    )
    private String nickname;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Member(
            String userId,
            String password,
            String nickname
    ) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 기존 코드에서 사용하는 닉네임 변경 메서드
     */
    public void changeNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 마이페이지 회원정보 수정에서 사용하는 닉네임 변경
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.updatedAt = LocalDateTime.now();
    }
}