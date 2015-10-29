package com.soctec.soctec.utils;

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

/**
 * Generates encrypted and base64 encoded strings from string-input.
 *
 * Uses org.apache.commons.codec.binary.Base64 library which is under Apache License version 2.0
 * See license in License.txt in the project root.
 *
 * @author Robin Punell
 * @version 1.0
 */
public class Encryptor
{
    private String keyString = "SocTec-SocTec-SocTec";
    private Cipher ecipher;
    private Cipher decipher;
    private DESKeySpec keySpec;
    private SecretKeyFactory keyFactory;
    private  SecretKey key;

    /**
     * Constructs the encryptor.
     * Creates the needed keys and cipher for encryption.
     *
     */
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
    /**
     * Encrypts and Base64 encodes a string
     * @param str the string to encrypt
     * @return a encrypted and Base64 encoded string
     */
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
