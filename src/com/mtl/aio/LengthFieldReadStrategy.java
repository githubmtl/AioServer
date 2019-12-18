package com.mtl.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * 说明:基于{报文长度}{报文内容}协议读取
 *
 * @作者 莫天龙
 * @时间 2019/12/17 9:12
 */
public class LengthFieldReadStrategy implements ReadStrategy{
    private int headLength;
    private LengthType lengthType;

    public LengthFieldReadStrategy(LengthType lengthType) {
        this.lengthType = lengthType;
        headLength=lengthType.getLength();
    }

    @Override
    public void read(final Session session) {
        AsynchronousSocketChannel socketChannel = session.getSocketChannel();
        long readTimeOut = session.getAioServer().getReadTimeOut();
        LengthFieldReadHandler readHandler=new LengthFieldReadHandler(headLength);
        if (readTimeOut==-1){
            socketChannel.read(readHandler.getHeadBuffer(),session,readHandler);
        }else{
            socketChannel.read(readHandler.getHeadBuffer(),readTimeOut, TimeUnit.SECONDS,session,readHandler);
        }
    }

    public static enum LengthType{
        SHORT(2),INT(4);
        private int length;
        private LengthType(int length) {
            this.length = length;
        }
        public int getLength(){
            return length;
        }
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public LengthType getLengthType() {
        return lengthType;
    }

    public void setLengthType(LengthType lengthType) {
        this.lengthType = lengthType;
    }
}
