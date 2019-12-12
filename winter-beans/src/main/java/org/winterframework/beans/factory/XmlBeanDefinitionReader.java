package org.winterframework.beans.factory;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.winterframework.beans.PropertyValue;
import org.winterframework.core.io.ClassPathResource;
import org.winterframework.core.io.Resource;
import org.winterframework.core.io.support.PropertiesLoaderUtils;
import org.winterframework.util.CollectionUtils;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    public int loadBeanDefinitions(Resource resource) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                exception.printStackTrace();
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                exception.printStackTrace();
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                exception.printStackTrace();
            }
        });
        builder.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                System.out.println("publicId:" + publicId + " systemId:" + systemId);
                if (systemId != null) {
                    if (systemId.endsWith(".dtd")) {

                    } else if (systemId.endsWith(".xsd")) {
                        Properties mappings =
                                PropertiesLoaderUtils.loadAllProperties("META-INF/spring.schemas");
                        Map<String, String> schemaMappings = new ConcurrentHashMap<String, String>(mappings.size());
                        CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
                        String schemaLocation = schemaMappings.get(systemId);
                        System.out.println("enitity resover used schma location: " + schemaLocation);
                        Resource resource = new ClassPathResource(schemaLocation);
                        InputSource source = new InputSource(resource.getInputStream());
                        source.setPublicId(publicId);
                        source.setSystemId(systemId);
                        return source;
                    }
                }
                return null;
            }
        });
        Document doc = null;
        try {
            doc = builder.parse(new ClassPathResource("spring/beanFactoryTest.xml").getInputStream());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = doc.getDocumentElement();
        if (isDefaultNamespace(root.getNamespaceURI())) {
            System.out.println("parsing default namespace:" + root.getTagName());
            NodeList nl = root.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node node = nl.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    if (isDefaultNamespace(ele.getNamespaceURI())) {
                        System.out.println("parsing default namespace:" + ele.getTagName());
                        parseDefaultElement(ele);
                    } else {

                    }
                }
            }
        } else {

        }
        return 0;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

    }

    public boolean isDefaultNamespace(String namespaceUri) {
        return (StringUtils.isBlank(namespaceUri) || "http://www.springframework.org/schema/beans".equals(namespaceUri));
    }

    private void parseDefaultElement(Element ele) {
        if ("bean".equals(ele.getNodeName()) || "bean".equals(ele.getLocalName())) {
            processBeanDefinition(ele);
        }
    }

    private void processBeanDefinition(Element ele) {
        BeanDefinitionHolder definitionHolder = parseBeanDefinitionElement(ele, null);
        // Register bean definition under primary name.
        String beanName = definitionHolder.getBeanName();
        getRegistry().registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

        // Register aliases for bean name, if any.
        String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            for (String aliase : aliases) {
                getRegistry().registerAlias(beanName, aliase);
            }
        }
        System.out.println("------------------------解析BeanDefinition结果----------------------");
        System.out.println(JSON.toJSONString(definitionHolder, true));
        System.out.println("------------------------解析BeanDefinition结果----------------------");
    }

    private BeanDefinitionHolder parseBeanDefinitionElement(Element ele, BeanDefinition containingBean) {
        String id = ele.getAttribute("id");
        String nameAttr = ele.getAttribute("name");

        List<String> aliases = new ArrayList<String>();
        if (StringUtils.isNotBlank(nameAttr)) {
            String[] nameArr = StringUtils.split(nameAttr, "");
            aliases.addAll(Arrays.asList(nameArr));
        }

        String beanName = id;
        // id 没值的情况下，以name分割后的第一个值作为beanName，其它作为 alias
        if (StringUtils.isBlank(beanName) && !aliases.isEmpty()) {
            beanName = aliases.remove(0);
        }

        if (containingBean == null) {
            // 检查保证name,alias没有使用过
        }

        String className = null;
        if (ele.hasAttribute("class")) {
            className = ele.getAttribute("class").trim();
        }
        try {
            String parent = null;
            if (ele.hasAttribute("parent")) {
                parent = ele.getAttribute("parent");
            }

            // 创建
            GenericBeanDefinition bd = new GenericBeanDefinition();
            bd.setParentName(parent);
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(className);
            bd.setBeanClass(aClass);

            // 填充attributes
            if (ele.hasAttribute("scope")) {
                // Spring 2.x "scope" attribute
                bd.setScope(ele.getAttribute("scope"));
            } else if (ele.hasAttribute("singleton")) {
                // Spring 1.x "singleton" attribute
                bd.setScope("true".equals(ele.getAttribute("singleton")) ?
                        "singleton" : "prototype");
            }

            String lazyInit = ele.getAttribute("lazy-init");
            bd.setLazyInit("true".equals(lazyInit));


            // constructors
            parseConstructorArgElements(ele, bd);
            // properties
            parsePropertyElements(ele, bd);

            String[] aliasesArray = aliases.toArray(new String[aliases.size()]);

            return new BeanDefinitionHolder(bd, beanName, aliasesArray);
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
        }
        return null;
    }

    private void parseConstructorArgElements(Element ele, BeanDefinition bd) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (
                    (node instanceof Element && (isDefaultNamespace(node.getNamespaceURI()) || !isDefaultNamespace(node.getParentNode().getNamespaceURI())))
                            &&
                            ("constructor-arg".equals(node.getNodeName()) || "constructor-arg".equals(node.getLocalName()))
            ) {
                parseConstructorArgElement((Element) node, bd);
            }
        }
    }

    public void parsePropertyElements(Element beanEle, BeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (isCandidateElement(node) && nodeNameEquals(node, "property")) {
                parsePropertyElement((Element) node, bd);
            }
        }
    }

    /**
     * Parse a property element.
     */
    public void parsePropertyElement(Element ele, BeanDefinition bd) {
        String propertyName = ele.getAttribute("name");
        if (StringUtils.isBlank(propertyName)) {
            throw new RuntimeException("Tag 'property' must have a 'name' attribute");
        }
        try {
            if (bd.getPropertyValues().contains(propertyName)) {
                throw new RuntimeException("Multiple 'property' definitions for property '" + propertyName + "'");
            }
            Object val = parsePropertyValue(ele, bd, propertyName);
            PropertyValue pv = new PropertyValue(propertyName, val);
            bd.getPropertyValues().addPropertyValue(pv);
        } finally {

        }
    }

    private void parseConstructorArgElement(Element ele, BeanDefinition bd) {

        String indexAttr = ele.getAttribute("index");
        String typeAttr = ele.getAttribute("type");
        String nameAttr = ele.getAttribute("name");
        if (StringUtils.isNotBlank(indexAttr)) {
            try {
                int index = Integer.parseInt(indexAttr);
                if (index < 0) {
                    throw new RuntimeException("'index' cannot be lower than 0");
                } else {
                    try {
                        Object value = parsePropertyValue(ele, bd, null);
                        ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                        if (StringUtils.isNotBlank(typeAttr)) {
                            valueHolder.setType(typeAttr);
                        }
                        if (StringUtils.isNotBlank(nameAttr)) {
                            valueHolder.setName(nameAttr);
                        }
                        if (bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
                            throw new RuntimeException("Ambiguous constructor-arg entries for index " + index);
                        } else {
                            bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
                        }
                    } finally {
                    }
                }
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Attribute 'index' of tag 'constructor-arg' must be an integer");
            }
        } else {
            try {
                Object value = parsePropertyValue(ele, bd, null);
                ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                if (StringUtils.isNotBlank(typeAttr)) {
                    valueHolder.setType(typeAttr);
                }
                if (StringUtils.isNotBlank(nameAttr)) {
                    valueHolder.setName(nameAttr);
                }
                bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
            } finally {
            }
        }
    }

    private Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = (propertyName != null) ?
                "<property> element for property '" + propertyName + "'" :
                "<constructor-arg> element";
        // Should only have one child element: ref, value, list, etc.
        NodeList nl = ele.getChildNodes();
        Element subElement = null;
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && !nodeNameEquals(node, "description") &&
                    !nodeNameEquals(node, "meta")) {
                // Child element is what we're looking for.
                if (subElement != null) {
                    throw new RuntimeException(elementName + " must not contain more than one sub-element");
                } else {
                    subElement = (Element) node;
                }
            }
        }
        boolean hasRefAttribute = ele.hasAttribute("ref");
        boolean hasValueAttribute = ele.hasAttribute("value");
        if ((hasRefAttribute && hasValueAttribute) ||
                ((hasRefAttribute || hasValueAttribute) && subElement != null)) {
            throw new RuntimeException(elementName +
                    " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element");
        }

        if (hasRefAttribute) {
            String refName = ele.getAttribute("ref");
            if (StringUtils.isBlank(refName)) {
                throw new RuntimeException(elementName + " contains empty 'ref' attribute");
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            return ref;
        } else if (hasValueAttribute) {
            TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute("value"));
            return valueHolder;
        } else if (subElement != null) {
            return parsePropertySubElement(subElement, bd);
        } else {
            // Neither child element nor "ref" or "value" attribute found.
            throw new RuntimeException(elementName + " must specify a ref or value");
        }
    }

    private Object parsePropertySubElement(Element ele, BeanDefinition bd) {

        if (!isDefaultNamespace(ele.getNamespaceURI())) {

        } else if (nodeNameEquals(ele, "bean")) {

        } else if (nodeNameEquals(ele, "ref")) {
            // A generic reference to any name of any bean.
            String refName = ele.getAttribute("bean");
            boolean toParent = false;
            if (StringUtils.isBlank(refName)) {
                // A reference to the id of another bean in the same XML file.
                refName = ele.getAttribute("local");
                if (StringUtils.isBlank(refName)) {
                    // A reference to the id of another bean in a parent context.
                    refName = ele.getAttribute("parent");
                    toParent = true;
                    if (StringUtils.isNotBlank(refName)) {
                        throw new RuntimeException("'bean', 'local' or 'parent' is required for <ref> element");
                    }
                }
            }
            if (StringUtils.isBlank(refName)) {
                throw new RuntimeException("<ref> element contains empty target attribute");
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
            return ref;
        } else if (nodeNameEquals(ele, "idref")) {
        } else if (nodeNameEquals(ele, "value")) {
            return parseValueElement(ele);
        } else if (nodeNameEquals(ele, "null")) {
            // It's a distinguished null value. Let's wrap it in a TypedStringValue
            // object in order to preserve the source location.
            TypedStringValue nullHolder = new TypedStringValue(null);
            return nullHolder;
        } else if (nodeNameEquals(ele, "array")) {
        } else if (nodeNameEquals(ele, "list")) {
        } else if (nodeNameEquals(ele, "set")) {
        } else if (nodeNameEquals(ele, "map")) {
        } else if (nodeNameEquals(ele, "props")) {
        } else {
            throw new RuntimeException("Unknown property sub-element: [" + ele.getNodeName() + "]");
        }
        return null;
    }

    public Object parseValueElement(Element ele) {
        // It's a literal value.
        String value = getTextValue(ele);
        String specifiedTypeName = ele.getAttribute("type");
        String typeName = specifiedTypeName;
        TypedStringValue typedValue = new TypedStringValue(value);
        typedValue.setSpecifiedTypeName(specifiedTypeName);
        return typedValue;
    }

    public boolean nodeNameEquals(Node node, String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName());
    }

    private boolean isCandidateElement(Node node) {
        return (node instanceof Element && (isDefaultNamespace(node.getNamespaceURI()) || !isDefaultNamespace(node.getParentNode().getNamespaceURI())));
    }

    public String getTextValue(Element valueEle) {
        StringBuilder sb = new StringBuilder();
        NodeList nl = valueEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                sb.append(item.getNodeValue());
            }
        }
        return sb.toString();
    }
}
