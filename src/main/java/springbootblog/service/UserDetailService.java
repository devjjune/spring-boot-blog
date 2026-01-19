package springbootblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import springbootblog.domain.User;
import springbootblog.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService { // 스프링 시큐리티 내장 인터페이스를 상속

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
    }

    // === UserDetails vs UserDetailsService ===
    // UserDetails (데이터의 형식, 엔티티가 상속): 게터로 사용자 데이터 '전달' 목적
    // UserDetailsService (로직의 형식, 서비스가 상속): 사용자를 찾아오는 '기능'을 실행하는 '로직'
    // UserDetailsService(기능)가 로직을 실행해서 찾아낸 결과물이 바로 UserDetails(데이터)이다.
}
