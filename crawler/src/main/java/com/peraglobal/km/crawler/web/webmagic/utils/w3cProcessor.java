package com.peraglobal.km.crawler.web.webmagic.utils;

import org.apache.http.HttpHost;

import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.WebSpider;
import com.peraglobal.km.crawler.web.webmagic.pipeline.FilePipeline;
import com.peraglobal.km.crawler.web.webmagic.processor.PageProcessor;


/**
 * @author code4crafter@gmail.com <br>
 */
public class w3cProcessor implements PageProcessor {

    public static final String URL_LIST = "https://www.w3.org/2005/11/Translations/Lists/ListLang-zh-hans.html";

    public static final String URL_POST = "http://www.w3.org/TR/2008/REC-WCAG20-20081211/";

    private Site site = Site
            .me()
            .setDomain("www.w3.org")
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
                    //.setHttpProxyPool(Lists.newArrayList(new String[]{"192.168.112.175","808"}, new String[]{"192.168.112.175","808"}));

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex(URL_LIST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@class=\"translationList\"]//dl").links().all());
            System.out.println(page.getTargetRequests().size());
            //文章页
        } else {
//        	System.out.print(page.getHtml());
            page.putField("title", page.getHtml().xpath("//title/text()"));
            page.putField("", page.getHtml());
            
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        WebSpider.create(new w3cProcessor())
        .addUrl(URL_POST)
        .addPipeline(new FilePipeline("C:/Temp/files/"))
                .run();
    }
}
