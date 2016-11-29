package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.task.model.TaskGroup;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;


/**
 * 2015-12-16
 * @author zheng.duan
 * 任务组操作管理数据保存接口
 */
@CacheSetting
public interface TaskGroupDao  extends BaseDao<TaskGroup> {
	
	/**
	 * 添加任务组
	 * @param qgd 组对象
	 * @return 0 or 1
	 */
	
	public int addGroup(Map map);

	/**
	 * 编辑任务组
	 * @param qgd 组对象
	 * @return 0 or 1
	 */
	public int editGroup(final TaskGroup qgd);

	/**
	 * 删除任务组
	 * @param qgd 组 ID
	 * @return 0 or 1
	 */
	public int delGroup(String group_id);

	/**
	 * 通过父 ID 查询任务组集合
	 * @param group_perant_id 父 ID
	 * @return 组集合
	 */
	public List<TaskGroup> queryGroupList(String group_perant_id);
	
	/**
	 * 通过组 ID 查询组对象
	 * @param group_id 组 ID
	 * @return 组对象
	 */
	public TaskGroup queryGroupByGroupId(Map map);
	
	/**
	 * 同节点组 ID 子节点组的数量
	 * @param group_perant_id 父组 ID
	 * @return count
	 */
	public int queryGroupCountByGroupPerantId(String group_perant_id);

	/**
	 * 通过父 ID 查询子组否有重复的名称
	 * @param taskGroup 任务组对象
	 * @return count
	 */
	public int findCountByPid(TaskGroup taskGroup);

	/**
	 * 通过父 ID 和 组 ID 查询是否有重复的名称
	 * @param taskGroup 任务组对象
	 * @return count
	 */
	public int findCountByPidAndId(TaskGroup taskGroup);

	/**
	 * 通过节点 ID 递归查询当前节点下所有的组集合
	 * @param group_id 节点 ID 
	 * @return 组集合
	 */
	public List<TaskGroup> recursionGroupById(String group_id);
	
}