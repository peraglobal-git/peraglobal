package com.peraglobal.km.crawler.task.biz;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.extract.biz.KnowledgeMetadataBiz;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.source.biz.KnowledgeSourceBiz;
import com.peraglobal.km.crawler.task.dao.TaskJobDao;
import com.peraglobal.km.crawler.task.model.JdbcEnity;
import com.peraglobal.km.crawler.task.model.JdbcField;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.LocalEntity;
import com.peraglobal.km.crawler.task.model.MonitorError;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskJobMonitor;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.task.model.TaskTrigger;
import com.peraglobal.km.crawler.task.model.TransferConfig;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.JdbcConnectionUtil;
import com.peraglobal.km.crawler.web.biz.AttachPropertyBiz;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.crawler.web.biz.RuleBiz;
import com.peraglobal.km.crawler.web.dao.GeneralDao;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.biz.MgKnowledgeMetadataBiz;
import com.peraglobal.pdp.common.id.IDGenerate;
import com.peraglobal.pdp.core.BaseBiz;
import com.peraglobal.pdp.core.condition.Pagination;



/**
 * 2015-12-17
 * @author xiaoming.wang
 * 采集任务操作管理业务逻辑接口
 */
@Service("taskJobBiz")
public class TaskJobBiz extends BaseBiz<TaskJob, TaskJobDao>{
	@Resource
	private KnowledgeSourceBiz KnowledgeSourceBiz;

	@Resource
	private GeneralDao generalDao;
	
	@Resource
	private TaskJobDao taskJobDao;
	
	@Resource
	private TaskRuleBiz taskRuleBiz;
		
	@Resource
	private RuleBiz ruleBiz;
	
	@Resource
	private AttachPropertyBiz attachPropertyBiz;
	
	@Resource
	private GeneralDao generalBiz;
	
	@Resource
	private TaskJobMonitorBiz taskJobMonitorBiz;

	@Resource
	private TaskTriggerBiz taskTriggerBiz;
	@Resource
    private QuartzScheduleBiz quartzScheduleBiz;
	@Resource
	private KnowledgeMetadataBiz knowledgeMetadataBiz;
	@Resource
	private DatumBiz datumBiz;
	
	@Resource
	private MonitorErrorBiz monitorErrorBiz;
	@Resource
	private MgDatumBiz mgDatumBiz;
	@Resource
	private MgKnowledgeMetadataBiz mgKnowledgeMetadataBiz;
	@Resource
	private TransferConfigBiz transferConfigBiz;
	
