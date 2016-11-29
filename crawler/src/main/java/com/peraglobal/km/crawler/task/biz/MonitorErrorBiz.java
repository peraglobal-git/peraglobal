package com.peraglobal.km.crawler.task.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.task.dao.MonitorErrorDao;
import com.peraglobal.km.crawler.task.model.MonitorError;
import com.peraglobal.pdp.core.BaseBiz;
@Service("monitorErrorBiz")
public class MonitorErrorBiz extends BaseBiz<MonitorError,MonitorErrorDao>{

	@Resource
	private MonitorErrorDao monitorErrorDao;
	/**
	 * 查询监控错误信息
	 * @param map
	 * @return
	 */
	public List<MonitorError> queryMonitorErrors(String monitorId){
		Map map = new HashMap();
		map.put("monitorId", monitorId);
		return this.monitorErrorDao.queryMonitorErrors(map);
	}
	
	/**
	 * 删除监控报错信息
	 * @param list
	 */
	public void delTaskMonitorErrorsByIds(List list){
		this.monitorErrorDao.delTaskMonitorErrorsByIds(list);
	}
}
