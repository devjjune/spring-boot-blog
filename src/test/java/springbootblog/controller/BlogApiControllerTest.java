package springbootblog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import springbootblog.repository.BlogRepository;

@SpringBootTest // 테스트용 애플리케이션 환경 구성
@AutoConfigureMockMvc // MockMvc 생성 및 자동 구성
class BlogApiControllerTest {
    // 실제 서버를 띄우지 않고도 HTTP 요청을 흉내 내어 컨트롤러 로직을 검증하는 테스트 클래스

    @Autowired
    protected MockMvc mockMvc; // 실제 HTTP 요청을 흉내 내는 객체

    @Autowired
    protected ObjectMapper objectMapper; // JSON 직렬화/역직렬화를 위한 클래스 (요청 본문 생성 시 필수)

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach // 테스트 실행 전 실행하는 메서드
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }
}
