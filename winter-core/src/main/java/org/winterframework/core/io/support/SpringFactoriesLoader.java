package org.winterframework.core.io.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterframework.OrderComparator;
import org.winterframework.core.io.UrlResource;
import org.winterframework.util.Assert;
import org.winterframework.util.ClassUtils;
import org.winterframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public abstract class SpringFactoriesLoader {


    /** The location to look for the factories. Can be present in multiple JAR files. */
    private static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

    private static final Logger logger = LoggerFactory.getLogger(SpringFactoriesLoader.class);

    /**
     * Load the factory implementations of the given type from the default location,
     * using the given class loader.
     * <p>The returned factories are ordered in accordance with the {@link OrderComparator}.
     * @param factoryClass the interface or abstract class representing the factory
     * @param classLoader the ClassLoader to use for loading (can be {@code null} to use the default)
     */
    public static <T> List<T> loadFactories(Class<T> factoryClass, ClassLoader classLoader) {
        Assert.notNull(factoryClass, "'factoryClass' must not be null");
        if (classLoader == null) {
            classLoader = SpringFactoriesLoader.class.getClassLoader();
        }
        List<String> factoryNames = loadFactoryNames(factoryClass, classLoader);
        if (logger.isTraceEnabled()) {
            logger.trace("Loaded [" + factoryClass.getName() + "] names: " + factoryNames);
        }
        List<T> result = new ArrayList<T>(factoryNames.size());
        for (String factoryName : factoryNames) {
            result.add(instantiateFactory(factoryName, factoryClass, classLoader));
        }
        OrderComparator.sort(result);
        return result;
    }

    public static List<String> loadFactoryNames(Class<?> factoryClass, ClassLoader classLoader) {
        String factoryClassName = factoryClass.getName();
        try {
            List<String> result = new ArrayList<String>();
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = PropertiesLoaderUtils.loadProperties(new UrlResource(url));
                String factoryClassNames = properties.getProperty(factoryClassName);
                result.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(factoryClassNames)));
            }
            return result;
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load [" + factoryClass.getName() +
                    "] factories from location [" + FACTORIES_RESOURCE_LOCATION + "]", ex);
        }
    }


    private static <T> T instantiateFactory(String instanceClassName, Class<T> factoryClass, ClassLoader classLoader) {
        try {
            Class<?> instanceClass = ClassUtils.forName(instanceClassName, classLoader);
            if (!factoryClass.isAssignableFrom(instanceClass)) {
                throw new IllegalArgumentException(
                        "Class [" + instanceClassName + "] is not assignable to [" + factoryClass.getName() + "]");
            }
            return (T) instanceClass.newInstance();
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("Cannot instantiate factory class: " + factoryClass.getName(), ex);
        }
    }
}
