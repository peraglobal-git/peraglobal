package com.peraglobal.km.mongo.biz;

import java.io.InputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.peraglobal.km.mongo.dao.MgBaseDao;

public abstract class MgBaseBiz<T> {
	@Autowired
	private MgBaseDao<T> mgBaseDao;
	// 新增一条数据
	public void insert(T entity) {
		this.mgBaseDao.insert(entity);
	}
	//保存或更新
	public void save(T entity) {
		this.mgBaseDao.save(entity);
	}

	public void deleteById(String id) {
		this.mgBaseDao.deleteById(id);	
	}

	public void update(Query query, Update update) {
		this.mgBaseDao.update(query, update);
	}
	@Deprecated
	public void update(T entity) {
		this.mgBaseDao.update(entity);
	}

	public List<T> findByCondition(Query query) {
		return this.mgBaseDao.findByCondition(query);
	}

	public List<T> findList(int skip, int limit) {
		return this.mgBaseDao.findList(skip, limit);
	}

	public T findOneById(String id) {
		return (T) this.mgBaseDao.findOneById(id);
	}

	public long findByCount(Query query) {
		return this.mgBaseDao.findByCount(query);
	}

	public List<T> findAll() {
		return this.mgBaseDao.findAll();
	}
	
	// 条件分页查询
	public List<T> findByCondition(Criteria criteria,int skip, int limit){
		return this.mgBaseDao.findByCondition(criteria, skip, limit);
	}
	
	public GridFSFile storeFile(InputStream input,String fileName){
		return this.mgBaseDao.storeFile(input, fileName);
	}
	
	public GridFSDBFile findFileById(String id){
		return this.mgBaseDao.findFileById(id);
	}
	
	public void deleteFiles(ObjectId objectId){
		this.mgBaseDao.deleteFiles(objectId);
	}
}
