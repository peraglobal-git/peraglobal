package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.task.model.MonitorError;
import com.peraglobal.pdp.core.BaseDao;

public interface MonitorErrorDao extends BaseDao<MonitorError> {
	/**
	 * 查询监控错误信息
	 * @param map
	 * @return
	 */
	public List<MonitorError> queryMonitorErrors(Map map);
	
	/**
	 * 删除监控报错信息
	 * @param list
	 */
	public void delTaskMonitorErrorsByIds(List list);
}
