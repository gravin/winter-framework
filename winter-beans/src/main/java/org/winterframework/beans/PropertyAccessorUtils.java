package org.winterframework.beans;

public abstract class PropertyAccessorUtils {

    /**
     * Determine the first nested property separator in the
     * given property path, ignoring dots in keys (like "map[my.key]").
     * @param propertyPath the property path to check
     * @return the index of the nested property separator, or -1 if none
     */
    public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, false);
    }

    /**
     * Determine the first nested property separator in the
     * given property path, ignoring dots in keys (like "map[my.key]").
     * @param propertyPath the property path to check
     * @return the index of the nested property separator, or -1 if none
     */
    public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
        return getNestedPropertySeparatorIndex(propertyPath, true);
    }


    private static int getNestedPropertySeparatorIndex(String propertyPath, boolean last) {
        boolean inKey = false;
        int length = propertyPath.length();
        int i = (last ? length - 1 : 0);
        while (last ? i >= 0 : i < length) {
            switch (propertyPath.charAt(i)) {
                case PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR:
                case PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR:
                    inKey = !inKey;
                    break;
                case PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR:
                    if (!inKey) {
                        return i;
                    }
            }
            if (last) {
                i--;
            }
            else {
                i++;
            }
        }
        return -1;
    }
}
