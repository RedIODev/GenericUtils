package dev.redio.genericUtils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenericTypeResolverTest {

    static abstract class GenericClass<T> {
        public Class<T> clazz;
        public GenericClass() {
            clazz = new GenericTypeResolver<T>(getClass()){}.get();
        }

        public Class<T> resolve() {
            return new GenericTypeResolver<T>(getClass()){}.get();
        }
    }

    @Test
    public void testConstructorTypeResolution() {
        GenericClass<String> testClass = new GenericClass<>(){};
        assertEquals(String.class,testClass.clazz);
    }

    @Test
    public void testInstanceTypeResolution() {
        GenericClass<String> testClass = new GenericClass<>() {};
        assertEquals(String.class,testClass.resolve());
    }

    static class NonGenericSubClass extends GenericClass<String> {}

    @Test
    public void testSubConstructorTypeResolution() {
        NonGenericSubClass testClass = new NonGenericSubClass();
        assertEquals(String.class, testClass.clazz);
    }

    @Test
    public void testSubInstanceTypeResolution() {
        NonGenericSubClass testClass = new NonGenericSubClass();
        assertEquals(String.class,testClass.resolve());
    }
}
