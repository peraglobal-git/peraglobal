package com.peraglobal.km.crawler.task.model;

import java.util.Date;

import com.peraglobal.pdp.core.model.BaseModel;

/**
 * 2015-7-2
 * @author yongqian.liu
 * @see 任务监控
 */
public class TaskJobMonitor extends BaseModel{

	private static final long serialVersionUID = 3136491627974522999L;

	private String jobId;   // 任务ID
	private String jobName;   // 任务名称
	private Integer triggerNumber;  // 已触发的次数
	private Integer exceptionNumber;  // 异常数量
	private Integer failedNumber=0;  // 采集失败条数
	private Integer fullNumber=0;  // 采集成功条数
	private Date startTime;  // 开始时间
	private Date pauseTime;  // 暂停时间
	private Date stopTime; // 结束时间
	private String jobState; // 任务的状态
	private String description; // 描述
	
	private String jobType; //采集类型
	private String jobInfo;//采集信息
	
	private String errorFlag;	//异常情况(0:无异常，1:连接错误，2：未知错误) 
	
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getJobInfo() {
		return jobInfo;
	}
	public void setJobInfo(String jobInfo) {
		this.jobInfo = jobInfo;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Integer getTriggerNumber() {
		return triggerNumber;
	}
	public void setTriggerNumber(Integer triggerNumber) {
		this.triggerNumber = triggerNumber;
	}
	public Integer getExceptionNumber() {
		return exceptionNumber;
	}
	public void setExceptionNumber(Integer exceptionNumber) {
		this.exceptionNumber = exceptionNumber;
	}
	public Integer getFailedNumber() {
		return failedNumber;
	}
	public void setFailedNumber(Integer failedNumber) {
		this.failedNumber = failedNumber;
	}
	public Integer getFullNumber() {
		return fullNumber;
	}
	public void setFullNumber(Integer fullNumber) {
		this.fullNumber = fullNumber;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getPauseTime() {
		return pauseTime;
	}
	public void setPauseTime(Date pauseTime) {
		this.pauseTime = pauseTime;
	}
	public Date getStopTime() {
		return stopTime;
	}
	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}
	public String getJobState() {
		return jobState;
	}
	public void setJobState(String jobState) {
		this.jobState = jobState;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getErrorFlag() {
		return errorFlag;
	}
	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}

}
