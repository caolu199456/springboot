package com.example.util.common;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码帮助类
 */
public class QRCodeUtils {
    /**
     * 生成二维码
     * @param content 内容
     * @param width 大小
     * @param height 高度
     * @param format png jpg
     * @param out 输出目的地
     */
    public static void create(String content,int width,int height,String format,OutputStream out) {
        //定义二维码的参数
        Map<Object,Object> map = new HashMap();
        //设置编码
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置纠错等级
        map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        map.put(EncodeHintType.MARGIN, 0);
        //生成二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            MatrixToImageWriter.writeToStream(bitMatrix, format, out);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String read(InputStream in) {
        try {
            MultiFormatReader multiFormatReader = new MultiFormatReader();
            BufferedImage image = ImageIO.read(in);
            //定义二维码参数
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");

            //获取读取二维码结果
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = multiFormatReader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) throws FileNotFoundException {
        String read = read(new FileInputStream("d:/1.png"));
        System.out.println(read);
    }
}
