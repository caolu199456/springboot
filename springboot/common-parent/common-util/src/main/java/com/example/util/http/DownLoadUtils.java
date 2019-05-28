package com.example.util.http;


import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownLoadUtils {
    public static File batchDownLoad(List<String> urls) throws IOException {
        File file= new File(FileUtils.getTempDirectoryPath(),new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".rar");
        try (
            FileOutputStream fos = new FileOutputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
        ){
            for (String url : urls) {
                InputStream stream = null;
                ZipEntry entry = null;
                try {
                    String fileName = url.substring(url.lastIndexOf("/"));
                    entry = new ZipEntry(fileName);
                    zipOut.putNextEntry(entry);

                    URL fileUrlObJ = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) fileUrlObJ.openConnection();
                    if (connection != null && connection.getResponseCode() == 200) {
                        stream = connection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = stream.read(buffer)) != -1) {
                            zipOut.write(buffer, 0, len);
                        }
                        zipOut.flush();

                    }
                } catch (Exception e) {
                    continue;
                }finally {
                    if (stream != null) {
                        stream.close();
                    }
                }

            }


        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static File compress(List<File> fileList) {

        File file= new File(FileUtils.getTempDirectoryPath(),new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".rar");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
        ){
            for (File fileParam : fileList) {
                ZipEntry entry = null;
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileParam))) {

                    entry = new ZipEntry(fileParam.getName());
                    zipOut.putNextEntry(entry);

                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = bis.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, len);
                    }
                    zipOut.flush();

                } catch (Exception e) {
                    continue;
                } finally {
                }

            }


        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void main(String[] args) throws IOException {
       /* File file = compress(new ArrayList<File>() {{
            add(new File("d:/APPdown.jpg"));
            add(new File("d:/free_charge_log.jpg"));
            add(new File("d:/QQ截图20171101095954.png"));
        }});
        System.out.println(file.getPath());*/
        System.out.println(System.getProperty("java.io.tmpdir"));
        System.out.println(System.getProperty("user.dir"));
    }

}
