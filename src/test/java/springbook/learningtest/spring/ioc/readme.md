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
## 웹 애플리케이션의 IoC컨테이너 구성
> 서버에서 동작하는 애플리케이션에서 스프링 IoC컨테이너를 사용하는 방법은 세가지로 구분할 수 있다
> 두 가지 방법은 웹 모듈안에 컨테이너 생성, 엔터프라이즈 애플리케이션에 생성
> 하나의 웹 애플리케이션은 여러 개의 서블릿을 가질 수 있다
> 프론트 컨트롤러 패턴 : 많은 웹 요청을 한 번에 받을 수 있는 대표 서블릿을 등록해두고, 공통적인 선행 작업을 수행하게 한 후에, 요청의 기능을 담당하는 핸들러라 불리는 클래스를 호출하는 방식
> 웹 애플리케이션 안에서 동작하는 IoC컨테이너는 두 가지 방법으로 만든다
> 1. 스프링 애플리케이션 요청을 처리하는 서블릿(DispatcherServlet) 안에서 생성
> 2. 웹 애플리케이션 레벨에서 생성
     > 스프링 웹 애플리케이션에는 두 개의 컨테이너, 즉 WebApplicationContext 오브젝트가 만들어진다
     > 프론트 컨트롤러 서블릿이 한개이상 등록 된다면 그만큼 전체 컨테이너 개수는 늘어난다(Ex) 프론트 컨틀롤러 서블린이 3개면 전체 컨테이너는 6개 )
## 웹 애플리케이션의 컨텍스트 계층구조
> 웹 애플리케이션에 등록되는 컨테이너는 일반적으로 루트 웹 애플리케이션 컨텍스트라고 한다
> 서블릿 레벨에 등록되는 컨테이너들의 부모 컨테이너가 되고, 전체 계층구조 내에서 가장 최상단에 위치한 루트 컨텍스트가 된다
> 하나 이상의 프론트 컨트롤러 역할을 하는 서블릿이 등록될 수 있는데, 각 서블릿이 공유하게 되는 공통적인 빈(@Service, @Repository, db설정 등)들을 웹 어플리케이션 컨텍스트(루트 컨텍스트)에 등록하면 서블릿마다 중복돼서 생성되는 걸 방지할 수 있다
> 계층구조로 만드는 이유 : 웹 기술에 의존적인 부분과 그렇지 않은 부분을 구분하기 위해서다
> 스프링 기술을 사용해서 데이터 액세스 게층이나, 서비스 계층은 빈으로 등록하지만, 프레젠테이션 계층은 반드시 스프링 기술을 사용하지 않고 만드는 경우도 있기 떄문이다
> 주의사항
> 1. 서블릿 컨텍스트의 빈은 루트 애플리케이션 컨텍스트의 빈을 참조할 수 있지만, 반대는 안된다
> 2. 루트 애플리케이션 컨텍스트에 정의된 빈은 이름이 같은 서블릿 컨텍스트의 빈이 존재하면 무시될 수 있다(overwrite)
> 3. 하나의 컨텍스트에 정의된 AOP설정은 다른 컨텍스트의 빈에는 영향을 미치지 않는다
## 웹 애플리케이션의의 컨텍스트 구성방법
> 1. 게층구조
     > 스프링 웹 기술을 사용하는 경우 웹 관련 빈들은 서블릿의 컨텍스트에 두고 나머지는 루트 애플리케이션 컨텍스트에 등록한다
> 2. 루트 애플리케이션 컨텍스트 단일구조
     > 스프링 웹 기술을 사용하지 않고, 서드파티 웹 프레임워크나 서비스 엔진만을 사용해서 프레젠테이션 계층을 만든다면, 스프링 서블릿(DispatcherServlet)을 둘 이유가 없다, 이떄는 루트 애플리케이션 컨텍스트만 등록하면 된다
