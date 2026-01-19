package springbootblog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springbootblog.domain.Article;

/*
* DTO 객체는 단순히 데이터를 전달만 하므로 별도의 비즈니스 로직을 포함하지 않는다.
 */

@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntity(String author) { // 생성자를 사용해 객체 생성. DTO를 엔티티로 만들어줌
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
