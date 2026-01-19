package springbootblog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import springbootblog.domain.Article;
import springbootblog.dto.AddArticleRequest;
import springbootblog.dto.UpdateArticleRequest;
import springbootblog.repository.BlogRepository;

import java.util.List;

/*
* 서비스: 리포지터리를 활용하여 핵심 비즈니스 로직을 구현한다.
* 예) 글 작성 / 수정 / 삭제, 트랜잭션 처리 등
 */

@RequiredArgsConstructor // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    // 게시글 추가 메서드
    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    // 모든 게시글 조회하는 메서드
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 특정 게시글 하나 조회하는 메서드
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 게시글 삭제 메서드
    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    // 게시글 수정 메서드
    @Transactional // 매칭한 메서드를 하나의 트랜잭션으로 묶음
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
