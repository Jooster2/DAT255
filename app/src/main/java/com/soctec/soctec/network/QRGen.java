package com.soctec.soctec.network;


import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRGen
{
    private String startInfo = "Inget IP hämtat";
    private BitMatrix startMatrix;
    private int qrSize = 150;
    private QRCodeWriter writer;


    public QRGen()
    {
        writer = new QRCodeWriter();
        try
        {
            startMatrix = writer.encode(    //TODO Behövs någon startmatris?
                    startInfo, BarcodeFormat.QR_CODE, qrSize, qrSize);

        } catch (WriterException e) {e.printStackTrace();}
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
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.GREEN);
            }
        }
        return bmp;
    }

    /**
     * Generates a qr-code from a ip address
     * @param ip The ip-address to encode
     * @return A QR image
     */
    public Bitmap getQR(String ip)
    {
        BitMatrix matrix = new BitMatrix(qrSize,qrSize);
        try
        {
            matrix = writer.encode(
                    ip, BarcodeFormat.QR_CODE, qrSize, qrSize);
        }catch (WriterException e)
        {
            e.printStackTrace();
        }

        return toBitmap(matrix);
    }

}



