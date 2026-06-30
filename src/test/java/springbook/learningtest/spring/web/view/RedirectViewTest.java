package springbook.learningtest.spring.web.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedirectViewTest extends AbstractDispatcherServletTest {
    @Test
    public void redirectView() throws ServletException, IOException {
        setClasses(HelloController.class, Config.class);
        runService("/hello");
        assertEquals("/main?name=Spring", this.res.getRedirectedUrl());
    }

    @org.springframework.stereotype.Controller
    public static class HelloController implements Controller {
        @RequestMapping("/hello")
        public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
//            return new ModelAndView(new RedirectView("/main", true)).addObject("name", "Spring");
			return new ModelAndView("redirect:/main").addObject("name", "Spring");
        }
    }

    @Configuration
    static class Config {
        @Bean
        public HandlerMapping handlerMapping() {
            return new RequestMappingHandlerMapping();
        }
    }
}
