package org.winterframework.beans.factory;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MutablePropertyValues implements PropertyValues {

    private final List<PropertyValue> propertyValueList;

    private Set<String> processedProperties;

    private volatile boolean converted = false;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<PropertyValue>(0);
    }

    public MutablePropertyValues(PropertyValues original) {
        if (original != null) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList<PropertyValue>(pvs.length);
            for (PropertyValue pv : pvs) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
        }
        else {
            this.propertyValueList = new ArrayList<PropertyValue>(0);
        }
    }

    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[this.propertyValueList.size()]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    public boolean contains(String propertyName) {
        return (getPropertyValue(propertyName) != null ||
                (this.processedProperties != null && this.processedProperties.contains(propertyName)));
    }

    @Override
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); i++) {
            PropertyValue currentPv = this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                // 同名覆盖
                this.propertyValueList.set(i, pv);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }

    public void setConverted() {
        this.converted = true;
    }

    public boolean isConverted() {
        return this.converted;
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }
}
