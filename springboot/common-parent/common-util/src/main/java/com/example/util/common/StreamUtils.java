package com.example.util.common;

import java.io.*;

public class StreamUtils {

    private static final int EOF = -1;

    /**
     * 流拷贝
     * @param in
     * @param out
     * @return
     * @throws IOException
     */
    public static long copy(InputStream in, OutputStream out) {
        try (
                BufferedInputStream input = new BufferedInputStream(in);
                BufferedOutputStream bos = new BufferedOutputStream(out)
                ){
            int len = 0;
            long size = 0;
            byte[] buffer = new byte[4096];
            while ((len = input.read(buffer)) != EOF) {
                bos.write(buffer, 0, len);
                size += len;
            }
            bos.flush();
            return size;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * 流转为数组
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream in) {
        try (
                InputStream input = in;
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ){
            int len = 0;
            long size = 0;
            byte[] buffer = new byte[4096];
            while ((len = input.read(buffer)) != EOF) {
                baos.write(buffer, 0, len);
                size += len;
            }
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 流转为string
     * @param in
     * @return
     * @throws IOException
     */
    public static String toString(InputStream in) {
        return new String(toByteArray(in));
    }
    /**
     * 流转为文件
     * @param in
     * @param file
     * @return
     * @throws IOException
     */
    public static void  toFile(InputStream in,File file) {
        try {
            copy(in, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
