package com.peraglobal.km.crawler.web.webmagic.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSFile;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Request;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.Task;
import com.peraglobal.km.crawler.web.webmagic.selector.PlainText;
import com.peraglobal.km.crawler.web.webmagic.utils.HttpConstant;
import com.peraglobal.km.crawler.web.webmagic.utils.UrlUtils;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.core.utils.AppConfigUtils;


/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class HttpClientFileDownloader extends AbstractDownloader {

//	private FileTemplate fileTemplate;
	private  String folderPath = ""; 
	/*private Mongo db;
	private DB mydb;*/
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();
    
    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();
    @Resource
    private MgDatumBiz mgDatumBiz;
    public HttpClientFileDownloader(){
    	this.folderPath = AppConfigUtils.get("conf.filePath");
    	mgDatumBiz = AdminConfigUtils.getBean("mgDatumBiz");
    	/*try {
			db = new Mongo(AppConfigUtils.get("km.mongo.address"), Integer.valueOf(AppConfigUtils.get("km.mongo.port")));
			mydb = db.getDB(AppConfigUtils.get("km.mongo.dbName"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
    /*public void closeMongo(){
    	db.close();
    }*/
    
    private CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    @Override
    public Page download(Request request, Task task) {
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        logger.info("downloading page {}", request.getUrl());
        CloseableHttpResponse httpResponse = null;
        int statusCode=0;
        try {
        	CloseableHttpClient httpclient = getHttpClient(site);  
            HttpGet httpget = new HttpGet(request.getUrl());
            httpResponse = httpclient.execute(httpget);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, charset, httpResponse, task);
                onSuccess(request);
                return page;
            } else {
                logger.warn("code error " + statusCode + "\t" + request.getUrl());
                return null;
            }
        } catch (IOException e) {
            logger.warn("download page " + request.getUrl() + " error", e);
            if (site.getCycleRetryTimes() > 0) {
                return addToCycleRetry(request, site);
            }
            onError(request);
            return null;
        } finally {
        	request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        httpClientGenerator.setPoolSize(thread);
    }

    protected boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(site.getTimeOut())
                .setSocketTimeout(site.getTimeOut())
                .setConnectTimeout(site.getTimeOut())
                .setCookieSpec(CookieSpecs.BEST_MATCH);
		if (site.getHttpProxyPool().isEnable()) {
			HttpHost host = site.getHttpProxyFromPool();
			requestConfigBuilder.setProxy(host);
			request.putExtra(Request.PROXY, host);
		}
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    protected RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (nameValuePair.length > 0) {
                requestBuilder.addParameters(nameValuePair);
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    protected Page handleResponse(Request request, String charset, HttpResponse httpResponse, Task task) throws IOException {
    	String fileName = "";
        String fileType = "";    	
    	
    	InputStream input = getContent(charset, httpResponse);
    	Header[] contentHeader = httpResponse.getHeaders("Content-Disposition");
        //Content-Disposition: attachment; filename=%e6%97%a0%e4%be%b5%e5%ae%b3%e9%92%bb%e4%ba%95%e6%b6%b2%e6%8a%80%e6%9c%af%e7%a0%94%e7%a9%b6%e7%8e%b0%e7%8a%b6%e5%8f%8a%e5%b1%95.pdf
        String _folderPath = this.folderPath + "/" + task.getUUID() + "/";
        File floder = new File(_folderPath);
        if (!floder.exists()) {
        	floder.mkdirs();
        }
        
        for(int i = 0; i < contentHeader.length; i++){
        	Header h = contentHeader[i];
        	fileType = h.getValue().substring(h.getValue().lastIndexOf(".")+1, h.getValue().length());
//        	System.out.println("fileType is:"+ fileType);
        }
      //取头信息
    	
    	if(fileType.equals("")){
    		Header[] headers = httpResponse.getAllHeaders();
	    	for(int i=0;i<headers.length;i++) {
	    		System.out.println(headers[i].getName() +"=="+ headers[i].getValue());
	    		if(headers[i].getName().equals("Content-Type")){
	    			fileType = headers[i].getValue().substring(headers[i].getValue().indexOf("/")+1,headers[i].getValue().length());
	    			break;
	    		}
	    	}
    	}
        fileName = ConverterUtil.EncoderByMd5(request.getUrl());
//        GridFS myFS = null;
//        GridFSInputFile inputFile = null;
        GridFSFile inputFile = null;
        try {
			/*myFS = new GridFS(mydb);
			inputFile = myFS.createFile(input, fileName + "." + fileType);
			inputFile.save();*/
        	inputFile = mgDatumBiz.storeFile(input, fileName + "." + fileType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //        fileName = fileName + "." + fileType;
        // 将文件上传至文件服务器
        // 第二个参数是在文件服务器建的子目录，第一级你可以写成项目的目录，路径前后不用带/
        /*FileInfo fileInfo = fileTemplate.uploadFile(content, "SDC/"+task.getUUID(), fileName + "." + fileType);
		String fileId = fileInfo.getId();
		InputStream inputStream = inputStream = fileTemplate.downloadFile(fileId);
        */
        // 以下是将文件存储存储在本地的逻辑=======开始
        /*File file = new File(_folderPath + fileName + "." + fileType);
        FileOutputStream output = null;
        try {
        	output = new FileOutputStream(file);
        	IOUtils.copy(input, output);  
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {  
            // 关闭低层流。
			IOUtils.closeQuietly(output); 
			IOUtils.closeQuietly(input);  
        }*/
        // 以上是将文件存储存储在本地的逻辑---结束
        // 将持久化到数据库的信息赋值给Page resultItems
        Page page = new Page();
        page.setRawText("");
        page.putField("filePath",inputFile.getId().toString());
    	page.putField("fileName", fileName);
    	page.putField("fileType", "." + fileType);
        page.setNeedCycleRetry(false);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return page;
    }

    protected InputStream getContent(String charset, HttpResponse httpResponse) throws IOException {
    	HttpEntity entity = httpResponse.getEntity();
        InputStream in = entity.getContent();
        return in;
    }

    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1、encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            logger.debug("Auto get charset: {}", charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.debug("Auto get charset: {}", charset);
        // 3、todo use tools as cpdetector for content decode
        return charset;
    }
    
}
