package com.inteview.IO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 *  AIO 异步非阻塞
 *  单线程
 *
 */
public class AIO1 {
    public static void main(String[] args) throws IOException {

        AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel
                .open()
                .bind(new InetSocketAddress("127.0.0.1",8888));

        // step1: 非阻塞的accept  回掉函数CompletionHandler
        asynchronousServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
           // step2: 链接事件发生自动调用
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                asynchronousServerSocketChannel.accept(null,this);

                try {
                    System.out.println(client.getRemoteAddress()+"");
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // step3: 非阻塞的read，回掉函数CompletionHandler

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
