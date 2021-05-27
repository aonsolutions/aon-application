package com.code.aon.common.util;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class CryptoUtil {

	private static String DIGEST_ALGORITHM = "MD5";
	private static String KEY_ALGORITHM = "DESede";
	private static String CHARSET = "UTF-8";

	public static String encrypt(String key, String value) {
        String encryptedValue = value;
        if (StringUtils.isNotBlank(value)) {
	        try {
	            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
	            byte[] keyDigest = messageDigest.digest(key.getBytes(CHARSET));
	
	            SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(keyDigest, 24), KEY_ALGORITHM);
	            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
	            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	
	            byte[] result = Base64.encodeBase64(cipher.doFinal(value.getBytes(CHARSET)));
	            encryptedValue = new String(result);
	        } catch (Exception ex) {
	        }
        }
        return encryptedValue;
    }

    public static String decrypt(String key, String encryptedValue) {
        String value = encryptedValue;
        if (StringUtils.isNotBlank(encryptedValue)) {
	        try {
	            byte[] message = Base64.decodeBase64(encryptedValue.getBytes(CHARSET));
	            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
	            byte[] keyDigest = messageDigest.digest(key.getBytes(CHARSET));
	
	            SecretKey secretKey = new SecretKeySpec(Arrays.copyOf(keyDigest, 24), KEY_ALGORITHM);
	            Cipher decipher = Cipher.getInstance(KEY_ALGORITHM);
	            decipher.init(Cipher.DECRYPT_MODE, secretKey);
	
	            byte[] result = decipher.doFinal(message);
	            value = new String(result, CHARSET);
	        } catch (Exception ex) {
	        }
        }
        return value;
    }

}