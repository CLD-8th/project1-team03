package team1.foody.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 가게별 리뷰 목록
     */
    List<Review> findByShopIdOrderByIdDesc(Long shopId);


    /**
     * 회원이 작성한 리뷰 목록
     * 마이페이지에서 사용
     */
    List<Review> findByMemberIdOrderByUpdatedAtDesc(Long memberId);


    /**
     * 가게 리뷰 평균 별점
     */
    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.shopId = :shopId
            """)
    Double findAverageRatingByShopId(
            @Param("shopId") Long shopId
    );
}