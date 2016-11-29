package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

/**
* 2015-12-16
* @author xiaoming.wang
* @see DB 采集任务数据库连接规则对象
*/
public class JdbcEnity implements Serializable{

	private static final long serialVersionUID = 1122216955259403944L;
	
	// 数据库连接信息
	private String jdbcId;
	private String name;
	private String type;
	private String driver;
	private String url;
	private String user;
	private String password;
	private String query;
	private String entityName;
	private String pkid;
	
	// 表字段信息
	private String[] fieldAs;
	private String[] fieldType;
	private String[] fieldName;
	
	// 附件信息
	private String attachmentAs; 
	private String attachmentType;
	private String attachmentName;
	private String attachmentFileType;
	private String attachmentFileName;
	
	
	public String getPkid() {
		return pkid;
	}
	public void setPkid(String pkid) {
		this.pkid = pkid;
	}
	public String getJdbcId() {
		return jdbcId;
	}
	public void setJdbcId(String jdbcId) {
		this.jdbcId = jdbcId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String[] getFieldAs() {
		return fieldAs;
	}
	public void setFieldAs(String[] fieldAs) {
		this.fieldAs = fieldAs;
	}
	public String[] getFieldType() {
		return fieldType;
	}
	public void setFieldType(String[] fieldType) {
		this.fieldType = fieldType;
	}
	public String[] getFieldName() {
		return fieldName;
	}
	public void setFieldName(String[] fieldName) {
		this.fieldName = fieldName;
	}
	public String getAttachmentAs() {
		return attachmentAs;
	}
	public void setAttachmentAs(String attachmentAs) {
		this.attachmentAs = attachmentAs;
	}
	public String getAttachmentType() {
		return attachmentType;
	}
	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
	public String getAttachmentFileType() {
		return attachmentFileType;
	}
	public void setAttachmentFileType(String attachmentFileType) {
		this.attachmentFileType = attachmentFileType;
	}
	public String getAttachmentFileName() {
		return attachmentFileName;
	}
	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}
	
	
}
