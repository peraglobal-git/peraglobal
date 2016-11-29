package com.peraglobal.km.crawler.web.webmagic.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpHost;

import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.processor.PageProcessor;


/**
 * @author code4crafter@gmail.com <br>
 */
public class SinaBlogProcessor implements PageProcessor {

    public static final String URL_LIST = "http://blog\\.sina\\.com\\.cn/s/articlelist_1487828712_0_\\d+\\.html";

    public static final String URL_POST = "http://news\\.163\\.com/13/0802/05/\\w{16}\\.html";
    public static final String URL_ALL_POST = "http://news\\.163\\.com/13/0802/05/\\w{16}_all\\.html";

    private Site site = Site
            .me()
            .setDomain("news.163.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
                    .setHttpProxy(new HttpHost("192.168.112.175",808));
    				//.setHttpProxyPool(Lists.newArrayList(new String[]{"192.168.112.175","808"}, new String[]{"192.168.112.175","808"}));

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex(URL_LIST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"articleList\"]").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
            //文章页
        } else {
            page.putField("title", page.getHtml().xpath("//h1[@id='h1title']"));
            page.putField("content", page.getHtml().xpath("//div[@id='epContentLeft']/p"));
            if (page.getUrl().regex(URL_ALL_POST).match()){
            	
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        WebSpider.create(new SinaBlogProcessor()).addUrl("http://news.163.com/13/0802/05/958I1E330001124J_all.html")
//                .run();
    	
    	String filepath = "C:/Temp/files/27eb2bea-e0cb-4e6f-b708-0cd90d3b7994";
    	File file = new File(filepath);
        try {
        	String str="";
        	if (file.isDirectory()){
        		String[] filelist = file.list();
        		File readfile = null;
        		FileInputStream in = null;
        		for (int i = 0; i < filelist.length; i++) {
                    readfile = new File(filepath + "\\" + filelist[i]);
                    in=new FileInputStream(readfile);
                    // size  为字串的长度 ，这里一次性读完
                    int size=in.available();
                    byte[] buffer=new byte[size];
                    in.read(buffer);
                    in.close();
                    readfile.delete();
                    str = new String(buffer,"utf-8");
                    System.out.println(i+":"+str);
        		}
        		file.delete();
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
