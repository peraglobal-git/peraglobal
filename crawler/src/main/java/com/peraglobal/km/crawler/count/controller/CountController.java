package com.peraglobal.km.crawler.count.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.rtf.RTFEditorKit;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.km.crawler.source.biz.KnowledgeSourceBiz;
import com.peraglobal.km.crawler.source.model.KnowledgeSource;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.condition.Pagination;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;

/**
 * 统计分析Controller类
 * @author wangxiaoming
 *
 */
@Controller
public class CountController extends ExtendedMultiActionController{
	@Resource
	private TaskJobBiz taskJobBiz;
	@Resource
	private KnowledgeSourceBiz knowledgeSourceBiz;
	@Resource
	private DatumBiz datumBiz;
	@Resource
	private MgDatumBiz mgDatumBiz;
	
	/**
	 * 功能：跳转统计分析页面（按知识源）
	 * @author wangxiaoming
	 */
	public ModelAndView countPage(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("km/crawler/count/main");
	}
	
	/**
	 * 功能：跳转统计分析页面（按知识模板）
	 * @author wangxiaoming
	 */
	public ModelAndView countPage2(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("km/crawler/count/main2");
	}
	
	/**
	 * 功能：跳转统计图页面
	 * @author wangxiaoming
	 */
	public ModelAndView toChart(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("km/crawler/count/chart");
	}
	/**
	 * 功能：按知识源获取列表
	 * @author wangxaioming
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryCountList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		List<KnowledgeSource> kn=null;
		new ServletRequestDataBinder(parameters).bind(request);
		kn=knowledgeSourceBiz.findKnowledgeListOrderTypeAsc(parameters.getPagination());
		for (KnowledgeSource knowledgeSource : kn) {
			knowledgeSource.setTaskjobCount(getTaskCount(knowledgeSource.getId()));
			knowledgeSource.setTaskDataCount(getTaskDataCount(knowledgeSource.getId()));
		}
		return this.putToModelAndViewJson(kn, parameters);
	}
	
	/**
	 * 功能：按知知识模板获取列表
	 * @author wangxaioming
	 * @param request
	 * @param response
	 * @return 
	 * @return
	 */
	public ModelAndView queryCountListByKnowledge(HttpServletRequest request,HttpServletResponse response){
//		String KnowledgeModel=externalRestBiz.getAllModelList();
		String KnowledgeModel="";
		JSONArray jsonArray = JSONArray.fromObject(KnowledgeModel);  
		List<Map<String,String>> mapListJson = (List)jsonArray;  
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List list  = knowledgeSourceBiz.findknowledgeList(mapListJson, parameters.getPagination());
		return this.putToModelAndViewJson(list, parameters);
	}
	
	/**
	 * 功能： 获取该知识源下的采集任务数量
	 * @author wangxiaoming
	 * @param request
	 * @param response
	 * @return
	 */
	public int getTaskCount(String sourceId){
		int taskcount=taskJobBiz.querytaskcountBysourceId(sourceId);
		return taskcount;
	}
	
	/**
	 * 功能： 获取该知识模板下的采集任务数量
	 * @author wangxiaoming
	 * @return
	 */
	public int getTaskCountByKnowledge(String knowledgeModel){
		int taskcount=taskJobBiz.querytaskcountByKnowledge(knowledgeModel);
		return taskcount;
	}
	
	/**
	 * 功能：查询该知识源下所有任务采集数据的总数
	 * @author wangxiaoming
	 * @param request
	 * @param response
	 * @return
	 */
	public  int getTaskDataCount(String sourceId){
		Condition condition = Condition.parseCondition("sourceid_string_eq");
		condition.setValue(sourceId);
		Condition condition2 = Condition.parseCondition("registermodel_string_eq");
		condition2.setValue("1");
		List<Condition> lts= new ArrayList<Condition>();
		lts.add(condition);
		lts.add(condition2);
		List<TaskJob> tjs = taskJobBiz.find(lts);
		int i=0;
		for (TaskJob taskJob : tjs) {
			
			//i += datumBiz.queryTaskDataCountBytaskId(taskJob.getId());
			i += mgDatumBiz.findByTaskId(taskJob.getId());
		}
		return i;
	}
	
