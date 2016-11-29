package com.peraglobal.km.crawler.task.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.gridfs.GridFSDBFile;
import com.peraglobal.km.crawler.extract.biz.KnowledgeMetadataBiz;
import com.peraglobal.km.crawler.task.biz.MonitorErrorBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.task.biz.TaskRuleBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.MonitorError;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskJobMonitor;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.web.biz.AttachmentCrawlerBiz;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.crawler.web.biz.RuleBiz;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.biz.MgKnowledgeMetadataBiz;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.km.mongo.model.KM_KnowledgeMetadata;
import com.peraglobal.pdp.common.utils.DateUtils;
import com.peraglobal.pdp.common.utils.FileUploadAndDownLoadUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.condition.Pagination;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;

/**
 *  <code>TaskJobController.java</code>
 *  <p>功能:采集任务监控Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 王晓鸣  时间 2015-12-18	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller  
public class TaskJobMonitorController extends ExtendedMultiActionController{  
	final String dateFormat="YYYY-MM-dd HH:mm:ss";
	private Logger LOG = LoggerFactory.getLogger(TaskJobMonitorController.class);
	
	@Resource
	private TaskJobMonitorBiz taskJobMonitorBiz;
	@Resource 
	private TaskJobBiz taskjobbiz;
	@Resource 
	private DatumBiz datumBiz;
	@Resource
	private TaskRuleBiz taskRuleBiz;
	@Resource
	private RuleBiz rulebiz;
	@Resource
	private KnowledgeMetadataBiz knowledgeMetadataBiz;
	@Resource
	private MonitorErrorBiz monitorErrorBiz;
	@Resource
	private MgDatumBiz mgDatumBiz;
	@Resource
	private MgKnowledgeMetadataBiz mgKnowledgeMetadataBiz;
	@Resource
	private AttachmentCrawlerBiz attachmentCrawlerBiz;
	/**
	 * 任务监控跳转页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toTaskJobMonitorPage(HttpServletRequest request, HttpServletResponse response) {
		String id= getStringValue(request,"id");
		//采集类型
		TaskJob tj= taskjobbiz.findById(id);
		String registerType= getStringValue(request,"registerType");
		if(registerType.equals(null) || "null".equals(registerType)|| registerType==""){
			registerType=tj.getRegisterType();
			if(registerType==null){
				registerType="0";
			}
		}
		//采集，转换，传输
		String registerModel= getStringValue(request,"registerModel");
		String path=null;
		if(registerModel.equals("1")){//采集
			path="km/crawler/task/taskMonitoring";
		}else if(registerModel.equals("2")){//转换
			path="km/crawler/extract/extractTaskMonitoring";
		}else{//传输
			path="km/crawler/transfer/transmissionTaskMonitoring";
		}
		ModelAndView view = new ModelAndView();
		String ruleContent= "";
		if(registerType.equals("1")){//互联网
			Map mapParam=new HashMap();
			mapParam.put("taskId", id);
			List<Rule> listRule= rulebiz.queryTaskRuleById(mapParam);
			if(listRule!=null && listRule.size()>0){
				//ruleContent= parseJSON(null,listRule,Integer.parseInt(registerType));
				for (int i = 0; i < listRule.size(); i++) {
					String name=listRule.get(i).getName();
					ruleContent+=name+",";
				}
				if(ruleContent.trim()!=null && ruleContent.trim()!=""){
					ruleContent=ruleContent.substring(0, ruleContent.length()-1);
				}
			}
		}else{//数据库和本地
			List<TaskRule> taskRule= taskRuleBiz.queryTaskRuleByjobId(id);
			if(taskRule!=null && taskRule.size()>0){
//				ruleContent=parseJSON(taskRule,null,Integer.parseInt(registerType));
				for (int i = 0; i < taskRule.size(); i++) {
					byte[] byteArray= taskRule.get(i).getRuleContent();
					if(byteArray.length>0){
						String str=new String(byteArray);
						//解析xml格式字符串
						List list= Dom4jXmlUtil.getKeyFromXMl(str);
						ruleContent = list.toString();
					}
				}
				if(ruleContent.trim()!=null && ruleContent.trim()!=""){
					ruleContent=ruleContent.substring(1, ruleContent.length()-1);
				}
			}
		}
		long collectNumber=0;
		long isFullNumber=0;
		long notFullNumber=0;
		Map param=null;
		if(registerType.equals("4")){//根据registerType判断从哪个表中获取数据
			/*param=new HashMap(); 
			param.put("taskId", id);
			collectNumber=knowledgeMetadataBiz.queryExtractNumberBytaskId(param);*/
			collectNumber=mgKnowledgeMetadataBiz.findByTaskId(id);
			isFullNumber=mgKnowledgeMetadataBiz.findFullByTaskId(id);
			notFullNumber=mgKnowledgeMetadataBiz.findnotFullByTaskId(id);
			
		}else if(registerType.equals(JobModel.TYPE_TRANSFER)){
			collectNumber = this.taskJobMonitorBiz.countTransferNumber(id);
		}else{
			/*param=new HashMap(); 
			param.put("id", id);
			collectNumber=datumBiz.numberOfQueriesToCollect(param);*/
			collectNumber=mgDatumBiz.findByTaskId(id);
			isFullNumber=mgDatumBiz.findFullByTaskId(id);
			notFullNumber=mgDatumBiz.findnotFullByTaskId(id);
		}
		view.addObject("id",id);
		view.addObject("triggerNumber",collectNumber);
		view.addObject("isFullNumber",isFullNumber);
		view.addObject("notFullNumber",notFullNumber);
		view.addObject("description",tj.getDescription());
		view.addObject("jobType",tj.getRegisterType());
		view.addObject("automatic",tj.getAutomatic());
		view.addObject("name",tj.getName());
		view.addObject("state",tj.getJobState());
		view.addObject("ruleContent", ruleContent);
		view.addObject("knowledgeModelName", tj.getKnowledgeModelName());
		view.addObject("systemName",tj.getSystemName());
		view.setViewName(path);
		return view;
	}
	
	/**
	 * 功能：查看采集数据详情
	 *  <p> 作者：王晓鸣 2016-6-2
	 * @return
	 */
	public ModelAndView toDatumViewPage(HttpServletRequest request, HttpServletResponse response) {
		//获取json字符串进行匹配
//		String properties=externalRestBiz.getModelProperties();
		String properties="";
		properties = exchangeJsonPros(properties);
		JSONArray jsa=JSONArray.fromObject(properties);
		//获取详情页面类型   1:采集数据详情   2:转换数据详情
		String viewType = getStringValue(request, "viewType");
		String id= getStringValue(request,"id");
		
		String path=null;
		Map<String, String> map=null;
		ModelAndView view = new ModelAndView();
		if("1".equals(viewType)){
			path="km/crawler/task/datumView";
			KM_Datum km_Datum = mgDatumBiz.findOneById(id);
			view.addObject("id",km_Datum.getId());
			map = km_Datum.getKvs().toMap();
		}else if("2".equals(viewType)){
			path="km/crawler/extract/extractView";
			KM_KnowledgeMetadata km_KnowledgeMetadata=mgKnowledgeMetadataBiz.findOneById(id);
			view.addObject("id",km_KnowledgeMetadata.getId());
			map=km_KnowledgeMetadata.getKvs().toMap();
		}

		List list = new ArrayList();
		 for (String key : map.keySet()) {
      	   String keyName=key;
      	   Map<String, String> paramMap= new HashMap<String, String>();
      		int l=0;
			for (int k = 0; k < jsa.size(); k++) {
				if(l>0){
					break;
				}
				Map mapParam=(Map)jsa.get(k);
				String val= ""+mapParam.get("PROP_ID");
				if(val.equals(keyName)){
					l++;
					String _val =  mapParam.get("PROP_NAME").toString();
					_val = _val.substring(0, _val.lastIndexOf("("));
					String value=""+ _val;
					String valuename=Dom4jXmlUtil.getTextFromTHML(map.get(key));
					paramMap.put("key", value);
					paramMap.put("value", valuename);
				}
			}
      	   list.add(paramMap);
      	  }
		view.addObject("datum", list);
		
		List<AttachmentCrawler> list1 = attachmentCrawlerBiz.findAllByDatumId(id);
		List list2 = new ArrayList();
		if(list1!=null){
			String datumName = "";
			String datumType = "";
			Map<String, String> map2=null;
			for (AttachmentCrawler attachmentCrawler : list1) {
				map2 = new HashMap<String, String>();
				map2.put("key", attachmentCrawler.getPath());
				datumName=attachmentCrawler.getName();
				if(datumName.indexOf(".")!=-1){
					datumName=datumName.substring(0, datumName.indexOf("."));
				}
				datumType=attachmentCrawler.getType();
				if(datumType.equals(".null")){
					//list2.add(datumName);
					map2.put("value", datumName);
				}else{
					//list2.add(datumName+datumType);
					map2.put("value", datumName+datumType);
				}
				list2.add(map2);
			}
		}
		view.addObject("datumName", list2);
		view.setViewName(path);
		return view;
		
	}
	
	/**
	 * 打开任务监控列表页面
	 * @param jobid 组 ID
	 * @return 任务监控列表
	 */
    public ModelAndView taskJobMonitorList(HttpServletRequest request,HttpServletResponse response){
    	List<TaskJobMonitor> map=null;
    	ListPageQuery parameters = new ListPageQuery();
    	try {
    		String jobid=getStringValue(request, "jobid");
    		new ServletRequestDataBinder(parameters).bind(request);
    		Condition condition = Condition.parseCondition("jobid_string_eq");
    		condition.setValue(jobid);
    		parameters.getConditions().getItems().add(condition);
    		map=  taskJobMonitorBiz.find(parameters.getPagination(), condition);
		} catch (Exception e) {
			throw e;
		}
		return this.putToModelAndViewJson(map, parameters,dateFormat);
 	}
    
    /**
 	 * 功能：采集数据列表
 	 * 作者：王晓鸣 2016-6-12
 	 * @param request
 	 * @param response
 	 * @return
 	 */
	public ModelAndView taskDatumList(HttpServletRequest request,HttpServletResponse response){
		String fullState=getStringValue(request, "fullState");
		String incompleteDataState= getStringValue(request, "incompleteDataState");
    	String taskId=getStringValue(request, "jobid");
    	Query query = new Query();
    	Criteria criteria = new Criteria("taskId").is(taskId);
    	if("0".equals(fullState)&&"1".equals(incompleteDataState)){
    		criteria.and("isFull").is("0");
    	}
    	if("0".equals(incompleteDataState)&&"1".equals(fullState)){
    		criteria.and("isFull").is("1");
    	}
    	if("1".equals(fullState)&&"1".equals(incompleteDataState)){
    		criteria.and("isFull").is("-1");
    	}
    	query.addCriteria(criteria);
    	ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		Pagination page = parameters.getPagination();
		page.init(mgDatumBiz.findByCount(query), page.getPageSize());
		List<KM_Datum> kn=null;
		kn=mgDatumBiz.findByTaskIdDatum(query,page.getFirstResult(),page.getPageSize());
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		if(kn!=null){
			for (KM_Datum km_Datum : kn) {
				Map<String, String> map =km_Datum.getKvs().toMap();
				if(map.get("KNOWLEDGE_NAME:INHERENT")==null||map.get("KNOWLEDGE_NAME:INHERENT")==""){
					map.put("KNOWLEDGE_NAMEINHERENT", "无");
				}else{
					map.put("KNOWLEDGE_NAMEINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("KNOWLEDGE_NAME:INHERENT")));
					map.remove("KNOWLEDGE_NAME:INHERENT");
				}
				
				if(map.get("KEYWORD:INHERENT")==null||map.get("KEYWORD:INHERENT")==""){
					map.put("KEYWORDINHERENT", "无");
				}else{
					map.put("KEYWORDINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("KEYWORD:INHERENT")));
					map.remove("KEYWORD:INHERENT");
				}
				
				if(map.get("SUMMERY:INHERENT")==null||map.get("SUMMERY:INHERENT")==""){
					map.put("SUMMERYINHERENT", "无");
				}else{
					map.put("SUMMERYINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("SUMMERY:INHERENT")));
					map.remove("SUMMERY:INHERENT");
				}
				
				if(map.get("KNOWLEDGE_AUTHOR:INHERENT")==null||map.get("KNOWLEDGE_AUTHOR:INHERENT")==""){
					map.put("KNOWLEDGE_AUTHORINHERENT", "无");
				}else{
					map.put("KNOWLEDGE_AUTHORINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("KNOWLEDGE_AUTHOR:INHERENT")));
					map.remove("KNOWLEDGE_AUTHOR:INHERENT");
				}
				
				if(km_Datum.getCreateDate()!=null){
					String createDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(km_Datum.getCreateDate());
					map.put("createDate", createDate);
					map.put("id", km_Datum.getId());
				}
				list.add(map);
			}
		}
		return this.putToModelAndViewJson(list, parameters);
    }
	
	 /**
 	 * 功能：转换数据列表
 	 * 作者：王晓鸣 2016-6-12
 	 * @param request
 	 * @param response
 	 * @return
 	 */
	public ModelAndView taskExtractList(HttpServletRequest request,HttpServletResponse response){
    	String taskId=getStringValue(request, "jobid");
    	Query query = new Query();
    	Criteria criteria = new Criteria("taskId").is(taskId);
    	query.addCriteria(criteria);
    	ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		Pagination page = parameters.getPagination();
		page.init(mgKnowledgeMetadataBiz.findByCount(query), page.getPageSize());
		List<KM_KnowledgeMetadata> kn=null;
		kn=mgKnowledgeMetadataBiz.findByTaskIdDatum(query,page.getFirstResult(),page.getPageSize());
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		if(kn!=null){
			for (KM_KnowledgeMetadata km_KnowledgeMetadata : kn) {
				Map<String, String> map =km_KnowledgeMetadata.getKvs().toMap();
				if(map.get("KNOWLEDGE_NAME:INHERENT")==null||map.get("KNOWLEDGE_NAME:INHERENT")==""){
					map.put("KNOWLEDGE_NAMEINHERENT", "无");
				}else{
					map.put("KNOWLEDGE_NAMEINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("KNOWLEDGE_NAME:INHERENT")));
					map.remove("KNOWLEDGE_NAME:INHERENT");
				}
				
				if(map.get("KEYWORD:INHERENT")==null||map.get("KEYWORD:INHERENT")==""){
					map.put("KEYWORDINHERENT", "无");
				}else{
					map.put("KEYWORDINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("KEYWORD:INHERENT")));
					map.remove("KEYWORD:INHERENT");
				}
				
				if(map.get("SUMMERY:INHERENT")==null||map.get("SUMMERY:INHERENT")==""){
					map.put("SUMMERYINHERENT", "无");
				}else{
					map.put("SUMMERYINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("SUMMERY:INHERENT")));
					map.remove("SUMMERY:INHERENT");
				}
				
				if(map.get("KNOWLEDGE_AUTHOR:INHERENT")==null||map.get("KNOWLEDGE_AUTHOR:INHERENT")==""){
					map.put("KNOWLEDGE_AUTHORINHERENT", "无");
				}else{
					map.put("KNOWLEDGE_AUTHORINHERENT", Dom4jXmlUtil.getTextFromTHML(map.get("KNOWLEDGE_AUTHOR:INHERENT")));
					map.remove("KNOWLEDGE_AUTHOR:INHERENT");
				}
				
				if(km_KnowledgeMetadata.getCreateDate()!=null){
					String createDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(km_KnowledgeMetadata.getCreateDate());
					map.put("createDate", createDate);
					map.put("id", km_KnowledgeMetadata.getId());
				}
				list.add(map);
			}
		}
		return this.putToModelAndViewJson(list, parameters);
    }
	
    /**
	 * 功能：获取监控出错信息
	 * 作者：井晓丹 2016-4-12
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getJobMonitorErrors(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		String monitorId = getStringValue(request, "monitorId");
		List<MonitorError> meList = monitorErrorBiz.queryMonitorErrors(monitorId);
		for(int i = 0; i< meList.size(); i++){
			meList.get(i).setCreateDateStr(DateUtils.format(meList.get(i).getCreateDate(), dateFormat));
		}
		if(meList!=null && meList.size()>0){
			result.put("errors",JSONArray.fromObject(meList));
		}else{
			result.put("errors","null");
		}
		return this.putToModelAndViewJson(result);
	}
    
	/**
	 * 转换全局属性字段存储
	 * @param properties
	 * @return
	 */
	private String exchangeJsonPros(String properties) {
		JSONArray exchangedArray = new JSONArray();
		JSONObject exchangeObj = null;
		JSONArray json = JSONArray.fromObject(properties); // 首先把字符串转成 JSONArray  对象
		String teptName = "";
		for(int i=0;i<json.size();i++){
			JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
			exchangeObj = new JSONObject();
			exchangeObj.put("PROP_ID", job.get("PROP_ID")+":"+job.get("TEPT_ID"));
			teptName = job.get("TEPT_NAME") == null ? "" : job.get("TEPT_NAME").toString();
			exchangeObj.put("PROP_NAME", job.get("PROP_NAME")+"("+teptName+")");
			exchangedArray.add(exchangeObj);
		}
		return exchangedArray.toString();
	}
	/**
	 * 下载附件
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileId = getStringValue(request, "fileId");
		String fileName = getStringValue(request, "fileName");
		try {
			response.addHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("GBK"),"ISO8859-1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		OutputStream out = null;
		InputStream in = null;

		try {
			response.reset();
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentType("application/octet-stream;charset=UTF-8");
			out = response.getOutputStream();
			GridFSDBFile dbFile = mgDatumBiz.findFileById(fileId);
			in = dbFile.getInputStream();
			// 获取二进制输出流
			out = response.getOutputStream();
			int i = -1;
			byte[] buffer = new byte[1024];
			// 输出文件数据
			while ((i = in.read(buffer)) != -1) {
				out.write(buffer, 0, i);
			}
			out.flush();
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
		}
	}
	

}