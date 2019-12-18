package com.mtl.aio;

import com.mtl.aio.coder.ByteToStringDecoder;
import com.mtl.aio.coder.MessageDecoder;
import com.mtl.aio.coder.MessageEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 说明:AIO服务类
 *
 * @作者 莫天龙
 * @时间 2019/12/16 10:26
 */
public class AioServer {
    private AsynchronousServerSocketChannel serverSocketChannel;
    /**
     * 读超时时间，单位秒
     */
    private long readTimeOut=-1L;

    /**
     * 写超时时间，单位秒
     */
    private long writeTimeOut=30L;

    /**
     * 异步IO操作线程
     */
    private int threadNum=-1;

    /**
     * 服务监听端口
     */
    private int port;

    /**
     * socket参数
     */
    private Map<SocketOption<Object>,Object> socketOptionsMap=new HashMap<>();

    /**
     * 保存客户端连接
     */
    private Map<String, Session> clients=new HashMap<>();

    /**
     * 读取策略
     */
    private ReadStrategy readStrategy;

    /**
     * 解码器链
     */
    private List<MessageDecoder> decoders=new ArrayList<>();
    /**
     * 编码器链
     */
    private List<MessageEncoder> encoders=new ArrayList<>();

    /**
     * 回调
     */
    private Handler handler;

    /**
     * 回调执行器
     */
    private ExecutorService executorService=Executors.newCachedThreadPool(new ThreadFactory(){
        private AtomicInteger count=new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            Thread t=new Thread(r);
            t.setName("aio handler pool ["+count.getAndIncrement()+"]");
            return t;
        }
    });

    AioServer() {
    }

    void setReadStrategy(ReadStrategy readStrategy) {
        this.readStrategy = readStrategy;
    }

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    Handler getHandler(){
        return handler;
    }

    void setOptions(SocketOption socketOption, Object val){
        socketOptionsMap.put(socketOption, val);
    }

    long getReadTimeOut() {
        return readTimeOut;
    }

    ReadStrategy getReadStrategy(){
        return readStrategy;
    }

    void setReadTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public AsynchronousServerSocketChannel bind(int port) throws IOException {
        newChannel();
        serverSocketChannel.bind(new InetSocketAddress(port));
        return serverSocketChannel;
    }

    public AsynchronousServerSocketChannel bind(int port,int backlog) throws IOException {
        newChannel();
        serverSocketChannel.bind(new InetSocketAddress(port),backlog);
        return serverSocketChannel;
    }

    private void newChannel() throws IOException {
        AsynchronousChannelGroup channelGroup=null;
        if (threadNum==-1){
            channelGroup=AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(),Runtime.getRuntime().availableProcessors());
        }else{
            channelGroup=AsynchronousChannelGroup.withFixedThreadPool(threadNum, new ThreadFactory() {
                private AtomicInteger count=new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t=new Thread(r);
                    t.setName("aio thread ["+count.getAndIncrement()+"]");
                    return t;
                }
            });
        }
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        this.serverSocketChannel=serverSocketChannel;
    }

    private Session createSession(){
        Session session=new Session(serverSocketChannel, null);
        session.setAioServer(this);
        session.setId(UUID.randomUUID().toString());
        return session;
    }

    List<MessageDecoder> getDecoders() {
        return decoders;
    }

    List<MessageEncoder> getEncoders() {
        return encoders;
    }

    void addDecoder(MessageDecoder messageDecoder){
        decoders.add(messageDecoder);
    }

    void addEncoder(MessageEncoder messageEncoder){
        encoders.add(messageEncoder);
    }

    public void start(){
        Session session = createSession();
        serverSocketChannel.accept(session,new AcceptHandler());
    }

    public Session getClient(String id) {
        return clients.get(id);
    }

    public Map<String, Session> getClients() {
        return clients;
    }

    void addClient(Session session) {
        synchronized (clients){
            this.clients.put(session.getId(), session);
        }
    }

    long getWriteTimeOut() {
        return writeTimeOut;
    }

    ExecutorService getExecutorService() {
        return executorService;
    }

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
