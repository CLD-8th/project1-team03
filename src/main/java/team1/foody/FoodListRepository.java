package team1.foody;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team1.foody.entity.Shop;

public interface FoodListRepository extends JpaRepository<Shop, Long> {

    Page<Shop> findByCategory(String category, Pageable pageable);
}