package team1.foody.review;

import jakarta.persistence.*;
<<<<<<< HEAD
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import team1.foody.member.Member;
import team1.foody.shop.Shop;

=======
>>>>>>> main
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
<<<<<<< HEAD
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
=======
>>>>>>> main
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
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
=======
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    // DB가 자동으로 넣고 갱신하므로 JPA는 건드리지 않음
    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    private Long memberId;
    private Long shopId;

    protected Review() {}

    public Review(Long shopId, Long memberId, int rating, String content, String imageUrl) {
        this.shopId = shopId;
        this.memberId = memberId;
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void update(int rating, String content, String imageUrl) {
        this.rating = rating;
        this.content = content;
        if (imageUrl != null) this.imageUrl = imageUrl;   // 새 사진 없으면 기존 사진 유지
    }

    public Long getId() { return id; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getMemberId() { return memberId; }
    public Long getShopId() { return shopId; }
>>>>>>> main
}