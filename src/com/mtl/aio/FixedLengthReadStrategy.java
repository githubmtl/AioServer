package com.mtl.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 说明:定长报文读取
 *
 * @作者 莫天龙
 * @时间 2019/12/16 12:20
 */
public class FixedLengthReadStrategy implements ReadStrategy {
    private int length;

    public FixedLengthReadStrategy(int length) {
        this.length = length;
    }

    @Override
    public void read(Session session) {
        AsynchronousSocketChannel socketChannel = session.getSocketChannel();
        ReadHandler readHandler=new ReadHandler(length,null);
        long readTimeOut = session.getAioServer().getReadTimeOut();
        if (readTimeOut==-1){
            socketChannel.read(readHandler.getByteBuffer(),session,readHandler);
        }else{
            socketChannel.read(readHandler.getByteBuffer(),readTimeOut, TimeUnit.SECONDS,session,readHandler);
        }
    }
}
