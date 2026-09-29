package team1.foody.member;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "member")
=======
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
>>>>>>> main
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(nullable = false, length = 200)
    private String password;   // 암호화된 비밀번호

    @Column(nullable = false, length = 20)
    private String nickname;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Member(String userId, String password, String nickname) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
    }

    // 마이페이지 - 회원정보 수정(FR-08)에서 사용
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
=======
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
>>>>>>> main
    }
}