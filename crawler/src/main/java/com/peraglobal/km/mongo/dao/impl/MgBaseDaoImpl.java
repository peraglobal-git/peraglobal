package com.peraglobal.km.mongo.dao.impl;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.peraglobal.km.mongo.MongoFactory;
import com.peraglobal.km.mongo.dao.MgBaseDao;

public class MgBaseDaoImpl<T> implements MgBaseDao<T> {

	@Autowired
    private MongoTemplate mongoTemplate;
	@Autowired
    private MongoFactory mongoFactory;
	@Autowired
	GridFsTemplate gridFsTemplate;
    private Class<T> entityClass ;

	@SuppressWarnings({ "unchecked", "unused" })
	public MgBaseDaoImpl(){
		if(this.entityClass == null){
			Type type = getClass().getGenericSuperclass();
			Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
			this.entityClass = (Class<T>) trueType;
		}
	}
    //增加一条数据
	public void insert(T entity) {
		this.mongoTemplate.insert(entity);
	}
	
	//增加一条数据
	public void save(T entity) {
		this.mongoTemplate.save(entity);
	}

	//根据条id删除一条数据
	public void deleteById(String id) {
		Query query = new Query();  
        query.addCriteria(new Criteria("_id").is(id));  
        this.mongoTemplate.remove(query, entityClass);
	}
	
	//根据条件删除数据
	public void deleteByConditions(Query query) {
		this.mongoTemplate.remove(query, entityClass);
	}
	
	//根据条件局部更新
	public void update(Query query, Update update) {
		this.mongoTemplate.updateMulti(query, update, entityClass);  
	}

	//整体更新
	public void update(T entity) {
		Map<String, Object> map = mongoFactory.converObjectToParams(entity);
        Query query = new Query();
        query.addCriteria(new Criteria("id").is(map.get("id")));
        Update update = (Update) map.get("update");
        this.mongoTemplate.updateMulti(query, update, entityClass);
	}

	//条件查询
	public List<T> findByCondition(Query query) {
		return this.mongoTemplate.find(query, entityClass);
	}

	//分页查询
	public List<T> findList(int skip, int limit) {
		 Query query = new Query();  
//       query.with(new Sort(new Order(Direction.ASC, "_id")));  
         query.skip(skip).limit(limit);  
         return this.mongoTemplate.find(query, entityClass);  
	}

	//单个查询
	public T findOneById(String id) {
		Query query = new Query();  
        query.addCriteria(new Criteria("id").is(id));  
        return this.mongoTemplate.findOne(query, entityClass); 
	}

	//统计数据量
	public long findByCount(Query query) {
		return this.mongoTemplate.count(query, entityClass);  
	}

	//查询所有
	public List<T> findAll() {
		return this.mongoTemplate.findAll(entityClass);
	}
	//条件分页查询
	public List<T> findByCondition(Criteria criteria, int skip, int limit) {
		Query query = new Query();
		query.addCriteria(criteria);
//		query.with(new Sort(new Order(Direction.ASC, "_id")));  
        query.skip(skip).limit(limit); 
		return this.mongoTemplate.find(query, entityClass);
	}
	
	public GridFSFile storeFile(InputStream content, String fileName) {
		return this.gridFsTemplate.store(content, fileName);
	}

	public GridFSDBFile findFileById(String id) {
		Query query = new Query();  
        query.addCriteria(new Criteria("_id").is(new ObjectId(id)));
        return this.gridFsTemplate.findOne(query);
	}
	
	//根据ObjectId删除文件
	public void deleteFiles(ObjectId objectId) {
		Query query = new Query();
		query.addCriteria(new Criteria("_id").is(objectId));
		this.gridFsTemplate.delete(query);
	}
	
	
}
