package com.peraglobal.km.crawler.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * MD5 工具类
 * @author wangxiaoming
 * @date 2016-1-18
 */
public class CrawlerMD5Utils {
	private static final String ENCRYPT_TYPE = "MD5";
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlerMD5Utils.class);
	
	/**
	 * 对特定字符串进行md5加密
	 * @param source 要加密的字符串
	 * @return 加密后的MD5值
	 *
	 * @author wangxiaoming
	 * @date 2016-1-18
	 */
	public static String encrypt(String source){
		return new String(HexUtils.encode(source.getBytes()));
		
	}
	/**
	 * 暗文解密
	 * @param source
	 * @return
	 * 
	 * @author wangxiaoming
	 * @date 2016-1-18
	 */
	public static String Decryption(String source){
		return new String(HexUtils.decode(source));
		
	}
	public static void main(String[] args) {
		String abc = "abc士大夫士大夫水电费";
		String s = new String(HexUtils.encode(abc.getBytes()));
		//String s = encrypt(abc);
		System.out.println(s);
		System.out.println("5555"+new String(HexUtils.decode(s)));
	}
	
	static class HexUtils{

		    private static final char[] HEX = {
		        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
		    };

		    public static char[] encode(byte[] bytes) {
		        final int nBytes = bytes.length;
		        char[] result = new char[2*nBytes];

		        int j = 0;
		        for (int i=0; i < nBytes; i++) {
		            // Char for top 4 bits
		            result[j++] = HEX[(0xF0 & bytes[i]) >>> 4 ];
		            // Bottom 4
		            result[j++] = HEX[(0x0F & bytes[i])];
		        }

		        return result;
		    }

		    public static byte[] decode(CharSequence s) {
		        int nChars = s.length();

		        if (nChars % 2 != 0) {
		            throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
		        }

		        byte[] result = new byte[nChars / 2];

		        for (int i = 0; i < nChars; i += 2) {
		            int msb = Character.digit(s.charAt(i), 16);
		            int lsb = Character.digit(s.charAt(i+1), 16);

		            if (msb < 0 || lsb < 0) {
		                throw new IllegalArgumentException("Non-hex character in input: " + s);
		            }
		            result[i / 2] = (byte) ((msb << 4) | lsb);
		        }
		        return result;
		    }
	}
}
