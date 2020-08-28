package com.inteview.IO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;



/**
 *  NIO 多路复用
 *
 *  问题1: NIO 多路复用，复用了什么？这种方法比NIO1好在哪里？
 *  答: 首先，要知道所有的 IO链接事件，IO读写事件，在内核中都统一为fd（文件描述符），在 NIO1 中，需要遍历文件描述符fd，
 *      并且将fd一个个传入内核，轮询这个fd上是否有事件发生。注意这一个个fd传递给内核，轮询fd的动作及其耗时，因为涉及内核调用
 *
 *      多路复用：指的是我能不能使用一次轮询，就能获取所有的有事件发生的fd，避免了多次调用内核的过程
 *
 *  同步IO多路复用器 有3种 Select 、 Poll 、Epoll
 *  Select 连接个数限制
 *  POll   连接个数无限制
 *         1.每次重复传递多个fds到内核
 *         2.内核选出有事件的fds
 *
 *  缺点:  1.每次重复传递fds   解决方案：epoll 的内核开辟内存空间
 *        2.每次遍历遍历fds
 *
 *  Epoll: 使用一个select轮询多个文件描述符
 *         select可以传入连接文件描述符、 读文件描述符、写文件描述符
 *
 *  epoll_create  开辟内核空间
 *  epoll_ctl     创建fd1，加入内核空间，监听一个accept
 *  epoll_wait    获取IO状态
 *
 *
 */

public class NIO2 {
    public static  void main(String[] args) throws IOException {
        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.configureBlocking(false);
        ss.bind(new InetSocketAddress("127.0.0.1",9090));

        Selector selector = Selector.open();

        // 注册监听事件
        ss.register(selector, SelectionKey.OP_ACCEPT);


        while (true){
            Set<SelectionKey> keys = selector.keys();

            //System.out.println(keys.size()+ "    size");

            // 阻塞方法，有事件发生了
            while (selector.select(500)>0){
                Set<SelectionKey> selectionKeys  = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectionKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if(key.isAcceptable()){

                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel client =ssc.accept();

                        client.configureBlocking(false);

                        ByteBuffer buffer = ByteBuffer.allocateDirect(8192);

                        client.register(selector,SelectionKey.OP_READ,buffer);

                        System.out.println("-------------------------------");

                        System.out.println("新客户端连接" + client.getRemoteAddress());

                        System.out.println("-------------------------------");


                    }else if(key.isReadable()){
                        SocketChannel sc = null;
                        try {
                            sc = (SocketChannel) key.channel();
                            ByteBuffer buffer  = ByteBuffer.allocateDirect(512);
                            buffer.clear();

                            int len = sc.read(buffer);

                            // 这个很重要哦
                            buffer.flip();
                            if (len!=-1){
                                byte[] aaa = new byte[buffer.limit()];
                                buffer.get(aaa);
                                String b = new String(aaa);
                                System.out.println(b);
                            }

                            ByteBuffer byteBufferTOWrite  = ByteBuffer.wrap("Welocome".getBytes());
                            sc.write(byteBufferTOWrite);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if(sc!=null){
                                try {
                                    sc.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
