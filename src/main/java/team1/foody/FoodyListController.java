package team1.foody;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team1.foody.entity.Shop;

@Controller
public class FoodyListController {

    private final FoodListRepository shopRepository;

    public FoodyListController(FoodListRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @GetMapping("/shops")
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

        model.addAttribute("shops", shopPage.getContent());
        model.addAttribute("shopPage", shopPage);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);

        return "shops";
    }
}