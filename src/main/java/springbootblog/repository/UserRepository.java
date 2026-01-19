package springbootblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootblog.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // PK인 email로 사용자 정보를 가져옴
}
