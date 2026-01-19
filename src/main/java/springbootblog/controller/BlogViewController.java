package springbootblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springbootblog.domain.Article;
import springbootblog.dto.ArticleListViewResponse;
import springbootblog.dto.ArticleViewResponse;
import springbootblog.service.BlogService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles") // 전체 글 목록 조회
    public String getArticles(Model model) { // 컨트롤러 → 뷰로 데이터를 전달하는 모델 객체
        List<ArticleListViewResponse> articles = blogService.findAll().stream() // 모든 게시글 엔티티 조회
                .map(ArticleListViewResponse::new) // 엔티티를 화면 전용 DTO로 변환
                .toList(); // DTO 리스트 생성
        model.addAttribute("articles", articles); // 뷰에서 사용할 게시글 목록 데이터 전달

        return "articleList"; // articleList.html 템플릿을 찾아 화면 렌더링
    }

    @GetMapping("/articles/{id}") // 개별 글 조회
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("article", new ArticleViewResponse());
        } else {
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle";
    }
}
