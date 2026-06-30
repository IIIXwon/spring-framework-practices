package springbook.learningtest.spring.web.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ServletControllerTest extends AbstractDispatcherServletTest {
    @Autowired
    ApplicationContext ac;
    @Test
    void helloServletController() throws Exception {
        setClasses(SimpleServletHandlerAdapter.class, HelloServlet.class);
        initRequest("/hello").addParameter("name", "Spring");
        assertEquals("Hello Spring", runService().getContentAsString());
    }

    @Component("/hello")
    static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String name = req.getParameter("name");
            resp.getWriter().print("Hello " + name);
        }
    }
}
