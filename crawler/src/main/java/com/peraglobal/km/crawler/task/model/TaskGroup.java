package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.peraglobal.pdp.core.model.BaseModel;

/**
 * 2015-12-16 2015-12-16
 * 
 * @author xiaoming.wang
 * @see 任务组对象
 */
public class TaskGroup extends BaseModel {

	
	private static final long serialVersionUID = 7348896195790605050L;

	private String name; // 组名字
	private String pid; // 父节点 Id
	private String description; // 描述
	private String type; // 类型（1：采集 2：转换 3：传输）
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	
	
    
}
