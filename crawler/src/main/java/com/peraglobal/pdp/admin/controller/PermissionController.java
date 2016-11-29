package com.peraglobal.pdp.admin.controller;


import java.util.ArrayList;
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

import com.peraglobal.pdp.admin.biz.ModuleBiz;
import com.peraglobal.pdp.admin.biz.PermissionBiz;
import com.peraglobal.pdp.admin.model.Module;
import com.peraglobal.pdp.admin.model.Permission;
import com.peraglobal.pdp.admin.shiro.LimitCredentialsMatcher;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;

/**
 *  <code>PermissionController.java</code>
 *  <p>功能:权限Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波  时间 2015-6-1	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class PermissionController extends ExtendedMultiActionController{
	

	private static final Logger LOG = LoggerFactory.getLogger(LimitCredentialsMatcher.class);
	
	@Resource
	private PermissionBiz permissionBiz;
	
	@Resource
	private ModuleBiz moduleBiz;
	
	
	/**
	 * 转到权限页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toPermissionPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"permission/permissionPage");
		return view;
	}
	
	/**
	 * 获取权限列表
	 * @param request
	 * @param response
	 */
	public ModelAndView getDataList(HttpServletRequest request,HttpServletResponse response) {
		
		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);	
		List<Permission> list = permissionBiz.find(parameters.getPagination(),parameters.getConditions());
		 
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String,Module> module = new HashMap<String,Module>();
		
		
		//关联模块名称,暂时写法,不推荐,单纯的显示,可以在实体类上扩充一个字段用于存储显示名称
		List<Module> all = moduleBiz.findAll();
		
		for(Module m : all) {
			module.put(m.getId(), m);
		}
		
		for(Permission p : list) {
			Map<String,Object> result = new HashMap<String, Object>();
			result.put("permissionName", p.getPermissionName());
			result.put("permissionCode", p.getPermissionCode());
			result.put("createDate", p.getCreateDate());
			result.put("id", p.getId());
			result.put("moduleId", p.getModuleId());
			Module mo = module.get(p.getModuleId());
			if(mo != null) {
				result.put("permissionModuleName", mo.getModuleName());
			}
			resultList.add(result);
		}
		//关联模块名称结束
		
		
	
		return this.putToModelAndViewJson(resultList, parameters);
	}
	
	
	/**
	 * 转到新增修改页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddPermissionPage(HttpServletRequest request,HttpServletResponse response,Permission permission) {
		
		ModelAndView view = new ModelAndView();
				
		//获取Id
		String id = request.getParameter("id");
		
		//转到添加或者修改页面
		view.setViewName(getSkinPageName()+"permission/addAndModifyPage");
		if(StringUtils.isNotEmpty(id)) {
			Permission p = permissionBiz.findById(id);
			view.addObject("permission", p);
		}else {
			//没有ID,所以添加Permission
			String moduleId = request.getParameter("moduleId");
			if(StringUtils.isNotEmpty(moduleId)) {
				permission.setModuleId(moduleId);
				view.addObject("permission", permission);
			}
		}
		return view;
	}
	
	
	/**
	 * 保存权限设置
	 * @param request
	 * @param response
	 * @param permission
	 */
	public ModelAndView saveOrUpdate(HttpServletRequest request,HttpServletResponse response,Permission permission) {
		Map<String,String> map = new HashMap<String,String>();
		//通过ID判断是新增还是删除
		String id = request.getParameter("id");
		Boolean checkName = permissionBiz.checkName(id,permission.getPermissionCode());
		try {
			if(checkName) {
				permissionBiz.save(permission);
				map.put("success", "success");
			}else {
				map.put("success", "repeate");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			LOG.error("保存权限失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 */
	public ModelAndView delPermission(HttpServletRequest request,HttpServletResponse response) {
		String ido = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		
		try {
			if(StringUtils.isNotEmpty(ido)) {
				List<String> ids = CollectionUtil.toStringCollection(ido);
				permissionBiz.deleteByIds(ids);
				result.put("success", "success");
			}else {
				result.put("success", "false");
				result.put("msg", "请选择要删除的数据");
			}
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			result.put("msg", "删除失败");
			LOG.error("删除权限失败",e);
			return JsonModelAndView.newSingle(result);
		}
	}
}
