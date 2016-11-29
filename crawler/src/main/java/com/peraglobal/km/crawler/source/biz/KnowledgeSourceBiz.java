package com.peraglobal.km.crawler.source.biz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.peraglobal.km.crawler.source.dao.KnowledgeSourceDao;
import com.peraglobal.km.crawler.source.model.KnowledgeDetail;
import com.peraglobal.km.crawler.source.model.KnowledgeSource;
import com.peraglobal.km.crawler.source.model.WebSiteLoginEntity;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.dao.TaskJobDao;
import com.peraglobal.km.crawler.task.model.JdbcEnity;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.util.CrawlerMD5Utils;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.common.utils.UtilMisc;
import com.peraglobal.pdp.core.BaseBiz;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.condition.Pagination;
import com.peraglobal.pdp.core.spring.ExtendedMessageProperty;
/**
 * 知识源Biz类
 * @author DuanZheng
 *
 */
@Service
public class KnowledgeSourceBiz extends BaseBiz<KnowledgeSource, KnowledgeSourceDao> {
	@Resource
	KnowledgeSourceDao sourceDao;
	@Resource
	TaskJobBiz taskJobBiz;
	@Resource
	DatumBiz datumBiz;
	@Resource
	MgDatumBiz mgDatumBiz;
	/**
	 * 模糊查询知识源列表信息
	 * @param map
	 * @return List<Source>
	 * author duanzheng
	 */
	public List<KnowledgeSource> fuzzyQueryInformationList(Map map){
		try {
			return sourceDao.fuzzyQueryInformationList(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 查询知识源列表
	 * @return List<Source>
	 * author duanzheng
	 */
	public List<KnowledgeSource> queryKnowledgeSourceList(){
		try {
			return sourceDao.queryKnowledgeSourceList();
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：查询知识源详细信息
	 * @param map
	 * @return List<Source>
	 * author duanzheng
	 */
	public List<KnowledgeSource> queryKnowledgeSourceDetailInfo(Map map){
		try {
			return sourceDao.queryKnowledgeSourceDetailInfo(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 模糊查询知识源列表行数
	 * @param map
	 * @return List<Source>
	 * author duanzheng
	 */
	public int fuzzyQueryInformationListCount(Map map){
		try {
			return sourceDao.fuzzyQueryInformationListCount(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 查询知识源是否有关联
	 * @param map
	 * @return int
	 * author duanzheng
	 */
	public Map queryTaskJobBySourceId(Map map){
		try {
			return sourceDao.queryTaskJobBySourceId(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 功能：正序查询或倒序查询知识源列表
	 * @param map
	 * @return List<KnowledgeSource>
	 * author duanzheng
	 */
	public List<KnowledgeSource> findKnowledgeListOrderType(Pagination pagination){
		try {
			return sourceDao.findKnowledgeListOrderType(pagination);
		} catch (Exception e) {
			throw e;
		}
	}
	public List<KnowledgeSource> findKnowledgeListOrderTypeAsc(Pagination pagination){
		try {
			return sourceDao.findKnowledgeListOrderTypeAsc(pagination);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int loginWebSite(WebSiteLoginEntity entity){
		String loginUrl = entity.getLoginRequest();
		String doLoginAction = entity.getLoginSubmit();
		try {
			HttpClient hc = new DefaultHttpClient();
			HttpGet request = new HttpGet(loginUrl);
			request.setHeader("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//		request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
//		request.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			request.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Win64; x64; Trident/4.0)");
			
			HttpResponse response = hc.execute(request);
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String temp = "";  
			while ((temp = br.readLine()) != null) {  
			    String str = new String(temp.getBytes(), "utf-8");  
			    result.append(str);  
			} 
			System.out.println("result:"+result.toString()); 
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
			    
			    if(elName.equals("userName")) {
			        elValue = entity.getUsername();//如果属性名是Email，就将值设置成用户定义的值
			    }else if(elName.equals("password")) {
			        elValue = entity.getPassword();//如果属性名是Passwd，就将值设置为用户定义的值
			    }
			    
			    if(!elName.equals("button") && !elType.equals("submit") && !elName.equals("") && !elValue.equals("")) {//此外有些button是input应排除
			    	formparams.add(new BasicNameValuePair(elName, elValue));//创建键值对加入list
			    }
			}
			
			HttpPost request1 = new HttpPost(doLoginAction);
			request1.setHeader("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			request1.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Win64; x64; Trident/4.0)");
			request1.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));
			HttpResponse response1 = hc.execute(request1);
			int statusCode = response1.getStatusLine().getStatusCode();
			System.out.println("statusCode:"+statusCode);
			String set_cookie = response1.getLastHeader("Set-Cookie").getValue();  
			request1.releaseConnection();
			if(set_cookie.indexOf("ticket-username")!=-1){
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		return 1;
//		HttpGet request2 = new HttpGet(url);
//		request2.setHeader("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
//		request2.setHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; .NET4.0C; .NET4.0E)");
//		set_cookie = set_cookie.substring(0,set_cookie.indexOf(";"));
//    	System.out.println("set_cookie:"+set_cookie);
//		request2.setHeader("Cookie", set_cookie);
//		HttpResponse response2 = hc.execute(request2);
//		BufferedReader br2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
//		StringBuffer result2 = new StringBuffer();
//		String temp1 = "";  
//	    while ((temp1 = br2.readLine()) != null) {  
//	        String str = new String(temp1.getBytes(), "utf-8");  
//	        result2.append(str);  
//	    } 
//		System.out.println("result:"+result2.toString()); 
//		String html1 = result2.toString();
//		if(html1.indexOf("注销")!=-1){
//			return 0;
//		}
		
	}
	
	/**
	 * 功能:根据souceId获取jdbcEntity对象
	 * <p>作者 王晓鸣 2016-1-28
	 * @param sourceId 
	 * @return
	 */
	public JdbcEnity getJdbcEnityBysourceId(String sourceId){
		KnowledgeSource source= findById(sourceId);
		//获取xml解析对象
		Dom4jXmlUtil dm=new Dom4jXmlUtil();
		//获取MD5加密对象
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		Map<String, String> map=dm.generateMap(source.getLinkContent());
		JdbcEnity jdbcEnity = new JdbcEnity();
		String DBurl=null;
		String DBType=map.get("type");
		String url=map.get("url");
		String port=map.get("port");
		String name=map.get("name");
		String type="JdbcDataSource";
		if (DBType.equals("oracle")) {
			DBurl="jdbc:oracle:thin:@"+url+":"+port+":"+name;
		}
		else if (DBType.equals("mysql")) {
			DBurl="jdbc:mysql://"+url+":"+port+"/"+name;
		}
		else if (DBType.equals("sqlserver")) {
			DBurl="jdbc:sqlserver://"+url+":"+port+";DatabaseName="+name;
		}
		jdbcEnity.setUrl(DBurl);
		jdbcEnity.setType(type);
		jdbcEnity.setUser(source.getUsername());
		jdbcEnity.setPassword(md5Utils.Decryption(source.getPassword()));
		jdbcEnity.setDriver(map.get("driver"));
		return jdbcEnity;
		
	}
	
	/**
	 * 功能:分页查询资源信息
	 * <p>作者 wenqihui 2015-8-3
	 * @param key
	 * @param page
	 * @return
	 */
	public List findknowledgeList(List<Map<String,String>> knowledgeList, Pagination page){
		List result = new ArrayList();
		page.init(knowledgeList.size(), page.getPageSize());
		knowledgeListHandler(result,knowledgeList,page);
		return result;
	}
	
	public void knowledgeListHandler(List list,List<Map<String,String>> knowledgeList,Pagination page){
		KnowledgeDetail detail=null;
		List<KnowledgeDetail> detailList = new ArrayList<KnowledgeDetail>();
		int beforeStep=0,step=0;
		for(int i = 0; i < knowledgeList.size(); i++){
			beforeStep++;
			if(beforeStep>page.getFirstResult() && step<page.getPageSize()){
							Map<String,String> obj=knowledgeList.get(i);  
			            	detail=new KnowledgeDetail();
			            	String knowledgeModel=obj.get("TEPT_ID");
			            	String knowledgeModelName=obj.get("TEPT_NAME");
			            	int taskcount=taskJobBiz.querytaskcountByKnowledge( knowledgeModel);
			    			int sourcecount= taskJobBiz.querySourceCountByKnowledge(knowledgeModel);
			            	int taskdatacountall=getTaskDataCountByKnowledge(knowledgeModel);
							detail.setKnowledgeModel(knowledgeModel);
							detail.setKnowledgeModelName(knowledgeModelName);
							detail.setTaskcount(taskcount);
							detail.setSourcecount(sourcecount);
							detail.setTaskdatacountall(taskdatacountall);
							detailList.add(detail);
							list.add(detailList.get(step));
				step++;
			}
		}
	}
	public  int getTaskDataCountByKnowledge(String knowledgeModel){
		//String knowledgeModel=getStringValue(request, "knowledgeModel");
		Condition condition = Condition.parseCondition("knowledgemodel_string_eq");
		condition.setValue(knowledgeModel);
		Condition condition2 = Condition.parseCondition("registermodel_string_eq");
		condition2.setValue("2");
		List<Condition> lts= new ArrayList<Condition>();
		lts.add(condition);
		lts.add(condition2);
		List<TaskJob> tjs = taskJobBiz.find(lts);
		int i=0;
		for (TaskJob taskJob : tjs) {
			
			//i += datumBiz.queryTaskDataCountBytaskId(taskJob.getConnectId());
			i += mgDatumBiz.findByTaskId(taskJob.getConnectId());
		}
		//Map<String, Object> result = new HashMap<String, Object>();
		//result.put("taskdatacount", i);
		//return this.putToModelAndViewJson(result);
		return i;
	}
}
