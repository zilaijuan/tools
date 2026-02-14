package com.qrtools;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class QRCodeDecoder {

    public static String decode(BufferedImage image) {
        String result = null;
        try {
            // 转换图像为ZXing可处理的格式
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            // 解码二维码
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result decodeResult = new MultiFormatReader().decode(bitmap, hints);
            result = decodeResult.getText();
        } catch (NotFoundException e) {
            // 未找到二维码
            System.out.println("未找到二维码");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}