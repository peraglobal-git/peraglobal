package com.peraglobal.km.crawler.quartz.biz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.task.biz.TaskRuleBiz;
import com.peraglobal.km.crawler.task.biz.TaskTriggerBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.LocalEntity;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.task.model.TaskTrigger;
import com.peraglobal.km.crawler.util.CronExpConversion;
import com.peraglobal.km.crawler.util.QuartzJobFactory;
import com.peraglobal.km.crawler.web.biz.AttachPropertyBiz;
import com.peraglobal.km.crawler.web.biz.RuleBiz;
import com.peraglobal.km.crawler.web.dao.GeneralDao;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

/**
 * 2015-7-1
 * @author yongqian.liu
 * @see Quartz 框架任务调度器集成采集任务管理实现类
 */
@Service("quartzScheduleBiz")
public class QuartzScheduleBiz {

	private static final Logger log = LoggerFactory.getLogger(QuartzScheduleBiz.class);
	
	@Resource
	private GeneralDao generalDao;
	
	@Resource
	private TaskJobBiz taskJobBiz;
	
	@Resource
	private TaskRuleBiz taskRuleBiz;
	
	@Resource
	private TaskTriggerBiz taskTriggerService;
	
	@Resource
	private TaskJobMonitorBiz taskJobMonitorService;
	
	@Resource
	private RuleBiz ruleBiz;
	
	@Resource 
	private AttachPropertyBiz attachPropertyBiz;
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	/**
     * 启动任务调度器
     */
	public void start() {
		Scheduler sched = schedulerFactoryBean.getScheduler();
		try {
        	sched.start();
        } catch (SchedulerException e) {
        	e.printStackTrace();
        	log.error("系统启动时开始任务调度器出现异常", e);
        }
	}
	
	/**
     * 暂停任务调度器，此方法暂时没有使用
     */
	public void standby() {
		Scheduler sched = schedulerFactoryBean.getScheduler();
		try {
        	sched.standby();
        } catch (SchedulerException e) {
        	e.printStackTrace();
        	log.error("暂停任务调度器出现异常", e);
        }
	}
	
	/**
     * 停止任务调度器，此方法暂时没有使用
     */
	public void shutdown() {
		Scheduler sched = schedulerFactoryBean.getScheduler();
		try {
        	sched.shutdown();
        } catch (SchedulerException e) {
        	e.printStackTrace();
        	log.error(" 停止任务调度器出现异常", e);
        }
	}
	
