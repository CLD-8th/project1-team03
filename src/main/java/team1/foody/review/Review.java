package team1.foody.review;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}