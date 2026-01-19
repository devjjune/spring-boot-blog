package springbootblog.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") // 자바 코드와 설정 파일을 연결
public class JwtProperties {
    private String issuer;
    private String secretKey;
}
