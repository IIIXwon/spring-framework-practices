package springbook.learningtest.spring.ioc;

import jakarta.annotation.Resource;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import springbook.learningtest.spring.ioc.bean.Hello;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(locations = "identifier.xml")
public class BeanIdentifierTest {
    @Autowired
    Hello hello;
    @Resource
    Hello 하이;
    @Resource
    ApplicationContext ac;

    @Test
    void id() {
        assertAll(() -> {
            assertNotNull(hello);
            assertNotNull(하이);
            assertEquals(hello, 하이);
            assertEquals(hello, ac.getBean("1234"));
            assertEquals(hello, ac.getBean("/hello"));
            assertEquals(hello, ac.getBean("헬로우"));
        });
        System.out.println("OK5");
    }

    @Component("하이")
    static class Hi {
    }

    @Component
    @Named("하우디")
    static class Howdy {
        @Resource
        Hi 하이;
    }

    @Configuration
    static class Config {
        @Bean(name = {"울랄라", "흠흠"})
        public Howdy lala(Hi 하이) {
            Howdy h = new Howdy();
            h.하이 = 하이;
            return h;
        }
    }
    @Test
    void hi() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Hi.class, Howdy.class, Config.class);
        Hi 하이 = ac.getBean("하이", Hi.class);
        assertNotNull(하이);
        Howdy h = ac.getBean("하우디", Howdy.class);
        assertEquals(하이, h.하이);
        Howdy h2 = ac.getBean("울랄라", Howdy.class);
        assertEquals(하이, h2.하이);
        Howdy h3 = ac.getBean("흠흠", Howdy.class);
        assertEquals(하이, h3.하이);
    }
}
