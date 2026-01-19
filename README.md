# 📋 Spring Boot Blog
> Spring Boot 기반 블로그 REST API 프로젝트  
> 게시글 CRUD와 인증(JWT / OAuth2)을 구현하고, 배포까지 경험한 개인 학습 프로젝트 

<br>

## 📌 학습 목표
- Spring Boot 기반 웹 애플리케이션의 구조를 이해하고, 백엔드 시스템의 한 사이클(설계-구현-배포)을 경험한다.
- 책과 강의를 기반으로 학습하되, 설계 구조와 원리를 의식하며 개발하고 문제 해결 과정을 회고로 정리한다.

<br>

## 📌 학습의 3가지 핵심 축
학습한 내용은 크게 3가지의 축으로 나눌 수 있다.
1. **RESTful API 설계:** 계층형 아키텍처를 기반으로 한 게시판 CRUD 구현
2. **보안 및 인증:** Spring Security를 활용한 JWT / OAuth2 인증 구조 이해
3. **클라우드 인프라:** AWS Elastic Beanstalk를 이용한 실제 서비스 배포 및 환경 설정

<br>

## ✨ 주요 기능
- 게시글 관리: 제목, 본문, 카테고리를 포함한 게시글 작성, 수정, 삭제 및 전체/상세 조회
- 회원 시스템: 일반 회원가입 및 소셜 로그인(Google)을 통한 서비스 이용
- 권한 제어: 작성자 본인만 자신의 게시글을 수정하거나 삭제할 수 있는 보호 기능

<br>

## 🛠 기술 스택
| 구분 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Build | Gradle |
| DB | H2 (local), MySQL (prod) |
| ORM | Spring Data JPA |
| Security | Spring Security, JWT, OAuth2 |
| Infra | AWS EC2 / RDS / Elastic Beanstalk |
| Test | JUnit5 |

<br>

## 📁 프로젝트 구조
```
src/main/java/springbootblog
├── config        # 공통 설정 및 인증/인가
│   ├── jwt       # JWT 발급·검증 로직
│   └── oauth     # OAuth2 로그인 연동
├── controller    # 요청 진입 지점
├── service       # 비즈니스 로직
├── repository    # 데이터 접근 계층 (JPA)
├── domain        # 핵심 도메인 모델 (Entity)
├── dto           # 요청/응답 전용 데이터 객체 (Entity 노출 방지)
└── util          # 공통 유틸리티
```
**레이어드 아키텍처**를 기반으로 요청/비즈니스 로직/데이터 접근의 책임이 분리된다.   
CRUD API 및 화면, 인증/인가 로직 등은 모두 레이어드 아키텍처 구조(계층화)를 기반으로 동작한다.
```
   [ 실제 코드 의존성 ]               [ 설계 및 구현 순서 ]
  ------------------              ------------------
      Controller                        Entity
          ↓                                ↓
       Service                         Repository
          ↓                                ↓
      Repository                        Service
          ↓                                ↓
        Entity                         Controller
```
스프링 애플리케이션은 **[컨트롤러 → 서비스 → 리포지터리] 방향으로 의존성이 설계**된다. 
요청 처리, 비즈니스 로직, 데이터 접근의 순서로 책임을 각각 분리하며, **의존성은 항상 상위 계층에서 하위 계층으로만 흐른다.**

이러한 구조에서는 **의존받는 계층이 먼저 존재해야 하므로**, **실제 설계 및 구현 순서는 의존 방향과 반대**가 된다.
이 순서대로 프로젝트를 설계하면 핵심 도메인(Entity)과 비즈니스 중심으로 사고할 수 있고, 중간 중간 단위테스트를 작성하고 검증하기에도 용이하다.

<br>

## 🔄 요청 처리 흐름
```
Client
 → Controller
 → Service
 → Repository
 → Database
 → Response (DTO)
```
사용자의 요청이 처리되는 흐름은 위와 같다.  
요청은 Controller에서 시작해 Service와 Repository를 거쳐 처리되며, 응답은 Entity를 직접 반환하지 않고 DTO 형태로 반환해 API 계층과 도메인 모델을 분리했다.


<br>

## 🔗 주소
- 서비스 URL: http://spring-boot-blog-env.eba-8hiiastj.ap-northeast-2.elasticbeanstalk.com/articles

<br>

## 🧩 기술 회고 & 트러블슈팅
- [[내장 톰캣이 뜨지 않았던 원인: ServletWebServerFactory 빈 누락]](https://velog.io/@hjy648012/Spring-%EC%84%9C%EB%B2%84%EB%8A%94-%EC%99%9C-%EB%9C%A8%EC%A7%80-%EC%95%8A%EC%95%98%EC%9D%84%EA%B9%8C-ServletWebServerFactory-bean-%EC%98%A4%EB%A5%98%EC%9D%98-%EC%A7%84%EC%A7%9C-%EC%9B%90%EC%9D%B8)
- [[테스트 환경에서 Table Not Found가 발생한 진짜 이유: 설정 파일 우선순위]](https://velog.io/@hjy648012/%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85-%EC%97%90%EB%9F%AC-%EB%A1%9C%EA%B7%B8%EB%8A%94-Table-Not-Found%EC%98%80%EC%A7%80%EB%A7%8C-%EB%B2%94%EC%9D%B8%EC%9D%80-Resource-Overriding%EC%9D%B4%EC%97%88%EB%8B%A4)
- [[Spring 애플리케이션 에러 로그를 읽는 기준 정리]](https://velog.io/@hjy648012/Spring-%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4%EC%85%98-%EC%97%90%EB%9F%AC-%EB%A1%9C%EA%B7%B8-%EC%9D%BD%EB%8A%94-%EB%B2%95)
- [[JWT secret key 오류의 원인: YAML 설정 들여쓰기 실수]](https://velog.io/@hjy648012/Spring-Boot-JWT-base64-encoded-secret-key-%EC%98%A4%EB%A5%98-%EC%95%8C%EA%B3%A0-%EB%B3%B4%EB%8B%88-YAML-%EB%93%A4%EC%97%AC%EC%93%B0%EA%B8%B0)
- [[Elastic Beanstalk 배포 후 502 오류 원인과 해결]](https://velog.io/@hjy648012/AWS-Elastic-Beanstalk-%EB%B0%B0%ED%8F%AC-%ED%9B%84-502-%EC%98%A4%EB%A5%98-%EC%9B%90%EC%9D%B8%EA%B3%BC-%ED%95%B4%EA%B2%B0)

<br>
