package com.peraglobal.km.crawler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MarkBreakpoint {
	
	/**
	 * 获取断点页数
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static String readBreakPage(String filePath){
		File f = new File(filePath);
		if (!f.exists()) {
			return "1";
		}
		String result = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(f);
			bufferedReader = new BufferedReader(fileReader);
			try {
				String read = null;
				while ((read = bufferedReader.readLine()) != null) {
					result = read;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fileReader != null) {
					fileReader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		System.out.println("读取出来的文件内容是：" + "\r\n" + result);
		return result;
	}
	/**
	 * 记录断点页数
	 * @param path
	 * @param pageSize
	 */
	public static void writeBreakPage(String filePath, String pageSize) {
		try {
			File floder = new File(filePath.substring(0, filePath.lastIndexOf("/")));
			if(!floder.exists()){
				floder.mkdirs();
			}
			File f = new File(filePath);
			if (!f.exists()) {
				f.createNewFile();// 不存在则创建
			}
			FileOutputStream o = null;
			o = new FileOutputStream(f);
			o.write(pageSize.getBytes("GBK"));
			o.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
		MarkBreakpoint p = new MarkBreakpoint();
		String path = "D:/sdc/extractJob/dd/2312.txt";
		p.writeBreakPage(path, "223");
		System.out.println(p.readBreakPage(path));
	}
}
