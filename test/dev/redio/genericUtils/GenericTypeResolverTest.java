package dev.redio.genericUtils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenericTypeResolverTest {

    static abstract class GenericClass<T> {
        public Class<T> clazz;
        public GenericClass() {
            clazz = new GenericTypeResolver<T>(getClass()){}.get();
        }
    }

    @Test
    public void testTypeResolution() {
        GenericClass<String> testClass = new GenericClass<>(){};
        assertEquals(String.class,testClass.clazz);
    }
}