	/**
	 * 功能：增加WEB任务和规则实例，仅创建 WEB 采集任务时使用
	 * <p>作者 井晓丹 2015-12-30
	 * @param job 任务对象
	 * @param rules 规则表达式
	 * @param attachProperty 附件规则表达式
	 * @param taskTrigger 定时任务
	 * @return true or false
	 */
	public String addWebJob(TaskJob job, List<Rule> rules
			, AttachProperty attachProperty, TaskTrigger taskTrigger) {
		try {
			if(job.getId() == null){
				job.setId(IDGenerate.uuid());
				this.add(job);
				// 新建定时任务
				if(taskTrigger!=null){
					taskTrigger.setJobId(job.getId());
					taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
					taskTriggerBiz.save(taskTrigger);
				}
				saveWebTaskRule(job, rules, attachProperty);// 添加任务到调度器中
				quartzScheduleBiz.addJob(job);
			}else{
				boolean isPause = false;
				if(job.getJobState().equals(JobModel.STATE_PAUSE)){
					isPause = true;
				}
				this.save(job);
				if(!isPause){
					List list = new ArrayList();
					list.add(job.getId());
					//删除附件属性
					attachPropertyBiz.delAttachPropertyByIds(list);
					//删除规则
					ruleBiz.deleteTaskRuleByIds(list);
					saveWebTaskRule(job, rules, attachProperty);
				}
				if(taskTrigger!=null){
					taskTrigger.setJobId(job.getId());
					taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
					taskTriggerBiz.save(taskTrigger);
				}else{
					TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(job.getId());
					if(trigger!=null){
						taskTriggerBiz.delete(trigger);
					}
				}
				quartzScheduleBiz.delJob(job);
				// 添加任务到调度器中
				quartzScheduleBiz.addJob(taskJobDao.findById(job.getId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return job.getId();
	}

	private void saveWebTaskRule(TaskJob job, List<Rule> rules,
			AttachProperty attachProperty) {
		Rule rule=null;
		// 新建 WEB 采集任务规则
		for(int i = 0; i < rules.size(); i++){
			 rule = rules.get(i);
			 rule.setTaskId(job.getId());
			 ruleBiz.save(rule);
		}
		// 新建 WEB 采集任务附件规则
		if(attachProperty !=null){
			attachProperty.setTaskId(job.getId());
			attachPropertyBiz.save(attachProperty);
		}
	}
	
	/**
	 * 增加 DB任务和规则实例，仅创建 DB 采集任务时使用
	 * @param job 任务对象
	 * @param jdbcEnity DB 规则表达式
	 * @param taskTrigger 定时任务
	 * @return true or false
	 */
	
	public String addDbJob(TaskJob job, JdbcEnity jdbcEnity, TaskTrigger taskTrigger){
		//TaskGroup group = taskGroupBiz.queryGroupByGroupId(job.getGroupId());
		//job.setGroupName(group.getName());
		byte[] ruleContent = Dom4jXmlUtil.putXML(jdbcEnity).getBytes();
		TaskRule taskRule = new TaskRule();
		//taskRule.setId(IDGenerate.uuid());
		taskRule.setRuleType(job.getRegisterType());
		taskRule.setRuleContent(ruleContent);
		//taskRule.setCreateDate(DateUtil.getTimeNow());
		//taskRule.setUpdateDate(DateUtil.getTimeNow());
		if(job.getId() == null){
			job.setId(IDGenerate.uuid());
			add(job);
			taskRule.setJobId(job.getId());
			taskRuleBiz.add(taskRule);
			if(taskTrigger!=null){
				taskTrigger.setJobId(job.getId());
				taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
				taskTriggerBiz.save(taskTrigger);
			}
			// 添加任务到调度器中
			quartzScheduleBiz.addJob(job);
    	}else{
    		String ruleId=taskRuleBiz.queryTaskRuleById(job.getId()).getId();
    		taskRule.setId(ruleId);
    		taskRule.setJobId(job.getId());
    		taskJobDao.update(job);
    		taskRuleBiz.updateIncrement(taskRule);
    		if(taskTrigger!=null){
				taskTrigger.setJobId(job.getId());
				taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
				taskTriggerBiz.save(taskTrigger);
				
			}else{
				TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(job.getId());
				if(trigger!=null){
					taskTriggerBiz.delete(trigger);
				}
			}
    		//删除编辑之前调度器中的任务
    		quartzScheduleBiz.delJob(job);
    		// 添加任务到调度器中
    		quartzScheduleBiz.addJob(taskJobDao.findById(job.getId()));
    	}
		return job.getId();
	}
	
	/**
	 * 增加 传输任务
	 * @param job 任务对象
	 * @param taskTrigger 定时任务
	 * @return true or false
	 */
	public void addTransfer(TaskJob job ,TaskTrigger taskTrigger,TransferConfig tc){
		if(job.getId() == null){
			job.setId(IDGenerate.uuid());
			add(job);
			if(taskTrigger!=null){
				taskTrigger.setJobId(job.getId());
				taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
				taskTriggerBiz.save(taskTrigger);
			}
			if(tc !=null){
				tc.setTaskId(job.getId());
				transferConfigBiz.save(tc);
			}
			// 添加任务到调度器中
			quartzScheduleBiz.addJob(job);
    	}else{
    		taskJobDao.update(job);
    		/*Map map =new HashMap();
    		map.put("automatic", job.getAutomatic());
    		map.put("id", job.getId());
    		taskJobDao.updateJobAutomatic(map);*/
    		if(taskTrigger!=null){
				taskTrigger.setJobId(job.getId());
				taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
				taskTriggerBiz.save(taskTrigger);
				
			}else{
				TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(job.getId());
				if(trigger!=null){
					taskTriggerBiz.delete(trigger);
				}
			}
    		//删除编辑之前调度器中的任务
    		quartzScheduleBiz.delJob(job);
    		// 添加任务到调度器中
    		quartzScheduleBiz.addJob(taskJobDao.findById(job.getId()));
    	}
	}
	/**
	 * 增加 Local 任务和规则实例，仅创建 Local 采集任务时使用，创建成功后马上开始任务特别处理
	 * @param job 任务对象
	 * @param Local 规则表达式
	 * @return true or false
	 */
	public boolean addJob(TaskJob job, LocalEntity localEntity) {
		try {
			job.setRegisterModel(JobModel.MODEL_CRAWLER); // 采集模块
			job.setSourceId(job.getGroupId());
			job.setJobState(JobModel.STATE_STRAT);
			this.add(job);
			
			// 添加任务到调度器中
			quartzScheduleBiz.addJob(job, localEntity);
			// 修改数据库任务状态，特殊任务，创建后直接开始任务功能
//			updateTaskJobState(job.getId(), JobModel.STATE_STRAT);
			quartzScheduleBiz.startJob(job);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 增加 转换任务和规则实例，仅创建转换任务时使用
	 * @param job 任务对象
	 * @param taskTrigger 定时任务
	 * @return true or false
	 */
	public String addExtractJob(TaskJob job, TaskTrigger taskTrigger, String mappingConfig) {
		try {
			if(job.getId() == null){
				// 新增
				job.setId(IDGenerate.uuid());
				job.setRegisterType(JobModel.TYPE_EXTRACT);	 //转换任务
				job.setRegisterModel(JobModel.MODEL_EXTRACT); // 转换模块
				job.setJobState(JobModel.STATE_READY);
				this.add(job);
				if(taskTrigger!=null){
					taskTrigger.setJobId(job.getId());
					taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
					taskTriggerBiz.save(taskTrigger);
				}
				// 保存转换规则
				if(!mappingConfig.equals("")){
					TaskRule taskRule = new TaskRule();
					taskRule.setId(IDGenerate.uuid());
					taskRule.setJobId(job.getId());
					taskRule.setRuleType(job.getRegisterType());
					taskRule.setRuleContent(mappingConfig.getBytes());
					taskJobDao.addRule(taskRule);
				}
				// 添加任务到调度器中
				quartzScheduleBiz.addJob(job);
			}else{
				// 修改
				this.save(job);
				//更新内容规则
				TaskRule tr= taskRuleBiz.queryTaskRuleById(job.getId());
				if(tr!=null){
					tr.setRuleContent(mappingConfig.getBytes());
					taskRuleBiz.save(tr);
				}
				//更新定时
				if(taskTrigger!=null){
					TaskTrigger tt= taskTriggerBiz.queryTriggerByJobId(job.getId());
					if(tt!=null){//如果等于null就添加否则就修改
						taskTrigger.setId(tt.getId());
						taskTrigger.setJobId(job.getId());
						taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
						taskTriggerBiz.save(taskTrigger);
					}else{
						taskTrigger.setJobId(job.getId());
						taskTrigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
						taskTriggerBiz.save(taskTrigger);
					}
				}else{
					TaskTrigger trigger = taskTriggerBiz.queryTriggerByJobId(job.getId());
					if(trigger!=null){
						taskTriggerBiz.delete(trigger);
					}
				}
				quartzScheduleBiz.delJob(job);
				// 添加任务到调度器中
				quartzScheduleBiz.addJob(taskJobDao.findById(job.getId()));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return job.getId();
		}
		return job.getId();
	}
	
	/**
	 * 编辑任务和规则实例
	 * @param job 任务对象
	 * @return true or false
	 */
	public boolean editJob(TaskJob job) {
		try {
			int result = taskJobDao.editJob(job);
//			if(result != 0){
//				// 更新任务监控中任务的名称
//				taskJobMonitorBiz.updateJobName(job.getId(), job.getName());
//			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * 更新任务状态及任务监控状态
	 * @param job_id 任务 ID
	 * @param job_state 任务状态
	 * @return true or false
	 */
	public boolean updateTaskJobState(String id, String jobState) {
		try {
			Map map=new HashMap();
			map.put("jobState", jobState);
			if(jobState.equals(JobModel.STATE_STRAT)){
				map.put("hasNewData", "0");
			}
			map.put("id", id);
			taskJobDao.updateJobState(map);
			
			TaskJob taskJob = taskJobDao.findById(id);//queryJobById(map);
			TaskJobMonitor jobMonitor = new TaskJobMonitor();
			jobMonitor.setJobId(taskJob.getId());
			jobMonitor.setJobName(taskJob.getName());
			jobMonitor.setJobState(jobState);
			// 开始任务时增加监控
			if(jobState.equals(JobModel.STATE_STRAT)){
				taskJobMonitorBiz.addJobMonitor(jobMonitor);
			}else if(jobState.equals(JobModel.STATE_PAUSE)){ // 更新暂停监控状态
				jobMonitor.setStopTime(new Date());
				jobMonitor.setPauseTime(new Date());
//					taskJobMonitorBiz.save(jobMonitor);
				taskJobMonitorBiz.updatePause(jobMonitor);
			}else{
				jobMonitor.setStopTime(new Date());
//					taskJobMonitorBiz.save(jobMonitor);
				taskJobMonitorBiz.updateStop(jobMonitor); // 停止任务监控状态
				// 放开上面的注释
			}
			
		} catch (Exception e) {
   			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 更新任务状态及任务监控状态及报错信息
	 * @param job_id 任务 ID
	 * @param job_state 任务状态
	 * @return true or false
	 */
	public boolean markTaskJobError(String id, String jobState, String errorFlag) {
		try {
			Map map=new HashMap();
			map.put("jobState", jobState);
			map.put("id", id);
			taskJobDao.updateJobState(map);
			
			TaskJob taskJob = taskJobDao.findById(id);//queryJobById(map);
			TaskJobMonitor jobMonitor = new TaskJobMonitor();
			jobMonitor.setJobId(taskJob.getId());
			jobMonitor.setJobName(taskJob.getName());
			jobMonitor.setJobState(jobState);
			if(jobState.equals(JobModel.STATE_PAUSE)||jobState.equals(JobModel.STATE_STOP)){ // 更新暂停监控状态
				TaskJobMonitor tjm = taskJobMonitorBiz.queryJobMonitorByJobId(taskJob.getId());
				if(tjm != null){
					tjm.setJobState(jobState);
					tjm.setPauseTime(new Date());
					tjm.setStopTime(new Date());
					tjm.setErrorFlag(errorFlag);
					taskJobMonitorBiz.save(tjm);
					
					MonitorError me = new MonitorError();
					me.setMonitorId(tjm.getId());
					me.setErrorType(errorFlag);
					me.setContent(errorFlag.equals(JobModel.JOB_CONECTION_ERROR)? "连接失败" : "未知错误");
					me.setTaskId(taskJob.getId());
					monitorErrorBiz.save(me);
				}
			}
			
		} catch (Exception e) {
   			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 获得所有任务，调度器初始化任务时使用
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobList() {
		try {
			return taskJobDao.queryJobList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据组ID获得任务集合
	 * @param group_id 组 ID
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobLIstByGroupId(Map map) {
		try {
			return taskJobDao.queryJobListByGroupId(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据任务ID获得任务对象
	 * @param job_id 任务 ID
	 * @return 任务对象
	 */
	public TaskJob queryJobById(Map map) {
		try {
			return taskJobDao.queryJobById(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据组ID获得当前组下所有的任务
	 * @param group_id 组 ID
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobListByInGroupId(java.util.Map<String, Object> map) {
		try {
			List<TaskJob> list=new ArrayList<TaskJob>();
			 list=taskJobDao.queryJobListByInGroupId(map);
			 return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 功能：根据 jdbc 对象获得数据库连接的所有表
	 * 作者：王晓鸣
	 * @param jdbc JDBC 数据源
	 * @param DBType 数据库类型
	 * @return 数据库表集合
	 */
	public List<String> getTables(JdbcEnity jdbc,String DBType) {
		try {
			return JdbcConnectionUtil.getTables(jdbc,DBType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据 jdbc 对象获得数据库连接的所有视图
	 * @param jdbc JDBC 数据源
	 * @param DBType 数据库类型
	 * @return 数据库视图集合
	 */
	public List<String> getViews(JdbcEnity jdbc,String DBType) {
		try {
			return JdbcConnectionUtil.getTables(jdbc,DBType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据 jdbc 对象获得数据库表的结构
	 * @param jdbcEnity  JDBC 数据源包含表名
	 * @return 数据库中某个表的字段集合
	 */
	public List<JdbcField> getFields(JdbcEnity jdbcEnity) {
		try {
			return JdbcConnectionUtil.getFields(jdbcEnity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 功能：根据任务 ID 获得 DB 规则对象a
	 * @param job_id 任务 ID
	 * @return DB 任务规则实例
	 */
	public TaskRule getTaskRuleByJobId(String jobId) {
		try {

			Map map=new HashMap();
			map.put("jobId", jobId);
			return taskJobDao.getTaskRuleByJobId(map);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 添加采集任务规则
	 * @param taskRule 采集任务规则对象
	 * @return 0 or 1
	 */
	public int addRule(TaskRule taskRule) {
//		taskRule.setId(taskJobDao.createId());
		return taskJobDao.addRule(taskRule);
	}
	
	/**
	 * 根据组（资源） ID 获得规则列表
	 * @param group_id 组 ID
	 * @return 规则列表
	 */
	public List<TaskRule> getTaskRulesByGroupId(java.util.Map<String, Object> map) {
		return taskJobDao.getTaskRulesByGroupId(map);
	}
	
	/**
	 * 根据任务名称模糊查询任务
	 * @param jobName
	 * @return
	 */
	public String queryJobListByJobName(String jobName) {
		Map map=new HashMap();
		map.put("name", jobName);
		List<Map> jobList = this.taskJobDao.queryJobByJobName(map);
		Pattern pattern = Pattern.compile("^-?\\d+$"); // 正整数的正则
//		boolean b= matcher.matches();
		String num = "";
		Matcher matcher = null;
		int maxNum = -1;
		String Name=null;
		for(Map job : jobList){
			Name=""+job.get("NAME");
			if(Name.equals(jobName)){
				if(maxNum > -1){
					continue;
				}
				maxNum = 0;
			}
			if(Name.startsWith(jobName+"-")){
				num = job.get("NAME").toString().substring(jobName.length()+1);
				matcher = pattern.matcher(num);
				if(matcher.matches() && maxNum <  Integer.parseInt(num)){
					maxNum = Integer.parseInt(num);
				}
			}
		}
		if(maxNum > -1){
			maxNum ++;
			return jobName + "-" + maxNum + "";
		}
		return jobName;
	}

	/**
	 * 删除采集任务前验证功能
	 * @param job_ids 任务 ID 数组
	 * @return 是否存在被占用的任务
	 */
	public boolean jobIdForApply(String job_ids) {
		boolean re = taskJobDao.queryJobIsStart(job_ids) > 0 ? true : false;
		if(!re){
			re = taskJobDao.queryTaskJobForConnectId(job_ids) > 0 ? true : false;
		}
		return re;
	}
	
	/**
	 * 查询根据状态值查询状态任务的数量
	 * @param job_state 任务的状态
	 * @return count
	 */
	public int queryJobStateCount(Map map) {
		return taskJobDao.queryJobStateCount(map);
	}

	/**
	 * 根据任务的状态查询优先级最高的任务集合
	 * @param state 状态值
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobByStateAndPriority(String state, int priority) {
		return taskJobDao.queryJobByStateAndPriority(state, priority);
	}
	
	/**
	 * 根据任务的状态查询优先级最高的任务集合
	 * @param state 状态值
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobByState(String state) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("jobState",state);
		return taskJobDao.queryJobByState(map);
	}

	/**
	 * 根据等待状态未开启的任务
	 * @param stateWait 等待状态
	 * @return 最高的优先级号
	 */
	public int queryJobMaxPriorityByState(String stateWait) {
		return taskJobDao.queryJobMaxPriorityByState(stateWait);
	}

	/**
	 * 设置任务的优先级
	 * @param job_ids 任务 ID
	 * @param priority 优先级
	 * @return 是否成功
	 */
	public boolean setJobPriority(String job_ids, String priority) {
		String[] job_id = job_ids.split(",");
		for (int i = 0; i < job_id.length; i++) {
			if(job_id[i] != "" && !"".equals(job_id[i])){
				taskJobDao.updateJobPriority(job_id[i], priority);
			}
		}
		return false;
	}

	/**
	 * 根据任务ID查询任务状态方法
	 * @param job_id 任务 ID
	 * @return 任务状态
	 */
	public String getStateByJobId(Map map) {
		return taskJobDao.getStateByJobId(map);
	}
	/**
	 * 查询任务列表行数
	 * @param group_id
	 * @return 行数
	 */
	public int getTaskListCount(String group_id) {
		return 0;
	}
	/**
	 * 功能：正序或倒序查询任务列表
	 * @param pagination
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 */
	public List<TaskJob> positiveSequenceOrReverseSwitchTasks(Pagination pagination,Map map){
		List<TaskJob> listJob=null;
		try {
			listJob=taskJobDao.positiveSequenceOrReverseSwitchTasks(pagination, map);
			if(listJob.size()>0){
				Map mapParam=null;
				for (int i = 0; i < listJob.size(); i++) {
					if(listJob.get(i).getRegisterType()!=null && listJob.get(i).getRegisterType()!=""){
						mapParam=new HashMap();
						if(listJob.get(i).getRegisterType().equals("4")){//根据registerType判断从哪个表中获取数据
							if(listJob.get(i).getId()!=null && listJob.get(i).getId()!=""){
								mapParam.put("taskId", listJob.get(i).getId());
								listJob.get(i).setDatumCount(knowledgeMetadataBiz.queryExtractNumberBytaskId(mapParam));	
							}
						}else if(listJob.get(i).getRegisterType().equals(JobModel.TYPE_TRANSFER)){
							listJob.get(i).setDatumCount(taskJobMonitorBiz.countTransferNumber(listJob.get(i).getId()));
						}else{
							if(listJob.get(i).getId()!=null && listJob.get(i).getId()!=""){
								//查询采集数量
								mapParam.put("id", listJob.get(i).getId());
								listJob.get(i).setDatumCount(datumBiz.numberOfQueriesToCollect(mapParam));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return listJob;
	}
	/**
	 * 功能：根据id和类型查询任务
	 * @param pagination
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 */
	public List<TaskJob> dependingOnTheTypeOfIdAndQueryTasks(Pagination pagination,Map map){
		List<TaskJob> list=null;
		try {
			list=taskJobDao.dependingOnTheTypeOfIdAndQueryTasks(pagination,map);
			if(list.size()>0){
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getRegisterType()!=null && list.get(i).getRegisterType()!=""){
						map=new HashMap();
						if(list.get(i).getRegisterType().equals("4")){//根据registerType判断从哪个表中获取数据
							if(list.get(i).getId()!=null && list.get(i).getId()!=""){
								map.put("taskId", list.get(i).getId());
								list.get(i).setDatumCount(knowledgeMetadataBiz.queryExtractNumberBytaskId(map));
							}
						}else if(list.get(i).getRegisterType().equals(JobModel.TYPE_TRANSFER)){
							list.get(i).setDatumCount(taskJobMonitorBiz.countTransferNumber(list.get(i).getId()));
						}else{
							if(list.get(i).getId()!=null && list.get(i).getId()!=""){
								//查询采集数量
								map.put("id", list.get(i).getId());
								list.get(i).setDatumCount(datumBiz.numberOfQueriesToCollect(map));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}
	
	/**
	 * 功能：根据id和类型查询任务
	 * @param map
	 * @return List<TaskJob>
	 * <p> author duanzheng
	 */
	public List<TaskJob> dependingOnTheTypeOfIdAndQueryTasks(Map map){
		try {
			return taskJobDao.dependingOnTheTypeOfIdAndQueryTasks(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：模糊查询任务列表
	 * @param pagination
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 */
	public List<TaskJob> fuzzyQueryTaskList(Pagination pagination,Map map){
		try {
			return taskJobDao.fuzzyQueryTaskList(pagination, map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：根据条件查询job列表
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 * 
	 */
	public List<TaskJob> accordingToTheConditionsQueryJobCollection(Map map){
		try {
			return taskJobDao.accordingToTheConditionsQueryJobCollection(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 修改已采数量
	 * @param map
	 */
	public void updateJobState(Map map){
		try {
			taskJobDao.updateJobState(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：根据id修改优先级
	 * @param map
	 * @return int
	 * author duanzheng
	 * 
	 */
	public int updateTaskJobPriorityById(Map map){
		try {
			return taskJobDao.updateTaskJobPriorityById(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：验证转换任务名称
	 * @param map
	 * @return int
	 * author duanzheng
	 */
	public int verifyTheConversionTaskName(Map map){
		try {
			return taskJobDao.verifyTheConversionTaskName(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 功能：根据sourceId查询任务数量
	 * @author wangxiaoming
	 * @param sourceId
	 * @return
	 */
	public int querytaskcountBysourceId(String sourceId){
		try {
			Map map = new HashMap();
			map.put("sourceId", sourceId);
			return taskJobDao.querytaskcountBysourceId(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 功能：根据知识模板查询任务数量
	 * @author wangxiaoming
	 * @param sourceId
	 * @return
	 */
	public int querytaskcountByKnowledge(String knowledgeModel){
		try {
			Map map = new HashMap();
			map.put("knowledgeModel", knowledgeModel);
			return taskJobDao.querytaskcountByKnowledge(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 功能：查询该知识模板下的知识源数量（已去重）
	 * @author wangxiaoming
	 * @param sourceId
	 * @return
	 */
	public int querySourceCountByKnowledge (String knowledgeModel){
		try {
			Map map = new HashMap();
			map.put("knowledgeModel", knowledgeModel);
			return taskJobDao.querySourceCountByKnowledge(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 根据采集任务id查询所有的转换任务和传输任务
	 * @param taskId
	 * @return
	 */
	public List<TaskJob> findAllTaskByCrawlerJobId(String taskId){
		List<TaskJob> list = null;
		try {
			Map map = new HashMap();
			map.put("taskId", taskId);
			list = taskJobDao.findAllTaskByCrawlerJobId(map);//递归查询
		} catch (Exception e) {
			throw e;
		}
		return list;
	}
	/**
	 * 功能：转换，传输，筛选
	 * 作者 段政 2016-3-24
	 * @param map
	 * @return
	 */
	public List<TaskJob> specialFilterTransformationAndTransportTask(Pagination pagination,Map map,String type){
		List<TaskJob> taskJobList=new ArrayList<TaskJob>();
		try {
			Map param=null;
			String getRegisterType=null;
			taskJobList=taskJobDao.specialFilterTransformationAndTransportTask(pagination,map);
				if(taskJobList.size()>0){
					for (int i = 0,n=taskJobList.size(); i < n; i++) {//循环数据
						param=new HashMap();
						if(taskJobList.get(i).getRegisterType()!=null && taskJobList.get(i).getRegisterType()!=""){
							if(taskJobList.get(i).getRegisterType().equals("4")){//根据registerType判断从哪个表中获取数据
								if(taskJobList.get(i).getId()!=null && taskJobList.get(i).getId()!=""){
									//param.put("taskId", taskJobList.get(i).getId());
									taskJobList.get(i).setDatumCount(mgKnowledgeMetadataBiz.findByTaskId(taskJobList.get(i).getId()));
								}
							}else if(taskJobList.get(i).getRegisterType().equals(JobModel.TYPE_TRANSFER)){
								taskJobList.get(i).setDatumCount(taskJobMonitorBiz.countTransferNumber(taskJobList.get(i).getId()));
							}else{
								if(taskJobList.get(i).getId()!=null && taskJobList.get(i).getId()!=""){
									//查询采集数量
									//param.put("id", taskJobList.get(i).getId());
									taskJobList.get(i).setDatumCount(mgDatumBiz.findByTaskId(taskJobList.get(i).getId()));
								}
							}
						}								
					}
				}
		} catch (Exception e) {
			throw e;
		}
		return taskJobList;
	}
	/**
	 * 功能：修改任务发现新数据
	 * 作者：井晓丹 2016-4-14
	 * @param map
	 */
	public List<TaskJob> queryTaskjobByCreatedate(Pagination pagination,Map map){
		List<TaskJob> taskJobList=new ArrayList<TaskJob>();
		try {
			Map param=null;
			taskJobList=taskJobDao.queryTaskjobByCreatedate(pagination, map);
			if(taskJobList.size()>0){
				for (int i = 0,n=taskJobList.size(); i < n; i++) {//循环数据
					param=new HashMap();
					if(taskJobList.get(i).getRegisterType()!=null && taskJobList.get(i).getRegisterType()!=""){
						if(taskJobList.get(i).getRegisterType().equals("4")){//根据registerType判断从哪个表中获取数据
							if(taskJobList.get(i).getId()!=null && taskJobList.get(i).getId()!=""){
								//param.put("taskId", taskJobList.get(i).getId());
								taskJobList.get(i).setDatumCount(mgKnowledgeMetadataBiz.findByTaskId(taskJobList.get(i).getId()));
							}
						}else if(taskJobList.get(i).getRegisterType().equals(JobModel.TYPE_TRANSFER)){
							taskJobList.get(i).setDatumCount(taskJobMonitorBiz.countTransferNumber(taskJobList.get(i).getId()));
						}else{
							if(taskJobList.get(i).getId()!=null && taskJobList.get(i).getId()!=""){
								//查询采集数量
								//param.put("id", taskJobList.get(i).getId());
								taskJobList.get(i).setDatumCount(mgDatumBiz.findByTaskId(taskJobList.get(i).getId()));
							}
						}
					}								
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return taskJobList;
		
	}
	/**
	 * 功能：修改任务发现新数据
	 * 作者：井晓丹 2016-4-14
	 * @param map
	 */
	public void updateJobHaveNewData(String connectId){
		Map map = new HashMap();
		map.put("connectId", connectId);
		map.put("jobState", JobModel.STATE_STOP);
		this.taskJobDao.updateJobHaveNewData(map);
	}
}

