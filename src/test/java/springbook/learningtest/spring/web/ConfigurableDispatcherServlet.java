package springbook.learningtest.spring.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

public class ConfigurableDispatcherServlet extends DispatcherServlet {
    private Class<?>[] classes;
    private ModelAndView modelAndView;

    public ConfigurableDispatcherServlet(Class<?>... classes) {
        this.classes = classes;
    }

    public void setClasses(Class<?> ...classes) {
        this.classes = classes;
    }

    public ModelAndView getModelAndView() {
        return modelAndView;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        modelAndView = null;
        super.service(req, res);
    }

    /*
        DispatcherServlet의 서블릿 컨텍스트를 생성하는 메소드를 오버라이드해서 테스트용 메타정보를 이용하는 서블릿 컨텍스트를 생성하게 했다
     */

    @Override
    protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
        AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
            @Override
            protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
                AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
                if (classes != null) {
                    reader.register(classes);
                }
            }
        };

        wac.setEnvironment(getEnvironment());
        wac.setParent(parent);
        wac.setConfigLocation(getContextConfigLocation());
        wac.refresh();

        return wac;
    }

    /*
        뷰를 실행하는 과정을 가로채서 컨트롤러가 돌려준 ModelAndView 정보를 따로 저장해둔다.
        테스트에서 HttpServletResponse를 확인하는 대신 컨트롤러가 리런한 ModelAndView를 검증할 수 있게 해준다
     */
    @Override
    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        modelAndView = mv;
        super.render(mv, request, response);
    }
}
