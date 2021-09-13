package com.mmall.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zhangruiyan
 * 上传文件
 */
public class FastDFSUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastDFSUtil.class);

    public static String upload(MultipartFile file){
        StringBuffer sb = new StringBuffer(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        try {
            //加载配置文件
            ClientGlobal.init("fastDFS.conf");
            //创建TrackerClient跟踪器客户端对象
            TrackerClient client = new TrackerClient();
            //通过跟踪器客户端对象获得跟踪器服务端对象
            TrackerServer tserver = client.getConnection();
            //获得存储节点对象
            StorageClient sc = new StorageClient(tserver, null);
            String[] split = file.getOriginalFilename().split("\\.");
            //拼接卷名
            String[] strings = sc.upload_file(file.getBytes(), split[split.length - 1], null);
            sb.append(strings[0]+"/");
            //拼接文件名
            sb.append(strings[1]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String upload(String file){
        StringBuffer sb = new StringBuffer(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        try {
            //加载配置文件
            ClientGlobal.init("fastDFS.conf");
            //创建TrackerClient跟踪器客户端对象
            TrackerClient client = new TrackerClient();
            //通过跟踪器客户端对象获得跟踪器服务端对象
            TrackerServer tserver = client.getConnection();
            //获得存储节点对象
            StorageClient sc = new StorageClient(tserver, null);
            String[] split = file.split("\\.");
            //拼接卷名
            String[] strings = sc.upload_file(file, split[split.length - 1], null);
            sb.append(strings[0]+"/");
            //拼接文件名
            sb.append(strings[1]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 删除文件
     * @param url
     */
    public static void delete(String url){
        try {
            //加载配置文件
            ClientGlobal.init("fastDFS.conf");
            //创建跟踪器客户端对象
            TrackerClient client = new TrackerClient();
            //通过客户端对象获得跟踪器服务端对象
            TrackerServer tserver = client.getConnection();
            //获得储存节点对象
            StorageClient sc = new StorageClient(tserver, null);
            String substring = url.substring(url.indexOf("g"));
            String group = substring.substring(0, substring.indexOf("/"));
            String fileName = substring.substring(substring.indexOf("/") + 1);
            int i = sc.delete_file(group,fileName);
            System.out.println(i);
            LOGGER.info("需要删除的文件名：{}",i);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public static void download(String src, OutputStream writ){
        try {
            //加载配置文件
            ClientGlobal.init("fastDFS.conf");
            //创建跟踪器客户端对象
            TrackerClient client = new TrackerClient();
            //通过客户端对象获得跟踪器服务端对象
            TrackerServer tserver = client.getConnection();
            //获得储存节点对象
            StorageClient sc = new StorageClient(tserver, null);
            String substring = src.substring(src.indexOf("g"));
            String group = substring.substring(0, substring.indexOf("/"));
            String fileName = substring.substring(substring.indexOf("/") + 1);
            byte[] bs = sc.download_file(group, fileName);
            writ.write(bs);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
