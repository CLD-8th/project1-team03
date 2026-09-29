package team1.foody.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_name")
    private String shopName;

    private String address;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "avg_rating")
    private BigDecimal avgRating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String detail;

    private String thumbnail;

    private String category;

    public Long getId() {
        return id;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAddress() {
        return address;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public BigDecimal getAvgRating() {
        return avgRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getDetail() {
        return detail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void increaseViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 1L;
        } else {
            this.viewCount++;
        }
    }

    public void updateAvgRating(BigDecimal avgRating) {
        this.avgRating = avgRating;
    }
}