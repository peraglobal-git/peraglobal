package com.peraglobal.pdp.admin.controller;

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
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.GlobalPropertiesBiz;
import com.peraglobal.pdp.admin.model.GlobalProperties;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;

/**
 *  <code>GlobalPropertiesController.java</code>
 *  <p>功能:全局配置Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波 hongbo.guo@peraglobal.com 时间 2015-6-10	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class GlobalPropertiesController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalPropertiesController.class);
	
	//全局配置业务类
	@Resource
	private GlobalPropertiesBiz globalPropertiesBiz;
	

	/**
	 * 转到添加全局属性配置页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toAddAndUpdatePage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		
		String id = getStringValue(request, "id");
		if(id == null) {
			view.addObject("type", "add");
		}else {
			view.addObject("type", "update");
		}
		
		view.setViewName(getSkinPageName()+"globalProperties/addAndModifyPage");
		
		return view;
	}
	
	
	/**
	 * 保存全局配置
	 * @param request
	 * @param response
	 * @param globalConfig
	 */
	public ModelAndView saveGlobalProperties(HttpServletRequest request,HttpServletResponse response,GlobalProperties globalProperties) {
		
		Map<String,String> map = new HashMap<String,String>();
		
		try{
			//获取对象的ID
			String id = globalProperties.getId();
			
			String code = globalProperties.getCode();
			
			Boolean checkCode = globalPropertiesBiz.checkCode(id,code);
			
			if(checkCode) {
				globalPropertiesBiz.save(globalProperties);
				map.put("success", "success");
			}else {
				map.put("success", "repeate");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			LOG.error("保存全局变量失败",e);
			map.put("success", "false");
			map.put("err", "保存失败");
			return JsonModelAndView.newSingle(map);
		}
	}
	
	/**
	 * 功能:获取列表
	 * <p>作者郭宏波 2015年6月15日 上午10:36:13
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getGlobalPropertiesList(HttpServletRequest request,HttpServletResponse response) {
		//获取所有的参数
		ListPageQuery parameters = new ListPageQuery();
		//与request与parameters绑定
		new ServletRequestDataBinder(parameters).bind(request);
		//根据分页参数,查询条件查询
		List<GlobalProperties> list = globalPropertiesBiz.find(parameters.getPagination(), parameters.getConditions());
		//以json格式返回
		return this.putToModelAndViewJson(list, parameters);
	}
	
	
	/**
	 * 根据id批量删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delGlobalProperties(HttpServletRequest request,HttpServletResponse response) {
		
		Map<String,String> map = new HashMap<String,String>();
		try{
			String ids = request.getParameter("id");
			if(StringUtil.isNotEmpty(ids)) {
				List<String> list = CollectionUtil.toStringCollection(ids);
				globalPropertiesBiz.deleteByIds(list);
				map.put("success", "success");
			}else {
				map.put("success", "false");
				map.put("msg", "请选择要删除的数据");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			map.put("msg", "删除数据失败");
			LOG.error("删除全局变量失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 启用用户,可以是多个,以","链接
	 * @param ids		多个id
	 * @param request
	 * @param response
	 */
	public ModelAndView enableOrDisableRole(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("success", "error");
		String ids = request.getParameter("ids");
		String enableOrDisable = request.getParameter("eod");
		if(StringUtils.isNotEmpty(ids)) {
			Boolean enableRoleSucc = globalPropertiesBiz.enableOrDisableRole(ids,enableOrDisable);
			if(enableRoleSucc) {
				map.put("success", "success");
			}
		}
		return JsonModelAndView.newSingle(map);
	}
}
