package springbootblog.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import springbootblog.domain.User;

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

        return Jwts.builder()
                // --- (1) 헤더(Header): 토큰의 타입 설정
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 토큰 종류(typ) : JWT

                // --- (2) 내용(Payload): 토큰에 담을 정보 (클레임)
                .setIssuer(jwtProperties.getIssuer()) // 발행자(iss): 설정 파일에서 가져온 값
                .setIssuedAt(now)                     // 발행일시(iat): 현재 시간
                .setExpiration(expiry)                // 만료일시(exp): 전달받은 만료 시간
                .setSubject(user.getEmail())          // 토큰 제목(sub): 사용자의 이메일
                .claim("id", user.getId())         // 비공개 클레임: 유저의 DB 고유 ID 저장

                // --- (3) 서명(Signature): 토큰의 위변조 방지
                // 서버만 아는 비밀키(SecretKey)를 사용하여 HS256 알고리즘으로 해싱(암호화)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact(); // 설정한 내용을 바탕으로 최종 토큰 문자열 생성
    }

    // [2] JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) { // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
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
        return Jwts.parser() // 클레임 조회
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
