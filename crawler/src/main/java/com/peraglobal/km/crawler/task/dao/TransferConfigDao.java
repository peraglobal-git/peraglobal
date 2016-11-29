package com.peraglobal.km.crawler.task.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.task.model.TransferConfig;
import com.peraglobal.pdp.core.BaseDao;
/**
 * 任务传输配置Dao接口
 * @author DuanZheng
 *
 */
public interface TransferConfigDao extends BaseDao<TransferConfig> {
	/**
	 * 根据JobId查询 
	 * @param map JobId
	 * @return 规则实例
	 */
	public TransferConfig findByTaskId(Map map);
	/**
	 * 功能：删除任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delConfigByTaskIds(List list);
}
