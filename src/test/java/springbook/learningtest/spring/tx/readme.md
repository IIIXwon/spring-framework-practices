## PlatformTransactionManager
> * 스프링 트랜잭션 추상화의 핵심, 트랜잭션의 경계를 지정하는 데 사용한다
> * 여러 트랜잭션 서비스 구현체들은 모두 PlatformTransactionManager를 상속받아 구현되어 있다
> * 그래서 트랜잭션 서비스의 종류나 환경이 바뀌더라도 트랜잭션을 사용하는 코드는 그대로 유지할수 있는 유연성을 얻을수 있다
## 트랜잭션 경계설정 전략
> * 코드에 의한 프로그램적인 방법 : 트랜잭션을 다루는 코드를 직접 만든다
> * 테스트 코드에서 의도적으로 트랜잭션을 만들고 종료시키거나, 여러번 트랜잭션을 거치는 상황을 만들어야 하는 경우 유용하다
> * AOP를 이용한 선언적인 방법 : AOP를 이용해 기존 코드에 트랜잭션 경계설정 기능 부여
> * 데코레이터 패턴을 적용한 프록시 빈을 사용해서 코드에는 전혀 영향을 주지 않으면서, 특정 메소드 실행전 후에 트랜잭션이 시작되고 종료되거나 기존 트랜잭션에 참여하도록 만들 수 있다
> * AOP를 이용해 트랜잭션 기능을 부여하는 방법은 크게 두 가지가 있다
## AOP와 tx 네임스페이스
> * xml 파일에서 편리하게 사용할 수 있는 전용 태그를 제공한다
> * <tx:advice></txAdvice>, <aop:config></aop:config>
## @Transactional
> * @Transacntion 애노테이션을 사용해 트랜잭션 AOP적용할 수 있다 
> * xml파일에도 <tx:annotaion-drive/>태그만 추가하면 된다, 자바코드 방삭이라면 @EnableTransactionManagement
> * 트랜잭션 경계설정 방법 두가지는 각각 장단점이 있다
> * aop와 tx 스키마의 태그 : 복잡해보이지만 코드에는 전혀 영향을 주지 않고 일괄적으로 트랜잭션을 적용하거나 변경할 수 있다
> * @Transacntionl : 일일이 대상 인터페이스나 클래스, 메소드에 부여하는 건 번거로운 작업이지만, 태그 방식에 비해 세밀한 설정이 가능하다
## AOP방식
> * 스프링 AOP의 기본 방식은 PROXY 방식이다, 그리고 AOP 전용 프레임워크인 AspectJ 모드도 지원한다
```java
    @Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
static class Config {
    @Bean
    public MemberDao memberDao(DataSource dataSource) {
        MemberDaoImpl memberDao = new MemberDaoImpl();
        memberDao.setDataSource(dataSource);
        memberDao.initTemplateConfig(dataSource);
        return memberDao;
    }
@Transactional
    static class MemberDaoImpl extends JdbcTemplate implements MemberDao {
        ...
        @Override
        @Transactional(propagation = Propagation.NEVER)
        public void addWithoutTx(List<Member> members) {
            add(members);
            // proxy 모드 : 타깃 오브젝트 안에서 일어나는 메서드에는 aop가 적용되지 않는다 
            // aspectj 모드 : 타깃 오브젝트 안에서 일어나는 메서드도 aop가 적용된다 단 jvm에 로드타임 위버 같은 부가설정을 해야한다
        }
    }
```
## 트렌젝션 속성
> * 전파 Propagation : 트랜잭션을 시작하거나, 기존 트랜잭션에 참여하는 방법을 결정하는 속성
> > * REQUIRED : 디폴트 속성, 미리 시작된 트랜잭션이 있으면 참여하고, 없으면 새로 시작한다, 하나의 트랜잭션이 시작된 후에 다른 트랜잭션 경계가 설정된 메소드를 호출하면 자연스럽게 같은 트랜잭션으로 묶인다
> > * SUPPORTS : 이미 트랜잭션이 있으면 참여하고, 없다면 트랜잭션 없이 진핸한다
> > * MANDATORY : 이미 트랜잭션이 있으면 참여하고, 없다면 예외를 발생시킨다 혼자서 독립적으로 트랜잭션이 진행되면 안 되는 경우 사용한다
> > * REQUIRED_NEW : 항상 새로운 트랜잭션을 시작한다, 이미 시작된 트랜잭션이 있다면 보류시킨다
> > * NOT_SUPPORTED : 트랜잭션을 사용하지 않게 한다, 이미 진행 중인 트랜잭션이 있다면 보류시킨다
> > * NEVER : 트랜잭션을 사용하지 않도록 강제한다, 이미 진행 중인 트랜잭션이 있다면 예외를 발생시킨다
> > * NESTED : 이미 진행 중인 트랜잭션이 있으면 중첩 트랜잭션을 시작한다, 독립적인 트랜잭션을 만드는 REQUIRED_NEW와 다르다, 자식 트랜잭션은 부모 트랜잭션의 영향을 받지만 자식 트랜잭션의 커밋과 롤백은 부모 트랜잭션에 영향을 주지 않은다
> * 격리 수준 isolation : 동시에 여러 트랜잭션이 진행될 떄에 트랜잭션의 작업 결과를 다른 트랜잭션에게 어떻게 노출할 것인지를 결정하는 기준이다
> > * DEFAULT : 사용하는 데이터 액세스 기술 또는 DB 드라이버 디폴트 설정을 따른다, 보통 READ_COMMITTED다,
> > * READ_COMMITTED : 다른 트랜잭션이 커밋하지 않은 정보는 읽을 수 없다, 대신 하나의 트랜잭션이 읽은 로우를 다른 트랜잭션이 수정할 수 있다 떄문에 처음 트랜잭션이 같은 로우를 다시 읽을 경우 다른 내용이 발견될 수 있다
> > * REPEATABLE_READ : 트랜잭션이 읽은 로우를 다른 트랜잭션이 수정하는 것을 막는다, 하지만 새로운 로우를 추가하는 것은 제한하지 않는다, 따라서 SELECT로 조건에 맞는 로우를 전부 가져오는 경우 트랜잭션이 끝나기 전에 추가된 로우가 발견될 수 있다
> > * SERIALIZABLE : 트랜잭션을 순차적으로 진행시켜주기 떄문에 여러 트랜잭션이 동시에 같은 테이블의 정보를 액세스하지 못한다, 안전하지만 성능이 떨어지기 때문에 극단적으로 안전한 작업이 필요한 경우가 아니라면 자주 사용되지 않는다
> * 제한시간 timeout, timeoutString : 트랜잭션의 제한시간을 초 단위로 지정한다
> * 읽기 전용 트랜잭션 readOnly : 성능 최적화나, 쓰기 작업을 의도적으로 방지하기 위해 사용할 수 있다, 쓰기 작업이 진행되면 예외가 발생한다, 일부 트랜잭션 매니저의 경우 무시하고 쓰기 작업을 허용할 수 있어 주의해야 한다
> * 롤백 예외 rollbackFor, rollbackForClassName: 선언적 트랜잭션에서는 런타임 예외가 발생하면 롤백한다, 반면 예외가 전혀 발생하지 않거나 체크 예외가 발생하면 커밋한다, 체크 예외를 커밋 대상으로 삼은 이유는 예외적인 상황에서 사용되기보다는 체크 예외가 리턴 값을 대신해서 비즈니스적인 의미를 담은 결과로 돌려주는 용도로 많이 사용되기 떄문이다, 체크 예외지만 롤백 대상으로 해야한다면 지정할 수 있다
> * 트랜잭션 커밋 예외 noRollbackFor,noRollbackForClassName : rollbackFor, rollbackForClassName와 반대로 런타임 예외를 트랜잭션 커밋 대상으로 지정한다
## 데이터 액세스 기술 트랜잭션 통합
> * 스프링은 두개 이상의 데이터 액세스 기술로 만든 DAO를 하나의 트랜잭션으로 묶어서 사용하는 방법을 제공한다, `DB당 트랜잭션 매니저는 하나만 사용한다는 원칙은 바뀌지 않는다`, 하나의 트랜잭션 매니저가 여러 개의 데이터 액세스 기술의 트랜잭션 기능을 지원해주도록 만드는 것이다
> * 예를 들면 JPA DAO로 일부 엔티티-테이블을 업데이트 하는것과 ,JDBC DAO로 복잡한 DB 전용 쿼리를 사용해 데이터를 가져오는 것을 하나의 트랜잭션 안에서 진행할 수 있다
## 트랜잭션 매니저별 조합 가능 기술
> * DataSourceTransactionManager : JDBC + iBatis, 트랜잭션을 통합하려면 동일한 DataSource를 사용해야 한다
> * JpaTransactionManager : JPA + JDBC + iBatis, JPA는 EntityManagerFactory를 통해 트랜잭션을 동기화 한다
> * HibernateTransactionManager : Hibernate + JDBC + iBatis, Hibernate는 SessionFactory를 통해 트랜잭션을 동기화 한다
> * JtaTransactionManager : 모든 종류의 데이터 액세스 기술의 DAO가 같은 트랜잭션 안에서 동작하게 만들 수 있다, 가장 강력하지만 JTA 서버 환경을 구성해야 하고 서버의 트랜잭션 매니저와 XA를 지원하는 특별한 DataSource를 구성하는 등의 부가적인 준비 작업이 필요하다, 하나 이상의 DB 또는 JMS와 같은 트랜잭션이 지원되는 서비스를 통합해서 하나의 트랜잭션으로 관리하려고 할 떄는 JTA가 반드시 필요하다