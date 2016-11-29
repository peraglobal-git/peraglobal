package com.peraglobal.km.crawler.extract.model;
import java.io.Serializable;

import com.peraglobal.pdp.core.cache.annotation.CacheSetting;
import com.peraglobal.pdp.core.metadata.OrderBy;
import com.peraglobal.pdp.core.model.BaseModel;
@OrderBy("createDate asc")
public class KnowledgeMetadata extends BaseModel implements Serializable{

	private static final long serialVersionUID = -8204827566856028124L;
	
	private String fileId;
	private String taskId;
	private byte[] metadata;
	private String fileLocation;
	private String md5;
	private String datumId;
	private String transferFlag;
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public byte[] getMetadata() {
		return metadata;
	}
	public void setMetadata(byte[] metadata) {
		this.metadata = metadata;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getDatumId() {
		return datumId;
	}
	public void setDatumId(String datumId) {
		this.datumId = datumId;
	}
	public String getTransferFlag() {
		return transferFlag;
	}
	public void setTransferFlag(String transferFlag) {
		this.transferFlag = transferFlag;
	}
	
	
}
