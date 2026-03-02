//package springbootblog.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//@RequiredArgsConstructor
//@Configuration
//public class WebSecurityConfig {
//    private final UserDetailsService userService;
//
//    // === [1] 특정 경로에서는 스프링 시큐리티 기능 비활성화 (보안 검사 필요 없는 정적 파일, DB 콘솔 등) ===
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers("/static/**");
//    }
//
//    // === [2] 필터 체인 메서드 : 특정 HTTP 요청에 대해 보안 설정 ===
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        return http
//                // 특정 경로에 대한 액세스 설정
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/login", "/signup", "/user").permitAll()
//                        .anyRequest().authenticated()
//                )
//                // 폼 기반 로그인 설정 (시큐리티 기본 로그인 폼 사용)
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .defaultSuccessUrl("/articles", true)
//                )
//                // 로그아웃 설정 (세션 무효화 -> 로그인 페이지로 이동)
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login")
//                        .invalidateHttpSession(true)
//                )
//                // csrf 비활성화 (학습 편의상)
//                .csrf(csrf -> csrf.disable())
//
//                .build();
//    }
//
//    // === [3] 로그인 검증 방식 설정 ===
//    // 인증 관리자 : 어디서 유저를 찾고(Service), 어떻게 비번을 검사할지(Encoder) 설정
//    @Bean
//    public AuthenticationManager authenticationManager(
//            BCryptPasswordEncoder encoder,
//            UserDetailsService userDetailsService) {
//
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(encoder); // 비밀번호 검사 도구 연결
//        provider.setUserDetailsService(userDetailsService); // 사용자 조회 서비스 연결
//
//        return new ProviderManager(provider);
//    }
//
//    // 비밀번호 암호화 도구 등록 (패스워드 인코더)
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
