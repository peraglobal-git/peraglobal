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
import com.peraglobal.pdp.admin.biz.RoleBiz;
import com.peraglobal.pdp.admin.biz.RolePermissionBiz;
import com.peraglobal.pdp.admin.biz.UserRoleBiz;
import com.peraglobal.pdp.admin.model.Role;
import com.peraglobal.pdp.admin.model.RolePermission;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.annotation.OperationLog;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.Status;
import com.peraglobal.pdp.core.enums.YesNoEnum;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.BeanUtil;

/**
 *  <code>RoleController.java</code>
 *  <p>功能:角色Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波  时间 2015-5-14	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class RoleController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(RoleController.class);
	
	@Resource
	private RoleBiz roleBiz;
	@Resource
	private PermissionBiz permissionBiz;
	@Resource
	private RolePermissionBiz rolePermissionBiz;
	@Resource
	private UserRoleBiz userRoleBiz;

	@Resource
	private ModuleBiz moduleBiz;
	

	/**
	 * 转到角色页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toRolePage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"role/rolePage");
		return view;
	}
	
	/**
	 * 获取组织部门树
	 * <p>
	 * @param request
	 * @param response
	 */
	public ModelAndView getRoleTree(HttpServletRequest request,HttpServletResponse response) {
		//获取TreeNode
		List<TreeNode> treeList = roleBiz.getTreeList();
		//转换成Json格式返回
		return JsonModelAndView.newSingle(treeList);
	}
	
	
	/**
	 * 转到新增或者修改页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddRolePage(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("role");
		String parentId = request.getParameter("parentId");
		String id = request.getParameter("id");
		if(StringUtils.isNotEmpty(id)) {
			Role role = roleBiz.findById(id);
			view.addObject("role",role);
			view.addObject("id", id);
			view.addObject("parentId", parentId);
			view.addObject("type", "修改角色");
		}else {
			view.addObject("parentId", parentId);
			view.addObject("type", "新增角色");
		}
		view.setViewName(getSkinPageName()+"role/addAndModifyPage");
		return view;
	}
	
	
	/**
	 * 添加,修改角色
	 * @param request
	 * @param response
	 * @param role
	 */
	public ModelAndView addOrUpdateRole(HttpServletRequest request,HttpServletResponse response,Role role) {
		//创建返回消息使用的map.返回的消息使用的json格式
		Map<String,String> map = new HashMap<String,String>();
		try{
			//获取参数父ID
			String parentId = request.getParameter("parentId");
			//获取参数ID
			String sid = request.getParameter("id");
			//获取参数角色名称
			String roleName = role.getRoleName();
			//判断是否重名,checkName为布尔值,true重名,false不重名
			Boolean checkName = roleBiz.checkName(sid,parentId,roleName);
			//判断重名,如果重名了,则不进行操作,并返回提示信息.
			if(checkName) {
				//如果有ID存在则进行更新操作
				if(StringUtils.isNotEmpty(sid)) {
					//更新的集合
					List<Role> list = new ArrayList<Role>();
					//为了保证数据的完整性,先查询到对应的数据,然后set新的数据
					Role role2 = roleBiz.findById(sid);
					//set角色名称
					role2.setRoleName(role.getRoleName());
					//set描述
					role2.setDes(role.getDes());
					//将要更新的对象添加到集合中
					list.add(role2);
					//执行更新操作
					roleBiz.updateBatch(list);
				}else {
					//set父节点ID
					role.setParentId(parentId);
					//set状态 Normal正常,Disable无效
					role.setStatus(Status.Normal);
					//SortNum 排序
					role.setSortNum(roleBiz.findRoleMaxSortNumr()+1);
					//保存新增
					roleBiz.save(role);
				}
				//返回的消息
				map.put("success", "success");
				map.put("id", role.getId());
			}else {
				map.put("success","repeate");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			//操作异常的处理
			map.put("success", "error");
			if(role.getId() != null) {
				map.put("msg", "更新失败!");
			}else {
				map.put("msg", "新增失败!");
			}
			LOG.error("保存角色失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	

	

	
	/**
	 * 获取部门表格列表
	 * @param request
	 * @param response
	 */
	public ModelAndView getDataList(HttpServletRequest request,HttpServletResponse response) {
		
		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);

		//分页查询方式,第一个参数是分页,如果不需要分页则传null,第二个参数是查询条件集合
		List<Role> roleList = roleBiz.find(parameters.getPagination(),parameters.getConditions());

		Map<String, Role> map = BeanUtil.convertListToMap(roleList);
		//将分页结果返回到页面
		return this.putToModelAndViewJson(roleList, parameters);
	}
	
	
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 */
	public ModelAndView delRole(HttpServletRequest request,HttpServletResponse response) {
		String ido = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		
		try {
			if(StringUtils.isNotEmpty(ido)) {
				List<String> ids = CollectionUtil.toStringCollection(ido);
				roleBiz.deleteByIds(ids);
				result.put("success", "success");
			}else {
				result.put("success", "false");
				result.put("msg", "请选择要删除的数据");
			}
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			result.put("msg", "删除失败");
			LOG.error("删除角色失败",e);
			return JsonModelAndView.newSingle(result);
		}
	}
	
	
	/**
	 * 启用角色,可以是多个,以","链接
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
			Boolean enableRoleSucc = roleBiz.enableOrDisableRole(ids,enableOrDisable);
			if(enableRoleSucc) {
				map.put("success", "success");
			}
		}
		return JsonModelAndView.newSingle(map);
	}
	
	
	/**
	 * 转到权限分配页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toPermission(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("role");
		String id = request.getParameter("id");
		String query = "roleId_eq";
		Condition condition = Condition.parseCondition(query);
		condition.setValue(id);
		List<RolePermission> rolePermission = rolePermissionBiz.find(condition);
		String ids = "";
		if(rolePermission.size() > 0) {
			for(RolePermission r : rolePermission) {
				ids = ids + r.getPermissionId() + ",";
			}
			ids = ids.substring(0, ids.lastIndexOf(","));
		}
		
		if(StringUtils.isNotEmpty(ids)) {
			view.addObject("ids", ids);
		}
		view.addObject("id", id);
		view.setViewName(getSkinPageName()+"role/addOrUpdatePermission");
		return view;
	}
	
	
	/**
	 * 查询权限列表
	 * @param request
	 * @param response
	 */
	public ModelAndView getPermissionList(HttpServletRequest request,HttpServletResponse response) {
		String id = getStringValue(request, "id");
		// 获取树的节点 @link IsPermissionEnum.Yes
		List<TreeNode> moduleTree = moduleBiz.getModuleTreeRoleChecked(YesNoEnum.Yes,id);
		return JsonModelAndView.newSingle(moduleTree);
	}
	
	
	
	/**
	 * 保存角色权限列表
	 * @param request
	 * @param response
	 */
	@OperationLog(type="授权",text="授权")
	public ModelAndView saveRolePermission(HttpServletRequest request,HttpServletResponse response) {
		//角色ID
		String ids = request.getParameter("ids");
		//对应角色的权限ID 多个以","分隔
		String roleId = request.getParameter("roleId");
		//结果map
		Map<String,String> map = new HashMap<String,String>();
		
		/*
		 *将角色权限对应的ID批量保存
		 */
		try {
			if(StringUtils.isNotEmpty(roleId)) {
				rolePermissionBiz.delRolePermissionByRoleId(roleId);
				
				if(StringUtils.isNotEmpty(ids)) {
					List<RolePermission> list = new ArrayList<RolePermission>();
					String[] idArray = ids.split(",");
					for(String pid : idArray) {
						pid = StringUtils.substringAfter(pid, "_");
						RolePermission permission = new RolePermission();
						permission.setRoleId(roleId);
						permission.setPermissionId(pid);
						list.add(permission);
					}
					rolePermissionBiz.saveBatch(list);
					map.put("success", "success");
				}else {
					map.put("success", "false");
				}
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "error");
			LOG.error("保存角色权限关系失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 获得角色Map
	 * @param request
	 * @param response
	 */
	public ModelAndView getRoleMapList(HttpServletRequest request,HttpServletResponse response) {
		List<Role> userRoleList = userRoleBiz.getUserRoleList();
		return JsonModelAndView.newSingle(userRoleList);
	}
	
}
