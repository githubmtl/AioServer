package com.mtl.aio;

import com.mtl.aio.coder.MessageDecoder;
import com.mtl.aio.coder.MessageEncoder;

import java.net.SocketOption;
import java.util.concurrent.ExecutorService;

/**
 * 说明:构造一个异步套接字
 *
 * @作者 莫天龙
 * @时间 2019/12/16 10:22
 */
public class AioServerBuilder {
    private AioServer aioServer;

    public AioServerBuilder() {
        aioServer=new AioServer();
    }

    public AioServerBuilder option(SocketOption socketOption,Object val){
        aioServer.setOptions(socketOption, val);
        return this;
    }

    public AioServerBuilder readTimeout(long timeout){
        aioServer.setReadTimeOut(timeout);
        return this;
    }

    public AioServerBuilder readStrategy(ReadStrategy readStrategy){
        aioServer.setReadStrategy(readStrategy);
        return this;
    }

    public AioServerBuilder addDecoder(MessageDecoder messageDecoder){
        aioServer.addDecoder(messageDecoder);
        return this;
    }

    public AioServerBuilder addEncoder(MessageEncoder messageEncoder){
        aioServer.addEncoder(messageEncoder);
        return this;
    }

    public AioServerBuilder addHandler(Handler handler){
        aioServer.setHandler(handler);
        return this;
    }

    public AioServer build(){
        return aioServer;
    }

    public AioServerBuilder addExecutor(ExecutorService executorService){
        ExecutorService executorService1 = aioServer.getExecutorService();
        executorService1.shutdownNow();
        aioServer.setExecutorService(executorService);
        return this;
    }

}
