package com.peraglobal.km.crawler.task.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.km.crawler.extract.biz.KnowledgeMetadataBiz;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.source.biz.KnowledgeSourceBiz;
import com.peraglobal.km.crawler.source.model.KnowledgeSource;
import com.peraglobal.km.crawler.task.biz.MonitorErrorBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.task.biz.TaskRegisterBiz;
import com.peraglobal.km.crawler.task.biz.TaskRuleBiz;
import com.peraglobal.km.crawler.task.biz.TaskTriggerBiz;
import com.peraglobal.km.crawler.task.biz.TransferConfigBiz;
import com.peraglobal.km.crawler.task.model.JdbcEnity;
import com.peraglobal.km.crawler.task.model.JdbcField;
import com.peraglobal.km.crawler.task.model.JobForm;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.LocalEntity;
import com.peraglobal.km.crawler.task.model.TaskConfig;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskJobMonitor;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.task.model.TaskTrigger;
import com.peraglobal.km.crawler.task.model.TransferConfig;
import com.peraglobal.km.crawler.util.CrawlerMD5Utils;
import com.peraglobal.km.crawler.util.DateUtil;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.JdbcConnectionUtil;
import com.peraglobal.km.crawler.web.biz.AttachPropertyBiz;
import com.peraglobal.km.crawler.web.biz.AttachmentCrawlerBiz;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.crawler.web.biz.GeneralTestProcessor;
import com.peraglobal.km.crawler.web.biz.RuleBiz;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.km.crawler.web.webmagic.WebSpider;
import com.peraglobal.km.crawler.web.webmagic.WebSpider.Status;
import com.peraglobal.km.crawler.web.webmagic.pipeline.FilePipeline;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.biz.MgKnowledgeMetadataBiz;
import com.peraglobal.pdp.common.id.IDGenerate;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

/**
 * <code>TaskJobController.java</code>
 * <p>
 * 功能:任务管理Controller
 * 
 * <p>
 * Copyright 安世亚太 2015 All right reserved.
 * 
 * @author 王晓鸣 时间 2015-12-17
 * @version 1.0 </br>最后修改人 无
 */
@Controller
public class TaskJobController extends ExtendedMultiActionController {
	// 常亮
	final String str = "YYYY-MM-dd HH:mm:ss";
	private Logger LOG = LoggerFactory.getLogger(TaskJobController.class);
	@Resource
	private TaskJobMonitorBiz taskJobMonitorBiz;
	@Resource
	private TaskJobBiz taskJobBiz;
	@Resource
	private TaskRegisterBiz taskRegisterBiz;
	@Resource
	private KnowledgeSourceBiz knowledgeSourceBiz;
	@Resource
	private TaskRuleBiz taskRuleBiz;
	@Resource
	private DatumBiz datumBiz;
	@Resource
	private TaskTriggerBiz taskTriggerBiz;
	@Resource
	private RuleBiz ruleBiz;
	@Resource
	private AttachPropertyBiz attachPropertyBiz;
	@Resource
	private QuartzScheduleBiz quartzScheduleBiz;
	@Resource
	private KnowledgeMetadataBiz knowledgeMetadataBiz;
	@Resource
	private AttachmentCrawlerBiz attachmentCrawlerBiz;
	@Resource
	private MonitorErrorBiz monitorErrorBiz;
	@Resource
	private MgDatumBiz mgDatumBiz;
	@Resource
	private MgKnowledgeMetadataBiz mgKnowledgeMetadataBiz;
	@Resource
	private TransferConfigBiz transferConfigBiz;
	/**
	 * 修改采集信息
	 * 
	 * @param request
	 * @param response
	 * @author duanzheng
	 */
	public ModelAndView updateJobInfo(HttpServletRequest request,
			HttpServletResponse response) {
		TaskJob job = new TaskJob();
		job.setId(getStringValue(request, "id"));
		job.setJobState(getStringValue(request, "jobState"));
		job.setGroupName(getStringValue(request, "groupName"));
		job.setRegisterType(getStringValue(request, "taskType"));
		job.setName(getStringValue(request, "taskName"));
		job.setJobPriority(getStringValue(request, "priority"));
		taskJobBiz.save(job);
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/task/main");
		return model;
	}

