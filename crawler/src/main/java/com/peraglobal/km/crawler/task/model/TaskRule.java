package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

import com.peraglobal.pdp.core.model.BaseModel;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see 采集任务规则
 */
public class TaskRule extends BaseModel implements Serializable{

	private static final long serialVersionUID = -1042611738866505678L;

	private String jobId; // 任务ID
	private String ruleType; // 类型
	private byte[] ruleContent; // 内容
	private String description; // 描述
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public byte[] getRuleContent() {
		return ruleContent;
	}
	public void setRuleContent(byte[] ruleContent) {
		this.ruleContent = ruleContent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
