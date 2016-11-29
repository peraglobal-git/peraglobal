package com.peraglobal.km.crawler.task.dao;

import java.util.List;

import com.peraglobal.km.crawler.task.model.TaskRegister;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;
@CacheSetting
public interface TaskRegisterDao extends BaseDao<TaskRegister>{
	/**
	 * 生成 ID 主键
	 * @return ID 值
	 */
	public String createId();
	
	/**
	 * 添加任务类型注册
	 * @param tr 任务类型注册对象
	 * @return 0 or 1
	 */
	public int addRegister(TaskRegister tr);

	/**
	 * 编辑任务类型注册
	 * @param tr 任务类型注册对象
	 * @return 0 or 1
	 */
	public int editRegister(TaskRegister tr);

	/**
	 * 通过 ID 删除任务类型注册实例
	 * @param register_id 任务类型注册 ID
	 * @return 0 or 1
	 */
	public int delRegister(String register_id);

	/**
	 * 获得任务类型注册集合
	 * @return 任务注册集合
	 */
	public List<TaskRegister> queryRegisterList();

	/**
	 * 通过 ID 查询任务类型注册实例
	 * @param register_id 任务类型注册 ID
	 * @return 任务类型注册实例
	 */
	public TaskRegister queryRegisterById(String register_id);

	/**
	 * 查询任务类型注册对象名称是否重复
	 * @param register_name 类型注册名称
	 * @return 0 or 1
	 */
	public int findRegisterNameByName(String register_name);

	/**
	 * 通过类型注册 ID 和名称查询名称是否重复，编辑使用
	 * @param register_id 类型注册 ID
	 * @param register_name 类型注册名称
	 * @return 0 or 1
	 */
	public int findRegisterNameById(String register_id, String register_name);

	/**
	 * 通过类型注册的类型区分查询实例
	 * @param register_type 类型
	 * @return 类型注册实例
	 */
	public TaskRegister queryRegisterByType(String register_type);
}
