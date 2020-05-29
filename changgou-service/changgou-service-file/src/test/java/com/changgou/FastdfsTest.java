package com.changgou;

import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;

/***
 * 描述
 * @author ljh
 * @packagename com.changgou
 * @version 1.0
 * @date 2020/5/6
 */
public class FastdfsTest {


    //图片上传
    @Test
    public void upload() throws Exception {
        //1.创建一个配置文件用于链接tracker服务器

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //3.创建trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer对象
        StorageServer storageServer = null;

        //6.创建storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //7.storageClient有许多的方法 比如上传图片

        //参数1 指定要上传的图片的路径
        //参数2 指定图片的扩展名 注意不要带点
        //参数3 指定的图片的元数据 比如图片的像素 高度 拍摄日期 作者，大小
        String[] jpgs = storageClient.upload_file("C:\\Users\\admin\\Pictures\\Saved Pictures\\42932268_1492004444336.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    //图片下载
    @Test
    public void download() throws Exception {
        //1.创建一个配置文件用于链接tracker服务器

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //3.创建trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer对象
        StorageServer storageServer = null;

        //6.创建storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //7.storageClient有许多的方法 下这图片
        //参数1 指定组名
        //参数2 指定要下的图片的路径
        byte[] bytes = storageClient.download_file("group1", "M00/00/00/wKjThF6yZjOABCvFAACAThdn_1U976.jpg");


        //io流

        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\com\\12345.jpg"));
        fileOutputStream.write(bytes);
        fileOutputStream.close();//需要finalyy中进行关闭
    }


    //图片的删除
    @Test
    public void delete() throws Exception {
        //1.创建一个配置文件用于链接tracker服务器

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //3.创建trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer对象
        StorageServer storageServer = null;

        //6.创建storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        int group1 = storageClient.delete_file("group1", "M00/00/00/wKjThF6yZjOABCvFAACAThdn_1U976.jpg");
        if(group1==0){
            System.out.println("success");
        }else{
            System.out.println("failed");
        }
    }


    //获取文件的信息数据
    @Test
    public void getFileInfo() throws Exception {
        //加载全局的配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取StorageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, null);
        //执行文件上传

        FileInfo group1 = storageClient.get_file_info("group1", "M00/00/00/wKjThF6yaHqAE2mFAACAThdn_1U904.jpg");

        System.out.println(group1);

    }

    //获取组相关的信息
    @Test
    public void getGroupInfo() throws Exception {
        //加载全局的配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();

        StorageServer group1 = trackerClient.getStoreStorage(trackerServer, "group1");
        System.out.println(group1);

        //组对应的服务器的地址  因为有可能有多个服务器.
        ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, "group1", "M00/00/00/wKjThF6yaHqAE2mFAACAThdn_1U904.jpg");
        for (ServerInfo serverInfo : group1s) {
            System.out.println(serverInfo.getIpAddr());
            System.out.println(serverInfo.getPort());
        }
    }

    @Test
    public void getTrackerInfo() throws Exception {
        //加载全局的配置文件
        ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //创建TrackerClient客户端对象
        TrackerClient trackerClient = new TrackerClient();
        //通过TrackerClient对象获取TrackerServer信息
        TrackerServer trackerServer = trackerClient.getConnection();

        int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();
        System.out.println(g_tracker_http_port);
        InetSocketAddress inetSocketAddress = trackerServer.getInetSocketAddress();
        System.out.println(inetSocketAddress);

    }


}
