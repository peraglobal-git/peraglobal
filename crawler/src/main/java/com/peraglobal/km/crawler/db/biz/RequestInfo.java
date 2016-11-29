package com.peraglobal.km.crawler.db.biz;

import java.io.File;
import java.util.Map;

import org.springframework.http.HttpRequest;


public class RequestInfo {
  private final String configFile;//rule-*-config.xml
  private final HttpRequest request;
  private final String taskId;//quartz 任务ID
  private final String taskState;//任务状态

  private final String metaDateFilePath;
  private int fetchSize;
  private final int threadNum; 
  private final String spiderRecordFilePath;
  public final  int logRate;//记录断点日志频率
  public String dbRuleXml;//db采集规则
  private final String dataTempPath;//临时文件路径
  


/**
   * 请求参数封装
   * @param request
   * @param requestParams
   */
  public RequestInfo(HttpRequest request, Map<String,Object> params) {
    this.request = request;
    this.taskId = (String)params.get("taskId");
    Object configFileObj = params.get("configFile");
    if(null!=configFileObj){
    	 this.configFile =(String)configFileObj;
    }else{
    	this.configFile =DBRULEDIR+File.separator+"rule"+LINE+"gecko"+LINE+"config.xml";
    }
    this.dataTempPath = DIR+File.separator+TEMPDIR;
    this.metaDateFilePath =  (String)params.get("metaDateFilePath");
    
   Object obj = params.get("spiderRecordFilePath");
   if(null==obj){
	   this.spiderRecordFilePath = DIR+File.separator+LOGDIR+File.separator+TASKMark+LINE;
   }else{
	   this.spiderRecordFilePath =  (String)params.get("spiderRecordFilePath")+File.separator+TASKMark+LINE;
   }
    
    Object fetchSizeObj = params.get("fetchSize");
    if(null!=fetchSizeObj){
    	this.fetchSize = Integer.parseInt(String.valueOf(fetchSizeObj));
    }else{
    	this.fetchSize = 500;
    }
    
    this.taskState =String.valueOf( params.get("taskState"));
    
    Object threadNumObj = params.get("threadNum");
    if(null!=threadNumObj){
    	this.threadNum = Integer.parseInt(String.valueOf(threadNumObj));
    }else{
    	this.threadNum  = 1;
    }
    Object logRateObj = params.get("logRate");
    if(null!=logRateObj){
    	this.logRate = Integer.parseInt(String.valueOf(logRateObj));
    }else{
    	this.logRate  = 1;
    }
    
    this.dbRuleXml =  (String)params.get("dbRuleXml");
    
    
  }

public String getDataTempPath() {
	return dataTempPath;
}

public String getDbRuleXml() {
	return dbRuleXml;
}

public int getLogRate() {
	return logRate;
}

public String getSpiderRecordFilePath() {
	return spiderRecordFilePath;
}


public int getThreadNum() {
	return threadNum;
}

public String getMetaDateFilePath() {
	return metaDateFilePath;
}

  public HttpRequest getRequest() {
	return request;
}
public String getConfigFile() {
    return configFile;
  }
public String getTaskId() {
	return taskId;
}
public String getTaskState() {
	return taskState;
}
public int getFetchSize() {
	return fetchSize;
}

public void setFetchSize(int fetchSize) {
	this.fetchSize = fetchSize;
}
public static final String LINE = "-";
public static final String DIR = "C:/kmCrawler"+File.separator+"db-import-data";
public static final String DBRULEDIR = "dbrule";
public static final String TEMPDIR = "db-temp-file";
public static final String LOGDIR = "db-breakpointlog";
public static final String TASKMark = "task-mark";

}