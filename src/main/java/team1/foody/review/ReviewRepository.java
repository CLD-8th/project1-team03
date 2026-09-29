package team1.foody.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByShopIdOrderByIdDesc(Long shopId);

    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.shopId = :shopId
            """)
    Double findAverageRatingByShopId(
            @Param("shopId") Long shopId
    );
}