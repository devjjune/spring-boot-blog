package springbootblog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import springbootblog.dto.AddUserRequest;
import springbootblog.service.UserService;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;

    // 회원 가입 처리: 사용자가 입력한 정보를 받아 가입시키고 로그인 페이지로 이동
    @PostMapping("/user")
    public String signup(AddUserRequest request) {
        userService.save(request); // 회원 가입 로직 호출 (비번 암호화 + DB 저장)
        return "redirect:/login"; // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }

    // 로그아웃 처리: 사용자의 인증 세션을 강제로 종료하고 로그인 페이지로 이동
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 시큐리티 기능 호출
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
