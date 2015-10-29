package com.soctec.soctec.network;


import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Generates QR-code images from a String input (usually an IP-address)
 *
 * Uses the following libraries which is under Apache License version 2.0:
 *
 *  com.google.zxing.BarcodeFormat
 *  com.google.zxing.WriterException
 *  com.google.zxing.common.BitMatrix
 *  com.google.zxing.qrcode.QRCodeWriter
 *
 * See license in License.txt in the project root.
 *
 * @author Robin Punell
 * @version 1.0
 */
public class QRGen
{
    private static final int QR_SIZE = 150;
    private QRCodeWriter writer;

    /**
     * Constructs a QR-generator
     * Creates a QRCodeWriter
     */
    public QRGen()
    {
        writer = new QRCodeWriter();
    }

    /**
     * Writes the given Matrix on a new Bitmap object.
     * @param matrix the matrix to write.
     * @return the new {@link Bitmap}-object.
     */
    private static Bitmap toBitmap(BitMatrix matrix)
    {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.rgb(219, 250, 170));
            }
        }
        return bmp;
    }

    /**
     * Generates a qr-code from a ip address
     * @param ip the ip-address to encode
     * @return a QR image
     */
    public Bitmap getQR(String ip)
    {
        BitMatrix matrix = new BitMatrix(QR_SIZE,QR_SIZE);
        try
        {
            matrix = writer.encode(
                    ip, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        }catch (WriterException e)
        {
            e.printStackTrace();
        }

        return toBitmap(matrix);
    }
}
