package com.peraglobal.km.crawler.web.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;

/**
 * 2016-1-8
 * @author xiaodan.jing
 * 元数据数据处理类
 */
@CacheSetting
public interface DatumDao extends BaseDao<Datum>{
	/**
	 * 功能：根据任务ID和url获取元数据
	 * 作者：井晓丹 2016-1-6
	 * @param map
	 * @return
	 */
	public List<Datum> getDatumByUrl(Map map);
	
	/**
	 * 功能：根据任务ID和PK值获取元数据
	 * 作者：井晓丹 2016-1-6
	 * @param map
	 * @return
	 */
	public List<Datum> getDatumByPK(Map map);
	/**
	 * 功能：查询采集数量
	 * 作者：段政 2016-2-3
	 * @param map
	 * @return int
	 */
	public int numberOfQueriesToCollect(Map map);
	/**
	 * 功能：删除采集数据
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delDatumByids(List list);
	/**
	 * 功能：根据taskid查询数据总数
	 * @author wangxiaoming
	 * @param map
	 * @return
	 */
	public int queryTaskDataCountBytaskId(Map map);
}
