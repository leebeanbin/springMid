package com.sparta.areadevelopment.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 이메일 인증 암호화 클래스
 */
public class SHA256Util {
    private SHA256Util() {}
    public static String getEncrypt(String source, String salt) {
        return getEncrypt(source, salt.getBytes());
    }

    /**
     * 암호화 메서드
     * @param source
     * @param salt
     * @return
     */
    public static String getEncrypt(String source, byte[] salt) {
        String result = "";
        byte[] a = source.getBytes();
        byte[] bytes = new byte[a.length + salt.length];
        System.arraycopy(a, 0, bytes, 0, a.length);
        System.arraycopy(salt, 0, bytes, a.length, salt.length);
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 256, 16).substring(1));
            }
            result = sb.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return result;
    }
    public static String createSalt(){
        Random random = new Random();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < salt.length; i++) {
            sb.append(String.format("%02x", salt[i]));
        }
        return sb.toString();
    }
}
