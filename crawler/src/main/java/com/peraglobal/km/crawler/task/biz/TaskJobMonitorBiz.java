package com.peraglobal.km.crawler.task.biz;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.source.model.KnowledgeDetail;
import com.peraglobal.km.crawler.task.dao.TaskJobMonitorDao;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskJobMonitor;
import com.peraglobal.pdp.core.BaseBiz;
import com.peraglobal.pdp.core.condition.Pagination;

/**
 * 2015-12-18
 * @author xiaoming.wang
 * 任务监控操作管理业务逻辑接口
 */
@Service("taskJobMonitorBiz")
public class TaskJobMonitorBiz extends BaseBiz<TaskJobMonitor, TaskJobMonitorDao> {
	
	private static Logger logger = LogManager.getLogger(TaskJobMonitorBiz.class);
	
	@Resource
	private TaskJobBiz taskJobBiz;
	
	@Resource
	private TaskJobMonitorDao taskJobMonitorDao;
	
	/**
	 * 增加任务监控，开始任务时触发
	 * @param jobMonitor 任务监控对象
	 * @return true or false
	 */
	
	public boolean addJobMonitor(TaskJobMonitor jobMonitor){
		try {
			Map map=new HashMap();
			if(jobMonitor.getJobId()==null || jobMonitor.getJobId()==""){
				map.put("jobId", "-1");
			}else{
				map.put("jobId", jobMonitor.getJobId());
			}
			Map<String,Object> taskJobMonitorMap= taskJobMonitorDao.queryMaxTriggerNumber(map);
			int trigger_number=0;
			if(taskJobMonitorMap!=null){
				String triggerNumber=""+ taskJobMonitorMap.get("triggerNumber");
				if(triggerNumber!=null && triggerNumber!=""){
					trigger_number=Integer.parseInt(triggerNumber);	
				}
			}
			jobMonitor.setTriggerNumber(trigger_number + 1); // 设置触发数量
			jobMonitor.setStartTime(new Date());
			this.save(jobMonitor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 更新暂停时间
	 * @param jobMonitor 任务监控对象
	 * @return true or false
	 */
	public boolean updatePause(TaskJobMonitor jobMonitor){
		try {
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(jobMonitor.getJobId());
			if(tjm != null){
				jobMonitor.setId(tjm.getId());
				tjm.setJobState(JobModel.STATE_PAUSE);
				tjm.setPauseTime(new Date());
				tjm.setStopTime(new Date());
				this.save(tjm);
			}else{
				this.addJobMonitor(jobMonitor);
			}
//			taskJobMonitorDao.updatePause(jobMonitor);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 更新停止时间
	 * @param jobMonitor 任务监控对象
	 * @return true or false
	 */
	
	public boolean updateStop(TaskJobMonitor jobMonitor){
		try {
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(jobMonitor.getJobId());
			if(tjm != null){
				tjm.setJobState(jobMonitor.getJobState());//修改这是因为如果开始任务超过10个需要把任务修改成等待，之前任务是等待状态，而监控信息是停止状态（之前不管修改停止状态还是修改等待状态这里永远是停止状态）
				if(jobMonitor.getJobState().equals(JobModel.STATE_STOP)){//判断任务是否停止，如果停止加上停止时间
					tjm.setStopTime(new Date());
				}
				this.save(tjm);
			//	taskJobMonitorDao.updateStop(jobMonitor);
			}else{
				this.addJobMonitor(jobMonitor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获得所有任务监控
	 * @return 任务监控集合
	 */
	
	public List<Map> queryJobMonitorAll(Map map){
		try {
			return taskJobMonitorDao.queryJobMonitorAll(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据任务ID获得当前任务监控
	 * @param job_id 任务 ID
	 * @return 任务监控实例
	 */
	
	public TaskJobMonitor queryJobMonitorByJobId(String job_id){
		try {
			Map map=new HashMap();
			map.put("jobId", job_id);
			
//			Map taskJobMonitorMap = taskJobMonitorDao.queryMaxTriggerNumber(map);
//			String triggerNumber=""+ taskJobMonitorMap.get("TRIGGERNUMBER");
//			int trigger_number=0;
//			if(triggerNumber!=null && triggerNumber!=""){
//				trigger_number= Integer.parseInt(triggerNumber);
//			}
//			map.put("triggerNumber", trigger_number);
			List<TaskJobMonitor> list = taskJobMonitorDao.queryJobMonitorByJobIdAndTrigger(map);
			TaskJobMonitor jobMonitor=null;
			if(list!=null && list.size()>0){
				jobMonitor = list.get(0);
			}
			return jobMonitor;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据任务ID获得当前任务所有监控
	 * @param job_id 任务 ID
	 * @return 任务监控集合
	 */
	
	public List<TaskJobMonitor> queryJobMonitorListByJobId(String job_id){
		try {
			return taskJobMonitorDao.queryJobMonitorListByJobId(job_id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 编辑当前运行的任务异常次数和异常备注
	 * @param job_id 任务 ID
	 * @param en 异常数
	 * @param desc 描述
	 * @return true or false
	 */
	
	public boolean updateException(Map map){
		try {
			String job_id=""+ map.get("job_id");
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(job_id);
			if(tjm != null){
				taskJobMonitorDao.updateException(map);
			}else{
				TaskJob job = taskJobBiz.queryJobById(map);
				TaskJobMonitor jobMonitor = new TaskJobMonitor();
				jobMonitor.setJobId(job.getId());
				jobMonitor.setJobName(job.getName());
				jobMonitor.setJobState(job.getJobState());
				this.addJobMonitor(jobMonitor);
				taskJobMonitorDao.updateException(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 编辑当前运行的任务采集成功条数
	 * @param job_id 任务 ID
	 * @param full 成功数
	 * @return true or false
	 */
	
	public boolean updateFull(String job_id, int full){
		try {
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(job_id);
			if(tjm != null){
				taskJobMonitorDao.updateFull(tjm.getId(), full);
			}else{
				Map map=new HashMap();
				map.put("job_id", job_id);
				TaskJob job = taskJobBiz.queryJobById(map);
				TaskJobMonitor jobMonitor = new TaskJobMonitor();
				jobMonitor.setJobId(job.getId());
				jobMonitor.setJobName(job.getName());
				jobMonitor.setJobState(job.getJobState());
				this.addJobMonitor(jobMonitor);
				taskJobMonitorDao.updateFull(jobMonitor.getId(), full);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 编辑当前运行的任务采集失败条数
	 * @param job_id 任务 ID
	 * @param failed 失败数
	 * @return true or false
	 */
	
	public boolean updateFailed(String job_id, int failed){
		try {
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(job_id);
			if(tjm != null){
				taskJobMonitorDao.updateFailed(tjm.getId(), failed);
			}else{
				Map map=new HashMap();
				map.put("job_id", job_id);
				TaskJob job = taskJobBiz.queryJobById(map);
				TaskJobMonitor jobMonitor = new TaskJobMonitor();
				jobMonitor.setJobId(job.getId());
				jobMonitor.setJobName(job.getName());
				jobMonitor.setJobState(job.getJobState());
				this.addJobMonitor(jobMonitor);
				taskJobMonitorDao.updateFailed(jobMonitor.getId(), failed);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 编辑当前运行的任务采集成功和失败条数
	 * @param job_id 任务 ID
	 * @param full 成功数
	 * @param failed 失败数
	 * @return true or false
	 */
	
	public boolean updateFullAndFailed(String job_id, int full, int failed){
		logger.info("任务ID："+job_id+"===成功数："+full+"===失败数："+failed);
		try {
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(job_id);
			if(tjm != null){
				tjm.setFullNumber(full);
				tjm.setFailedNumber(failed);
				this.save(tjm);
			}else{
				TaskJob job = taskJobBiz.findById(job_id);
				TaskJobMonitor jobMonitor = new TaskJobMonitor();
				jobMonitor.setJobId(job.getId());
				jobMonitor.setJobName(job.getName());
				jobMonitor.setJobState(job.getJobState());
				jobMonitor.setFullNumber(full);
				jobMonitor.setFailedNumber(failed);
				this.addJobMonitor(jobMonitor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 删除任务监控，删除任务时触发
	 * @param monitor_ids 任务监控 ID 集合
	 * @return true or false
	 */
	
	public boolean delJobMonitors(String monitor_ids){
		try {
			String[] monitor_id = monitor_ids.split(",");
			for (int i = 0; i < monitor_id.length; i++) {
				if(!monitor_id[i].equals("")){
					taskJobMonitorDao.delJobMonitor(monitor_id[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 清空任务监控
	 * @return true or false
	 */
	
	public boolean delAll(){
		try {
			taskJobMonitorDao.delAll();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 根据任务ID删除任务监控数据
	 * @param job_id 任务 ID
	 * @return true or false
	 */
	
	public boolean delJobMonitorByJobId(String job_id){
		try {
			taskJobMonitorDao.delJobMonitorByJobId(job_id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 更新任务监控中任务的名称
	 * @param job_id 任务 ID
	 * @param job_name 任务名称
	 * @return true or false
	 */
	
	public boolean updateJobName(String job_id, String job_name){
		try {
			TaskJobMonitor tjm = this.queryJobMonitorByJobId(job_id);
			if(tjm != null){
				Map map=new HashMap();
				map.put("jobName", job_name);
				map.put("jobId", job_id);
				taskJobMonitorDao.updateJobName(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 功能：删除任务监控信息
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delTASKJOBMONITORByIds(List list){
		try {
			taskJobMonitorDao.delTASKJOBMONITORByIds(list);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：统计传输任务数量
	 * <p> 作者：井晓丹 2016-3-28
	 * @param 任务id
	 */
	public int countTransferNumber(String jobId){
		Map map = new HashMap();
		map.put("jobId", jobId);
		Map<String,Object> result = this.taskJobMonitorDao.countTransferNumber(map);
		if(result!=null){
			return Integer.parseInt(result.get("transferNumber").toString());
		}
		return 0;
	}
}
