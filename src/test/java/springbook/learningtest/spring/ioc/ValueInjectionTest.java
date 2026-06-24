package springbook.learningtest.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValueInjectionTest {
    @Test
    public void valueInjection() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanSP.class, ConfigSP.class, DatabasePropertyPlaceHolder.class);
        BeanSP bean = ac.getBean(BeanSP.class);
        assertEquals("Mac OS X", bean.name);
        assertEquals("Mac OS X", bean.osname);
        assertEquals("Spring", bean.username);
        assertEquals("Spring", bean.hello.name);
    }

    static class BeanSP {
        @Value("#{systemProperties['os.name']}")
        String name;
        @Value("${database.username}")
        String username;
        @Value("${os.name}")
        String osname;
        @Autowired
        Hello hello;
    }

    static class ConfigSP {
        @Bean
        public Hello hello(@Value("${database.username}") String username) {
            Hello hello = new Hello();
            hello.name = username;
            return hello;
        }
    }

    static class Hello {
        String name;
    }

    static class DatabasePropertyPlaceHolder extends PropertySourcesPlaceholderConfigurer {
        public DatabasePropertyPlaceHolder() {
            this.setLocation(new ClassPathResource("database.properties", getClass()));
        }
    }

    @Test
    public void importResource() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ConfigIR.class);
        BeanSP bean = ac.getBean(BeanSP.class);
        assertEquals("Mac OS X", bean.name);
        assertEquals("Spring", bean.username);
    }

    @ImportResource("/springbook/learningtest/spring/ioc/properties2.xml")
    @Configuration
    static class ConfigIR {
        @Bean
        public BeanSP beanSp() {
            return new BeanSP();
        }

        @Bean
        public Hello hello() {
            return new Hello();
        }
    }

    @Test
    public void propertyEditor() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanPE.class);
        BeanPE bean = ac.getBean(BeanPE.class);
        assertEquals(Charset.forName("UTF-8"), bean.charset);
        assertArrayEquals(new int[]{1, 2, 3}, bean.intarr);
        assertTrue(bean.file.exists());
        assertEquals(1.2, bean.rate);
        assertTrue(bean.flag);
    }

    static class BeanPE {
        @Value("UTF-8")
        Charset charset;
        @Value("1,2,3")
        int[] intarr;
        @Value("true")
        boolean flag;
        @Value("1.2")
        double rate;
        @Value("classpath:test-applicationContext.xml")
        File file;
    }

    @Test
    public void collectionInject() {
        ApplicationContext ac = new GenericXmlApplicationContext(new ClassPathResource("collection.xml", getClass()));
        BeanC bean = ac.getBean(BeanC.class);
        assertEquals(3, bean.nameList.size());
        assertEquals("Spring", bean.nameList.get(0));
        assertEquals("IoC", bean.nameList.get(1));
        assertEquals("DI", bean.nameList.get(2));

        assertEquals(3, bean.nameSet.size());

        assertEquals(30, bean.ages.get("Kim"));
        assertEquals(35, bean.ages.get("Lee"));
        assertEquals(40, bean.ages.get("Ahn"));

        assertEquals("Spring",(String) bean.settings.get("username"));
        assertEquals("Book",(String) bean.settings.get("password"));
    }

    static class BeanC {
        List<String> nameList;

        public void setNameList(List<String> names) {
            this.nameList = names;
        }

        Set<String> nameSet;

        public void setNameSet(Set<String> nameSet) {
            this.nameSet = nameSet;
        }

        Map<String, Integer> ages;

        public void setAges(Map<String, Integer> ages) {
            this.ages = ages;
        }

        Properties settings;

        public void setSettings(Properties settings) {
            this.settings = settings;
        }

        List beans;

        public void setBeans(List beans) {
            this.beans = beans;
        }
    }
}