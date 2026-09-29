package team1.foody.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import team1.foody.member.Member;
import team1.foody.shop.Shop;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;   // 별점

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Builder
    public Review(Integer rating, String content, String imageUrl,
                  Member member, Shop shop) {
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
        this.member = member;
        this.shop = shop;
    }

    // 마이페이지 - 리뷰 수정 (사진 수정은 이번 범위에서 제외)
    public void update(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}