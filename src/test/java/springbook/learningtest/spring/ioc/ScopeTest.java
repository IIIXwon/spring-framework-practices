package springbook.learningtest.spring.ioc;

import jakarta.annotation.Resource;
import jakarta.inject.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScopeTest {
    @Test
    void singletonScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class, SingletonClientBean.class);

        Set<SingletonBean> bean = new HashSet<>();
        bean.add(ac.getBean(SingletonBean.class));
        bean.add(ac.getBean(SingletonBean.class));
        assertEquals(1, bean.size());

        bean.add(ac.getBean(SingletonClientBean.class).bean1);
        bean.add(ac.getBean(SingletonClientBean.class).bean2);
        assertEquals(1, bean.size());
    }

    static class SingletonBean {
    }

    static class SingletonClientBean {
        @Autowired
        SingletonBean bean1;
        @Autowired
        SingletonBean bean2;
    }

    @Test
    void prototypeScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, PrototypeClientBean.class);

        Set<PrototypeBean> bean = new HashSet<>();
        bean.add(ac.getBean(PrototypeBean.class));
        bean.add(ac.getBean(PrototypeBean.class));
        assertEquals(2, bean.size());

        bean.add(ac.getBean(PrototypeClientBean.class).bean1);
        bean.add(ac.getBean(PrototypeClientBean.class).bean2);
        assertEquals(4, bean.size());
    }

    @Component("prototypeBean22")
    @Scope("prototype")
    static class PrototypeBean {
    }

    static class PrototypeClientBean {
        @Autowired
        PrototypeBean bean1;
        @Autowired
        PrototypeBean bean2;
    }

    @Test
    void objectFactory() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ObjectFactoryConfig.class);
        ObjectFactory<PrototypeBean> factoryBean = ac.getBean("prototypeBeanFactory", ObjectFactory.class);

        Set<PrototypeBean> beans = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            beans.add(factoryBean.getObject());
            assertEquals(i, beans.size());
        }
    }

    @Configuration
    static class ObjectFactoryConfig {
        @Bean
        public ObjectFactoryCreatingFactoryBean prototypeBeanFactory() {
            ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
            factoryBean.setTargetBeanName("prototypeBean22");
            return factoryBean;
        }
    }

    @Test
    void serviceLocatorFactoryBean() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ServiceLocatorConfig.class);
        PrototypeBeanFactory factory = ac.getBean(PrototypeBeanFactory.class);

        Set<PrototypeBean> beans = new HashSet<>();
        for (int i = 1; i <= 10; i++) {
            beans.add(factory.getPrototypeBean());
            assertEquals(i, beans.size());
        }
    }

    interface PrototypeBeanFactory {
        PrototypeBean getPrototypeBean();
    }

    @Configuration
    static class ServiceLocatorConfig {
        @Bean
        public ServiceLocatorFactoryBean prototypeBeanFactory() {
            ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
            factoryBean.setServiceLocatorInterface(PrototypeBeanFactory.class);
            return factoryBean;
        }
    }

    @Test
    void providerTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ProviderClient.class);
        ProviderClient client = ac.getBean(ProviderClient.class);

        Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
        for (int i = 1; i <= 10; i++) {
            bean.add(client.prototypeBeanProvider.get());
            assertEquals(i, bean.size());
        }
    }

    static class ProviderClient {
        @Resource
        Provider<PrototypeBean> prototypeBeanProvider;
    }

    static class AnnotationConfigDispatcherServlet extends DispatcherServlet {
        private Class<?>[] classes;

        public AnnotationConfigDispatcherServlet(Class<?>... classes) {
            super();
            this.classes = classes;
        }

        @Override
        protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
            AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
                protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
                        throws BeansException, IOException {
                    AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
                    reader.register(classes);
                }
            };
            wac.setServletContext(getServletContext());
            wac.setServletConfig(getServletConfig());
            wac.refresh();
            return wac;
        }
    }

    MockHttpServletResponse response = new MockHttpServletResponse();
    @Test
    public void requestScope() throws Exception {
        MockServletConfig ctx = new MockServletConfig(new MockServletContext(), "spring");
        DispatcherServlet ds = new AnnotationConfigDispatcherServlet(HelloController.class, HelloService.class, RequestBean.class, BeanCounter.class);
        ds.init(new MockServletConfig());

        BeanCounter counter = ds.getWebApplicationContext().getBean(BeanCounter.class);
        ds.service(new MockHttpServletRequest("GET", "/hello"), response);
        assertEquals(2, counter.addCounter);
        assertEquals(1, counter.size());

        ds.service(new MockHttpServletRequest("GET", "/hello"), response);
        assertEquals(4, counter.addCounter);
        assertEquals(2, counter.size());
        for (String name : ((AbstractRefreshableWebApplicationContext)ds.getWebApplicationContext()).getBeanFactory().getRegisteredScopeNames())
            System.out.println(name);
    }
    @RequestMapping("/")
    @Controller
    static class HelloController {
        @Autowired HelloService helloService;
        @Autowired Provider<RequestBean> requestBeanProvider;
        @Autowired BeanCounter beanCounter;
        @RequestMapping("hello") public String hello() {
            beanCounter.addCounter++;
            beanCounter.add(requestBeanProvider.get());
            helloService.hello();
            return "";
        }
    }
    static class HelloService {
        @Autowired Provider<RequestBean> requestBeanProvider;
        @Autowired BeanCounter beanCounter;

        public void hello() {
            beanCounter.addCounter++;
            beanCounter.add(requestBeanProvider.get());
        }
    }

    @Scope(value = "request")
    static class RequestBean {
    }

    static class BeanCounter extends HashSet {
        int addCounter = 0;
    }
}
