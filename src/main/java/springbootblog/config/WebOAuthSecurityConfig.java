package springbootblog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import springbootblog.config.jwt.TokenProvider;
import springbootblog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import springbootblog.config.oauth.OAuth2SuccessHandler;
import springbootblog.config.oauth.OAuth2UserCustomService;
import springbootblog.repository.RefreshTokenRepository;
import springbootblog.service.UserService;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    // === [1] 특정 경로에서는 스프링 시큐리티 기능 비활성화 (보안 검사 필요 없는 정적 파일, DB 콘솔 등) ===
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    // === [2] 시큐리티 필터 체인 메서드 : 특정 HTTP 요청에 대해 보안 설정 ===
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1) Stateless(무상태) 환경 설정
        // JWT(토큰 방식)를 사용하므로 서버에서 세션을 저장X -> 기존 세션/폼 로그인 비활성화
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 2) 토큰 헤더를 확인할 '커스텀 인증 필터' 추가
        // 헤더에서 JWT를 꺼내 인증을 처리하는 필터를 로그인 필터 이전에 끼워 넣음
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 3) URL 접근 권한 설정
        // 토큰 발급(/api/token)은 인증 없이 가능하게, 나머지 API(/api/**)는 토큰 인증 필수
        http.authorizeRequests()
                .requestMatchers("/api/token").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll();

        // 4) OAuth2 로그인 상세 설정
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(auth -> auth
                        // OAuth2 인증 과정 중 임시 정보(상태)를 쿠키에 저장하는 저장소 설정
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                // 구글/카카오 로그인 성공 시 실행할 로직(JWT 발급 등) 연결
                .successHandler(oAuth2SuccessHandler())
                // 외부 서버에서 가져온 유저 정보를 우리 DB에 처리하는 서비스 연결
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserCustomService)
                )
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/login")
        );

        // 5) 예외 처리 (Exception Handling)
        // 토큰 없이 API에 접근하면 로그인 페이지로 리다이렉트 시키는 대신, 깔끔하게 '401 Unauthorized' 에러 코드를 반환하도록 설정
        http.exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                )
        );

        return http.build();
    }

    // === [3] 보안 보조 도구(Bean) 등록 ===

    // OAuth2 인증 성공 시 JWT 토큰 발행을 전담하는 핸들러
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    // 매 요청마다 토큰을 검사할 필터
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    // OAuth2 인증 과정에서 필요한 정보를 쿠키에 담기 위한 저장소
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
}
