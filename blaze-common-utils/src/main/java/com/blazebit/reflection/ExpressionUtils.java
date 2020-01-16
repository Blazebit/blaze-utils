package com.blazebit.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ExpressionUtils {

    private static final ConcurrentMap<ExpressionUtils.PropertyPathExpressionKey, PropertyPathExpression<Object, Object>> cache = new ConcurrentHashMap<ExpressionUtils.PropertyPathExpressionKey, PropertyPathExpression<Object, Object>>();

	/* Without value class */

    public static <X, Y> PropertyPathExpression<X, Y> getExpression(
            Class<X> source, String propertyPath) {
        return getExpression(source, propertyPath, (Class<Y>) null);
    }

    public static <X, Y> PropertyPathExpressionValueHolder<X, Y> getValueHolder(
            Class<X> source, X target, String propertyPath) {
        return getValueHolder(source, target, propertyPath, (Class<Y>) null);
    }

    public static <X, Y> PropertyPathExpressionValueHolder<X, Y> getValueHolder(
        Class<X> source, X target, String propertyPath, boolean allowFieldAccess) {
        return getValueHolder(source, target, propertyPath, (Class<Y>) null, allowFieldAccess);
    }

	/* With value class */

    @SuppressWarnings("unchecked")
    public static <X, Y> PropertyPathExpression<X, Y> getExpression(
            Class<X> source, String propertyPath, Class<Y> valueClass) {
        return getExpression(source, propertyPath, valueClass, false);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> PropertyPathExpression<X, Y> getExpression(
        Class<X> source, String propertyPath, Class<Y> valueClass, boolean allowFieldAccess) {
        final PropertyPathExpressionKey key = new PropertyPathExpressionKey(
            source, propertyPath, allowFieldAccess);
        PropertyPathExpression<Object, Object> expression = cache.get(key);

        if (expression == null) {
            expression = new PropertyPathExpression<Object, Object>(
                    (Class<Object>) source, propertyPath, allowFieldAccess);
            final PropertyPathExpression<Object, Object> oldExpression = cache
                    .putIfAbsent(key, expression);

            if (oldExpression != null) {
                expression = oldExpression;
            }
        }

        return (PropertyPathExpression<X, Y>) expression;
    }

    public static <X, Y> PropertyPathExpressionValueHolder<X, Y> getValueHolder(
            Class<X> source, X target, String propertyPath, Class<Y> valueClass) {
        return new PropertyPathExpressionValueHolder<X, Y>(target, getExpression(source, propertyPath, valueClass));
    }

    public static <X, Y> PropertyPathExpressionValueHolder<X, Y> getValueHolder(
        Class<X> source, X target, String propertyPath, Class<Y> valueClass, boolean allowFieldAccess) {
        return new PropertyPathExpressionValueHolder<X, Y>(target, getExpression(source, propertyPath, valueClass, allowFieldAccess));
    }

	/* With source class */

    public static <X, Y> Y getValue(Class<X> source, X target,
                                    String propertyPath) {
        return getValue(source, target, propertyPath, (Class<Y>) null);
    }

    public static <X, Y> Y getNullSafeValue(Class<X> source, X target,
                                            String propertyPath) {
        return getNullSafeValue(source, target, propertyPath, (Class<Y>) null);
    }

    public static <X, Y> void setValue(Class<X> source, X target,
                                       String propertyPath, Y value) {
        getExpression(source, propertyPath).setValue(target, value);
    }

    public static <X, Y> Y getValue(Class<X> source, X target,
                                    String propertyPath, boolean allowFieldAccess) {
        return getValue(source, target, propertyPath, (Class<Y>) null, allowFieldAccess);
    }

    public static <X, Y> Y getNullSafeValue(Class<X> source, X target,
                                            String propertyPath, boolean allowFieldAccess) {
        return getNullSafeValue(source, target, propertyPath, (Class<Y>) null, allowFieldAccess);
    }

    public static <X, Y> void setValue(Class<X> source, X target,
                                       String propertyPath, Y value, boolean allowFieldAccess) {
        getExpression(source, propertyPath, null, allowFieldAccess).setValue(target, value);
    }

	/* With value class */

    public static <X, Y> Y getValue(Class<X> source, X target,
                                    String propertyPath, Class<Y> valueClass) {
        return getExpression(source, propertyPath, valueClass).getValue(target);
    }

    public static <X, Y> Y getNullSafeValue(Class<X> source, X target,
                                            String propertyPath, Class<Y> valueClass) {
        return getExpression(source, propertyPath, valueClass)
                .getNullSafeValue(target);
    }

    public static <X, Y> Y getValue(Class<X> source, X target,
                                    String propertyPath, Class<Y> valueClass, boolean allowFieldAccess) {
        return getExpression(source, propertyPath, valueClass, allowFieldAccess).getValue(target);
    }

    public static <X, Y> Y getNullSafeValue(Class<X> source, X target,
                                            String propertyPath, Class<Y> valueClass, boolean allowFieldAccess) {
        return getExpression(source, propertyPath, valueClass, allowFieldAccess)
            .getNullSafeValue(target);
    }

	/* Without source class */

    public static <X, Y> Y getValue(X target, String propertyPath) {
        return getValue(target, propertyPath, null);
    }

    public static <X, Y> Y getNullSafeValue(X target, String propertyPath) {
        return getNullSafeValue(target, propertyPath, null);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> void setValue(X target, String propertyPath, Y value) {
        getExpression((Class<X>) target.getClass(), propertyPath).setValue(
                target, value);
    }

    public static <X, Y> Y getValue(X target, String propertyPath, boolean allowFieldAccess) {
        return getValue(target, propertyPath, null, allowFieldAccess);
    }

    public static <X, Y> Y getNullSafeValue(X target, String propertyPath, boolean allowFieldAccess) {
        return getNullSafeValue(target, propertyPath, null, allowFieldAccess);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> void setValue(X target, String propertyPath, Y value, boolean allowFieldAccess) {
        getExpression((Class<X>) target.getClass(), propertyPath, null, allowFieldAccess).setValue(
            target, value);
    }

	/* With value class */

    @SuppressWarnings("unchecked")
    public static <X, Y> Y getValue(X target, String propertyPath,
                                    Class<Y> valueClass) {
        return getExpression((Class<X>) target.getClass(), propertyPath,
                valueClass).getValue(target);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> Y getNullSafeValue(X target, String propertyPath,
                                            Class<Y> valueClass) {
        return target == null ? null : getExpression(
                (Class<X>) target.getClass(), propertyPath, valueClass)
                .getNullSafeValue(target);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> Y getValue(X target, String propertyPath,
                                    Class<Y> valueClass, boolean allowFieldAccess) {
        return getExpression((Class<X>) target.getClass(), propertyPath,
                             valueClass, allowFieldAccess).getValue(target);
    }

    @SuppressWarnings("unchecked")
    public static <X, Y> Y getNullSafeValue(X target, String propertyPath,
                                            Class<Y> valueClass, boolean allowFieldAccess) {
        return target == null ? null : getExpression(
            (Class<X>) target.getClass(), propertyPath, valueClass, allowFieldAccess)
            .getNullSafeValue(target);
    }

    private static class PropertyPathExpressionKey {
        final Class<?> source;
        final String propertyPath;
        final boolean allowField;

        public PropertyPathExpressionKey(Class<?> source, String propertyPath, boolean allowField) {
            this.source = source;
            this.propertyPath = propertyPath;
            this.allowField = allowField;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((propertyPath == null) ? 0 : propertyPath.hashCode());
            result = prime * result
                    + ((source == null) ? 0 : source.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PropertyPathExpressionKey other = (PropertyPathExpressionKey) obj;
            if (propertyPath == null) {
                if (other.propertyPath != null)
                    return false;
            } else if (!propertyPath.equals(other.propertyPath))
                return false;
            if (source == null) {
                if (other.source != null)
                    return false;
            } else if (!source.equals(other.source))
                return false;
            return true;
        }
    }

    private ExpressionUtils() {
    }
}
