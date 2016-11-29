package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;
import java.util.List;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see WEB 采集任务-XML 解析装换为html标签
 */
public class TaskConfig implements Serializable{

	private static final long serialVersionUID = -7311138856144099313L;
	
	private String name; // input name
	private String size; // input size
	private String label; // 显示 label
	private String value; // 值
	private String add; // 是否自增列
	private String del; // 是否自删除
	private String flag; 
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	List<TaskOption> taskOption;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAdd() {
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public List<TaskOption> getTaskOption() {
		return taskOption;
	}

	public void setTaskOption(List<TaskOption> taskOption) {
		this.taskOption = taskOption;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	

}
