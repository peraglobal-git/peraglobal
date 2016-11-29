package com.peraglobal.km.crawler.web.model;


import java.io.Serializable;

import com.peraglobal.pdp.core.model.BaseModel;



public class Picture extends BaseModel implements Serializable{

	private static final long serialVersionUID = -5786639792886088149L;
	
	private String taskId;
	private String datumID;
	private String name;
	private String isFail;
	private String path;
	private String type;

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getDatumID() {
		return datumID;
	}
	public void setDatumID(String datumID) {
		this.datumID = datumID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIsFail() {
		return isFail;
	}
	public void setIsFail(String isFail) {
		this.isFail = isFail;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
