package team1.foody.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 엔티티.
 *
 * 아이디는 중복될 수 없으므로 제약을 지정.
 */
@Entity
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

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
     * 별명 변경.
     */
    public void changeNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }
}