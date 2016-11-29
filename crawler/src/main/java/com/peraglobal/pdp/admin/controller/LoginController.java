package com.peraglobal.pdp.admin.controller;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.peraglobal.pdp.admin.biz.LoginLogBiz;
import com.peraglobal.pdp.admin.biz.UserBiz;
import com.peraglobal.pdp.admin.license.exception.LicenseException;
import com.peraglobal.pdp.admin.license.exception.SessionCountException;
import com.peraglobal.pdp.admin.model.LoginLog;
import com.peraglobal.pdp.admin.model.User;
import com.peraglobal.pdp.admin.shiro.PDPShiroRealm;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.admin.utils.LdapCheckUtils;
import com.peraglobal.pdp.common.utils.Base64Utils;
import com.peraglobal.pdp.common.utils.DateUtils;
import com.peraglobal.pdp.common.utils.MD5Utils;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
import com.peraglobal.pdp.modeler.biz.GlobalConfigBiz;
import com.peraglobal.pdp.modeler.utils.CodeUtils;
import com.peraglobal.pdp.operationlog.biz.OperationLogBiz;
import com.peraglobal.pdp.operationlog.model.OperationLog;

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
public class LoginController extends ExtendedMultiActionController{
	

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	private static final String AD_DISABLED_NO_CLOSED="ad_disabled_no_closed";
	
	@Resource
	private GlobalConfigBiz globalConfigBiz;
	@Autowired
	private PDPShiroRealm pdpShiroRealm;
	@Resource
	private LoginLogBiz loginLogBiz;
	
	@Resource
	private OperationLogBiz operationLogBiz;
	
	@Resource
	private UserBiz userBiz;
	
