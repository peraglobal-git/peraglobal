package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

import com.peraglobal.pdp.core.model.BaseModel;

import oracle.sql.BLOB;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see 任务类型注册
 */
public class TaskRegister extends BaseModel implements Serializable{
	
	private static final long serialVersionUID = 3091377410632772103L;
	
	private String registerName; // 名称
	private String registerModel; // 模块
	private String registerTpye; // 类型
	private BLOB ruleXml; // xml
	private String rulePath; // xml 的路径
	private String description; // 描述
	
	public String getRegisterName() {
		return registerName;
	}
	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}
	public String getRegisterModel() {
		return registerModel;
	}
	public void setRegisterModel(String registerModel) {
		this.registerModel = registerModel;
	}
	public String getRegisterTpye() {
		return registerTpye;
	}
	public void setRegisterTpye(String registerTpye) {
		this.registerTpye = registerTpye;
	}
	public BLOB getRuleXml() {
		return ruleXml;
	}
	public void setRuleXml(BLOB ruleXml) {
		this.ruleXml = ruleXml;
	}
	public String getRulePath() {
		return rulePath;
	}
	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}