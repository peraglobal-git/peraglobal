package com.peraglobal.km.crawler.source.model;

public class KnowledgeDetail {
	private String knowledgeModel;//知识模板ID
	private String knowledgeModelName; //知识模板名称
	private int taskcount;	   //采集任务数量
	private int sourcecount;   //该知识模板下的知识源数量
	private int taskdatacount; //单个采集任务的采集数据量
	private int taskdatacountall;//采集数据总量
	public String getKnowledgeModel() {
		return knowledgeModel;
	}
	public void setKnowledgeModel(String knowledgeModel) {
		this.knowledgeModel = knowledgeModel;
	}
	public String getKnowledgeModelName() {
		return knowledgeModelName;
	}
	public void setKnowledgeModelName(String knowledgeModelName) {
		this.knowledgeModelName = knowledgeModelName;
	}
	public int getTaskcount() {
		return taskcount;
	}
	public void setTaskcount(int taskcount) {
		this.taskcount = taskcount;
	}
	public int getSourcecount() {
		return sourcecount;
	}
	public void setSourcecount(int sourcecount) {
		this.sourcecount = sourcecount;
	}
	public int getTaskdatacount() {
		return taskdatacount;
	}
	public void setTaskdatacount(int taskdatacount) {
		this.taskdatacount = taskdatacount;
	}
	public int getTaskdatacountall() {
		return taskdatacountall;
	}
	public void setTaskdatacountall(int taskdatacountall) {
		this.taskdatacountall = taskdatacountall;
	}
	
}
