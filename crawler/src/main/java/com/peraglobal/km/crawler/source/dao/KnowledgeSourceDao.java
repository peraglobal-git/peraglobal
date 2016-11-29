package com.peraglobal.km.crawler.source.dao;

import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.source.model.KnowledgeSource;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.condition.Pagination;
/**
 * 知识源Dao接口
 * @author DuanZheng
 *
 */
public interface KnowledgeSourceDao extends BaseDao<KnowledgeSource> {
	/**
	 * 模糊查询知识源列表信息
	 * @param map
	 * @return List<Source>
	 * author duanzheng
	 */
	public List<KnowledgeSource> fuzzyQueryInformationList(Map map);
	/**
	 * 功能：查询知识源列表
	 * @return List<Source>
	 * author duanzheng
	 */
	public List<KnowledgeSource> queryKnowledgeSourceList();
	/**
	 * 功能：查询知识源详细信息
	 * @param map
	 * @return List<Source>
	 * author duanzheng
	 */
	public List<KnowledgeSource> queryKnowledgeSourceDetailInfo(Map map);
	/**
	 * 模糊查询知识源列表行数
	 * @param map
	 * @return List<Source>
	 * author duanzheng
	 */
	public int fuzzyQueryInformationListCount(Map map);
	/**
	 * 查询知识源是否有关联
	 * @param map
	 * @return int
	 * author duanzheng
	 */
	public Map queryTaskJobBySourceId(Map map);
	/**
	 * 功能：倒序查询知识源列表
	 * @param map
	 * @return List<KnowledgeSource>
	 * author duanzheng
	 */
	public List<KnowledgeSource> findKnowledgeListOrderType(Pagination pagination);
	/**
	 * 功能：正序查询知识源列表
	 * @param map
	 * @return List<KnowledgeSource>
	 * author duanzheng
	 */
	public List<KnowledgeSource> findKnowledgeListOrderTypeAsc(Pagination pagination);
}
