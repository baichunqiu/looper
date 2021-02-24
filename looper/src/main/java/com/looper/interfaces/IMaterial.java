package com.looper.interfaces;

public interface IMaterial<M> {

    /**
     * 获取原料
     */
    M material();

    /**
     * 是否可用
     */
    boolean available();

    /**
     * 处理延迟时间
     */
    long delay();

}
