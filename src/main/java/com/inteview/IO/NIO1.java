package com.inteview.IO;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * NIO  同步非阻塞IO  调用方法一定有返回，或者NULL或者有连接client
 *
 *
 * step0: 创建ServerSockerChannel、绑定端口号、设置端口为非阻塞
 *        ServerSocketChannel ss = ServerSocketChannel.open();
 *        ss.bind(new InetSocketAddress(9090));
 *        ss.configureBlocking(false);
 *
 * step1: 非阻塞方式接受客户端链接,对比阻塞方式接受客户端链接，阻塞方式若无客户端链接会停止在这一行代码不会继续执行，
 *        直到有客户端链接才继续执行； 非阻塞方式接受链接，不管是否有客户端链接会立刻返回结果，无链接返回为null,有链接fanhui
 *        SocketChannel client = ss.accept();
 *
 * step2: 判断是否有客户端链接，若有将链接设置为非阻塞、获取端口打印（只是为了显示，非必须）、将 client 加入 clients 集合中
 *         client.configureBlocking(false);
 *         int port = client.socket().getPort();
 *         System.out.println("client.....port: " + port);
 *         clients.add(client);
 *
 *    注意: 这里设置非阻塞有两处，一个是step0, ss.configureBlocking(false); 设置client链接动作为非阻塞
 *          一个是 step2, client.configureBlocking(false); 设置client读取非阻塞
 *
 * step3: 遍历clients已经连接进来的客户能不能读写数据
 *
 *        int num = c.read(buffer);
 *        if (num > 0) {
 *          buffer.flip();
 *          byte[] aaa = new byte[buffer.limit()];
 *          buffer.get(aaa);
 *          String b = new String(aaa);
 *          System.out.println(c.socket().getPort() + ":" + b);
 *          buffer.clear();
 *        }
 *
 *  优点：1.不需要很多线程连接
 *  缺点：1.当有1w个客户端连接，但是只有一个输入IO，这样我要循环1w次去系统调用recv，循环内核空间切换用户空间1w次
 *       这里step3中遍历clients,并且每个client都要调用 read方法，这里read方法是会发送recv命令，这个操作是内核操作消耗资源
 *
 */
public class NIO1 {
    public static void main(String[] args) throws Exception {
        LinkedList<SocketChannel> clients = new LinkedList<>();

        // step0: 创建ServerSockerChannel、绑定端口号、设置端口为非阻塞
        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false);     // OS的NIO
        //设置非阻塞


        while (true) {
            // 接受客户端的连接
            Thread.sleep(1000);

            // step1: 非阻塞方式接受客户端链接
            SocketChannel client = ss.accept();
            // accept 调用内核了 ：  1.没有客户端连接就返回NULL或者-1，
            //                      2.BIO会停止在这里，无返回，NIO是返回null，继续执行下去
            //                      3.有客户端连接就返回fd 5 client


            // step2:判断是否有客户端链接，若有将client加入clients的集合中，设置非阻塞
            if (client == null) {
                //System.out.println("null................");
            } else {
                client.configureBlocking(false);
                int port = client.socket().getPort();
                System.out.println("client.....port: " + port);
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);

            // step3: 遍历已经连接进来的客户能不能读写数据
            for (SocketChannel c : clients) {

                // c.reader(buffer) 该方法非阻塞，返回int 大于0则有数据读取
                int num = c.read(buffer);

                // 这个read 发送recv的系统调用
                if (num > 0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);
                    String b = new String(aaa);
                    System.out.println(c.socket().getPort() + ":" + b);
                    buffer.clear();
                }
            }
        }
    }
}
