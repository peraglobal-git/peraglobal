package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see 本地 采集任务规则属性赋对象
 */
public class LocalEntity implements Serializable {
	
	private static final long serialVersionUID = -4007918974852263352L;
	private String local_id;
	private String job_id;
	private  MultipartFile mFile;
	private String expert;
	private String metaKey;
	private String fileName;
	private String excelProperties;
	
	public String getLocal_id() {
		return local_id;
	}
	public void setLocal_id(String local_id) {
		this.local_id = local_id;
	}
	public String getJob_id() {
		return job_id;
	}
	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}
	public MultipartFile getmFile() {
		return mFile;
	}
	public void setmFile(MultipartFile mFile) {
		this.mFile = mFile;
	}
	public String getExpert() {
		return expert;
	}
	public void setExpert(String expert) {
		this.expert = expert;
	}
	public String getMetaKey() {
		return metaKey;
	}
	public void setMetaKey(String metaKey) {
		this.metaKey = metaKey;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getExcelProperties() {
		return excelProperties;
	}
	public void setExcelProperties(String excelProperties) {
		this.excelProperties = excelProperties;
	}
	
}
