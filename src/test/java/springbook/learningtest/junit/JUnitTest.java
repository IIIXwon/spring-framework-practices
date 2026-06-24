package springbook.learningtest.junit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringJUnitConfig
public class JUnitTest {
    @Autowired ApplicationContext context;
    static Set<JUnitTest> testObjects = new HashSet<>();
    static ApplicationContext contextObject = null;


    @Test
    void test1() {
        assertAll(
                () -> assertTrue(!testObjects.contains(this)),
                () -> testObjects.add(this),
                () -> assertEquals(1, testObjects.size()),
                () -> assertTrue(contextObject == null || context == contextObject),
                () -> contextObject = context
        );
    }

    @Test
    void test2() {
        assertAll(
                () -> assertTrue(!testObjects.contains(this)),
                () -> testObjects.add(this),
                () -> assertEquals(2, testObjects.size()),
                () -> assertTrue(contextObject == null || context == contextObject),
                () -> contextObject = context
        );
    }
}
