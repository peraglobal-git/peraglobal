package com.peraglobal.km.crawler.web.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.peraglobal.pdp.core.metadata.OrderBy;
import com.peraglobal.pdp.core.model.BaseModel;
@OrderBy("createDate asc")
public class Datum extends BaseModel implements Serializable{
	

	private static final long serialVersionUID = -7543534558398748669L;
	
	private String taskId;
	private String url;
	private byte[] kvs;
	private byte[] htmlMeta;
	private Timestamp expire;
	private String isFail;
	private String hasAttachment;
	private String dbpk;
	private String md5;
	private String[] attachmentIds;
	private String datumId; //详情页多页情况，关联第一页

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
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
	public byte[] getKvs() {
		return kvs;
	}
	public void setKvs(byte[] kvs) {
		this.kvs = kvs;
	}
	public Timestamp getExpire() {
		return expire;
	}
	public void setExpire(Timestamp expire) {
		this.expire = expire;
	}
	public String getIsFail() {
		return isFail;
	}
	public void setIsFail(String isFail) {
		this.isFail = isFail;
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
	
}
