package team1.foody.review;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< HEAD

=======
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
>>>>>>> main
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

<<<<<<< HEAD
    // FR-09: 내가 쓴 리뷰 목록 (가게 정보를 한 번에 가져와서 추가 쿼리 방지)
    @org.springframework.data.jpa.repository.Query(
            "select r from Review r join fetch r.shop " +
                    "where r.member.id = :memberId order by r.updatedAt desc")
    List<Review> findByMemberId(@org.springframework.data.repository.query.Param("memberId") Long memberId);
=======
    List<Review> findByShopIdOrderByIdDesc(Long shopId);

    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.shopId = :shopId
            """)
    Double findAverageRatingByShopId(
            @Param("shopId") Long shopId
    );
>>>>>>> main
}