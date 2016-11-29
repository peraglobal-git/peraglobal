package com.peraglobal.pdp.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.ResourceBiz;
import com.peraglobal.pdp.admin.model.Resource;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;

/**
 *  <code>ResourcepdpController.java</code>
 *  <p>功能:资源Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 文齐辉 qihui.wen@peraglobal.com 时间 2015-4-30	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class ResourceController extends ExtendedMultiActionController{

	@Autowired
	private ResourceBiz resourceBiz ;
	
	/**
	 * 功能:保存页面跳转
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveResourcePage(HttpServletRequest request,HttpServletResponse response){
		String id = getStringValue(request,"id");
		ModelAndView view = new ModelAndView(getSkinPageName()+"resource/saveResource");
		if(!StringUtil.isEmpty(id)){
			Resource resource = resourceBiz.findById(id);
			view.addObject("obj",resource);
		}
		return view;
	}
	
	/**
	 * 功能:保存或修改
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveResource(HttpServletRequest request,HttpServletResponse response,Resource resource){
		Map<String, Object> result = new HashMap<String, Object>();
		String operateStr = null;
		if (resource.getId() == null) {
			operateStr = "添加";
		} else {
			operateStr = "修改";
		}
		try {
			resourceBiz.save(resource);
			result.put("code", "ok");
			operateStr += "成功！";
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveResource save exception", e);
		}
		result.put("id", resource.getId());
		result.put("msg", operateStr);
		return this.putToModelAndViewJson(result);
	}
	
	/**
	 * 功能:查找列表
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findResourceList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List<Resource> list = resourceBiz.find(parameters.getPagination(), parameters.getConditions());
		ModelAndView modelAndView =  this.putToModelAndView(getSkinPageName()+"resource/findResourceList",list, parameters);
		return modelAndView;
	}

	/**
	 * 功能:查找列表Json
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */ 
	public ModelAndView findResourceJosnList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List<Resource> list = resourceBiz.find(parameters.getPagination(), parameters.getConditions());
		return this.putToModelAndViewJson(list, parameters);
	}
	
	/**
	 * 功能:查找列表Json,对应Combox
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */ 
	public ModelAndView findResourceJosnCombox(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List<Resource> list = resourceBiz.find(null, parameters.getConditions());
		return JsonModelAndView.newSingle(list);
	}
	
	
	
	/**
	 * 功能:验证用户名是否重复
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 */
	public ModelAndView validateNameByAjax(HttpServletRequest request, HttpServletResponse response){
		String url = request.getParameter("url");
		String oldUrl = request.getParameter("oldUrl");
		if(url.equals(oldUrl)){
			return JsonModelAndView.newSingle(true);
		}
		Condition condition = Condition.parseCondition("url_eq");
		condition.setValue(url);
		List<Condition> conList = new ArrayList<Condition>();
		conList.add(condition);
		List<Resource> resourceList = resourceBiz.find(null, conList);
		if(!CollectionUtil.isEmpty(resourceList)){
			return JsonModelAndView.newSingle("资源URL重复");
		}
		return JsonModelAndView.newSingle(true);
	}
	
	
	
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 */
	public ModelAndView delResource(HttpServletRequest request,HttpServletResponse response) {
		String ido = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		try {
			if(StringUtils.isNotEmpty(ido)) {
				List<String> ids = CollectionUtil.toStringCollection(ido);
				resourceBiz.deleteByIds(ids);
				result.put("code", "success");
			}else {
				result.put("code", "fail");
				result.put("msg", "请选择要删除的数据");
			}
		}catch(Exception e) {
			result.put("code", "false");
			result.put("msg", "删除失败");
			logger.error("删除资源项失败",e);
		}
		return this.putToModelAndViewJson(result);
	}
	
	
}
