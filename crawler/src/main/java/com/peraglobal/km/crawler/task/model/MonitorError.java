package com.peraglobal.km.crawler.task.model;

import com.peraglobal.pdp.core.model.BaseModel;

/**
 * 错误信息
 * 
 * @author xiaodan.jing
 * 
 */
public class MonitorError extends BaseModel {
	private String taskId;
	private String monitorId;
	private String errorType;
	private String content;
	private String createDateStr;

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateDateStr() {
		return createDateStr;
	}

	public void setCreateDateStr(String createDateStr) {
		this.createDateStr = createDateStr;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
