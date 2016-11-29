package com.peraglobal.pdp.admin.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.common.utils.UtilMisc;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.Status;
import com.peraglobal.pdp.core.metadata.ChineseName;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
import com.peraglobal.pdp.core.utils.DynamicPropertyUtils;
import com.peraglobal.pdp.modeler.biz.DynamicModelPropertyBiz;
import com.peraglobal.pdp.modeler.model.DynamicModelProperty;

/**
 *  <code>DynamicModelPropertyController.java</code>
 *  <p>功能:动态属性Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 文齐辉 qihui.wen@peraglobal.com 时间 2015年11月27日 下午1:35:23	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class DynamicModelPropertyController extends ExtendedMultiActionController{

	private static final Logger logger = LoggerFactory.getLogger(DynamicModelPropertyController.class);
	
	@Resource
	private DynamicModelPropertyBiz dynamicModelPropertyBiz ;
	
	
	/**
	 * 功能:list页面跳转
	 * <p>作者文齐辉 2015年11月27日 下午1:38:50
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listPage(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView(getSkinPageName()+"dynamicProperty/dynamicPropertyList");
	}

	/**
	 * 功能:前段获取列表数据json
	 * <p>作者文齐辉 2015年11月27日 下午1:42:40
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery page = bindToListPageQuery(request);
		List<DynamicModelProperty> list = dynamicModelPropertyBiz.find(page);
		return putToModelAndViewJson(list, page);
	}
	
	/**
	 * 功能:保存页面跳转
	 * <p>作者文齐辉 2015年11月27日 下午1:45:40
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView savePage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView model = new ModelAndView(getSkinPageName()+"dynamicProperty/saveDynamicProperty");
		String id = getStringValue(request,"id");
		List<Map<String,String>> modelClassList = Lists.newArrayList();
		if(!StringUtil.isEmpty(id)){
			DynamicModelProperty property = dynamicModelPropertyBiz.findById(id);
			model.addObject("obj",property);
		}
		//获取所有需要扩展的实体类
		for(Class<?> clazz : AppConfigUtils.getModelEntityList()){
			if(DynamicPropertyUtils.isDynamicProperty(clazz)){
				String text = null;
				if(clazz.isAnnotationPresent(ChineseName.class)){
					text = clazz.getAnnotation(ChineseName.class).value();
				}else{
					text = clazz.getSimpleName();
				}
				/*Map<String,String> map = Maps.newHashMap();
				map.put("value", clazz.getName());
				map.put("text", text);*/
				modelClassList.add(UtilMisc.toMap("value", clazz.getName(),"text",text));
			}
		}
		model.addObject("modelClassList",modelClassList);
		return model;
	}
	
	/**
	 * 功能:保存处理
	 * <p>作者文齐辉 2015年11月27日 下午1:45:40
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response,DynamicModelProperty dynamicProperty){
		Map<String,String> result = Maps.newHashMap();
		try{
			dynamicModelPropertyBiz.save(dynamicProperty);
			result.put("code", "ok");
		}catch(Exception e){
			logger.error("报错动态属性异常",e);
			result.put("code", "fail");
		}
		return putToModelAndViewJson(result);
	}
	
	/**
	 * 删除处理
	 * @param request
	 * @param response
	 */
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response) {
		String[] ids = getArrayValue(request,"id");
		Map<String,String> result = new HashMap<String,String>();
		try {
			if(ids!=null){
				dynamicModelPropertyBiz.deleteByIds(Arrays.asList(ids));
				result.put("code", "ok");
			}
		}catch(Exception e) {
			result.put("code", "fail");
			result.put("msg", "删除失败");
			logger.error("删除自定义属性失败",e);
		}
		return this.putToModelAndViewJson(result);
	}
	
	/**
	 * 修改状态处理
	 * @param request
	 * @param response
	 */
	public ModelAndView updateStatus(HttpServletRequest request,HttpServletResponse response) {
		String[] ids = getArrayValue(request,"id");
		String status = getStringValue(request,"status");
		Map<String,String> result = new HashMap<String,String>();
		try {
			DynamicModelProperty property = new DynamicModelProperty();
			property.setStatus(Status.valueOf(status));
			dynamicModelPropertyBiz.updateBatch(property,Arrays.asList(ids));;
			result.put("code", "ok");
		}catch(Exception e) {
			result.put("code", "fail");
			result.put("msg", "删除失败");
			logger.error("删除自定义属性失败",e);
		}
		return this.putToModelAndViewJson(result);
	}
	
	/**
	 * 功能:根据modelclassName查询扩展属性
	 * <p>作者文齐辉 2015年12月1日 下午4:40:34
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findDynamicPropertyByClass(HttpServletRequest request,HttpServletResponse response){
		String model = getStringValue(request,"model");
		List<DynamicModelProperty> dmpList = dynamicModelPropertyBiz.findByModelClassName(model);
		return JsonModelAndView.newSingle(dmpList);
	}
}
