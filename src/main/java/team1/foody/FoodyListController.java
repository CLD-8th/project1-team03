package team1.foody;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import team1.foody.entity.Shop;
import team1.foody.service.ShopRankingService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class FoodyListController {

    private final FoodListRepository shopRepository;
    private final ShopRankingService shopRankingService;

    public FoodyListController(
            FoodListRepository shopRepository,
            ShopRankingService shopRankingService
    ) {
        this.shopRepository = shopRepository;
        this.shopRankingService = shopRankingService;
    }

    @GetMapping({"/","/shops"})
    public String shops(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "latest") String sort,
            Model model
    ) {

        Sort sortOption;

        switch (sort) {
            case "views":
                sortOption = Sort.by("viewCount").descending();
                break;

            case "rating":
                sortOption = Sort.by("avgRating").descending();
                break;

            default:
                sortOption = Sort.by("id").descending();
                break;
        }

        Pageable pageable = PageRequest.of(
                page,
                20,
                sortOption
        );

        Page<Shop> shopPage;

        if (category == null || category.isBlank()) {
            shopPage = shopRepository.findAll(pageable);
        } else {
            shopPage = shopRepository.findByCategory(category, pageable);
        }

        // Redis 인기 랭킹 TOP 5
        List<Long> rankingIds = shopRankingService.topShopIds(5);

        List<Shop> rankingShops;

        if (rankingIds.isEmpty()) {
            rankingShops = List.of();
        } else {

            List<Shop> foundShops = shopRepository.findAllById(rankingIds);

            Map<Long, Shop> shopMap = foundShops.stream()
                    .collect(Collectors.toMap(
                            Shop::getId,
                            Function.identity()
                    ));

            // Redis 랭킹 순서 유지
            rankingShops = rankingIds.stream()
                    .map(shopMap::get)
                    .filter(shop -> shop != null)
                    .toList();
        }

        model.addAttribute("shops", shopPage.getContent());
        model.addAttribute("shopPage", shopPage);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("rankingShops", rankingShops);

        return "shops";
    }

    // 가게 상세 페이지
    @GetMapping("/shops/{id}")
    public String shopDetail(
            @PathVariable Long id,
            Model model
    ) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow();

        // DB 조회수 +1
        shop.increaseViewCount();
        shopRepository.save(shop);

        // Redis 랭킹 점수 +1
        shopRankingService.increaseScore(id);

        model.addAttribute("shop", shop);

        return "shop-detail";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}