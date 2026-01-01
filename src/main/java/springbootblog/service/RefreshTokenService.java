package springbootblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springbootblog.domain.RefreshToken;
import springbootblog.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
