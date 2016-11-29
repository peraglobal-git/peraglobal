package com.peraglobal.km.crawler.db.biz;

import static com.peraglobal.km.crawler.db.biz.DataImportException.SEVERE;
import static com.peraglobal.km.crawler.db.biz.DataImportException.wrapAndThrow;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peraglobal.km.crawler.db.biz.MetaDataBuilder.MetaDataWrapper;
import com.peraglobal.km.crawler.db.model.DBSpiderRecord;
import com.peraglobal.km.crawler.db.model.Entity;
import com.peraglobal.km.crawler.db.model.SpiderConfiguration;
/**
 * 实体处理器
 * @author hadoop
 */
public class SqlEntityProcessor extends EntityProcessor{
  private static final Logger LOG = LoggerFactory.getLogger(SqlEntityProcessor.class);
  protected boolean isFirstInit = true;
  protected String entityName;
  protected Context context;
  protected DataSource dataSource;
  private Map<String, Map<String,String>> dataSourceRule;
  protected CacheSupport cacheSupport = null;
  private String dataSourceName;
  protected Iterator<Map<String, Object>> rowIterator;
  protected String query;  
  protected Entity entity;
  private int  fetchSize;//默认查询记录数
  private int  markSize ;//断点标记
  private List<String> spiderLog;
  private String spiderRecordFilePath;
  private String taskId;
  public final static String PUASE 	= "puase";
  public final static String STOP 	= "stop";
  public int logRate;//记录日志频率

public SqlEntityProcessor(Entity entity,SpiderConfiguration config){
	 this.query = entity.getQuery();
	 this.entity = entity;
	 this.dataSourceName = config.getDataSourceName();
	 this.dataSourceRule = config.getDataSourcesRule();
	 this.dataSource = getDataSourceInstance(dataSourceName);
	 this.entityName = entity.getName();
	 this.fetchSize = config.getRequestInfo().getFetchSize();
	 this.spiderRecordFilePath = config.getRequestInfo().getSpiderRecordFilePath();
	 this.taskId = config.getRequestInfo().getTaskId();
	 this.logRate = config.getRequestInfo().getLogRate();
	 
	//解析当前任务断点标记
	 analysisDBSpiderRecord();
	
  }

public String getTaskId() {
	return taskId;
}
public void setTaskId(String taskId) {
	this.taskId = taskId;
}
public String getSpiderRecordFilePath() {
	return spiderRecordFilePath;
}
/**
 *  format: taskId entityName firstSize fetchSize
 *  example: 2015-06-1116:35:09   xxx xxx xxx xxx 
 */
public void analysisDBSpiderRecord(){
	//加载
	loadDBSpiderRecord(taskId);
	if(spiderLog!=null){
		String[] strs = null;
		for(String line:spiderLog){
			strs = line.split("\t");
			if(null!=strs&&!"".equals(strs)){
				if(strs[1].equals(taskId)){
					this.markSize = Integer.parseInt(strs[3]);
					this.fetchSize = Integer.parseInt(strs[4]);
					break;
				}
			}
		}
	}
}
public void  loadDBSpiderRecord(String taskId) {
	  if(DBSpiderRecord.existsRecord(spiderRecordFilePath+taskId+DBSpiderRecord.LOGFILETYPE)){
		 this.spiderLog = DBSpiderRecord.readRecord(spiderRecordFilePath+taskId+DBSpiderRecord.LOGFILETYPE);
	  }
}
public void mark(int count,String taskState,MetaDataWrapper metaData){
	  this.setMarkSize(count);
	  String spiderLogFileFullPath = spiderRecordFilePath+taskId+DBSpiderRecord.LOGFILETYPE;
	  String message ="\t"+ taskId+"\t"+entityName+"\t"+ markSize+"\t"+fetchSize;
	  if(PUASE.equals(taskState)){
		  DBSpiderRecord.writeRecord(spiderLogFileFullPath,message); 
	  }else if(STOP.equals(taskState)){
		 DBSpiderRecord.removeRecord(spiderLogFileFullPath,taskId);
		 metaData.deleteDir(new File(metaData.getDataFileTempPath()));
	  }else if(markSize%logRate==0){
		  DBSpiderRecord.writeRecord(spiderLogFileFullPath,message); 
	  }
	  
}
/**
	  * 实例化dataSource
	  * @param key
	  * @param name <datasource name="gecko"/>
	  * @return
	  */
	 public DataSource getDataSourceInstance(String dataSourceName) {
		    DataSource dataSrc  = new JdbcDataSource();
		    try {
		      Properties initProps = new Properties();
		      //从规则中获取datasource配置
		      Map<String,String>  dsMap =dataSourceRule.get(dataSourceName);
		      for (String key : dsMap.keySet()) {
		    	   initProps.put(key,dsMap.get(key) );
		     }
		      //初始化dataSource实例
		      dataSrc.init(initProps);
		    } catch (Exception e) {
		    	  throw new DataImportException(SEVERE, "Failed to initialize DataSource: " + dataSourceName);
		    }
		    return dataSrc;
		  }

@Override
  public void init(Context context) {
    this.context = context;
    
    if (isFirstInit) {
        firstInit(context);
      }
    if(cacheSupport!=null) {
        cacheSupport.initNew();
      }   
  }
 
