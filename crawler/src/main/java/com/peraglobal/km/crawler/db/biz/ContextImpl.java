package com.peraglobal.km.crawler.db.biz;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peraglobal.km.crawler.db.biz.MetaDataBuilder.MetaDataWrapper;

/**
 * An implementation for the Context
 */
public class ContextImpl extends Context {
  protected EntityProcessor ep;
  private DataSource ds;
  private DataImporter dataImporter;
  private MetaDataBuilder metaDataBuilder;
  private Exception lastException;
  private Map<String, Object> entitySession, globalSession,dataSourceSession;
  private String currProcess;
  private MetaDataWrapper metaDataWrapper;


public ContextImpl(EntityProcessor ep,DataSource ds, MetaDataBuilder metaDataBuider,Map<String, Object> global, String currProcess) {
    this.ep = ep;
    this.metaDataBuilder = metaDataBuider;
    this.ds = ds;
    if (metaDataBuider != null) {
      dataImporter = metaDataBuider.dataImporter;
    }
    this.globalSession = global;
    this.currProcess = currProcess;
  }
/**
 * @param name entity attribute name
 */
  @Override
  public String getEntityAttribute(String name) {
	  if(ep instanceof SqlEntityProcessor){
		  SqlEntityProcessor sep =  (SqlEntityProcessor)ep;
		  return sep==null || sep.getEntity() == null ? null : sep.getEntity().getAllAttributes().get(name);
	  }
	  return null;
  
  }

  public DataSource getDs() {
	return ds;
}
public void setDs(DataSource ds) {
	this.ds = ds;
}
public DataImporter getDataImporter() {
	return dataImporter;
}
public void setDataImporter(DataImporter dataImporter) {
	this.dataImporter = dataImporter;
}
public String getCurrProcess() {
	return currProcess;
}
public void setCurrProcess(String currProcess) {
	this.currProcess = currProcess;
}
@Override
  public List<Map<String, String>> getAllEntityFields() {
	  if(ep instanceof SqlEntityProcessor){
		  SqlEntityProcessor sep =  (SqlEntityProcessor)ep;
		  return sep==null || sep.getEntity() == null ? Collections.EMPTY_LIST : sep.getEntity().getAllFieldsList();
	  }
	  return Collections.EMPTY_LIST;
  }

  @Override
  public DataSource getDataSource() {
    if (ds != null) return ds;
    if(ep==null) { return null; }
    if(ep instanceof SqlEntityProcessor){
		  SqlEntityProcessor sep =  (SqlEntityProcessor)ep;
		  if (ep!=null && sep.getDataSource() == null) {
			  sep.setDataSource(((SqlEntityProcessor) ep).getDataSourceInstance(((SqlEntityProcessor) ep).getDataSourceName()));
		    }
		  return sep.getDataSource();
    }
    return null;
  }

  @Override
  public void setSessionAttribute(String name, Object val, String scope) {
    if(name == null) {
      return;
    }
    if (Context.SCOPE_ENTITY.equals(scope)) {
      if (entitySession == null) {
    	  entitySession = new HashMap<String, Object>();
      }
      entitySession.put(name, val);
    } else if (Context.SCOPE_GLOBAL.equals(scope)) {
      if (globalSession != null) {
        globalSession.put(name, val);
      }
    } else if (Context.SCOPE_DATASOURCE.equals(scope)) {
        if (dataSourceSession == null) {
        	dataSourceSession = new HashMap<String, Object>();
        }
        dataSourceSession.put(name, val);
    } 
  }

  @Override
  public Object getSessionAttribute(String name, String scope) {
	  
    if (Context.SCOPE_ENTITY.equals(scope)) {
      if (entitySession != null){
    	  return entitySession.get(name);
      }
    } else if (Context.SCOPE_GLOBAL.equals(scope)) {
      if (globalSession != null) {
        return globalSession.get(name);
      }
    } else if (Context.SCOPE_DATASOURCE.equals(scope)) {
    	if (dataSourceSession != null) {
    	 return dataSourceSession.get(name);
    	}
    } 
    return null;
  }

  MetaDataBuilder getMetaDataBuilder(){
    return metaDataBuilder;
  }
  public void setMetaDataBuilder(MetaDataBuilder metaDataBuilder) {
	this.metaDataBuilder = metaDataBuilder;
 }
  public MetaDataWrapper getMetaDataWrapper() {
	return metaDataWrapper;
}
public void setMetaDataWrapper(MetaDataWrapper metaDataWrapper) {
	this.metaDataWrapper = metaDataWrapper;
}
  @Override
  public String currentProcess() {
    return currProcess;
  }
  
  @Override
  public EntityProcessor getEntityProcessor() {
  	return ep;
  }
  public Exception getLastException() { return lastException; }
  public void setLastException(Exception lastException) {this.lastException = lastException; }

}
