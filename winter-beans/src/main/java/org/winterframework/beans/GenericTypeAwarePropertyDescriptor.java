package org.winterframework.beans;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.winterframework.core.BridgeMethodResolver;
import org.winterframework.core.GenericTypeResolver;
import org.winterframework.core.MethodParameter;
import org.winterframework.util.ClassUtils;
import org.winterframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of the standard JavaBeans {@link PropertyDescriptor} class,
 * overriding {@code getPropertyType()} such that a generically declared
 * type variable will be resolved against the containing bean class.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
class GenericTypeAwarePropertyDescriptor extends PropertyDescriptor {

    private final Class<?> beanClass;

    private final Method readMethod;

    private final Method writeMethod;

    private final Class<?> propertyEditorClass;

    private volatile Set<Method> ambiguousWriteMethods;

    private Class<?> propertyType;

    private MethodParameter writeMethodParameter;


    public GenericTypeAwarePropertyDescriptor(Class<?> beanClass, String propertyName,
                                              Method readMethod, Method writeMethod, Class<?> propertyEditorClass)
            throws IntrospectionException {

        super(propertyName, null, null);
        this.beanClass = beanClass;
        this.propertyEditorClass = propertyEditorClass;

        Method readMethodToUse = BridgeMethodResolver.findBridgedMethod(readMethod);
        Method writeMethodToUse = BridgeMethodResolver.findBridgedMethod(writeMethod);
        if (writeMethodToUse == null && readMethodToUse != null) {
            // Fallback: Original JavaBeans introspection might not have found matching setter
            // method due to lack of bridge method resolution, in case of the getter using a
            // covariant return type whereas the setter is defined for the concrete property type.
            Method candidate = ClassUtils.getMethodIfAvailable(
                    this.beanClass, "set" + StringUtils.capitalize(getName()), (Class<?>[]) null);
            if (candidate != null && candidate.getParameterTypes().length == 1) {
                writeMethodToUse = candidate;
            }
        }
        this.readMethod = readMethodToUse;
        this.writeMethod = writeMethodToUse;

        if (this.writeMethod != null && this.readMethod == null) {
            // Write method not matched against read method: potentially ambiguous through
            // several overloaded variants, in which case an arbitrary winner has been chosen
            // by the JDK's JavaBeans Introspector...
            Set<Method> ambiguousCandidates = new HashSet<Method>();
            for (Method method : beanClass.getMethods()) {
                if (method.getName().equals(writeMethodToUse.getName()) &&
                        !method.equals(writeMethodToUse) && !method.isBridge()) {
                    ambiguousCandidates.add(method);
                }
            }
            if (!ambiguousCandidates.isEmpty()) {
                this.ambiguousWriteMethods = ambiguousCandidates;
            }
        }
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public Method getReadMethod() {
        return this.readMethod;
    }

    @Override
    public Method getWriteMethod() {
        return this.writeMethod;
    }

    public Method getWriteMethodForActualAccess() {
        Set<Method> ambiguousCandidates = this.ambiguousWriteMethods;
        if (ambiguousCandidates != null) {
            this.ambiguousWriteMethods = null;
            LoggerFactory.getLogger(GenericTypeAwarePropertyDescriptor.class).warn("Invalid JavaBean property '" +
                    getName() + "' being accessed! Ambiguous write methods found next to actually used [" +
                    this.writeMethod + "]: " + ambiguousCandidates);
        }
        return this.writeMethod;
    }

    @Override
    public Class<?> getPropertyEditorClass() {
        return this.propertyEditorClass;
    }

    @Override
    public synchronized Class<?> getPropertyType() {
        if (this.propertyType == null) {
            if (this.readMethod != null) {
                this.propertyType = GenericTypeResolver.resolveReturnType(this.readMethod, this.beanClass);
            }
            else {
                MethodParameter writeMethodParam = getWriteMethodParameter();
                if (writeMethodParam != null) {
                    this.propertyType = writeMethodParam.getParameterType();
                }
                else {
                    this.propertyType = super.getPropertyType();
                }
            }
        }
        return this.propertyType;
    }

    public synchronized MethodParameter getWriteMethodParameter() {
        if (this.writeMethod == null) {
            return null;
        }
        if (this.writeMethodParameter == null) {
            this.writeMethodParameter = new MethodParameter(this.writeMethod, 0);
            GenericTypeResolver.resolveParameterType(this.writeMethodParameter, this.beanClass);
        }
        return this.writeMethodParameter;
    }

}
