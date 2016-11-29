package com.peraglobal.km.mongo.dao;

import java.io.InputStream;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
@Transactional
public interface MgBaseDao<T> {
	// 增加一条数据
	public abstract void insert(T entity);
	
	//增加一条数据
	public void save(T entity);

	// 根据条id删除一条数据
	public abstract void deleteById(String id);
	
	//根据条件删除数据
	public abstract void deleteByConditions(Query query);

	// 根据条件局部更新
	public abstract void update(Query query, Update update);

	// 整体更新
	public abstract void update(T entity);

	// 条件查询
	public abstract List<T> findByCondition(Query query);
	
	// 条件分页查询
	public abstract List<T> findByCondition(Criteria criteria,int skip, int limit);

	// 分页查询
	public abstract List<T> findList(int skip, int limit);

	// 单个查询
	public abstract T findOneById(String id);

	// 统计数据量
	public abstract long findByCount(Query query);

	// 查询所有
	public abstract List<T> findAll();
	
	// 存储附件
	public abstract GridFSFile storeFile(InputStream input,String fileName);
	
	//查询文件
	public abstract GridFSDBFile findFileById(String id);
	
	//根据ObjectId删除文件
	public abstract void deleteFiles(ObjectId objectId);
}