	/**
	 * 转换任务 任务监控 跳转页面
	 * 
	 * @param id
	 * @return
	 */
	public ModelAndView toTaskTransformJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String id = getStringValue(request, "id");
		model.addObject("id", id);
		model.setViewName("km/crawler/extract/taskExtract");
		return model;
	}

	/**
	 * 传输任务 任务监控 跳转页面
	 * 
	 * @param id
	 * @return
	 */
	public ModelAndView toTaskTransmissionJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		String id = getStringValue(request, "id");
		model.addObject("id", id);
		model.setViewName("km/crawler/transfer/getMobitoringList");
		return model;
	}

	/**
	 * 根据id获取监控列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTaskJobListById(HttpServletRequest request,
			HttpServletResponse response) {
		String jobid = getStringValue(request, "id");
		ListPageQuery list = new ListPageQuery();
		new ServletRequestDataBinder(list).bind(request);
		Condition con = Condition.parseCondition("jobid_string_eq");
		con.setValue(jobid);
		list.getConditions().getItems().add(con);
		List<TaskJobMonitor> map = taskJobMonitorBiz.find(list.getPagination(),
				con);
		return this.putToModelAndViewJson(map, list);
	}

	/**
	 * 新建本地采集任务打开批量上传页面 openUpload
	 * 
	 * @param group_id
	 *            组 ID
	 * @param register_type
	 *            采集任务类型
	 * @return 上传页面
	 */

	public ModelAndView openUpload(HttpServletRequest request,
			HttpServletResponse response) {
		String groupId = getStringValue(request, "groupId");
		String registerType = getStringValue(request, "registerType");
		ModelAndView mav = new ModelAndView();
		mav.addObject("groupId", groupId);
		mav.addObject("registerType", registerType);
		mav.setViewName("km/crawler/task/taskGroupBatchUpload");
		return mav;
	}

	/**
	 * 上传excel 同时创建本地采集任务
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return 任务列表
	 * @throws IOException
	 * @throws SchedulerException
	 */

	public ModelAndView uploadExcel(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			SchedulerException {
		ModelAndView mav = new ModelAndView();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		String getId = IDGenerate.uuid(); // 随机生成任务ID
		String sourceId = getStringValue(multipartRequest, "sourceId");
		String sourceName = getStringValue(multipartRequest, "sourceName");
//		String registerType = getStringValue(multipartRequest, "registerType");
		
		String name = getStringValue(multipartRequest, "name");
		
		// 知识形态
		String knowledgeType = getStringValue(request, "knowledgeType");
		String knowledgeTypeName = getStringValue(request, "knowledgeTypeName");
		// 知识模板
		String knowledgeModel = getStringValue(request, "knowledgeModel");
		String knowledgeModelName = getStringValue(request,
				"knowledgeModelName");
		// 存储系统
		String system = getStringValue(request, "system");
		String systemName = getStringValue(request, "systemName");
		
		TaskJob taskJob = new TaskJob();
		taskJob.setName(name);
		taskJob.setGroupId(sourceId);
		taskJob.setSourceId(sourceId);
		taskJob.setSourceName(sourceName);
		taskJob.setRegisterType(JobModel.TYPE_LOCAL);
		taskJob.setKnowledgeType(knowledgeType);
		taskJob.setKnowledgeTypeName(knowledgeTypeName);
		taskJob.setKnowledgeModel(knowledgeModel);
		taskJob.setKnowledgeModelName(knowledgeModelName);
		taskJob.setSystemId(system);
		taskJob.setSystemName(systemName);
		
		// 得到上传的文件
		MultipartFile mFile = multipartRequest.getFile("file");
		String fileName = "";
		// 转成file文件存到磁盘
		try {
			// 得到上传服务器的路径
			String path = AppConfigUtils.get("conf.excelPath");
			// 得到上传的文件的文件名
			fileName = mFile.getOriginalFilename();
			/*String name = taskJobBiz.queryJobListByJobName(fileName);
			taskJob.setName(name);*/
			path += getId + "/";
			File floder = new File(path);
			if (!floder.exists()) {
				floder.mkdirs();
			}
			mFile.transferTo(new File(path + fileName));
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String excelProperties = multipartRequest
				.getParameter("excelProperties");
		String metaKey = multipartRequest.getParameter("metaKey");
		LocalEntity local = new LocalEntity();
		local.setExcelProperties(excelProperties);
		local.setMetaKey(metaKey);
		local.setFileName(fileName);

		taskJob.setId(getId);
		taskJobBiz.addJob(taskJob, local);
		// 添加任务并开始任务
//		this.saveExtractAndTransferJob(request, sourceId, taskJob, null);
		mav.addObject("resultType", "close");
		mav.setViewName("km/crawler/task/taskGroupBatchUpload");
		return mav;// this.jobList(taskJob.getGroupId());
	}

	/**
	 * 初始化任务列表
	 * 
	 * @param group_id
	 *            组 ID
	 * @return 任务列表页面
	 */

	public ModelAndView findJosnList(HttpServletRequest request,
			HttpServletResponse response) {
		String registermodel = getStringValue(request, "registermodel");
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		Condition condition = Condition
				.parseCondition("registermodel_string_eq");
		condition.setValue(registermodel);
		parameters.getConditions().getItems().add(condition);
		List<TaskJob> tjs = taskJobBiz.find(parameters.getPagination(),
				condition);
		Map map = null;
		for (int i = 0, n = tjs.size(); i < n; i++) {
			if (tjs.get(i).getRegisterType() != null
					&& tjs.get(i).getRegisterType() != "") {
				map = new HashMap();
				if (tjs.get(i).getRegisterType().equals("4")) {// 根据registerType判断从哪个表中获取数据
					if (tjs.get(i).getId() != null && tjs.get(i).getId() != "") {
						//map.put("taskId", tjs.get(i).getId());
						tjs.get(i).setDatumCount(
								/*knowledgeMetadataBiz.queryExtractNumberBytaskId(map)*/
								mgKnowledgeMetadataBiz.findByTaskId(tjs.get(i).getId())
								);
					}
				}else if(tjs.get(i).getRegisterType().equals(JobModel.TYPE_TRANSFER)){
					tjs.get(i).setDatumCount(
							taskJobMonitorBiz.countTransferNumber(tjs.get(i)
									.getId()));
				} else {
					if (tjs.get(i).getId() != null && tjs.get(i).getId() != "") {
						// 查询采集数量
						//map.put("id", tjs.get(i).getId());
						tjs.get(i).setDatumCount(
								/*datumBiz.numberOfQueriesToCollect(map)*/
								mgDatumBiz.findByTaskId(tjs.get(i).getId())
								);
								
					}
				}
			}
		}
		ModelAndView modelAndView = this.putToModelAndViewJson(tjs, parameters,
				str);
		return modelAndView;
	}

	/**
	 * 点击树加载相应的列表
	 * 
	 */
	public ModelAndView findTreeJosnList(HttpServletRequest request,
			HttpServletResponse response) {
		String sourceId = getStringValue(request, "sourceId");
		String registermodel = getStringValue(request, "registermodel");
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		Map map = new HashMap();
		map.put("sourceId", sourceId);
		map.put("registermodel", registermodel);
		List<TaskJob> tjs = taskJobBiz.dependingOnTheTypeOfIdAndQueryTasks(
				parameters.getPagination(), map);
		return this.putToModelAndViewJson(tjs, parameters, str);
	}

	/**
	 * 打开新建传输任务页面
	 * 
	 * @param taskJob
	 *            任务对象
	 * @return 转换任务新建页面
	 */

	public ModelAndView showAddTransferJob(HttpServletRequest request,
			HttpServletResponse response, JobForm taskJob) {
		ModelAndView mav = new ModelAndView();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 2);
		List<TaskJob> extractJobs = this.taskJobBiz
				.queryJobListByInGroupId(map);
		mav.addObject("extractJobs", extractJobs);
		mav.addObject("taskJob", taskJob);
		mav.setViewName("transfer/transferJob");
		return mav;
	}

	/**
	 * 功能:保存传输任务页面跳转
	 * <p>
	 * 作者 井晓丹 2015-12-24
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSaveTransferTaskPage(HttpServletRequest request,
			HttpServletResponse response) {
		
		ModelAndView view = new ModelAndView("km/crawler/transfer/saveTransferTask");
		// 增加查询知识源列表
		List<KnowledgeSource> sourceList = knowledgeSourceBiz.findAll(); 
		view.addObject("sourceList", sourceList); 
		return view;
	}

	/**
	 * 功能:保存转换任务页面跳转
	 * <p>
	 * 作者 井晓丹 2015-12-24
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSaveExtractTaskPage(HttpServletRequest request,
			HttpServletResponse response) {
		String id = getStringValue(request, "id");
		String groupId = getStringValue(request, "groupId");
		String name1 = getStringValue(request, "name");
		Condition condition = Condition
				.parseCondition("registermodel_string_eq");
		condition.setValue("1");
		List<TaskJob> tjs = taskJobBiz.find(null, condition);
		ModelAndView view = new ModelAndView(
				"km/crawler/extract/saveExtractTask");
		if (id != null && id != "") {
			TaskJob jobbyId = taskJobBiz.findById(id);
			String description = jobbyId.getDescription();
			String connectId = jobbyId.getConnectId();
			view.addObject("description", description);
			view.addObject("connectId", connectId);
		}
		view.addObject("name", name1);
		view.addObject("groupId", groupId);
		view.addObject("id", id);
		view.addObject("taskList", tjs);
		return view;
	}

	/**
	 * 功能：批量删除
	 * <p>
	 * 作者 王晓鸣 2015-12-2
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delTransferTaskJob(HttpServletRequest request,
			HttpServletResponse response) {
		String ido = request.getParameter("id");
		Map<String, String> result = new HashMap<String, String>();

		try {
			if (StringUtils.isNotEmpty(ido)) {
				List<String> ids = CollectionUtil.toStringCollection(ido);
				taskJobBiz.deleteByIds(ids);
				result.put("success", "success");
			} else {
				result.put("success", "false");
				result.put("msg", "请选择要删除的数据");
			}
		} catch (Exception e) {
			result.put("success", "false");
			result.put("msg", "删除失败");
			logger.error("删除任务失败", e);
		}
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:保存或修改 转换任务
	 * <p>
	 * 作者 王晓鸣 2015-12-29
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveExtractTaskJob(HttpServletRequest request,
			HttpServletResponse response, TaskJob taskJob) {
		Map<String, Object> result = new HashMap<String, Object>();
		String connectId = request.getParameter("TaskSelect");
		String groupId = getStringValue(request, "groupId");
		String operateStr = null;
		String id = taskJob.getId();
		if (id == null) {
			taskJob.setId(null);
			operateStr = "添加";
			taskJob.setGroupId(groupId);
			taskJob.setConnectId(connectId);
			taskJob.setRegisterModel(JobModel.MODEL_EXTRACT);
			taskJob.setJobState(JobModel.STATE_READY);
		} else {
			taskJob.setId(id);
			operateStr = "修改";
			taskJob.setConnectId(connectId);
			taskJob.setRegisterModel(JobModel.MODEL_EXTRACT);
			// taskJob.setJobState(JobModel.STATE_READY);
		}
		try {
			taskJobBiz.save(taskJob);
			result.put("code", "ok");
			operateStr += "成功！";
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveTransferTaskJob save exception", e);
		}
		result.put("id", taskJob.getId());
		result.put("msg", operateStr);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:保存或修改 传输任务
	 * <p>
	 * 作者 井晓丹 2015-12-24
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveTransferTaskJob(HttpServletRequest request,
			HttpServletResponse response, TaskJob taskJob) {
		Map<String, Object> result = new HashMap<String, Object>();
		String TaskSelect = request.getParameter("TaskSelect");

		String systemId = request.getParameter("system");
		String sourceId = getStringValue(request, "sourceId");
		String sourceName = getStringValue(request, "sourceName");
		String systemname = request.getParameter("systemName");
		
		try {
			if (systemname != null && systemname != "") {
				// String systemnameutf = new
				// String(systemname.getBytes("iso-8859-1"),"utf-8");
				// taskJob.setSystemName(systemnameutf);
				taskJob.setSystemName(systemname);
			}
			if (systemId != null && systemId != "") {
				taskJob.setSystemId(systemId);
			}
			Dom4jXmlUtil dm=new Dom4jXmlUtil();
			CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
			String linktype=request.getParameter("SelectType");
			String linkstate=getStringValue(request, "linkState");
//			if ("2".equals(linktype)) {
			TransferConfig tc = null;
			if (true) {
				tc = new TransferConfig();
				String userName=getStringValue(request, "username");
				String password=getStringValue(request, "password");
				String insertSQL = getStringValue(request,"insertSQL");
				tc.setUsername(userName);
				String DBtype=request.getParameter("DBSelectType");
				String driver=null;
				if (DBtype.equals("oracle")) {
					driver="oracle.jdbc.driver.OracleDriver";
				}else if (DBtype.equals("mysql")) {
					driver="com.mysql.jdbc.Driver";
				}else if (DBtype.equals("sqlserver")) {
					driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
					//String url="jdbc:sqlserver://localhost:1433; DatabaseName=UniversityDB";
				}
				String name=getStringValue(request, "dataname");
				String port=getStringValue(request, "port");
				String dburl=getStringValue(request, "dburl");
				String[] filedNames=new String[5];
				String[] knowledgeKeys= new String[5];
				filedNames[0]="name";
				knowledgeKeys[0]=name;
				filedNames[1]="type";
				knowledgeKeys[1]=DBtype;
				filedNames[2]="driver";
				knowledgeKeys[2]=driver;
				filedNames[3]="url";
				knowledgeKeys[3]=dburl;
				filedNames[4]="port";
				knowledgeKeys[4]=port;
				//再次进行数据库链接验证
				String state=new JdbcConnectionUtil().LinkVerification(DBtype, dburl, port, name, userName, password);
				String linkcontent=dm.extractMapping(filedNames, knowledgeKeys);
				tc.setPassword(md5Utils.encrypt(password));
				tc.setLinkContent(linkcontent);
				tc.setLinkType(linktype);
				tc.setLinkState(state);
				tc.setInsertSQL(insertSQL);
			}
			
			// String name=getStringValue(request, "name");
			TaskTrigger taskTrigger = null;
			String triggerchecked = getStringValue(request, "triggerchecked");
			if ("0".equals(triggerchecked)) {
				taskTrigger = getTrigger(request, response);
				taskTrigger.setJobName(taskJob.getName());
				taskJob.setAutomatic(0);
			} else {
				taskJob.setAutomatic(1);
			}

			String operateStr = null;
			String id = taskJob.getId();
			if (id == null) {
				taskJob.setId(null);
				operateStr = "添加";
				taskJob.setSourceId(sourceId);
				taskJob.setSourceName(sourceName);
				if (TaskSelect != null && TaskSelect != "") {
					String[] tsk = TaskSelect.split(",");
					taskJob.setConnectId(tsk[0]);
					taskJob.setName(tsk[1]);
					TaskJob extractJob = this.taskJobBiz.findById(tsk[0]);
					taskJob.setKnowledgeType(extractJob.getKnowledgeType());
					taskJob.setKnowledgeTypeName(extractJob.getKnowledgeTypeName());
					taskJob.setKnowledgeModel(extractJob.getKnowledgeModel());
					taskJob.setKnowledgeModelName(extractJob.getKnowledgeModelName());
				}
				taskJob.setRegisterType(JobModel.TYPE_TRANSFER);
				taskJob.setRegisterModel(JobModel.MODEL_TRANSFER);
				taskJob.setJobState(JobModel.STATE_READY);
			} else {
				taskJob.setId(id);
				TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(id);
				if (taskTrigger != null && trigger != null) {
					taskTrigger.setId(trigger.getId());
				}
				operateStr = "修改";
				// taskJob.setConnectId(connectId);
				taskJob.setRegisterModel(JobModel.MODEL_TRANSFER);
				//taskJob.setJobState(JobModel.STATE_READY);
			}
			try {
				taskJobBiz.addTransfer(taskJob, taskTrigger,tc);
				result.put("code", "ok");
				operateStr += "成功！";
			} catch (Exception e) {
				result.put("code", "fail");
				operateStr += "失败！";
				logger.error(" saveTransferTaskJob save exception", e);
			}
			result.put("msg", operateStr);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.put("id", taskJob.getId());

		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:保存或修改 db采集任务
	 * <p>
	 * 作者 井晓丹 2015-12-24
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public ModelAndView saveDBTaskJob(HttpServletRequest request,
			HttpServletResponse response, TaskJob taskJob) throws Exception {
		boolean updFlg = false;
		String id = taskJob.getId();
		if (id != null) {
			updFlg = true;
		}
		boolean updName = false;
		String oldName = getStringValue(request, "oldname");
		if (updFlg && !oldName.equals(taskJob.getName())) {
			// 需要修改转换和传输任务的名称
			updName = true;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		Dom4jXmlUtil dm = new Dom4jXmlUtil();
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		String sourceId = getStringValue(request, "sourceId");
		String sourceName = getStringValue(request, "sourceName");
		/*List<Map<String, Object>> mapListJson = null;
		List list = new ArrayList();
		if (!updFlg) {
			// 知识模板
			String knowledgeModel = getStringValue(request, "knowledgeModel");
			// 转换规则
			String modelPros = externalRestBiz
					.getModelProperties(knowledgeModel);
			JSONArray jsonArray = JSONArray.fromObject(modelPros);
			mapListJson = (List) jsonArray;
		}*/
		// 从页面获取数据库规则属性
		String[] field = getArrayValue(request, "field");
		String[] fieldName = new String[field.length];
		String[] fieldType = new String[field.length];
		String[] fieldAs = new String[field.length];
		for (int i = 0; i < field.length; i++) {
			String fieldArray[] = field[i].split(";");
			fieldName[i] = fieldArray[0];
			fieldAs[i] = URLDecoder.decode(fieldArray[2],"utf8");
			fieldType[i] = fieldArray[1];
			/*if (!updFlg) {
				if(fieldArray[2].indexOf("INHERENT")!=-1){
					list.add(fieldArray[2]);
					continue;
				}
				for (int j = 0; j < mapListJson.size(); j++) {
					boolean ifBreak = false;
					Map<String, Object> obj = mapListJson.get(j);
					for (Entry<String, Object> entry : obj.entrySet()) {
						String strkey = entry.getKey();
						if (!strkey.equals("PROP_ID"))
							continue;
						if (entry.getValue().equals(fieldArray[2].split(":")[0])) {
							list.add(fieldArray[2]);
							ifBreak = true;
							break;
						}
					}
					if (ifBreak)
						break;
				}
			}*/
		}

		// String dataname=getStringValue(request, "dataname");
		// String url=getStringValue(request, "DBurl");
		// String type=getStringValue(request, "type");
		// String user=getStringValue(request, "user");
		// String password=getStringValue(request, "password");
		String entityname = request.getParameter("TaskSelect");
		KnowledgeSource source = knowledgeSourceBiz.findById(sourceId);
		Map<String, String> map = dm.generateMap(source.getLinkContent());
		String DBurl = null;
		String DBType = map.get("type");
		String url = map.get("url");
		String port = map.get("port");
		String dataname = map.get("name");
		String type = "JdbcDataSource";
		if (DBType.equals("oracle")) {
			DBurl = "jdbc:oracle:thin:@" + url + ":" + port + ":" + dataname;
		} else if (DBType.equals("mysql")) {
			DBurl = "jdbc:mysql://" + url + ":" + port + "/" + dataname;
		} else if (DBType.equals("sqlserver")) {
			DBurl = "jdbc:sqlserver://" + url + ":" + port + ";DatabaseName="
					+ dataname;
		}
		String pkid = request.getParameter("pkid");
		String attachmentAs = request.getParameter("attachmentAs");
		String attachmentName = request.getParameter("attachmentName");
		String attachmentFileName = request.getParameter("attachmentFileName");
		String attachmentFileType = request.getParameter("attachmentFileType");

		JdbcEnity jdbcEnity = new JdbcEnity();
		jdbcEnity.setName(dataname);
		jdbcEnity.setUrl(DBurl);
		jdbcEnity.setType(type);
		jdbcEnity.setUser(source.getUsername());
		jdbcEnity.setPassword(md5Utils.Decryption(source.getPassword()));
		jdbcEnity.setDriver(map.get("driver"));
		jdbcEnity.setEntityName(entityname);
		jdbcEnity.setPkid(pkid);
		jdbcEnity.setAttachmentAs(attachmentAs);
		jdbcEnity.setAttachmentName(attachmentName);
		jdbcEnity.setAttachmentFileName(attachmentFileName);
		jdbcEnity.setAttachmentFileType(attachmentFileType);
		jdbcEnity.setFieldAs(fieldAs);
		jdbcEnity.setFieldName(fieldName);
		jdbcEnity.setFieldType(fieldType);
		// 附件抽取规则
		String extractList = getStringValue(request, "extractList");
		if(extractList!=null){
			taskJob.setExtractList(extractList);
		}
		TaskTrigger taskTrigger = null;
		String triggerchecked = getStringValue(request, "triggerchecked");
		if ("0".equals(triggerchecked)) {
			taskTrigger = getTrigger(request, response);
			taskTrigger.setJobName(taskJob.getName());
			taskJob.setAutomatic(0);
		} else {
			taskJob.setAutomatic(1);
		}
		String operateStr = null;
		if (id == null) {
			taskJob.setId(null);
			operateStr = "添加";
			taskJob.setSourceId(sourceId);
			taskJob.setSourceName(sourceName);
			taskJob.setRegisterModel(JobModel.MODEL_CRAWLER);
			taskJob.setRegisterType(JobModel.TYPE_DB);
			taskJob.setJobState(JobModel.STATE_READY);
		} else {
			taskJob.setId(id);
			TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(id);
			if (taskTrigger != null && trigger != null) {
				taskTrigger.setId(trigger.getId());
			}
			operateStr = "修改";
			taskJob.setRegisterModel(JobModel.MODEL_CRAWLER);
			// taskJob.setJobState(JobModel.STATE_READY);
		}
		/*// 知识形态
		String knowledgeType = getStringValue(request, "knowledgeType");
		String knowledgeTypeName = getStringValue(request, "knowledgeTypeName");
		// 知识模板
		String knowledgeModel = getStringValue(request, "knowledgeModel");
		String knowledgeModelName = getStringValue(request,
				"knowledgeModelName");
		// 存储系统
		String system = getStringValue(request, "system");
		String systemName = getStringValue(request, "systemName");
		taskJob.setKnowledgeType(knowledgeType);
		taskJob.setKnowledgeTypeName(knowledgeTypeName);
		taskJob.setKnowledgeModel(knowledgeModel);
		taskJob.setKnowledgeModelName(knowledgeModelName);
		taskJob.setSystemId(system);
		taskJob.setSystemName(systemName);*/
		try {
			String jobId = taskJobBiz.addDbJob(taskJob, jdbcEnity, taskTrigger);
			// taskJob.setId(jobId);
			if (id == null) {
				// 同时创建转换和传输任务
//				this.saveExtractAndTransferJob(request, sourceId, taskJob, null);
			} else {
				// 需要修改转换和传输的任务名称
				if (updName) {
					// select * from taskjob t start with id = '' connect by
					// prior t.id = t.connectid ;
					List<TaskJob> listJob = taskJobBiz
							.findAllTaskByCrawlerJobId(jobId);
					for (int i = 0; i < listJob.size(); i++) {
						listJob.get(i).setName(taskJob.getName());
					}
					taskJobBiz.updateBatch(listJob);
				}
			}
			// 是否开始任务
			String startTask = getStringValue(request, "startTask");
			if (startTask.equals("1")) {
				// 开始任务
				// taskJob.setJobState(JobModel.STATE_STRAT);
				quartzScheduleBiz.startJob(taskJob);
			}
			result.put("code", "ok");
			operateStr += "成功！";
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveTransferTaskJob save exception", e);
		}
		result.put("id", taskJob.getId());
		result.put("msg", operateStr);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:验证任务名是否重复
	 * <p>
	 * 作者 井晓丹 2015-12-24
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView validateNameByAjax(HttpServletRequest request,
			HttpServletResponse response) {
		String name = getStringValue(request, "name");
		String oldName = getStringValue(request, "oldName");
		// 如果没有修改名称则直接返回
		if (name.equals(oldName)) {
			return JsonModelAndView.newSingle(true);
		}
		String groupId = getStringValue(request, "groupId");
		String model = getStringValue(request, "registerModel");
		Condition condition = Condition.parseCondition("name_eq");
		condition.setValue(name);
		Condition conditionGroupId = Condition.parseCondition("sourceId_eq");
		conditionGroupId.setValue(groupId);
		Condition conditionModel = Condition.parseCondition("registerModel_eq");
		conditionModel.setValue(model);
		List<Condition> conList = new ArrayList<Condition>();
		conList.add(condition);
		conList.add(conditionGroupId);
		conList.add(conditionModel);

		List<TaskJob> jobList = taskJobBiz.find(null, conList);
		if (!CollectionUtil.isEmpty(jobList)) {
			return JsonModelAndView.newSingle("任务名称重复");
		}
		return JsonModelAndView.newSingle(true);
	}
	/**
	 * 功能:验证本地采集任务名是否重复
	 * <p>
	 * 作者 井晓丹 2016-4-18
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView validateLocalNameByAjax(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		String name = getStringValue(request, "name");
		String oldName = getStringValue(request, "oldName");
		// 如果没有修改名称则直接返回
		if (name.equals(oldName)) {
			result.put("state", "0");
			return this.putToModelAndViewJson(result);
		}else if(!(name.matches("[a-zA-Z0-9._@\u4E00-\u9FA5]+$"))){
			result.put("state", "2");
			return this.putToModelAndViewJson(result);
		}
		String groupId = getStringValue(request, "groupId");
		String model = getStringValue(request, "registerModel");
		Condition condition = Condition.parseCondition("name_eq");
		condition.setValue(name);
		Condition conditionGroupId = Condition.parseCondition("sourceId_eq");
		conditionGroupId.setValue(groupId);
		Condition conditionModel = Condition.parseCondition("registerModel_eq");
		conditionModel.setValue(model);
		List<Condition> conList = new ArrayList<Condition>();
		conList.add(condition);
		conList.add(conditionGroupId);
		conList.add(conditionModel);
		
		List<TaskJob> jobList = taskJobBiz.find(null, conList);
		if (!CollectionUtil.isEmpty(jobList)) {
			result.put("state", "1");
		}else{
			result.put("state", "0");
		}
		return this.putToModelAndViewJson(result);
	}
	/**
	 * 功能:创建采集任务
	 * <p>
	 * 作者 井晓丹 2015-12-30
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView toCrawlerJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		String groupId = getStringValue(request, "groupId");
		KnowledgeSource source = knowledgeSourceBiz.findById(groupId);
		String sourceType = source.getLinkType();
		ModelAndView model = new ModelAndView();
		Random rnd = new Random();
		int num = 100 + rnd.nextInt(900);
		String taskName = source.getName() + "_"
				+ DateUtil.format(new Date(), "yyyyMMdd") + "_" + num;
		model.addObject("taskName", taskName);
		// 获取知识形态
		/*String typeList = extenalRestBiz.getTypeList();
		model.addObject("typeList", JSONArray.fromObject(typeList));*/
		// 获取xml解析对象
		Dom4jXmlUtil dm = new Dom4jXmlUtil();
		// 获取MD5加密对象
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		model.addObject("groupId", groupId);
		model.addObject("sourceType", sourceType);
		if (sourceType.equals(JobModel.TYPE_WEB)) {
			// 知识源是网站，打开web采集创建页面
			List<List<TaskConfig>> tcs = taskRegisterBiz
					.getTaskConfigByType(JobModel.TYPE_WEB);
			// 获取km字段规则
			/*String properties = externalRestBiz.getModelProperties();
			properties = exchangeJsonPros(properties);
			model.addObject("properties", JSONArray.fromObject(properties));*/
			model.addObject("tcs", tcs);
			model.addObject("ruleArrayLength", 2);
			model.setViewName("km/crawler/task/saveWebTask");
		} else if (sourceType.equals(JobModel.TYPE_DB)) {
			// 获取数据库tables列表
			Map<String, String> map = dm.generateMap(source.getLinkContent());
			JdbcEnity jdbcEnity = new JdbcEnity();
			String DBurl = null;
			String DBType = map.get("type");
			String url = map.get("url");
			String port = map.get("port");
			String name = map.get("name");
			String type = "JdbcDataSource";
			if (DBType.equals("oracle")) {
				DBurl = "jdbc:oracle:thin:@" + url + ":" + port + ":" + name;
			} else if (DBType.equals("mysql")) {
				DBurl = "jdbc:mysql://" + url + ":" + port + "/" + name;
			} else if (DBType.equals("sqlserver")) {
				DBurl = "jdbc:sqlserver://" + url + ":" + port
						+ ";DatabaseName=" + name;
			}
			jdbcEnity.setUrl(DBurl);
			jdbcEnity.setType(type);
			jdbcEnity.setUser(source.getUsername());
			jdbcEnity.setPassword(md5Utils.Decryption(source.getPassword()));
			jdbcEnity.setDriver(map.get("driver"));
			List<String> tables = taskJobBiz.getTables(jdbcEnity, DBType);
			/*String properties = externalRestBiz.getModelProperties();
			properties = exchangeJsonPros(properties);*/
			// 知识源是数据库，打开DB采集创建页面
			model.addObject("tables", tables);
//			model.addObject("properties", properties);
			model.setViewName("km/crawler/task/saveDBTask");
		} else if (sourceType.equals(JobModel.TYPE_LOCAL)) {
			// 跳转本地采集页面
			String id = getStringValue(request, "groupId");
			String registerType = getStringValue(request, "registerType");
			model.addObject("groupId", id);
			model.addObject("registerType", registerType);
			model.setViewName("km/crawler/task/taskGroupBatchUpload");
		}
		return model;
	}
	
	public ModelAndView toCrawlerJob(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		// 获取所有知识源
		List<KnowledgeSource> sourceList = this.knowledgeSourceBiz.queryKnowledgeSourceList();
		// 获取知识形态
		/*String typeList = extenalRestBiz.getTypeList();
		model.addObject("typeList", JSONArray.fromObject(typeList));*/
		model.addObject("sourceList", sourceList);
		model.setViewName("km/crawler/task/createCrawlerTask");
		return model;
	}
	
	/**
	 * 功能:创建采集任务(web类)
	 * <p>
	 * 作者 井晓丹 2016-4-8
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView toCrawlerWebJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		// 获取所有知识源
		List<KnowledgeSource> sourceList = this.knowledgeSourceBiz.queryKnowledgeSourceList();
		String sourceId = getStringValue(request, "sourceId");
		sourceId = sourceId == null ? "" :sourceId;
		String sourceName = getStringValue(request, "sourceName");
		if(sourceName != null){
			try {
				sourceName=URLDecoder.decode(sourceName,"utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Random rnd = new Random();
			int num = 100 + rnd.nextInt(900);
			String taskName = sourceName + "_"
					+ DateUtil.format(new Date(), "yyyyMMdd") + "_" + num;
			model.addObject("taskName", taskName);
		}
		
		// 获取知识形态
		/*String typeList = extenalRestBiz.getTypeList();
		model.addObject("typeList", JSONArray.fromObject(typeList));
		// 获取知识固有属性
		String inherentProp = extenalRestBiz.getInherentProp();
		JSONArray inherentPropArray = JSONArray.fromObject(inherentProp);
		model.addObject("inherentProp", inherentPropArray);
		model.addObject("inherentPropSize", inherentPropArray.size()+1);*/
		// 知识源是网站，打开web采集创建页面
		List<List<TaskConfig>> tcs = taskRegisterBiz
				.getTaskConfigByType(JobModel.TYPE_WEB);
		// 获取km字段规则
		/*String properties = externalRestBiz.getModelProperties();
		properties = exchangeJsonPros(properties);*/
		model.addObject("inherentPropSize", 1);
		model.addObject("tcs", tcs);
