## registerBean, registerBeanWithDependency
> 학습 테스트 이외에는 사용하지 않는다. 스프링 IoC컨테이너는 파일 포맷이나 리소스 종류에 독립적이고, 오브젝트로 표현되는 순수한 메타정보를 사용한다
## genericApplicationContext
> genericApplicationContext는 가장 일반적인 애플리케이션 컨텍스트 구현체, 외부 리소스에 정의된 메타정보를 가지고 빈을 생성한다
## genericXmlApplicationContext
> genericApplicationContext와 genericApplicationContext가 결합된 구현체
## createContextWithoutParent
> childContext.xml 빈 메타정보에 없는 빈(printer)을 참조하고 있어서 에러가 난다
## contextHierarchy
> IoC컨테이너는 계층 구조를 가질 수 있다
> 자식 컨텍스트는 부모 컨텍스트를 참조 할 수 있지만, 부모 컨텍스트는 자식 컨텍스트를 참조 할 수 없다
## simpleBeanScanning
> 스테레오타입 애노테이션 붙은 클래스는 빈 스캐너가 감지해서 자동으로 빈으로 등록한다
## filterBeanScanning
> 표현식을 통해서 비슷한 빈들을 일괄적으로 등록할 수 있다
## configurationBean
> xml파일로 빈 설정과 같은 기능을 자바코드로 할 수 있다, IoC컨테이너가 관리하는 빈 이므로 싱글톤 객체이다
## constructorArgName
> 빈이 등록 될때 생성자를 통해 값을 정해줄 수 있다, 지정방법에는 1.index 지정 방법, 2.타입 지정 방법, 3.파라미터 이름을 이용하는 방법이 있다.
## autowire
> byName : 프로퍼티 이름과 참조 빈의 이름이 동일한 경우 생략 가능하다
> byType : 프로퍼티 이름과 참조 빈의 이름이 달라도 타입이 같다면 생략이 가능하다 (같은 타입의 빈이 2개 이상이라면 에러가 난다)