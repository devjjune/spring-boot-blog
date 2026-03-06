package springbootblog.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter // 설정값 주입 시 필요
@Getter // 다른 클래스에서 설정값을 꺼내 쓸 때 필요
@Component // 스프링이 관리하는 빈으로 등록
@ConfigurationProperties("jwt") // 설정 파일에서 설정값을 찾아와 필드에 주입
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
