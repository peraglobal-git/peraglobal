package com.peraglobal.km.crawler.web.webmagic.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.peraglobal.km.crawler.util.CrawlerUtil;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Request;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.Task;
import com.peraglobal.km.crawler.web.webmagic.selector.PlainText;
import com.peraglobal.km.crawler.web.webmagic.utils.HttpConstant;
import com.peraglobal.km.crawler.web.webmagic.utils.UrlUtils;
import com.peraglobal.pdp.common.id.IDGenerate;


/**
 * The http downloader based on HttpClient.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class HttpClientDownloader extends AbstractDownloader {

	private String set_cookie ="";
	public HttpClientDownloader(Site site){
		try {
			if(site.getAttachProperty() == null || site.getAttachProperty().getDoLoginAction() == null || site.getAttachProperty().getDoLoginAction().equals("")) return;
			AttachProperty attachProperty = site.getAttachProperty();
			String loginUrl = attachProperty.getLoginurl();
			String doLoginUrl = attachProperty.getDoLoginAction();
			
			HttpGet request = new HttpGet(loginUrl);
			HttpResponse response = getHttpClient(site).execute(request);
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String temp = "";  
		    while ((temp = br.readLine()) != null) {  
		        String str = new String(temp.getBytes(), "utf-8");  
		        result.append(str);  
		    } 
//			System.out.println("result:"+result.toString()); 
			String html = result.toString();
			request.releaseConnection();
			
			Document doc = Jsoup.parse(html);//将纯HTML文本转化成具有结构的Document对象
			Element formEl = doc.getElementById("form1");//获取登录form
			Elements inputs = formEl.getElementsByTag("input");//获取form底下的所有input
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for(Element el: inputs) {//loop循环每一个input
			    String elName = el.attr("name");//获取input的name属性
			    String elValue = el.attr("value");//获取input的value属性
			    String elType = el.attr("type");//获取input的type属性
			    
			    if(elName.equals(attachProperty.getUsernameKey())) {
			        elValue = attachProperty.getUsernameValue();//如果属性名是UserName，就将值设置成用户定义的值
			    }else if(elName.equals(attachProperty.getPasswordKey())) {
			        elValue = attachProperty.getPasswordValue();//如果属性名是Passwd，就将值设置为用户定义的值
			    }
			    
			    if(!elName.equals("button") && !elType.equals("submit") && !elName.equals("") && !elValue.equals("")) {//此外有些button是input应排除
			    	formparams.add(new BasicNameValuePair(elName, elValue));//创建键值对加入list
			    }
			}
			
			//新建【登录提交】请求  
			HttpPost httppost = new HttpPost(doLoginUrl);  
			httppost.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));
  
			//处理请求，得到响应  
			HttpResponse doLoginResponse = getHttpClient(site).execute(httppost);  
     
			set_cookie = doLoginResponse.getFirstHeader("Set-Cookie").getValue();  
			  
			//打印Cookie值  
			System.out.println(set_cookie.substring(0,set_cookie.indexOf(";")));  
			httppost.releaseConnection();
			httppost.abort();
			//打印返回的结果  
			/*HttpEntity entity = doLoginResponse.getEntity();
			
			StringBuilder result1 = new StringBuilder();  
			if (entity != null) {  
			    InputStream instream = entity.getContent();  
			    BufferedReader br1 = new BufferedReader(new InputStreamReader(instream));  
			    String temp1 = "";  
			    while ((temp = br1.readLine()) != null) {  
			        String str = new String(temp1.getBytes(), "utf-8");  
			        result1.append(str);  
			    }  
			}  
			System.out.println(result);*/
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private HttpClientGenerator httpClientGenerator = new HttpClientGenerator();

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
        	if(!set_cookie.equals("")){
        		set_cookie = set_cookie.substring(0,set_cookie.indexOf(";"));
            	headers.put("Cookie", set_cookie);
        		headers.put(set_cookie.split("=")[0].trim(), set_cookie.split("=")[1].trim());
        		set_cookie = "";
        	}
        	HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpResponse = getHttpClient(site).execute(httpUriRequest);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            if (statusAccept(acceptStatCode, statusCode)) {
                Page page = handleResponse(request, charset, httpResponse, task);
//                System.out.println(page.getRawText());
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

    protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) throws UnsupportedEncodingException {
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
		}else if(site.getHttpProxy()!=null){
			HttpHost host = site.getHttpProxy();
			requestConfigBuilder.setProxy(host);
			request.putExtra(Request.PROXY, host);
		}
        requestBuilder.setConfig(requestConfigBuilder.build());
        //艾迪留学网 分页请求 start
        /*if(request.getUrl().contains("/list/Default.aspx?1=1")){
        	List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        	formparams.add(new BasicNameValuePair("__EVENTTARGET", "linkButtonNextPage1"));
            formparams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
            formparams.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwUKLTM3MDE5MDg3Mg9kFgICAw9kFhYCBw88KwALAQAPFgweCVBhZ2VDb3VudAIBHghEYXRhS2V5cxYAHhBDdXJyZW50UGFnZUluZGV4Zh4QVmlydHVhbEl0ZW1Db3VudAI7HgtfIUl0ZW1Db3VudAI7HhVfIURhdGFTb3VyY2VJdGVtQ291bnQCO2QWAmYPZBZ2AgIPZBYCZg9kFgJmDxUCHGh0dHA6Ly93d3cudXRhc2NoaW5hLm5ldC5jbi8V5aGU5pav6ams5bC85Lqa5aSn5a2mZAIDD2QWAmYPZBYCZg8VAjhodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTYwOAzpgpPov6rlpKflraZkAgQPZBYCZg9kFgJmDxUCDnd3dy5paXQub3JnLmNuG+e+juWbveS8iuWIqeivuueQhuW3peWkp+WtpmQCBQ9kFgJmD2QWAmYPFQI2aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD00D+iOq+e6s+S7gOWkp+WtpmQCBg9kFgJmD2QWAmYPFQI3aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD0yMBLnu7Tlt57lhaznq4vkuK3lraZkAgcPZBYCZg9kFgJmDxUCNWh0dHA6Ly93d3cudWItY2hpbmEuY24vdWItY291cnNlcy8xMjMtY29uc3VsdGluZy5odG1sEuW3tOaLieeRnueJueWkp+WtpmQCCA9kFgJmD2QWAmYPFQI4aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD01OTMP5piG5aOr5YWw5aSn5a2mZAIJD2QWAmYPZBYCZg8VAjhodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTU3MBXln4Pov6rmlq/np5HmloflpKflraZkAgoPZBYCZg9kFgJmDxUCOGh0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9ODIxFee+juWbveadnOavlOWF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6I6x5oGp5a2m6ZmiZAIhD2QWAmYPZBYCZg8VAjdodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTgxD+a7kemTgeWNouWkp+WtpmQCIg9kFgJmD2QWAmYPFQI4aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD00NzQY6Iux5Zu95YmR5qGl5pWZ6IKy6ZuG5ZuiZAIjD2QWAmYPZBYCZg8VAjdodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTk5D+W4g+mygeWFi+Wkp+WtpmQCJA9kFgJmD2QWAmYPFQIcaHR0cDovL3d3dy5tdXJkb2NoLWNoaW5hLmNvbQ/ojqvpgZPlhYvlpKflraZkAiUPZBYCZg9kFgJmDxUCN2h0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9NzYM5aWz55qH5aSn5a2mZAImD2QWAmYPZBYCZg8VAjdodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTk3EumYv+WwlOS8r+WhlOWkp+WtpmQCJw9kFgJmD2QWAmYPFQI3aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD04Nw/lpJrkvKblpJrlpKflraZkAigPZBYCZg9kFgJmDxUCN2h0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9ODUc5LiN5YiX6aKgIOWTpeS8puavlOS6muWkp+WtpmQCKQ9kFgJmD2QWAmYPFQI3aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD0xMhXmlrDljZflqIHlsJTlo6vlpKflraZkAioPZBYCZg9kFgJmDxUCN2h0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9OTAM57qm5YWL5aSn5a2mZAIrD2QWAmYPZBYCZg8VAjhodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTE5NiTlqIHlu4nlronmoLzmi4nmlq/ogYzkuJrmlZnogrLlrabpmaJkAiwPZBYCZg9kFgJmDxUCN2h0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9ODgP5Zyj546b5Li95aSn5a2mZAItD2QWAmYPZBYCZg8VAjdodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTc5DOa4qeiOjuWkp+WtpmQCLg9kFgJmD2QWAmYPFQI3aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD04NhLlurfnp5Hov6rkuprlpKflraZkAi8PZBYCZg9kFgJmDxUCOGh0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9MTA1D+m6puWQieWwlOWkp+WtpmQCMA9kFgJmD2QWAmYPFQI4aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD0xMDgS5Y2h5bCU5Yqg6YeM5aSn5a2mZAIxD2QWAmYPZBYCZg8VAjdodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTgzEuilv+mWgOiPsuaymeWkp+WtpmQCMg9kFgJmD2QWAmYPFQI3aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD0xORXloqjlsJTmnKznkIblt6XlrabpmaJkAjMPZBYCZg9kFgJmDxUCN2h0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9ODkP5Zyt5bCU5aSr5aSn5a2mZAI0D2QWAmYPZBYCZg8VAjhodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTEwMBXokKjmlq/ljaHliIfmuKnlpKflraZkAjUPZBYCZg9kFgJmDxUCOGh0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9MTAxD+iJvuexs+WIqeWNoeWwlGQCNg9kFgJmD2QWAmYPFQI3aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD03NxXpqazmi4nmlq/mr5TnurPlpKflraZkAjcPZBYCZg9kFgJmDxUCN2h0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9ODQV6bqm5YWL6ams5pav54m55aSn5a2mZAI4D2QWAmYPZBYCZg8VAjhodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTIwOBLpn7PpopHlt6XnqIvlrabpmaJkAjkPZBYCZg9kFgJmDxUCOGh0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9MTA5DOilv+WuieWkp+eVpWQCOg9kFgJmD2QWAmYPFQI4aHR0cDovL3NjaG9vbC5ibG9nLmVkdWdsb2JhbC5jb20vZGVmYXVsdC5hc3B4P2Jsb2dpZD0xOTMY5paw5bee5pWZ6IKy5Y+K5Z+56K6t572yZAI7D2QWAmYPZBYCZg8VAjhodHRwOi8vc2Nob29sLmJsb2cuZWR1Z2xvYmFsLmNvbS9kZWZhdWx0LmFzcHg/YmxvZ2lkPTE5NB7lt7Tmi4nnkZ7nibnlpKflrabmgonlsLzmoKHljLpkAjwPZBYCZg9kFgJmDxUCOGh0dHA6Ly9zY2hvb2wuYmxvZy5lZHVnbG9iYWwuY29tL2RlZmF1bHQuYXNweD9ibG9naWQ9MTk4IeWMl+W4g+mHjOaWr+ePreiBjOS4muaKgOacr+WtpumZomQCCQ8PFgIeB0VuYWJsZWRoZGQCCw8PFgIfBmhkZAINDxYCHwQCARYCZg9kFgICAQ8PFgoeBFRleHQFATEeD0NvbW1hbmRBcmd1bWVudAUBMR4JRm9yZUNvbG9yCo0BHwZoHgRfIVNCAgRkZAIPDw8WAh8GaGRkAhEPDxYCHwZoZGQCEw8PFgIfBmhkZAIZDzwrAAsBAA8WDB8AAjIfARYAHwJmHwMCqBcfBAI8HwUCqBdkFgJmD2QWeAICD2QWAmYPZBYCZg8VAjk8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9OSZpZD00ODU3JnR5cGU9NzMnPjwvYT4HWzExLjEyXWQCAw9kFgJmD2QWAmYPFQI5PGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTkmaWQ9NDg1NiZ0eXBlPTc4Jz48L2E+B1sxMS4xMl1kAgQPZBYCZg9kFgJmDxUCPjxhIGhyZWY9Jy9kZXNjcmlwdGlvbnBpYy5hc3B4P2Jsb2dpZD05JmlkPTQ4NTUmdHlwZWlkPTgxJz48L2E+B1sxMS4wNl1kAgUPZBYCZg9kFgJmDxUCQTxhIGhyZWY9Jy9kZXNjcmlwdGlvbnBpYy5hc3B4P2Jsb2dpZD02MDgmaWQ9NDg1MiZ0eXBlaWQ9ODAyJz48L2E+B1swMi4yMV1kAgYPZBYCZg9kFgJmDxUCXjxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD05JmlkPTQ4NDYmdHlwZT03NSc+5aGU5pav6ams5bC85Lqa5aSn5a2m5Luj6KGoOOaciOS8keWBhzwvYT4HWzA4LjAxXWQCBw9kFgJmD2QWAmYPFQJuPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTYwOCZpZD00ODQ0JnR5cGU9Nzk2Jz7pgpMg6L+qIOWkpyDlraYyMDEz5bm0MeaciOWFpeWtpuehleWjq+ivvueoi+S7i+e7jTwvYT4HWzA2LjI5XWQCCA9kFgJmD2QWAmYPFQJ2PGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTYwOCZpZD00ODQ1JnR5cGU9Nzk2Jz7oi7Hlm73pgpPov6rlpKflraYyMDEz5bm05LiA5pyI5YWl5a2m5ZWG56eR566h55CG57G756GV5aOrPC9hPgdbMDYuMjldZAIJD2QWAmYPZBYCZg8VAnY8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NjA4JmlkPTQ4NDMmdHlwZT03OTYnPjIwMTLlubTlu7rnrZHorr7orqHnoZXlo6vpmaLplb/lpZbnq6DoirHokL3kuK3lm73nlZnlrabnlJ88L2E+B1swNS4yOF1kAgoPZBYCZg9kFgJmDxUCZDxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00MzgmaWQ9NDg0MCZ0eXBlPTU4OCc+VGhvbWFzIEphY29ic+iNo+iOt0FJQemdkuW5tOW7uuetkeW4iOWlljwvYT4HWzAyLjEwXWQCCw9kFgJmD2QWAmYPFQJOPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTQzOCZpZD00ODM5JnR5cGU9NTg4Jz5Wb2ljZSBvZiBIb2xvY2F1c3Q8L2E+B1swMi4wOF1kAgwPZBYCZg9kFgJmDxUCcjxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00MzgmaWQ9NDgzOCZ0eXBlPTU4OCc+55qH5Yag5aSn5Y6F5Li+5Yqe5rW35Yab56CB5aS077yITmF2eSBQaWVy77yJ5paw6K6+6K6hPC9hPgdbMDIuMDhdZAIND2QWAmYPZBYCZg8VAmA8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NDM4JmlkPTQ4MzcmdHlwZT01ODgnPklJVOWQkeiuv+WuouW8gOaUvuagoeWGheWuvummhuS9j+WuvzwvYT4HWzAyLjA4XWQCDg9kFgJmD2QWAmYPFQJXPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTQzOCZpZD00ODM2JnR5cGU9NTg4Jz5JSVTnlLXlipvliJvmlrDorr7mlr3lkK/liqg8L2E+B1swMi4wOF1kAg8PZBYCZg9kFgJmDxUCUzxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00MzgmaWQ9NDgzNCZ0eXBlPTU4OCc+MjAxMiBzcHJpbmcgT3JpZW50YXRpb248L2E+B1swMS4xMF1kAhAPZBYCZg9kFgJmDxUCczxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00MzgmaWQ9NDgzMiZ0eXBlPTU4OCc+55S15rCU5LiO6K6h566X5py65bel56iL57O75pys56eR55Sf6I2j6I63SUVFReWlluWtpumHkTwvYT4HWzAxLjEwXWQCEQ9kFgJmD2QWAmYPFQJ1PGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTQzOCZpZD00ODMwJnR5cGU9NTg4Jz5JSVTlia/moKHplb/lo6vlkozkuK3lm73lip7lhazlrqTkuLvku7vlupTpgoDorr/pl67ljJfkuqw8L2E+B1swMS4xMF1kAhIPZBYCZg9kFgJmDxUCdTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00MzgmaWQ9NDgyOSZ0eXBlPTU4OCc+SUlU5qCh6ZW/5ZKM5Ymv5qCh6ZW/5bqU6YKA5Y+C5Yqg56ys5YWt5bGK5Lit576O5YyW5a2m5belPC9hPgdbMDEuMTBdZAITD2QWAmYPZBYCZg8VAmk8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NDM4JmlkPTQ4MzEmdHlwZT01ODgnPklJVOS4reWbveWKnuWFrOWupOaIkOWKn+S4vuWKnuS4iua1t+agoeWPi+S8mjwvYT4HWzAxLjEwXWQCFA9kFgJmD2QWAmYPFQJ1PGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTQzOCZpZD00ODI4JnR5cGU9NTg4Jz5JSVTmoKHplb/jgIHlia/moKHplb/lkoxJSVTkuK3lm73lip7lhazlrqTkuLvku7vlupTpgoDorr88L2E+B1swMS4xMF1kAhUPZBYCZg9kFgJmDxUCaTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00MzgmaWQ9NDgyNyZ0eXBlPTU4OCc+SUlU5Lit5Zu95Yqe5YWs5a6k5oiQ5Yqf5Li+5Yqe5YyX5Lqs5qCh5Y+L5LyaPC9hPgdbMTIuMTNdZAIWD2QWAmYPZBYCZg8VAnI8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NDM4JmlkPTQ4MjYmdHlwZT01ODgnPklJVOWVhuWtpumZoumHkeiejeehleWjq+iiq+ivhOmAieS4uuWFqOeQg+S4gOa1gemhueebrjwvYT4HWzEyLjEyXWQCFw9kFgJmD2QWAmYPFQJqPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTYwOCZpZD00ODI1JnR5cGU9Nzk2Jz4yMDEy5bm05LiA5pyI5YWl5a2m5ZWG56eR566h55CG57G756GV5aOr566A6K6vPC9hPgdbMDkuMTRdZAIYD2QWAmYPZBYCZg8VAlg8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NjA4JmlkPTQ4MjQmdHlwZT03OTYnPumCk+i/quWkp+WtpjIwMTHkuJbnlYzmjpLlkI08L2E+B1swOS4xNF1kAhkPZBYCZg9kFgJmDxUCbTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD00JmlkPTQ4MjMmdHlwZT01Nyc+5r6z5rSy5YWr5aSn5ZCN5qCh5LmL5LiA6I6r57qz5LuA5aSn5a2mOOaciOmdouivleS8mjwvYT4HWzA4LjE4XWQCGg9kFgJmD2QWAmYPFQJOPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00ODIyJnR5cGU9NDIyJz7miqTnkIbor4Hkuabor77nqIs8L2E+B1swOC4xN11kAhsPZBYCZg9kFgJmDxUCTjxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD0xOTUmaWQ9NDgyMSZ0eXBlPTQyMic+5Zu96ZmF5a2m55Sf6K++56iLPC9hPgdbMDguMTddZAIcD2QWAmYPZBYCZg8VAl48YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9MTk1JmlkPTQ4MTkmdHlwZT00MTcnPjIwMTHlubTmiIjpob/lm73pmYXlrabpmaLlpZblrabph5E8L2E+B1swOC4xN11kAh0PZBYCZg9kFgJmDxUCXjxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD0xOTUmaWQ9NDgxOCZ0eXBlPTQxNyc+MjAxMeW5tOm7hOmHkeefreacn+iLseivrea4uOWtpuWbojwvYT4HWzA4LjE3XWQCHg9kFgJmD2QWAmYPFQJXPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTIwJmlkPTQ4MTYmdHlwZT0xMTEnPuWvhumbhuiLseivreivvueoi++8iElFTFDvvIk8L2E+B1swOC4wNF1kAh8PZBYCZg9kFgJmDxUCSTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD02MDgmaWQ9NDgwNyZ0eXBlPTc5Nic+MjAxMeW5tOWtpui0uTwvYT4HWzAyLjE1XWQCIA9kFgJmD2QWAmYPFQJkPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTYwOCZpZD00ODA2JnR5cGU9Nzk2Jz7oi7Hlm73pgpPov6rlpKflrabllYblrabpmaIyMDEx5bm054Ot5oubPC9hPgdbMDIuMTVdZAIhD2QWAmYPZBYCZg8VAmQ8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NjA4JmlkPTQ4MDEmdHlwZT03OTYnPumCk+i/quW7uuetkeWtpumZoi3mnIDpgILlkIjkuK3lm73lrabnlJ88L2E+B1swMS4yMF1kAiIPZBYCZg9kFgJmDxUCZTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD02MDgmaWQ9NDgwMyZ0eXBlPTc5Nic+6YKT6L+q5bu6562R5a2m6ZmiMjAxMeW5tDPmnIjpnaLor5XlvZXlj5Y8L2E+B1swMS4yMF1kAiMPZBYCZg9kFgJmDxUCWjxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD02MDgmaWQ9NDgwMiZ0eXBlPTc5Nic+6YKT6L+q5bu6562R5a2m6Zmi5a2m55Sf5L2c5ZOBPC9hPgdbMDEuMjBdZAIkD2QWAmYPZBYCZg8VAmw8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NjA4JmlkPTQ3OTcmdHlwZT03OTYnPumCk+i/quWkp+WtpuW7uuetkeiuvuiuoeWtpumZouaOqOW5v+ezu+WIl+aKpemBkzwvYT4HWzExLjE5XWQCJQ9kFgJmD2QWAmYPFQJgPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00Nzk1JnR5cGU9NDE3Jz7miIjpob/np5HmioDnkIblt6XlrabpmaLojrflpZbkv6Hmga88L2E+B1swOS4zMF1kAiYPZBYCZg9kFgJmDxUCajxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD02MDgmaWQ9NDc5NCZ0eXBlPTc5Nic+MTDmnIgyMOaXpemCk+i/quWkp+WtpuS4reWbveWtpueUn+WcqOe6v+WSqOivojwvYT4HWzA5LjI4XWQCJw9kFgJmD2QWAmYPFQJ4PGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTU5MyZpZD00NzkyJnR5cGU9Nzg3Jz7mnInigJzlpZbigJ3lj6/mi7/pnZ7npZ7or53igJTigJTmmIblo6vlhbDlpKflrablpZblrabph5HnlLM8L2E+B1swOS4yMV1kAigPZBYCZg9kFgJmDxUCcDxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD01OTMmaWQ9NDc5MCZ0eXBlPTc4Nyc+MjAxMOW5tOaYhuWjq+WFsOWkp+WtpklFTFRTNi4wIOivreiogOePreeUs+ivt+acq+ePrTwvYT4HWzA4LjMwXWQCKQ9kFgJmD2QWAmYPFQJpPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTYwOCZpZD00Nzg4JnR5cGU9Nzk2Jz7pgpPov6rlpKflrabmr5XkuJrnlJ/lvJXpoobmlrDorr7orqHluIjkuYvot688L2E+B1swOC4xMl1kAioPZBYCZg9kFgJmDxUCdTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD02MDgmaWQ9NDc4OSZ0eXBlPTc5Nic+6YKT6L+q5q+V5Lia55Sf5YuH5aS65YWo6Iux5pyA6auY5bu6562R6K6+6K6h5a2m55Sf5aSn5aWWPC9hPgdbMDguMTJdZAIrD2QWAmYPZBYCZg8VAnc8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NzYzJmlkPTQ3ODcmdHlwZT04ODYnPuS4k+WNh+ehleW5tumdnuWPquiDveivu+S8muiuoV8t5ZWG56eR5YWo5LiT5Lia5Z2H5Y+v5LiT5Y2HPC9hPgdbMDguMTBdZAIsD2QWAmYPZBYCZg8VAnc8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NTkzJmlkPTQ3ODYmdHlwZT03ODcnPuaYhuWjq+WFsOWkp+WtpjEw5pyI6K+t6KiA55u05Y2H54+t4oCU4oCU55Sz6K+35oiq5q2i5pel5pyfPC9hPgdbMDguMDRdZAItD2QWAmYPZBYCZg8VAmQ8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9NjA4JmlkPTQ3ODQmdHlwZT03OTYnPumCk+i/quWkp+WtpjIwMTDlubTnp4vlraPlvIDlrabpm4bnu5Plj7c8L2E+B1swOC4wNF1kAi4PZBYCZg9kFgJmDxUCYTxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD02MDgmaWQ9NDc4NSZ0eXBlPTc5Nic+MjAxMOW7uuetkeiuvuiuoeWHhuaWsOeUn+iuv+iwiOiusOWunjwvYT4HWzA4LjA0XWQCLw9kFgJmD2QWAmYPFQJfPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTYwOCZpZD00NjIyJnR5cGU9Nzk2Jz4yMDEw5bm0MeaciOWFpeWtpuehleWjq+ivvueoi+S7i+e7jTwvYT4HWzA4LjAyXWQCMA9kFgJmD2QWAmYPFQJpPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00NzIyJnR5cGU9NDE3Jz7lhaXor7vmvrPmtLLlpKflrabnmoTmlZnogrLot6/nu4/kuI7ovazlrabliIY8L2E+B1swNy4yMV1kAjEPZBYCZg9kFgJmDxUCYDxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD0xOTUmaWQ9NDc3MyZ0eXBlPTQxNyc+5oiI6aG/55qE5ri45a2m6K6h5YiS5ZKM5rW35aSW6aG555uuPC9hPgdbMDcuMjFdZAIyD2QWAmYPZBYCZg8VAmA8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9MTk1JmlkPTQ3ODImdHlwZT00MTcnPuaIiOmhv+enkeaKgOeQhuW3peWtpumZouWtpueUn+aEn+iogDwvYT4HWzA3LjA3XWQCMw9kFgJmD2QWAmYPFQJXPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00NzgxJnR5cGU9NDIyJz7lpYvov5vogYzkuJrmlZnogrLlpZblrabph5E8L2E+B1swNy4wN11kAjQPZBYCZg9kFgJmDxUCTjxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD0xOTUmaWQ9NDcyMCZ0eXBlPTQxNyc+5Zyo5ZCJ6ZqG5biC55Sf5rS7PC9hPgdbMDcuMDddZAI1D2QWAmYPZBYCZg8VAmA8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9MTk1JmlkPTQ3MTkmdHlwZT00MTcnPuaIiOmhv+enkeaKgOeQhuW3peWtpumZouWFpeWtpuimgeaxgjwvYT4HWzA3LjA3XWQCNg9kFgJmD2QWAmYPFQJoPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00NzgwJnR5cGU9NDIyJz7miqTnkIblrabmloflh63vvIjnmbvorrAv5oqk55CG6YOoMu+8ieivvueoizwvYT4HWzA3LjA2XWQCNw9kFgJmD2QWAmYPFQJjPGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00NzE3JnR5cGU9NDE3Jz7miIjpob/miYDlnKjln47luILigJTigJTlkInpmobluILku4vnu408L2E+B1swNy4wNl1kAjgPZBYCZg9kFgJmDxUCVzxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD0xOTUmaWQ9NDc3OSZ0eXBlPTQyMic+5YWl6K+75r6z5rSy5aSn5a2m6L2s5a2m5YiGPC9hPgdbMDcuMDZdZAI5D2QWAmYPZBYCZg8VAmo8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9MTk1JmlkPTQ3NzYmdHlwZT00MjInPuaIiOmhv+enkeaKgOeQhuW3peWtpumZoi3or77nqIvmpoLov7DvvIjkuozvvIk8L2E+B1swNy4wNV1kAjoPZBYCZg9kFgJmDxUCajxhIGhyZWY9Jy9kZXNjcmlwdGlvbi5hc3B4P2Jsb2dpZD0xOTUmaWQ9NDc3NSZ0eXBlPTQyMic+5oiI6aG/56eR5oqA55CG5bel5a2m6ZmiLeivvueoi+amgui/sO+8iOS4gO+8iTwvYT4HWzA3LjA1XWQCOw9kFgJmD2QWAmYPFQJ2PGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTU5MyZpZD00NzY0JnR5cGU9Nzg3Jz7nibnmrornlLPor7flh4blpIfml7bigJTigJTmmIblo6vlhbDlpKflrabvvJoxMOaciDEx5pel55u0PC9hPgdbMDYuMjRdZAI8D2QWAmYPZBYCZg8VAmM8YSBocmVmPScvZGVzY3JpcHRpb24uYXNweD9ibG9naWQ9MTk1JmlkPTQ3NjImdHlwZT00MTYnPuaIiOmhv+enkeaKgOeQhuW3peWtpumZokNFT+iHtOasoui/jui+njwvYT4HWzA2LjA5XWQCPQ9kFgJmD2QWAmYPFQJePGEgaHJlZj0nL2Rlc2NyaXB0aW9uLmFzcHg/YmxvZ2lkPTE5NSZpZD00NzQ5JnR5cGU9NDIyJz4y5ZGo6Iux6K+t6IGM5Lia5paH5YyW5a2m5Lmg5LmL5peFPC9hPgdbMDYuMDddZAIbDw8WAh8GaGRkAh0PDxYCHwZoZGQCHw8WAh8EAgEWAmYPZBYCAgEPDxYKHwcFATEfCAUBMR8JCo0BHwZoHwoCBGRkZGX0X2BozSrPKZkLZwJgwZ2GLc7M"));
            formparams.add(new BasicNameValuePair("__VIEWSTATEGENERATOR", "DCE5697F"));
            formparams.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWCgKjtYvsBAKZ7YzdCgKct7iSDALJvvqWDgKah6L3CQKkipGkCgL6ipKrDQKF2LmECgLJvr6YDgLZlNm+CtODGM7vtCcDOv3TvNAtPJ+wnVDd"));
            formparams.add(new BasicNameValuePair("txtSearch", ""));
            formparams.add(new BasicNameValuePair("textBoxPageNumber", ""));
            formparams.add(new BasicNameValuePair("textBoxPageNumber1", ""));
            requestBuilder.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));
        }*/
        // 艾迪留学网 分页请求 end
        
        // 地址资料post参数 start
        /*List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("strSql", "1=1 and Edoc is not null  and Edoc <> ''"));
        formparams.add(new BasicNameValuePair("pageIndex", "1"));
        formparams.add(new BasicNameValuePair("Flag", "0"));
        formparams.add(new BasicNameValuePair("OrderType", "1"));
        formparams.add(new BasicNameValuePair("OrderField", "PKIIB"));
        requestBuilder.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));*/
        // 地址资料post参数 end
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
        String content = getContent(charset, httpResponse);
        Page page = new Page();
        page.setRawText(content);
//        page.putField("", content);
        page.setUrl(new PlainText(request.getUrl()));
        if(request.getDatumId()!=null && request.getDatumId().indexOf("_more") !=-1){
        	page.setMoreDatumId(request.getDatumId().substring(0,page.getDatumId().indexOf("_more")));
        }
        page.setDatumId(IDGenerate.uuid());	// 元数据ID ConverterUtil.EncoderByMd5(request.getUrl())
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return page;
    }

    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
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
