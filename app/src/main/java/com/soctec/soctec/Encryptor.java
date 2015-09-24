package com.soctec.soctec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.binary.Base64;

public class Encryptor
{
    private String keyString = "SocTec-SocTec-SocTec";
    private Cipher ecipher;
    private Cipher decipher;

    public Encryptor()
    {
        DESKeySpec keySpec = new DESKeySpec(keyString.getBytes("UTF8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        ecipher = Cipher.getInstance("DES");
        decipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        decipher.init(Cipher.DECRYPT_MODE, key);

    }

    public String encrypt(String str)
    {
        byte[] enc = ecipher.doFinal(str.getBytes("UTF-8"));
        return new String(Base64.encodeBase64(enc));
    }

    public String decrypt(String str)
    {
        //Unfinished method, not needed atm.
        return str;
    }

}
