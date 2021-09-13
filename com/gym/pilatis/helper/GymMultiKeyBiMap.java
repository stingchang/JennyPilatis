package com.gym.pilatis.helper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GymMultiKeyBiMap<K, V> {

    /*
     * multiple sessions map to one memberid
     */

    private ConcurrentHashMap<K, V> keyToVal = new ConcurrentHashMap<>();
    private ConcurrentHashMap<V, Set<K>> valToKeySet = new ConcurrentHashMap<>();

    public V put(K key, V value) {
        if (key == null || value == null) {
            return null;
        }
        keyToVal.put(key, value);

        Set<K> keySet = valToKeySet.get(value);
        if (keySet == null) {
            keySet = new HashSet<>();
            valToKeySet.put(value, keySet);
        }
        keySet.add(key);
        return value;
    }

    public boolean containsKeyAndVal(K key, V value) {
        return key != null && value != null && keyToVal.containsKey(key) && valToKeySet.containsKey(value);
    }

    public boolean containsKey(K key) {
        return keyToVal.containsKey(key);
    }

    public boolean containsValue(V val) {
        return valToKeySet.containsKey(val);
    }

    public V getValue(K key) {
        if (keyToVal.containsKey(key)) {
            return keyToVal.get(key);
        }
        return null;
    }

    public Set<K> getKeySet(V value) {
        if (valToKeySet.containsKey(value)) {
            return valToKeySet.get(value);
        }
        return null;
    }

    public V deleteKey(K key) {
        try {
            if (key == null) {
                return null;
            }
            V val = keyToVal.remove(key);
            Set<K> keySet = valToKeySet.get(val);
            if (keySet.contains(key)) {
                if (keySet.size() == 1) {
                    valToKeySet.remove(val);
                } else {
                    keySet.remove(key);
                }
            }

            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<K> deleteValue(V value) {
        try {
            if (value == null) {
                return null;
            }
            Set<K> keySet = valToKeySet.get(value);
            for (K key : keySet) {
                keyToVal.remove(key);
            }
            valToKeySet.remove(value);
            return keySet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
