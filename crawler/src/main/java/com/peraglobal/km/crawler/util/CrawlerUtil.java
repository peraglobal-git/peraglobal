package com.peraglobal.km.crawler.util;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CrawlerUtil {
	/**
	 * 
	 * @param taskId
	 * @param genre
	 * @return Map<optFlag,Map<type,List<String>>>
	 */
	public static Map<String,Map<String,List<String>>> getRules(String taskId,String genre){
		return null;
	}
	/**
	 * 主键生成策略uuid
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
