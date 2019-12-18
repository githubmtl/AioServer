package com.mtl.aio;

import com.mtl.aio.exception.AioServerException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * 说明:代表一次连接
 *
 * @作者 莫天龙
 * @时间 2019/12/16 11:04
 */
public class Session extends AbstarctSession{

    public Session(AsynchronousServerSocketChannel serverSocketChannel, AsynchronousSocketChannel socketChannel) {
        super(serverSocketChannel, socketChannel);
    }


    /**
     * 同步写出
     * @param byteBuffer
     */
    @Override
    public void write(ByteBuffer byteBuffer) {
        AsynchronousSocketChannel socketChannel = getSocketChannel();
        Handler handler = getAioServer().getHandler();
        wLock.lock();
        try {
            socketChannel.write(byteBuffer).get();
            getAioServer().getExecutorService().execute(()->{
                handler.onMessageWrited(this);
            });
        } catch (Exception e) {
            getAioServer().getExecutorService().execute(()->{
                handler.onWriteThrowing(this,e);
            });
        }finally {
            wLock.unlock();
        }
    }

    @Override
    public void close() {
        try {
            getSocketChannel().close();
        } catch (IOException e) {
            new AioServerException("关闭socket异常！",e);
        }
    }

    @Override
    public String toString() {
        return "Session{} " + super.toString();
    }
}
