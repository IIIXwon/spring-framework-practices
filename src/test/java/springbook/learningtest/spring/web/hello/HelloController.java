package springbook.learningtest.spring.web.hello;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashMap;
import java.util.Map;


public class HelloController implements Controller {
    @Autowired
    HelloSpring helloSpring;
    @Override
    public @Nullable ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String name = request.getParameter("name");
        String message = helloSpring.sayHello(name);

        Map<String, Object> model = new HashMap<>();
        model.put("message", message);
        return new ModelAndView("/hello.html", model);
    }

}
