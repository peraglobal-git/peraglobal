package com.peraglobal.km.crawler.extract.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.extract.dao.KnowledgeMetadataDao;
import com.peraglobal.km.crawler.extract.model.KnowledgeMetadata;
import com.peraglobal.km.mongo.biz.MgKnowledgeMetadataBiz;
import com.peraglobal.pdp.core.BaseBiz;
/**
 * 2016-3-15
 * @author xiaodan.jing
 * 转换后数据处理类
 */
/**
 * 2016-3-15
 * @author xiaodan.jing
 * 转换后数据处理类
 */
@Service("knowledgeMetadataBiz")
public class KnowledgeMetadataBiz extends BaseBiz<KnowledgeMetadata,KnowledgeMetadataDao> {
	@Resource
	private KnowledgeMetadataDao knowledgeMetadataDao;
	@Resource
	private MgKnowledgeMetadataBiz mgKnowledgeMetadataBiz;
	
	/**
	 * 查询转换元数据
	 * @param state 状态值
	 * @return 任务集合
	 */
	public boolean getKnowledgeMetadataByMd5(String taskId,String md5) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("taskId",taskId);
		map.put("md5",md5);
		List<KnowledgeMetadata> list = knowledgeMetadataDao.getKnowledgeMetadataByMd5(map);
		if(list!=null && list.size() >0){
			return true;
		}
		return false;
	}
	/**
	 * 查询转换元数据
	 * @param state 状态值
	 * @return 任务集合
	 */
	public KnowledgeMetadata getKnowledgeMdByDatumId(String taskId,String datumId) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("datumId",datumId);
		map.put("taskId",taskId);
		List<KnowledgeMetadata> list = knowledgeMetadataDao.getKnowledgeMdByDatumId(map);
		if(list!=null && list.size() >0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 功能：根据taskId查询转换数量
	 * 作者 段政 2016-3-16（周三）
	 * @param map
	 * @return int
	 */
	public int queryExtractNumberBytaskId(Map map){
		try {
			return knowledgeMetadataDao.queryExtractNumberBytaskId(map);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：批量删除转换数据
	 * 作者 段政 2016-3-21
	 * @param map
	 * @return
	 */
	public void deleteConvertDataByList(List<String> list){
		/*try {
			return knowledgeMetadataDao.deleteConvertDataByList(list);
		} catch (Exception e) {
			throw e;
		}*/
		try {
			for (String id : list) {
				mgKnowledgeMetadataBiz.deleteByTaskId(id);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：批量更新传输成功标识
	 * 作者 井晓丹 2016-5-4
	 * @param map
	 * @return
	 */
	public void updateKnowledgeMdTransferSucess(List list){
		try {
			knowledgeMetadataDao.updateKnowledgeMdTransferSucess(list);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 功能：批量更新传输失败标识
	 * 作者 井晓丹 2016-5-4
	 * @param map
	 * @return
	 */
	public void updateKnowledgeMdTransferFailed(List list){
		try {
			knowledgeMetadataDao.updateKnowledgeMdTransferFailed(list);
		} catch (Exception e) {
			throw e;
		}
	}
	
	
}
