package team1.foody.shop;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shop")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_name", nullable = false, unique = true, length = 100)
    private String shopName;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "avg_rating")
    private Integer avgRating;   // 리뷰가 없으면 null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(length = 500)
    private String thumbnail;

    @Column(nullable = false, length = 100)
    private String category;

    @Builder
    public Shop(String shopName, String address, String detail,
                String thumbnail, String category) {
        this.shopName = shopName;
        this.address = address;
        this.detail = detail;
        this.thumbnail = thumbnail;
        this.category = category;
    }
}