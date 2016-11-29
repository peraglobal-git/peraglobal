package com.peraglobal.pdp.admin.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;
import com.peraglobal.pdp.admin.biz.ResourceMessageBiz;
import com.peraglobal.pdp.common.json.JsonUtils;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;

/**
 *  <code>LangController.java</code>
 *  <p>功能:国际化资源文件处理Controller类
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author wenqihui qihui.wen@peraglobal.com 时间 2015-8-3	
 *  @version 1.0 
 *  </br>最后修改人 无
 */

@Controller
public class LangController  extends ExtendedMultiActionController{

	private Logger logger = LoggerFactory.getLogger(LangController.class);
	
	@Resource
	private ResourceMessageBiz resourceMessageBiz;
	
	/**
	 * 功能:查询资源信息页面跳转
	 * <p>作者 wenqihui 2015-8-3
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toResourceMessageList(HttpServletRequest request,HttpServletResponse response){
		ModelAndView view = new ModelAndView(getSkinPageName()+"lang/findResourceMessageList");
		return view ;
	}
	
	/**
	 * 功能:查询资源信息json
	 * <p>作者 wenqihui 2015-8-3
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findResourceMessageList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		String key = getStringValue(request,"key");
		List<Map<String,String>> list  = resourceMessageBiz.findResourceMessageList(key, parameters.getPagination());
		return this.putToModelAndViewJson(list, parameters);
	}

	/**
	 * 功能:修改资源信息
	 * <p>作者 wenqihui 2015-8-3
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveResourceMessage(HttpServletRequest request,HttpServletResponse response){
		String data = getStringValue(request,"data");
		Map<String,String> result = Maps.newHashMap();
		try {
			@SuppressWarnings("unchecked")
			Map<String,String>  map = JsonUtils.readObject(data, Map.class);
			resourceMessageBiz.saveResourceMessageMap(map);
			result.put("code", "success");
		} catch (JsonParseException e) {
			result.put("error", "修改资源异常");
			logger.error("saveResourceMessage exception",e);
		} catch (JsonMappingException e) {
			result.put("error", "修改资源异常");
			logger.error("saveResourceMessage exception",e);
		} catch (IOException e) {
			result.put("error", "修改资源异常");
			logger.error("saveResourceMessage exception",e);
		}
		return putToModelAndViewJson(result);
	}
}
