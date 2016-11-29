package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.condition.Pagination;

public interface TaskJobDao extends BaseDao<TaskJob> {
	
	/**
	 * 添加采集任务
	 * @param job 任务对象
	 * @return 0 or 1
	 */
	public int addJob(final TaskJob job);

	/**
	 * 编辑采集任务
	 * @param job 任务对象
	 * @return 0 or 1
	 */
	public int editJob(final TaskJob job);

	/**
	 * 删除采集任务
	 * @param job_id 任务 ID
	 * @return 0 or 1
	 */
	public int delJob(String job_id);
	
	/**
	 * 更新采集任务状态
	 * @param job_id 任务 ID
	 * @param job_state 任务状态
	 * @return 0 or 1
	 */
	public void updateJobState(Map map);

	/**
	 * 查询所有采集任务集合，初始化任务调度器功能使用
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobList();

	/**
	 * 通过任务组 ID 查询采集任务集合
	 * @param map 任务组 ID
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobListByGroupId(Map map);

	/**
	 * 通过任务 ID 查询任务对象
	 * @param job_id 任务 ID
	 * @return 任务实例
	 */
	public TaskJob queryJobById(Map map);
	
	/**
	 * 通过任务组 ID 查询当前任务组所有任务组的采集任务集合
	 * @param group_id 组 ID
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobListByInGroupId(java.util.Map<String, Object> map);

	/**
	 * 查询当前任务名称的任务名称是否重复
	 * @param job_name 任务名称
	 * @return 0 or 1
	 */
	public int findCountByName(String group_id, String job_name);

	/**
	 * 编辑任务时查询任务名称是否重复
	 * @param taskJob 任务对象
	 * @return 0 or 1
	 */
	public int findCountByNameAndId(TaskJob taskJob);
	
	/**
	 * 添加采集任务规则
	 * @param taskRule 采集任务规则对象
	 * @return 0 or 1
	 */
	public int addRule(final TaskRule taskRule);
	
	/**
	 * 通过采集任务 ID 查询当前任务的规则实例
	 * @param job_id 任务 ID
	 * @return 规则实例
	 */
	public TaskRule getTaskRuleByJobId(Map map);
	
	/**
	 * 删除采集任务规则实例，删除任务关联使用
	 * @param job_id 任务 ID
	 * @return 0 or 1
	 */
	public int delRuleByJobId(String job_id);
	
	/**
	 * 根据组（资源） ID 获得规则列表
	 * @param group_id 组 ID
	 * @return 规则列表
	 */
	public List<TaskRule> getTaskRulesByGroupId(java.util.Map<String, Object> map);
	
	/**
	 * 根据任务名称模糊查询任务
	 * @param jobName
	 * @return
	 */
	public List<Map> queryJobByJobName(Map map);
	
	/**
	 * 根据任务查询是否开始任务
	 * @param job_ids 任务 ID 数组
	 * @return true or false
	 */
	public int queryJobIsStart(String job_ids);
	
	/**
	 * 根据任务查询是否开始任务
	 * @param job_ids 任务 ID 数组
	 * @return true or false
	 */
	public int queryTaskJobForConnectId(String job_ids);
	
	/**
	 * 功能：查询根据状态值查询状态任务的数量
	 * 作者：井晓丹 2016-1-6
	 * @param map:jobState 任务的状态
	 * @return count
	 */
	public int queryJobStateCount(Map map);

	/**
	 * 根据任务的状态查询优先级最高的任务集合
	 * @param state 状态值
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobByStateAndPriority(String state, int priority);
	
	/**
	 * 根据任务的状态查询优先级最高的任务集合
	 * @param state 状态值
	 * @return 任务集合
	 */
	public List<TaskJob> queryJobByState(Map map);

	/**
	 * 根据等待状态查询最高的优先级
	 * @param stateWait 等待状态
	 * @return 最高的优先级号
	 */
	public int queryJobMaxPriorityByState(String stateWait);

	/**
	 * 设置任务的优先级
	 * @param job_id 任务的 ID
	 * @param priority 优先级
	 */
	public void updateJobPriority(String job_id, String priority);
	
	/**
	 * 根据Id修改任务的定时状态
	 * @param map 
	 */
	public int updateJobAutomatic(Map map);
	
	/**
	 * 根据任务ID查询任务状态方法
	 * @param job_id 任务 ID
	 * @return 任务状态
	 */
	public String getStateByJobId(Map map);
	/**
	 * 功能：正序或倒序查询任务列表
	 * @param pagination
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 */
	public List<TaskJob> positiveSequenceOrReverseSwitchTasks(@Param("param") Pagination pagination,@Param("param") Map map);
	/**
	 * 功能：正序或倒序查询任务列表
	 * <p>author:duanzheng
	 * @param map
	 * @return List<TaskJob>
	 */
	public List<TaskJob> dependingOnTheTypeOfIdAndQueryTasks(@Param("param")Map map);
	
	/**
	 * 功能：根据id和类型查询任务
	 * @param pagination
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 */
	public List<TaskJob> dependingOnTheTypeOfIdAndQueryTasks(@Param("param") Pagination pagination,@Param("param") Map map);
	/**
	 * 功能：模糊查询任务列表
	 * @param pagination
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 */
	public List<TaskJob> fuzzyQueryTaskList(@Param("param") Pagination pagination,@Param("param") Map map);
	/**
	 * 功能：根据条件查询job列表
	 * @param map
	 * @return List<TaskJob>
	 * author duanzheng
	 * 
	 */
	public List<TaskJob> accordingToTheConditionsQueryJobCollection(Map map);
	/**
	 * 功能：根据id修改优先级
	 * @param map
	 * @return int
	 * author duanzheng
	 * 
	 */
	public int updateTaskJobPriorityById(Map map);
	/**
	 * 功能：验证转换任务名称
	 * @param map
	 * @return int
	 * author duanzheng
	 */
	public int verifyTheConversionTaskName(Map map);
	
	/**
	 * 功能：根据sourceId查询任务数量
	 * @author wangxiaoming
	 * @param map
	 * @return
	 */
	public int querytaskcountBysourceId(Map map);
	
	/**
	 * 功能：根据sourceId查询任务数量
	 * @author wangxiaoming
	 * @param map
	 * @return
	 */
	public int querytaskcountByKnowledge(Map map);
	
	/**
	 * 功能：查询该知识模板下的知识源数量（已去重）
	 * @author wangxiaoming
	 * @param map
	 * @return
	 */
	public int querySourceCountByKnowledge(Map map);
	
	/**
	 * 根据采集任务id查询所有的转换任务和传输任务
	 * @param taskId
	 * @return
	 */
	public List<TaskJob> findAllTaskByCrawlerJobId(Map map);
	/**
	 * 功能：转换，传输，筛选
	 * 作者 段政 2016-3-24
	 * @param map
	 * @return
	 */
	public List<TaskJob> specialFilterTransformationAndTransportTask(@Param("param") Pagination pagination,@Param("param") Map map);
	/**
	 * 功能：根据connecId查询数据
	 * @param map
	 * @return
	 */
	public TaskJob queryTaskJobDataByConnecId(Map map);
	/**
	 * 功能：修改任务发现新数据
	 * 作者：井晓丹 2016-4-14
	 * @param map
	 */
	public void updateJobHaveNewData(Map map);
	/**
	 * 功能：按日期筛选任务
	 * 作者：王晓鸣 2016-4-15
	 * @param map
	 * @return 
	 */
	public List<TaskJob> queryTaskjobByCreatedate(@Param("param") Pagination pagination,@Param("param") Map map);
	
}
