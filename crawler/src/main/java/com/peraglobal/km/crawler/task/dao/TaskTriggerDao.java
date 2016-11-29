package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.task.model.TaskTrigger;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;

public interface TaskTriggerDao extends BaseDao<TaskTrigger> {
	/**
	 * 生成 ID 主键
	 * @return ID 值
	 */
	public String createId();

	/**
	 * 添加定时任务
	 * @param trigger 定时任务对象
	 * @return 0 or 1
	 */
	public int addTrigger(final TaskTrigger trigger);

	/**
	 * 编辑定时任务
	 * @param trigger 定时任务对象
	 * @return 0 or 1
	 */
	public int editTrigger(TaskTrigger trigger);

	/**
	 * 通过 ID 删除定时任务
	 * @param trigger_id 定时任务 ID
	 * @return 0 or 1
	 */
	public int delTrigger(String trigger_id);
	
	/**
	 * 通过采集任务 ID 删除定时任务 
	 * @param job_id 采集任务 ID
	 * @return 0 or 1
	 */
	public int delTriggerByJobId(Map map);
	
	/**
	 * 查询所有定时任务集合
	 * @return 定时任务集合
	 */
	public List<TaskTrigger> queryTriggerList();
	
	/**
	 * 通过定时任务 ID 查询定时任务实例
	 * @param trigger_id 定时任务 ID
	 * @return 定时任务实例
	 */
	public TaskTrigger queryTriggerById(String trigger_id);
	
	/**
	 * 更新触发器状态
	 * @param trigger 定时任务对象
	 * @return 0 or 1
	 */
	public int editTriggerState(TaskTrigger trigger);
	
	
	/**
	 * 功能：通过采集任务 ID 查询定时任务实例
	 * 作者：井晓丹 2016-1-5
	 * @param job_id 采集任务 ID
	 * @return 定时任务
	 */
	public TaskTrigger queryTriggerByJobId(Map map);
	
	/**
	 * 功能：通过采集任务 ID 删除定时任务实例
	 * 作者：段政 2016-2-23
	 * @param job_id 采集任务 ID
	 * @return 定时任务
	 */
	public int bulkDeletionByJobId(List list);
}
