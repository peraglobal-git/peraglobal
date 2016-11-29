package com.peraglobal.km.crawler.task.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.dao.TaskTriggerDao;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskTrigger;
import com.peraglobal.pdp.common.id.IDGenerate;
import com.peraglobal.pdp.core.BaseBiz;

/**
 * 2015-12-18
 * @author xiaoming.wang
 * 定时任务操作管理业务逻辑实现
 */
@Service
public class TaskTriggerBiz extends BaseBiz<TaskTrigger,TaskTriggerDao>{

	@Resource
	private TaskTriggerDao taskTriggerDao;
	@Resource
    private QuartzScheduleBiz quartzScheduleService;
	/**
	 * 添加定时任务
	 * @param trigger 定时任务对象
	 * @return true or false
	 */
	public boolean addTrigger(TaskTrigger trigger) {
		try {
			trigger.setId(IDGenerate.uuid());
			return taskTriggerDao.addTrigger(trigger) == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 编辑定时任务
	 * @param trigger 定时任务对象
	 * @return true or false
	 */
	public boolean editTrigger(TaskTrigger trigger) {
		try {
			return taskTriggerDao.editTrigger(trigger) == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除多个定时任务
	 * @param trigger_ids 定时任务 ID 集合
	 * @return true or false
	 */
	public boolean delTriggers(String trigger_ids) {
		try {
			String[] trigger_id = trigger_ids.split(",");
			for (int i = 0; i < trigger_id.length; i++) {
				if(!trigger_id[i].equals("")){
					this.delTrigger(trigger_id[i]);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除定时任务
	 * @param trigger_id 定时任务 ID
	 * @return true or false
	 */
	public boolean delTrigger(String trigger_id) {
		try {
			return taskTriggerDao.delTrigger(trigger_id) == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获得所有的定时任务
	 * @return 定时任务集合
	 */
	public List<TaskTrigger> queryTriggerList() {
		try {
			return taskTriggerDao.queryTriggerList();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据定时任务ID获得定时任务对象
	 * @param trigger_id 定时任务 ID
	 * @return 定时任务
	 */
	public TaskTrigger queryTriggerById(String trigger_id){
		try {
			return taskTriggerDao.queryTriggerById(trigger_id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据任务ID获得定时任务对象
	 * 作者：井晓丹 2016-1-6
	 * @param job_id 任务 ID
	 * @return 定时任务实例
	 */
	public TaskTrigger queryTriggerByJobId(String jobId) {
		Map map = new HashMap();
		map.put("jobId", jobId);
		return taskTriggerDao.queryTriggerByJobId(map);
		
	}

	/**
	 * 禁用定时任务
	 * @param trigger_ids 定时任务ID
	 * @return true or false
	 */
	public boolean pauseTrigger(String trigger_ids) {
		try {
			String[] trigger_id = trigger_ids.split(",");
			TaskTrigger trigger = null;
			for (int i = 0; i < trigger_id.length; i++) {
				if(trigger_id[i] != null){
					trigger  = taskTriggerDao.queryTriggerById(trigger_id[i]);
					trigger.setTriggerState(JobModel.TRIGGER_OFF); // 禁用状态
					taskTriggerDao.editTriggerState(trigger); // 更新触发器数据库状态
					quartzScheduleService.pauseTrigger(trigger.getJobId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 启用定时任务
	 * @param trigger_ids 定时任务ID
	 * @return true or false
	 */
	public boolean resumeTrigger(String trigger_ids) {
		try {
			String[] trigger_id = trigger_ids.split(",");
			TaskTrigger trigger = null;
			for (int i = 0; i < trigger_id.length; i++) {
				if(trigger_id[i] != null){
					trigger  = taskTriggerDao.queryTriggerById(trigger_id[i]);
					trigger.setTriggerState(JobModel.TRIGGER_ON); // 启用状态
					taskTriggerDao.editTriggerState(trigger); // 更新触发器数据库状态
					quartzScheduleService.resumeTrigger(trigger.getJobId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 通过采集任务 ID 删除定时任务 
	 * @param job_id 采集任务 ID
	 * @return 0 or 1
	 */
	public int delTriggerByJobId(Map map){
		return taskTriggerDao.delTriggerByJobId(map);
	}
	/**
	 * 功能：通过采集任务 ID 删除定时任务实例
	 * 作者：段政 2016-2-23
	 * @param job_id 采集任务 ID
	 * @return 定时任务
	 */
	public int bulkDeletionByJobId(List list){
		try {
			return taskTriggerDao.bulkDeletionByJobId(list);
		} catch (Exception e) {
			throw e;
		}
	}
}