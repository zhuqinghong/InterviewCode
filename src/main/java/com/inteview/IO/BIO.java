package com.inteview.IO;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * BIO     同步阻塞IO的多线程写法
 *
 * 同步：    A调用B，B执行返回结果给A，A继续执行
 * 非同步：  A调用B，B直接返回一个-1或者null， A继续执行
 *
 *
 * 阻塞：    A调用B，如果B没有返回给我，我就挂起线程，停止在这里不会继续执行
 * 非阻塞：  A调用B，B直接就有个返回结果，我继续执行，当B返回
 *
 *Linux对应的底层操作
 *
 * socket  => fd 文件描述符 3
 * bind  （3，8090）
 * listen 3
 * while（true）
 * accept （3， =5 阻塞
 *
 *
 * 优势：1.接受很多客户端连接
 * 缺点：1.线程多浪费内存
 *       2.cpu调度消耗时间片
 *
 * 根源：accept， recv
 *
 *
 * 测试方法
 * nc localhost port
 * 发送数据
 */

public class BIO {
    public static void main(String[] args) throws IOException {

        //BIO  单线程写法
        //singleThread();

        //BIO  多线程 线程池的方式
        multiThreads();
    }

    /**
     * BIO 同步阻塞IO   单线程写法  也是网络IO最简单的写法
     *
     *
     *  step0: 创建SeverSocket对象 绑定端口号8090
     *         ServerSocket serverSocket = new ServerSocket(8090);
     *
     *  step1: 该处阻塞在accept()等待客户端链接
     *         Socket client = serverSocket.accept();
     *
     *  step2: 客户端链接client 获取InputStream输入流
     *         InputStream inputStream = client.getInputStream();
     *         byte[] bytes = new byte[1024];
     *
     *  step3: InputStream 读取数据,该处阻塞
     *         inputStream.read(bytes)!=-1 时有数据读取
     *
     *  结论: 单线程的BIO解决了，Server 和 Client的链接，通信数据交换

     *  问题: 这种单线程的BIO有什么缺点？如何解决这个缺点？
     *  答:  假设有一个server, 2个客户端client1 ，client2;
     *       client1 链接到 server上，client1 没有立刻写数据；
     *       那么server会阻塞到 step3 处，这个时候如果client2 链接 server，那么无法链接server
     *       即一个server同一时刻只能连一个client
     */
    public static void singleThread() throws IOException {

        // step0: 创建SeverSocket对象 绑定端口号8090
        ServerSocket serverSocket = new ServerSocket(8090);
        System.out.println("step1: new ServerSocket(8090)");

        while(true){

            //step1: 该处阻塞在accept()等待链接
            Socket client = serverSocket.accept();
            System.out.println("step2: client \t"+client.getPort());

            try {
                //step2: client获取输入流
                InputStream inputStream = client.getInputStream();
                byte[] bytes = new byte[1024];
                // BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));

                // step3: 读取数据  inputStream.read(bytes)  该处阻塞
                while(inputStream.read(bytes)!=-1){
                    // read是阻塞的
                    System.out.println(new String(bytes));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }
        }
    }


    /**
     * BIO 同步阻塞IO     多线程写法
     *
     *
     *  step0: 创建SeverSocket对象 绑定端口号8090
     *         ServerSocket serverSocket = new ServerSocket(8090);
     *
     *  step1: 该处阻塞在accept()等待客户端链接
     *         Socket client = serverSocket.accept();
     *
     *  step2: ThreadPool 线程池创建线程 处理一个客户端链接
     *         这样每个client端读取阻塞在线程内部，而不是主线程
     *
     *         客户端链接client 获取InputStream输入流
     *         InputStream inputStream = client.getInputStream();
     *         byte[] bytes = new byte[1024];
     *
     *  step3: InputStream 读取数据,该处阻塞
     *         inputStream.read(bytes)!=-1 时有数据读取
     *
     *  结论: 解决了 单线程只能链接一个client的问题
     *  问题: 1.多个client都来链接server，server 不停的创建线程，这样线程资源浪费
     *       2.线程多了cpu调度效率低
     */

    public static void multiThreads() throws IOException {
        //step0: 创建SeverSocket对象 绑定端口号8090
        ServerSocket serverSocket = new ServerSocket(8090);
        System.out.println("step1: new ServerSocket(8090)");

        while(true){

            // step1: 该处阻塞在accept()等待客户端链接
            Socket client = serverSocket.accept();
            System.out.println("step2: client \t"+client.getPort());

            new Thread(()->{
                try {
                    InputStream inputStream = client.getInputStream();
                    byte[] bytes = new byte[1024];
                    // BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream));
                    while(inputStream.read(bytes)!=-1){
                        // read是阻塞的
                        System.out.println(new String(bytes));
                    }
                } catch (IOException e) {
                }
            }).start();
        }
    }
}
