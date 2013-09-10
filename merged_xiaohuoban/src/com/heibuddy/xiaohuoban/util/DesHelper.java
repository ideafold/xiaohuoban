package com.heibuddy.xiaohuoban.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public final class DesHelper {
    public static class DESCrypto {
        private static final int IV_SIZE_DEFAULT = 8;
        private String password;
        private byte[] iv;
        
        public DESCrypto(final String password, final String iv) {
            checkAndSetPassword(password);
            checkAndSetIV(iv);
        }

        private void checkAndSetIV(final String iv) {
        	try {
				this.iv = iv.getBytes(DesHelper.ENC_UTF8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
        }

        private void checkAndSetPassword(final String password) {
            if (password.isEmpty()) {
                this.password = DesHelper.getRandomString();
            } else {
                this.password = password;
            }
        }

        public byte[] decrypt(final byte[] encryptedData) {
            return process(encryptedData, Cipher.DECRYPT_MODE);
        }

        public String decrypt(final String text) {
            final byte[] allEncryptedData = DesHelper.base64Decode(text);
            
            /*
            byte[] iv = new byte[DESCrypto.IV_SIZE_DEFAULT];
            System.arraycopy(allEncryptedData, 0, iv, 0, DESCrypto.IV_SIZE_DEFAULT);
            
            byte[] encryptedData = new byte[allEncryptedData.length-DESCrypto.IV_SIZE_DEFAULT];
            System.arraycopy(allEncryptedData, DESCrypto.IV_SIZE_DEFAULT, encryptedData, 0, allEncryptedData.length-DESCrypto.IV_SIZE_DEFAULT);
            */
            final byte[] data = decrypt(allEncryptedData);
            return DesHelper.getString(data);
        }

        public byte[] encrypt(final byte[] data) {
            return process(data, Cipher.ENCRYPT_MODE);
        }

        public String encrypt(final String text) {
            final byte[] data = DesHelper.getRawBytes(text);
            final byte[] encryptedData = encrypt(data);
            /*
            byte[] result = new byte[this.iv.length + encryptedData.length];
            System.arraycopy(this.iv, 0, result, 0, this.iv.length);
            System.arraycopy(encryptedData, 0, result, this.iv.length, encryptedData.length);
            */
            return DesHelper.base64Encode(encryptedData);
        }

        public byte[] getIv() {
            return this.iv;
        }

        public String getPassword() {
            return this.password;
        }

        private byte[] process(final byte[] data, final int mode) {
            return process(data, mode, this.password, this.iv);
        }

        static byte[] process(final byte[] data, final int mode, final String password, final byte[] iv) {

            try{
            	final MessageDigest md = MessageDigest.getInstance("md5");
            	final byte[] digestOfPassword = md.digest(password
            			.getBytes("utf-8"));
            	final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            	for (int j = 0, k = 16; j < 8;) {
            		keyBytes[k++] = keyBytes[j++];
            	}
            	final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            	
                final Cipher cipher = Cipher
                        .getInstance("DESede/CBC/PKCS5Padding");
                final IvParameterSpec ivParams = new IvParameterSpec(iv);
                cipher.init(mode, key, ivParams);
                return cipher.doFinal(data);
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        
        public void setPassword(final String password) {
            checkAndSetPassword(password);
        }
    }
    
    public static final String TAG = DesHelper.class.getSimpleName();

    public static final String ENC_UTF8 = "UTF-8";

    static byte[] base64Decode(final String text) {
    	byte[] res = null;
    	try {
    		res = Base64.decodeBase64(text.getBytes());
    	}
    	catch (Exception e) {
    		e.printStackTrace();
        }
        return res; 
    }

    static String base64Encode(final byte[] data) {
    	String res = null;
    	try {
    		res = new String(Base64.encodeBase64(data));
    	}
    	catch (Exception e) {
    		e.printStackTrace();
        }
        return res;
    }

    static byte[] getRandomBytes(final int size) {
        final SecureRandom random = new SecureRandom();
        final byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return bytes;
    }

    static String getRandomString() {
        final SecureRandom random = new SecureRandom();
        return String.valueOf(random.nextLong());
    }

    static byte[] getRawBytes(final String text) {
        try {
            return text.getBytes(DesHelper.ENC_UTF8);
        } catch (final UnsupportedEncodingException e) {
            return text.getBytes();
        }
    }

    static String getString(final byte[] data) {
        try {
            return new String(data, DesHelper.ENC_UTF8);
        } catch (final UnsupportedEncodingException e) {
            return new String(data);
        }
    }
    
}