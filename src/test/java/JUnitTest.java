import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringJUnitConfig
public class JUnitTest {
    @Autowired ApplicationContext context;
    Set<JUnitTest> testObjects;
    ApplicationContext contextObject;

    @BeforeEach
    void setUp() {
        testObjects = new HashSet<>();
        contextObject = null;
    }

    @Test
    void test1() {
        assertTrue(!testObjects.contains(this));
        testObjects.add(this);
        assertTrue(contextObject == null || context == contextObject);
        contextObject = context;
    }

    @Test
    void test2() {
        assertTrue(!testObjects.contains(this));
        testObjects.add(this);
        assertTrue(contextObject == null || context == contextObject);
        contextObject = context;
    }

    @Test
    void test3() {
        assertTrue(!testObjects.contains(this));
        testObjects.add(this);
        assertEquals(contextObject, this.contextObject);
        contextObject = context;
    }
}
