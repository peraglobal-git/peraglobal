package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.task.model.TaskJobMonitor;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;


/**
 * 2015-7-2
 * @author yongqian.liu
 * 任务监控操作管理数据保存接口
 */

public interface TaskJobMonitorDao extends BaseDao<TaskJobMonitor> {

	/**
	 * 增加任务监控，开始任务时触发
	 * @param jobMonitor 任务监控对象
	 * @return 0 or 1
	 */
	public int addJobMonitor(TaskJobMonitor jobMonitor);
	
	/**
	 * 功能：编辑任务监控对象，更新暂停时间
	 * 作者：井晓丹 2016-1-7
	 * @param jobMonitor 任务监控对象
	 * @return 0 or 1
	 */
	public int updatePause(TaskJobMonitor jobMonitor);
	
	/**
	 * 功能：编辑任务监控对象，更新停止时间
	 * 作者：井晓丹 2016-1-7
	 * @param jobMonitor 任务监控对象
	 * @return 0 or 1
	 */
	public int updateStop(TaskJobMonitor jobMonitor);

	/**
	 * 获得所有任务监控
	 * @return 任务监控集合
	 */
	public List<Map> queryJobMonitorAll(Map map);

	/**
	 * 根据任务 ID 和触发数获得当前任务监控
	 * @param job_id 任务 ID
	 * @param tn 触发数
	 * @return 监控实例
	 */
	public List<TaskJobMonitor> queryJobMonitorByJobIdAndTrigger(Map map);
	
	/**
	 * 根据任务ID获得当前任务所有监控
	 * @param job_id 任务 ID
	 * @return 任务监控集合
	 */
	public List<TaskJobMonitor> queryJobMonitorListByJobId(String job_id);

	/**
	 * 编辑当前运行的任务异常次数和异常备注
	 * @param monitor_id 监控 ID
	 * @param en 异常数
	 * @param desc 描述
	 * @return 0 or 1
	 */
	public int updateException(Map map);
	
	/**
	 * 编辑当前运行的任务采集成功条数
	 * @param monitor_id 任务监控 ID
	 * @param full 成功数
	 * @return 0 or 1
	 */
	public int updateFull(String monitor_id, int full);
	
	/**
	 * 编辑当前运行的任务采集失败条数
	 * @param monitor_id 任务监控 ID
	 * @param failed 失败数
	 * @return 0 or 1
	 */
	public int updateFailed(String monitor_id, int failed);
	
	/**
	 * 编辑当前运行的任务采集成功和失败条数
	 * @param monitor_id 任务监控 ID
	 * @param full 成功数
	 * @param failed 失败数
	 * @return 0 or 1
	 */
	public int updateFullAndFailed(Map map);
	
	/**
	 * 功能：通过任务 ID 获得当前任务监控最大的触发数
	 * 作者：井晓丹 2016-1-8
	 * @param job_id 任务 ID
	 * @return number
	 */
	public Map<String,Object> queryMaxTriggerNumber(Map map);
	
	/**
	 * 清空任务监控，删除所有任务监控数据行
	 * @return 0 or 1
	 */
	public int delAll();
		
	/**
	 * 删除任务监控，删除任务时触发
	 * @param monitor_id 任务监控 ID
	 * @return 0 or 1
	 */
	public int delJobMonitor(String monitor_id);
	
	/**
	 * 根据任务 ID 删除任务监控数据
	 * @param job_id 任务 ID
	 * @return 0 or 1
	 */
	public int delJobMonitorByJobId(String job_id);
	
	/**
	 * 更新任务名称，在任务修改过程中同时修改监控冗余字段值
	 * @param job_id 任务 ID
	 * @param job_name 任务名称
	 * @return 0 or 1
	 */
	public int updateJobName(Map map);
	
	/**
	 * 更新任务监控中任务的状态
	 * @param job_id 任务 ID
	 * @param job_state 任务状态
	 * @return 0 or 1
	 */
	public int updateJobState(String job_id, String job_state);
	/**
	 * 功能：删除任务监控信息
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delTASKJOBMONITORByIds(List list);
	/**
	 * 功能：统计传输数目
	 * <p> 作者：井晓丹 2016-3-28
	 * @param 任务id
	 */
	public Map<String,Object> countTransferNumber(Map map);
}