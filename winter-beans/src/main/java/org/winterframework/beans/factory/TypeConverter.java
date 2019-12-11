package org.winterframework.beans.factory;

import java.lang.reflect.Field;

public interface TypeConverter {
    <T> T convertIfNecessary(Object value, Class<T> requiredType);
}
