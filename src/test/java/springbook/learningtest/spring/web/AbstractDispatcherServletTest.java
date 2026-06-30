package springbook.learningtest.spring.web;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractDispatcherServletTest implements AfterRunService {
    protected MockHttpServletRequest req;
    protected MockHttpServletResponse res;
    protected MockServletConfig config = new MockServletConfig("spring");
    protected MockHttpSession session;

    private ConfigurableDispatcherServlet dispatcherServlet;
    private Class<?>[] classes;
    private String servletPath;

    public AbstractDispatcherServletTest setClasses(Class<?> ...classes) {
        this.classes = classes;
        return this;
    }

    public AbstractDispatcherServletTest setServletPath(String servletPath) {
        if(req == null)
            this.servletPath = servletPath;
        else
            req.setServletPath(servletPath);
        return this;
    }

    public AbstractDispatcherServletTest initRequest(String requestUri, String method) {
        req = new MockHttpServletRequest(method, requestUri);
        res = new MockHttpServletResponse();
        if (servletPath != null) setServletPath(servletPath);
        return this;
    }

    public AbstractDispatcherServletTest initRequest(String requestUri, RequestMethod method) {
        return initRequest(requestUri, method.toString());
    }

    public AbstractDispatcherServletTest initRequest(String requestUri) {
        return initRequest(requestUri, RequestMethod.GET);
    }

    public AbstractDispatcherServletTest addParameter(String name, String value) {
        if (req == null) throw new IllegalArgumentException("request가 초기화되지 않았습니다");
        req.addParameter(name,value);
        return this;
    }

    public AbstractDispatcherServletTest buildDispatcherServlet() throws ServletException {
        if (classes == null) throw new IllegalArgumentException("classes를 설정해야합니다");
        dispatcherServlet = new ConfigurableDispatcherServlet();
        dispatcherServlet.setClasses(classes);
        dispatcherServlet.init(config);
        return this;
    }

    public AfterRunService runService() throws ServletException, IOException {
        if (dispatcherServlet ==null) buildDispatcherServlet();
        if(req ==null) throw new IllegalArgumentException("request가 준비되지 않았습니다");
        dispatcherServlet.service(req, res);
        return this;
    }

    public AfterRunService runService(String requestUri) throws ServletException, IOException {
        initRequest(requestUri);
        runService();
        return this;
    }

    public AfterRunService runService(String requestUri, String method) throws ServletException, IOException {
        initRequest(requestUri, method);
        runService();
        return this;
    }

    public WebApplicationContext getContext() {
        if(dispatcherServlet == null) throw new IllegalArgumentException("DispatcherServlet이 준비되지 않았습니다");
        return dispatcherServlet.getWebApplicationContext();
    }

    public <T> T getBean(Class<T> beanType) {
        if(dispatcherServlet == null) throw new IllegalArgumentException("DispatcherServlet이 준비되지 않았습니다");
        return getContext().getBean(beanType);
    }

    public ModelAndView getModelAndView() {
        return dispatcherServlet.getModelAndView();
    }

    public AfterRunService assertModel(String name, Object value) {
        assertEquals(value, getModelAndView().getModel().get(name));
        return this;
    }

    public AfterRunService assertViewName(String viewName) {
        assertEquals(viewName, getModelAndView().getViewName());
        return this;
    }

    public String getContentAsString() throws UnsupportedEncodingException {
        return res.getContentAsString();
    }

    @AfterEach
    public void closeServletContext() {
        if (dispatcherServlet != null)
            ((ConfigurableApplicationContext)dispatcherServlet.getWebApplicationContext()).close();
    }
}
