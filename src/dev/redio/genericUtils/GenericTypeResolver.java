package dev.redio.genericUtils;

import dev.redio.genericUtils.exceptions.TypeResolutionException;
import dev.redio.genericUtils.exceptions.TypeResolverInitException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Tool to resolve the Class of a generic type parameter at runtime.
 *
 * @param <T> parameter to resolve.
 */
public abstract class GenericTypeResolver<T> implements Supplier<Class<T>> {

    private static final Map<TypeVariable<?>, Class<?>> TYPE_CACHE = new HashMap<>();

    private final Class<?> resolvedParameter;

    protected GenericTypeResolver(final Class<?> provider) throws TypeResolutionException, TypeResolverInitException {
        final Class<?> thisClass = getClass();
        if (!thisClass.isAnonymousClass() || !thisClass.getSuperclass().equals(GenericTypeResolver.class))
            throw new TypeResolverInitException(thisClass + " is not a direct anonymous instance of GenericTypeResolver.");

        final Type typeT = resolveGenericParameter();

        if (typeT instanceof Class<?> classT) {
            resolvedParameter = classT;
            return;
        }

        Class<?> classT = getClassFromCache(typeT);

        if (classT != null) {
            resolvedParameter = classT;
            return;
        }

        addTypeVariablesToCache(provider);

        classT = getClassFromCache(typeT);

        if (classT != null) {
            resolvedParameter = classT;
            return;
        }

        throw new TypeResolutionException(typeT);
    }

    /**
     * Returns the result of the type resolution.
     *
     * @return the resolved type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final Class<T> get() {
        return (Class<T>) resolvedParameter;
    }

    @Override
    public final boolean equals(final Object o) {
        if (o == null)
            return false;

        if (o instanceof GenericTypeResolver<?> resolver)
            return resolvedParameter.equals(resolver.resolvedParameter);
        return false;
    }

    @Override
    public final int hashCode() {
        return resolvedParameter.hashCode();
    }

    private Type resolveGenericParameter() throws TypeResolutionException {
        if (getClass().getGenericSuperclass() instanceof ParameterizedType parameterizedType)
            return parameterizedType.getActualTypeArguments()[0];
        throw new TypeResolutionException("Can't resolve parameter of non parameterized type.");
    }

    private static Class<?> getClassFromCache(final Type typeT) {
        if (typeT instanceof TypeVariable<?> typeVariable)
            return TYPE_CACHE.get(typeVariable);
        return null;
    }

    //
    // Matcher
    //

    private static void addTypeVariablesToCache(Class<?> provider) {
        TYPE_CACHE.putAll(matchTypeParameterAndClass(
                getParameterizedSuperTypes(provider), getSuperTypeVariables(provider)));
    }

    private static Map<TypeVariable<?>, Class<?>> matchTypeParameterAndClass(final List<ParameterizedType> container,
                                                                             final List<TypeVariable<?>> typeVariables) {
        final Map<TypeVariable<?>, Class<?>> result = new HashMap<>();

        for (ParameterizedType type : container) {
            for (TypeVariable<?> variable : typeVariables) {
                final int index = getParameterTypeIndex(type, variable);
                if (index == -1)
                    continue;
                result.put(variable, getParameterClassFromParameterizedType(type, index, container));
            }
        }
        return result;
    }

    private static int getParameterTypeIndex(final ParameterizedType container, final TypeVariable<?> typeVariable) {
        if (container.getRawType() instanceof Class<?> rawClass) {
            final TypeVariable<?>[] containerTypeVariables = rawClass.getTypeParameters();
            for (int i = 0; i < containerTypeVariables.length; i++)
                if (containerTypeVariables[i] == typeVariable)
                    return i;
        }

        return -1;
    }

    private static Class<?> getParameterClassFromParameterizedType(final ParameterizedType sourceType,
                                                                   final int index,
                                                                   final List<ParameterizedType> superTypes)
            throws IndexOutOfBoundsException, TypeResolutionException {
        final Type type = sourceType.getActualTypeArguments()[index];

        if (type instanceof Class<?> clazz)
            return clazz;

        if (type instanceof TypeVariable<?> typeVariable)
            return getParameterClassFromSuperTypes(typeVariable, superTypes);

        if (type instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() instanceof Class<?> clazz)
            return clazz;

        throw new TypeResolutionException(type);

    }

    private static Class<?> getParameterClassFromSuperTypes(final TypeVariable<?> typeVariable,
                                                            final List<ParameterizedType> container) {
        for (ParameterizedType type : container) {
            final int index = getParameterTypeIndex(type, typeVariable);
            if (index == -1)
                continue;
            return getParameterClassFromParameterizedType(type, index, container);
        }

        throw new TypeResolutionException(typeVariable);
    }

    private static List<ParameterizedType> getParameterizedSuperTypes(final Class<?> provider) {
        final List<ParameterizedType> result = new ArrayList<>();

        if (provider == null || Object.class.equals(provider))
            return result;

        final Type genericSuperClass = provider.getGenericSuperclass();

        if (genericSuperClass instanceof ParameterizedType parameterizedType)
            result.add(parameterizedType);

        for (Type genericSuperInterface : provider.getGenericInterfaces())
            if (genericSuperInterface instanceof ParameterizedType parameterizedType)
                result.add(parameterizedType);

        result.addAll(getParameterizedSuperTypes(provider.getSuperclass()));

        for (Class<?> superInterface : provider.getInterfaces())
            result.addAll(getParameterizedSuperTypes(superInterface));

        return result;
    }

    private static List<TypeVariable<?>> getSuperTypeVariables(final Class<?> provider) {
        final List<TypeVariable<?>> result = new ArrayList<>();

        if (provider == null || Object.class.equals(provider))
            return result;

        result.addAll(List.of(provider.getTypeParameters()));

        result.addAll(getSuperTypeVariables(provider.getSuperclass()));

        for (Class<?> superInterface : provider.getInterfaces())
            result.addAll(getSuperTypeVariables(superInterface));

        return result;
    }
}
