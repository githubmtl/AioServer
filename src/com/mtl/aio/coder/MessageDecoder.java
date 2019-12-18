package com.mtl.aio.coder;

/**
 * 说明:消息解码器
 *
 * @作者 莫天龙
 * @时间 2019/12/16 10:59
 */
public interface MessageDecoder<I,O> {
    /**
     * 消息解码
     * @param message
     * @return
     */
    public O decode(I message);
}
