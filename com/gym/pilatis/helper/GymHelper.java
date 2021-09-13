package com.gym.pilatis.helper;

public class GymHelper {
    /* General helper function */
    public static final boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static final boolean isNonEmpty(String string) {
        return !isEmpty(string);
    }

    public static final boolean  isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
    public static final boolean  isNonEmpty(Object[] array) {
        return array != null && array.length > 0;
    }
    
    public static final String getOrDefault(String string, String defaultString) {
        defaultString = isNonEmpty(defaultString) ? defaultString : "";
        return isEmpty(string) ? defaultString : string;
    }
}
