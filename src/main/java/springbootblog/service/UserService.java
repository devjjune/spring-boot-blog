package springbootblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import springbootblog.domain.User;
import springbootblog.dto.AddUserRequest;
import springbootblog.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입: DTO를 엔티티로 변환하여 DB에 저장
    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 비밀번호 암호화 필수
                .build()).getId();
    }

    // ID로 유저 찾기: 마이페이지 조회나 본인 인증 등 '고유 식별'이 필요할 때 사용
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    // 이메일로 유저 찾기: 로그인, 중복 가입 확인, 비밀번호 찾기 등 '계정 확인' 시 사용
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
