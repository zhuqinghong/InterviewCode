package com.inteview.IO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AIO
 * 多线程线程池写法
 */
public class AIO2 {
    public static void main(String[] args) throws IOException {

        ExecutorService threadPool=Executors.newCachedThreadPool();

        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withCachedThreadPool(threadPool,1);

        AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel
                .open(channelGroup)
                .bind(new InetSocketAddress("127.0.0.1",8888));

        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                asynchronousServerSocketChannel.accept(null,this);

                try {
                    System.out.println(client.getRemoteAddress()+"");
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            System.out.println(new String(attachment.array(),0, result));
                            client.write(ByteBuffer.wrap("HelloClient".getBytes()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            exc.printStackTrace();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        while (true){

        }
    }
}
