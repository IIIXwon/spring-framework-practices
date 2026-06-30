package springbook.learningtest.spring.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

public class SimpleAnnotationControllerTest extends AbstractDispatcherServletTest {
    @Test
    void helloController() throws Exception {
        setClasses(SimpleControllerTest.HelloController.class);
        initRequest("/hello").addParameter("name", "Spring");
        runService();
        assertModel("message", "Hello Spring");
        assertViewName("/hello.html");
    }

    @Controller
    static class HelloController {
        @RequestMapping("/hello")
        public String hello(@RequestParam("name") String name, ModelMap map) {
            map.put("message", "Hello " + name);
            return "/hello.html";
        }
    }
}
