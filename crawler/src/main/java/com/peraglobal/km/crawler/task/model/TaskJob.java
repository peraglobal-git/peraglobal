package com.peraglobal.km.crawler.task.model;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.pdp.core.metadata.OrderBy;
import com.peraglobal.pdp.core.model.BaseModel;
@OrderBy("(case  when jobState ='1'then '0'else '5' end),jobState asc,createDate desc")
public class TaskJob extends BaseModel{

	private String name; // 任务名称
	private String groupId; // 组ID
	private String groupName; // 组名称
	private String jobState; // 任务的状态
	private String registerModel; // 模块（1：采集 2：转换 3：传输）
	private String registerType; // 类型（1：web 2：DB 3：local 4：extract 5：transfer）
	private String description; // 描述
	private boolean hand; // 是否为手动任务，为持久化字段
	private long datumCount;		//采集总数
	private int attachmentCount;	// 附件总数
	private int knowledgeMetadataCount;		//转换数量
	private String connectId;			// 转换任务关联的采集任务id，传输任务关联的转换任务id
	private String jobPriority; // 优先级
	private AttachProperty attachProperty;
	private Integer priority=0; //优先级（0:普通，1：优先）
	private Integer automatic = 1; //采集方式：0自动，1手动
	private String knowledgeType;			//知识形态id
	private String knowledgeTypeName;		//知识形态名称
	private String knowledgeModel;		//知识模板id
	private String knowledgeModelName;	//知识模板名称
	private String systemId;					//传输系统id
	private String systemName;				//传输系统名称
	private long taskjobDataCount;//该采集任务采集的数据量（数据库中没有的字段）
	private String sourceName;//该采集任务对应的知识源名称
	private String hasNewData; //是否有新数据
	private String extractList;//抽取规则
	
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public long getTaskjobDataCount() {
		return taskjobDataCount;
	}
	public void setTaskjobDataCount(long taskjobDataCount) {
		this.taskjobDataCount = taskjobDataCount;
	}
	public Integer getAutomatic() {
		return automatic;
	}
	public void setAutomatic(Integer automatic) {
		this.automatic = automatic;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	private String sourceId;
	
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getJobState() {
		return jobState;
	}
	public void setJobState(String jobState) {
		this.jobState = jobState;
	}
	public String getRegisterModel() {
		return registerModel;
	}
	public void setRegisterModel(String registerModel) {
		this.registerModel = registerModel;
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
	public boolean isHand() {
		return hand;
	}
	public void setHand(boolean hand) {
		this.hand = hand;
	}
	public long getDatumCount() {
		return datumCount;
	}
	public void setDatumCount(long datumCount) {
		this.datumCount = datumCount;
	}
	public int getAttachmentCount() {
		return attachmentCount;
	}
	public void setAttachmentCount(int attachmentCount) {
		this.attachmentCount = attachmentCount;
	}
	public String getConnectId() {
		return connectId;
	}
	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}
	public String getJobPriority() {
		return jobPriority;
	}
	public void setJobPriority(String jobPriority) {
		this.jobPriority = jobPriority;
	}
	public AttachProperty getAttachProperty() {
		return attachProperty;
	}
	public void setAttachProperty(AttachProperty attachProperty) {
		this.attachProperty = attachProperty;
	}
	public String getKnowledgeType() {
		return knowledgeType;
	}
	public void setKnowledgeType(String knowledgeType) {
		this.knowledgeType = knowledgeType;
	}
	public String getKnowledgeTypeName() {
		return this.knowledgeTypeName;
	}
	public void setKnowledgeTypeName(String knowledgeTypeName) {
		this.knowledgeTypeName = knowledgeTypeName;
	}
	public String getKnowledgeModel() {
		return knowledgeModel;
	}
	public void setKnowledgeModel(String knowledgeModel) {
		this.knowledgeModel = knowledgeModel;
	}
	public String getKnowledgeModelName() {
		return knowledgeModelName;
	}
	public void setKnowledgeModelName(String knowledgeModelName) {
		this.knowledgeModelName = knowledgeModelName;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public int getKnowledgeMetadataCount() {
		return knowledgeMetadataCount;
	}
	public void setKnowledgeMetadataCount(int knowledgeMetadataCount) {
		this.knowledgeMetadataCount = knowledgeMetadataCount;
	}
	public String getHasNewData() {
		return hasNewData;
	}
	public void setHasNewData(String hasNewData) {
		this.hasNewData = hasNewData;
	}
	public String getExtractList() {
		return extractList;
	}
	public void setExtractList(String extractList) {
		this.extractList = extractList;
	}
	
	
	
}
