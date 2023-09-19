package com.virtual.util.access;

public interface VDataHandle<T> {

    void offer(T data);

    T poll();

    void dataStatus(String str);
}
