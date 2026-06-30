package springbook.learningtest.spring.web.hello;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleHelloTest extends AbstractDispatcherServletTest {
    @Test
    void helloController() throws Exception {
        ModelAndView mav = setClasses(Config.class)
                .initRequest("/hello", RequestMethod.GET)
                .addParameter("name", "Spring")
                .runService()
                .getModelAndView();
        assertEquals("/hello.html", mav.getViewName());
        assertEquals("Hello Spring", mav.getModel().get("message"));

        setClasses(Config.class)
                .initRequest("/hello", RequestMethod.GET)
                .addParameter("name", "Spring")
                .runService()
                .assertModel("message", "Hello Spring")
                .assertViewName("/hello.html");
    }

    @Configuration
    static class Config {
        @Bean
        public HelloSpring helloSpring() {
            return new HelloSpring();
        }

        @Bean(name = "/hello")
        public HelloController helloController() {
            return new HelloController();
        }
    }
}
