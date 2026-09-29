package team1.foody.member;

import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
public interface MemberRepository extends JpaRepository<Member, Long> {
}
=======
import java.util.Optional;

/**
 * 회원 저장소.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
>>>>>>> main
