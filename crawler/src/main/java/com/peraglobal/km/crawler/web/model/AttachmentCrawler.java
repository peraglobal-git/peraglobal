package com.peraglobal.km.crawler.web.model;
import com.peraglobal.pdp.core.model.BaseModel;

import java.io.Serializable;

public class AttachmentCrawler extends BaseModel{

	private String taskId;
	private String datumID;
	private String name;
	private String isFail;
	private String path;
	private String type;
	private String txt;
	private String fileSize;
	
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
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
	
	
}
