package springbook.learningtest.spring.web.hello;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;
import springbook.learningtest.spring.web.ConfigurableDispatcherServlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloControllerTest {
    @Test
    void helloController() throws Exception {
        ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();
        servlet.setClasses(Config.class);
        servlet.init(new MockServletConfig("spring"));

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
        req.addParameter("name", "Spring");
        MockHttpServletResponse res = new MockHttpServletResponse();

        servlet.service(req, res);

        ModelAndView mav = servlet.getModelAndView();
        assertEquals("/hello.html", mav.getViewName());
        assertEquals("Hello Spring", (String) mav.getModel().get("message"));
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
