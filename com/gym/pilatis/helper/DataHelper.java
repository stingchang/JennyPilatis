package com.gym.pilatis.helper;

public class DataHelper {

    public static final DataHelper INSTANCE = new DataHelper();

    private DataHelper() {
    }

    private static final String SALT = "some_text_";

    public static final String encode(String key) {
        // code(salt + time + key)
        return (SALT+key);
    }

    public static final String encode(String key, int length) {
        // code(salt + time + key)
        return encode(key).substring(0, length);
    }

    public static final String decode(String key) {
        // code(salt + time + key)
        return key;
    }

}
