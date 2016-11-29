package com.peraglobal.km.crawler.source.model;

import java.util.Date;

import com.peraglobal.pdp.core.metadata.OrderBy;
import com.peraglobal.pdp.core.model.BaseModel;
/**
 * 功能：知识源实体类
 * <p>段政 2015-1-13
 * @author duanzheng 
 *
 */
@OrderBy("createDate desc")
public class KnowledgeSource extends BaseModel{
	private String linkType;//知识源类型：1 web 2 db 3 local
	private String name;//知识元名称
	private String username;//用户名
	private String password;//密码
	private String linkContent;//连接内容
	private String linkState;//连接状态 0成功，1异常
	private int taskjobCount;//该知识源下的采集任务数量（数据库中没有的字段）
	private int taskDataCount;//该知识源下的采集数据总量（数据库中没有的字段）
	
	public int getTaskjobCount() {
		return taskjobCount;
	}
	public void setTaskjobCount(int taskjobCount) {
		this.taskjobCount = taskjobCount;
	}
	public int getTaskDataCount() {
		return taskDataCount;
	}
	public void setTaskDataCount(int taskDataCount) {
		this.taskDataCount = taskDataCount;
	}
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLinkContent() {
		return linkContent;
	}
	public void setLinkContent(String linkContent) {
		this.linkContent = linkContent;
	}
	public String getLinkState() {
		return linkState;
	}
	public void setLinkState(String linkState) {
		this.linkState = linkState;
	}
}
