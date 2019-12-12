package org.winterframework.beans.factory;

import org.winterframework.beans.PropertyAccessor;
import org.winterframework.beans.PropertyEditorRegistry;

public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {
}
