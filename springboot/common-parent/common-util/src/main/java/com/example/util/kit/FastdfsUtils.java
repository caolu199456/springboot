package com.example.util.kit;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * fastdfs上传帮助类
 *
 * @author: CL
 * @email: caolu@sunseaaiot.com
 * @date: 2019-03-07 12:12:00
 */
public class FastdfsUtils {
    private static Properties properties = new Properties();
    static {
        try {


            ClientGlobal.init("fdfs_client.conf");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param file
     * @return 携带url地址
     */
    public static String upload(File file) {

        StorageClient1 storageClient = new StorageClient1();
        try {
            String fileName = file.getName();
            String[] fileInfo = storageClient.upload_appender_file(file.getAbsolutePath(), fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "", null);
            String httpHost = ClientGlobal.getG_tracker_group().tracker_servers[0].getHostString();

            return "http://" + httpHost + ":" + ClientGlobal.getG_tracker_http_port() + "/" + fileInfo[0] + "/" + fileInfo[1];
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String upload = upload(new File("d:/200812308231244_2.jpg"));
        System.out.println(upload);
    }


}