	/**
     * 初始化所有任务
     */
    public void initJob(){
    	try {
			
			// 获得任务集合
			List<TaskJob> jobList = taskJobBiz.findAll();
			if(jobList != null && jobList.size() > 0){
				for (TaskJob taskJob : jobList) {
					this.addJob(taskJob);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("系统启动时初始化任务到调度器时出现异常", e);
		}    
    }
	
    /**
	 * 添加任务到调度器
	 * @param job 当前任务
	 */
	public void addJob(TaskJob job) {
		
		Scheduler sched = schedulerFactoryBean.getScheduler();
		try { 
			// 构建job信息
	        JobDetail jobDetail = JobBuilder
	        		.newJob(QuartzJobFactory.class)
	        		.withIdentity(job.getId(), job.getGroupId())
	        		.storeDurably(true) // 指明任务就算没有绑定Trigger仍保留在Quartz的JobStore中
	        		.build();
	        
	        // 放入参数，运行时的方法可以获取
	        jobDetail.getJobDataMap().put(JobModel.JOB_KEY, job);
	       
	        /*********为不同类型任务设置规则*********/
	        if(job.getRegisterType().equals(JobModel.TYPE_WEB)){
	        	// web 采集任务规则设置
				 // 得到任务规则对象
	        	Map<String,String> map = new HashMap<String,String>();
	        	map.put("taskId", job.getId());
	  			Map<String, Map<String, List<Map<String, Object>>>> rulemap = ruleBiz.getTaskRules(map);
	  	        jobDetail.getJobDataMap().put(JobModel.RULE_KEY, rulemap);
	  	        
	  	        // 得到任务附加属性
	  	        AttachProperty attachProperty = attachPropertyBiz.getAttachPropertyByTaskId(map);
	  	        if(attachProperty!=null){
	  	        	jobDetail.getJobDataMap().put(JobModel.ATTACH_PROPERTY_KEY, attachProperty);
	  	        }
	        }else if(job.getRegisterType().equals(JobModel.TYPE_DB)){
	        	// DB 采集任务规则设置
	        	String jobId= job.getId();
	        	if(jobId!=null){
	        		TaskRule taskRule = taskJobBiz.getTaskRuleByJobId(job.getId());
	        		if(taskRule!=null){
	        			jobDetail.getJobDataMap().put(JobModel.RULE_KEY, taskRule.getRuleContent());
	        		}
	        	}
	        	TaskRule taskRule = taskJobBiz.getTaskRuleByJobId(job.getId());
	        	String tt= new String(taskRule.getRuleContent());
	        //	byte[] tt = taskRule.getRuleContent();
	        	System.out.println(tt);
	        	
	        	jobDetail.getJobDataMap().put(JobModel.RULE_KEY, tt);
	        }else if(job.getRegisterType().equals(JobModel.TYPE_EXTRACT)){
	        	// 转换任务规则设置
	        	
	        }else if(job.getRegisterType().equals(JobModel.TYPE_TRANSFER)){
	        	// 传输任务规则设置
	        	
	        }
			// 查询当前任务是否为定时任务
	        TaskTrigger taskTime = taskTriggerService.queryTriggerByJobId(job.getId());
	        Trigger trigger = null;
	        
	        // 如果不存在定时任务则添加简单的触发器
	        if(taskTime == null){
		        trigger = CronExpConversion.triggerToSimple(job); // 简单的触发器
		        sched.scheduleJob(jobDetail, trigger);
	        }else{
	        	// 判断定时任务的类型
	        	switch (taskTime.getTriggerType()) {
				case JobModel.TRIGGER_DAY: // 每天执行
					// 开始时间
					trigger = CronExpConversion.triggerToDay(job, jobDetail, taskTime, JobModel.ID_START);
					sched.scheduleJob(jobDetail, trigger);

					// 结束时间
					trigger = CronExpConversion.triggerToDay(job, jobDetail, taskTime, JobModel.ID_STOP);
					sched.scheduleJob(trigger);
		        	break;
				case JobModel.TRIGGER_WEEK: // 每周执行
					// 开始时间
					trigger = CronExpConversion.triggerToWeek(job, jobDetail, taskTime, JobModel.ID_START);
					sched.scheduleJob(jobDetail, trigger);

					// 结束时间
					trigger = CronExpConversion.triggerToWeek(job, jobDetail, taskTime, JobModel.ID_STOP);
					sched.scheduleJob(trigger);
					
					break;
				case JobModel.TRIGGER_MONTH: // 每月执行
					
					// 开始时间
					trigger = CronExpConversion.triggerToMonth(job, jobDetail, taskTime, JobModel.ID_START);
					sched.scheduleJob(jobDetail, trigger);

					// 结束时间
					trigger = CronExpConversion.triggerToMonth(job, jobDetail, taskTime, JobModel.ID_STOP);
					sched.scheduleJob(trigger);
					break;
				default: // 执行一次
					
					// 开始时间
					trigger = CronExpConversion.triggerToOne(job, jobDetail, taskTime.getTriggerDate());
					sched.scheduleJob(jobDetail, trigger);
					break;
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			log.error("添加任务到调度器出现异常", e);
		}   
		
    }
	
	/**
     * 为调度器添加本地上传特殊任务
     * @param job 任务对象
     * @param localEntity 附件信息
     */
	public void addJob(TaskJob job, LocalEntity localEntity) {
		Scheduler sched = schedulerFactoryBean.getScheduler();
		try {
			if(sched==null){
				 
			}
			// 构建job信息
	        JobDetail jobDetail = JobBuilder
	        		.newJob(QuartzJobFactory.class)
	        		.withIdentity(job.getId(), job.getGroupId())
	        		.storeDurably(true) // 指明任务就算没有绑定Trigger仍保留在Quartz的JobStore中
	        		.build();
	        
	        // 放入参数，运行时的方法可以获取
	        jobDetail.getJobDataMap().put(JobModel.JOB_KEY, job);
	       
	        // local本地采集任务规则设置
	  	    jobDetail.getJobDataMap().put(JobModel.RULE_KEY, localEntity);
		        
        	// 简单的触发器
	        Trigger trigger = TriggerBuilder
	        		.newTrigger()
	        		.withIdentity(job.getId(), job.getGroupId())
	        		.build();
        	if(sched!=null){
        		sched.scheduleJob(jobDetail, trigger);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("添加任务出现异常", e);
		}    
    }
	
	/**
     * 从调度器中删除任务
     * @param job 当前任务
     */
	public void delJob(TaskJob job) {
		try {
			Scheduler sched = schedulerFactoryBean.getScheduler();
			JobKey jobKey = JobKey.jobKey(job.getId(), job.getGroupId());
			sched.deleteJob(jobKey);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("从调度器中删除任务出现异常", e);
		}
		
	}
	
	/**
	 * 功能：开始任务
	 * <p>作者 井晓丹 2016-1-5
	 * @param 当前任务
	 * @return
	 */
	public void startJob(TaskJob currentJob) {
		try {
			
			// 查询当前任务状态，如果状态是：暂停则恢复任务，如果任务状态是：就绪或停止或完成则触发一次
//			TaskJob taskJob = taskJobBiz.findById(currentJob.getId());//.queryJobById(currentJob.getId());
			// 如果任务状态为开始则不做任务处理
			if(currentJob.getJobState().equals(JobModel.STATE_STRAT)){
				return;
			}else{
				currentJob.setJobState(JobModel.STATE_STRAT);
				currentJob.setHand(true); // 设置手动触发标记
				boolean re = this.editTaskJobState(currentJob); // 更新调度器中任务对象的状态
				if(re){
					// 获得任务调度器实例
					Scheduler sched = schedulerFactoryBean.getScheduler();
					// 获得任务
					JobKey jobkey = JobKey.jobKey(currentJob.getId(), currentJob.getGroupId());
					// 触发任务
					sched.triggerJob(jobkey);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("开始任务出现异常", e);
		}
	}
	
	/**
     * 暂停任务
     * @param job 当前任务
     */
	public void pauseJob(TaskJob currentJob) {
		try {
			// 如果任务状态为暂停或停止或完成则不做任务处理
			if(!currentJob.getJobState().equals(JobModel.STATE_STRAT)){
				return;
			}else{
				currentJob.setJobState(JobModel.STATE_PAUSE);
				currentJob.setHand(true); // 设置手动触发标记
				boolean re = this.editTaskJobState(currentJob);
				if(re){
					Scheduler sched = schedulerFactoryBean.getScheduler();
					JobKey jobkey = JobKey.jobKey(currentJob.getId(), currentJob.getGroupId());
					sched.triggerJob(jobkey);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("暂停任务出现异常", e);
		}
	}
	
	 /**
     * 停止任务
     * @param job 当前任务
     */
	public void stopJob(TaskJob currentJob) {
		try {
			// 如果任务状态为停止或者就绪状态则不做任务处理
			if(currentJob.getJobState().equals(JobModel.STATE_READY)
					|| currentJob.getJobState().equals(JobModel.STATE_STOP)){
				return;
			}else{
				currentJob.setJobState(JobModel.STATE_STOP);
				currentJob.setHand(true); // 设置手动触发标记
				boolean re = this.editTaskJobState(currentJob);
				if(re){
					Scheduler sched = schedulerFactoryBean.getScheduler();
					JobKey jobkey = JobKey.jobKey(currentJob.getId(), currentJob.getGroupId());
					sched.triggerJob(jobkey);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("停止任务出现异常", e);
		}
	}
	
	/**
     * 为调度器添加触发器功能
     * @param trigger 触发器
     */
    public void addTrigger(Trigger trigger){
    	Scheduler sched = schedulerFactoryBean.getScheduler();
    	try {
			sched.scheduleJob(trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("为调度器添加触发器失败出现异常", e);
		}
    }
    
    /**
     * 禁用暂停触发器
     * @param job_id 任务 ID
     */
    public void pauseTrigger(String job_id){
    	Scheduler sched = schedulerFactoryBean.getScheduler();
    	try {
    		Map map=new HashMap();
	 		map.put("id", job_id);
	 		TaskJob taskJob = taskJobBiz.queryJobById(map);
    		TriggerKey tk = TriggerKey.triggerKey(job_id + JobModel.ID_START, taskJob.getGroupId());
    		sched.pauseTrigger(tk);
    		tk = TriggerKey.triggerKey(job_id + JobModel.ID_STOP, taskJob.getGroupId());
    		sched.pauseTrigger(tk);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("为调度器添加触发器失败出现异常", e);
		}
    }
    
    /**
     * 启用恢复触发器
     * @param job_id 任务ID
     */
    public void resumeTrigger(String job_id){
    	Scheduler sched = schedulerFactoryBean.getScheduler();
    	try {
    		Map map=new HashMap();
	 		map.put("id", job_id);
	 		TaskJob taskJob = taskJobBiz.queryJobById(map);
    		TriggerKey tk = TriggerKey.triggerKey(job_id + JobModel.ID_START, taskJob.getGroupId());
    		sched.resumeTrigger(tk);
    		tk = TriggerKey.triggerKey(job_id + JobModel.ID_STOP, taskJob.getGroupId());
    		sched.resumeTrigger(tk);
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("为调度器添加触发器失败出现异常", e);
		}
    }
    
	/**
     * 判断当前任务是否为定时任务
     * @param job_id 任务 ID
     * @param jobDataMap 调度器中任务 Map 对象
     */
	public void fixedTimeUpJob(String job_id){
		try {
			
			// 日期格式化字符串
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			String date = df.format(new Date());
			// 获得当前时间的定时任务
			TaskTrigger triggerJob = taskTriggerService.queryTriggerByJobId(job_id);
			
			// 判断定时任务是否启用
			if(triggerJob != null && triggerJob.getTriggerState().equals(JobModel.TRIGGER_ON)){
				Map map1=new HashMap();
				map1.put("id", job_id);
				TaskJob taskJob = taskJobBiz.queryJobById(map1);
				
				// 执行一次，直接开始任务
				if(triggerJob.getTriggerType().equals(JobModel.TRIGGER_ONE)){ 
					// 判断当前任务是否为开始状态，如果非开始状态则更新开始任务状态
					if(!taskJob.getJobState().equals(JobModel.STATE_STRAT)){
						taskJob.setJobState(JobModel.STATE_STRAT);
						
						// 修改调度器中任务的状态，返回后直接启动任务
						this.editTaskJobState(taskJob);
					}
				}else{
					String startTime = df.format(df.parse(triggerJob.getStartTime()));
					String stopTime = df.format(df.parse(triggerJob.getStopTime()));
	
					// 如果触发任务为当前开始时间，则为定时任务触发开发任务
					if(startTime.equals(date)){
						// 判断当前任务是否为开始状态，如果非开始状态则更新开始任务状态
						if(!taskJob.getJobState().equals(JobModel.STATE_STRAT)){
							taskJob.setJobState(JobModel.STATE_STRAT);
							
							// 修改调度器中任务的状态，返回后直接启动任务
							this.editTaskJobState(taskJob);
						}
					}
					
					// 如果触发任务为当前停止时间，则判断任务状态是否开始，如果是则更新就绪任务状态
					if(stopTime.equals(date)) {
						if(taskJob.getJobState().equals(JobModel.STATE_STRAT)){
							taskJob.setJobState(JobModel.STATE_READY);
							
							// 修改调度器中任务的状态
							this.editTaskJobState(taskJob);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("处理定时任务出现异常", e);
		}
	}
	
	/**
	 * 对外接口，更新数据库和内存中任务的状态，主要是采集任务运行中调用
	 * @param job_id 任务 ID
	 * @param job_state 任务状态
	 */
	public void updateTaskJobState(String job_id, String job_state){
		try {
			
			// 更改数据状态
			taskJobBiz.updateTaskJobState(job_id, job_state);
			Map map1=new HashMap();
			map1.put("id", job_id);
			TaskJob job = taskJobBiz.queryJobById(map1);
			Scheduler sched = schedulerFactoryBean.getScheduler();
	        JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(job.getId(), job.getGroupId()));
	        if(jobDetail != null){
	        	jobDetail = jobDetail.getJobBuilder().ofType(QuartzJobFactory.class).build();
	        	
	        	//更新参数 实际测试中发现无法更新
	            JobDataMap jobDataMap = jobDetail.getJobDataMap();
	            TaskJob taskJob = (TaskJob)jobDataMap.get(JobModel.JOB_KEY);
	            taskJob.setJobState(job_state);
	            jobDataMap.put(JobModel.JOB_KEY, taskJob);
	            jobDetail.getJobBuilder().usingJobData(jobDataMap);
	        }
	        
	        // 自动采集完成任务后，启动等待任务功能（注释此逻辑，无等待中任务）
//	        this.startAfterJob(); 
	        
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("更新数据库和内存中任务的状态出现异常", e);
		}
	}
	/**
	 * 任务执行过程中出错
	 * @param job_id 任务 ID
	 * @param job_state 任务状态
	 */
	public void markTaskJobError(String job_id, String job_state,String error){
		try {
			
			// 更改数据状态
			taskJobBiz.markTaskJobError(job_id, job_state, error);
			Map map1=new HashMap();
			map1.put("id", job_id);
			TaskJob job = taskJobBiz.queryJobById(map1);
			Scheduler sched = schedulerFactoryBean.getScheduler();
	        JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(job.getId(), job.getGroupId()));
	        if(jobDetail != null){
	        	jobDetail = jobDetail.getJobBuilder().ofType(QuartzJobFactory.class).build();
	        	
	        	//更新参数 实际测试中发现无法更新
	            JobDataMap jobDataMap = jobDetail.getJobDataMap();
	            TaskJob taskJob = (TaskJob)jobDataMap.get(JobModel.JOB_KEY);
	            taskJob.setJobState(job_state);
	            jobDataMap.put(JobModel.JOB_KEY, taskJob);
	            jobDetail.getJobBuilder().usingJobData(jobDataMap);
	        }
	        
	        // 自动采集完成任务后，启动等待任务功能（注释此逻辑，无等待中任务）
//	        this.startAfterJob(); 
	        
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("更新数据库和内存中任务的状态出现异常", e);
		}
	}
	/**
	 * 内部方法，更新调度器和数据库中任务的状态
	 * @param job 当前任务
	 * @return 成功或失败
	 */
	private boolean editTaskJobState(TaskJob job) {
        try {
        	boolean re = true;
        	
        	// 修改任务状态
			taskJobBiz.updateTaskJobState(job.getId(), job.getJobState());
        	Scheduler sched = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = sched.getJobDetail(JobKey.jobKey(job.getId(), job.getGroupId()));
            
            // 如果不存在jobDetail对象则创建
            if(jobDetail == null){
            	this.addJob(job);
            	re = false;
            }else{
            	jobDetail = jobDetail.getJobBuilder().ofType(QuartzJobFactory.class).build();
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                TaskJob taskJob = (TaskJob)jobDataMap.get(JobModel.JOB_KEY);
                
                // 如果状态为就绪和暂停都作为暂停处理，特殊的情况，这种情况是定时任务触发，暂停采集任务的功能
                if(job.getJobState().equals(JobModel.STATE_READY))
                	job.setJobState(JobModel.STATE_PAUSE);
                
                taskJob.setJobState(job.getJobState()); // 设置任务的状态
                taskJob.setHand(job.isHand());  // 设置是否为手动触发任务标记
                jobDataMap.put(JobModel.JOB_KEY, taskJob);
                jobDetail.getJobBuilder().usingJobData(jobDataMap);
            }
            return re;
        } catch (SchedulerException e) {
        	e.printStackTrace();
            log.error("更新任务状态出现异常", e);
        }
        return false;
    }
	
	/**
	 * 开始任务之前，判断开始任务是不是超出最大允许范围，如果不够则开始当前任务
	 * 如果超出则设置当前任务为等待状态
	 * @param job 当前开始的任务
	 * @return 是否开启任务
	 */
	public boolean startBeforeJob(TaskJob job){
		int taskMaxRunNum = Integer.parseInt(AppConfigUtils.get("task.run.maxnum"));//Integer.parseInt("10");
		// 判断正在运行的任务是不是超出最大允许范围
		Map<String,String> map = new HashMap<String,String>();
		map.put("jobState",JobModel.STATE_STRAT);
		int count = taskJobBiz.queryJobStateCount(map);
		if(count <= taskMaxRunNum){
			return true;
		}else{
			// 修改为等待中状态逻辑（去掉此逻辑 by jingxd 2016-4-8）
			//boolean bool=taskJobBiz.updateTaskJobState(job.getId(), JobModel.STATE_WAIT); // 修改当前状态为等待
			return false;
		}
	}
	
	/**
	 * 暂停或停止任务之后，查询等待任务中优先级最高的任务，并开启任务，如果不存在则不处理
	 */
	public void startAfterJob(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("jobState",JobModel.STATE_STRAT);
		int count = taskJobBiz.queryJobStateCount(map);
		int taskMaxRunNum = Integer.parseInt(AppConfigUtils.get("task.run.maxnum"));
		if(count < taskMaxRunNum){
			// 查询等待任务的最高优先级
			List<TaskJob> jobList = taskJobBiz.queryJobByState(JobModel.STATE_WAIT);
			if(jobList != null && jobList.size() > 0){
				TaskJob job = jobList.get(0);
				this.taskJobBiz.updateTaskJobState(job.getId(), JobModel.STATE_STRAT);
				this.startJob(job);
//				for (int i = 0; i < taskMaxRunNum - count; i++) {
//					this.startJob(jobList.get(i));
//				}
			}
			/*// 如果有优先级设置的任务则查询最高优先级的任务
			if(priority != 0){
				// 根据状态值查询优先级最高的人任务
				List<TaskJob> jobList = taskJobBiz.queryJobByStateAndPriority(JobModel.STATE_WAIT, priority);
				if(jobList != null && jobList.size() > 0){
					for (int i = 0; i < taskMaxRunNum - count; i++) {
						this.startJob(jobList.get(i));
					}
				}
			}else{
				// 根据状态值查询优先级最高的人任务
				List<TaskJob> jobList = taskJobBiz.queryJobByState(JobModel.STATE_WAIT);
				if(jobList != null && jobList.size() > 0){
					for (int i = 0; i < taskMaxRunNum - count; i++) {
						this.startJob(jobList.get(i));
					}
				}
			}*/
		}
	}
	
	
	
	
}
