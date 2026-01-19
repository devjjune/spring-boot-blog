package springbootblog.repository;

import springbootblog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/*
* 리포지터리: 데이터베이스에 접근(저장/조회 등), 도메인 객체를 DB에 저장하고 관리한다.
* 보통 JpaRepository를 상속해 기본 CRUD 메서드를 제공받는다.
* 상속할 때 엔티티 타입(Article)과, 해당 엔티티의 PK 타입(Long)을 지정한다.
 */

public interface BlogRepository extends JpaRepository<Article, Long> {
}
// 리포지터리를 ‘인터페이스’로 사용하는 이유: 데이터 접근 기술의 교체 가능성
