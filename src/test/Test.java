package test;

import com.mtl.aio.*;
import com.mtl.aio.coder.ByteToStringDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch countDownLatch=new CountDownLatch(1);
        AioServerBuilder aioServerBuilder=new AioServerBuilder();
        aioServerBuilder.addDecoder(new ByteToStringDecoder()).readStrategy(new FixedLengthReadStrategy(2000));
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
                //int i = Integer.parseInt(message);
                //String s = String.valueOf(i*i);
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
        aioServer.bind(10086);
        aioServer.start();
        countDownLatch.await();
    }
}
