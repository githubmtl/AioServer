package com.mtl.aio;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * 说明:读取策略，如定长读取，报文长度+报文体读取
 *
 * @作者 莫天龙
 * @时间 2019/12/16 10:44
 */
public interface ReadStrategy {
    public void read(Session session);
}
