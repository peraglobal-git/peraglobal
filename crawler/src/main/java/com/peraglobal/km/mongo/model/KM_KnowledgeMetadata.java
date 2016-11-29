package com.peraglobal.km.mongo.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.DBObject;

@Document(collection = "KM_KnowledgeMetadata")
public class KM_KnowledgeMetadata {
	@Id
	private String id;
	private String taskId;
	private String fileLocation;
	private String md5;
	private String datumId;
	private String transferFlag;
	private Date createDate;
	private DBObject kvs;
	private String isFull;	//  0:完整/ 1:不完整
	private String isUpdate;//  0:已编辑/ 1：未编辑
	private String isChecked;// 0:校验通过/1:校验未未通过/-1删除  
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public DBObject getKvs() {
		return kvs;
	}
	public void setKvs(DBObject kvs) {
		this.kvs = kvs;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getDatumId() {
		return datumId;
	}
	public void setDatumId(String datumId) {
		this.datumId = datumId;
	}
	public String getTransferFlag() {
		return transferFlag;
	}
	public void setTransferFlag(String transferFlag) {
		this.transferFlag = transferFlag;
	}
	public String getIsFull() {
		return isFull;
	}
	public void setIsFull(String isFull) {
		this.isFull = isFull;
	}
	public String getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}
	public String getIsUpdate() {
		return isUpdate;
	}
	public void setIsUpdate(String isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	
}
