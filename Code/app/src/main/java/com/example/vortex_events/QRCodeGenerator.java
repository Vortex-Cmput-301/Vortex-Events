package com.example.vortex_events;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
/**
 * Utility class for generating QR code bitmaps using the ZXing library.
 *
 * This class provides a method to convert a text string into a QR code image,
 * which can then be displayed in an ImageView or shared as needed.
 */
public class QRCodeGenerator {
    /**
     * Generates a QR code bitmap from the given text.
     *
     * @param text   the string content to encode into a QR code
     * @param width  the width of the output bitmap
     * @param height the height of the output bitmap
     * @return a Bitmap containing the generated QR code
     * @throws WriterException if the QR encoding process fails
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
