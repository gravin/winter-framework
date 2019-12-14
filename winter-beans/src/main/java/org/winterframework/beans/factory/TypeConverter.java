package org.winterframework.beans.factory;

import org.winterframework.core.MethodParameter;

import java.lang.reflect.Field;

public interface TypeConverter {
    <T> T convertIfNecessary(Object value, Class<T> requiredType);
    <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam);
//    <T> T convertIfNecessary(Object value, Class<T> requiredType, Field field);
}
