package com.peraglobal.km.crawler.db.biz;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

	/**
	 * 去掉url中的路径，留下请求参数部分
	 * @param strURL url地址
	 * @return url请求参数部分
	 */
	private static String truncateUrl(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;
		strURL = strURL.trim();
		if (strURL.length() > 1) {
			arrSplit = strURL.split("[?]");
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}
		return strAllParam;
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 * @param URL url地址
	 * @return url请求参数部分
	 */
	public static Map<String,Object> urlRequestParam(String url) {
		Map<String, Object> mapRequest = new HashMap<String, Object>();
		String[] arrSplit = null;
		String strUrlParam = truncateUrl(url);
		if (strUrlParam == null) {
			return mapRequest;
		}
		// 每个键值为一组
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = strSplit.split("[=]");
			// 解析出键值
			if (arrSplitEqual.length > 0) {
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
			} 
		}
		return mapRequest;
	}

}
