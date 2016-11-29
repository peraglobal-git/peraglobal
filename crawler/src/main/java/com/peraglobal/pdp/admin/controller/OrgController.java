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

import com.peraglobal.pdp.admin.biz.OrgBiz;
import com.peraglobal.pdp.admin.model.Org;
import com.peraglobal.pdp.admin.vo.OrgVo;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.Status;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.BeanUtil;

/**
 *  <code>OrgController.java</code>
 *  <p>功能:组织部门Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波  时间 2015-5-14	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class OrgController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(OrgController.class);
	
	@Resource
	private OrgBiz orgBiz;
	
	/**
	 * 获取组织部门树
	 * <p>
	 * @param request
	 * @param response
	 */
	public ModelAndView getOrgTree(HttpServletRequest request,HttpServletResponse response) {
		String status = getStringValue(request, "status");
		//获取TreeNode
		List<TreeNode> treeList = orgBiz.getTreeList(status);
		//转换成Json格式返回
		return JsonModelAndView.newSingle(treeList);
	}
	
	/**
	 * 转到组织部门页面
	 * @param request
	 * @param response
	 * @param view
	 * @return
	 */
	public ModelAndView toOrgPage(HttpServletRequest request,HttpServletResponse response,ModelAndView view) {
		view.setViewName(getSkinPageName()+"org/orgPage");
		return view;
	}
	
	/**
	 * 转到新增或者修改页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddOrgPage(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView("org");
		String sId = request.getParameter("id");
		String sPid = request.getParameter("pid");
		if(StringUtils.isNotEmpty(sId)) {
			Org org = orgBiz.findById(sId);
			view.addObject("org",org);
			view.addObject("id",sId);
			view.addObject("pid", sPid);
			view.addObject("type", "修改部门");
		}else {
			view.addObject("type", "新增部门");
			view.addObject("pid", sPid);
		}
		view.setViewName(getSkinPageName()+"org/addAndModifyPage");
		return view;
	}
	
	/**
	 * 新增或者修改方法
	 * @param request
	 * @param response
	 */
	public ModelAndView saveOrUpdate(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> result = new HashMap<String,String>();
		
		List<Org> list = new ArrayList<Org>();
		
		String sId = request.getParameter("id");
		String orgName = request.getParameter("name");
		String orgCode = request.getParameter("orgCode");
		String depdes = request.getParameter("depdes");
		String sPid = request.getParameter("pid");
		Org o = null;
		try {
			
			//查询是否重名,重名不予保存,传递参数(id,parentId,name)
			Boolean bool = orgBiz.checkName(sId, sPid, orgName);
			if(bool) {
				//如果id不为空则说明是进行更新操作
				if(StringUtils.isNotEmpty(sId)) {
					o = orgBiz.findById(sId);
					o.setName(orgName);
					o.setOrgCode(orgCode);
					o.setDepdes(depdes);
					list.add(o);
					orgBiz.updateBatch(list);
				}else {
						o = new Org();
						o.setName(orgName);
						o.setOrgCode(orgCode);
						o.setDepdes(depdes);
						o.setParentId(sPid);
						o.setStatus(Status.Normal);//默认启用
						o.setOrderCode(orgBiz.findOrgMaxOrder()+1);
						orgBiz.save(o);
				}
				result.put("success", "success");
			}else {
				result.put("success", "repeate");
			}
			result.put("id", o.getId());
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			if(StringUtils.isNotEmpty(sId)) {
				result.put("err", "更新操作失败");
			}else {
				result.put("err", "新增操作失败");
			}
			LOG.error("保存组织部门失败",e);
			return JsonModelAndView.newSingle(result);
		}
	}

	/**
	 * 启用部门,可以是多个,以","链接
	 * @param ids		多个id
	 * @param request
	 * @param response
	 */
	public ModelAndView enableOrDisableOrg(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("success", "error");
		String ids = request.getParameter("ids");
		String enableOrDisable = request.getParameter("eod");
		if(StringUtils.isNotEmpty(ids)) {
			Boolean flag = orgBiz.enableOrDisableOrg(ids,enableOrDisable);
			if(flag) {
				map.put("success", "success");
			}
		}
		return JsonModelAndView.newSingle(map);
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
		//查询部门list
		List<Org> orgList = orgBiz.find(parameters.getPagination(),parameters.getConditions());
		//将分页结果返回到页面
		return this.putToModelAndViewJson(orgList, parameters);
		//return null;
	}
	
	/**
	 * 获取树形表格-部门列表
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTreeDataList(HttpServletRequest request,HttpServletResponse response) {
		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);
		//查询部门list
		List<Org> orgList = orgBiz.find(parameters.getConditions().getItems());
		if(orgList!=null && orgList.size()>0){
			List<OrgVo> list = new ArrayList<OrgVo>();
			for(Org org : orgList){
				OrgVo vo = new OrgVo();
				BeanUtil.copyBean(org,vo);
				list.add(vo);
			}
			List<OrgVo> resList = getfatherNode(list);
			if(resList.size()>0){
				return this.putToModelAndViewJson(resList, parameters);
			}else{
				List<Org> allOrgs = orgBiz.findAll();
				if(allOrgs!=null && allOrgs.size()>0){
					List<OrgVo> orgVos = new ArrayList<OrgVo>();
					for(Org org : allOrgs){
						OrgVo vo = new OrgVo();
						BeanUtil.copyBean(org,vo);
						orgVos.add(vo);
					}
					List<OrgVo> resList2 =  getTreeNodes(list,orgVos);
					return this.putToModelAndViewJson(resList2, parameters);
				}
			}
		}else{
			return this.putToModelAndViewJson(orgList, parameters);
		}
		//将分页结果返回到页面
		return null;
	}
	
	 	/**
	 	 * 查找父节点
	 	 * @param treeDataList
	 	 * @return
	 	 */
	    public List<OrgVo> getfatherNode(List<OrgVo> treeDataList) {
	        List<OrgVo> newTreeDataList = new ArrayList<OrgVo>();
	        for (OrgVo jsonTreeData : treeDataList) {
	            if("0".equals(jsonTreeData.getParentId())) {
	                //获取父节点下的子节点
	                jsonTreeData.setChildren(getChildrenNode(jsonTreeData.getId(),treeDataList));
	                newTreeDataList.add(jsonTreeData);
	            }
	        }
	        return newTreeDataList;
	    }

	    /**
	     * 查找子节点
	     * @param pid
	     * @param treeDataList
	     * @return
	     */
	    public List<OrgVo> getChildrenNode(String pid , List<OrgVo> treeDataList) {
	        List<OrgVo> newTreeDataList = new ArrayList<OrgVo>();
	        for (OrgVo jsonTreeData : treeDataList) {
	            if(jsonTreeData.getParentId() == null)  continue;
	            //这是一个子节点
	            if(jsonTreeData.getParentId().equals(pid)){
	                //递归获取子节点下的子节点
	                jsonTreeData.setChildren(getChildrenNode(jsonTreeData.getId() , treeDataList));
	                newTreeDataList.add(jsonTreeData);
	            }
	        }
	        return newTreeDataList;
	    }
	
	    /**
	     * 查找某节点的相关节点
	     * @param orgList
	     * @param treeDataList
	     * @return
	     */
	    public List<OrgVo> getTreeNodes(List<OrgVo> orgList,List<OrgVo> treeDataList){
	    	List<OrgVo> newTreeDataList = new ArrayList<OrgVo>();
	    	for(OrgVo org : orgList){
	    		//获取子节点
   			 	org.setChildren(getChildrenNode(org.getId() , treeDataList));
	    		//获取父节点
	    		 OrgVo vo =  getParentNodeUp(org,treeDataList);
	    		 if(vo!=null)
	    		 if(!newTreeDataList.contains(vo)){
	    			 newTreeDataList.add(vo);
	    		 }
	    	}
	    	
	    	return newTreeDataList;
	    }
	    
	    /**
	     * 查找父级节点
	     * @param org
	     * @param treeDataList
	     * @return
	     */
	    public OrgVo getParentNodeUp(OrgVo org,List<OrgVo> treeDataList){
	    	OrgVo orgVo = null;
	    	if("0".equals(org.getParentId())){
    			return org;
    		}
	    	for(OrgVo vo : treeDataList){
	    		if(org.getParentId().equals(vo.getId())){
    				List<OrgVo> child = new ArrayList<OrgVo>();
    				child.add(org);
    				vo.setChildren(child);
    				orgVo = getParentNodeUp(vo,treeDataList);
    				if(orgVo!=null && "0".equals(orgVo.getParentId())){
        				return orgVo;
        			}
    			}
	    		
    		}
	    	return null;
	    }
	
	/**
	 * 批量删除
	 * @param request
	 * @param response
	 */
	public ModelAndView delOrg(HttpServletRequest request,HttpServletResponse response) {
		//获取删除对象的id,批量删除id以","连接
		String ido = request.getParameter("id");
		//返回结果的HashMap
		Map<String,String> result = new HashMap<String,String>();
		
		try {
			//如果存在有要删除的ID,则进行删除
			if(StringUtils.isNotEmpty(ido)) {
				List<String> ids = CollectionUtil.toStringCollection(request.getParameter("id"));
				//执行删除
				orgBiz.deleteByIds(ids);
				//成功的消息
				result.put("success", "success");
			}else {
				//失败的消息
				result.put("success", "false");
				result.put("msg", "请选择要删除的数据");
			}
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			result.put("msg", "删除失败");
			logger.error("删除组织部门失败",e);
			return JsonModelAndView.newSingle(result);
		}
	}
}
