package springbootblog.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import springbootblog.domain.auth.entity.RefreshToken;
import springbootblog.domain.auth.repository.RefreshTokenRepository;
import springbootblog.domain.user.entity.User;
import springbootblog.domain.user.repository.UserRepository;
import springbootblog.global.jwt.TokenProvider;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class WebOAuthSecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @DisplayName("Security: /api로 시작하는 경로는 인증 없이 접근 시 401 에러를 반환한다.")
    @Test
    void apiRequests_withoutToken_returns401() throws Exception {
        // given: 인증이 필요한 API 경로
        final String url = "/api/articles";

        // when: 토큰 없이 요청 전송
        ResultActions result = mockMvc.perform(get(url));

        // then: 401 Unauthorized 응답 확인 (HttpStatusEntryPoint 검증)
        result.andExpect(status().isUnauthorized());
    }

    @DisplayName("Security: /api/token 경로는 인증 없이도 접근 가능하다.")
    @Test
    void tokenRequest_withoutAuthentication_returnsOk() throws Exception {
        // 1. 테스트용 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("test@gmail.com")
                .password("test")
                .build());

        // 2. 리프레시 토큰 생성
        String refreshToken = tokenProvider.generateToken(testUser, Duration.ofDays(1));

        // 3. 생성한 토큰을 DB(Refresh Token 테이블)에도 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        final String url = "/api/token";
        final String content = "{\"refreshToken\":\"" + refreshToken + "\"}";

        // 4. 요청 및 검증
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());
    }

    @DisplayName("Security: 정적 리소스(js, css 등)는 시큐리티 검사에서 제외된다.")
    @Test
    void staticResources_areIgnored() throws Exception {
        // given: WebSecurityCustomizer에서 제외한 경로
        final String url = "/js/main.js";

        // when & then: 시큐리티 필터를 아예 타지 않으므로 200 혹은 404 기대 (401/403 X)
        mockMvc.perform(get(url))
                .andExpect(status().isNotFound()); // 파일이 실제론 없으니 404가 뜨는 게 정상
    }
}
