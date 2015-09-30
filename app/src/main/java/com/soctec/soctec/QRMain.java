package com.soctec.soctec;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.soctec.soctec.core.Encryptor;
import com.soctec.soctec.network.QRGen;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;


public class QRMain
{
    private Encryptor encryptor;
    private QRGen qrGen;
    private Context mContext;


    public QRMain(Context mContext)
    {
        this.mContext = mContext;
        encryptor = new Encryptor();
        qrGen = new QRGen();
    }


    public String getPlayAcc()
    {
        String accMail = null;

        AccountManager manager = (AccountManager) mContext.getSystemService(mContext.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google")) //TODO Felhatering vid avsaknad av gmail-konto
            {
                accMail = account.name;
                break;
            }
        }
        return accMail;
    }


    public String getUserIP()
    {

        WifiManager manager = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        int ipAddress = manager.getConnectionInfo().getIpAddress();


        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
        {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try
        {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException e)
        {
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
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
