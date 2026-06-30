package springbook.learningtest.spring.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewResolverTest extends AbstractDispatcherServletTest {

    @Test
    void view() throws Exception {
        setClasses(Config.class, HelloController.class);
        runService("/hello");
        assertEquals("/springbook/learningtest/web/hello.html", this.res.getForwardedUrl());

    }

    @org.springframework.stereotype.Controller

    static class HelloController implements Controller {

        @Override
        @RequestMapping("/hello")
        public @Nullable ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView("hello").addObject("message", "Hello Spring");
        }
    }

    @Configuration
    static class Config {
        @Bean
        public HandlerMapping handlerMapping() {
            return new RequestMappingHandlerMapping();
        }
        @Bean
        public ViewResolver viewResolver() {
            InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
            viewResolver.setPrefix("/springbook/learningtest/web/");
            viewResolver.setSuffix(".html");
            return viewResolver;
        }

    }
}
