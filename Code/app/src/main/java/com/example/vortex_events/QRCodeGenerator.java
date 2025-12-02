package com.example.vortex_events;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Utility that generates QR code Bitmaps from a text payload.
 */
public class QRCodeGenerator {

    /**
     * Generates a QR code bitmap for the provided text.
     * @param text payload to encode
     * @param width bitmap width
     * @param height bitmap height
     * @return generated QR Bitmap
     * @throws WriterException when QR encoding fails
     */
    public static Bitmap generateQRCodeBitmap(String text, int width, int height)
            throws WriterException {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bmp;
    }
}
