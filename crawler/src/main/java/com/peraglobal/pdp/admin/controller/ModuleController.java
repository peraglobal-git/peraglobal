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
import com.peraglobal.pdp.admin.model.Module;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.Status;
import com.peraglobal.pdp.core.enums.YesNoEnum;
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
public class ModuleController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(ModuleController.class);
	
	@Resource
	private ModuleBiz moduleBiz;
	@Resource
	private OperationLogBiz operationLogBiz;
	
	
	

	/**
	 * 转到导航页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toModulePage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"module/modulePage");
		return view;
	}
	

	/**
	 * 功能获取导航列表
	 * <p>作者 郭宏波 2015-5-18
	 * @param request
	 * @param response
	 */
	public ModelAndView getModuleList(HttpServletRequest request,HttpServletResponse response) {

		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);
		
		List<Module> moduleList = moduleBiz.find(parameters.getPagination(),parameters.getConditions());
		//将分页结果返回到页面
		return this.putToModelAndViewJson(moduleList, parameters);
	}
	
	
	
	
	/**
	 * 功能获取导航树
	 * <p>作者 郭宏波 2015-5-18
	 * @param request
	 * @param response
	 */
	public ModelAndView getModuleTreeList(HttpServletRequest request,HttpServletResponse response) {
		
		String isPermission = getStringValue(request,"isPermission");
		List<TreeNode> moduleTree = null;
		if(StringUtil.isEmpty(isPermission)){
			//获取树的节点
			 moduleTree = moduleBiz.getModuleTree();
		}else{
			//获取树的节点 @link IsPermissionEnum.Yes
			moduleTree = moduleBiz.getModuleTreeByIsPermission(YesNoEnum.Yes);
		}
		//树的扩展属性
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("levelNum", "0");

		//创建根节点
		TreeNode tn = new TreeNode();
		
		//节点ID
		tn.setId("0");
		//节点名称
		tn.setText("系统");
		//添加子节点
		tn.setChildren(moduleTree);
		//添加扩展属性
		tn.setAttributes(map);
		//添加图标
		tn.setIconCls("treeproj-icon icon-suo");
		//treenode集合
		List<TreeNode> result = new ArrayList<TreeNode>();
		
		result.add(tn);
		//以JSON格式返回用户集合
		return JsonModelAndView.newSingle(result);
	}
	
	
	
	
	/**
	 * 转到新增或者修改页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddModulePage(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("module");
		String sId = request.getParameter("id");
		String sPid = request.getParameter("parentId");
		String levelNum = request.getParameter("levelNum");
		if(!StringUtil.isEmpty(sId)) {
			Module module = moduleBiz.findById(sId);
			view.addObject("module",module);
		}else {
			Module module = new Module();
			module.setParentId(sPid);
			module.setLevelNum(Integer.parseInt(levelNum));
			view.addObject("module", module);
		}
		view.setViewName(getSkinPageName()+"module/addAndModifyPage");
		return view;
	}
	
	/**
	 * 添加或者修改导航
	 * @param request
	 * @param response
	 * @param module
	 */
	public ModelAndView addOrUpdateModule(HttpServletRequest request,HttpServletResponse response,Module module) {
		Map<String,String> map = new HashMap<String,String>();
		String addLoginfo = "";
		String editLoginfo = "";
		try {
			String parentId = getStringValue(request,"parentId");
			String filePath = getStringValue(request,"filePath");
			String name = module.getModuleName();
			Boolean checkName = moduleBiz.checkName(module.getId(), parentId, name);
			if(checkName) {
				if(StringUtil.isEmpty(module.getId())){
					if(null == module.getIsCanDelete())
						module.setIsCanDelete(YesNoEnum.Yes);
					if("0".equals(parentId)){
						module.setLevelNum(1);					
					}else{
						Module parentModule = moduleBiz.findById(parentId);
						module.setLevelNum(parentModule.getLevelNum()+1);
						//SortNum 排序
						module.setSortNum(moduleBiz.getMax(null, parentId) == null ? 0 : moduleBiz.getMax(null, parentId) +1);
					}
					editLoginfo = "新建了菜单:["+name+"]";
				}else{
					editLoginfo = "修改了菜单:["+name+"]";
				}
				if(module.getStatus()==null){
					module.setStatus(Status.Normal); //默认为启用
				}
				moduleBiz.saveByAttachme(module,filePath);
				map.put("id",module.getId());
			}else {
				map.put("success", "repeate");
			}
			//操作日志
            OperationLog log = new OperationLog();
            log.setModelName("功能菜单管理");
            if(addLoginfo.length()>0){
            	log.setOperationType("新建");
            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")"+addLoginfo);
            }else if(editLoginfo.length()>0){
            	log.setOperationType("修改");
            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")"+editLoginfo);
            }
            operationLogBiz.saveCustomLog(log);
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			LOG.error("保存导航信息失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 删除选择的记录
	 * @param request
	 * @param response
	 * @param module
	 */
	public ModelAndView delModule(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			List<String> ids = CollectionUtil.toStringCollection(request.getParameter("id"));
			for (String id : ids) {
				Module module = moduleBiz.findById(id);
				if(null != module){
					if(YesNoEnum.Yes==module.getIsCanDelete()){
						moduleBiz.deleteById(id);
					}
				}
			}
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			map.put("msg", "删除失败");
			LOG.error("删除导航信息失败",e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 上移,下移
	 * @param request
	 * @param response
	 * @param module
	 */
	public ModelAndView changeSortNum(HttpServletRequest request,HttpServletResponse response,Module module) {
		Map<String,String> map = new HashMap<String,String>();
		String op = request.getParameter("op");
		String sourceId = request.getParameter("sourceId");
		String targetId = request.getParameter("targetId");
		String loginfo = "";
		if(StringUtils.isNotEmpty(sourceId) && StringUtils.isNotEmpty(targetId)) {
			List<Module> list = new ArrayList<Module>();
			Module sourceModule = moduleBiz.findById(sourceId);
			Module targetModule = moduleBiz.findById(targetId);
			if(sourceModule != null && targetModule != null) {
				loginfo = sourceModule.getModuleName();
				Integer sourceSortNum = sourceModule.getSortNum();
				sourceModule.setSortNum(targetModule.getSortNum());
				targetModule.setSortNum(sourceSortNum);
				list.add(targetModule);
				list.add(sourceModule);
				
				moduleBiz.updateBatch(list);
			}
			map.put("success", "success");
			//操作日志
            OperationLog log = new OperationLog();
            log.setModelName("功能菜单管理");
            if("up".equals(op)){
            	log.setOperationType("上移");
            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")上移了菜单["+loginfo+"]");
            }else if("down".equals(op)){
            	log.setOperationType("下移");
            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")下移了菜单["+loginfo+"]");
            }
            operationLogBiz.saveCustomLog(log);
		}else {
			map.put("success", "error");
		}
		return JsonModelAndView.newSingle(map);
	}
	
	/**
	 * 修改状态-启用停用
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateStatusModule(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("success", "error");
		String id = request.getParameter("id");
		String enableOrDisable = request.getParameter("eod");
		if(StringUtils.isNotEmpty(id)) {
			Boolean flag = moduleBiz.enableOrDisableMoudle(id,enableOrDisable);
			if(flag) {
				map.put("success", "success");
				//操作日志
				Module module = moduleBiz.findById(id);
				String loginfo = "";
				if(module!=null){
					loginfo = module.getModuleName();
				}
	            OperationLog log = new OperationLog();
	            log.setModelName("功能菜单管理");
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
	
}
