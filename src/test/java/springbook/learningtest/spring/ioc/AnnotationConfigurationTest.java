package springbook.learningtest.spring.ioc;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import springbook.learningtest.spring.ioc.resource.Hello;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationConfigurationTest {

    String basePath;
    String beanBasePath;

    @BeforeEach
    void setUp() {
        basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";
        beanBasePath = basePath + "annotation/";
    }

    @Test
    void atResource() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "resource.xml");
        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();
        assertEquals("Hello Spring", ac.getBean("printer").toString());
    }

    @Test
    void atAutowiredCollection() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(Client.class, ServiceA.class, ServiceB.class);
        Client client = ac.getBean(Client.class);
        assertEquals(2, client.beanBArray.length);
        assertEquals(2, client.beanBSet.size());
        assertEquals(2, client.beanBMap.size());
        assertEquals(2, client.beanBList.size());
        assertEquals(2, client.beanBCollection.size());
    }

    static class Client {
        @Autowired Set<Service> beanBSet;
        @Autowired Service[] beanBArray;
        @Autowired Map<String, Service> beanBMap;
        @Autowired List<Service> beanBList;
        @Autowired Collection<Service> beanBCollection;
    }

    interface Service {}
    static class ServiceA implements Service {}
    static class ServiceB implements Service {}

    @Test
    void atQualifier() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(QClient.class, QServiceA.class, QServiceB.class);
        QClient qClient = ac.getBean(QClient.class);
        assertEquals(QServiceA.class, qClient.service.getClass());
    }

    static class QClient {
        @Autowired @Qualifier("mainService") Service service;
    }

    @Qualifier("mainService")
    static class QServiceA implements Service {}
    static class QServiceB implements Service {}

    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    @interface Main{}

    @Test
    void atInject() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(IClient.class, IServiceA.class, IServiceB.class);
        IClient iClient = ac.getBean(IClient.class);
        assertEquals(IServiceA.class, iClient.service.getClass());
    }

    static class IClient {
        @Inject
        @Main Service service;
    }

    @Main
    static class IServiceA implements Service {}
    static class IServiceB implements Service {}
}
