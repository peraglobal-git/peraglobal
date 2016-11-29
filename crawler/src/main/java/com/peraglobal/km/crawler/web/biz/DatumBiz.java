package com.peraglobal.km.crawler.web.biz;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.web.dao.DatumDao;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.core.BaseBiz;
/**
 * 2016-1-8
 * @author xiaodan.jing
 * 元数据逻辑处理类
 */
@Service("datumBiz")
public class DatumBiz extends BaseBiz<Datum,DatumDao> {
	@Resource
	private DatumDao datumDao;
	@Resource
	private MgDatumBiz mgDatumBiz;
	/**
	 * 功能：根据任务ID和url获取元数据
	 * 作者：井晓丹 2016-1-6
	 * @param map
	 * @return
	 */
	public Datum getDatumByUrl(Map map){
		List<Datum> list = datumDao.getDatumByUrl(map);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 功能：根据任务ID和PK值获取元数据
	 * 作者：井晓丹 2016-1-6
	 * @param map
	 * @return
	 */
	public Datum getDatumByPK(String taskId,String dbPk){
		Map<String,String> map = new HashMap<String,String>();
		map.put("taskId", taskId);
		map.put("dbPk", dbPk);
		List<Datum> list = datumDao.getDatumByPK(map);
		if(list.size()>0){
			return (Datum)list.get(0);
		}
		return null;
	}
	/**
	 * 功能：根据任务ID和PK值获取元数据
	 * 作者：井晓丹 2016-1-6
	 * @param map
	 * @return
	 */
	public int numberOfQueriesToCollect(Map map){
		try {
			return datumDao.numberOfQueriesToCollect(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：删除采集数据
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delDatumByids(List<String> list){
	/*	try {
			datumDao.delDatumByids(list);
		} catch (Exception e) {
			throw e;
		}*/
		try{
			for (String id : list) {
				mgDatumBiz.deleteByTaskId(id);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public int queryTaskDataCountBytaskId(String taskId){
		try {
			Map map = new HashMap();
			map.put("taskId", taskId);
			return datumDao.queryTaskDataCountBytaskId(map);
		} catch (Exception e) {
			throw e;
		}
	}
}