//		model.addObject("properties", JSONArray.fromObject(properties));
		model.addObject("ruleArrayLength", 2);
		model.addObject("sourceId", sourceId);
		model.addObject("sourceName", sourceName);
		model.addObject("sourceList", sourceList);
		model.setViewName("km/crawler/task/saveWebTask");
		return model;
	}
	/**
	 * 功能:创建采集任务
	 * <p>
	 * 作者 井晓丹 2015-12-30
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView toCrawlerDBJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		// 获取所有知识源
		List<KnowledgeSource> sourceList = this.knowledgeSourceBiz.queryKnowledgeSourceList();
		/*// 获取知识形态
		String typeList = extenalRestBiz.getTypeList();
		//获取知识模板固有属性
		String inherentProp = extenalRestBiz.getInherentProp();
		model.addObject("inherentProp", JSONArray.fromObject(inherentProp));
		model.addObject("typeList", JSONArray.fromObject(typeList));*/
		// 获取xml解析对象
		Dom4jXmlUtil dm = new Dom4jXmlUtil();
		// 获取MD5加密对象
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		String sourceId = getStringValue(request, "sourceId");
		String sourceName = getStringValue(request, "sourceName");
		if(sourceName != null){
			try {
				sourceName=URLDecoder.decode(sourceName,"utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Random rnd = new Random();
			int num = 100 + rnd.nextInt(900);
			String taskName = sourceName + "_"
					+ DateUtil.format(new Date(), "yyyyMMdd") + "_" + num;
			model.addObject("taskName", taskName);
		}
		KnowledgeSource source = knowledgeSourceBiz.findById(sourceId);
		// 获取数据库tables列表
		Map<String, String> map = dm.generateMap(source.getLinkContent());
		JdbcEnity jdbcEnity = new JdbcEnity();
		String DBurl = null;
		String DBType = map.get("type");
		String url = map.get("url");
		String port = map.get("port");
		String name = map.get("name");
		String type = "JdbcDataSource";
		if (DBType.equals("oracle")) {
			DBurl = "jdbc:oracle:thin:@" + url + ":" + port + ":" + name;
		} else if (DBType.equals("mysql")) {
			DBurl = "jdbc:mysql://" + url + ":" + port + "/" + name;
		} else if (DBType.equals("sqlserver")) {
			DBurl = "jdbc:sqlserver://" + url + ":" + port
					+ ";DatabaseName=" + name;
		}
		jdbcEnity.setUrl(DBurl);
		jdbcEnity.setType(type);
		jdbcEnity.setUser(source.getUsername());
		jdbcEnity.setPassword(md5Utils.Decryption(source.getPassword()));
		jdbcEnity.setDriver(map.get("driver"));
		List<String> tables = taskJobBiz.getTables(jdbcEnity, DBType);
		/*String properties = externalRestBiz.getModelProperties();
		properties = exchangeJsonPros(properties);*/
		// 知识源是数据库，打开DB采集创建页面
		model.addObject("tables", tables);
//		model.addObject("properties", properties);
		model.addObject("sourceId", sourceId);
		model.addObject("sourceName", sourceName);
		model.addObject("sourceList", sourceList);
		model.setViewName("km/crawler/task/saveDBTask");
		return model;
	}
	/**
	 * 功能:创建采集任务(本地类)
	 * <p>
	 * 作者 井晓丹 2016-4-8
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView toCrawlerLocalJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		// 跳转本地采集页面
		String sourceId = getStringValue(request, "sourceId");
		String sourceName = getStringValue(request, "sourceName");
		if(sourceName != null){
			try {
				sourceName=URLDecoder.decode(sourceName,"utf8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Random rnd = new Random();
			int num = 100 + rnd.nextInt(900);
			String taskName = sourceName + "_"
					+ DateUtil.format(new Date(), "yyyyMMdd") + "_" + num;
			model.addObject("taskName", taskName);
		}
		String registerType = getStringValue(request, "registerType");
		
		// 获取所有知识源
		List<KnowledgeSource> sourceList = this.knowledgeSourceBiz.queryKnowledgeSourceList();
		// 获取知识形态
		/*String typeList = extenalRestBiz.getTypeList();
		model.addObject("typeList", JSONArray.fromObject(typeList));*/
		model.addObject("sourceId", sourceId);
		model.addObject("sourceName", sourceName);
		model.addObject("registerType", registerType);
		model.addObject("sourceList", sourceList);
		model.setViewName("km/crawler/task/taskGroupBatchUpload");
		return model;
	}

	/**
	 * 功能:创建转换任务
	 * <p>
	 * 作者 段政 2016-3-4
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView toExtractJobPage(HttpServletRequest request,
			HttpServletResponse response) {
		
		/* 修改备注（yongqian.liu）：删除 sourceId，propertyId 属性
		String propertyId = getStringValue(request, "propertyId"); // 模板id
		String registermodel = getStringValue(request, "registermodel"); // 模块（1：采集；2：转换；3：传输）
		String sourceId = getStringValue(request, "sourceId");// 资源id
		if (sourceId == null || sourceId == "" || sourceId.equals("0")) {
			sourceId = tj.getSourceId();
		}
		*/
		
		List<Map> resultMap = new ArrayList<Map>();
		ModelAndView model = new ModelAndView();
		String id = getStringValue(request, "id");// 转换任务的id，修改时使用
		String operationType = getStringValue(request, "operationType"); // 操作类型，判断此操作是添加还是编辑
		
		// 获取知识形态
		/*String typeList = extenalRestBiz.getTypeList();
		model.addObject("typeList", JSONArray.fromObject(typeList));*/
		model.addObject("operationType", operationType);
		Map map = new HashMap();
		
		// 编辑转换任务
		if (operationType.equals("update")) {
			TaskJob tj = taskJobBiz.findById(id);
			// 修改备注（yongqian.liu）：根据知识源ID获得知识源对象
			KnowledgeSource knowledgeSource = knowledgeSourceBiz.findById(tj.getSourceId());
			//抽取规则
			String extractList=tj.getExtractList();
			if(extractList!=null&&extractList!=""){
				List exList=Arrays.asList(extractList.split(","));
				model.addObject("extractList",exList);
			}
			model.addObject("sourceId", tj.getSourceId());
			model.addObject("sourceName", knowledgeSource.getName());
			model.addObject("id", id);// 页面上的转换任务
			model.addObject("name", tj.getName());// 页面上的转换任务
			if (!(id.equals(null) && "null".equals(id))) {
				if (tj != null) {
					String connectId = tj.getConnectId();// 选择采集任务
					TaskJob crawlerJob = taskJobBiz.findById(connectId);
					map.put("name", tj.getName());
					// 判断是修改还是添加
					model.addObject("jobState", tj.getJobState());
					model.addObject("knowledgeType", tj.getKnowledgeType());
					// 调用webService接口获取知识形态下模板集合
					/*String models = externalRestBiz.getModelListByTypeId(tj
							.getKnowledgeType());
					JSONArray jsonArr = JSONArray.fromObject(models);
					model.addObject("knowledgeTypeList", jsonArr);*/

					model.addObject("knowledgeModel", tj.getKnowledgeModel());

					model.addObject("systemId", tj.getSystemId());
					String jsonString = null;
					// 判断是web，db还是local
					if (crawlerJob.getRegisterType() != null
							&& crawlerJob.getRegisterType() != "") {
						if (crawlerJob.getRegisterType().equals("1")) {
							Map map1 = new HashMap();
							map1.put("id", connectId);
							List<Rule> ruleList = ruleBiz.queryTaskRuleById(map1);
							resultMap = getRule(null, null, ruleList);
						} else {
							List<TaskRule> tr = taskRuleBiz
									.queryTaskRuleByjobId(connectId);
							resultMap = getRule(null, tr, null);
						}
					}
					if (resultMap.size() > 0) {
						// 获取km字段规则
						List<Map> list = queryRuleTaskRulesAndParsing(tj.getKnowledgeModel());//获取模板下的属性
						if(list.size()>0){//如果模板有属性执行一下代码，否则就全部置灰
							for (int i = 0,n=resultMap.size(); i <n; i++) {// 从数据库中获取到的属性
								if(!StringUtils.isNumeric(resultMap.get(i).get("key").toString())){
									resultMap.get(i).put("hide", "false");
									continue;
								}
								for (int j = 0,c=list.size(); j <c; j++) {// 模板的属性
									String resultMapKey = ""
											+ resultMap.get(i).get("key");
									String listKey = ""
											+ list.get(j).get("PROP_ID");
									if (resultMapKey.equals(listKey)
											|| listKey.equals(resultMapKey)) {
										resultMap.get(i).put("hide", "false");
										break;
									} else {
										resultMap.get(i).put("hide", "true");
									}
								}
							}
						}else{
							for (int i = 0,n=resultMap.size(); i <n; i++) {
								resultMap.get(i).put("hide", "true");
							}
						}

						List<Map> listMap = new ArrayList<Map>();
						// 判断是web，db还是local
						List<TaskRule> tr = taskRuleBiz.queryTaskRuleByjobId(id);//查询自己的规则
						listMap = getRule(null, tr, null);//解析规则
						if (listMap.size() > 0) {
							for (int i = 0,n=resultMap.size(); i <n ; i++) {
								String hide = ""+ resultMap.get(i).get("hide");
								if (hide != null && hide.equals("false")) {
									for (int j2 = 0,c=listMap.size(); j2 < c; j2++) {
										String key = ""+ resultMap.get(i).get("key");
										String PROP_ID = ""+ listMap.get(j2).get("key");
										if (key.equals(PROP_ID)|| PROP_ID.equals(key)) {
											resultMap.get(i).put("checked","true");
											break;
										}
									}

								}
							}
						}
					}
					model.addObject("ruleList", resultMap);
					// 定时任务
					queryTrigger(id, model);
				}
			}
			model.setViewName("km/crawler/extract/editExtractTask");
			
		// 创建转换任务	
		}else{
			// 修改备注（yongqian.liu）：增加查询知识源列表集合
			List<KnowledgeSource> sourceList = knowledgeSourceBiz.findAll();
			model.addObject("sourceList", sourceList);
			
			/* 修改备注（yongqian.liu）：删除 sourceId 属性
			map.put("sourceId", sourceId);
			model.addObject("sourceId", sourceId);
			map.put("registermodel", registermodel);
			// 获取采集任务列表（需要转换的任务）
			List<TaskJob> taskJobList = taskJobBiz
					.dependingOnTheTypeOfIdAndQueryTasks(map);
			if (taskJobList.size() > 0) {
				model.addObject("taskJobList", taskJobList);
			// 获取km字段规则
			List<Map> list = queryRuleTaskRulesAndParsing(propertyId);
			model.addObject("listProperties", list);
			}*/
			
			model.setViewName("km/crawler/extract/saveExtractTask");
		}
		return model;
	}
	
	/**
	 * 功能:根据知识源 ID 获取采集任务列表
	 * <p>
	 * 作者 刘永谦 2016-4-11
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getJobListBysouceId(HttpServletRequest request,
			HttpServletResponse response) {
		
		String sourceId = getStringValue(request, "sourceId");
		String registermodel = getStringValue(request, "registermodel");
		
		Map map = new HashMap();
		map.put("sourceId", sourceId); // 知识源 ID
		map.put("registermodel", registermodel); // 模块类型
		
		// 获取采集任务列表（需要转换的任务）
		List<TaskJob> taskJobList = taskJobBiz.dependingOnTheTypeOfIdAndQueryTasks(map);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("taskJobList", taskJobList);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能 获取数据库tables列表
	 * <p>
	 * 作者 王晓鸣 2015-1-4
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTablesAndViews(HttpServletRequest request,
			HttpServletResponse response) {
		String url = getStringValue(request, "url");
		String type = getStringValue(request, "type");
		String user = getStringValue(request, "user");
		String password = getStringValue(request, "password");
		JdbcEnity jdbcEnity = new JdbcEnity();
		jdbcEnity.setUrl(url);
		jdbcEnity.setType(type);
		jdbcEnity.setUser(user);
		jdbcEnity.setPassword(password);
		jdbcEnity.setDriver("oracle.jdbc.driver.OracleDriver");
		List<String> tables = taskJobBiz.getTables(jdbcEnity, type);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("tables", tables);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:获取该转换任务下的存储系统
	 * <p>
	 * 作者 王晓鸣 2016-3-14
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView validateforTansfer(HttpServletRequest request,
			HttpServletResponse response) {
		String connectId = getStringValue(request, "connectId");
		String systemId = getStringValue(request, "systemId");
		String editOrsave = getStringValue(request, "editOrsave");
		String transferState = null;
		Map<String, Object> result = new HashMap<String, Object>();
		if ("0".equals(editOrsave)) {
			transferState = "1";
		} else {
			Condition condition = Condition
					.parseCondition("connectid_string_eq");
			condition.setValue(connectId);
			List<TaskJob> taskJoblist = taskJobBiz.find(condition);

			for (TaskJob taskJob : taskJoblist) {
				if (systemId.equals(taskJob.getSystemId())) {
					transferState = "0";
					break;
				}
			}
		}
		result.put("transferState", transferState);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:DB 创建规则获得字段集合
	 * <p>
	 * 作者 王晓鸣 2015-1-4
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getFields(HttpServletRequest request,
			HttpServletResponse response) {
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		Dom4jXmlUtil dm = new Dom4jXmlUtil();
		String entityname = getStringValue(request, "entityname");
		String sourceId = getStringValue(request, "sourceId");
		KnowledgeSource source = knowledgeSourceBiz.findById(sourceId);
		Map<String, String> map = dm.generateMap(source.getLinkContent());
		String DBurl = null;
		String DBType = map.get("type");
		String url = map.get("url");
		String port = map.get("port");
		String name = map.get("name");
		String type = "JdbcDataSource";
		if (DBType.equals("oracle")) {
			DBurl = "jdbc:oracle:thin:@" + url + ":" + port + ":" + name;
		} else if (DBType.equals("mysql")) {
			DBurl = "jdbc:mysql://" + url + ":" + port + "/" + name;
		} else if (DBType.equals("sqlserver")) {
			DBurl = "jdbc:sqlserver://" + url + ":" + port + ";DatabaseName="
					+ name;
		}
		JdbcEnity jdbcEnity = new JdbcEnity();
		jdbcEnity.setUrl(DBurl);
		jdbcEnity.setType(type);
		jdbcEnity.setUser(source.getUsername());
		jdbcEnity.setPassword(md5Utils.Decryption(source.getPassword()));
		jdbcEnity.setEntityName(entityname);
		jdbcEnity.setDriver(map.get("driver"));
		// 获取字段集合
		List<JdbcField> fields = taskJobBiz.getFields(jdbcEnity);
		// 获取km字段规则
		/*String properties = externalRestBiz.getModelProperties();
		properties = exchangeJsonPros(properties);
		result.put("properties", properties);*/
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("fields", fields);
		return this.putToModelAndViewJson(result);

	}
	/**
	 * 转换全局属性字段存储
	 * @param properties
	 * @return
	 */
	private String exchangeJsonPros(String properties) {
		// PROP_ID=13641 PROP_NAME=知识文本 TEPT_ID=1 TEPT_NAME=知识采集模板
		JSONArray exchangedArray = new JSONArray();
		JSONObject exchangeObj = null;
		JSONArray json = JSONArray.fromObject(properties); // 首先把字符串转成 JSONArray  对象
		String teptName = "";
		for(int i=0;i<json.size();i++){
			JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
			exchangeObj = new JSONObject();
			exchangeObj.put("PROP_ID", job.get("PROP_ID")+":"+job.get("TEPT_ID"));
			teptName = job.get("TEPT_NAME") == null ? "" : job.get("TEPT_NAME").toString();
			exchangeObj.put("PROP_NAME", job.get("PROP_NAME")+"("+teptName+")");
			exchangedArray.add(exchangeObj);
		}
		return exchangedArray.toString();
	}

	/**
	 * 功能:保存或修改 web采集任务
	 * <p>
	 * 作者 井晓丹 2015-12-30
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	public ModelAndView saveWebTaskJob(HttpServletRequest request,
			HttpServletResponse response, JobForm jobForm) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String sourceId = getStringValue(request, "sourceId");
		String sourceName = getStringValue(request, "sourceName");
		String operateStr = null;
		TaskJob taskJob = new TaskJob();
		// 是否需要登录
		AttachProperty attachProperty = null;
		// 规则集合
		List<Rule> rules = new ArrayList<Rule>();
		// 定时规则
		TaskTrigger taskTrigger = null;
		boolean updFlg = false;
		String id = jobForm.getId();
		if (id != null && !id.equals("")) {
			taskJob = this.taskJobBiz.findById(id);
			updFlg = true;
		} else {
			taskJob = new TaskJob();
		}
		boolean updName = false;
		if (updFlg && !jobForm.getName().equals(taskJob.getName())) {
			// 需要修改转换和传输任务的名称
			updName = true;
		}
		taskJob.setName(jobForm.getName());
		if (jobForm.getThreadNum() >= 0) {
			if (attachProperty == null)
				attachProperty = new AttachProperty();
			attachProperty.setThreadNum(jobForm.getThreadNum());
		}
		if (jobForm.getChartset() != null) {
			if (attachProperty == null)
				attachProperty = new AttachProperty();
			attachProperty.setCharSet(jobForm.getChartset());
		}
		// 代理服务器设置
		String[] ipAddress = getArrayValue(request, "ipAddress");
		String[] ipPort = getArrayValue(request, "ipPort");
		String ipAddressPort = "";
		if (ipAddress != null && ipAddress.length > 0) {
			for (int i = 0; i < ipAddress.length; i++) {
				if (ipAddress[i].equals(""))
					continue;
				ipAddressPort += ipAddress[i] + ":" + ipPort[i] + ";";
			}
			if (!ipAddressPort.equals("")) {
				ipAddressPort = ipAddressPort.substring(0,
						ipAddressPort.length() - 1);
				attachProperty.setIpAddressPort(ipAddressPort);
			}
		}

		// 种子地址
		Rule seedUrl = new Rule();
		seedUrl.setName("seedUrl");
		seedUrl.setOptFlag(Rule.SEED_URL);
		seedUrl.setRule(jobForm.getSeed_url());
		seedUrl.setType("url");
		rules.add(seedUrl);

		// 列表页正则 规则
		Rule listUrl = new Rule();
		listUrl.setName("list");
		listUrl.setOptFlag(Rule.LIST_URL_OPT);
		listUrl.setRule(jobForm.getList_url());
		listUrl.setType(jobForm.getList_rule_type());
		rules.add(listUrl);
		// 详情页正则 规则
		Rule detailUrl = new Rule();
		detailUrl.setName("detail");
		detailUrl.setOptFlag(Rule.DETAIL_URL_OPT);
		detailUrl.setRule(jobForm.getDetail_url());
		detailUrl.setType(jobForm.getDetail_rule_type());
		rules.add(detailUrl);

		// 详情页多页正则 规则
		if (jobForm.getDetail_more_url() != null
				&& !jobForm.getDetail_more_url().equals("")) {
			Rule detaiMorelUrl = new Rule();
			detaiMorelUrl.setName("detailMore");
			detaiMorelUrl.setOptFlag(Rule.DETAIL_MORE_URL_OPT);
			detaiMorelUrl.setRule(jobForm.getDetail_more_url());
			detaiMorelUrl.setType(jobForm.getDetail_more_rule_type());
			rules.add(detaiMorelUrl);
		}
		// 列表页区域规则
		Rule listContent = null;
		if (jobForm.getList_content_Val() != null
				&& !jobForm.getList_content_Val().equals("")) {
			listContent = new Rule();
			listContent.setName(jobForm.getList_content());
			listContent.setOptFlag(Rule.LIST_CONTENT_OPT);
			listContent.setRule(jobForm.getList_content_Val());
			listContent.setType(jobForm.getList_content_type());
			rules.add(listContent);
		}
		List<Map<String, Object>> mapListJson = null;
		
		/*if (!updFlg) {
			// 知识模板
			String knowledgeModel = getStringValue(request, "knowledgeModel");
			// 转换规则
			String modelPros = externalRestBiz
					.getModelProperties(knowledgeModel);
			JSONArray jsonArray = JSONArray.fromObject(modelPros);
			mapListJson = (List) jsonArray;
		}*/
		// 内容提取 规则
		String[] detailContents = getArrayValue(request, "detail_content");
		String[] detailContentVals = getArrayValue(request,
				"detail_content_Val");
		String[] detailContentTypes = getArrayValue(request,
				"detail_content_type");
		Rule detailContent = null;
