package springbook.learningtest.spring.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import springbook.learningtest.spring.web.AbstractDispatcherServletTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleControllerTest extends AbstractDispatcherServletTest {
    @Test
    void helloController() throws Exception {
        setClasses(HelloController.class);
        initRequest("/hello").addParameter("name", "Spring");
        runService();
        assertModel("message", "Hello Spring");
        assertViewName("/hello.html");
    }

    @Test
    void noParameterHelloSimpleController() {
        setClasses(HelloController.class);
        initRequest("/hello");
        assertThrows(Exception.class, () -> runService());
    }

    @Test
    void helloControllerUnitTest()  {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Spring");
        Map<String, Object> model = new HashMap<>();
        new HelloController().control(params, model);
        assertEquals("Hello Spring", model.get("message"));
    }



    @Component("/hello")
    static class HelloController extends SimpleController {
        public HelloController() {
            setRequiredParams(new String[]{"name"});
            setViewName("/hello.html");
        }

        @Override
        public void control(Map<String, String> params, Map<String, Object> model) {
            model.put("message", "Hello " + params.get("name"));
        }
    }


    static abstract class SimpleController implements Controller {
        private String[] requiredParams;
        private String viewName;

        public void setRequiredParams(String[] requiredParams) {
            this.requiredParams = requiredParams;
        }

        public void setViewName(String viewName) {
            this.viewName = viewName;
        }

        @Override
        final public @Nullable ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
            if (viewName == null) throw new IllegalStateException();
            Map<String, String> params = new HashMap<>();
            for (String param: requiredParams) {
                String value = req.getParameter(param);
                if (value == null) throw new IllegalStateException();
                params.put(param, value);
            }
            Map<String, Object> model = new HashMap<>();
            control(params, model);
            return new ModelAndView(viewName, model);
        }

        public abstract void control(Map<String, String> params, Map<String, Object> model);
    }
}
