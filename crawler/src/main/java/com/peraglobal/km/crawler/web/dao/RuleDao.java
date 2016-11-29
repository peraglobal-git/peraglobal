package com.peraglobal.km.crawler.web.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;

@CacheSetting
public interface RuleDao extends BaseDao<Rule>{

	public List<Map<String, Object>> findRuleListByTaskId(Map map);
	/**
	 * 功能：查询任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public List<Rule> queryTaskRuleById(Map map);
	/**
	 * 功能：删除任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void deleteTaskRuleByIds(List list);
}
