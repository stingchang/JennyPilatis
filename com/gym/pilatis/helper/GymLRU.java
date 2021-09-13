package com.gym.pilatis.helper;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.Duration;

/*
 * concurrency: concurrentHashmap + doublelyLinkedList
 */
public class GymLRU<K, V> {
    // TODO
    private final ConcurrentHashMap<K, Node> map;
    private final long liveTimeInMinutes;
    private final Node head;
    private final Node tail;

    private class Node {
        V value;
        Node prev;
        Node next;
        LocalTime lastUpdateTime;

        Node(V value) {
            this.value = value;
            this.lastUpdateTime = LocalTime.now();
        }
    }

    public GymLRU() {
        this(16, 30);
    }

    public GymLRU(int capacity, int day) {
        this(capacity, day, 0, 0);
    }

    public GymLRU(int capacity, int day, int hour, int minutes) {
        map = new ConcurrentHashMap<K, Node>() {
            private static final long serialVersionUID = 1L;

            @SuppressWarnings("unused")
            protected boolean removeEldestEntry(Map.Entry<K, Node> eldest) {
                return size() > capacity;
            }
        };

        this.head = new Node(null);
        this.tail = new Node(null);
        head.next = tail;
        tail.prev = head;

        this.liveTimeInMinutes = (day * 24 + hour) * 60 + minutes;
    }

    public V remove(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node node = map.get(key);
        Node prev = node.prev;
        Node next = node.next;
        V val = node.value;

        prev.next = next;
        next.prev = prev;
        map.remove(key);
        node = null;

        return val;
    }

    public void add(K key, V value) {
        Node node = new Node(value);
        node.prev = head;
        node.next = head.next;

        head.next = node;
        node.next.prev = node;
        map.put(key, node);

        trimFromTail();
    }

    public V get(K key) {
        if (!map.contains(key)) {
            return null;
        }
        V val = map.get(key).value;
        remove(key);
        add(key, val);
        return val;
    }

    public boolean containsKey(K key) {
        if (map.contains(key))
            return map.containsKey(key);
        return false;
    }

    private void trimFromTail() {

        new Thread(() -> {
            LocalTime lastNodeExpiredTime = tail.prev.lastUpdateTime.plusMinutes(liveTimeInMinutes);
            boolean expired = lastNodeExpiredTime.compareTo(LocalTime.now()) < 0;
            
            while (tail.prev != head && expired) {
                tail.prev = tail.prev.prev;
                tail.prev.next = tail;

                lastNodeExpiredTime = tail.prev.lastUpdateTime.plusMinutes(liveTimeInMinutes);
                expired = lastNodeExpiredTime.compareTo(LocalTime.now()) < 0;
            }
        }).run();
    }

    // update time if too old
    public V getOrDefault(K key, V defaultValue) {
        return map.containsKey(key) ? map.get(key).value : defaultValue;
    }
}
/*

this class should support log in member cache
For put operation, perform a deletion and then insert
put, 
 */

/*

ConcurrentHashMap<K,N> map
N
    K key, 
    V value,
    N next
    N prev

map.get, map.put
    map.remove
    map.put
    map.trim

map.trim -> different thread

 */
