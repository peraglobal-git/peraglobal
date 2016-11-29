package com.peraglobal.km.crawler.extract.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.peraglobal.km.crawler.extract.model.KnowledgeMetadata;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.pdp.core.BaseDao;

public interface KnowledgeMetadataDao extends BaseDao<KnowledgeMetadata>{
	/**
	 * 功能：根据任务ID和Md5获取元数据
	 * 作者：井晓丹 2016-1-6
	 * @param map
	 * @return
	 */
	public List<KnowledgeMetadata> getKnowledgeMetadataByMd5(Map map);
	/**
	 * 功能：根据任务采集元数据ID获取
	 * 作者：井晓丹 2016-5-4
	 * @param map
	 * @return
	 */
	public List<KnowledgeMetadata> getKnowledgeMdByDatumId(Map map);
	/**
	 * 功能：根据taskId查询转换数量
	 * 作者 段政 2016-3-16（周三）
	 * @param map
	 * @return int
	 */
	public int queryExtractNumberBytaskId(Map map);
	/**
	 * 功能：批量删除转换数据
	 * 作者 段政 2016-3-21
	 * @param map
	 * @return
	 */
	public int deleteConvertDataByList(List list);
	/**
	 * 功能：批量更新传输成功标识
	 * 作者 井晓丹 2016-5-4
	 * @param map
	 * @return
	 */
	public void updateKnowledgeMdTransferSucess(List list);
	/**
	 * 功能：批量更新传输失败标识
	 * 作者 井晓丹 2016-5-4
	 * @param map
	 * @return
	 */
	public void updateKnowledgeMdTransferFailed(List list);
}
