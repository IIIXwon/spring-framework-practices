package springbook.learningtest.spring.ioc;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AutoRegisteredBeansTest {
    @Test
    public void autoRegisteredBean() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SystemBean.class);
        SystemBean bean = ac.getBean(SystemBean.class);
        assertNotNull(bean.configurationClassPostProcessor);
        assertNotNull(bean.autowiredAnnotationBeanPostProcessor);
        assertNotNull(bean.commonAnnotationBeanPostProcessor);
        assertNotNull(bean.eventListenerMethodProcessor);
        assertNotNull(bean.defaultEventListenerFactory);
        assertNotNull(bean.applicationContext);
        assertNotNull(bean.beanFactory);
        assertNotNull(bean.resourceLoader);
        assertNotNull(bean.applicationEventPublisher);
        assertNotNull(bean.systemProperties);
        assertNotNull(bean.systemEnvironment);
        assertNotNull(bean.standardEnvironment);
        assertNotNull(bean.applicationStartup);
        assertNotNull(bean.messageSource);
        assertNotNull(bean.applicationEventMulticaster);
        assertNotNull(bean.lifecycleProcessor);
    }

    @Test
    public void genericApplicationContextAutoRegisteredBean() throws NoSuchFieldException, IllegalAccessException {
        System.getProperties().put("os.name", "Hi");
        // 방법 1: 빈 설정 클래스를 생성자에 직접 전달 (자동 refresh 실행됨)
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.refresh();
        // 만약 기본 생성자를 썼다면 반드시 아래 주석처럼 refresh를 수동으로 해주어야 합니다.
        // AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        // ac.register(SystemBean.class);
        // ac.refresh(); <--- 이게 없으면 "has not been refreshed yet" 에러 발생!

        // 이제 안전하게 BeanFactory를 꺼내옵니다.
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ac.getBeanFactory();

        System.out.println("=========================================================");
        System.out.println("   [ULTRA VIEW] 스프링 컨테이너 내부 인프라 완전 정복");
        System.out.println("=========================================================");

        // [PART 1] 일반 스프링 빈 목록
        System.out.println("\n▶ 1. 정식 등록된 스프링 빈 목록");
        for (String name : ac.getBeanDefinitionNames()) {
            assertNotNull(ac.getBean(name));
            System.out.println("   - BeanName: " + name + " -> " + ac.getBean(name).getClass().getName());
        }

        // [PART 2] 주입 가능한 특수 인터페이스 추출 (리플렉션)
        System.out.println("\n▶ 2. 빈 목록엔 없지만 주입 가능한 인터페이스 (Resolvable Dependencies)");
        Field resolvableField = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
        resolvableField.setAccessible(true);
        Map<Class<?>, Object> resolvableDependencies = (Map<Class<?>, Object>) resolvableField.get(beanFactory);

        for (Map.Entry<Class<?>, Object> entry : resolvableDependencies.entrySet()){
            Object value = entry.getValue();
            assertNotNull(value);
            System.out.println("   - 주입 가능한 타입: " + entry.getKey().getName() + " -> " + value.getClass().getName());
        }

        // [PART 3] 특수 환경 오브젝트
        System.out.println("\n▶ 3. 컨테이너 내부 환경 싱글톤 객체들");
        String[] singletonNames = beanFactory.getSingletonNames();
        for (String sName : singletonNames) {
            if (!beanFactory.containsBeanDefinition(sName)){
                assertNotNull(ac.getBean(sName));
                Object singletonObject = beanFactory.getSingleton(sName);
                System.out.println("   - Infra Object Name: " + sName + " -> " + singletonObject.getClass().getName());
            }
        }
        System.out.println("=========================================================");
    }

    @Test
    public void annotationConfigApplicationContextAutoRegisteredBean() throws NoSuchFieldException, IllegalAccessException {
        System.getProperties().put("os.name", "Hi");
        // 방법 1: 빈 설정 클래스를 생성자에 직접 전달 (자동 refresh 실행됨)
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SystemBean.class);

        // 만약 기본 생성자를 썼다면 반드시 아래 주석처럼 refresh를 수동으로 해주어야 합니다.
        // AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        // ac.register(SystemBean.class);
        // ac.refresh(); <--- 이게 없으면 "has not been refreshed yet" 에러 발생!

        // 이제 안전하게 BeanFactory를 꺼내옵니다.
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ac.getBeanFactory();

        System.out.println("=========================================================");
        System.out.println("   [ULTRA VIEW] 스프링 컨테이너 내부 인프라 완전 정복");
        System.out.println("=========================================================");

        // [PART 1] 일반 스프링 빈 목록
        System.out.println("\n▶ 1. 정식 등록된 스프링 빈 목록");
        for (String name : ac.getBeanDefinitionNames()) {
            assertNotNull(ac.getBean(name));
            System.out.println("   - BeanName: " + name + " -> " + ac.getBean(name).getClass().getName());
        }

        // [PART 2] 주입 가능한 특수 인터페이스 추출 (리플렉션)
        System.out.println("\n▶ 2. 빈 목록엔 없지만 주입 가능한 인터페이스 (Resolvable Dependencies)");
        Field resolvableField = DefaultListableBeanFactory.class.getDeclaredField("resolvableDependencies");
        resolvableField.setAccessible(true);
        Map<Class<?>, Object> resolvableDependencies = (Map<Class<?>, Object>) resolvableField.get(beanFactory);

        for (Map.Entry<Class<?>, Object> entry : resolvableDependencies.entrySet()){
            Object value = entry.getValue();
            assertNotNull(value);
            System.out.println("   - 주입 가능한 타입: " + entry.getKey().getName() + " -> " + value.getClass().getName());
        }

        // [PART 3] 특수 환경 오브젝트
        System.out.println("\n▶ 3. 컨테이너 내부 환경 싱글톤 객체들");
        String[] singletonNames = beanFactory.getSingletonNames();
        for (String sName : singletonNames) {
            if (!beanFactory.containsBeanDefinition(sName)){
                assertNotNull(ac.getBean(sName));
                Object singletonObject = beanFactory.getSingleton(sName);
                System.out.println("   - Infra Object Name: " + sName + " -> " + singletonObject.getClass().getName());
            }
        }
        System.out.println("=========================================================");

        SystemBean bean = ac.getBean(SystemBean.class);
        System.out.println(bean.osname);
        System.out.println(bean.path);

    }

    static class SystemBean {
        // 1. BeanDefinition
        @Resource
        ConfigurationClassPostProcessor configurationClassPostProcessor;
        @Autowired
        AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor;
        @Resource
        CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor;
        @Autowired
        EventListenerMethodProcessor eventListenerMethodProcessor;
        @Resource
        DefaultEventListenerFactory defaultEventListenerFactory;

        // 2. Resolvable Dependencies
        @Resource
        ApplicationContext applicationContext;
        @Autowired
        BeanFactory beanFactory;
        @Autowired
        ResourceLoader resourceLoader;
        @Autowired
        ApplicationEventPublisher applicationEventPublisher;

        // 3. 컨테이너 내부 환경 싱글톤 객체들
        @Resource
        Properties systemProperties;
        @Autowired
        Map systemEnvironment;
        @Resource
        StandardEnvironment standardEnvironment;
        @Autowired
        ApplicationStartup applicationStartup;
        @Resource
        MessageSource messageSource;
        @Autowired
        ApplicationEventMulticaster applicationEventMulticaster;
        @Autowired
        @Lazy // Autowired되는 시점에 lifecycleProcessor이 없을 수 있다
        LifecycleProcessor lifecycleProcessor;

        @Value("#{systemProperties['os.name']}")
        String osname;
        @Value("#{systemEnvironment['PATH']}")
        String path;
    }
}
