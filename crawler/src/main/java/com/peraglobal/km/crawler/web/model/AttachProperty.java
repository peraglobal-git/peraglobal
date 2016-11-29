package com.peraglobal.km.crawler.web.model;


import com.peraglobal.pdp.core.model.BaseIncrementIdModel;

public class AttachProperty extends BaseIncrementIdModel{

	private static final long serialVersionUID = -4536364561854547720L;
	
	private String taskId;
	private String loginurl;
	private String doLoginAction;
	private String usernameKey;
	private String usernameValue;
	private String passwordKey;
	private String passwordValue;
	private Integer threadNum;
	private String charSet;
	private String ipAddressPort;
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getLoginurl() {
		return loginurl;
	}
	public void setLoginurl(String loginurl) {
		this.loginurl = loginurl;
	}
	public String getDoLoginAction() {
		return doLoginAction;
	}
	public void setDoLoginAction(String doLoginAction) {
		this.doLoginAction = doLoginAction;
	}
	public String getUsernameKey() {
		return usernameKey;
	}
	public void setUsernameKey(String usernameKey) {
		this.usernameKey = usernameKey;
	}
	public String getUsernameValue() {
		return usernameValue;
	}
	public void setUsernameValue(String usernameValue) {
		this.usernameValue = usernameValue;
	}
	public String getPasswordKey() {
		return passwordKey;
	}
	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}
	public String getPasswordValue() {
		return passwordValue;
	}
	public void setPasswordValue(String passwordValue) {
		this.passwordValue = passwordValue;
	}
	public Integer getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(Integer threadNum) {
		this.threadNum = threadNum;
	}
	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	public String getIpAddressPort() {
		return ipAddressPort;
	}
	public void setIpAddressPort(String ipAddressPort) {
		this.ipAddressPort = ipAddressPort;
	}
	
	
	
}
