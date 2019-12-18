package com.mtl.aio.coder;

/**
 * 说明:消息编码器
 *
 * @作者 莫天龙
 * @时间 2019/12/16 10:57
 */
public interface MessageEncoder <I,O> {
    /**
     * 编码
     * @param message
     * @return
     */
    public O encode(I message);
}
