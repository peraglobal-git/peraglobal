package com.peraglobal.km.crawler.task.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.task.dao.TaskRuleDao;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.pdp.core.BaseBiz;

@Service("taskRuleBiz")
public class TaskRuleBiz extends BaseBiz<TaskRule, TaskRuleDao>{

	@Resource
	private TaskRuleDao taskRuleDao;
	/**
	 * 根据jobId获得规则对象
	 * @param jobId
	 * @return 规则对象
	 */
	public List<TaskRule> queryTaskRuleByjobId(String jobId) {
		try {
			Map map=new HashMap();
			map.put("jobId", jobId);
			return taskRuleDao.queryRuleByjobId(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据jobId获得规则对象
	 * @param jobId
	 * @return 规则对象
	 */
	public TaskRule queryTaskRuleById(String jobId) {
		try {
			Map map=new HashMap();
			map.put("jobId", jobId);
			return taskRuleDao.queryRuleById(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 功能：删除任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delTaskRuleByIds(List list){
		try {
			taskRuleDao.delTaskRuleByIds(list);
		} catch (Exception e) {
			throw e;
		}
	}
}
