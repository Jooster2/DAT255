package com.soctec.soctec;

import android.graphics.Bitmap;


public class Unnamed
{
    private Encryptor encryptor;
    private QRGen qrGen;

    public Unnamed()
    {
        encryptor = new Encryptor();
        qrGen = new QRGen();
    }


    public String getPlayAcc()
    {
        //dummy
        return "abc@abc.com";
    }

    public String getUserIP()
    {
        //dummy
        return "192.168.0.1";
    }

    public Bitmap getQrOfIp()
    {
        return qrGen.getQR(getUserIP());
    }

    public String getEncryptedAcc()
    {
        return encryptor.encrypt(getPlayAcc());
    }

}
