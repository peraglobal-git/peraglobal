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

import com.peraglobal.pdp.admin.biz.GlobalValueBiz;
import com.peraglobal.pdp.admin.biz.ModuleBiz;
import com.peraglobal.pdp.admin.model.Module;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.admin.vo.ModuleGlobalConfigVo;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.common.utils.UtilMisc;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.modeler.biz.GlobalConfigBiz;
import com.peraglobal.pdp.modeler.enums.GlobalConfigEnum;
import com.peraglobal.pdp.modeler.model.GlobalConfig;
import com.peraglobal.pdp.operationlog.biz.OperationLogBiz;
import com.peraglobal.pdp.operationlog.model.OperationLog;

/**
 *  <code>GlobalConfigController.java</code>
 *  <p>功能:全局配置Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波 hongbo.guo@peraglobal.com 时间 2015-6-10	
 *  @version 1.0 
 *  </br>最后修改人 wenqihui
 */
@Controller
public class GlobalConfigController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalConfigController.class);
	
	//全局配置业务类
	@Resource
	private GlobalConfigBiz globalConfigBiz;
	
	//全局配置值业务类
	@Resource
	private GlobalValueBiz globalValueBiz;

	@Resource
	private ModuleBiz moduleBiz;
	@Resource
	private OperationLogBiz operationLogBiz;
	
	/**
	 * 转到全局属性页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toGlobalPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"globalConfig/globalPage");
		//根据分页参数,查询条件查询
		GlobalConfig mail = new GlobalConfig() ;
		GlobalConfig service = new GlobalConfig() ;
		List<GlobalConfig> list = globalConfigBiz.findAll() ; 
		for(GlobalConfig mc : list){
			if(mc.getCode().equals("email_disabled_no_closed")){
				mail = mc ;
				List<GlobalConfig> gcs = globalConfigBiz.findConfigByParentCode(mc.getCode()) ;
				view.addObject("mailList",gcs) ;
			}else if(mc.getCode().equals("ad_disabled_no_closed")){
				service = mc ;
				List<GlobalConfig> gcs = globalConfigBiz.findConfigByParentCode(mc.getCode()) ;
				view.addObject("serviceList",gcs) ;
			}else if(mc.getCode().equals("fail_login_count")){
				view.addObject("count",mc) ;
			}else if(mc.getCode().equals("default_index_page")){
				view.addObject("default",mc) ;
			}else if(mc.getCode().equals("system_name")){
				view.addObject("system",mc) ;
			}else if(mc.getCode().equals("dic_value_is_show")){
				view.addObject("dic",mc) ;
			}else if(mc.getCode().equals("reset_pass_disabled_no_closed")){
				view.addObject("redis",mc) ;
			}else if(mc.getCode().equals("reset_pass_set_type")){
				view.addObject("reset",mc) ;
			}
		}
		List<Module> moduleList = moduleBiz.findModuleListByLevel("0");
		view.addObject("moduleList", moduleList) ;
		view.addObject("mail", mail) ;
		view.addObject("service",service) ;
		view.addObject("moduleCode", getStringValue(request,"moduleCode"));
		return view;
	}
	public ModelAndView toGlobalConfigList(HttpServletRequest request, HttpServletResponse response)
    {
        return new ModelAndView(getSkinPageName()+"globalConfig/findGlobalConfigList");
    }
	
	/**
	 * 功能:查询配置项根据模块code	
	 * <p>作者 wenqihui 2015-8-7
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView findConfigByModuleCode(HttpServletRequest request,HttpServletResponse response) {
		String moduleCode = getStringValue(request,"moduleCode");
		ListPageQuery parameters = bindToListPageQuery(request);
		Condition parentCon = Condition.parseCondition("parentCode_eq");
		parentCon.setValue("0");
		Condition moduleCon = Condition.parseCondition("moduleCode_eq");
		moduleCon.setValue(moduleCode);
		parameters.getConditions().put("parentCode_eq", parentCon);
		parameters.getConditions().put("moduleCode_eq", moduleCon);
		List<GlobalConfig> list = globalConfigBiz.find(null, parameters.getConditions());
		return JsonModelAndView.newSingle(list);
	}
	

	/**
	 * 转到添加全局属性配置页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toAddPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		Map<String, String> map = globalConfigBiz.findGConfig() ;
		view.setViewName(getSkinPageName()+"globalConfig/saveGlobalConfig");
		view.addObject("moduleCode", getStringValue(request, "moduleCode"));
		return view;
	}
	
	
	/**
	 * 保存全局配置
	 * @param request
	 * @param response
	 * @param globalConfig
	 */
	public ModelAndView saveGlobalConfig(HttpServletRequest request,HttpServletResponse response,GlobalConfig globalConfig) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			String code = globalConfig.getCode();
			Boolean checkCode = globalConfigBiz.checkCode(code);
			String[] subItemNames = request.getParameterValues("subItemName");
			String[] subItemCodes = request.getParameterValues("subItemCode");
			String[] subItemTypes = request.getParameterValues("subItemType");
			String[] subItemValues = request.getParameterValues("subItemValue");
			if(checkCode) {
				globalConfig.setParentCode("0");
				globalConfig.setConfigType(GlobalConfigEnum.Customer);
				Map<String,String[]> items = UtilMisc.toMap("subItemNames", subItemNames, 
						"subItemCodes",subItemCodes,"subItemTypes",subItemTypes,"subItemValues",subItemValues);
				globalConfigBiz.saveAndSubItem(globalConfig,items);
				map.put("success", "success");
			}else {
				map.put("success", "repeate");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			LOG.error(e.getMessage(),e);
			map.put("success", "false");
			map.put("err", "保存失败");
			return JsonModelAndView.newSingle(map);
		}
	}
	public ModelAndView saveGlobal(HttpServletRequest request,HttpServletResponse response,GlobalConfig globalConfig) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			String code = globalConfig.getCode();
			Boolean checkCode = globalConfigBiz.checkCode(code);
			if(checkCode) {
				globalConfig.setParentCode("0");
				globalConfigBiz.save(globalConfig); 
				map.put("success", "success");
			}else {
				map.put("success", "repeate");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			LOG.error(e.getMessage(),e);
			map.put("success", "false");
			map.put("err", "保存失败");
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	
	/**
	 * 功能:保存邮件配置
	 * <p>作者 wenqihui 2015-8-7
	 * @param request
	 * @param response
	 * @param globalConfig
	 * @return
	 */
	public ModelAndView saveMailConfig(HttpServletRequest request,HttpServletResponse response,ModuleGlobalConfigVo config) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			String mailValue = request.getParameter("mailValue") ;
			GlobalConfig gc = globalConfigBiz.findByCode("email_disabled_no_closed") ;
			List<GlobalConfig> gcList = new ArrayList<GlobalConfig>() ;
			if(gc == null){
				gc = new GlobalConfig() ;
				gc.setName("开启邮件发送功能");
				gc.setCode("email_disabled_no_closed");
				globalConfigBiz.save(gc); 
			}
			if(mailValue.equals("true")){
				gc.setValue("true");
				for(GlobalConfig gConfig : config.getConfigList()){
					GlobalConfig gcg = globalConfigBiz.findByCode(gConfig.getCode()) ;
					gcg.setValue(gConfig.getValue());
					gcList.add(gcg) ;
				}
			}else{
				gc.setValue("false");
			}
			gcList.add(gc) ;
			globalConfigBiz.saveBatch(gcList);
			map.put("success", "success");
		}catch(Exception e){
			LOG.error("批量保存全局配置信息异常",e);
			map.put("error", "保存全局配置信息异常");
		}
		return putToModelAndViewJson(map);
	}
	/**
	 * 功能:保存域服务器配置
	 * <p>作者 wenqihui 2015-8-7
	 * @param request
	 * @param response
	 * @param globalConfig
	 * @return	
	 */
	public ModelAndView saveServiceConfig(HttpServletRequest request,HttpServletResponse response,ModuleGlobalConfigVo config) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			String serviceValue = request.getParameter("serviceValue") ;
			GlobalConfig gc = globalConfigBiz.findByCode("ad_disabled_no_closed") ;
			List<GlobalConfig> gcList = new ArrayList<GlobalConfig>() ;
			if(gc == null){
				gc = new GlobalConfig() ;
				gc.setName("开启域验证");
				gc.setCode("ad_disabled_no_closed");
				globalConfigBiz.save(gc); 
			}
			if(serviceValue.equals("true")){
				gc.setValue("true");
				for(GlobalConfig gConfig : config.getConfigList()){
					GlobalConfig gcg = globalConfigBiz.findByCode(gConfig.getCode()) ;
					gcg.setValue(gConfig.getValue());
					gcList.add(gcg) ;
				}
			}else{
				gc.setValue("false");
			}
			gcList.add(gc) ;
			globalConfigBiz.saveBatch(gcList);
			map.put("success", "success");
		}catch(Exception e){
			LOG.error("批量保存全局配置信息异常",e);
			map.put("error", "保存全局配置信息异常");
		}
		return putToModelAndViewJson(map);
	}
	/**
	 * 功能:批量保存值
	 * <p>作者 wenqihui 2015-8-7
	 * @param request
	 * @param response
	 * @param globalConfig
	 * @return
	 */
	public ModelAndView saveGlobalConfigBatch(HttpServletRequest request,HttpServletResponse response,GlobalConfig globalConfig) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			String isSubItem = getStringValue(request,"isSubItem");
			if(!CollectionUtil.isEmpty(globalConfig.getList())){
				if("Yes".equals(isSubItem)){
					globalConfigBiz.updateBatchByCode(globalConfig.getList());
				}else{
					globalConfigBiz.saveBatch(globalConfig.getList());
				}
			}
			map.put("code", "success");
		}catch(Exception e){
			LOG.error("批量保存全局配置信息异常",e);
			map.put("error", "保存全局配置信息异常");
		}
		return putToModelAndViewJson(map);
	}
	/**
	 * 功能:批量保存值
	 * <p>作者 jiyi 2015-9-15
	 * @param request
	 * @param response
	 * @param globalConfig
	 * @return
	 */
	public ModelAndView saveBasicGlobalConfigBatch(HttpServletRequest request,HttpServletResponse response,ModuleGlobalConfigVo config) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			List<GlobalConfig> list = config.getConfigList();
			globalConfigBiz.saveOrUpdate(list);
			map.put("code", "success");
			String imgcode ="";
			for(GlobalConfig tmp:list){
				tmp.setConfigType(GlobalConfigEnum.Basics);
				GlobalConfig configbc = globalConfigBiz.findByCode(tmp.getCode());
				if(config!=null){
					if(tmp.getValue() != null && !"".equals(tmp.getValue())){
						imgcode = tmp.getCode();
					}
				}
			}
			if(StringUtil.isNotEmpty(imgcode) && imgcode.equals("login_background_img_big")){//semp-admin-web中的更换登录背景图
				//操作日志
	            OperationLog log = new OperationLog();
	            log.setModelName("个性化设置");
	            log.setOperationType("保存");
	            log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")设置了登录背景图");
	            operationLogBiz.saveCustomLog(log);
			}
		}catch(Exception e){
			LOG.error("批量保存全局配置信息异常",e);
			map.put("error", "保存全局配置信息异常");
		}
		return putToModelAndViewJson(map);
	}
	
	/**
	 * 功能:保存带parentCode子项参数
	 * <p>作者 wenqihui 2015-8-7
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveGlobalConfigItem(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			Map<String,String[]> paramMap = request.getParameterMap();
			String[] parentCode = paramMap.get("parentCode");
			if(parentCode!=null && parentCode.length>0){
				globalConfigBiz.saveBatchByCode(paramMap,parentCode[0]);
			}
			map.put("code", "success");
		}catch(Exception e){
			LOG.error("批量子项配置信息异常",e);
			map.put("code", "fail");
		}
		return putToModelAndViewJson(map);
	}
	
	
	/**
	 * 根据id批量删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delGlobalConfig(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		
		try{
			String ids = request.getParameter("id");
			if(StringUtils.isNotEmpty(ids)) {
				List<String> idlist = CollectionUtil.toStringCollection(ids);
				globalConfigBiz.deleteByIds(idlist);
				map.put("success", "success");
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			map.put("msg", "删除失败");
			LOG.error("删除数据失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	/**
	 * 获取所有的表名
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTableName(HttpServletRequest request,HttpServletResponse response) {
		List<Module> moduleList = moduleBiz.findModuleListByParentId("0");
		return JsonModelAndView.newSingle(moduleList);
	}
	/**
	 * 获取一级菜单
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getMo(HttpServletRequest request,HttpServletResponse response) {
		List<Module> moduleList = moduleBiz.findModuleListByLevel("0");
		return JsonModelAndView.newSingle(moduleList);
	}
	/**
	 * 功能:更具parentCode查询子项
	 * <p>作者 wenqihui 2015-8-6
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findGlobalConfigByParentCode(HttpServletRequest request,HttpServletResponse response) {
		String parentCode = getStringValue(request,"parentCode");
		Condition parentCon = Condition.parseCondition("parentCode_eq");
		parentCon.setValue(parentCode);
		List<GlobalConfig> list = globalConfigBiz.find(parentCon);
		return JsonModelAndView.newSingle(list);
	}
	/**
	 * 功能:配置分页
	 * <p>作者 jiyi 2015-9-15
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getGlobalConfigList(HttpServletRequest request,HttpServletResponse response) {
		 //获取所有的参数
        ListPageQuery parameters = new ListPageQuery();
        //与request与parameters绑定
        new ServletRequestDataBinder(parameters).bind(request);
        //加入类型参数
        Condition con = Condition.parseCondition("configType_eq");
		con.setValue(GlobalConfigEnum.Customer);
        parameters.getConditions().put("configType",con ) ;
        //根据分页参数,查询条件查询
        List<GlobalConfig> list = globalConfigBiz.find(parameters.getPagination(), parameters.getConditions());

        //以json格式返回
        return this.putToModelAndViewJson(list, parameters);
	}
	
	
	
}
