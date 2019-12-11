package org.winterframework.beans.factory;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConstructorArgumentValues {

    private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>(0);

    private final List<ValueHolder> genericArgumentValues = new LinkedList<ValueHolder>();

    public Map<Integer, ValueHolder> getIndexedArgumentValues() {
        return indexedArgumentValues;
    }

    public List<ValueHolder> getGenericArgumentValues() {
        return genericArgumentValues;
    }

    public boolean hasIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.containsKey(index);
    }

    public ConstructorArgumentValues(ConstructorArgumentValues original) {
        addArgumentValues(original);
    }

    public ConstructorArgumentValues() {
    }

    public void addArgumentValues(ConstructorArgumentValues other) {
        if (other != null) {
            for (Map.Entry<Integer, ValueHolder> entry : other.indexedArgumentValues.entrySet()) {
                addIndexedArgumentValue(entry.getKey(), entry.getValue().copy());
            }
            for (ValueHolder valueHolder : other.genericArgumentValues) {
                if (!this.genericArgumentValues.contains(valueHolder)) {
                    addGenericArgumentValue(valueHolder.copy());
                }
            }
        }
    }

    public void addIndexedArgumentValue(int index, ValueHolder newValue) {
        this.indexedArgumentValues.put(index, newValue);
    }

    public void addGenericArgumentValue(Object value) {
        this.genericArgumentValues.add(new ValueHolder(value));
    }

    public static class ValueHolder {

        private Object value;

        private String type;

        private String name;

        private Object source;

        private boolean converted = false;

        private Object convertedValue;

        public ValueHolder(Object value) {
            this.value = value;
        }

        public ValueHolder(Object value, String type, String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }

        public boolean isConverted() {
            return converted;
        }

        public void setConverted(boolean converted) {
            this.converted = converted;
        }

        public Object getConvertedValue() {
            return convertedValue;
        }

        public void setConvertedValue(Object convertedValue) {
            this.convertedValue = convertedValue;
        }

        public ValueHolder copy() {
            ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
            copy.setSource(this.source);
            return copy;
        }
    }
}
