package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see WEB 采集任务创建实例
 */
public class JobForm implements Serializable{
	
	private static final long serialVersionUID = 2762950307141537326L;
	private String id;
	private String groupId;
	private String name;
	private String registerType;
	private String description;
	
	private String seed_url;			// WEB采集 种子地址
	private String[] seed_urls;			// WEB采集 种子地址
	private String list_url;			// WEB采集 列表页地址规则
	private String list_rule_type;		// WEB采集 列表页地址规则类型
	private String detail_url;			// WEB采集 详情页地址规则
	private String detail_rule_type;	// WEB采集 详情页地址规则类型
	private String detail_more_url;			// WEB采集 详情多页地址规则
	private String detail_more_rule_type;	// WEB采集 详情多页地址规则类型
	private String list_content;		// WEB采集 列表区域名称
	private String list_content_Val;	// WEB采集 列表区域规则
	private String list_content_type;	// WEB采集 列表区域规则类型
//	private String[] detail_content;	// WEB采集 内容项 名称
//	private String[] detail_content_Val;// WEB采集 内容项 取值规则
//	private String[] detail_content_type;// WEB采集 内容项 规则类型
	
	// 附件
	private String attachment_content;		// 附件区域
	private String attachment_content_Val;	// 附件区域规则
	private String attachment_content_type;	// 附件区域规则类型
	
	private String loginFlag;			//是否需要登录
	//登录信息
	private String login_url;			// 登录请求地址
	private String doLoginAction;		// 登录提交地址
	private String username_key;		// 登录用户名 form name
	private String username_value;		// 登录用户名 form value
	private String password_key;		// 登录密码 form name
	private String password_value;		// 登录密码 form value
	
	private int threadNum;				// 允许的线程数
	private String chartset;			// 网站编码
	
	private String connectId;			// 转换任务关联的采集任务id，传输任务关联的转换任务id
	private String[] fieldNames;			// 采集任务的元数据字段
	private String[] knowledgeKeys;		// 知识需求的元数据字段
	
//	private String[] ipAddress;			// WEB采集 代理服务器地址
//	private String[] ipPort;			// WEB采集 代理服务器端口

	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegisterType() {
		return registerType;
	}
	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeed_url() {
		return seed_url;
	}
	public void setSeed_url(String seed_url) {
		this.seed_url = seed_url;
	}
	public String getList_url() {
		return list_url;
	}
	public void setList_url(String list_url) {
		this.list_url = list_url;
	}
	public String getList_rule_type() {
		return list_rule_type;
	}
	public void setList_rule_type(String list_rule_type) {
		this.list_rule_type = list_rule_type;
	}
	public String getDetail_url() {
		return detail_url;
	}
	public void setDetail_url(String detail_url) {
		this.detail_url = detail_url;
	}
	public String getDetail_rule_type() {
		return detail_rule_type;
	}
	public void setDetail_rule_type(String detail_rule_type) {
		this.detail_rule_type = detail_rule_type;
	}
	public String getList_content() {
		return list_content;
	}
	public void setList_content(String list_content) {
		this.list_content = list_content;
	}
	public String getList_content_Val() {
		return list_content_Val;
	}
	public void setList_content_Val(String list_content_Val) {
		this.list_content_Val = list_content_Val;
	}
	public String getList_content_type() {
		return list_content_type;
	}
	public void setList_content_type(String list_content_type) {
		this.list_content_type = list_content_type;
	}
	
	public String getLogin_url() {
		return login_url;
	}
	public void setLogin_url(String login_url) {
		this.login_url = login_url;
	}
	public String getUsername_key() {
		return username_key;
	}
	public void setUsername_key(String username_key) {
		this.username_key = username_key;
	}
	public String getUsername_value() {
		return username_value;
	}
	public void setUsername_value(String username_value) {
		this.username_value = username_value;
	}
	public String getPassword_key() {
		return password_key;
	}
	public void setPassword_key(String password_key) {
		this.password_key = password_key;
	}
	public String getPassword_value() {
		return password_value;
	}
	public void setPassword_value(String password_value) {
		this.password_value = password_value;
	}
	public String getDoLoginAction() {
		return doLoginAction;
	}
	public void setDoLoginAction(String doLoginAction) {
		this.doLoginAction = doLoginAction;
	}
	public String getLoginFlag() {
		return loginFlag;
	}
	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}
	public String getAttachment_content() {
		return attachment_content;
	}
	public void setAttachment_content(String attachment_content) {
		this.attachment_content = attachment_content;
	}
	public String getAttachment_content_Val() {
		return attachment_content_Val;
	}
	public void setAttachment_content_Val(String attachment_content_Val) {
		this.attachment_content_Val = attachment_content_Val;
	}
	public String getAttachment_content_type() {
		return attachment_content_type;
	}
	public void setAttachment_content_type(String attachment_content_type) {
		this.attachment_content_type = attachment_content_type;
	}
	public int getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
	public String getChartset() {
		return chartset;
	}
	public void setChartset(String chartset) {
		this.chartset = chartset;
	}
	public String[] getSeed_urls() {
		return seed_urls;
	}
	public void setSeed_urls(String[] seed_urls) {
		this.seed_urls = seed_urls;
	}
	public String[] getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
	public String[] getKnowledgeKeys() {
		return knowledgeKeys;
	}
	public void setKnowledgeKeys(String[] knowledgeKeys) {
		this.knowledgeKeys = knowledgeKeys;
	}
	public String getConnectId() {
		return connectId;
	}
	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDetail_more_url() {
		return detail_more_url;
	}
	public void setDetail_more_url(String detail_more_url) {
		this.detail_more_url = detail_more_url;
	}
	public String getDetail_more_rule_type() {
		return detail_more_rule_type;
	}
	public void setDetail_more_rule_type(String detail_more_rule_type) {
		this.detail_more_rule_type = detail_more_rule_type;
	}
	
}