> 3. 서블릿 컨텍스트 단일구조
     > 스프링 외의 프레임워크나 서비스 엔진에서 스프링의 빈을 이용할 생각이 아니라면 루트 애플리케이션 컨텍스트를 생략할 수 있다
## 스프링 웹 연동의 시발점: ServletContext
> 스프링 컨텍스트가 구동되기 직전, WAS(톰캣)가 가장 먼저 켜지면서 web.xml파일 정보를 파싱해서 웹 애플리케이션당 단 하나 존재하는 전역 환경 객체인 ServletContext를 생성한다.
> ServletContext의 구현체 org.apache.catalina.core.ApplicationContextFacade에 xml 설정들이 저장된다
> 톰캣은 이 ServletContext를 스프링 프레임워크 쪽에 넘겨주며, 스프링은 이를 매개체로 삼아 톰캣 엔진 위에 루트 컨텍스트와 서블릿 컨텍스트를 차례대로 등록하고 연동하게 된다.
## 루트 애플리케이션 컨텍스트 등록
``` 구버전 (xml 설정)
    ContextLoaderListener.java
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext scToUse = getServletContextToUse(event);
		initWebApplicationContext(scToUse);
	}
```
``` 최신버전 (java code 설정)
    AbstractContextLoaderInitializer.java
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		registerContextLoaderListener(servletContext);
	}
```
## 서블릿 애플리케이션 컨텍스트 등록
``` 구버전 (xml 설정)
그냥 web.xml파일에 <servlet></servlet> 태그로 선언
```
``` 최신버전 (java code 설정)
    AbstractDispatcherServletInitializer.java
    @Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		registerDispatcherServlet(servletContext);
	}
```
```자바코드로 한번에 설정하는법
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    // 부모 컨텍스트 설정 클래스 지정
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] { RootConfig.class }; // 개발자가 만든 자바 설정 파일, @Servie, @Repository
    }

    // Servlet) 컨텍스트 설정 클래스 지정
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebConfig.class };   // @Controller, ViewResolver 설정 파일
    }

    // DispatcherServlet의 URL 매핑 패턴 지정
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}

```
## atResource
> xml설정 대신 @Resource 애노테이션의 빈 의존관계를 설정할 수 있다
> 1. <context:annotation-config />
> > 애노테이션 의존관계 정보를 읽어서 메타정보를 추가해주는 기능을 가진 빈 후처리기 전용 태그 사용
> 2. <context:component-scan />
> > 빈 스캐닝을 통한 빈 등록방법
> 3. AnnotationConfigApplicationContext AnnotationConfigWebApplicationContext
> > 빈 스캐너와 애노테이션 의존관계 정보를 읽는 후처리기를 내장한 애플리케이션 컨텍스트 사용
## atAutowiredCollection
> @Autowired는 스프링 전용 의존관계 설정 애노테이션, 타입에 의한 자동 와이어링 방식으로 동작 
> 같은 타입의 빈이 여러개 있을때 컬렉션이나, 배열로 주입 받을수 있다
> 충돌을 피하려는 목적으로 사용해선 안 되고, 의도적으로 여러개 빈을 등록하고 이를 모두 참조하거나, 선별적으로 찾을때 사용하는 것이 좋다
## atQualifier
> 같은 타입의 빈이 여러개 등록됬을때, 타입에 의한 자동와이어링을 시도하면 에러가 발생한다
> @Qualifier를 사용해서 자동와이어링 대상을 제한할 수 있다
> 먼저 한정자 이름을 확인 하고 매칭되지 않는다면, 빈 이름을 확인한다
## atInject
> @Inject는 JSR-330에 정의된 자바 표준 스펙이다 @Autowired와 같은 역할을 한다
## id
> 하나의 빈은 여러 개의 이름을 가질수 있다, 특수문자가 포함된 빈 식별자도 가능하다
## hi
> id테스트를 java code로 구현한것
> @Named는 자바 표준 스펙(JSR-330)으로 빈의 이름을 지정, @Bean은 스프링 전용 애노테이션이다