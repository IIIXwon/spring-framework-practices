package springbook.learningtest.spring.ioc;

import jakarta.inject.Named;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import springbook.learningtest.spring.ioc.bean.Hello;
import springbook.learningtest.spring.ioc.bean.Printer;
import springbook.learningtest.spring.ioc.bean.StringPrinter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SimpleAtBeanTest {
    @Test
    public void simpleAtBean() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(HelloService.class);
        Hello h1 = ac.getBean("hello", Hello.class);
        Hello h2 = ac.getBean("hello2", Hello.class);
        assertNotNull(h1.getPrinter());
        assertEquals(h1.getPrinter(), h2.getPrinter());

        HelloService hs = ac.getBean("service", HelloService.class);
        assertNotNull(hs);
    }

    @Named("service")
    static class HelloService {
        private Printer printer;

        @Autowired
        public void setPrinter(Printer printer) {
            this.printer = printer;
        }

        @Bean
        private Hello hello() {
            Hello hello = new Hello();
            hello.setName("Spring");
//            hello.setPrinter(this.printer);
            hello.setPrinter(printer());
            return hello;
        }

        @Bean
        private Hello hello2() {
            Hello hello = new Hello();
            hello.setName("Spring2");
//            hello.setPrinter(this.printer);
            hello.setPrinter(printer());
            return hello;
        }

        @Bean
        private Printer printer() {
            return new StringPrinter();
        }
    }
}
