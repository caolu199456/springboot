package com.example.util.http;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.example.util.security.MD5;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by CL on 2016/12/22.
 */
public class OSSUtils {

    private OSSClient ossClient;
    private String defaultBucket;

    public OSSUtils(OSSClient ossClient, String defaultBucket) {
        this.ossClient = ossClient;
        this.defaultBucket = defaultBucket;
    }
    public void close(){
       ossClient.shutdown();
    }
    /**
     * 传入文件  md5_文件名形式
     * @param file
     * @param bucketName null采取默认
     * @return
     * @throws IOException
     */
    public String upload(File file,String bucketName) {
        try {
            StringBuilder fileKey = new StringBuilder("app/");

            fileKey.append(MD5.getBigFileMD5(file)).append("_").append(file.getName());

            ossClient.putObject(bucketName == null ? this.defaultBucket : bucketName, fileKey.toString(), file);

            return fileKey.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 传入流  md5_文件名形式
     * @param in
     * @param bucketName null采取默认
     * @return
     * @throws IOException
     */
    public String upload(InputStream in, String fileName,String bucketName) {
        File tempFile =new File(FileUtils.getTempDirectory(),UUID.randomUUID().toString());
        try (InputStream is = in;
             FileOutputStream fos = new FileOutputStream(tempFile)
        ){
            IOUtils.copyLarge(is, fos);

            StringBuilder fileKey = new StringBuilder("app/");

            fileKey.append(MD5.getBigFileMD5(tempFile)).append("_").append(fileName);

            ossClient.putObject(bucketName == null ? this.defaultBucket : bucketName, fileKey.toString(), tempFile);


            return fileKey.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 传入流产生随机文件名 uuid.jpg  返回fileKey(下载所需要)
     * @param in 输入流
     * @param fileSuffix .jpg
     * @param bucketName null使用默认bucket
     * @return
     * @throws IOException
     */
    public String uploadStream(InputStream in,String fileSuffix, String bucketName) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

        StringBuilder fileKey = new StringBuilder("data/");

        fileKey.append(sdf.format(new Date())).append("/").append(UUID.randomUUID().toString().replace("-","")).append(fileSuffix);

        try {
            ossClient.putObject(bucketName==null?this.defaultBucket:bucketName, fileKey.toString(), in);
        }catch (Exception e){
            throw new Exception("OSS文件上传失败");
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return fileKey.toString();
    }

    /**
     * 获取外链URL 默认10年过期
     * @param fileKey
     * @param bucketName
     * @return
     */
    public String getFileUrl(String fileKey, String bucketName) {
        // 过期时间10分钟
        Date expiration = new Date(System.currentTimeMillis() + 10 * 365 * 24 * 60 * 60 * 1000);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName==null?this.defaultBucket:bucketName, fileKey, HttpMethod.GET);
        req.setExpiration(expiration);
        URL signedUrl = ossClient.generatePresignedUrl(req);
        return signedUrl.toString();
    }

    /**
     * 获取外链地址指定有效期
     * @param fileKey
     * @param bucketName
     * @param expireTime 单位毫秒
     * @return
     */
    public String getFileUrl(String fileKey, String bucketName,Long expireTime) {
        Date expiration = new Date(System.currentTimeMillis() + expireTime );
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName==null?this.defaultBucket:bucketName, fileKey, HttpMethod.GET);
        req.setExpiration(expiration);
        URL signedUrl = ossClient.generatePresignedUrl(req);
        return signedUrl.toString();
    }
    /**
     * 下载文件
     * @param fileKey  上传返回的fileKey  2016/7/4/24/d187255d-f7c7-4ad1-9a5c-f329d78d61f5.jpg
     * @param path 往本地哪里存 "d:/"
     */
    public File download(String fileKey,String bucketName, String path) throws FileNotFoundException {
        /*
        * 由于上传的时候采用uuid则此时直接获取文件名即可
        * */

        System.out.println(path+fileKey.substring(0,fileKey.lastIndexOf("/")));
        File dir = new File(path+fileKey.substring(0,fileKey.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file =  new File(path + fileKey);
        ossClient.getObject(new GetObjectRequest(bucketName,fileKey),file);
        return file;

    }


    public static void main(String[] args) {

    }
}