  /**
   * first time init call. do one-time operations here
   * it's necessary to call it from the overridden method,
   */
  protected void firstInit(Context context) {
	  //entityName = context.getEntityAttribute("name");
      initCache(context);
      isFirstInit = false;
  }
  protected void initCache(Context context) {
      String cacheImplName = CacheSupport.CACHE_IMPL;
      if (cacheImplName != null ) {
        cacheSupport = new CacheSupport(cacheImplName);
      }
  }
  protected void initQuery(String q) {
    try {
	    JdbcDataSource jdbcDataSource = null;
		if(dataSource instanceof JdbcDataSource){
			jdbcDataSource = (JdbcDataSource)dataSource;
			this.query = q;
			rowIterator = jdbcDataSource.getData(query,markSize,fetchSize);
			
		}
    } catch (DataImportException e) {
    	throw new DataImportException(DataImportException.SEVERE, e); //throw e;
    } catch (Exception e) {
      LOG.error( "The query failed '" + q + "'", e);
      throw new DataImportException(DataImportException.SEVERE, e);
    }
  }

  @Override
  public Map<String, Object> nextRow() {    
    if (rowIterator == null) {
      String q = getQuery();
      initQuery(q);
    }
    return getNext();
  }

  public String getQuery() {
    String queryString = context.getEntityAttribute(QUERY);
    if (Context.FULL_DUMP.equals(context.currentProcess())) {
      return queryString+" order by "+entity.getPk()+" asc";
    }
    return null;
  }



  public static final String QUERY = "query";
  protected Map<String, Object> getNext() {
	  if (rowIterator == null)
          return null;
        if (rowIterator.hasNext())
          return rowIterator.next();
	  //以下是启用缓存机制，由于缓存会导致数据采集不完整，暂时不用缓存机制
        if(cacheSupport==null) {
	        try {
	          if (rowIterator == null)
	            return null;
	          if (rowIterator.hasNext())
	            return rowIterator.next();
	          query = null;
	          rowIterator = null;
	          return null;
	        } catch (Exception e) {
	          query = null;
	          rowIterator = null;
	          wrapAndThrow(DataImportException.WARN, e);
	          return null;
	        }
	      } else  {
	    	  return cacheSupport.getCacheData(context, query, rowIterator);
	      }  
	  }
  public DataSource getDataSource() {
	return dataSource;
}
public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
}
public String getDataSourceName() {
	return dataSourceName;
}
  public Entity getEntity() {
	return entity;
  }
  public String getEntityName() {
	return entityName;
}
	public void setQuery(String query) {
		this.query = query;
	}
  public int getFetchSize() {
		return fetchSize;
	}
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}
	public int getMarkSize() {
		return markSize;
	}
	public void setMarkSize(int markSize) {
		this.markSize = markSize;
	}
@Override
  public void destroy() {
    query = null;
  }

}
