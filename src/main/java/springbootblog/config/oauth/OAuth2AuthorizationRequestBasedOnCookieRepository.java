package springbootblog.config.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;
import springbootblog.util.CookieUtil;

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000; // 쿠키 유효 시간 (5시간)

    // [1] 인증 요청 정보 삭제: 권한 부여 요청 정보를 쿠키에서 읽어온 뒤 삭제 프로세스를 준비
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    // [2] 인증 요청 정보 로드: 브라우저 쿠키에 저장된 인증 요청 정보를 읽어와서 다시 객체로 변환
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        // CookieUtil을 사용하여 쿠키(문자열)를 객체로 역직렬화함
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    // [3] 인증 요청 정보 저장: 외부 로그인을 시작할 때 인증 요청 정보를 쿠키에 직렬화하여 저장
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        // 객체를 문자열로 변환(serialize)하여 쿠키에 담음
        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
    }

    // [4] 쿠키 삭제: 인증이 완료되거나 실패하여 더 이상 필요 없는 쿠키를 제거
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}