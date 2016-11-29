package com.peraglobal.km.crawler.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

public class ConverterUtil {
	/**
	 * 利用MD5将输入转换成32位字符串
	 * @param str 转换前的字符串
	 * @return newStr 转换为32位字符串
	 */
	public static String EncoderByMd5(String str){
		String newStr = "";
		try { 
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");  
            messageDigest.update(str.getBytes());  
            newStr = toHex(messageDigest.digest());  
        } catch (Exception e) {  
            e.printStackTrace();  
        } 
		return newStr;
	}
	
	/** 
     * 将16位byte[] 转换为32位String 
     *  
     * @param buffer 
     * @return 
     */
	private static String toHex(byte buffer[]) {  
        StringBuffer sb = new StringBuffer(buffer.length * 2);  
        for (int i = 0; i < buffer.length; i++) {  
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));  
            sb.append(Character.forDigit(buffer[i] & 15, 16));  
        }
        return sb.toString();  
    }
	
	public static String[] listToArray(List<String> list){
		if(list!=null&&list.size()!=0)
		return list.toArray(new String[list.size()]);    
		return null;
	}
	
	public static List<String> arrayToList(String[] arr){
		if(arr!=null&&arr.length!=0)
		return   Arrays.asList(arr);   
		return null;
	}
	public static String arrayToStr(String[] arr){
		StringBuffer sb = new StringBuffer();
		for(String s : arr) {    
			sb.append(s).append(",");
		} 
		return sb.toString();
	}
	public static String[] strToArray(String str){
		if(null!=str&&!"".equals(str)&&str.indexOf(",")!=-1){
			return str.split(",");
		}
		return null;
	}
	public static String encodingToUTF8(String str){
		if(null!=str)
			try {
				if(!"UTF-8".equals(ConverterUtil.getEncoding(str))){
					return new String(str.getBytes(ConverterUtil.getEncoding(str)),"UTF-8");
				}else{
					return str;
				}
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		return null;
	}
    public static String getEncoding(String str) {      
        String encode = "GB2312";      
       try {      
           if (str.equals(new String(str.getBytes(encode), encode))) {      
                String s = encode;      
               return s;      
            }      
        } catch (Exception exception) {      
        }      
        encode = "ISO-8859-1";      
       try {      
           if (str.equals(new String(str.getBytes(encode), encode))) {      
                String s1 = encode;      
               return s1;      
            }      
        } catch (Exception exception1) {      
        }      
        encode = "UTF-8";      
       try {      
           if (str.equals(new String(str.getBytes(encode), encode))) {      
                String s2 = encode;      
               return s2;      
            }      
        } catch (Exception exception2) {      
        }      
        encode = "GBK";      
       try {      
           if (str.equals(new String(str.getBytes(encode), encode))) {      
                String s3 = encode;      
               return s3;      
            }      
        } catch (Exception exception3) {      
        }      
       return "";      
    } 	
}
