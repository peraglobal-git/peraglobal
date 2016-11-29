package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.pdp.core.BaseDao;

public interface TaskRuleDao extends BaseDao<TaskRule>{
	
	/**
	 * 根据JobId查询 
	 * @param map JobId
	 * @return 规则实例
	 */
	public List<TaskRule> queryRuleByjobId(Map map);
	
	/**
	 * 根据JobId查询 
	 * @param map JobId
	 * @return 规则实例
	 */
	public TaskRule queryRuleById(Map map);
	/**
	 * 功能：删除任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delTaskRuleByIds(List list);
}
