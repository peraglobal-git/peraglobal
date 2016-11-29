package com.peraglobal.km.crawler.quartz.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
@Controller
public class QuartzScheduleController extends ExtendedMultiActionController{
	
	private static final Logger log = LoggerFactory.getLogger(QuartzScheduleController.class);
	
	@Resource
    private TaskJobBiz taskJobBiz;
	
    @Resource
    private QuartzScheduleBiz quartzScheduleBiz;
    
    /**
	 * 功能：开始任务
	 * <p>作者 井晓丹 2016-1-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView startTask(HttpServletRequest request,HttpServletResponse response) {
		String id = request.getParameter("id");
		String[] ids = id.split(",");
		
		for(int i = 0; i < ids.length; i++){
			TaskJob taskJob = taskJobBiz.findById(ids[i]);
			quartzScheduleBiz.startJob(taskJob);
		}
		Map<String,String> result = new HashMap<String,String>();
		result.put("msg", "任务已开始！");
		return this.putToModelAndViewJson(result);
	}
	/**
	 * 功能：暂停任务
	 * <p>作者 井晓丹 2016-2-24
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView pauseTask(HttpServletRequest request,HttpServletResponse response) {
		String id = request.getParameter("id");
		String[] ids = id.split(",");
		for(int i = 0; i < ids.length; i++){
			TaskJob taskJob = taskJobBiz.findById(ids[i]);
			quartzScheduleBiz.pauseJob(taskJob);
		}
		Map<String,String> result = new HashMap<String,String>();
		result.put("msg", "任务已暂停！");
		return this.putToModelAndViewJson(result);
	}
	/**
	 * 功能：停止任务
	 * <p>作者 井晓丹 2016-2-24
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView stopTask(HttpServletRequest request,HttpServletResponse response) {
		String id = request.getParameter("id");
		String[] ids = id.split(",");
		for(int i = 0; i < ids.length; i++){
			TaskJob taskJob = taskJobBiz.findById(ids[i]);
			quartzScheduleBiz.stopJob(taskJob);
		}
		Map<String,String> result = new HashMap<String,String>();
		result.put("msg", "任务已停止！");
		return this.putToModelAndViewJson(result);
	}
    
    
    
}
