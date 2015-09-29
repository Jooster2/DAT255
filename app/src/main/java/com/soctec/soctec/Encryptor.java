package com.soctec.soctec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Encryptor
{
    private String keyString = "SocTec-SocTec-SocTec";
    private Cipher ecipher;
    private Cipher decipher;
    private DESKeySpec keySpec;
    private SecretKeyFactory keyFactory;
    private  SecretKey key;

    public Encryptor()
    {

        try
        {
            keySpec = new DESKeySpec(keyString.getBytes("UTF8"));
            keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(keySpec);

            ecipher = Cipher.getInstance("DES");
            decipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            decipher.init(Cipher.DECRYPT_MODE, key);

        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException  e)
        {
            e.printStackTrace();
        }
    }

    public String encrypt(String str)
    {
        byte[] enc = new byte[0];
        try
        {
            enc = ecipher.doFinal(str.getBytes("UTF-8"));

        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(enc));
    }

    public String decrypt(String str)
    {
        //Unfinished method, not needed atm.
        return str;
    }

}
