package com.peraglobal.km.crawler.task.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.task.dao.TransferConfigDao;
import com.peraglobal.km.crawler.task.model.TransferConfig;
import com.peraglobal.pdp.core.BaseBiz;
/**
 * 传输设置Biz类
 * @author xiaodan.jing
 *
 */
@Service("tcBiz")
public class TransferConfigBiz extends BaseBiz<TransferConfig, TransferConfigDao> {
	@Resource
	TransferConfigDao transferConfigDao;
	
	public TransferConfig findByTaskId(String taskId){
		Map map=new HashMap();
		map.put("taskId", taskId);
		return transferConfigDao.findByTaskId(map);
	}
	/**
	 * 功能：删除任务规则
	 * <p> 作者：段政 2016-2-25
	 * @param list
	 */
	public void delConfigByIds(List list){
		try {
			transferConfigDao.delConfigByTaskIds(list);
		} catch (Exception e) {
			throw e;
		}
	}
}
