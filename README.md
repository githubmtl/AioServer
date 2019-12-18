# AioServer
JAVA AIO 网络通信框架实现
# 简单实用例子
```java
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        AioServerBuilder aioServerBuilder=new AioServerBuilder();
        //添加编码器和读取策略（定长读取，还支持报文长度+报文体的方式读取），也可自定义读取策略
        aioServerBuilder.addDecoder(new ByteToStringDecoder()).readStrategy(new FixedLengthReadStrategy(2000));
        //添加消息处理器
        aioServerBuilder.addHandler(new Handler<String>(){

            @Override
            public void onConnected(Session session) {
                System.out.println("已连接："+session);
            }

            @Override
            public void onConnectThrowing(Session session, Throwable t) {
                System.out.println("连接时，出错！");
                t.printStackTrace();
                session.close();
            }

            @Override
            public void onMessageReceived(Session session, String message) {
                System.out.println(Thread.currentThread().getName()+" 收到消息:"+message);
                StringBuilder m=new StringBuilder().append(UUID.randomUUID().toString());
                m.append(UUID.randomUUID().toString());
                m.append(UUID.randomUUID().toString());
                m.append(UUID.randomUUID().toString());
                m.append(UUID.randomUUID().toString());
                m.append(UUID.randomUUID().toString());
                String s=m.toString();
                ByteBuffer byteBuffer=ByteBuffer.allocate(s.length());
                byteBuffer.put(s.getBytes(Charset.forName("utf-8")));
                byteBuffer.flip();
                session.write(byteBuffer);
            }

            @Override
            public void onReadThrowing(Session session, Throwable t) {
                System.out.println("读时，出错！");
                t.printStackTrace();
                session.close();
            }

            @Override
            public void onMessageWrited(Session session) {
                System.out.println("写出完成！");
            }

            @Override
            public void onWriteThrowing(Session session, Throwable t) {
                System.out.println("读时，出错！");
                t.printStackTrace();
                session.close();
            }
        });
        AioServer aioServer = aioServerBuilder.build();
        //服务监听端口
        aioServer.bind(10086);
        //开始异步监听
        aioServer.start();
        countDownLatch.await();
    }
}
```
