package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

import com.peraglobal.pdp.core.model.BaseModel;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see WEB 采集任务-XML 解析装换为下拉菜单
 */
public class TaskOption implements Serializable{
	
	private static final long serialVersionUID = -793682128118697826L;
	
	private String id; // 主键
	private String text; // input name
	private String value; // 值
	
	public String getid() {
		return id;
	}
	public void setid(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
