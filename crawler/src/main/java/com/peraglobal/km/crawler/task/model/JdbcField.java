package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see DB 采集任务数据库连接规则属性赋对象
 * 
 */
public class JdbcField implements Serializable {

	private static final long serialVersionUID = 3640887107054017308L;
	
	private String fieldId;
	private String fieldName;
	private String fieldType;
	private String fieldLength;
	
	
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(String fieldLength) {
		this.fieldLength = fieldLength;
	}
	
	
}
