package com.peraglobal.pdp.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.Status;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.modeler.biz.DictionaryBiz;
import com.peraglobal.pdp.modeler.enums.DicCategoryEnum;
import com.peraglobal.pdp.modeler.model.Dictionary;
import com.peraglobal.pdp.operationlog.biz.OperationLogBiz;
import com.peraglobal.pdp.operationlog.model.OperationLog;

/**
 *  <code>DictionarypdpController.java</code>
 *  <p>功能:字典Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 文齐辉 qihui.wen@peraglobal.com 时间 2015-4-30	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class DictionaryController extends ExtendedMultiActionController{

	
	@Resource
	private DictionaryBiz dictionaryBiz ;
	@Resource
	private OperationLogBiz operationLogBiz ;
	
	/**
	 * 功能:保存页面跳转
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSaveDictionaryPage(HttpServletRequest request,HttpServletResponse response){
		String id = getStringValue(request,"id");
		String parentId = getStringValue(request,"parentId");
		String dicCategory = getStringValue(request,"dicCategory");
		String dictionaryType = getStringValue(request,"dictionaryType");
		String pointDic = getStringValue(request,"pointDic");	//页面的定位文字
		ModelAndView view = new ModelAndView(getSkinPageName()+"dictionary/saveDictionary");
		view.addObject("parentId", parentId);
		view.addObject("dicCategory", dicCategory);
		if(StringUtil.isNotEmpty(dictionaryType)){
			try {
				dictionaryType = URLDecoder.decode(dictionaryType,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			view.addObject("dictionaryType", dictionaryType);
		}
		if(StringUtil.isNotEmpty(pointDic)){
			try {
				pointDic = URLDecoder.decode(pointDic,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			view.addObject("pointDic", pointDic);
		}
		if(!StringUtil.isEmpty(id)){
			Dictionary dictionary = dictionaryBiz.findById(id);
			view.addObject("obj",dictionary);
			view.addObject("parentId", dictionary.getParentId());
			view.addObject("dicCategory", dictionary.getDicCategory());
			view.addObject("dictionaryType", dictionary.getDictionaryType());
		}
		return view;
	}
	
	/**
	 * 左侧树的跳转添加修改页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddDicTreePage(HttpServletRequest request,HttpServletResponse response){
		String id = getStringValue(request,"id");
		String parentId = getStringValue(request,"parentId");
		String dicCategory = getStringValue(request,"dicCategory");
		String dictionaryType = getStringValue(request,"dictionaryType");
		ModelAndView view = new ModelAndView(getSkinPageName()+"dictionary/saveDictionaryTree");
		view.addObject("parentId", parentId);
		view.addObject("dicCategory", dicCategory);
		view.addObject("id","");
		if(StringUtil.isNotEmpty(dictionaryType)){
			try {
				dictionaryType = URLDecoder.decode(dictionaryType,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			view.addObject("dictionaryType", dictionaryType);
		}
		if(!StringUtil.isEmpty(id)){
			Dictionary dictionary = dictionaryBiz.findById(id);
			view.addObject("obj",dictionary);
			view.addObject("id",dictionary.getId());
			view.addObject("parentId", dictionary.getParentId());
			view.addObject("dicCategory", dictionary.getDicCategory());
			view.addObject("dictionaryType", dictionary.getDictionaryType());
		}
		return view;
	}
	
	/**
	 * 功能:保存或修改
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveDictionary(HttpServletRequest request,HttpServletResponse response,Dictionary dictionary){
		Map<String, Object> result = new HashMap<String, Object>();
		String parentId = getStringValue(request,"parentId");
		String dicCategory = getStringValue(request,"dicCategory");
		String dictionaryType = getStringValue(request,"dictionaryType");
		String operateStr = null;
		String id = dictionary.getId();
		String addLoginfo = "";
		String editLoginfo = "";
		if (id == null || (id != null &&"newTreeNode".equals(id))) {
			dictionary.setId(null);
			operateStr = "添加";
			if("Category".equals(dicCategory)){
				dictionary.setDicCategory(DicCategoryEnum.Category);
			}else if("Dic".equals(dicCategory)){
				dictionary.setDicCategory(DicCategoryEnum.Dic);
			}
			if(StringUtil.isNotEmpty(dictionaryType)){
				dictionary.setDictionaryType(dictionaryType);
			}
			if(dictionary.getSortNum()==null){
				dictionary.setSortNum(dictionaryBiz.findMaxSortNum()+1);
			}
			dictionary.setStatus(Status.Normal);	//默认启用
			dictionary.setParentId(parentId);
			addLoginfo = "新增了字典:["+dictionary.getDictionaryName()+"]";
		} else {
			operateStr = "修改";
			editLoginfo = "修改了字典:["+dictionary.getDictionaryName()+"]";
		}
		
		try {
			dictionaryBiz.save(dictionary);
			result.put("code", "ok");
			operateStr += "成功！";
			//操作日志
            OperationLog log = new OperationLog();
            log.setModelName("字典管理");
            if(addLoginfo.length()>0){
            	log.setOperationType("新建");
            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")"+addLoginfo);
            }else if(editLoginfo.length()>0){
            	log.setOperationType("修改");
            	log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")"+editLoginfo);
            }
            operationLogBiz.saveCustomLog(log);
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveDictionary save exception", e);
		}
		result.put("id", dictionary.getId());
		result.put("msg", operateStr);
		return this.putToModelAndViewJson(result);
	}
	
	/**
	 * 功能:查找列表
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	//@PageCacheMode(constructors={Calendar.DAY_OF_MONTH,1})
	public ModelAndView findDictionaryList(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView =  new ModelAndView(getSkinPageName()+"dictionary/findDictionaryList");
		return modelAndView;
	}

	/**
	 * 功能:查找列表Json,开启页面缓存功能，时间为1分钟
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */ 
	public ModelAndView findDictionaryJosnList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List<Dictionary> list = dictionaryBiz.find(parameters.getPagination(), parameters.getConditions());
		return this.putToModelAndViewJson(list, parameters);
	}
	
	
	/**
	 * 功能:查找列表
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getDictionaryList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		ModelAndView modelAndView = new ModelAndView(getSkinPageName()+"dictionary/findDictionaryList");
		return modelAndView;
	}
	
	
	/**
	 * 功能:验证用户名是否重复
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 */
	public ModelAndView validateNameByAjax(HttpServletRequest request, HttpServletResponse response){
		String dictionaryName = getStringValue(request,"dictionaryName");
		String oldDicName = getStringValue(request,"oldDicName");
		//如果没有修改名称则直接返回
		if(dictionaryName.equals(oldDicName)){
			return JsonModelAndView.newSingle(true);
		}
		String parentId = getStringValue(request,"dictionaryParentId");
		Condition condition = Condition.parseCondition("dictionaryName_eq");
		condition.setValue(dictionaryName);
		Condition conditionParentId = Condition.parseCondition("parentId_eq");
		conditionParentId.setValue(parentId);
		List<Condition> conList = new ArrayList<Condition>();
		conList.add(condition);
		conList.add(conditionParentId);
		
		List<Dictionary> dictionaryList = dictionaryBiz.find(null, conList);
		if(!CollectionUtil.isEmpty(dictionaryList)){
			return JsonModelAndView.newSingle("字典名称重复");
		}
		return JsonModelAndView.newSingle(true);
	}
	
	/**
	 * 
	 * 功能:	功能:验证字典值是否重复
	 * <p>作者梅廷贺 2016年1月18日 下午5:16:30
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView validateCodeByAjax(HttpServletRequest request, HttpServletResponse response){
		String value = getStringValue(request,"value");
		String oldDicName = getStringValue(request,"oldDicName");
		//如果没有修改名称则直接返回
		if(value.equals(oldDicName)){
			return JsonModelAndView.newSingle(true);
		}
		String parentId = getStringValue(request,"dictionaryParentId");
		Condition condition = Condition.parseCondition("value_eq");
		condition.setValue(value);
		Condition conditionParentId = Condition.parseCondition("parentId_eq");
		conditionParentId.setValue(parentId);
		List<Condition> conList = new ArrayList<Condition>();
		conList.add(condition);
		conList.add(conditionParentId);
		
		List<Dictionary> dictionaryList = dictionaryBiz.find(null, conList);
		if(!CollectionUtil.isEmpty(dictionaryList)){
			return JsonModelAndView.newSingle("字典值重复");
		}
		return JsonModelAndView.newSingle(true);
	}
	
	
	/**
	 * 功能:字典树
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 */
	public ModelAndView getDictionaryTree(HttpServletRequest request, HttpServletResponse response){
		List<TreeNode> childList = dictionaryBiz.getTreeList();
		TreeNode tn = new TreeNode();
		tn.setId("0");
		tn.setText("字典");
		tn.setIconCls("treeproj-icon icon-suo");
		tn.setChildren(childList);
		List<TreeNode> root = new ArrayList<TreeNode>();
		root.add(tn);
		return JsonModelAndView.newSingle(root);
	}
	
	
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 */
	public ModelAndView delDictionary(HttpServletRequest request,HttpServletResponse response) {
		String ido = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		String loginfo = "";
		try {
			if(StringUtils.isNotEmpty(ido)) {
				List<String> ids = CollectionUtil.toStringCollection(ido);
				List<Dictionary> diclist = dictionaryBiz.findByIds(ids);
				if(diclist!=null && diclist.size()>0){
					for(Dictionary dic : diclist){
						loginfo +=","+dic.getDictionaryName();
					}
				}
				dictionaryBiz.deleteByIds(ids);
				result.put("success", "success");
				//操作日志
				if(loginfo.length()>0){
					loginfo = loginfo.substring(1);
				}
	            OperationLog log = new OperationLog();
	            log.setModelName("字典管理");
	            log.setOperationType("删除");
	            log.setLogText(AdminConfigUtils.getCurrentUser().getRealName()+"("+AdminConfigUtils.getCurrentUsername()+")删除字典:["+loginfo+"]");
	            operationLogBiz.saveCustomLog(log);
			}else {
				result.put("success", "false");
				result.put("msg", "请选择要删除的数据");
			}
		}catch(Exception e) {
			result.put("success", "false");
			result.put("msg", "删除失败");
			logger.error("删除字典项失败",e);
		}
		return this.putToModelAndViewJson(result);
	}
	
	
	/**
	 * 根据字典父ID,得到对应的数据字典
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getDictionaryByParentId(HttpServletRequest request,HttpServletResponse response) {
		String parentId = getStringValue(request, "parentId");
		
		if(parentId != null) {
			Condition condition = Condition.parseCondition("parentId_eq");
			condition.setValue(parentId);
			List<Dictionary> list = dictionaryBiz.find(condition);
			return JsonModelAndView.newSingle(list);
		}
		return null;
	}
	
	/**
	 * 根据字典类型type，值value，获得字典名称dictionaryName
	 * @param request
	 * @param response
	 * @param dictionary
	 * @return
	 */
	public ModelAndView findDictionaryNameByTypeValue(HttpServletRequest request,HttpServletResponse response,Dictionary dictionary){
		Map<String, Object> map = new HashMap<String, Object>();
		String type = getStringValue(request,"type");
		String value = getStringValue(request,"value");
		String name = dictionaryBiz.findDictionaryNameByTypeValue(type, value);
		map.put("name", name);
		return JsonModelAndView.newSingle(map);
	}
	/**
	 * 根据字典类型type
	 * @param request
	 * @param response
	 * @param dictionary
	 * @return
	 */
	public ModelAndView findDictionaryByType(HttpServletRequest request,HttpServletResponse response,Dictionary dictionary){
		String type = getStringValue(request,"type");
		List<Dictionary> dcList = new ArrayList<Dictionary>();
		Dictionary nothing = new Dictionary();
		nothing.setDictionaryName("------请选择------");
		nothing.setValue("");
		dcList.add(nothing);
		List<Dictionary> dctList = dictionaryBiz.findDictionariesByType(type);
		dcList.addAll(dctList);
		return JsonModelAndView.newSingle(dcList);
	}
}
