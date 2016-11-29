package com.peraglobal.km.crawler.web.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.web.dao.RuleDao;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.pdp.core.BaseBiz;
/**
 * 2015-12-30
 * @author xiaodan.jing
 * web采集任务操作管理业务逻辑接口
 */
@Service
public class RuleBiz extends BaseBiz<Rule,RuleDao> {
	@Resource
	private RuleDao ruleDao;
	/**
	 * 功能：删除任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void deleteTaskRuleByIds(List list){
		try {
			ruleDao.deleteTaskRuleByIds(list);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 功能：查询任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public List<Rule> queryTaskRuleById(Map map){
		try {
			return ruleDao.queryTaskRuleById(map);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Map<String, Map<String, List<Map<String, Object>>>> getTaskRules(Map map){
		Map<String, Map<String, List<Map<String, Object>>>>  rulemap = new HashMap<String, Map<String, List<Map<String, Object>>>>();
		List<Map<String, Object>> list = ruleDao.findRuleListByTaskId(map);
		
		for (Map<String, Object> rule : list) {
			Map<String, List<Map<String, Object>>> optflag = rulemap.get(rule.get("optflag"));
			if(optflag==null){
				rulemap.put(rule.get("optflag").toString(), optflag=new HashMap<String, List<Map<String, Object>>>());
			}
			List<Map<String, Object>> types = optflag.get(rule.get("type"));
			
			if(types == null){
				optflag.put(rule.get("type").toString(), types=new ArrayList<Map<String,Object>>());
			}
			types.add(rule);
		}
		return rulemap;
	}
}
