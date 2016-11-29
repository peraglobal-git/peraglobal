package com.peraglobal.km.crawler.web.model;

import com.peraglobal.pdp.core.model.BaseIncrementIdModel;

public class Rule extends BaseIncrementIdModel{

	
	private String taskId;
	private String name;
	private String genre;
	private String type;
	private String optFlag;
	private String rule;
	
	// 规则应用
	public static final String SEED_URL = "seedurl";
	public static final String LIST_URL_OPT = "listurl";
	public static final String DETAIL_URL_OPT = "detailurl";
	public static final String DETAIL_MORE_URL_OPT = "detailmoreurl";
	public static final String ATTACHMENT_URL_OPT = "attachementurl";
	public static final String LIST_CONTENT_OPT = "listcontent";
	public static final String DETAIL_CONTENT_OPT = "detailcontent";
	
	// 规则类型
	public static final String RULE_TYPE_REGEX = "regex";
	public static final String RULE_TYPE_XPATH = "xpath";
	public static final String RULE_TYPE_CSS = "css";
	public static final String RULE_TYPE_URL = "url";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOptFlag() {
		return optFlag;
	}
	public void setOptFlag(String optFlag) {
		this.optFlag = optFlag;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
