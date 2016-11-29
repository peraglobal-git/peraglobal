package com.peraglobal.km.webservice.model;

import java.io.Serializable;

/**
 * webService采集传输附件的实体类
 * @author hongbo.guo
 *
 */
public class AttachmentMeta implements Serializable{
	
	/**
	 *  序列化
	 */
	private static final long serialVersionUID = -3987425957275174904L;

	//附件id
	private String fileId;
	
	//附件文件名
	private String fileName;
	
	//附件文件大小
	private  long fileSize;
	
	//附件服务器地址
	private String fileServerPath;
	
	//附件处理的txt mongo中的id
	private String txtId;

	//附件处理的txt 从mongo获取的txt附件下载到km服务器上的路径
	private String txtPath;
	
	//入库后的知识ID（sequence）
	private String knowledgeId;
	
	//采集源id（用SourceId设定）
	private String sourceId;
	
	//附件类型（固定值：KMKNOWLEDGE）
	private String type;

	public String getTxtPath() {
		return txtPath;
	}

	public void setTxtPath(String txtPath) {
		this.txtPath = txtPath;
	}

	public String getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(String knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getTxtId() {
		return txtId;
	}

	public void setTxtId(String txtId) {
		this.txtId = txtId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
	}
}
