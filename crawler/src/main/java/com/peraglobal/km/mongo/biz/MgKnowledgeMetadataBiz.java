package com.peraglobal.km.mongo.biz;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.peraglobal.km.mongo.dao.MgKnowledgeMetadataDao;
import com.peraglobal.km.mongo.model.KM_KnowledgeMetadata;
@Service("mgKnowledgeMetadataBiz")
public class MgKnowledgeMetadataBiz extends MgBaseBiz<KM_KnowledgeMetadata> {
	@Resource
	private MgKnowledgeMetadataDao mgKnowledgeMetadataDao;
	
	//根据taskId统计转换数量
	public long findByTaskId (String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId));
		return mgKnowledgeMetadataDao.findByCount(query);
	}
	//根据taskId统计完整数据的数量
	public long findFullByTaskId (String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId).and("isFull").is("0"));
		return mgKnowledgeMetadataDao.findByCount(query);
	}
	//根据taskId统计不完整数据的数量
	public long findnotFullByTaskId (String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId).and("isFull").is("1"));
		return mgKnowledgeMetadataDao.findByCount(query);
	}
	//根据taskId批量删除转换数据
	public void deleteByTaskId(String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId));
		mgKnowledgeMetadataDao.deleteByConditions(query);
	}
	
	// 批量修改数据传输状态
	public void updateBatchTransferFlag(String transferFlag,List ids){
		Query query = new Query();
		query.addCriteria(new Criteria("id").in(ids));
		Update update = new Update();
		update.set("transferFlag", transferFlag);
		mgKnowledgeMetadataDao.update(query, update);
	}
	
	//根据taskId查询采集数据
	public List<KM_KnowledgeMetadata> findByTaskIdDatum (Query query,int skip,int limit){
        query.skip(skip).limit(limit);  
		return mgKnowledgeMetadataDao.findByCondition(query);
	}
}
