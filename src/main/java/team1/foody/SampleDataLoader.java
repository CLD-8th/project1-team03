package team1.foody;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import team1.foody.member.Member;
import team1.foody.member.MemberRepository;
import team1.foody.review.Review;
import team1.foody.review.ReviewRepository;
import team1.foody.shop.Shop;
import team1.foody.shop.ShopRepository;

@Profile("sample")
@Component
@RequiredArgsConstructor

public class SampleDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Member me = memberRepository.save(Member.builder()
                .userId("test01")
                .password(passwordEncoder.encode("test1234!"))   // 테스트용 비밀번호: test1234!
                .nickname("테스터").build());

        Shop shop1 = shopRepository.save(Shop.builder()
                .shopName("부산밀면").address("부산 진구 어딘가 1").category("한식").build());
        Shop shop2 = shopRepository.save(Shop.builder()
                .shopName("해운대국밥").address("부산 해운대구 어딘가 2").category("한식").build());

        reviewRepository.save(Review.builder()
                .rating(5).content("정말 맛있어요").member(me).shop(shop1).build());
        reviewRepository.save(Review.builder()
                .rating(3).content("보통이에요").member(me).shop(shop2).build());
    }
}