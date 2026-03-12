package springbootblog.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import springbootblog.domain.user.entity.User;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);

    }

    // [1] JWT 토큰 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .header()
                    .add("typ", "JWT")
                .and()
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .subject(user.getEmail()) // 토큰 제목(sub): 사용자의 이메일
                .claim("id", user.getId()) // 비공개 클레임: 유저의 DB 고유 ID 저장
                .signWith(key) // 서명(Signature): 토큰의 위변조 방지
                .compact(); // 설정한 내용을 바탕으로 최종 토큰 문자열 생성
    }

    // [2] JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            // 1. 검증에 사용할 SecretKey 생성
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));

            // 2. 파서 빌드 및 토큰 검증
            Jwts.parser()
                    .verifyWith(key)   // [변경] setSigningKey 대신 사용
                    .build()            // [필수] 파서 객체 생성
                    .parseSignedClaims(token); // [변경] parseClaimsJws 대신 권장

            return true;
        } catch (Exception e) {
            // 서명 위조, 만료 등 모든 예외 상황에서 false 반환
            return false;
        }
    }

    // [3] 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
    }

    // [4] 토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));

        return Jwts.parser()
                .verifyWith(key) // setSigningKey 대신 verifyWith 사용
                .build()         // 빌드 호출 필수
                .parseSignedClaims(token) // parseClaimsJws 대신 parseSignedClaims 추천
                .getPayload();   // getBody() 대신 getPayload() 사용
    }
}
