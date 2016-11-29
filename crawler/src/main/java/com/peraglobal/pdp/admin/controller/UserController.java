package com.peraglobal.pdp.admin.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.MailBiz;
import com.peraglobal.pdp.admin.biz.OrgBiz;
import com.peraglobal.pdp.admin.biz.RoleBiz;
import com.peraglobal.pdp.admin.biz.SessionManagerBiz;
import com.peraglobal.pdp.admin.biz.UserBiz;
import com.peraglobal.pdp.admin.biz.UserRoleBiz;
import com.peraglobal.pdp.admin.license.exception.LicenseException;
import com.peraglobal.pdp.admin.license.exception.UserCountException;
import com.peraglobal.pdp.admin.model.CurrentUserInfo;
import com.peraglobal.pdp.admin.model.Org;
import com.peraglobal.pdp.admin.model.Role;
import com.peraglobal.pdp.admin.model.User;
import com.peraglobal.pdp.admin.model.UserRole;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.DateUtils;
import com.peraglobal.pdp.common.utils.MD5Utils;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.Status;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.operationlog.biz.OperationLogBiz;
import com.peraglobal.pdp.operationlog.model.OperationLog;

/**
 *  <code>UserController.java</code>
 *  <p>功能:用户Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波  时间 2015-5-14	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class UserController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	
	@Resource
	private UserBiz userBiz;
	
	@Resource
    private MailBiz mailBiz;

	
	@Resource
	private OrgBiz orgBiz;
	
	@Resource
	private UserRoleBiz userRoleBiz;
	
	@Resource
	private RoleBiz roleBiz;

	@Resource
	private SessionManagerBiz sessionManagerBiz;
	@Resource
	private OperationLogBiz operationLogBiz;

	/**
	 * 功能获取用户列表,如果传入了组织id,则获取该组织下的人员
	 * <p>作者 郭宏波 2015-5-14
	 * @param request
	 * @param response
	 * @param orgId 	组织ID
	 * @return	ModelAndView
	 */
	public ModelAndView getUserList(HttpServletRequest request,HttpServletResponse response) {
		
		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);
		//查询获得用户集合
		List<User> users = userBiz.find(parameters.getPagination(),parameters.getConditions());
		
		//如果有用户,则给用户添加自己的部门
		if(users != null) {
			
			//获取所有的部门列表,为user添加部门
			List<Org> list = orgBiz.findAll();
			/*
			 * 将所有的部门存放到同一个map中,方便快速通过id获取对应的org对象.
			 * 好处:减少多次数据库连接
			 * 坏处:如果org数据表中存放数据过多,则map占用内存较多
			 * key为long类型的ID
			 * value为Org类型的对象
			 */
			Map<String,Org> map = new HashMap<String,Org>();
			if(list != null && list.size() > 0) {
				for(Org org : list) {
					map.put(org.getId(), org);
				}
			}
			
			//给user对象添加归属部门
			for(User user : users) {
				Org org = map.get(user.getOrgId());
				if(org != null) {
					user.setOrgName(org.getName());
				}
				//返回用户角色名称
				user.setRoleName(getRoleName(user));
			}
		}
		//将分页结果返回到页面
		return this.putToModelAndViewJson(users, parameters);
	}
	
	/**
	 * 根据用户返回用户角色名称
	 * @param user
	 * @return
	 */
	public String getRoleName(User user){
		String roleName = "";
		String query = "userId_eq";
		Condition condition = Condition.parseCondition(query);
		condition.setValue(user.getId());
		List<UserRole> userRoleList = userRoleBiz.find(condition);
		for (UserRole userRole : userRoleList) {
			Role role = roleBiz.findById(userRole.getRoleId());
			if(null != role && role.getStatus() == Status.Normal){
				roleName += role.getRoleName() + ",";				
			}
		}
		if("".equals(roleName))
			return "";
		return roleName.substring(0, roleName.length()-1);
	}
	
	/**
	 * 转到新增或者修改页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddUserPage(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		
		String sId = request.getParameter("id");
		String orgId = request.getParameter("orgId");
		String orgName = request.getParameter("orgName");
		try {
			//用户添加页面需要显示中文,所以需要对中文解码
			if(StringUtils.isNotEmpty(orgName)) {
				orgName = new String(URLDecoder.decode(orgName,"utf-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//如果存在id,则说明是修改,如果没有id则说明是新增页面
		if(StringUtils.isNotEmpty(sId)) {
			User user = userBiz.findById(sId);
			view.addObject("user",user);
			view.addObject("id",sId);
			view.addObject("type", "修改用户");
		}else {
			view.addObject("type", "新增用户");
		}
		view.addObject("orgId", orgId);
		view.addObject("orgName",orgName);
		view.setViewName(getSkinPageName()+"user/addAndModifyPage");
		//转到页面
		return view;
	}
	
	
	
	/**
	 * 新增或者修改用户
	 * <p>根据是否有ID判断新增或者修改,返回成功或者错误
	 * @param request
	 * @param response
	 * @param user
	 */
	public ModelAndView saveOrUpdateUser(HttpServletRequest request,HttpServletResponse response,User user) {
		//返回信息
		Map<String,String> map = new HashMap<String,String>();
		try{
			//组织ID
			String orgId = request.getParameter("orgId");
			
			//判断是否重名,如果重名则直接返回
			Boolean checkName = userBiz.checkName(user.getUsername(),user.getId());
			if(!checkName) {
				map.put("success", "repeate");
				return JsonModelAndView.newSingle(map);
			}
			//获取用户id,如果存在是更新,不存在就是新增
			if(user.getId() != null) {
				userBiz.save(user);
			}else {
				if(StringUtil.isEmpty(orgId)) orgId="1"; 
				user.setOrgId(orgId);
				String password = user.getPassword();
				user.setPassword(MD5Utils.encrypt(password));
				user.setStatus(Status.Disable);
				try{
					userBiz.save(user);
				}catch(LicenseException e){
					if(e instanceof UserCountException){
						map.put("success", "false");
						map.put("msg", "超出系统允许的用户数量，请删除后再试");
						return JsonModelAndView.newSingle(map);
					}
				}
			}
			map.put("success", "success");
			map.put("id", user.getId());
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			LOG.error("添加用户失败",e);
			map.put("success", "false");
			return JsonModelAndView.newSingle(map);
		}
	}
		
	
	/**
	 * 删除用户
	 * @param request 
	 * @param response
	 */
	public ModelAndView delUser(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		
		try{
			String ids = request.getParameter("id");
			if(StringUtils.isNotEmpty(ids)) {
				String[] split = ids.split(",");
				for(String id : split) {
					userBiz.deleteById(id);
				}
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			map.put("msg", "删除失败");
			LOG.error("删除用户失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 功能转到重置密码页面
	 * <p>作者 郭宏波 2015-5-25
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toResetPasswordPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		view.setViewName(getSkinPageName()+"user/resetPassword");
		
		return view;
	}
	
	
	/**
	 * 功能转到设置密集页面
	 * <p>作者 郭宏波 2015-5-25
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSetDegreePage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		view.setViewName(getSkinPageName()+"user/setDegree");
		
		return view;
	}
	
	
	
	/**
	 * 功能转到设置角色页面
	 * <p>作者 郭宏波 2015-5-25
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSetUserRolePage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		view.setViewName(getSkinPageName()+"user/setDegree");
		
		return view;
	}
	
	
	
	/**
	 * 功能转到修改密码页面	
	 * <p>作者 郭宏波 2015-5-25
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toChangePasswordPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView(getSkinPageName()+"user/changeUserPasswordPage");
		return view;
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
		String loginfo = "";
		if(StringUtils.isNotEmpty(ids)) {
			Boolean enableRoleSucc = userBiz.enableOrDisableRole(ids,enableOrDisable);
			if(enableRoleSucc) {
				map.put("success", "success");
				List<String> userids = CollectionUtil.toStringCollection(ids);
				List<User> userList = userBiz.findByIds(userids);
				if(userList!=null && userList.size()>0){
					for(User user : userList){
						loginfo +=","+user.getRealName()+"("+user.getUsername()+")";
					}
				}
				//操作日志
	            OperationLog log = new OperationLog();
	            log.setModelName("用户管理");
	            log.setModelId("sys");
	            if(loginfo.length()>0){
	            	loginfo = loginfo.substring(1);
	            }
	            if("enable".equals(enableOrDisable)){
	            	log.setOperationType("启用");
	            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")启用了用户:["+loginfo+"]");
	            }else{
	            	log.setOperationType("停用");
	            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")停用了用户:["+loginfo+"]");
	            }
	            operationLogBiz.saveCustomLog(log);
			}
		}
		return JsonModelAndView.newSingle(map);
	}
	
	
	/**
	 * 转到重置密码页面
	 * @param request
	 * @param response
	 */
	public ModelAndView toReSetPassword(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		String id = request.getParameter("id");
		if(StringUtils.isNotEmpty(id)) {
			view.addObject("id", id);
			view.setViewName(getSkinPageName()+"user/reSetUserPasswordPage");
			return view;
		}
		return null;
	}
	
	
	
	
	/**
	 * 保存重置的密码
	 * @param request
	 * @param response
	 */
	public ModelAndView saveSetPassword(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("success", "false");
		map.put("err", "重置密码失败");
		
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		try {
			if(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(password)) {
				List<User> list = new ArrayList<User>();
				User user = userBiz.findById(id);
				if(user != null) {
					user.setPassword(MD5Utils.encrypt(password));
					list.add(user);
					userBiz.updateBatchAndSendMail(list,user,password);
					map.put("success", "success");
				}
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			LOG.error("保存重置密码错误",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	/**
	 * 转到用户页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toUserPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"user/userPage");
		return view;
	}
	
	
	/**
	 * 转到设置角色页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSetUserRole(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		
		String id = request.getParameter("id");
		//根据当前用户,找到对应用户的角色,用于回显
		String query = "userId_eq";
		Condition condition = Condition.parseCondition(query);
		condition.setValue(id);
		List<UserRole> ur = userRoleBiz.find(condition);
		
		//拼接用户角色ID
		String ids = "";
		if(ur.size() > 0) {
			for(UserRole userRole : ur) {
				ids = ids + userRole.getRoleId() + ",";
			}
			ids = ids.substring(0, ids.lastIndexOf(","));
		}
		
		if(StringUtils.isNotEmpty(ids)) {
			view.addObject("ids", ids);
		}
		
		if(StringUtils.isNotEmpty(id)) {
			view.setViewName(getSkinPageName()+"user/setUserRolePage");
			view.addObject("id", id);
			return view;
		}
		return null;
	}
	
	
	
	/**
	 * 保存用户角色
	 * @param request
	 * @param response
	 */
	public ModelAndView saveUserRole(HttpServletRequest request,HttpServletResponse response) {
		//角色ID 多个以","分隔
		String ids = request.getParameter("ids");
		//用户ID 
		String userId = this.getStringValue(request, "userId");
		//结果map
		Map<String,String> map = new HashMap<String,String>();
		
		/*
		 *将角色权限对应的ID批量保存
		 */
		try {
			if(userId != null && !userId.equals("0")) {
				userRoleBiz.deleteRoleByUserId(userId);
				
				String[] id = ids.split(",");
				if(StringUtils.isNotEmpty(ids)) {
					List<UserRole> list = new ArrayList<UserRole>();
					for(String i : id) {
						UserRole ur = new UserRole();
						ur.setUserId(userId);
						ur.setRoleId(i);
						list.add(ur);
					}
					userRoleBiz.saveBatch(list);
					
					String roleNames = roleBiz.getRoleNameByIds(id);
					User user = userBiz.findById(userId);
					user.setRoleName(roleNames);
					userBiz.save(user);
					
					map.put("success", "success");
					
				}else {
					map.put("success", "false");
				}
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "error");
			LOG.error("保存用户角色失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 保存修改密码
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveChangePassword(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		
		String password = request.getParameter("password");
		String newPassword = request.getParameter("newPassword");
		String newConfirmPassword = request.getParameter("newConfirmPassword");
		
		try {
			if(StringUtil.isNotEmpty(password) && StringUtil.isNotEmpty(newPassword) && StringUtil.isNotEmpty(newConfirmPassword)) {
				password = MD5Utils.encrypt(password);
				newPassword = MD5Utils.encrypt(newPassword);
				newConfirmPassword = MD5Utils.encrypt(newConfirmPassword);
				User user = (User) SecurityUtils.getSubject().getPrincipal();
				String userPassword = user.getPassword();
				if(!password.equals(userPassword)) {
					map.put("success", "false");
					map.put("err", "原密码输入错误");
					return JsonModelAndView.newSingle(map);
				}
				if(!newPassword.equals(newConfirmPassword)) {
					map.put("success", "false");
					map.put("err", "两次输入的密码不一致");
					return JsonModelAndView.newSingle(map);
				}
				user.setPassword(newPassword);
				List<User> list = new ArrayList<User>();
				list.add(user);
				userBiz.updateBatch(list);
				map.put("success", "success");
			}else {
				map.put("success", "false");
				map.put("err", "必须输入密码");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			map.put("err", "保存密码错误");
			LOG.error("保存修改后的密码错误",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	/**
	 * 转到用户导入页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toImportUserPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"user/importUserPage");
		return view;
	}
	/**
	 * 用户模板下载
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void downLoadTemplateSystemUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("下载用户导入模板-----开始！！！");
		OutputStream outp = null;
		FileInputStream in = null;
		try {
			String fileName = "systemUser.xls";
			String ctxPath = request.getSession().getServletContext().getRealPath(File.separator) + File.separator + "template" + File.separator;
			String filedownload = ctxPath + fileName;
			logger.info("-------systemUser filedownload-----------" + filedownload);
			fileName = URLEncoder.encode(fileName, "UTF-8");
			// 要下载的模板所在的绝对路径
			response.reset();
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentType("application/octet-stream;charset=UTF-8");
			outp = response.getOutputStream();
			in = new FileInputStream(filedownload);
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) > 0) {
				outp.write(b, 0, i);
			}
			outp.flush();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
			if (outp != null) {
				outp.close();
				outp = null;
			}
		}
		logger.info("下载用户导入模板-----结束！！！");
	}

	/**
	 * 功能: 导入用户
	 * @param request
	 * @param response
	 */
	public void importSystemUser(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {
		logger.info("导入用户-----开始！！！");
		Map<String, String> results = new HashMap<String, String>();
		try {
			// 转型为MultipartHttpRequest
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			// 根据前台的name名称得到上传的文件
			MultipartFile file = multipartRequest.getFile("importExcel");
			results = userBiz.importSystemUser(file);
			outJsonHtml(response, results);
			logger.info("导入用户----成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			results.put("success", "false");
			results.put("errmsg", "操作失败.");
			outJsonHtml(response, results);
		}
		logger.info("导入用户-----结束！！！");
	}
   
	/**
	 * 跳转到部门列表页
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toMoveUserOrgList(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		String ids = request.getParameter("ids");
		view.addObject("ids", ids);
		view.setViewName(getSkinPageName()+"user/moveUserToOrg");
		return view;
	}
	
	/**
	 * 修改用户所属部门
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateUserOrg(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("success", "error");
		String ids = request.getParameter("ids");
		String orgId = request.getParameter("orgId");
		if(StringUtils.isNotEmpty(ids)) {
			Boolean changeOrg = userBiz.updateUserOrg(ids,orgId);
			if(changeOrg) {
				map.put("success", "success");
			}
		}
		return JsonModelAndView.newSingle(map);
	}
	
	/**
	 * 用户解除锁定
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView unlockUser(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("success", "error");
		String ids = request.getParameter("ids");
		String loginfo = "";
		if(StringUtils.isNotEmpty(ids)) {
			Boolean flag = userBiz.unlockUser(ids);
			if(flag) {
				map.put("success", "success");
				//查询用户名称
				List<String> userids = CollectionUtil.toStringCollection(ids);
				List<User> userList = userBiz.findByIds(userids);
				if(userList!=null && userList.size()>0){
					for(User user : userList){
						loginfo += ","+ user.getRealName()+"("+user.getUsername()+")";
					}
				}
				//操作日志
	            OperationLog log = new OperationLog();
	            log.setModelName("用户管理");
	            log.setModelId("sys");
	            if(loginfo.length()>0){
	            	loginfo = loginfo.substring(1);
	            }
	            log.setOperationType("解锁");
	            log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")解锁了用户:["+loginfo+"]");
	            operationLogBiz.saveCustomLog(log);

			}
		}
		return JsonModelAndView.newSingle(map);
	}
	/**
	 * 功能转到在线人数页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toUserOnLineList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		view.setViewName(getSkinPageName()+"login/userOnLineList");
        view.addObject("START_DATE", DateUtils.format(new Date(), null)); 
		return view;
	}
	
	/**
	 * 在线人数
	 * @return
	 */
	public ModelAndView onLineList(HttpServletRequest request, HttpServletResponse response) {
		// 获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		// 将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);
		List<CurrentUserInfo> list = sessionManagerBiz.findLineList(parameters.getPagination());
		return JsonModelAndView.newSingle(list);
	}

	/**
	 * 强制下线
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView offline(HttpServletRequest request,HttpServletResponse response){
		Map<String, String> map = new HashMap<String, String>();
		String sessionIds=request.getParameter("sessionIds");
		boolean f = false;
				f = sessionManagerBiz.forceLogout(sessionIds);
		if(f){
			map.put("success", "ok");
		}
		return JsonModelAndView.newSingle(map);
	}
	
	/**
	 * 功能:根据用户id查询用户信息，前段ajax使用
	 * <p>作者文齐辉 2015年11月3日 下午4:19:58
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findUserById(HttpServletRequest request,HttpServletResponse response){
		String id = getStringValue(request,"id");
		User user = userBiz.findById(id);
		return JsonModelAndView.newSingle(user);
	}
	
	
	/**
	 * 获取用户树,以部门方式关联
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findOrgUserTree(HttpServletRequest request,HttpServletResponse response){
		List<TreeNode> findAll = orgBiz.getTreeList(null);
		Map<String, List<TreeNode>> userTreeNode = this.getUserOrgTreeNode();
		createOrgTree(findAll, userTreeNode);
		return JsonModelAndView.newSingle(findAll);
	}
	
	//获取部门人员树
	private void createOrgTree(List<TreeNode> tns,Map<String,List<TreeNode>> m) {
		for (TreeNode treeNode : tns) {
			List<TreeNode> children = treeNode.getChildren();
			String orgId = treeNode.getId();
			List<TreeNode> list = m.get(orgId);
			if(children != null && !children.isEmpty()) {
				createOrgTree(children,m);
				if(list != null) {
					children.addAll(list);
				}
			}
		}
	}
	
	//获取user的treeMap
	private Map<String,List<TreeNode>> getUserOrgTreeNode() {
		Map<String,List<TreeNode>> m = new HashMap<String,List<TreeNode>>();
		
		List<User> all = userBiz.findAll();
		for (User user : all) {
			Map<String,String> attr = new HashMap<String,String>();
			attr.put("nodeType","person");
			String orgId = user.getOrgId();
			List<TreeNode> list = m.get(orgId);
			TreeNode n = new TreeNode();
			n.setId(user.getId());
			n.setText(user.getRealName());
			n.setAttributes(attr);
			if(list == null) {
				List<TreeNode> tList = new ArrayList<TreeNode>();
				tList.add(n);
				m.put(orgId, tList);
			}else {
				list.add(n);
			}
		}
		return m;
	}
	
	
	//获取userMap
	private Map<String,User> getUserMap() {
		Map<String,User> m = new HashMap<String,User>();
		
		List<User> all = userBiz.findAll();
		for (User user : all) {
			m.put(user.getId(), user);
		}
		return m;
	}
	
	//获取RoleUserMap
	private Map<String,List<TreeNode>> getRoleUserMap(Map<String,User> map) {
		List<UserRole> urList = userRoleBiz.findAll();
		Map<String,List<TreeNode>> userRoleMap = new HashMap<String,List<TreeNode>>();
		for (UserRole userRole : urList) {
			Map<String,String> attr = new HashMap<String,String>();
			attr.put("nodeType","person");
			List<TreeNode> list = userRoleMap.get(userRole.getRoleId());
			TreeNode tn = new TreeNode();
			User user = map.get(userRole.getUserId());
			if(user != null) {
				tn.setId(user.getId());
				tn.setText(user.getRealName());
				tn.setAttributes(attr);
				if(list != null && !list.isEmpty()) {
					list.add(tn);
				}else {
					List<TreeNode> nodeList = new ArrayList<TreeNode>();
					nodeList.add(tn);
					userRoleMap.put(userRole.getRoleId(), nodeList);
				}
			}
		}
		return userRoleMap;
	}
	
	//获取userRoleList
	private List<TreeNode> getRoleUserTreeNodeMap(Map<String,List<TreeNode>> map) {
		
		List<TreeNode> treeList = roleBiz.getTreeList();
		getRoleUserAll(treeList,map);
		return treeList;
	}
	
	//映射 role treenode
	private void getRoleUserAll(List<TreeNode> list,Map<String,List<TreeNode>> map) {
		if(list != null && !list.isEmpty()) {
			for (TreeNode treeNode : list) {
				if(treeNode != null) {
					List<TreeNode> list2 = map.get(treeNode.getId());
					List<TreeNode> children = treeNode.getChildren();
					if(children != null) {
						getRoleUserAll(children,map);
					}else{
						children = new ArrayList<TreeNode>();
					}
					if(list2 != null) {
						children.addAll(list2);
					}
				}
			}
		}
	}
	
	/**
	 * 获取用户树,以角色方式关联
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findRoleUserTree(HttpServletRequest request,HttpServletResponse response){
		Map<String, User> userMap = getUserMap();
		Map<String, List<TreeNode>> roleUserMap = getRoleUserMap(userMap);
		List<TreeNode> roleUserTreeNodeMap = getRoleUserTreeNodeMap(roleUserMap);
		return JsonModelAndView.newSingle(roleUserTreeNodeMap);
	}
	
	
	/**
	 * 选择用户
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSelectUserPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view){
		view.setViewName(getSkinPageName()+"user/selectUserPage");
		return view;
	}
}
