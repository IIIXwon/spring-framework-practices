## EntityManagerFactory
> * JPA 퍼시스턴스 컨텍스트에 접근하고, 엔티티 인스턴스를 관리하려면 EntityManager를 구현한 오브젝트가 필요하다
> * EntityManager는 JPA에서 두가지 방식으로 관리 되는데, 하나는 애플리케이션이 관리하는 EntityManager이고, 다른 하나는 컨테이너가 관리하는 EntityManager다
> * 어떤 방식을 사용하든 반드시 EntityManagerFactory를 빈을호 등록해야 한다, 스프링에서는 세 가지 방법을 이용해 EntityManagerFactory 빈을 등록할 수 있다
> * LocalEntityManagerFactoryBean, JavaEE 서버가 제공하는 EntityManagerFactoryBean은 거의 사용하지 않는다 
## LocalContainerEntityManagerFactoryBean
> * 스프링이 직접 제공하는 컨테이너 관리 EntityManager를 위한 EntityManagerFactory다
## 트랜잭션 매니저
> * 컨테이너가 관리하는 EntityManager 방식에서는 컨테이너가 제공하는 트랜잭션 매니저가 반드시 필요하다
> * 스프링의 EntityManager를 사용하려면 적절한 트랜잭션 매니저가 등록되어 있어야한다
> * JPA는 반드시 드랜잭션 안에서 동작하도록 설계되어 있다
> * LocalContainerEntityManagerFactoryBean 사용할 떄는
```java
@Bean
public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
return new JpaTransactionManager(emf);
}
```
> * 같이 트랜잭션 매니저를 추가해야한다
## 애플리케이션 관리 EntityManager
> * EntityManager를 사용하는 두 번쨰 방법은 애플리케이션 코드가 관리하는 EntityManager를 이용하는 것이다
```java
EntityManager em = dao.emf.createEntityManager();
```
> * 이후 모든 작업은 일반적인 EntityManager와 사용방법이 같다
```java
        em.createQuery("delete from Member ").executeUpdate();
        em.getTransaction().begin();
        Member m = new Member(10, "Spring", 7.8);
        em.persist(m);
        Long count = em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
        assertEquals(1L, count);
        em.getTransaction().commit();
```
> * 트랜잭션이 시작되는 벙위 밖에서 EntityManager를 사용하면
```
jakarta.persistence.TransactionRequiredException: No active transaction for update or delete query
```
> * 트랜잭션을 활성화 하라는 에러메세지가 나타난다
> * EntityManager는 스프링 빈으로 등록되지 않아 DI를 위한 특별한 @PersistenceContext 애노테이션을 사용한다, 하지만 @Autowired를 사용해도 되긴한다 (DI 결과는 같음)
> * 이유 : EntityManager를 사용하려면 트랜잭션 범위 안에서 사용해야하는데 DI를 통해 EntityManager가 한번만 생성되는 건 말이 안되기 떄문이다
> * 확인 해보면 EntityManager객체는 실제 객체가 아니라 proxy객체를 등록해 연결된 트랜잭션 마다 독립적인 EntityManager를 사용한다
> * @PersistenceContext의 type 속성
> * PersistenceContextType.TRANSACTION : 트랜잭션의 시작과 종료에 맞춰 영속성 컨텍스트가 생성되고 닫힌다.
```java
    public static class MemberDao {
        @PersistenceContext(type = PersistenceContextType.TRANSACTION)
        EntityManager em;
        @PersistenceUnit
        EntityManagerFactory emf;

        public void addDuplicatedId() {
            em.persist(new Member(10, "Spring", 7.8));
            em.persist(new Member(10, "Spring", 7.8));
            em.flush();
        }
    }

    @Test
    @Transactional
    void JpaApiException() {
        // assertThrows(PersistenceException.class, () -> dao.addDuplicatedId()); // 트랜잭션 설정 없이 persist api 사용시 에러
        // dao.addDuplicatedId(); // 키 중복 에러
    }
```
> * PersistenceContextType.EXTENDED : 트랜잭션 벙위를 벗어나서도 영속성 컨텍스트와 엔티티 상태가 유지가 됨
```java
    public static class MemberDao {
        @PersistenceContext(type = PersistenceContextType.EXTENDED)
        EntityManager em;
        @PersistenceUnit
        EntityManagerFactory emf;

        public void addDuplicatedId() {
            em.persist(new Member(10, "Spring", 7.8));
            em.persist(new Member(10, "Spring", 7.8));
            em.flush();
        }
    }

    @Test
    @Transactional
    void JpaApiException() {
         assertThrows(PersistenceException.class, () -> dao.addDuplicatedId()); // 키 중복 에러
    }
```
## JPA 예외 변환
> * JPA API를 사용하다 에러메세지가 나면 자동으로 스프링의 데이터 액세스 예외 추상화 클래스로 변환되지 않는다
> * 스프링 AOP를 사용해서 스프링 데이터 액세스 예외 추상화 클래스로 변환할 수 있다
> * @Repository : 빈 스캐닝을 위한 스테레오타입 애노테이션 뿐만아니라 예외 변환 기능을 위한 AOP 포인트컷 이기도 하다
> * PersistenceExceptionTranslationPostProcessor 빈 등록 : 실질적으로 예외 변환을 하는 후처리기 빈이다