package com.peraglobal.km.crawler.web.biz;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.web.dao.AttachPropertyDao;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.pdp.core.BaseBiz;

/**
 * 2015-12-30
 * @author xiaodan.jing
 * web采集登录信息业务逻辑接口
 */
@Service
public class AttachPropertyBiz extends BaseBiz<AttachProperty,AttachPropertyDao> {

	@Resource
	private AttachPropertyDao attachPropertyDao;
	
	public AttachProperty getAttachPropertyByTaskId(Map map){
		List<AttachProperty> list = attachPropertyDao.getAttachPropertyByTaskId(map);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 功能：删除附件属性
	 * <p> 作者：段政 2016-2-25 
	 * @param list
	 */
	public void delAttachPropertyByIds(List list){
		try {
			attachPropertyDao.delAttachPropertyByIds(list);
		} catch (Exception e) {
			throw e;
		}
	}
}
