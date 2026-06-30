package springbook.learningtest.spring.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import static org.junit.jupiter.api.Assertions.*;

public class InterceptorTest extends AbstractDispatcherServletTest {
    @Test
    void preHandlerReturnValue() throws Exception {
        setClasses(InterceptorConfig.class, Controller1.class);
        runService("/hello").assertViewName("hello.html");
        assertEquals(getBean(Controller1.class), getBean(Interceptor1.class).handler);
    }

    @Component("/hello")
    static class Controller1 implements Controller {

        @Override
        public @Nullable ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            return new ModelAndView("hello.html");
        }
    }

    static class Interceptor1 implements HandlerInterceptor {
        Object handler;
        boolean ret = true;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            this.handler = handler;
            return ret;
        }
    }

    @Configuration
    static class InterceptorConfig {
        @Bean
        public HandlerMapping beanNamerUrlHM() {
            BeanNameUrlHandlerMapping handlerMapping = new BeanNameUrlHandlerMapping();
            handlerMapping.setInterceptors(interceptor1());
            return handlerMapping;
        }

        @Bean
        public HandlerInterceptor interceptor1() {
            return new Interceptor1();
        }
    }

    @Test
    void postHandle() throws Exception {
        setClasses(InterceptorConfig2.class, Controller1.class);
        runService("/hello").assertViewName("hello.html");
        assertTrue(getBean(Interceptor2.class).post);

        getBean(Interceptor2.class).ret = false;
        getBean(Interceptor2.class).post = false;
        assertFalse(getBean(Interceptor2.class).post);
    }

    @Configuration
    static class InterceptorConfig2 {
        @Bean
        public HandlerMapping handlerMapping() {
            BeanNameUrlHandlerMapping handlerMapping = new BeanNameUrlHandlerMapping();
            handlerMapping.setInterceptors(interceptor2());
            return handlerMapping;
        }

        @Bean
        public HandlerInterceptor interceptor2() {
            return new Interceptor2();
        }
    }

    static class Interceptor2 implements HandlerInterceptor {
        boolean post;
        boolean ret = true;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            return ret;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
            post = true;
        }
    }
}
