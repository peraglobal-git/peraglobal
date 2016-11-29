package com.peraglobal.km.crawler.web.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;

/**
 * 2016-1-8
 * @author xiaodan.jing
 * 附件数据处理类
 */
@CacheSetting
public interface AttachmentCrawlerDao extends BaseDao<AttachmentCrawler>{
	/**
	 * 功能：根据任务ID和mongo objectId获取附加
	 * 作者：井晓丹 2016-3-22
	 * @param map
	 * @return
	 */
	public List<AttachmentCrawler> getAttachmentByFilePath(Map map);
	/**
	 * 功能：根据元数据id获取附件
	 * 作者：井晓丹 2016-3-22
	 * @param map
	 * @return
	 */
	public List<AttachmentCrawler> findAllByDatumId(Map map);
	/**
	 * 功能：删除采集附件
	 * 作者 段政 2016-3-21
	 * @param list
	 * @return
	 */
	public int deleteATTACHMENTCRAWLERByList(List list);
	
}
