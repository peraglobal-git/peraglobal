package com.peraglobal.km.crawler.task.model;

import java.io.Serializable;

/**
 * 2015-12-16
 * @author xiaoming.wang
 * @see 任务相关静态 key 类
 */
public class JobModel implements Serializable{

	private static final long serialVersionUID = 1422297957320214525L;

	public static final String JOB_KEY = "job_key"; // job_key
	public static final String RULE_KEY = "rule_key"; // job_key
	public static final String ATTACH_PROPERTY_KEY = "attachProperty_key"; // job_key
	
	// 任务状态
	public static final String STATE_READY = "0"; // 就绪任务
	public static final String STATE_STRAT = "1"; // 开始任务
	public static final String STATE_PAUSE = "2"; // 暂停任务
	public static final String STATE_STOP = "3"; // 停止任务
	public static final String STATE_END = "4"; // 完成任务
	public static final String STATE_WAIT = "5"; // 等待任务
	
	// 任务模块
	public static final String MODEL_CRAWLER = "1"; // 采集任务模块
	public static final String MODEL_EXTRACT = "2"; // 转换任务模块
	public static final String MODEL_TRANSFER = "3"; // 传输任务模块
	
	// 任务类型
	public static final String TYPE_WEB = "1"; // web 采集任务
	public static final String TYPE_DB = "2"; // DB 采集任务
	public static final String TYPE_LOCAL = "3"; // 本地采集任务
	public static final String TYPE_EXTRACT = "4"; // 转换任务
	public static final String TYPE_TRANSFER = "5"; // 传输任务

	// 本地上传excle和压缩文件
	public static final String LOCAL_FILE = "local_file"; // 上传的文件
	public static final String LOCAL_EXPERT = "local_expert"; // 专家采集
	public static final String LOCAL_METAKEY = "metaKey";	// 合并字段标识
	
	// 触发组拼 ID 使用
	public static final String ID_START = "start"; // 触发器组拼 ID 使用
	public static final String ID_STOP = "stop"; // 触发器组拼 ID 使用
	
	// 触发器类型常量
	public static final int TRIGGER_DAY = 1; // 天
	public static final int TRIGGER_WEEK = 2; // 周
	public static final int TRIGGER_MONTH = 3; // 月
	public static final int TRIGGER_ONE = 4; // 触发一次
	
	// 触发器状态
	public static final String TRIGGER_ON = "1"; // 启用
	public static final String TRIGGER_OFF = "2"; // 禁用
	
	// 任务异常情况
	public static final String JOB_NORMAL = "0"; // 任务正常
	public static final String JOB_CONECTION_ERROR = "1";	 // 任务异常
	public static final String JOB_UNKNOW_ERROR = "2";	 // 任务异常
	
}