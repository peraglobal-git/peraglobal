package com.peraglobal.km.crawler.db.biz;

import org.apache.http.client.methods.HttpGet;

public class HttpDownloadHandler {
	
	public static HttpGet getHttpGet(String url){
		return  new HttpGet(url);
	}
	public static void releaseConnection(HttpGet httpget){
		httpget.releaseConnection();
	}
}
