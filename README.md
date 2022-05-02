# GenericUtils
  A small library that includes tools to improve working with generics in Java at runtime.<p>
  Currently the sole feature of the library is the resolution of generic parameters at runtime.<p>
## Runtime generic resolution
### What it does
  It allows you to get type information about a generic type parameter from within a generic class instance.
### How to use it
  You can retrieve the class instance of the generic parameter by simply creating a new instance of GenericTypeResolver<T> with your generic type parameter and call the get method on it.
  ```
    public class GenericClass<T> {
      public GenericClass() {
        Class<?> typeT = new GenericTypeResolver<T>(){}.get(); 
      }

      public void instanceMethod() {
        Class<?> typeT = new GenericTypeResolver<T>(){}.get();
      }
    }
  ```
### Limitations
  The tool is limited by Javas [type erasure](https://www.baeldung.com/java-type-erasure). It can only be applied in specific cases.
#### Case one
  The generic type is an abstract class, and the generic type is resolved while inheriting from the class.
  ```
    abstract class GenericSuperClass<T> {}
    class NonGenericSubClass extends GenericSuperClass<String> {}
  ```
#### Case two
  The generic type is an abstract class, and the inheriting type is also abstract
  ```
    abstract class GenericSuperClass<T> {}
    abstract class GenericSubClass<T> extends GenericSuperClass<T> {}
  ```
#### Case three
  The generic type is an interface, and the generic type is resolved while implementing the interface.
  ```
    interface GenericInterface<T> {}
    class NonGenericSubClass implements GenericInterface<String> {}
  ```
#### Case four/(five)
 The generic type is an interface, and the subtypes are abstract/interfaces.
 ```
    interface GenericInterface<T> {}
    interface GenericSubInterface<T> extends GenericInterface<T> {}
    abstract class GenericSubClass<T> implements GenericInterface<T> {}
 ```
 The tool does not work on generically used types like List<T>. In that case you would need to make an anonymous inner class every time you use it.
 The tool also doesn't work on generic method parameters.
### Why is this needed?
  If you work with Java for some time, you might have run into the problem that you wanted to check something about a generic type at runtime.
  If you research about that topic you will likely stumble about the term of [type erasure](https://www.baeldung.com/java-type-erasure). 
  In short Java does **_not_** retain generic types at runtime.
#### Example
  ```
    public class GenericExample<T> {
      T field;
    }
  ```
  is compiled to
  ```
    public class GenericExample {
      Object field;
    }
  ```
  Now it should be clear where the problem is to check type information at runtime, it just isn't there anymore.
### How it works
  It works by using the fact that the generic type is compile time known if the type is resolved during inheritance or the final type is an anonymous inner class. 
  The tool traverses the inheritance hierarchy of the generic class/interface, finds the point of resolution, and extracts the class instance of the requested type.
  That means that the tool is only really useful for libraries where the final resolution of the generic type is on the user side.
### Performance 
  As you might have guessed this process is relatively costly when it comes to performance. 
  In order to increase performance, the tool is caching every resolved variable and its actual type.
