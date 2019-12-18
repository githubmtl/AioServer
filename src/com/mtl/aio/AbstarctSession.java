package com.mtl.aio;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 说明:
 *
 * @作者 莫天龙
 * @时间 2019/12/16 12:04
 */
public abstract class AbstarctSession implements ISession{
    private String id;
    private AsynchronousServerSocketChannel serverSocketChannel;
    protected Lock wLock=new ReentrantLock();
    private AsynchronousSocketChannel socketChannel;

    private AioServer aioServer;
    public AbstarctSession(AsynchronousServerSocketChannel serverSocketChannel, AsynchronousSocketChannel socketChannel) {
        this.serverSocketChannel = serverSocketChannel;
        this.socketChannel = socketChannel;
    }

    AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    void setServerSocketChannel(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    void setSocketChannel(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    AioServer getAioServer() {
        return aioServer;
    }

    void setAioServer(AioServer aioServer) {
        this.aioServer = aioServer;
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AbstarctSession{" +
                "id='" + id + '\'' +
                ", serverSocketChannel=" + serverSocketChannel +
                ", socketChannel=" + socketChannel +
                ", aioServer=" + aioServer +
                '}';
    }
}
