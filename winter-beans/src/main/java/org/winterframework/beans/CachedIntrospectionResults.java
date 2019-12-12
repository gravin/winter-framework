package org.winterframework.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterframework.core.io.support.SpringFactoriesLoader;
import org.winterframework.util.ClassUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CachedIntrospectionResults {

    private static final Logger logger = LoggerFactory.getLogger(CachedIntrospectionResults.class);

    /**
     * Map keyed by class containing CachedIntrospectionResults.
     * Needs to be a WeakHashMap with WeakReferences as values to allow
     * for proper garbage collection in case of multiple class loaders.
     */
    static final Map<Class<?>, Object> classCache = new WeakHashMap<Class<?>, Object>();


    /** Stores the BeanInfoFactory instances */
    private static List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(
            BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());

    /**
     * Create CachedIntrospectionResults for the given bean class.
     * @param beanClass the bean class to analyze
     * @return the corresponding CachedIntrospectionResults
     * @throws BeansException in case of introspection failure
     */
    @SuppressWarnings("unchecked")
    static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
        CachedIntrospectionResults results;
        Object value;
        synchronized (classCache) {
            value = classCache.get(beanClass);
        }
        if (value instanceof Reference) {
            Reference<CachedIntrospectionResults> ref = (Reference<CachedIntrospectionResults>) value;
            results = ref.get();
        }
        else {
            results = (CachedIntrospectionResults) value;
        }
        if (results == null) {
            if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader())) {
                results = new CachedIntrospectionResults(beanClass);
                synchronized (classCache) {
                    classCache.put(beanClass, results);
                }
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
                }
                results = new CachedIntrospectionResults(beanClass);
                synchronized (classCache) {
                    classCache.put(beanClass, new WeakReference<CachedIntrospectionResults>(results));
                }
            }
        }
        return results;
    }

    /**
     * Create a new CachedIntrospectionResults instance for the given class.
     * @param beanClass the bean class to analyze
     * @throws BeansException in case of introspection failure
     */
    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }

            BeanInfo beanInfo = null;
            for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
                beanInfo = beanInfoFactory.getBeanInfo(beanClass);
                if (beanInfo != null) {
                    break;
                }
            }
            if (beanInfo == null) {
                // If none of the factories supported the class, fall back to the default
                beanInfo = (shouldIntrospectorIgnoreBeaninfoClasses ?
                        Introspector.getBeanInfo(beanClass, Introspector.IGNORE_ALL_BEANINFO) :
                        Introspector.getBeanInfo(beanClass));
            }
            this.beanInfo = beanInfo;

            // Only bother with flushFromCaches if the Introspector actually cached...
            if (!shouldIntrospectorIgnoreBeaninfoClasses) {
                // Immediately remove class from Introspector cache, to allow for proper
                // garbage collection on class loader shutdown - we cache it here anyway,
                // in a GC-friendly manner. In contrast to CachedIntrospectionResults,
                // Introspector does not use WeakReferences as values of its WeakHashMap!
                Class<?> classToFlush = beanClass;
                do {
                    Introspector.flushFromCaches(classToFlush);
                    classToFlush = classToFlush.getSuperclass();
                }
                while (classToFlush != null && classToFlush != Object.class);
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }
            this.propertyDescriptorCache = new LinkedHashMap<String, PropertyDescriptor>();

            // This call is slow so we do it once.
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (Class.class.equals(beanClass) &&
                        ("classLoader".equals(pd.getName()) ||  "protectionDomain".equals(pd.getName()))) {
                    // Ignore Class.getClassLoader() and getProtectionDomain() methods - nobody needs to bind to those
                    continue;
                }
                if (logger.isTraceEnabled()) {
                    logger.trace("Found bean property '" + pd.getName() + "'" +
                            (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") +
                            (pd.getPropertyEditorClass() != null ?
                                    "; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
                }
                pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                this.propertyDescriptorCache.put(pd.getName(), pd);
            }
        }
        catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }
}
