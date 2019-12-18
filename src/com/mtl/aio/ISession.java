package com.mtl.aio;

import java.nio.ByteBuffer;
/**
 * 说明:代表一次连接
 *
 * @作者 莫天龙
 * @时间 2019/12/16 11:05
 */
public interface ISession {

    /**
     * 写数据
     */
    public void write(ByteBuffer byteBuffer);

    /**
     * 关闭连接
     */
    public void close();

}
