package springbootblog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import springbootblog.config.jwt.TokenProvider;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter { // 요청 하나당 필터 한 번씩 실행
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization"; // 요청 헤더의 키값
    private final static String TOKEN_PREFIX = "Bearer "; // 토큰 앞에 붙는 접두사

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 키의 값을 꺼냄
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 2. 꺼낸 값에서 "Bearer "를 제거하고 순수한 토큰값만 추출
        String token = getAccessToken(authorizationHeader);

        // 3. 토큰이 유효한지 확인 (TokenProvider 이용)
        if (tokenProvider.validToken(token)) {
            // 4. 유효하다면 토큰으로부터 인증 정보(Authentication)를 가져옴
            Authentication authentication = tokenProvider.getAuthentication(token);
            // 5. 시큐리티 저장소(SecurityContextHolder)에 인증 정보를 등록 -> '인증된 유저'로 인정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. if문 조건 X (토큰 유효 X) -> 다음 필터로 요청을 넘김
        filterChain.doFilter(request, response);
    }

    // HTTP 헤더에서 순수한 토큰값만 추출
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length()); // "Bearer " 이후의 문자열 반환
        }
        return null;
    }
}