	/**
	 * 功能:跳转到登录首页
	 * <p>作者 文齐辉 2015-4-28
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView tologin(HttpServletRequest request,HttpServletResponse response){
		String viewName = getSkinUiName();
		ModelAndView view;
		view = new ModelAndView(viewName+"login");
		//如果当前用户已经登录跳转到首页
		if(SecurityUtils.getSubject().isAuthenticated()){
			return loginSuccessHandler(view);
		}
		String code = request.getParameter("code");
		loginErrorMsg(view, code);
		return view;
	}


	
	
	/**
	 * 功能:登录处理
	 * <p>作者 文齐辉 2015-5-8
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	public ModelAndView login(HttpServletRequest request,HttpServletResponse response,User user) {
		ModelAndView view = new ModelAndView("redirect:/login/tologin.html");
		//如果当前用户已经登录跳转到首页
		if(SecurityUtils.getSubject().isAuthenticated()){
			return loginSuccessHandler(view);
		}
		if(StringUtil.isEmpty(user.getUsername())){
			return view;
		}
			
			/**
			 * 登陆前查看用户是否开启域验证
			 */
			Boolean verifymode = false;
			User user2 = userBiz.findByUsername(user.getUsername());
			if(StringUtil.isNotEmpty(user2)){
				if("1".equals(user2.getVerifyMode()))
					verifymode = true;
			}
			//开启域验证
			if ("true".equalsIgnoreCase(CodeUtils.getGlobalConfigValue(AD_DISABLED_NO_CLOSED)) && verifymode) {
				boolean b = validateAdUsername(user.getUsername(), user.getPassword());
				if (b) {
					SecurityUtils.getSubject().login(new UsernamePasswordToken(user.getUsername(), user.getPassword()));
					loginLogBiz.saveLoginLog(AppConfigUtils.getCurrentUser(), "域验证登陆" ,AppConfigUtils.getIpAddr(request));
					return loginSuccessHandler(view);
				}else {
					view.addObject("code", "2");
				}
				return view;
			}else {
				Map<String,String> map  = loginHandler(user);
				String code = map.get("code");
				if("00".equals(code)){ //登录成功跳转
					loginLogBiz.saveLoginLog(AppConfigUtils.getCurrentUser(), "本地登录" ,AppConfigUtils.getIpAddr(request));
					return loginSuccessHandler(view);
				}else{ //登录失败返回登录页面
					view.addObject("code", code);
					return view;
				}
			}
	}
	
	/**
	 * 功能:登录处理
	 * <p>作者文齐辉 2016年2月2日 下午1:44:06
	 * @param user
	 * @return
	 */
	private Map<String,String> loginHandler(User user){
		Map<String,String> result = Maps.newHashMap();
		try{
			SecurityUtils.getSubject().login(new UsernamePasswordToken(user.getUsername(), user.getPassword()));
			result.put("code", "00");
		}catch(AuthenticationException e) {  
			if(e instanceof ExcessiveAttemptsException){
				result.put("code", "1");
			}else if(e instanceof LockedAccountException){
				result.put("code", "3");
			}else if(e instanceof DisabledAccountException){
				result.put("code", "4");
			}else if(e.getCause() instanceof SessionCountException){
				result.put("code", "5");
			}else{
				result.put("code", "0");
			}
			LOG.error("登录失败",e);
		}
		return result;
	}
	

	private void loginErrorMsg(ModelAndView view, String code) {
		if("0".equals(code)){
			view.addObject("message", "用户名或密码错误");
		}else if("1".equals(code)){
			view.addObject("message", "密码错误次数超过系统指定次数，账号已锁定");
		}else if("2".equals(code)){
			view.addObject("message", "域验证未通过");
		}else if("3".equals(code)){
			view.addObject("message", "账号已锁定，请联系管理员");
		}else if("4".equals(code)){
			view.addObject("message", "无效账号，请联系管理员");
		}else if("5".equals(code)){
			view.addObject("message", "当前在线用户数超出上限值，请联系管理员");
		}
	}
	
	/**
	 * 功能:cas登录接口
	 * <p>作者文齐辉 2016年2月2日 下午1:27:15
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView casLogin(HttpServletRequest request,HttpServletResponse response) {
		//登录成功跳转url
		String backURL = getStringValue(request, "backURL");
		ModelAndView view = new ModelAndView("redirect:"+backURL);
		ModelAndView errorView = new ModelAndView(getSkinUiName()+"casLoginError");
		String username = getStringValue(request, "username");
		String password = getStringValue(request, "password");
		password = Base64Utils.decode(password);
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		//如果当前用户已经登录跳转到首页
		if(SecurityUtils.getSubject().isAuthenticated()){
			return loginSuccessHandler(view);
		}
		if(StringUtil.isEmpty(username)){
			errorView.addObject("code", "-1");
			return errorView;
		}
		Map<String,String> map  = loginHandler(user);
		String code = map.get("code");
		if("00".equals(code)){ //登录成功跳转
			loginLogBiz.saveLoginLog(AppConfigUtils.getCurrentUser(), "cas登录" ,AppConfigUtils.getIpAddr(request));
			return view;
		}else{ //登录失败返回登录页面
			errorView.addObject("code", code);
			loginErrorMsg(errorView,code);
			return errorView;
		}
	}
	
	/***
	 *  administrator 跳转页面
	 * 功能:
	 * <p>作者梅廷贺 2015年12月25日 上午9:29:19
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSuperLogin(HttpServletRequest request,HttpServletResponse response){
		String viewName = getSkinUiName();
		ModelAndView view;
		view = new ModelAndView(viewName+"administratorLogin/administratorLogin");
		//如果当前用户已经登录跳转到首页
		if(SecurityUtils.getSubject().isAuthenticated()){
			return loginSuccessHandler(view);
		}
		String code = request.getParameter("code");
		if("0".equals(code)){
			view.addObject("message", "用户名或密码错误");
		}else if("1".equals(code)){
			view.addObject("message", "密码错误次数超过系统指定次数，账号已锁定");
		}else if("2".equals(code)){
			view.addObject("message", "域验证未通过");
		}else if("3".equals(code)){
			view.addObject("message", "账号已锁定，请联系管理员");
		}else if("4".equals(code)){
			view.addObject("message", "无效账号，请联系管理员");
		}
		return view;
	}
	
	/**
	 *  administrator 登陆页面
	 *  
	 * 功能:
	 * <p>作者梅廷贺 2015年12月25日 上午9:27:08
	 * @param request
	 * @param response
	 * @param user
	 * @return
	 */
	public ModelAndView superLogin(HttpServletRequest request,HttpServletResponse response,User user) {
		ModelAndView view = new ModelAndView("redirect:/login/toSuperLogin.html");
		String pswd = getStringValue(request, "password");
		String pswd2 = getStringValue(request, "password1");
		//如果当前用户已经登录跳转到首页
		if(SecurityUtils.getSubject().isAuthenticated()){
			return loginSuccessHandler(view);
		}
		if(StringUtil.isEmpty(user.getUsername())){
			return view;
		}
		try{
			/**
			 * 密码组合匹配>赋值
			 */
			if (StringUtil.isNotEmpty(pswd)) {
				pswd = MD5Utils.encrypt(pswd);

				if (StringUtil.isNotEmpty(pswd2)) {
					pswd2 = MD5Utils.encrypt(pswd2);
					pswd += pswd2;
				}
			}
			//使用权限工具进行用户登录，登录成功后跳到shiro配置的successUrl中，与下面的return没什么关系！
			SecurityUtils.getSubject().login(new UsernamePasswordToken(user.getUsername(), pswd));
			loginLogBiz.saveLoginLog(AppConfigUtils.getCurrentUser(), "本地登陆" ,AppConfigUtils.getIpAddr(request));
			return loginSuccessHandler(view);
		}catch(AuthenticationException e) {  
			if(e instanceof ExcessiveAttemptsException){
				view.addObject("code", "1");
			}else if(e instanceof LockedAccountException){
				view.addObject("code", "3");
			}else if(e instanceof DisabledAccountException){
				view.addObject("code", "4");
			}else{
				view.addObject("code", "0");
			}
			LOG.error("登录失败",e);
			return view;
		}
	}
	

	/**
	 * 域验证校验
	 * @param userName
	 * @param passWord
	 * @author MEITINGHE
	 * @return
	 */
	public boolean validateAdUsername(String userName, String passWord) {
		LdapCheckUtils ldap = new LdapCheckUtils();
		String host = CodeUtils.getGlobalConfigValue("ad_server_name"); // 服务器
		String port = CodeUtils.getGlobalConfigValue("ad_server_port"); // 端口
		String domain = CodeUtils.getGlobalConfigValue("ad_server_domain"); // 域名
		boolean b = ldap.getCtx(host,port,domain,userName, passWord);
		return b;
		
	}
	
	
	/**
	 * 注销已登录用户
	 * <p>
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView logout(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView();
		try {
			Object principal = SecurityUtils.getSubject().getPrincipal();
			pdpShiroRealm.clearCachedAuthorizationInfo(principal);
			view.setViewName("login");
			return view;
		}catch(Exception e) {
			LOG.error("注销登录用户失败",e);
			view.setViewName("login");
			return view;
		}
	}
	
	/**
	 * 功能转到登陆日志页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toLoginLogList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("user");
		view.setViewName(getSkinPageName()+"login/findLoginLogList");
		
		return view;
	}
	
	/**
	 * 获得登陆日志列表
	 * @return
	 */
	public ModelAndView findLoginLogList(HttpServletRequest request,HttpServletResponse response){
		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);
		//查询获得用户集合
		List<LoginLog> list = loginLogBiz.findLoginLog(parameters.getPagination(),parameters.getConditions().getItems());
		return this.putToModelAndViewJson(list, parameters,DateUtils.PATTERN_YYYY_MM_DD_HH_MM_SS);
	}
	
	/**
	 * 功能:登录成功后统一跳转，如果配置了url则跳转到指定url
	 * <p>作者文齐辉 2015年12月22日 下午2:25:26
	 * @param view
	 * @return
	 */
	private ModelAndView loginSuccessHandler(ModelAndView view){
		if(view ==null) view = new ModelAndView();
		//登录成功重定向url
		String url = AppConfigUtils.get("login.success.url");
		if(StringUtil.isEmpty(url)){
			view.setViewName("redirect:/main/main.html");
			return view;
		}
		view.setViewName("redirect:"+url);
		return view;
	}
	/**
	 * 导出登录日志
	 * @param request
	 * @param response
	 */
	public void exportLoginLogList(HttpServletRequest request,HttpServletResponse response){

		logger.info("访问日志导出------开始！！！");
		try {
                List<LoginLog> list = new ArrayList<LoginLog>();
                Condition coStstem  = Condition.parseCondition("createByName_nq");
                coStstem.setValue("administrator");
               
                list = loginLogBiz.find(coStstem);
				// 创建一个新的Excel
				HSSFWorkbook workBook = new HSSFWorkbook();
				// 创建sheet页
				HSSFSheet sheet = workBook.createSheet();
				// sheet页名称
				workBook.setSheetName(0, "登录日志");
				// 创建header页
				HSSFHeader header = sheet.getHeader();
				// 设置标题居中
				header.setCenter("标题");
				// 设置第一行为Header
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell0 = row.createCell(Short.valueOf("0"));
				HSSFCell cell1 = row.createCell(Short.valueOf("1"));
				HSSFCell cell2 = row.createCell(Short.valueOf("2"));
				cell0.setCellValue("用户姓名");
				cell1.setCellValue("用户账号");
				cell2.setCellValue("登录时间");
				HSSFCellStyle cellStyle = workBook.createCellStyle();
				// 指定日期显示格式
				HSSFDataFormat format = workBook.createDataFormat();
				cellStyle.setDataFormat(format.getFormat("m/d/yy h:mm"));
				// 循环将数据展示
				if (list != null && list.size()>0) {
					for (int i = 0; i < list.size(); i++) {
						LoginLog ll = list.get(i);
						row = sheet.createRow(i + 1);
						cell0 = row.createCell(Short.valueOf("0"));
						cell1 = row.createCell(Short.valueOf("1"));
						cell2 = row.createCell(Short.valueOf("2"));
						cell0.setCellValue(ll.getCreateByrealName());
						cell1.setCellValue(ll.getCreateByName());
						cell2.setCellValue(ll.getCreateDate()==null ? "" :
							  DateUtils.format(ll.getCreateDate(), DateUtils.PATTERN_YYYY_MM_DD_HH_MM));
						// 设置日期格式
						//cell3.setCellStyle(cellStyle);
						sheet.setColumnWidth((short) 0, (short) 4000);
						sheet.setColumnWidth((short) 1, (short) 4000);
						sheet.setColumnWidth((short) 2, (short) 5000);
					}
				}
				// 通过Response把数据以Excel格式保存
				response.reset();
				response.setContentType("application/msexcel;charset=UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename=\"" + new String(("用户登录日志表" + ".xls").getBytes("GBK"), "ISO8859_1") + "\"");
				OutputStream out = response.getOutputStream();
				workBook.write(out);
				out.flush();
				out.close();

		         OperationLog log = new OperationLog();
	             log.setModelName("登录日志");
	             log.setModelId("sys");
	             log.setOperationType("导出");
	             log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+
	            		"("+AdminConfigUtils.getCurrentUsername()+")在"+ DateUtils.format(new Date(), DateUtils.PATTERN_YYYY_MM_DD_HH_MM)+"导出了登录日志");
		         operationLogBiz.saveCustomLog(log);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		logger.info("访问日志导出------结束！！！");
	}
	
	public ModelAndView deleteLoginLog(HttpServletRequest request,HttpServletResponse response){
		Map<String,String> map = new HashMap<String,String>();
		try{
			User user = AdminConfigUtils.getCurrentUser();
			loginLogBiz.deleteLoginLog(user);
			map.put("success", "success");
		}catch(Exception e ){
			e.printStackTrace();
			map.put("success", "false");
		}
		return JsonModelAndView.newSingle(map);
		
	}

}