	/**
	 * 功能：查询该知识模板下所有任务采集数据的总数
	 * @author wangxiaoming
	 * @return
	 */
	public  int getTaskDataCountByKnowledge(String knowledgeModel){
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
			i += mgDatumBiz.findByTaskId(taskJob.getId());
		}
		return i;
	}
	
	/**
	 * 功能：页面跳转采集数据详情（按知识源）
	 * @author wangxiaoming
	 * @param request
	 * @param response
	 * @return
	 */
	public  ModelAndView toTaskDataDetailPage(HttpServletRequest request,HttpServletResponse response){
		String sourceId=getStringValue(request, "id");
		ModelAndView model = new ModelAndView();
		model.addObject("sourceId", sourceId);
		model.setViewName("km/crawler/count/TaskDataDetailBySouece");
		return model;
	}
	
	/**
	 * 功能：页面跳转采集数据详情（按知识模板）
	 * @author wangxiaoming
	 * @param request
	 * @param response
	 * @return
	 */
	public  ModelAndView toTaskDataDetailPageByKnowledge(HttpServletRequest request,HttpServletResponse response){
		String knowledgeModel=getStringValue(request, "id");
		ModelAndView model = new ModelAndView();
		model.addObject("knowledgeModel", knowledgeModel);
		model.setViewName("km/crawler/count/TaskDataDetailByKnowledge");
		return model;
	}
	
	/**
	 * 功能：获取采集数据详情表（按知识源）
	 * @author wangxiaoming
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTaskDataList(HttpServletRequest request,HttpServletResponse response){
		String sourceId=getStringValue(request, "sourceId");
		ListPageQuery parameters = new ListPageQuery();
		
		Condition condition = Condition.parseCondition("sourceid_string_eq");
		condition.setValue(sourceId);
		Condition condition2 = Condition.parseCondition("registermodel_string_eq");
		condition2.setValue("1");
		List<Condition> lts= new ArrayList<Condition>();
		lts.add(condition);
		lts.add(condition2);
		parameters.getConditions().getItems().addAll(lts);
		new ServletRequestDataBinder(parameters).bind(request);
		List<TaskJob> tjs = taskJobBiz.find(parameters.getPagination(), lts);
		for (TaskJob taskJob : tjs) {
			//int	i = datumBiz.queryTaskDataCountBytaskId(taskJob.getId());
			long i = mgDatumBiz.findByTaskId(taskJob.getId());
			taskJob.setTaskjobDataCount(i);
		}
		return this.putToModelAndViewJson(tjs, parameters);
	}
	
	/**
	 * 功能：获取采集数据详情表（按知识模板）
	 * @author wangxiaoming
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTaskDataListByKnowledge(HttpServletRequest request,HttpServletResponse response){
		String knowledgeModel=getStringValue(request, "knowledgeModel");
		ListPageQuery parameters = new ListPageQuery();
		Condition condition = Condition.parseCondition("knowledgemodel_string_eq");
		condition.setValue(knowledgeModel);
		Condition condition2 = Condition.parseCondition("registermodel_string_eq");
		condition2.setValue("2");
		List<Condition> lts= new ArrayList<Condition>();
		lts.add(condition);
		lts.add(condition2);
		parameters.getConditions().getItems().addAll(lts);
		new ServletRequestDataBinder(parameters).bind(request);
		List<TaskJob> tjs = taskJobBiz.find(parameters.getPagination(), lts);
		for (TaskJob taskJob : tjs) {
			//获取该采集任务对应的知识源名称
			String sourceName=getTaskJobSourcename(taskJob.getSourceId());
			//获取该采集任务对应采集的数据量
			long taskjobDataCount=getTaskJobDataCount(taskJob.getConnectId());
			
			
			taskJob.setSourceName(sourceName);
			taskJob.setTaskjobDataCount(taskjobDataCount);
		}
		return this.putToModelAndViewJson(tjs, parameters);
	}
	
	/**
	 * 功能：查询单个采集任务采集的数据量
	 * @param response
	 */
	public long getTaskJobDataCount(String taskId){
		//int	i = datumBiz.queryTaskDataCountBytaskId(taskId);
		long i =mgDatumBiz.findByTaskId(taskId);
		return i;
	}
	
	/**
	 * 功能：根据采集任务获取知识源名称
	 * @return
	 */
	public String getTaskJobSourcename(String sourceId){
		KnowledgeSource source=knowledgeSourceBiz.findById(sourceId);
		return source.getName();
	}
	
	/**
	 * 功能：获取该知识模板下的知识源数量
	 * @return
	 */
	public int getSourcecountByKnowledge(String knowledgeModel){
		int i=taskJobBiz.querySourceCountByKnowledge(knowledgeModel);
		return i;
	}
}
