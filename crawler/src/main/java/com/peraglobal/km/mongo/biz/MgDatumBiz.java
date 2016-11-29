package com.peraglobal.km.mongo.biz;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.peraglobal.km.mongo.dao.MgDatumDao;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.pdp.core.condition.Pagination;
import com.sun.mail.util.QEncoderStream;
@Service("mgDatumBiz")
public class MgDatumBiz extends MgBaseBiz<KM_Datum> {
	@Resource
	private MgDatumDao mgDatumDao;
	
	//根据taskId统计采集数量
	public long findByTaskId (String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId));
		return mgDatumDao.findByCount(query);
	}
	
	//根据taskId统计完整数据的数量
	public long findFullByTaskId (String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId).and("isFull").is("0"));
		return mgDatumDao.findByCount(query);
	}
	//根据taskId统计不完整数据的数量
	public long findnotFullByTaskId (String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId).and("isFull").is("1"));
		return mgDatumDao.findByCount(query);
	}
	//根据taskId批量删除采集数据
	public void deleteByTaskId(String taskId){
		Query query = new Query();
		query.addCriteria(new Criteria("taskId").is(taskId));
		mgDatumDao.deleteByConditions(query);
	}
	
	//根据taskId查询采集数据
	public List<KM_Datum> findByTaskIdDatum (Query query,int skip,int limit){
		//query.with(new Sort(new Order(Direction.DESC, "birth")));  
        query.skip(skip).limit(limit);  
		return mgDatumDao.findByCondition(query);
	}
}
