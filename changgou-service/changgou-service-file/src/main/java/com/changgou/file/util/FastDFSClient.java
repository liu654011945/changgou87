package com.changgou.file.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/***
 * 工具类 用于操作图片的工具类（上传 下载 删除....）
 * @author ljh
 * @packagename com.changgou.file.util
 * @version 1.0
 * @date 2020/5/6
 */
public class FastDFSClient {

    static {
        ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
        try {
            ClientGlobal.init(classPathResource.getPath());//获取类路径下的文件
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param file 要上传的文件的封装对象
     * @return
     */
    public static String[] upload(FastDFSFile file) throws Exception {
        //1.创建一个配置文件用于链接tracker服务器

        //2.加载配置文件
        // ClientGlobal.init("C:\\Users\\admin\\IdeaProjects\\changgou87\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");

        //3.创建trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer对象
        StorageServer storageServer = null;

        //6.创建storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //7.storageClient有许多的方法 比如上传图片

        //参数1 指定要上传的图片字节数组对象 文件本身
        //参数2 指定图片的扩展名 注意不要带点
        //参数3 指定的图片的元数据 比如图片的像素 高度 拍摄日期 作者，大小
        NameValuePair[] nameValuePairs = new NameValuePair[]{
                new NameValuePair(file.getAuthor()),
                new NameValuePair(file.getExt())

        };
        String[] jpgs = storageClient.upload_file(file.getContent(), file.getExt(), nameValuePairs);

        return jpgs;
    }

    //下载图片
    public static byte[] downFile(String groupName, String remoteFileName) throws Exception {
        //1.创建一个配置文件用于链接tracker服务器


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
        byte[] bytes = storageClient.download_file(groupName, remoteFileName);
        return bytes;
    }

    //删除图片

    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        //1.创建一个配置文件用于链接tracker服务器

        //2.加载配置文件

        //3.创建trackerclient 对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer对象
        StorageServer storageServer = null;

        //6.创建storageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        int group1 = storageClient.delete_file(groupName, remoteFileName);
        if (group1 == 0) {
            System.out.println("success");
        } else {
            System.out.println("failed");
        }
    }







    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            //3.创建trackerclient 对象
            TrackerClient trackerClient = new TrackerClient();

            //4.创建trackerServer对象
            TrackerServer trackerServer = trackerClient.getConnection();

            //5.创建storageServer对象
            StorageServer storageServer = null;

            //6.创建storageClient对象
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            //获取文件信息
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取tracker的信息
    public static String getTrackerUrl() {
        try {
            //创建TrackerClient对象
            TrackerClient trackerClient = new TrackerClient();
            //通过TrackerClient获取TrackerServer对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Tracker地址
            return "http://" + trackerServer.getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_http_port();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
