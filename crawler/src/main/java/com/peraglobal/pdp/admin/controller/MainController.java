package com.peraglobal.pdp.admin.controller;

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
import org.springframework.web.servlet.support.RequestContext;

import com.google.common.collect.Lists;
import com.peraglobal.pdp.admin.biz.ModuleBiz;
import com.peraglobal.pdp.admin.model.Module;
import com.peraglobal.pdp.admin.shiro.PDPShiroRealm;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.SingleFieldCondition;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
import com.peraglobal.pdp.modeler.biz.GlobalConfigBiz;
import com.peraglobal.pdp.modeler.model.GlobalConfig;

/**
 *  <code>LoginController.java</code>
 *  <p>功能:登录Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 文齐辉 qihui.wen@peraglobal.com 时间 2015-4-28	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class MainController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
	
	@Resource
	private PDPShiroRealm realm ;
	
	@Resource
	private ModuleBiz moduleBiz;
	
	@Resource
	private GlobalConfigBiz globalConfigBiz;
	/**
	 * 功能 跳转到模拟的链表页面
	 * <p>作者 郭宏波 2015-5-7
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView main(HttpServletRequest request,HttpServletResponse response) {
		RequestContext rc = new RequestContext(request);
//		LOG.debug("***********{}************" , rc.getMessage("add.button.text"));
//		SecurityUtils.getSubject().checkRole("user");
		ModelAndView view;
		view = new ModelAndView(getSkinUiName()+"main");
		if(realm==null){
			realm = AppConfigUtils.getBean("pdpShiroRealm");
		}
		realm.clearCachedAuthorizationInfo(AdminConfigUtils.getCurrentUser());
/*		if(list.size() == 0) {
			view.setViewName("login");
		}*/

		Condition condition = Condition.parseCondition("code_eq");
		condition.setValue("default_index_page");
		List<GlobalConfig> globalConfig = globalConfigBiz.find(condition);
		if(StringUtil.isNotEmpty(globalConfig)){
			view .addObject("default_index_page", globalConfig.get(0).getValue());
		}
		return view;
	}
	
	
	/**
	 * 转到系统管理页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSystemProject(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		String moduleId = getStringValue(request,"moduleId");
		view.setViewName(getSkinUiName()+"system/system");
	/*	Condition condition1 = Condition.parseCondition("levelNum_int");
		condition1.setValue(2);
		Condition condition2 = Condition.parseCondition("parentId_int");
		condition2.setValue(moduleId);
		List<Module> moduleList = moduleBiz.find(Lists.newArrayList(condition1, condition2));*/
		List<Module> resultList = Lists.newArrayList();
		//获取当前用户有权限匹配二级模块
		List<Module> moduleList = AdminConfigUtils.getCurrentUser().getModuleList();
		for(Module m:moduleList){
			if(m.getId().equals(moduleId)){
				for(Module child:m.getChildList()){
					resultList.add(child);
				}
			}
		}
		view.addObject("module", moduleBiz.findById(moduleId));
		view.addObject("modules", resultList);
		return view;
	} 
	
	
	
	/**
	 * 根据ID获取查询后的二级菜单
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "static-access", "unused" })
	private List<Module> getSecondModuleList(long id) {
		Condition condition = new SingleFieldCondition();
		String con = "parentId_eq";
		condition = condition.parseCondition(con);
		condition.setValue(id);
		List<Module> list = moduleBiz.find(condition);
		return list;
	}
	
	

	/**
	 * 功能: 获取当前用户的权限json
	 * <p>作者 文齐辉 2015-5-18
	 * @param request
	 * @param response
	 */
	public ModelAndView getPermission(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("permissions", AdminConfigUtils.getCurrentUser().getPermissionList());
		return JsonModelAndView.newSingle(result);
	}
	
	
	/**
	 * 转到全局属性页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toVelocityTestPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"velocityTest/listTestPage");
		return view;
	}
}
