package springbootblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springbootblog.domain.Article;
import springbootblog.domain.dto.AddArticleRequest;
import springbootblog.repository.BlogRepository;

import java.util.List;

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // 블로그 모든 글 조회하는 메서드
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 블로그 글 하나 조회하는 메서드
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 블로그 글 삭제 메서드
    public void delete(long id) {
        blogRepository.deleteById(id);
    }
}
