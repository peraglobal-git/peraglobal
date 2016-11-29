package com.peraglobal.km.crawler.task.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.peraglobal.km.crawler.source.biz.KnowledgeSourceBiz;
import com.peraglobal.km.crawler.source.model.KnowledgeSource;
import com.peraglobal.km.crawler.task.biz.TaskGroupBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.model.TaskGroup;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.pdp.admin.model.Module;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.enums.YesNoEnum;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

/**
 * 2015-12-16
 * 
 * @author zheng.duan
 * @see 采集任务组基本操作管理
 */
@Controller
public class TaskGroupController extends ExtendedMultiActionController {

	@Resource
	private TaskGroupBiz taskGroupBiz;
	@Resource
	private TaskJobBiz taskJobBiz;
	@Resource
	KnowledgeSourceBiz sourceBiz;

	/**
	 * 转到系统管理页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toCrawlerSystem(HttpServletRequest request, HttpServletResponse response, ModelAndView view) {
		String moduleId = getStringValue(request, "moduleId");
		view.setViewName("km/crawler/system");
		// 获取当前用户有权限匹配二级模块
		List<Module> resultList = Lists.newArrayList();
		// 当前模块
		Module model_source = new Module();
		model_source.setId("1");
		model_source.setModuleName("知识源管理");
		model_source.setLevelNum(1);
		model_source.setIsCanRefresh(YesNoEnum.Yes);
		model_source.setPath("source/knowledgeSource.html");
		resultList.add(model_source);

		Module model_task = new Module();
		model_task.setId("2");
		model_task.setModuleName("任务管理");
		model_task.setLevelNum(1);
		resultList.add(model_task);

		Module model_crawler = new Module();
		model_crawler.setId("21");
		model_crawler.setParentId("2");
		model_crawler.setModuleName("采集任务");
		model_crawler.setLevelNum(2);
		model_crawler.setIsCanRefresh(YesNoEnum.Yes);
		model_crawler.setPath("taskGroup/crawler.html");
		List childList = Lists.newArrayList();
		childList.add(model_crawler);

		Module model_transfer = new Module();
		model_transfer.setId("23");
		model_transfer.setParentId("2");
		model_transfer.setModuleName("传输任务");
		model_transfer.setLevelNum(2);
		model_transfer.setIsCanRefresh(YesNoEnum.Yes);
		model_transfer.setPath("taskGroup/transfer.html");
		childList.add(model_transfer);
		model_task.setChildList(childList);

		Module model_source3 = new Module();
		model_source3.setId("4");
		model_source3.setModuleName("统计分析");
		model_source3.setLevelNum(1);
		model_source3.setIsCanRefresh(YesNoEnum.Yes);
		model_source3.setPath("count/countPage.html");
		model_source3.setChildList(null);
		resultList.add(model_source3);

		view.addObject("module", model_source);
		view.addObject("modules", resultList);
		view.addObject("systemName", "采集管理");
		return view;
	}

	/**
	 * 修改采集信息页面跳转
	 * 
	 * @return
	 */
	public ModelAndView modifiedCollectInformation(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String id = getStringValue(request, "id");
		Map map = new HashMap();
		map.put("id", id);
		TaskJob job = taskJobBiz.queryJobById(map);
		String jobState = job.getJobState();
		if (jobState != null && jobState != "") {
			model.addObject("id", id);
			model.addObject("jobState", jobState);
			model.addObject("name", job.getName());
			model.addObject("groupName", job.getGroupName());
			model.addObject("registerType", job.getRegisterType());
			model.addObject("jobPriority", job.getJobPriority());
		}
		model.setViewName("km/crawler/task/editCollectedInformation");
		return model;
	}

	/**
	 * 采集管理入口
	 * 
	 * @param request
	 * @param response
	 * @author duanzheng
	 * @return
	 */
	public ModelAndView crawler(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/task/main");
		model.addObject("taskMaxNumber", AppConfigUtils.get("task.run.maxnum"));
		return model;
	}

	/**
	 * 转换管理入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView extract(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/extract/main");
		model.addObject("taskMaxNumber", AppConfigUtils.get("task.run.maxnum"));
		return model;
	}

	/**
	 * 功能:采集任务分组树
	 * <p>
	 * 作者 井晓丹 2015-12-22
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView getTaskGroupTree(HttpServletRequest request, HttpServletResponse response) {
		List<TreeNode> root = null;
		root = new ArrayList<TreeNode>();
		TreeNode tn = new TreeNode();
		tn.setId("0");
		tn.setText("知识源列表");
		tn.setIconCls("treeproj-icon icon-suo");
		List<KnowledgeSource> list = sourceBiz.findAll();
		List<TreeNode> listnode = new ArrayList<TreeNode>();
		if (list != null && list.size() > 0) {
			TreeNode treeNode = null;
			for (int i = 0; i < list.size(); i++) {
				treeNode = new TreeNode();
				treeNode.setId(list.get(i).getId());
				treeNode.setText(list.get(i).getName());
				listnode.add(treeNode);
			}
			tn.setChildren(listnode);
		}
		root.add(tn);
		return JsonModelAndView.newSingle(root);
	}

	/**
	 * 转换管理入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTaskGroupList(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		ListPageQuery parameters = new ListPageQuery();
		String typeId = getStringValue(request, "typeId");
		// 将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("typeId", typeId);
		List<TaskJob> tjs = taskJobBiz.queryJobListByInGroupId(map);
		mav.addObject("taskJobs", tjs);
		return this.putToModelAndViewJson(tjs, parameters);
	}

	/**
	 * 传输管理入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView transfer(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/transfer/main");
		model.addObject("taskMaxNumber", AppConfigUtils.get("task.run.maxnum"));
		return model;
	}

	/**
	 * 新建组时验证当前节点的组 名称是否重复
	 * 
	 * @param taskGroup
	 *            组对象
	 * @return 是否存在当前组名称
	 */
	public @ResponseBody String findGroupName(TaskGroup taskGroup) {
		boolean result = taskGroupBiz.findGroupName(taskGroup);
		return result + "";
	}

	/******** 转换组实现部分 ********/

	/**
	 * tree 页面
	 */
	public ModelAndView extractLeft(HttpServletRequest request, HttpServletResponse response) {
		String group_type = getStringValue(request, "group_type");
		ModelAndView mav = new ModelAndView();
		mav.addObject("group_type", group_type);
		mav.setViewName("extract/extractLeft");
		return mav;
	}

	/******** 传输组实现部分 ********/

	/**
	 * tree 页面
	 */
	public ModelAndView transferLeft(HttpServletRequest request, HttpServletResponse response) {
		String group_type = getStringValue(request, "group_type");
		ModelAndView mav = new ModelAndView();
		mav.addObject("group_type", group_type);
		mav.setViewName("transfer/transferLeft");
		return mav;
	}

}