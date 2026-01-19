package springbootblog.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/*
* 도메인의 엔티티 클래스.
* 엔티티는 데이터베이스의 테이블과 1:1로 매핑되는 객체로,
* 비즈니스 핵심 로직과 관련된 개념이 엔티티가 된다.
*
* 컨트롤러/서비스/리포지터리는 모두 도메인을 중심으로 설계되므로
* 도메인 엔티티는 애플리케이션 설계 단계에서 가장 먼저 정의된다.
 */

@Entity // 엔티티로 지정
@Getter // 필드 값 가져오는 게터 메서드들 대체
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 엔티티 클래스의 기본 생성자 추가
public class Article {

    @Id // id 필드를 기본키로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동 생성
    @Column(name = "id", updatable = false) // 엔티티 필드와 테이블 컬럼을 매핑 (수정 불가)
    private Long id;

    @Column(name = "title", nullable = false) // 'title'이라는 not null 컬럼과 매핑
    private String title;

    @Column(name = "content", nullable = false) // 'content'라는 not null 컬럼과 매핑
    private String content;

    @Column(name = "author", nullable = false)
    private String author;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder // 빌더 패턴으로 Article 객체 생성
    public Article(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
