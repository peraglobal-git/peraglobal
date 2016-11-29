package com.peraglobal.km.webservice.model;

import java.io.Serializable;
import java.util.List;

import com.peraglobal.pdp.common.id.IDGenerate;

/**
 * webService中 采集传输任务信息和知识的实体类
 * 
 * @author hongbo.guo
 * 
 */
public class KnowledgeMeta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2473292410003189551L;

	public KnowledgeMeta(){}
	public KnowledgeMeta(String taskId, String taskName,
			String knowledgePatternId, String knowledgePatternName,
			String templateManageId, String templateManageName,String sourceId,String sourceName) {
		this.taskId = taskId;
		this.taskName = taskName;
		this.knowledgePatternId = knowledgePatternId;
		this.knowledgePatternName = knowledgePatternName;
		this.templateManageId = templateManageId;
		this.templateManageName = templateManageName;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
	}
	/* 共享的任务ID, 用于任务表、 知识表、知识备份表 、任务批次表的共享 */
	private String taskUUID;

	// 元数据ID
	private String metaDataId;

	// 任务ID
	private String taskId;

	// 任务名称
	private String taskName;

	// 知识形态Id
	private String knowledgePatternId;

	// 知识形态名称
	private String knowledgePatternName;

	// 知识模板ID
	private String templateManageId;

	// 知识模板名称
	private String templateManageName;

	// 批次ID
	private String batchId;

	/**
	 * 知识源id
	 */
	private String sourceId;
	/**
	 * 知识源名称
	 */
	private String sourceName;

	// 元数据体 (知识)
	private String xml;

	private List<AttachmentMeta> attachment;

	public String getMetaDataId() {
		return metaDataId;
	}

	public void setMetaDataId(String metaDataId) {
		this.metaDataId = metaDataId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getKnowledgePatternId() {
		return knowledgePatternId;
	}

	public void setKnowledgePatternId(String knowledgePatternId) {
		this.knowledgePatternId = knowledgePatternId;
	}

	public String getKnowledgePatternName() {
		return knowledgePatternName;
	}

	public void setKnowledgePatternName(String knowledgePatternName) {
		this.knowledgePatternName = knowledgePatternName;
	}

	public String getTemplateManageId() {
		return templateManageId;
	}

	public void setTemplateManageId(String templateManageId) {
		this.templateManageId = templateManageId;
	}

	public String getTemplateManageName() {
		return templateManageName;
	}

	public void setTemplateManageName(String templateManageName) {
		this.templateManageName = templateManageName;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public List<AttachmentMeta> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<AttachmentMeta> attachment) {
		this.attachment = attachment;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTaskUUID() {
		return taskUUID;
	}

	public void setTaskUUID(String taskUUID) {
		this.taskUUID = taskUUID;
	}

}
