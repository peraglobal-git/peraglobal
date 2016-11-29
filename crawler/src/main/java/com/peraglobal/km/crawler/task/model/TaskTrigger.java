package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

import com.peraglobal.pdp.core.model.BaseModel;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see 定时任务
 */
public class TaskTrigger extends BaseModel implements Serializable{

	private static final long serialVersionUID = 4258076840136186680L;
	
	private String jobId; // 任务 ID
	private String jobName; // 任务名字
	private Integer triggerType; // 类型
	private String triggerSpace; // 间隔
	private String triggerState; // 状态
	private String triggerFew; // 第几周
	private String triggerDay; // 天
	private String triggerMonth; // 月
	private String triggerWeek; // 周
	private String triggerDate; // 日期
	private String startTime; // 开始时间
	private String stopTime; // 结束时间
	private String description; // 描述
	
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
	public Integer getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}
	public String getTriggerSpace() {
		return triggerSpace;
	}
	public void setTriggerSpace(String triggerSpace) {
		this.triggerSpace = triggerSpace;
	}
	public String getTriggerState() {
		return triggerState;
	}
	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
	}
	public String getTriggerFew() {
		return triggerFew;
	}
	public void setTriggerFew(String triggerFew) {
		this.triggerFew = triggerFew;
	}
	public String getTriggerDay() {
		return triggerDay;
	}
	public void setTriggerDay(String triggerDay) {
		this.triggerDay = triggerDay;
	}
	public String getTriggerMonth() {
		return triggerMonth;
	}
	public void setTriggerMonth(String triggerMonth) {
		this.triggerMonth = triggerMonth;
	}
	public String getTriggerWeek() {
		return triggerWeek;
	}
	public void setTriggerWeek(String triggerWeek) {
		this.triggerWeek = triggerWeek;
	}
	public String getTriggerDate() {
		return triggerDate;
	}
	public void setTriggerDate(String triggerDate) {
		this.triggerDate = triggerDate;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
