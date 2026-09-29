package team1.foody.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @Column(
            name = "image_url",
            length = 500
    )
    private String imageUrl;

    // DB에서 자동 처리
    @Column(
            name = "updated_at",
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;

    @Column(
            name = "member_id",
            nullable = false
    )
    private Long memberId;

    @Column(
            name = "shop_id",
            nullable = false
    )
    private Long shopId;


    protected Review() {
    }


    public Review(
            Long shopId,
            Long memberId,
            int rating,
            String content,
            String imageUrl
    ) {
        this.shopId = shopId;
        this.memberId = memberId;
        this.rating = rating;
        this.content = content;
        this.imageUrl = imageUrl;
    }


    public void update(
            int rating,
            String content,
            String imageUrl
    ) {
        this.rating = rating;
        this.content = content;

        // 새 사진을 올리지 않았으면 기존 사진 유지
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }


    public Long getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getShopId() {
        return shopId;
    }
}