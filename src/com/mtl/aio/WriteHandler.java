package com.mtl.aio;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * 说明:
 *
 * @作者 莫天龙
 * @时间 2019/12/17 11:11
 */
public class WriteHandler implements CompletionHandler<Integer,Session> {
    private ByteBuffer byteBuffer;

    public WriteHandler(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public void completed(Integer result, Session attachment) {
        if (byteBuffer.hasRemaining()){
            attachment.write(byteBuffer);
        }else{//写完回调
            Handler handler = attachment.getAioServer().getHandler();
            handler.onMessageWrited(attachment);
        }
    }

    @Override
    public void failed(Throwable exc, Session attachment) {
        Handler handler = attachment.getAioServer().getHandler();
        handler.onWriteThrowing(attachment, exc);
    }
}
