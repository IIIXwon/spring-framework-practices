[//]: # (# 🚀 프로젝트 제목 &#40;Project Title&#41;)

[//]: # (> 프로젝트에 대한 한 줄 요약 설명을 적어주세요.)

[//]: # ()
[//]: # (---)

[//]: # ()
[//]: # (## 1. 프로젝트 소개 &#40;Introduction&#41;)

[//]: # (- **개발 기간**: 202X.XX ~ 202X.XX)

[//]: # (- **주요 기능**:)

[//]: # (  - 핵심 기능 1)

[//]: # (  - 핵심 기능 2)

[//]: # (- **기획 의도**: 프로젝트를 시작하게 된 계기나 해결하고자 한 문제를 적어주세요.)

[//]: # ()
[//]: # (## 2. 기술 스택 &#40;Tech Stack&#41;)

[//]: # (- **Backend**: Java, Spring Boot, JPA)

[//]: # (- **Database**: MySQL, Redis)

[//]: # (- **Test**: JUnit 5, AssertJ)

[//]: # (- **Tools**: Git, Gradle)

[//]: # ()
[//]: # (## 3. 시작 가이드 &#40;Getting Started&#41;)

[//]: # (- **요구사항 &#40;Prerequisites&#41;**:)

[//]: # (  - Java 17 이상)

[//]: # (  - MySQL 8.0 이상)

[//]: # (- **설치 및 실행 방법 &#40;Installation&#41;**:)

[//]: # (  ```bash)

[//]: # (  $ git clone [https://github.com/username/repo-name.git]&#40;https://github.com/username/repo-name.git&#41;)

[//]: # (  $ cd repo-name)

[//]: # (  $ ./gradlew bootRun)

[//]: # (  ```)

## JUnit은 매 테스트 마다 새로운 객체를 만든다 (JUnitTest)
- test1() -> JUnitTest@3236
- test2() -> JUnitTest@3320
- test3() -> JUnitTest@3329
> 컬렉션에 총 3개가 저장된다.
## ApplicationContext은 싱글톤이다.
- test1() -> contextObject = null, context = GenericApplicationContext@3225 
- test2() -> contextObject = GenericApplicationContext@3225, context = GenericApplicationContext@3225
## ApplicationContext은 싱글톤이다.
- test1() -> contextObject = null, context = GenericApplicationContext@3225
- test2() -> contextObject = GenericApplicationContext@3225, context = GenericApplicationContext@3225
