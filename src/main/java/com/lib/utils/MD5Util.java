package com.lib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
  
public class MD5Util {  
      
    /** 
     * 将源字符串使用MD5加密为字节数组 
     * @param source 
     * @return 
     */  
    public static byte[] encode2bytes(String source) {  
        byte[] result = null;  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.reset();  
            md.update(source.getBytes("UTF-8"));  
            result = md.digest();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
          
        return result;  
    }  
      
    /** 
     * 将源字符串使用MD5加密为32位16进制数 
     * @param source 
     * @return 
     */  
    public static String encode2hex(String source) {  
        byte[] data = encode2bytes(source);  
  
        StringBuffer hexString = new StringBuffer();  
        for (int i = 0; i < data.length; i++) {  
            String hex = Integer.toHexString(0xff & data[i]);  
              
            if (hex.length() == 1) {  
                hexString.append('0');  
            }  
              
            hexString.append(hex);  
        }  
          
        return hexString.toString();  
    }  
      
    /** 
     * 验证字符串是否匹配 
     * @param unknown 待验证的字符串 
     * @param okHex 使用MD5加密过的16进制字符串 
     * @return  匹配返回true，不匹配返回false 
     */  
    public static boolean validate(String unknown , String okHex) {  
        return okHex.equals(encode2hex(unknown));  
    }  
//利用MD5算法对文档进行去重校验  
public static String getFileMD5(File file) {
    MessageDigest digest = null;
    FileInputStream in = null;
    byte buffer[] = new byte[8192];
    int len;
    try {
        digest =MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer)) != -1) {
            digest.update(buffer, 0, len);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    } finally {
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  
    }
}
