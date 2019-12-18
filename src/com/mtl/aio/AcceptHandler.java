package com.mtl.aio;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;

/**
 *
 * @作者 莫天龙
 * @时间 2019/12/16 15:27
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,Session> {
    @Override
    public void completed(AsynchronousSocketChannel result, Session attachment) {
        AioServer aioServer = attachment.getAioServer();
        aioServer.start();
        attachment.setSocketChannel(result);
        Handler handler = aioServer.getHandler();
        aioServer.addClient(attachment);
        aioServer.getExecutorService().execute(()->{
            handler.onConnected(attachment);
        });
        //开始读取数据
        aioServer.getReadStrategy().read(attachment);
    }

    @Override
    public void failed(Throwable exc, Session attachment) {
        Handler handler = attachment.getAioServer().getHandler();
        attachment.getAioServer().getExecutorService().execute(()->{
            handler.onConnectThrowing(attachment, exc);
        });
    }
}
