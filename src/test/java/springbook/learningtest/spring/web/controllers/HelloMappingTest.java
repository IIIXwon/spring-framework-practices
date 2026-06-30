package springbook.learningtest.spring.web.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import java.io.IOException;
import java.util.Properties;

public class HelloMappingTest extends AbstractDispatcherServletTest {
    @Test
    public void beanNameUrlHM() throws ServletException, IOException {
        setClasses(Config.class);
        runService("/hello").assertViewName("/hello.html");
        runService("/hello/world").assertViewName("/hello/world.html");
        runService("/multi/").assertViewName("/multi/*.html");
        runService("/multi/a").assertViewName("/multi/*.html");
//        runService("/root/sub").assertViewName("/root/*/sub.html");
        runService("/root/a/sub").assertViewName("/root/*/sub.html");
//        runService("/root/a/b/c/sub").assertViewName("/root/*/sub.html");
        runService("/s").assertViewName("/s*.html");
        runService("/s1234").assertViewName("/s*.html");
    }

    @Configuration
    static class Config {
        @Bean("/hello")
        public Controller1 controller5() {
            Controller1 controller = new Controller1();
            controller.setUrl("/hello");
            return controller;
        }

        @Bean("/hello/world")
        public Controller1 controller1() {
            Controller1 controller = new Controller1();
            controller.setUrl("/hello/world");
            return controller;
        }

        @Bean("/multi/*")
        public Controller1 controller2() {
            Controller1 controller = new Controller1();
            controller.setUrl("/multi/*");
            return controller;
        }

        @Bean("/root/*/sub")
//        @Bean("/root/**/sub")         // 최신버전 스프링 web 프로젝트에서는 **는 url맨 앞이나 끝에만 와야한다, 성능 이슈로 수정됨 spring 6부터
        public Controller1 controller3() {
            Controller1 controller = new Controller1();
            controller.setUrl("/root/*/sub");
            return controller;
        }

        @Bean("/s*")
        public Controller1 controller4() {
            Controller1 controller = new Controller1();
            controller.setUrl("/s*");
            return controller;
        }
    }

    static class Controller1 extends AbstractController {
        private String url;

        public void setUrl(String url) {
            this.url = url;
        }

        protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
            return new ModelAndView(url + ".html");
        }
    }

    @Test
    public void simpleUrlHM() throws ServletException, IOException {
        setClasses(Config3.class);
        runService("/hello").assertViewName("c1.html");
        runService("/multi/a").assertViewName("c2.html");
        runService("/deep/a/sub").assertViewName("c3.html");
    }

    @Configuration
    static class Config3 {
        @Bean
        public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
            SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
            Properties mappings = new Properties();
            mappings.setProperty("/hello", "c1");
            mappings.setProperty("multi/*", "c2");
            mappings.setProperty("/deep/*/sub", "c3");
            handlerMapping.setMappings(mappings);
            return handlerMapping;
        }

        @Bean("c1")
        public Controller1 controller1() {
            Controller1 controller1 = new Controller1();
            controller1.setUrl("c1");
            return controller1;
        }

        @Bean("c2")
        public Controller1 controller2() {
            Controller1 controller1 = new Controller1();
            controller1.setUrl("c2");
            return controller1;
        }

        @Bean("c3")
        public Controller1 controller3() {
            Controller1 controller1 = new Controller1();
            controller1.setUrl("c3");
            return controller1;
        }
    }

    @Test
    public void orderOfHM() throws ServletException, IOException {
        setClasses(Controller4.class, Controller5.class);
        runService("/hello").assertViewName("controller5.html");

        setClasses(BeanNameHM.class, AnnotationHM.class, Controller4.class, Controller5.class);
        buildDispatcherServlet();
//        runService("/hello").assertViewName("controller5.html");  // BeanNameUrlHandlerMapping
        runService("/hello").assertViewName("controller4.html");    // RequestMappingHandlerMapping
    }

    static class BeanNameHM extends BeanNameUrlHandlerMapping {
        public BeanNameHM() {
//            setOrder(1);     // BeanNameUrlHandlerMapping
            setOrder(2);    // RequestMappingHandlerMapping
        }
    }

    static class AnnotationHM extends RequestMappingHandlerMapping {
        public AnnotationHM() {
//            setOrder(2);  // BeanNameUrlHandlerMapping
            setOrder(1);    // RequestMappingHandlerMapping
        }
    }

    @org.springframework.stereotype.Controller
    @RequestMapping
    static class Controller4 {
        @RequestMapping("/hello")
        public String hello() {
            return "controller4.html";
        }
    }

    @Component("/hello")
    static class Controller5 implements Controller {
        public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
            return new ModelAndView("controller5.html");
        }
    }

    @Test
    public void controllerBeanNameHM() throws ServletException, IOException {
        setClasses(BeanNameUrlHandlerMapping.class, Controller2.class);
        runService("/hello").assertViewName("hello2.html");
    }

    @RequestMapping("hello")
    @Component("hello")
    static class Controller2 implements Controller {
        public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
            return new ModelAndView("hello2.html");
        }
    }

    @Test
    public void defaultHandler() throws ServletException, IOException {
        setClasses(Config2.class, DefaultHandler.class);
        runService("/dsalkfjalk").assertViewName("defaulthandler.html");
    }

    @Component("defaultHandler")
    static class DefaultHandler implements Controller {
        public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
            return new ModelAndView("defaulthandler.html");
        }
    }

    @Configuration
    static class Config2 {
        @Bean
        public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping(DefaultHandler controller) {
            BeanNameUrlHandlerMapping handlerMapping = new BeanNameUrlHandlerMapping();
            handlerMapping.setDefaultHandler(controller);
            return handlerMapping;
        }
    }


    @Test
    public void temp() throws ServletException, IOException {
        setClasses(AbcController.class);
        runService("/hello").assertViewName("hello.html");

    }

    @Component("/hello")
    static class AbcController implements Controller {
        public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
            return new ModelAndView("hello.html");
        }
    }

    static class BH extends BeanNameUrlHandlerMapping {

    }
}
