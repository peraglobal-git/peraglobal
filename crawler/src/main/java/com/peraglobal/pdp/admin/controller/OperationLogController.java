package com.peraglobal.pdp.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.UserBiz;
import com.peraglobal.pdp.admin.biz.UserRoleBiz;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.utils.DateUtils;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
import com.peraglobal.pdp.operationlog.biz.OperationLogBiz;
import com.peraglobal.pdp.operationlog.model.OperationLog;

/**
 *  <code>OperationLogController.java</code>
 *  <p>功能:操作日志Controller类
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author wenqihui qihui.wen@peraglobal.com 时间 2015-6-25	
 *  @version 1.0 
 *  </br>最后修改人 无
 */

@Controller
public class OperationLogController extends ExtendedMultiActionController{
	
	@Resource
	private OperationLogBiz operationLogBiz;
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
	private UserRoleBiz userRoleBiz;
	/**
	 * 功能:日志查询页面跳转
	 * <p>作者 wenqihui 2015-6-25
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findOperationLogList(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView(getSkinPageName()+"operationlog/findOperationLogList");
	}

	/**
	 * 功能:查找列表Json,当前逻辑，一个用户不可以同时具有一个以上的三员角色
	 * <p>作者 wenqihui 2015-6-25
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findOperationLogJosnList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List<OperationLog> list = null;
		if ("true".equalsIgnoreCase(AppConfigUtils.get("admin.log.syrole"))) {
			//系统管理员角色ID,安全保密员 角色ID,安全审计员 角色ID
			String[] Ids={"10004001","10004002","10004003"};
			List<String> allRole = userRoleBiz.findRoleIdsListByUserId(AdminConfigUtils.getCurrentUser().getId());
			List<String> sysUserIds = userRoleBiz.findUsersListByRoleId(Ids[0].toString());
			List<String> checkUserIds = userRoleBiz.findUsersListByRoleId(Ids[2].toString());
			List<String> safeUserIds = userRoleBiz.findUsersListByRoleId(Ids[1].toString());
			list = operationLogBiz.findLogListSY(parameters.getPagination(), parameters.getConditions().getItems(), allRole, sysUserIds, checkUserIds, safeUserIds);
		}else{
			list = operationLogBiz.find(parameters.getPagination(), parameters.getConditions());
		}

		ModelAndView modelAndView =  this.putToModelAndViewJson(list, parameters);
		return modelAndView;
	}
	
}
