package com.peraglobal.km.crawler.db.model;

import static com.peraglobal.km.crawler.db.biz.DataImportException.wrapAndThrow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peraglobal.km.crawler.db.biz.DataImportException;

public class DBSpiderRecord {
	private static final Logger log = LoggerFactory.getLogger(DBSpiderRecord.class);
     /**
      * 爬虫log是否存在
      * @param filePath
      * @return
      */
     public static boolean existsRecord(String filePath){
    	  File fie = new File(filePath);
	  	   if(!fie.exists()){
	  		   return false;
	  	   }
	  	   return true;
     }
     public static boolean removeRecord(String filePath,String taskId){
    	  File fie = new File(filePath);
	  	   if(fie.exists()){
	  		   fie.delete();
	  		   return true;
	  	   }
	  	   return false;
    }
     
   /**
    * 删除断点日志
    * @param filePath
    * @param state
    * @return
    *  format: taskId entityName firstSize rowNum
    *  example: 2015-06-11 16:35:09   xxx xxx xxx xxx 
    * 删除一行内容（java本身没有删除的方法，本方法通过先读取文件的内容（需删除的行数除外），放到list中，再重新写入）
    
     public static  boolean removeLine(String filePath,String taskId){
    	 
    	   boolean flag = false;
    	 	File file =  new File(filePath);
    	 	 if(!file.exists()){
    	 		 return flag;
    	 	}
    		    BufferedReader br=null;
        	    BufferedWriter bw=null; 
    	 	try {
				br = new BufferedReader(new FileReader(file));
				String str = null;
				String[] strs = null;
				List<String> tmpListStr = new ArrayList<String>();
	    	    while( (str=br.readLine()) != null ){
	    	    	if(!"".equals(str)&&null!=str){
	    				strs = str.split("\t");
	    				for(String st:strs){
	    					System.out.println("["+st+"]");
	    				}
	    				if(!strs[1].equals(taskId)){
	    					tmpListStr.add(str);
	    				}
	    	    	}
	    	    }
	    	   bw = new BufferedWriter(new FileWriter(file));
	    	    for( int i=0;i<tmpListStr.size();i++ ){
	    	    	bw.write(tmpListStr.get(i).toString());
	    	    	bw.newLine();
	    	    }
	    	    flag = true;
    		} catch (FileNotFoundException e) {
    			 wrapAndThrow(DataImportException.SEVERE, e,"文件读取路径错误");
			} catch (IOException e) {
				wrapAndThrow(DataImportException.SEVERE, e,"读取一行数据时出错");
			}finally{
	    	    try {
	    	    	if(br!=null){
	    	    		br.close();
	    	    	}
	    	    	if(bw!=null){
						bw.flush();
						bw.close();
	    	    	}
				} catch (IOException e) {
					wrapAndThrow(DataImportException.SEVERE, e,"读取一行数据时出错");
				}
	    	   
			}
	  	   return flag;
     }*/
     /**
      * 读取log或者txt信息
      * @param filePath
      * @return
      */
     public static List<String> readRecord(String filePath) {
      List<String> list = null;
      try {
    	   list = new ArrayList<String>();
	       FileInputStream is = new FileInputStream(filePath);
	       InputStreamReader isr = new InputStreamReader(is);
	       BufferedReader br = new BufferedReader(isr);
	       String line="";
       try {
	        while ((line = br.readLine()) != null) {
		         if (line.equals(""))
		          continue;
		         else
		          list.add(line);
		     }
       } catch (IOException e) {
    	  wrapAndThrow(DataImportException.SEVERE, e,"读取一行数据时出错");
       }
      } catch (FileNotFoundException e) {
    	  wrapAndThrow(DataImportException.SEVERE, e,"文件读取路径错误");
      }
      return list;
     }
     /*
     public static  void updateLog(String filePath,String taskId,String message){
	    //  if(SpiderLogger.removeLine(filePath, taskId));	{
	    	  SpiderLogger.writeLog(filePath,message);
	    	  System.out.println(message);
	    //  }
     }*/
     /**
      * 文件并写入内容
      * 
      * @param filePath
      * @param fileName
      * @param msg
      */
     public static void writeRecord(String filePath,String msg) {
      try {
    	  File file = new File(filePath.substring(0,filePath.lastIndexOf(File.separator)+1));
    	  if(file.mkdirs()){}
    	//创建文件输出流
			FileOutputStream output = new FileOutputStream(new File(filePath));
			String outPutStr = new String(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
	        .format(new Date())+ msg);
			output.write(outPutStr.getBytes());
			output.close();
			log.info(outPutStr);
			
      } catch (IOException e) {
    	  e.printStackTrace();
      }
     }
     public static final String LOGFILETYPE = ".log";
}