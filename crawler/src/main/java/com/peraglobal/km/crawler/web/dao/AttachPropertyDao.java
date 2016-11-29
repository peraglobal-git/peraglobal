package com.peraglobal.km.crawler.web.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;

@CacheSetting
public interface AttachPropertyDao extends BaseDao<AttachProperty>{
	
	public List<AttachProperty> getAttachPropertyByTaskId(Map map);
	/**
	 * 功能：删除附件属性
	 * <p> 作者：段政 2016-2-25 
	 * @param list
	 */
	public void delAttachPropertyByIds(List list);
}
