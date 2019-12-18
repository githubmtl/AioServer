package com.mtl.aio;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * 说明:回调类
 *
 * @作者 莫天龙
 * @时间 2019/12/16 10:52
 */
public interface Handler <T> {
    /**
     * 当连接时执行
     * @param session
     */
    public void onConnected(Session session);

    /**
     * 当连接时发生异常执行
     * @param session
     */
    public void onConnectThrowing(Session session,Throwable t);

    /**
     * 当收到消息的时候执行
     * @param session
     * @param message
     */
    public void onMessageReceived(Session session,final T message);

    /**
     * 读取时发生异常时执行
     * @param session
     * @param t
     */
    public void onReadThrowing(Session session,Throwable t);

    /**
     * 当消息写出完成时执行
     * @param session
     */
    public void onMessageWrited(Session session);

    /**
     * 当写出消息发生异常时执行
     * @param session
     * @param t
     */
    public void onWriteThrowing(Session session,Throwable t);
}
