package com.looper;

public interface IMaterial<T> {

    /**
     * 获取原料
     *
     * @return
     */
    <T> T getMaterial();

    /**
     * 获取原料处理延迟时间
     *
     * @return
     */
    long getDelay();


    /**
     * 是否可用
     *
     * @return
     */
    boolean available();

    void countPlus();
}
