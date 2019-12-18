package com.mtl.aio;

import com.mtl.aio.coder.MessageDecoder;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 说明:
 *
 * @作者 莫天龙
 * @时间 2019/12/17 10:28
 */
public class LengthFieldReadHandler implements CompletionHandler<Integer,Session> {

    private ByteBuffer headBuffer;

    private boolean headFlag=false;

    private ByteBuffer bodyBuffer;

    private int headLength;

    private int bodyLength;

    public LengthFieldReadHandler(int headLength) {
        this.headLength = headLength;
        headBuffer=ByteBuffer.allocate(headLength);
    }

    @Override
    public void completed(Integer result, Session attachment) {
        AsynchronousSocketChannel socketChannel = attachment.getSocketChannel();
        long readTimeOut = attachment.getAioServer().getReadTimeOut();
        if (headBuffer.position()<headLength&&!headFlag){//报文头没读取完
            if (result==-1){
                attachment.close();
                return;
            }
            if (readTimeOut==-1){
                socketChannel.read(headBuffer,attachment,this);
            }else{
                socketChannel.read(headBuffer,readTimeOut, TimeUnit.SECONDS,attachment,this);
            }
        }else{//报文头已读完
            if (!headFlag){
                headFlag=true;
                headBuffer.flip();
                bodyLength=headBuffer.getInt();
                bodyBuffer=ByteBuffer.allocate(bodyLength);
            }
            if (bodyBuffer.position()<bodyLength){//报文体没读完
                if (result==-1){
                    attachment.close();
                    return ;
                }
                if (readTimeOut==-1){
                    socketChannel.read(bodyBuffer,attachment,this);
                }else{
                    socketChannel.read(bodyBuffer,readTimeOut, TimeUnit.SECONDS,attachment,this);
                }
            }else{//报文体读完
                //接着读
                if (result!=-1){
                    LengthFieldReadStrategy readStrategy =(LengthFieldReadStrategy) attachment.getAioServer().getReadStrategy();
                    LengthFieldReadHandler readHandler=new LengthFieldReadHandler(readStrategy.getHeadLength());
                    if (readTimeOut==-1){
                        socketChannel.read(readHandler.headBuffer,attachment,readHandler);
                    }else{
                        socketChannel.read(readHandler.headBuffer,readTimeOut, TimeUnit.SECONDS,attachment,readHandler);
                    }
                }
                //处理读取的消息
                bodyBuffer.flip();
                byte[] array = bodyBuffer.array();
                AioServer aioServer = attachment.getAioServer();
                //调用解码器
                Object msg=null;
                List<MessageDecoder> decoders = aioServer.getDecoders();
                for(int i=0;i<decoders.size();i++){
                    MessageDecoder messageDecoder = decoders.get(i);
                    if (i==0){
                        msg=messageDecoder.decode(array);
                    }else{
                        msg=messageDecoder.decode(msg);
                    }
                }
                Handler handler = aioServer.getHandler();
                final Object m=msg;
                aioServer.getExecutorService().execute(()->{
                    handler.onMessageReceived(attachment, m);
                });
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

    public ByteBuffer getHeadBuffer() {
        return headBuffer;
    }

    public ByteBuffer getBodyBuffer() {
        return bodyBuffer;
    }

    public int getHeadLength() {
        return headLength;
    }
}
