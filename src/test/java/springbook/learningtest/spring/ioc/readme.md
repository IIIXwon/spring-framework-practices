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
## simpleAtBean
> **핵심 요약**
> XML 없이 순수 자바 코드만으로 스프링 컨텍스트 설정을 구동함.
> `HelloService` 등록 시 내장 클래스의 디폴트 식별자 규칙을 쓰지 않고, 자바 표준인 `@Named("service")`를 사용하여 빈 이름을 커스텀하게 명시함.
>
> * **스프링 싱글톤 보장 (Full 모드 vs Lite 모드)**
    >   * 자바 코드로 빈 설정 시, 상단에 `@Configuration`이 선언되지 않은 클래스 내부에서 수동으로 `@Bean`을 등록할 때는 매우 주의해야 한다.
>   * **`@Configuration`이 선언된 클래스 (Full 모드)**
      >     * 스프링이 **CGLIB** 바이트코드 조작 라이브러리를 사용해 동적 프록시 객체를 만들어 낸다.
>     * 내부의 `@Bean` 메서드를 자바 코드로 직접 호출하더라도, 컨테이너가 개입하여 이미 등록된 빈을 찾아 반환하므로 **철저하게 싱글톤이 보장된다.**
>   * **`@Configuration`이 선언되지 않은 클래스 (Lite 모드) - *주의!***
      >     * CGLIB 프록시가 적용되지 않고 **순수 자바 클래스**로 작동한다.
>     * 만약 내부나 외부에서 다른 `@Bean` 팩토리 메서드를 **자바 코드 방식으로 직접 호출(`this.printer()`)**하면, 호출할 때마다 `new` 객체가 매번 새로 생성되어 **싱글톤이 완전히 깨지게 된다.**
>
> * **💡 이 테스트 코드가 에러 없이 통과(싱글톤 유지)한 이유**
    >   * 이 테스트 코드 내부의 `hello()`, `hello2()` 메서드는 싱글톤이 깨지는 `this.printer()` 메서드를 직접 호출하는 악수를 두지 않았다.
>   * 대신, 스프링이 생성 시점에 `@Autowired`로 딱 한 번 클래스 필드에 주입해 준 `this.printer` 멤버 변수를 수평적으로 안전하게 공유(참조)했기 때문에 싱글톤이 유지되어 통과한 것이다.

## valueInjection
> * 외부에서 값을 주입하는 두 가지 주요 용도
> * **환경에 따라 매번 달라지는 값**: 데이터베이스 연결 정보(DataSource의 url, username, password, DriverClass 등)가 대표적이다.
> * **클래스 필드의 동적 초기화**: 기본 초기값을 가지고 있으나 특정 상황(테스트 환경, 이벤트 기간 등)에서 다른 값으로 변경해야 할 때 유용하다.
> * 설정이 바뀌더라도 소스 코드를 컴파일 하지 않아도 된다
> * `@Value("${...}")`를 처리하기 위해 `PropertySourcesPlaceholderConfigurer`와 같은 빈 후처리기 등록이 필요하다.
## importResource
> * 자바 코드 기반 설정(`@Configuration`)과 기존의 XML 설정 방식을 혼용하여 유연하게 사용할 수 있다.
> * 클래스 상단에 `@ImportResource("/경로/설정.xml")` 형태로 지정하여 XML에 등록된 빈을 자바 컨텍스트로 가져온다.
## propertyEditor
> * XML 설정과 애노테이션의 `@Value` 속성에 들어가는 값은 본질적으로 모두 **문자열**로 작성된다.
> * 프로퍼티의 타입이 문자열이 아닐 경우, 스프링은 내부적으로 `PropertyEditor`나 `ConversionService`를 통해 타입 변환을 시도한다.
> * 기본 타입(Primitive, Charset, File, 배열 등)은 스프링이 제공하는 기본 에디터에 의해 자동으로 형변환된다.
> * 만약 복잡한 커스텀 타입(오브젝트)으로 변환해야 한다면 개발자가 직접 인터페이스를 구현하여 등록해 주어야 한다.
## collectionInject
> * `List`, `Set`, `Map`, `Properties` 등의 컬렉션 타입도 외부(XML 등)에서 통째로 값을 주입받을 수 있다.
> * 컬렉션 내부에 여러 가지 타입의 오브젝트가 무분별하게 혼용되면 런타임에 타입 변환 에러가 발생할 수 있다.
> * 안정성을 위해 가능한 한 **타입 가이드라인(Generic)**를 명시하여 스프링 컨테이너가 적합한 타입 변환기를 올바르게 적용할 수 있도록 설계해야 한다.

## autoRegisteredBean
> * ApplicationContext 객체를 생성 하면 자동으로 등록되는 빈들이 있다, ApplicationContext 구현체에 빈 후처리기 내장 유무에 따라 등록되는 빈의 목록이 다르다, Resolvable Dependencies, 컨테이너 내부 환경 싱글톤 객체들은 공통으로 생성된다
> * Resolvable Dependencies은 빈 이름을 가지고 있지 않아서 ac.getBean("빈 이름")으로 사용할수 없지만, 스프링 컨테이너에서 DI를 받아 사용할 수 있다
> * SystemProperties는 JVM이 가지는 속성, SystemEnvironment는 OS의 환경변수를 가리킨다
> * SystemEnvironment의 타입이 Map<String, Object>이 아니라, Map인 이유 : 하위 호환성과 컨테이너에 등록된 특정 타입의 빈을 컬렉션에 담아 달라는 다중 빈 주입 메커니즘 떄문에 관례적으로 Map을 선언한다 