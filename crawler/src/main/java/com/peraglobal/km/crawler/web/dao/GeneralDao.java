package com.peraglobal.km.crawler.web.dao;

import java.util.List;
import java.util.Map;








import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.km.crawler.web.model.Picture;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.cache.annotation.CacheSetting;

@CacheSetting
public interface GeneralDao extends BaseDao<AttachmentCrawler> {
	
	
	public String save(final AttachmentCrawler a) ;
	
	public AttachmentCrawler getAttachment(String id);
	
	public void save(final Picture a) ;
	
	public void save(final Rule a) ;
	
	public String save(final Datum d);
	
	public Datum getDatumByPK(String taskId, String pk);
	/**
	 * 功能：根据任务ID和URL获取元数据对象
	 * @param taskId
	 * @param url
	 * @return
	 */
	public Datum getDatumByUrl(String taskId, String url);
	
	public Map<String, Map<String, List<Map<String, Object>>>> getTaskRules(String taskId);

	public void save (final AttachProperty a);
	
	public AttachProperty getAttachProperty(String taskId);
	
	public void delRuleByTaskId(String taskId);
	
	public void delAttachPropertyByTaskId(String taskId);

	public void update(Datum d);

	public void update(AttachmentCrawler a);
	
	public List<Rule> getDetailKeysByTaskId(String taskId);
	
	public AttachmentCrawler getAttachmentByName(String datumId, String name);
	
	public Datum getDatumById(String datumId);
}

