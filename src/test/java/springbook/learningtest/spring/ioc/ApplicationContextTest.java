package springbook.learningtest.spring.ioc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import springbook.learningtest.spring.ioc.bean.*;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextTest {
    String basePath;
    StaticApplicationContext ac;

    @BeforeEach
    void setUp() {
        basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";
        ac = new StaticApplicationContext();
    }

    @Test
    void registerBean() {
        ac.registerSingleton("hello1", Hello.class);
        Hello hello1 = ac.getBean("hello1", Hello.class);
        assertNotNull(hello1);

        RootBeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        ac.registerBeanDefinition("hello2", helloDef);

        Hello hello2 = ac.getBean("hello2", Hello.class);
        assertEquals("Hello Spring", hello2.sayHello());
        assertNotEquals(hello1, hello2);

        assertEquals(2, ac.getBeanFactory().getBeanDefinitionCount());
    }

    @Test
    void registerBeanWithDependency() {
        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        RootBeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));
        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertEquals("Hello Spring", ac.getBean("printer").toString());
    }

    @Test
    void genericApplicationContext() {
        GenericApplicationContext ac = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
        reader.loadBeanDefinitions("springbook/learningtest/spring/ioc/genericApplicationContext.xml");
        ac.refresh();
        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();
        assertEquals("Hello Spring", ac.getBean("printer").toString());
    }

    @Test
    void genericXmlApplicationContext() {
        GenericApplicationContext ac = new GenericXmlApplicationContext(basePath + "genericApplicationContext.xml");
        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();
        assertEquals("Hello Spring", ac.getBean("printer").toString());
    }

    @Test
    void createContextWithoutParent() {
        assertThrows(BeanCreationException.class, () -> new GenericXmlApplicationContext(basePath + "childContext.xml"));
    }

    @Test
    void contextHierarchy() {
        ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");
        GenericApplicationContext child = new GenericApplicationContext(parent);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions(basePath + "childContext.xml");
        child.refresh();

        Printer printer = child.getBean("printer", Printer.class);
        assertNotNull(printer);

        Hello hello = child.getBean("hello", Hello.class);
        assertNotNull(hello);

        hello.print();
        assertEquals("Hello Child", printer.toString());
    }

    @Test
    void simpleBeanScanning() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext("springbook.learningtest.spring.ioc.bean");
        AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
        assertNotNull(hello);
    }

    @Test
    void filterBeanScanning() {
        ApplicationContext ctx = new GenericXmlApplicationContext(basePath + "filteredScanningContext.xml");
        AnnotatedHello annotatedHello = ctx.getBean("annotatedHello", AnnotatedHello.class);
        assertNotNull(annotatedHello);
        Hello hello = ctx.getBean("hello", Hello.class);
        assertNotNull(hello);
    }

    @Test
    void configurationBean() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
        AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
        assertNotNull(hello);

        AnnotatedHelloConfig config = ctx.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
        assertNotNull(config);

        assertEquals(config.annotatedHello(), hello);
        assertEquals(config.annotatedHello(), config.annotatedHello());

        System.out.println(ctx.getBean("systemProperties").getClass());
    }

    @Test
    void constructorArgName() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "constructorInjection.xml");
        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();
        assertEquals("Hello Spring", ac.getBean("printer").toString());
    }

    @Test
    void autowire() {
        ApplicationContext ac = new GenericXmlApplicationContext(basePath + "autowire.xml");
        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();
        assertEquals("Hello Spring", ac.getBean("printer").toString());
    }
}