package springbootblog.domain.article.dto;

import lombok.Getter;
import springbootblog.domain.article.entity.Article;

@Getter
public class ArticleResponse {

    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
