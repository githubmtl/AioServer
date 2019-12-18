package com.mtl.aio;

import com.mtl.aio.coder.MessageDecoder;
import com.mtl.aio.exception.AioServerException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 说明:读取异步回调
 *
 * @作者 莫天龙
 * @时间 2019/12/16 12:22
 */
public class ReadHandler implements CompletionHandler<Integer, Session> {

    private int length;

    private ByteBuffer byteBuffer;

    public Consumer<byte[]> consumer;

    /**
     *
     * @param length
     */
    public ReadHandler(int length,Consumer<byte[]> consumer) {
        this.length = length;
        this.consumer=consumer;
        byteBuffer = ByteBuffer.allocate(length);
    }

    @Override
    public void completed(Integer result, Session attachment) {
        AsynchronousSocketChannel socketChannel = attachment.getSocketChannel();
        AioServer aioServer = attachment.getAioServer();
        long readTimeOut = aioServer.getReadTimeOut();
        if (byteBuffer.position() < length) {//没读够，继续读取
            if (result==-1){
                attachment.close();
                return;
            }
            if (readTimeOut == -1) {
                socketChannel.read(byteBuffer, attachment, this);
            } else {
                socketChannel.read(byteBuffer, readTimeOut, TimeUnit.SECONDS, attachment, this);
            }
        } else if (byteBuffer.position()==length||result==-1){
            //继续读取下一条数据
            if (result!=-1){
                ReadHandler readHandler = new ReadHandler(length,consumer);
                if (readTimeOut==-1){
                    socketChannel.read(readHandler.byteBuffer, attachment, readHandler);
                }else{
                    socketChannel.read(readHandler.byteBuffer, readTimeOut, TimeUnit.SECONDS, attachment, readHandler);
                }
            }
            //读取完成
            byteBuffer.flip();
            //通过解码器，回调handler
            if (consumer==null){
                //调用解码器
                Object msg=null;
                List<MessageDecoder> decoders = aioServer.getDecoders();
                for(int i=0;i<decoders.size();i++){
                    MessageDecoder messageDecoder = decoders.get(i);
                    if (i==0){
                        msg=messageDecoder.decode(byteBuffer.array());
                    }else{
                        msg=messageDecoder.decode(msg);
                    }
                }
                Handler handler = aioServer.getHandler();
                final Object m=msg;
                aioServer.getExecutorService().execute(()->{
                    handler.onMessageReceived(attachment, m);
                });
            }else{
                consumer.accept(byteBuffer.array());
            }
        }
    }

    @Override
    public void failed(Throwable exc, Session attachment) {
        Handler handler = attachment.getAioServer().getHandler();
        attachment.getAioServer().getExecutorService().execute(()->{
            handler.onReadThrowing(attachment, exc);
        });
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

}
