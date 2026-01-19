package springbootblog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users") // 테이블명을 엔티티 클래스명과 다르게 정의 (User: SQL 예약어)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity // 핵심 비즈니스 로직 객체이므로 엔티티로서 domain 패키지에 포함
public class User implements UserDetails { // 인증 객체인 UserDetails 상속받음

    // === [1] users 엔티티로 DB 설계 ===

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User update(String nickname) {
        this.nickname = nickname;

        return this;
    }

    // === [2] UserDetails 메서드로 시큐리티 설계 (메서드를 통해 인증 정보 전달) ===

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
        // user 권한이 있음을 반환 (인가)
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
