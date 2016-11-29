package com.peraglobal.km.mongo.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.DBObject;

@Document(collection = "KM_Datum")
public class KM_Datum {
	@Id
	private String id;
	private String taskId;
	private String url;
	private DBObject kvs;
	private byte[] htmlMeta;
	private String hasAttachment;
	private String dbpk;
	private String md5;
	private String[] attachmentIds;
	private String datumId; //详情页多页情况，关联第一页
	private Date createDate;
	private String isFull;	//  0:完整/ 1:不完整
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
	public String getHasAttachment() {
		return hasAttachment;
	}
	public void setHasAttachment(String hasAttachment) {
		this.hasAttachment = hasAttachment;
	}
	public String getDbpk() {
		return dbpk;
	}
	public void setDbpk(String dbpk) {
		this.dbpk = dbpk;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String[] getAttachmentIds() {
		return attachmentIds;
	}
	public void setAttachmentIds(String[] attachmentIds) {
		this.attachmentIds = attachmentIds;
	}
	public String getDatumId() {
		return datumId;
	}
	public void setDatumId(String datumId) {
		this.datumId = datumId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public byte[] getHtmlMeta() {
		return htmlMeta;
	}
	public void setHtmlMeta(byte[] htmlMeta) {
		this.htmlMeta = htmlMeta;
	}
	public String getIsFull() {
		return isFull;
	}
	public void setIsFull(String isFull) {
		this.isFull = isFull;
	}
	
	
}