//		List list = new ArrayList();
		for (int i = 0; i < detailContents.length; i++) {
			detailContent = new Rule();
			detailContent.setName(URLDecoder.decode(detailContents[i],"utf8"));
			detailContent.setOptFlag(Rule.DETAIL_CONTENT_OPT);
			detailContent.setRule(detailContentVals[i].trim().equals("null") ? "" : URLDecoder.decode(detailContentVals[i],"utf8"));
			detailContent.setType(detailContentTypes[i]);
			rules.add(detailContent);
			/*if (!updFlg) {
				if(detailContents[i].indexOf("INHERENT")!=-1){
					list.add(detailContents[i]);
					continue;
				}
				for (int j = 0; j < mapListJson.size(); j++) {
					boolean ifBreak = false;
					Map<String, Object> obj = mapListJson.get(j);
					for (Entry<String, Object> entry : obj.entrySet()) {
						String strkey = entry.getKey();
						if (!strkey.equals("PROP_ID"))
							continue;
						if (entry.getValue().equals(detailContents[i].split(":")[0])) {
							list.add(detailContents[i]);
							ifBreak = true;
							break;
						}
					}
					if (ifBreak)
						break;
				}
			}*/
		}

		// 附件规则
		String downloadAttachment = getStringValue(request, "downloadAttachment");
		if ("on".equals(downloadAttachment)) { //下载附件
			Rule attachmentContent = null;
			if (jobForm.getAttachment_content_Val() != null
					&& !jobForm.getAttachment_content_Val().equals("")) {
				attachmentContent = new Rule();
				attachmentContent.setName(jobForm.getAttachment_content());
				attachmentContent.setOptFlag(Rule.ATTACHMENT_URL_OPT);
				attachmentContent.setRule(jobForm.getAttachment_content_Val());
				attachmentContent.setType(jobForm.getAttachment_content_type());
				rules.add(attachmentContent);
			}
		}
		// 附件抽取规则
		String extractList = getStringValue(request, "extractList");
		if(extractList!=null&&extractList!=""){
			taskJob.setExtractList(extractList);
		}
		// 任务定时
		String triggerchecked = getStringValue(request, "triggerchecked");
		if ("0".equals(triggerchecked)) {
			taskTrigger = getTrigger(request, response);
			taskTrigger.setJobName(taskJob.getName());
			taskJob.setAutomatic(0);
		} else {
			taskJob.setAutomatic(1);
		}

		if (id == null || id.equals("")) {
			// String taskId = IDGenerate.uuid();
			// taskJob.setId(taskId);
			operateStr = "添加";
			taskJob.setSourceId(sourceId);
			taskJob.setSourceName(sourceName);
			taskJob.setRegisterModel(JobModel.MODEL_CRAWLER);
			taskJob.setJobState(JobModel.STATE_READY);
			taskJob.setRegisterType(JobModel.TYPE_WEB);
			taskJob.setPriority(0);
			/*// 知识形态
			String knowledgeType = getStringValue(request, "knowledgeType");
			String knowledgeTypeName = getStringValue(request, "knowledgeTypeName");
			// 知识模板
			String knowledgeModel = getStringValue(request, "knowledgeModel");
			String knowledgeModelName = getStringValue(request,
					"knowledgeModelName");
			// 存储系统
			String system = getStringValue(request, "system");
			String systemName = getStringValue(request, "systemName");
			taskJob.setKnowledgeType(knowledgeType);
			taskJob.setKnowledgeTypeName(knowledgeTypeName);
			taskJob.setKnowledgeModel(knowledgeModel);
			taskJob.setKnowledgeModelName(knowledgeModelName);
			taskJob.setSystemId(system);
			taskJob.setSystemName(systemName);*/
		} else {
			TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(id);
			if (taskTrigger != null && trigger != null) {
				taskTrigger.setId(trigger.getId());
			}
			operateStr = "修改";
		}
		try {
			// 新增
			String taskId = taskJobBiz.addWebJob(taskJob, rules,
					attachProperty, taskTrigger);
			taskJob.setId(taskId);
			if (id == null || id.equals("")) {
				// 生成转换任务和传输任务
//				saveExtractAndTransferJob(request, sourceId, taskJob, null);
			} else {
				// 需要修改转换和传输的任务名称
				if (updName) {
					// select * from taskjob t start with id = '' connect by
					// prior t.id = t.connectid ;
					List<TaskJob> listJob = taskJobBiz
							.findAllTaskByCrawlerJobId(taskId);
					for (int i = 0; i < listJob.size(); i++) {
						listJob.get(i).setName(jobForm.getName());
					}
					taskJobBiz.updateBatch(listJob);
				}
			}
			// 是否开始任务
			String startTask = getStringValue(request, "startTask");
			if (startTask.equals("1")) {
				// 开始任务
				// taskJob.setJobState(JobModel.STATE_STRAT);
				quartzScheduleBiz.startJob(taskJob);
			}
			result.put("code", "ok");
			operateStr += "成功！";
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveTransferTaskJob save exception", e);
		}
		result.put("id", taskJob.getId());
		result.put("msg", operateStr);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能：创建采集任务的时候生成转换任务和传输任务 作者：井晓丹 2016-01-29
	 * 
	 * @param request
	 * @param sourceId
	 * @param taskJob
	 */
	private void saveExtractAndTransferJob(HttpServletRequest request,
			String sourceId, TaskJob taskJob, List list) {
		/*// 知识形态
		String knowledgeType = getStringValue(request, "knowledgeType");
		String knowledgeTypeName = getStringValue(request, "knowledgeTypeName");
		// 知识模板
		String knowledgeModel = getStringValue(request, "knowledgeModel");
		String knowledgeModelName = getStringValue(request,
				"knowledgeModelName");*/
		// 存储系统
		String system = getStringValue(request, "system");
		String systemName = getStringValue(request, "systemName");
		/*// 如果选择了知识形态，要生成转换任务
		if (knowledgeType != null) {
			TaskJob extractJob = new TaskJob();
			extractJob.setId(IDGenerate.uuid());
			extractJob.setSourceId(sourceId);
			extractJob.setSourceName(taskJob.getSourceName());
			extractJob.setName(taskJob.getName());
			// 设置转换任务关联采集任务
			extractJob.setConnectId(taskJob.getId());
			extractJob.setRegisterModel(JobModel.MODEL_EXTRACT);
			extractJob.setRegisterType(JobModel.TYPE_EXTRACT);
			extractJob.setJobState(JobModel.STATE_READY);
			extractJob.setKnowledgeType(knowledgeType);
			extractJob.setKnowledgeTypeName(knowledgeTypeName);
			extractJob.setKnowledgeModel(knowledgeModel);
			extractJob.setKnowledgeModelName(knowledgeModelName);
			// 附件抽取规则
			String extractList = taskJob.getExtractList();
			if(extractList!=null&&extractList!=""){
				extractJob.setExtractList(extractList);
			}
			taskJobBiz.add(extractJob);
			if (list != null) {
				String extractXml = Dom4jXmlUtil.extractXMLMapping(list);
				TaskRule taskRule = new TaskRule();
				taskRule.setJobId(extractJob.getId());
				taskRule.setRuleType(extractJob.getRegisterType());
				taskRule.setRuleContent(extractXml.getBytes());
				taskRuleBiz.save(taskRule);
			}
			
		}*/
		// 如果选择了存储系统，要生成传输任务
		if (system != null) {
			TaskJob transferJob = new TaskJob();
			transferJob.setName(taskJob.getName());
			transferJob.setSourceId(sourceId);
			transferJob.setSourceName(taskJob.getSourceName());
			// 设置转换任务关联采集任务
			transferJob.setConnectId(taskJob.getId());
			transferJob.setRegisterModel(JobModel.MODEL_TRANSFER);
			transferJob.setRegisterType(JobModel.TYPE_TRANSFER);
			transferJob.setJobState(JobModel.STATE_READY);
			transferJob.setSystemId(system);
			transferJob.setSystemName(systemName);
			/*transferJob.setKnowledgeType(knowledgeType);
			transferJob.setKnowledgeTypeName(knowledgeTypeName);
			transferJob.setKnowledgeModel(knowledgeModel);
			transferJob.setKnowledgeModelName(knowledgeModelName);*/
			taskJobBiz.save(transferJob);
		}
	}

	/**
	 * 功能：正序或倒序查询任务列表
	 * 
	 * @param pagination
	 * @param map
	 * @return List<TaskJob> author duanzheng
	 */
	public ModelAndView positiveSequenceOrReverseSwitchTasks(
			HttpServletRequest request, HttpServletResponse response) {
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		List<TaskJob> listJob = null;
		try {
			Map map = new HashMap();
			map.put("type", getStringValue(request, "type"));
			if (getStringValue(request, "id").equals("0")) {
				map.put("id", null);
			} else {
				map.put("id", getStringValue(request, "id"));
			}
			map.put("registermodel", getStringValue(request, "registermodel"));
			listJob = taskJobBiz.positiveSequenceOrReverseSwitchTasks(
					parameters.getPagination(), map);
		} catch (Exception e) {
			throw e;
		}
		return this.putToModelAndViewJson(listJob, parameters, str);
	}

	/**
	 * 功能：模糊查询,状态筛选，数据源筛选任务列表
	 * 
	 * @param pagination
	 * @param map
	 * @return List<TaskJob> author duanzheng
	 */
	public ModelAndView fuzzyQueryTaskList(HttpServletRequest request,
			HttpServletResponse response) {
		ListPageQuery parameters = new ListPageQuery();
		List<TaskJob> tjs = null;
		try {
			ModelAndView h = new ModelAndView();
			String type = getStringValue(request, "registerType");// 任务类型（供转换和传输使用）
			String PriorityToCollectVal = getStringValue(request,
					"PriorityToCollectVal");// 优先级筛选
			String collectStateVal = getStringValue(request, "collectStateVal"); // 状态筛选
			String knowledgeSourceVal = getStringValue(request,
					"knowledgeSourceVal");// 知识源筛选
			//String collectTasknameVal=getStringValue(request, "collectTasknameVal");//任务名称筛选
			String  Namesearch=getStringValue(request, "Namesearch");
			String  collectRegisterVal=getStringValue(request, "collectRegisterVal");
			String collectWayVal = getStringValue(request, "collectWayVal"); // 方式筛选
			String collectDateVal=getStringValue(request, "collectDateVal");
			String strtTime=getStringValue(request, "startTime");
			String stopTime=getStringValue(request, "stopTime")+" 23:59:59";
			if (collectStateVal == null || knowledgeSourceVal == null
					|| PriorityToCollectVal == null || collectWayVal == null) {

			}
			if (collectStateVal != null) {
				if (collectStateVal.equals("-1")) {
					collectStateVal = null;
				}
			}
			if (collectRegisterVal != null) {
				if (collectRegisterVal.equals("-1")) {
					collectRegisterVal = null;
				}
			}
			if (knowledgeSourceVal != null) {
				if (knowledgeSourceVal.equals("-1")) {
					knowledgeSourceVal = null;
				}
			}
			if (PriorityToCollectVal != null) {
				if (PriorityToCollectVal.equals("-1")) {
					PriorityToCollectVal = null;
				}
			}
			if (collectWayVal != null) {
				if (collectWayVal.equals("-1")) {
					collectWayVal = null;
				}
			}

			String registermodel = getStringValue(request, "registermodel");// 模块（1：采集
																			// 2：转换
																			// 3：传输）
			String search = getStringValue(request, "search"); // 要搜索的值
			String sourceId = getStringValue(request, "id");// 知识源id
			if (registermodel == null && search == null && sourceId == null
					&& collectStateVal == null && knowledgeSourceVal == null
					&& collectWayVal == null) {
				return this.putToModelAndViewJson(tjs, parameters);
			}
			if (search == null) {
				search = "请输入知识源筛选任务……";
			}
			search = search.trim();
			if (search.equals("请输入知识源筛选任务……") || search.equals("")
					|| search.equals(null) || search.equals("请输入知识源筛选任务……")
					|| search.equals("请输入知识源筛选任务……")) {
				search = null;
			}

			if (sourceId != null) {
				if (sourceId.equals("0")) {
					sourceId = null;
				}
			}
			Map map = new HashMap();
			map.put("sourcename", search);
			map.put("name", Namesearch);
			map.put("sourceId", sourceId);
			map.put("registermodel", registermodel);
			map.put("priority", PriorityToCollectVal);
			map.put("jobState", collectStateVal);
			map.put("registerType", knowledgeSourceVal);
			map.put("AUTOMATIC", collectWayVal);
			map.put("startTime", strtTime);
			map.put("stopTime", stopTime);
			map.put("registertype", collectRegisterVal);
			new ServletRequestDataBinder(parameters).bind(request);
			if(collectDateVal!=null){
				tjs=taskJobBiz.queryTaskjobByCreatedate(parameters.getPagination(), map);
			}else{
				tjs = taskJobBiz.specialFilterTransformationAndTransportTask(
						parameters.getPagination(), map, type);
			}
			
			
		} catch (Exception e) {
			throw e;
		}
		return this.putToModelAndViewJson(tjs, parameters, str);

	}

	/**
	 * 功能:验证知识源链接状态
	 * <p>
	 * 作者 王晓鸣 2016-1-27
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView knowledgeLinkstate(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		String sourceId = getStringValue(request, "sourceId");
		String state = knowledgeSourceBiz.findById(sourceId).getLinkState();
		result.put("state", state);
		return this.putToModelAndViewJson(result);
	}
	
	/**
	 * 功能：根据批量id删除任务（单条删除或多条删除）
	 * 作者： 段政
	 * @param id
	 *            return ModelAndView author duanzheng
	 */
	public ModelAndView deleteTaskJob(HttpServletRequest request,
			HttpServletResponse response) {
		Map map = new HashMap();
		try {
			String[] array = request.getParameterValues("listId"); // 获取所有的
			String type = getStringValue(request, "type");// 获取模块类型
			List list = new ArrayList();
			Map mapParams = null;
			if (array != null) {
				for (int i = 0,n=array.length; i <n; i++) {
					String id = array[i];
					String jobState = null;
					if (!(id.equals(""))) {
						mapParams = new HashMap();
						mapParams.put("id", id);
						jobState = taskJobBiz.getStateByJobId(mapParams);
						if (jobState == null) {

						} else {
							if (jobState.equals("1")) {
								TaskJob job = taskJobBiz.findById(id);
								map.put("msg", "任务正在进行中不能删除！");
								if (job != null) {
									map.put("msg", job.getName()
											+ "任务正在进行中不能删除！");
								}
								return this.putToModelAndViewJson(map);
							} else if (jobState.equals("4")) {
								TaskJob job = taskJobBiz.findById(id);
								map.put("msg", "任务正在等待中不能删除！");
								if (job != null) {
									map.put("msg", job.getName()
											+ "任务正在等待中不能删除！");
								}
								return this.putToModelAndViewJson(map);
							} else if (jobState.equals("0")) {
								mapParams = new HashMap();
								mapParams.put("connectid", id);
								mapParams.put("registerModel", type);
								list.addAll(this
										.queryDeleteJobRelationId(mapParams));
							} else {
								mapParams = new HashMap();
								mapParams.put("connectid", id);
								List<TaskJob> job = taskJobBiz
										.accordingToTheConditionsQueryJobCollection(mapParams);
								if (job.size() > 0) {
									String name = job.get(0).getName();
									map.put("msg", name + "在任务中被关联不能删除！");
									break;
								}
								list.add(id);
							}
						}
					}
				}
			}
			if (list.size() > 0) {
				// 删除任务监控报错信息
				monitorErrorBiz.delTaskMonitorErrorsByIds(list);
				// 删除任务监控信息
				taskJobMonitorBiz.delTASKJOBMONITORByIds(list);
				// 删除定时任务
				taskTriggerBiz.bulkDeletionByJobId(list);
				if (type.equals("1")) {// 采集
					// 删除附件属性
					attachmentCrawlerBiz.deleteATTACHMENTCRAWLERByList(list); // 采集任务
					// 删除采集数据
					datumBiz.delDatumByids(list); // 采集任务
					// 删除附件属性
					attachPropertyBiz.delAttachPropertyByIds(list); // 采集任务
					// 删除任务规则
					taskRuleBiz.delTaskRuleByIds(list); // 采集 转换任务
					// 删除规则
					ruleBiz.deleteTaskRuleByIds(list); // 采集任务
				} else if (type.equals("2")) {// 转换
					// 删除任务规则
					taskRuleBiz.delTaskRuleByIds(list); // 采集 转换任务
					// 删除转换数据
					knowledgeMetadataBiz.deleteConvertDataByList(list); // 转换任务
				} else if (type.equals("3")) {// 传输
					transferConfigBiz.delConfigByIds(list);
				}
				// 删除任务
				taskJobBiz.deleteByIds(list);
				map.put("msg", "删除成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "删除失败！");
		}
		return this.putToModelAndViewJson(map);
	}

	/**
	 * 功能：查询要删除的任务关系id author：段政
	 * 
	 * @param Map
	 * @return List
	 * 
	 */
	public List queryDeleteJobRelationId(Map map) {
		List listArr=null;
		List<TaskJob> list = null;
		try {
			Map mapParams = null;
			String id = "" + map.get("connectid");
			String type = "" + map.get("registerModel");
			if (id != null && id != "") {
				list = taskJobBiz.findAllTaskByCrawlerJobId(id);// 递归查询
				if(list.size()>0){
					listArr=new ArrayList();
					for (int j = 0; j < list.size(); j++) {
						listArr.add(list.get(j).getId());
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return listArr;
	}

	/**
	 * 功能:跳转到采集任务编辑页面
	 * <p>
	 * 作者 王晓鸣 2016-1-28
	 * 
	 * @param request
	 * @param response
	 */
	public ModelAndView toTaskjobEditPage(HttpServletRequest request,
			HttpServletResponse response) {
		String id = getStringValue(request, "id");
		Dom4jXmlUtil dm = new Dom4jXmlUtil();
		ModelAndView model = new ModelAndView();
		TaskJob taskJob = taskJobBiz.findById(id);
		String sourceId = taskJob.getSourceId();
		//抽取规则
		String extractList=taskJob.getExtractList();
		if(extractList!=null&&extractList!=""){
			List exList=Arrays.asList(extractList.split(","));
			model.addObject("extractList",exList);
		}
		model.addObject("sourceName",taskJob.getSourceName());
		model.addObject("name", taskJob.getName());
		model.addObject("sourceId", sourceId);
		model.addObject("id", id);
		model.addObject("jobstate", taskJob.getJobState());
		model.addObject("knowledgeModel", taskJob.getKnowledgeModel());
		model.addObject("knowledgeModelName",
				taskJob.getKnowledgeModelName());
		model.addObject("knowledgeType", taskJob.getKnowledgeType());
		model.addObject("knowledgeTypeName", taskJob.getKnowledgeTypeName());
		model.addObject("systemId", taskJob.getSystemId());
		model.addObject("systemName", taskJob.getSystemName());
		
		// KnowledgeSource source = knowledgeSourceBiz.findById(sourceId);
		// 获取km字段规则
		
		/*// 获取知识固有属性
		String inherentProp = extenalRestBiz.getInherentProp();
		model.addObject("inherentProp", JSONArray.fromObject(inherentProp));
		//获取模板扩展属性
		String properties_model = externalRestBiz.getModelProperties(taskJob.getKnowledgeModel());
		model.addObject("properties_model", JSONArray.fromObject(properties_model));
		String properties = externalRestBiz.getModelProperties();
		properties = exchangeJsonPros(properties);*/
		String registerType = taskJob.getRegisterType();
		queryTrigger(id, model);
		if ("1".equals(registerType)) {
//			model.addObject("properties", JSONArray.fromObject(properties));
			Map<String, String> map = new HashMap<String, String>();
			map.put("taskId", id);
			Map<String, Map<String, List<Map<String, Object>>>> ruleMap = ruleBiz
					.getTaskRules(map);
			// 起始网址
			String seedUrl = "";
			List<Map<String, Object>> seedList = ruleMap.get(Rule.SEED_URL)
					.get(Rule.RULE_TYPE_URL);
			if (seedList != null && seedList.size() > 0) {
				Map<String, Object> seed = seedList.get(0);
				seedUrl = seed.get("rule").toString().replace("\"", "&quot;");
				model.addObject("seedUrl", seedUrl);
			}
			// 列表网址
			String listUrl = "";
			Map<String, List<Map<String, Object>>> listurls = ruleMap
					.get(Rule.LIST_URL_OPT);
			listUrl = listurls != null ? listurls.get(Rule.RULE_TYPE_REGEX)
					.get(0).get("rule").toString().replace("\"", "&quot;") : "";
			model.addObject("listUrl", listUrl);
			// 列表区域
			String listContent = "";
			Map<String, List<Map<String, Object>>> listcontents = ruleMap
					.get(Rule.LIST_CONTENT_OPT);
			if (listcontents != null) {
				listContent = listcontents.get(Rule.RULE_TYPE_XPATH).get(0)
						.get("rule") != null ? listcontents
						.get(Rule.RULE_TYPE_XPATH).get(0).get("rule")
						.toString().replace("\"", "&quot;") : "";
			}
			model.addObject("listContent", listContent);
			// 详情网址
			String detailUrl = "";
			Map<String, List<Map<String, Object>>> detailurls = ruleMap
					.get(Rule.DETAIL_URL_OPT);
			detailUrl = detailurls != null ? detailurls
					.get(Rule.RULE_TYPE_REGEX).get(0).get("rule").toString()
					.replace("\"", "&quot;") : "";
			model.addObject("detailUrl", detailUrl);
			// 详情多页
			String detailMoreUrl = "";
			Map<String, List<Map<String, Object>>> detailmoreurls = ruleMap
					.get(Rule.DETAIL_MORE_URL_OPT);
			if (detailmoreurls != null) {
				detailMoreUrl = detailmoreurls.get(Rule.RULE_TYPE_REGEX).get(0)
						.get("rule") != null ? detailmoreurls
						.get(Rule.RULE_TYPE_REGEX).get(0).get("rule")
						.toString().replace("\"", "&quot;") : "";
			}
			model.addObject("detailMoreUrl", detailMoreUrl);
			// 采集内容
			Map<String, List<Map<String, Object>>> detailcontents = ruleMap
					.get(Rule.DETAIL_CONTENT_OPT);
			String[] detail_content = new String[detailcontents.size()]; // WEB采集
																			// 内容项
																			// 名称
			String[] detail_content_Val = new String[detailcontents.size()];// WEB采集
																			// 内容项
																			// 取值规则
			String[] detail_content_type = new String[detailcontents.size()];// WEB采集
																				// 内容项
																				// 规则类型
			List<Rule> detailRuleList = new ArrayList<Rule>();
			Rule rule = null;
			if (detailcontents != null && detailcontents.size() > 0) {
				for (List<Map<String, Object>> lc : detailcontents.values()) {
					for (Map<String, Object> mapDetail : lc) {
						rule = new Rule();
						rule.setName(mapDetail.get("name").toString());
						rule.setType(mapDetail.get("type").toString());
						rule.setRule(mapDetail.get("rule")!=null ? mapDetail.get("rule").toString().replace("\"", "&quot;") : "");
						detailRuleList.add(rule);
					}
				}
			}
			model.addObject("ruleArrayLength", detailRuleList.size() + 1);
			model.addObject("detailRuleList", detailRuleList);
			// 附件规则
			String attachmentUrl = "";
			Map<String, List<Map<String, Object>>> attclurls = ruleMap
					.get(Rule.ATTACHMENT_URL_OPT);
			if (attclurls != null) {
				attachmentUrl = attclurls.get(Rule.RULE_TYPE_XPATH).get(0)
						.get("rule") != null ? attclurls
						.get(Rule.RULE_TYPE_XPATH).get(0).get("rule")
						.toString().replace("\"", "&quot;") : "";
			}
			model.addObject("attachmentUrl", attachmentUrl);

			AttachProperty attachProperty = this.attachPropertyBiz
					.getAttachPropertyByTaskId(map);
			model.addObject("attachProperty", attachProperty);
			List<String[]> ipAddressPortList = new ArrayList<String[]>();
			if (attachProperty.getIpAddressPort() != null) {
				String[] ipAddressPorts = attachProperty.getIpAddressPort()
						.split(";");
				for (int i = 0; i < ipAddressPorts.length; i++) {
					String[] ipAddressPort = ipAddressPorts[i].split(":");
					ipAddressPortList.add(ipAddressPort);
				}
				model.addObject("ipAddressPortList", ipAddressPortList);
			} else {
				model.addObject("ipAddressPortList", "null");
			}

			model.setViewName("km/crawler/task/editWebTask");
		} else if ("2".equals(registerType)) {
			JdbcConnectionUtil jdbcConnectionUtil = new JdbcConnectionUtil();
			TaskRule taskRule = taskRuleBiz.queryTaskRuleById(id);
			String ruleXml = new String(taskRule.getRuleContent());
			// 将xml文件转换为map集合
			Map<String, String> map = dm.generateMapForDB(ruleXml);
			String entityname = map.get("entityname");
			String selectJson = map.get("json");
			// 获取jdbcEtity对象
			JdbcEnity jdbc = knowledgeSourceBiz
					.getJdbcEnityBysourceId(sourceId);
			jdbc.setEntityName(entityname);
			List<JdbcField> list = jdbcConnectionUtil.getFields(jdbc);

			// json字符串转换list/map集合
			/*JSONArray jsonArray = JSONArray.fromObject(properties);
			List<Map<String, Object>> mapListJson = (List) jsonArray;*/
			JSONArray jsonArray1 = JSONArray.fromObject(selectJson);
			List<Map<String, Object>> mapListJson1 = (List) jsonArray1;
			model.addObject("entityname", entityname);
			model.addObject("list", list);
//			model.addObject("properties", mapListJson);
			model.addObject("selectlist", mapListJson1);
//			model.addObject("typeList", properties);
			model.addObject("filekey", map.get("filekey"));
			model.addObject("filevalue", map.get("filevalue"));
			model.addObject("filename", map.get("filename"));
			model.addObject("filetype", map.get("filetype"));
			
			model.setViewName("km/crawler/task/editDBTask");
		} else if ("3".equals(registerType)) {
			model.setViewName("km/crawler/task/editLocalTask");
		}
		return model;

	}

	/**
	 * 功能:根据知识形态获取模板列表
	 * <p>
	 * 作者 井晓丹 2015-1-28
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getModelsByTypeId(HttpServletRequest request,
			HttpServletResponse response) {
		String typeId = getStringValue(request, "typeId");
		String id = getStringValue(request, "id");
		String operationType = getStringValue(request, "operationType");
		if (operationType == null) {
			operationType = "add";
		}
		String models = null;
		if (typeId == null) {

		} else {
			// 调用webService接口获取知识形态下模板集合
//			models = externalRestBiz.getModelListByTypeId(typeId);
		}
		// 模板id
		String propertyId = getStringValue(request, "propertyId");
		// 获取模板属性
		List<Map> list = queryRuleTaskRulesAndParsing(typeId);
		List resultParamList = new ArrayList();
		List<Map> resultMap = new ArrayList<Map>();
		String registerType = null;
		if (id != null && id != "" && (!id.equals("0")) && (!"0".equals(id))) {
			TaskJob tj = taskJobBiz.findById(id);
			if (operationType.equals("update")) {
				TaskJob tjt = taskJobBiz.findById(tj.getConnectId());
				id = tj.getConnectId();
				registerType = tjt.getRegisterType();
			} else {
				registerType = tj.getRegisterType();
			}
			if (registerType.equals("1")) { // 类型（1：web 2：DB 3：local）
				Map map = new HashMap();
				map.put("id", id);
				List<Rule> listRule = ruleBiz.queryTaskRuleById(map);
				if (listRule.size() > 0) {
					for (int i = 0,n=listRule.size(); i <n ; i++) {
						resultParamList.add(listRule.get(i).getName().trim());
					}
				}
			} else if (registerType.equals("2") || registerType.equals("4")) {// 数据库
																				// 转换
				TaskRule tr = taskRuleBiz.queryTaskRuleById(id);
				if (tr != null) {
					byte[] contentByte = tr.getRuleContent();
					String str = new String(contentByte);
					if (str != null) {
//						resultParamList = Dom4jXmlUtil.parse(str);
						resultParamList = Dom4jXmlUtil.getKeyFromXMl(str);
					}
				}
			}
		}
		if (resultParamList.size() > 0) {
			resultMap = getRule(resultParamList, null, null);
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("listProperties", list);
		result.put("models", models);
		if (resultMap.size() > 0) {
			for (int i = 0,n=resultMap.size(); i <n; i++) {// 从数据库中获取到的属性
				if(!StringUtils.isNumeric(resultMap.get(i).get("key").toString())){
					resultMap.get(i).put("hide", "false");
					continue;
				}
				for (int j = 0,c=list.size(); j <c; j++) {// 模板的属性
					String resultMapKey = "" + resultMap.get(i).get("key");
					String listKey = "" + list.get(j).get("PROP_ID");
					if (resultMapKey.equals(listKey)) {
						resultMap.get(i).put("hide", "false");
						break;
					} else {
						resultMap.get(i).put("hide", "true");
					}
				}
			}
		}
		result.put("resultMap", resultMap);
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能：根据key获取value
	 * 
	 * @param list
	 * @return
	 */
	public List<Map> getRule(List list, List<TaskRule> listTaskRule,
			List<Rule> listRule) {
		List<Map> resultList = new ArrayList<Map>();
		Map resultMap = null;
		// 获取json字符串进行匹配
		String properties = "";
//		String properties = externalRestBiz.getModelProperties();
		JSONArray jsa = JSONArray.fromObject(properties);
//		com.alibaba.fastjson.JSONArray jsa = (com.alibaba.fastjson.JSONArray) com.alibaba.fastjson.JSONArray
//				.parse(properties);
		if (list != null && list.size() > 0) {
			for (int j = 0,n=list.size(); j <n ; j++) {
				String param = list.get(j).toString();
				param = param.indexOf(":")!=-1 ? param.split(":")[0] : param;
				if (!(param.equals(null) && param.equals(""))) {
					
					int l = 0;
					for (int k = 0,c=jsa.size(); k <c ; k++) {
						if (l > 0) {
							break;
						}
						JSONObject job = jsa.getJSONObject(k);
						String val = job.get("PROP_ID").toString();
						if (val.equals(param)) {
							l++;
							String value = job.get("PROP_NAME").toString();
							resultMap = new HashMap();
							resultMap.put("key", val);
							resultMap.put("value", value);
							resultList.add(resultMap);
						}
					}
				}
			}
		} else if (listTaskRule != null && listTaskRule.size() > 0) {

			for (int i = 0,n=listTaskRule.size(); i <n ; i++) {
				byte[] byteArray = listTaskRule.get(i).getRuleContent();
				List<Map> listMap = parseXMLFormat(byteArray,jsa);
				if (listMap.size() > 0) {
					resultList.addAll(listMap);
				}
			}
		} else if (listRule != null && listRule.size() > 0) {
			for (int i = 0,n=listRule.size(); i <n ; i++) {
				String name = listRule.get(i).getName();
				name = name.indexOf(":")!=-1 ? name.split(":")[0] : name;
				if (name != null && name != "") {
					int l = 0;
					for (int k = 0,c=jsa.size(); k <c ; k++) {
						if (l > 0) {
							break;
						}
						JSONObject job = jsa.getJSONObject(k);
						String key = job.get("PROP_ID").toString();
						if (key.equals(name)) {
							l++;
							String value = job.get("PROP_NAME").toString();
							resultMap = new HashMap();
							resultMap.put("key", key);
							resultMap.put("value", value);
							resultList.add(resultMap);
						}
					}
				}
			}
		}
		return resultList;
	}

	/**
	 * 功能：解析xml格式 作者 段政 2016-3-16
	 * 
	 * @param byteArray
	 * @return
	 */
	public List<Map> parseXMLFormat(byte[] byteArray,JSONArray jsa) {
		List<Map> list = null;
		Map resultMap = null;
		if (byteArray.length > 0) {
			String str = new String(byteArray);
			// 解析xml格式字符串
//			List listkey = Dom4jXmlUtil.parse(str);
			List listkey = Dom4jXmlUtil.getKeyFromXMl(str);
			list = new ArrayList<Map>();
			for (int j = 0,n=listkey.size(); j <n ; j++) {
				String param = "" + listkey.get(j);
				param = param.indexOf(":")!=-1 ? param.split(":")[0] : param;
				if (!(param.equals(null) && param.equals(""))) {
					int l = 0;
					for (int k = 0,c=jsa.size(); k <c ; k++) {
						if (l > 0) {
							break;
						}
						JSONObject job = jsa.getJSONObject(k);
						String key = "" + job.get("PROP_ID");
						if (key.equals(param)) {
							l++;
							String value = "" + job.get("PROP_NAME");
							resultMap = new HashMap();
							resultMap.put("key", key);
							resultMap.put("value", value);
							list.add(resultMap);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * 功能:获取km字段
	 * <p>
	 * 作者 井晓丹 2016-2-2
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getproperties(HttpServletRequest request,
			HttpServletResponse response) {
		// 获取km字段规则
		/*String properties = externalRestBiz.getModelProperties();
		properties = exchangeJsonPros(properties);
		result.put("properties", properties);*/
		Map<String, Object> result = new HashMap<String, Object>();
		return this.putToModelAndViewJson(result);
	}

	/**
	 * 功能:获取任务定时信息
	 * <p>
	 * 作者 王晓鸣 2016-2-4
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public TaskTrigger getTrigger(HttpServletRequest request,
			HttpServletResponse response) {
		TaskTrigger taskTrigger = new TaskTrigger();
		String startTime = getStringValue(request, "startTime");
		String stopTime = getStringValue(request, "stopTime");
		// int triggerType=Integer.parseInt(getStringValue(request,
		// "triggerType"));
		Integer triggerType = Integer.valueOf(getStringValue(request,
				"triggerType"));
		// taskTrigger.setTriggerType(triggerType);
		String triggerWeek = getStringValue(request, "checkWeek");
		if (triggerWeek != null && triggerWeek != "") {
			String trriggerWeek_sub = triggerWeek.substring(0,
					triggerWeek.length() - 1);
			taskTrigger.setTriggerWeek(trriggerWeek_sub);
		}
		String monthradio = getStringValue(request, "monthradio");
		if (monthradio != null && monthradio != "") {
			String triggermonth = getStringValue(request, "triggermonth");
			taskTrigger.setTriggerMonth(triggermonth);
			if ("1".equals(monthradio)) {
				String triggerDay = getStringValue(request, "triggerday");
				taskTrigger.setTriggerDay(triggerDay);
			} else if ("2".equals(monthradio)) {
				String triggerweek = getStringValue(request, "triggerfew");
				String triggerweek2 = getStringValue(request, "triggerweek");
				taskTrigger.setTriggerFew(triggerweek);
				taskTrigger.setTriggerWeek(triggerweek2);
			}
		}
		taskTrigger.setTriggerType(triggerType);
		if (triggerType == 4) {
			taskTrigger.setTriggerDate(startTime);
		} else {
			taskTrigger.setStartTime(startTime);
		}
		taskTrigger.setStopTime(stopTime);
		return taskTrigger;

	}

	/**
	 * 功能:查询任务定时
	 * <p>
	 * 作者 王晓鸣 2016-2-21
	 * 
	 * @param id
	 */
	public void queryTrigger(String jobId, ModelAndView model) {
		TaskTrigger taskTrigger = taskTriggerBiz.queryTriggerByJobId(jobId);
		if (taskTrigger != null) {
			model.addObject("checkbox_trigger", "1");
			Integer triggerType = taskTrigger.getTriggerType();
			model.addObject("triggerType", triggerType);
			if (triggerType != null) {
				if (triggerType == 4) {
					model.addObject("startTime", taskTrigger.getTriggerDate());
				} else {
					model.addObject("startTime", taskTrigger.getStartTime());
				}
				model.addObject("stopTime", taskTrigger.getStopTime());
				if (triggerType == 2) {
					String[] weekTime = taskTrigger.getTriggerWeek().split(",");
					List<String> weekList = Arrays.asList(weekTime);
					model.addObject("weekList1", weekList);
				} else if (triggerType == 3) {

					List<String> monthList = Arrays.asList(taskTrigger
							.getTriggerMonth().split(","));

					String triggerDay = taskTrigger.getTriggerDay();
					if (triggerDay != "" && triggerDay != null) {
						List<String> dayList = Arrays.asList(triggerDay
								.split(","));
						model.addObject("monthType", "1");
						model.addObject("dayList", dayList);
					}
					String triggerFew = taskTrigger.getTriggerFew();
					if (triggerFew != "" && triggerFew != null) {
						List<String> weekList = Arrays.asList(triggerFew
								.split(","));
						List<String> weekList2 = Arrays.asList(taskTrigger
								.getTriggerWeek().split(","));
						model.addObject("monthType", "2");
						model.addObject("weekList", weekList);
						model.addObject("weekList2", weekList2);
					}
					model.addObject("monthList", monthList);

				}
			}
		} else {
			model.addObject("triggerType", 0);
		}
	}

	/**
	 * 功能:修改优先级
	 * <p>
	 * 作者 段政 2016-2-23
	 * 
	 * @param id
	 *            priority
	 */
	public ModelAndView updateTaskJobPriorityById(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String id = getStringValue(request, "id");
			String priority = getStringValue(request, "priority");
			if (id != null && id != "") {
				Map map = new HashMap();
				map.put("id", id);
				map.put("priority", priority);
				taskJobBiz.updateTaskJobPriorityById(map);
			}
		} catch (Exception e) {
			throw e;
		}
		return putToModelAndViewJson(null);
	}

	/**
	 * 功能:web采集测试
	 * <p>
	 * 作者 井晓丹 2016-2-23
	 * 
	 * @param id
	 *            priority
	 */
	public ModelAndView spiderTest(HttpServletRequest request,
			HttpServletResponse response, JobForm jobForm) {
		// 是否需要登录
		AttachProperty attachProperty = null;
		// 规则集合
		List<Rule> rules = new ArrayList<Rule>();
		Map<String, Map<String, List<Map<String, Object>>>> ruleMap = new HashMap<String, Map<String, List<Map<String, Object>>>>();
		Map<String, Object> mapObj = null;
		List<Map<String, Object>> listMap = null;
		Map<String, List<Map<String, Object>>> mapRule = null;

		// if (jobForm.getThreadNum() >= 0) {
		// if (attachProperty == null)
		// attachProperty = new AttachProperty();
		// attachProperty.setThreadNum(jobForm.getThreadNum());
		// }
		// if (jobForm.getChartset() != null) {
		// if (attachProperty == null)
		// attachProperty = new AttachProperty();
		// attachProperty.setCharSet(jobForm.getChartset());
		// }
		// 代理服务器设置
		String[] ipAddress = getArrayValue(request, "ipAddress");
		String[] ipPort = getArrayValue(request, "ipPort");
		;
		String ipAddressPort = "";
		if (ipAddress != null && ipAddress.length > 0) {
			for (int i = 0; i < ipAddress.length; i++) {
				if (ipAddress[i].equals(""))
					continue;
				ipAddressPort += ipAddress[i] + ":" + ipPort[i] + ";";
			}
			if (!ipAddressPort.equals("")) {
				ipAddressPort = ipAddressPort.substring(0,
						ipAddressPort.length() - 1);
				attachProperty.setIpAddressPort(ipAddressPort);
			}
		}
		String seedUrl = jobForm.getSeed_url();
		if(seedUrl.indexOf(",") !=-1){
			seedUrl = seedUrl.split(",")[0];
		}
		// 种子地址
		if (seedUrl != null && !seedUrl.equals("")) {
			mapObj = new HashMap<String, Object>();
			listMap = new ArrayList<Map<String, Object>>();
			mapRule = new HashMap<String, List<Map<String, Object>>>();
			mapObj.put("rule", seedUrl);
			mapObj.put("type", "url");
			mapObj.put("name", "seedUrl");
			mapObj.put("optflag", Rule.SEED_URL);
			listMap.add(mapObj);
			mapRule.put("url", listMap);
			ruleMap.put("seedurl", mapRule);
		}
		// 列表页正则 规则
		if (jobForm.getList_url() != null && !jobForm.getList_url().equals("")) {
			mapObj = new HashMap<String, Object>();
			listMap = new ArrayList<Map<String, Object>>();
			mapRule = new HashMap<String, List<Map<String, Object>>>();
			mapObj.put("rule", jobForm.getList_url());
			mapObj.put("type", jobForm.getList_rule_type());
			mapObj.put("name", "list");
			mapObj.put("optflag", Rule.LIST_URL_OPT);
			listMap.add(mapObj);
			mapRule.put(jobForm.getList_rule_type(), listMap);
			ruleMap.put("listurl", mapRule);
		}

		// 详情页正则 规则
		if (jobForm.getDetail_url() != null
				&& !jobForm.getDetail_url().equals("")) {
			mapObj = new HashMap<String, Object>();
			listMap = new ArrayList<Map<String, Object>>();
			mapRule = new HashMap<String, List<Map<String, Object>>>();
			mapObj.put("rule", jobForm.getDetail_url());
			mapObj.put("type", jobForm.getDetail_rule_type());
			mapObj.put("name", "detail");
			mapObj.put("optflag", Rule.DETAIL_URL_OPT);
			listMap.add(mapObj);
			mapRule.put(jobForm.getDetail_rule_type(), listMap);
			ruleMap.put("detailurl", mapRule);
		}

		// 详情页多页正则 规则
		if (jobForm.getDetail_more_url() != null) {
			mapObj = new HashMap<String, Object>();
			listMap = new ArrayList<Map<String, Object>>();
			mapRule = new HashMap<String, List<Map<String, Object>>>();
			mapObj.put("rule", jobForm.getDetail_more_url());
			mapObj.put("type", jobForm.getDetail_more_rule_type());
			mapObj.put("name", "detailMore");
			mapObj.put("optflag", Rule.DETAIL_MORE_URL_OPT);
			listMap.add(mapObj);
			mapRule.put(jobForm.getDetail_more_rule_type(), listMap);
			ruleMap.put("detailmoreurl", mapRule);
		}
		// 列表页区域规则
		if (jobForm.getList_content() != null
				&& !jobForm.getList_content().equals("")) {
			mapObj = new HashMap<String, Object>();
			listMap = new ArrayList<Map<String, Object>>();
			mapRule = new HashMap<String, List<Map<String, Object>>>();
			mapObj.put("rule", jobForm.getList_content_Val());
			mapObj.put("type", jobForm.getList_content_type());
			mapObj.put("name", jobForm.getList_content());
			mapObj.put("optflag", Rule.LIST_CONTENT_OPT);
			listMap.add(mapObj);
			mapRule.put(jobForm.getList_content_type(), listMap);
			ruleMap.put("listcontent", mapRule);
		}
		// 内容提取 规则
		String[] detailContents = getArrayValue(request, "detail_content");
		String[] detailContentVals = getArrayValue(request,
				"detail_content_Val");
		String[] detailContentTypes = getArrayValue(request,
				"detail_content_type");
		// 内容提取 规则
		String[] detailContentNames={};
		try {
			String detailContentNamesStr = URLDecoder.decode(getStringValue(request, "detail_content_Name"),"utf8");
			detailContentNames = detailContentNamesStr.split(",");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Rule detailContent = null;
		mapRule = new HashMap<String, List<Map<String, Object>>>();
		listMap = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < detailContents.length; i++) {
			mapObj = new HashMap<String, Object>();
			mapObj.put("rule", detailContentVals[i]);
			mapObj.put("type", detailContentTypes[i]);
			mapObj.put("name", detailContentNames[i]);
			mapObj.put("optflag", Rule.DETAIL_CONTENT_OPT);
			listMap.add(mapObj);
		}
		mapRule.put("xpath", listMap);
		ruleMap.put("detailcontent", mapRule);
		// 附件规则
		/*Rule attachmentContent = null;
		if (jobForm.getAttachment_content() != null) {
			mapObj = new HashMap<String, Object>();
			listMap = new ArrayList<Map<String, Object>>();
			mapRule = new HashMap<String, List<Map<String, Object>>>();
			mapObj.put("RULE", jobForm.getAttachment_content_Val());
			mapObj.put("TYPE", jobForm.getAttachment_content_type());
			mapObj.put("NAME", jobForm.getAttachment_content());
			mapObj.put("OPTFLAG", Rule.ATTACHMENT_URL_OPT);
			listMap.add(mapObj);
			mapRule.put(jobForm.getAttachment_content_type(), listMap);
			ruleMap.put("attachementurl", mapRule);

		}*/
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/task/testPage");
	
		WebSpider webSpider = WebSpider
				.create(new GeneralTestProcessor(ruleMap))
				.addPipeline(new FilePipeline("C:/Temp/files"))
				.addUrl(seedUrl);
		String uuid = webSpider.getUUID();
		webSpider.start();
		List<String> pages = new ArrayList<String>();
		StringBuffer str = new StringBuffer("采集测试结果：\n");
		while (true) {
			if (webSpider.getStatus().equals(Status.Stopped)) {
				System.out.println("Status:" + webSpider.getStatus());
				String filepath = "C:/Temp/files/" + uuid;
				File file = new File(filepath);
				try {
					if (file.isDirectory()) {
						String[] filelist = file.list();
						File readfile = null;
						FileInputStream in = null;
						List<String> lines=null;
						if(filelist.length>1){
						    BufferedReader br = null;  
							String line = "";
							int tableWidth = detailContentNames.length * 200 + 40;
							str.append("<table style=\"border:solid 1px #F2F2F2;width:"+ tableWidth +"px;\">");
							str.append("<tr style=\"color:#0000FF;background-color:#F9F9F9;\">");
							str.append("<td style=\"width:40px; \">序号</td>");
							for (int i = 0; i < detailContentNames.length; i++) {
								String thName = detailContentNames[i];
								if(thName.length()>=12){
									thName = thName.substring(0,12)+"…";
								}
								str.append("<td style=\"width:200px; \">"+thName+"</td>");
							}
							str.append("</tr>");
							int num = 1;
							for (int i = 0; i < filelist.length; i++) {
								String xmlContent = "";
								br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath + "\\" + filelist[i]),"UTF-8"));   
								while ((line = br.readLine()) != null) {
									xmlContent += line;
								}
								Document document;
								
								try {
									document = DocumentHelper.parseText(xmlContent);
									List<Element> listField = document.selectNodes("metadata/fields/field");
									str.append("<tr>");
									str.append("<td>"+num+"</td>");
									for (int j = 0; j < detailContentNames.length; j++) {
										for(Element ele:listField){ 
											//System.out.println(ele.getTextTrim());
											Element eleKey = ele.element("key");
											Element eleValue = ele.element("value");
											if(eleKey.getTextTrim().equals(detailContentNames[j].trim())){
												String eleValueStr = eleValue.getTextTrim();
												if(eleValueStr.length()>=12){
													eleValueStr = eleValueStr.substring(0,12)+"…";
												}
												if(eleValueStr.indexOf("<")!=-1){
													eleValueStr = eleValueStr.replaceAll("<", "&lt;");
													eleValueStr = eleValueStr.replaceAll(">", "&gt;");
												}
												str.append("<td>"+eleValueStr+"</td>");
												break;
											}
								        }
									}
									str.append("</tr>");
									num++;
									/*str.append("<tr>");
									str.append("<td>"+num+"</td>");
									for(Element ele:listField){ 
										//System.out.println(ele.getTextTrim());
										Element eleKey = ele.element("key");
										Element eleValue = ele.element("value");
										str.append("<td>"+eleKey.getTextTrim()+"</td>");
							        }
									str.append("</tr>");
									num++;
									List<Element> list = document.selectNodes("metadata/fields/field/value");
									str.append("<tr>");
									str.append("<td>"+num+"</td>");
									for(Element ele:list){
										//System.out.println(ele.getTextTrim());
										str.append("<td>"+ele.getTextTrim()+"</td>");
							        }
									str.append("</tr>");
									num++;*/
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							str.append("</table>");
							br.close();
						}
						file.delete();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {

				}
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("code", "ok");
				if (str.equals("采集测试结果：\n")) {
					str = new StringBuffer("根据规则设置无法采集到内容，请检查采集规则！");
				}
				result.put("result", str.toString());
				return this.putToModelAndViewJson(result);
			}
		}
	}

	/**
	 * 功能:页面跳转：跳转到转换任务的编辑页面
	 * <p>
	 * 作者 王晓鸣 2016-3-11
	 * 
	 * @param request
	 */
	public ModelAndView toEditTransferTaskPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		// 修改备注（yongqian.liu）：咱不使用 groupId 属性
		//String sourceId = getStringValue(request, "groupId");
		String id = getStringValue(request, "id");
		TaskJob taskJob = taskJobBiz.findById(id);
		String sourceId = taskJob.getSourceId();
		// 根据知识源ID获得知识源对象
		KnowledgeSource knowledgeSource = knowledgeSourceBiz.findById(sourceId);
		// Condition condition = Condition.parseCondition("id_string_eq");
		// condition.setValue(taskJob.getConnectId());
		TaskJob tjs = taskJobBiz.findById(taskJob.getConnectId());
		model.addObject("id", id);
		model.addObject("sourceId", sourceId);
		model.addObject("sourceName", knowledgeSource.getName());
		model.addObject("name", taskJob.getName());
		model.addObject("jobState", taskJob.getJobState());
		model.setViewName("km/crawler/transfer/editTransferTask");
		model.addObject("systemid", taskJob.getSystemId());
		model.addObject("systemname", taskJob.getSystemName());
		model.addObject("taskList", tjs);
		queryTrigger(id, model);
		return model;
	}

	/**
	 * 功能:页面跳转：根据知识模板获取模板属性字段(旧方法，已废弃)
	 * <p>
	 * 作者 王晓鸣 2016-3-4
	 * 
	 * @param request
	 */
	public ModelAndView getModelPropertiesById(HttpServletRequest request,
			HttpServletResponse response) {
		String modelId = getStringValue(request, "modelId");
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/task/attributeField");
		/*String properties = externalRestBiz.getModelProperties(modelId);
		model.addObject("properties", JSONArray.fromObject(properties));*/
		return model;
	}
	/**
	 * 功能:根据知识模板获取模板属性字段(新增加的属性字段查看方法)
	 * <p>
	 * 作者 王晓鸣 2016-4-11
	 * 
	 * @param request
	 */
	public ModelAndView getAttrubuteById(HttpServletRequest request,
			HttpServletResponse response) {
		String modelId = getStringValue(request, "modelId");
		Map<String, Object> result = new HashMap<String, Object>();
		/*String properties = externalRestBiz.getModelProperties(modelId);
		//properties = exchangeJsonPros(properties);
		result.put("properties", properties);*/
		//ModelAndView model = new ModelAndView();
		//model.setViewName("km/crawler/task/attributeField");
		//model.addObject("properties", JSONArray.fromObject(properties));
		return this.putToModelAndViewJson(result);
	}
	/**
	 * 功能：添加转换任务
	 * <p>
	 * 作者：段政 2016-3-7
	 * 
	 * @param request
	 *            ,response
	 */

	public ModelAndView addTransformTask(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			TaskJob job = null;
			// 判断是修改还是添加
			String operationType = getStringValue(request, "operationType");
			String id = getStringValue(request, "id");// 转换任务的id
			// 判断是否添加还是修改，如果是修改就获取原来的信息，从页面上获取修改的信息，进行赋值，添加就重新new。
			if (operationType.equals("update")) {
				job = this.taskJobBiz.findById(id);
			} else {
				job = new TaskJob();
			}
			// 附件抽取规则
			String extractList = getStringValue(request, "extractList");
			if(extractList!=null){
				job.setExtractList(extractList);
			}
			// 转换的名称
			String taskJobName = getStringValue(request, "taskJobName");
			// String transformDataVal= getStringValue(request,
			// "transformDataVal");
			if (taskJobName != null && taskJobName != "") {
				// transformDataVal=new
				// String(transformDataVal.getBytes("iso-8859-1"),"utf-8");
				job.setName(taskJobName);
			}
			// 知识形态id
			String knowledgeTypeVal = getStringValue(request, "knowledgeType");
			if (knowledgeTypeVal != null && knowledgeTypeVal != "") {
				job.setKnowledgeType(knowledgeTypeVal);
			}
			// 知识形态名称
			String knowledgeTypeName = getStringValue(request,
					"knowledgeTypeName");
			if (knowledgeTypeName != null && knowledgeTypeName != "") {
				// knowledgeTypeName=new
				// String(knowledgeTypeName.getBytes("iso-8859-1"),"utf-8");
				job.setKnowledgeTypeName(knowledgeTypeName);
			}
			// 知识模板Id
			String knowledgeModelVal = getStringValue(request, "knowledgeModel");
			if (knowledgeModelVal != null && knowledgeModelVal != "") {
				job.setKnowledgeModel(knowledgeModelVal);
			}
			// 知识模板名称
			String knowledgeModelName = getStringValue(request,
					"knowledgeModelName");
			if (knowledgeModelName != null && knowledgeModelName != "") {
				// knowledgeModelName=new
				// String(knowledgeModelName.getBytes("iso-8859-1"),"utf-8");
				job.setKnowledgeModelName(knowledgeModelName);
			}
			// 储存系统Id
			String systemVal = getStringValue(request, "system");
			// 储存系统Name
			String systemName = getStringValue(request, "systemName");
			// 任务定时
			String triggerchecked = getStringValue(request, "triggerchecked");
			// 定时规则
			TaskTrigger taskTrigger = null;
			if ("0".equals(triggerchecked)) {
				taskTrigger = getTrigger(request, response);
				taskTrigger.setJobName(job.getName());
				job.setAutomatic(0);
			} else {
				job.setAutomatic(1);
			}
			// 要转换任务的id
			if (id != null && id != "" && !operationType.equals("update")) {
				job.setConnectId(id);
			}
			// 根据id获取信息
			TaskJob tj = taskJobBiz.findById(id);
			String extractXml = null;
			if (tj.getRegisterType().equals("1")
					|| tj.getRegisterType().equals("2")
					|| tj.getRegisterType().equals("4")) {
				List list = null;
				// 属性字符串
				String[] propertyString = getArrayValue(request,
						"propertyString");
				if (propertyString.length > 0) {
					list = new ArrayList();
					for (int i = 0; i < propertyString.length; i++) {
						if (i != 0) {
							String key = propertyString[i].trim();
							if (key != null && key != "") {
								list.add(key);
							}
						}
					}
				}
				extractXml = Dom4jXmlUtil.extractXMLMapping(list);
			}
			if (operationType.equals("update")) {// 修改
				job.setId(id);
				String jobId = taskJobBiz.addExtractJob(job, taskTrigger,
						extractXml);
				/*
				 * taskJobBiz.editJob(job); //更新内容规则 TaskRule tr=
				 * taskRuleBiz.queryTaskRuleById(id); if(tr!=null){
				 * tr.setRuleContent(extractXml.getBytes());
				 * taskRuleBiz.save(tr); }
				 * 
				 * //更新定时 TaskTrigger tt=
				 * taskTriggerBiz.queryTriggerByJobId(id); if(tt!=null){
				 * taskTrigger.setId(tt.getId());
				 * taskTrigger.setJobName(transformDataVal);
				 * taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
				 * taskTriggerBiz.save(taskTrigger); }
				 */
			} else {// 添加
					// 资源Id
				String sourceId = getStringValue(request, "sourceId");
				String sourceName = getStringValue(request, "sourceName");
				job.setSourceId(sourceId);
				job.setSourceName(sourceName);
				String jobId = taskJobBiz.addExtractJob(job, taskTrigger,
						extractXml);
				job.setId(jobId);
				// 如果选择了存储系统，要生成传输任务
				if (systemVal != null) {
					TaskJob transferJob = new TaskJob();
					transferJob.setName(job.getName());
					transferJob.setSourceId(sourceId);
					// 设置传输任务关联转换任务
					transferJob.setConnectId(job.getId());
					transferJob.setRegisterModel(JobModel.MODEL_TRANSFER);
					transferJob.setRegisterType(JobModel.TYPE_TRANSFER);
					transferJob.setJobState(JobModel.STATE_READY);
					transferJob.setSystemId(systemVal);
					transferJob.setSystemName(systemName);
					transferJob.setKnowledgeType(knowledgeTypeVal);
					transferJob.setKnowledgeTypeName(knowledgeTypeName);
					transferJob.setKnowledgeModel(knowledgeModelVal);
					transferJob.setKnowledgeModelName(knowledgeModelName);
					transferJob.setSourceName(sourceName);
					transferJob.setSourceId(sourceId);
					
					taskJobBiz.save(transferJob);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.putToModelAndViewJson(null);
	}

	/**
	 * 功能：切换模板查询规则
	 * <p>
	 * 作者：段政 2016-3-8
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public List<Map> queryRuleTaskRulesAndParsing(String propertyId) {
		ModelAndView model = new ModelAndView();
		List<Map> list = new ArrayList<Map>();
		/*try {

			String ModelPropertiesJSON = extenalRestBiz
					.getModelProperties(propertyId);
			// 获取json字符串进行匹配
			com.alibaba.fastjson.JSONArray jsa = (com.alibaba.fastjson.JSONArray) com.alibaba.fastjson.JSONArray
					.parse(ModelPropertiesJSON);
			for (int k = 0,n=jsa.size(); k <n ; k++) {
				Map mapParam = (Map) jsa.get(k);
				if (mapParam != null) {
					list.add(mapParam);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return list;
	}

	/**
	 * 功能：验证转换任务名称
	 * 
	 * @param map
	 *            author duanzheng
	 */
	public ModelAndView verifyTheConversionTaskName(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 转换任务名称
			String name = getStringValue(request, "name");
			// 资源id
			String sourceId = getStringValue(request, "sourceId");
			// 知识模板
			String knowledgeModel = getStringValue(request, "knowledgeModel");
			// // 模块（1：采集 2：转换 3：传输）
			String registerModel = getStringValue(request, "registerModel");

			map.put("name", name);
			map.put("sourceId", sourceId);
			map.put("knowledgeModel", knowledgeModel);
			map.put("registerModel", registerModel);
			int count = taskJobBiz.verifyTheConversionTaskName(map);
			map.put("count", count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.putToModelAndViewJson(map);

	}
	/**
	 * 功能:查询运行中任务总数
	 * 作者 井晓丹2016-4-8
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getRunTaskCount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String,String> map = new HashMap<String,String>();
		map.put("jobState",JobModel.STATE_STRAT);
		int count = taskJobBiz.queryJobStateCount(map);
		result.put("count", count);
		return this.putToModelAndViewJson(result);
	}
	/**
	 * 功能：获取当前任务是否出错
	 * 作者：井晓丹 2016-4-11
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTaskErrorFlag(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		String taskId = getStringValue(request, "taskId");
		TaskJobMonitor tjm = this.taskJobMonitorBiz.queryJobMonitorByJobId(taskId);
		if(tjm!=null){
			result.put("errorFlag", tjm.getErrorFlag()==null ? "0" : tjm.getErrorFlag());
		}else{
			result.put("errorFlag","0");
		}
		return this.putToModelAndViewJson(result);
	}
	/**
	 * 功能：DB采集测试
	 * 作者：王晓鸣 2016-4-112
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView DBSpiderTest(HttpServletRequest request,HttpServletResponse response){
		Connection con = null;  
        ResultSet rs = null;  
        PreparedStatement stmt = null;
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();  
        List<List<String>> contentList=new ArrayList<List<String>>();
        List<String> titleList = new ArrayList<String>();
		String sourceId=getStringValue(request, "sourceId");
		//获取数据库表名
		String entityname = request.getParameter("TaskSelect");
		
		//获取主键
		String pkid = request.getParameter("pkid");
		//获取字段属性名
		String AttributesNameArray=request.getParameter("AttributesNameArray");
		
		//获取数据对中对应的列名
		String[] field = getArrayValue(request, "field");
		String[] fieldName = new String[field.length];
		String selectName="";
		for (int i = 0; i < field.length; i++) {
			String fieldArray[] = field[i].split(";");
			selectName +="("+fieldArray[0]+")"+",";
			fieldName[i] = fieldArray[0];
			}
		
		try {
			AttributesNameArray=URLDecoder.decode(AttributesNameArray,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AttributesNameArray=AttributesNameArray.substring(0, AttributesNameArray.length());
		String[] AttributesNameArrayB=AttributesNameArray.split(";");
		for (int i = 0; i < AttributesNameArrayB.length; i++) {
			titleList.add(AttributesNameArrayB[i]);
		}
		KnowledgeSource tjs = knowledgeSourceBiz.findById(sourceId);
		
		//获取xml解析对象
		Dom4jXmlUtil dm=new Dom4jXmlUtil();
		
		//获取MD5加密对象
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		
		//将数据库中的xml文件转换成map集合
		Map<String, String> map=dm.generateMap(tjs.getLinkContent());
		String url=map.get("url");
		String port=map.get("port");
		String DBtype=map.get("type");
		String username=tjs.getUsername();
		String password=md5Utils.Decryption(tjs.getPassword());
		String dataname=map.get("name");
		
		
		//获取数据库链接
		con=new JdbcConnectionUtil().getconnection(DBtype, url, port, dataname, username, password);
		
		try {  
		//String selectName="(username),(password)";
		selectName = selectName.substring(0,selectName.length()- 1);
		//判断数据库类型执行不同的查询语句
		String sql="";
		if(DBtype.equals("oracle")){
	        sql = "select "+selectName+" from "+entityname+" where rownum<=5 order by "+pkid;  
		}else if(DBtype.equals("mysql")){
	        sql = "select "+selectName+" from "+entityname+" limit 5 "; //order by "+pkid;  

		}else if(DBtype.equals("sqlserver")){
	        sql = "select top 5"+selectName+" from "+entityname;//+"order by "+pkid;  

		}
       
        //创建Statement对象
        stmt = con.prepareStatement(sql);  
         
        //执行SQL语句  
        rs = stmt.executeQuery();  
          
        //处理查询结果（将查询结果转换成List<Map>格式）  
        ResultSetMetaData rsmd = rs.getMetaData();  
        int num = rsmd.getColumnCount();  
          
        while(rs.next()){  
        	List<String> list = new ArrayList<String>();
            Map map2 = new HashMap();  
            for(int i = 0;i < num;i++){  
                String columnName = rsmd.getColumnName(i+1);  
                list.add(rs.getString(columnName));
                //map2.put(AttributesNameArrayB[i],rs.getString(columnName));  
            } 
            contentList.add(list);
           // resultList.add(map2);  
        	}
		} catch (Exception e) {  
	            e.printStackTrace();  
	            return null;
	    }finally {  
	            try {  
	                   //关闭结果集  
	                if (rs != null) {  
	                    rs.close();  
	                    rs = null;  
	                }  
	                   //关闭执行  
	                if (stmt != null) {  
	                    stmt.close();  
	                    stmt = null;  
	                }  
	                if (con != null) {  
	                    con.close();  
	                    con = null;  
	                }  
	            } catch (SQLException e) {  
	                e.printStackTrace();  
	            }  
	        }  
		Map<String, Object> result = new HashMap<String, Object>();
		//result.put("list", resultList);
		result.put("contentList", contentList);
		result.put("titleList", titleList);
		return this.putToModelAndViewJson(result);
		
	}
}
