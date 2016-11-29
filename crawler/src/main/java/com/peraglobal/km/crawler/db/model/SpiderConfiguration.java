package com.peraglobal.km.crawler.db.model;

import static com.peraglobal.km.crawler.db.biz.DataImportException.SEVERE;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.peraglobal.km.crawler.db.biz.ConfigParseUtil;
import com.peraglobal.km.crawler.db.biz.DataImportException;
import com.peraglobal.km.crawler.db.biz.RequestInfo;
import com.peraglobal.km.crawler.util.ConverterUtil;

/**
 * <p>
 * Mapping for rule-*-config.xml, *-log.log
 * </p>
 */
public class SpiderConfiguration extends Configuration{
	
  private static final Logger log = LoggerFactory.getLogger(SpiderConfiguration.class);
  private ReentrantLock importLock = new ReentrantLock();
  
  private  List<Entity> entities; //所有entities
  private  Map<String, Map<String,String>> dataSourcesRule;
  private String dataSourceName;//数据源名称
  private RequestInfo requestInfo;

  
public SpiderConfiguration(Map<String,Object> params){
	  initConfiguration(params);
  }
  /**
	  * 加载配置项
	  * @param params
	  * @return
	  */
	 public boolean initConfiguration(Map<String,Object> params){
		requestInfo = new RequestInfo(null,params);
		return  this.initConfiguration(requestInfo);
	 }
	 public RequestInfo getRequestInfo() {
		 return requestInfo;
	 }
	/**
	  * 加载配置项
	  * @param params
	  * @param defaultParams
	  * @return
	  * @throws IOException
	  */
	 public boolean initConfiguration(RequestInfo params) {
		  if (importLock.tryLock()) {
		      boolean success = false;
		      try {       
		    
		    	//this.dataSourceName = params.getDataSourceName();
			    //load rule xml
			    String configStr=null;
			    if(null==params.getDbRuleXml()){
			    	//test
			    	String dataconfigFile = params.getConfigFile();
			        ClassLoader classLoader = getClass().getClassLoader();  
			        InputStream in = classLoader.getResourceAsStream(dataconfigFile);
					configStr = IOUtils.toString(in);
			    }else{
			    	configStr = params.getDbRuleXml();
			    }
			    InputSource is =null;
			    if(null!=configStr){
			    	is = new InputSource(new StringReader(configStr));
			    }
		        if(is!=null) {          
		          loadDataConfig(is);
		          success = true;
		        }    
		  
		      } catch (IOException e) {
		    	  throw new DataImportException(SEVERE,
			              "load rule xml file problem: " + e.getMessage(), e);
		      } finally {
		        importLock.unlock();
		      }
		      return success;
		    } else {
		      return false;
		    }
		  }
	public String getDataSourceName() {
		return dataSourceName;
	}
	/**
	 * 加载规则xml
	 * @param configFile InputSource
	 * @return
	 */
	public void loadDataConfig(InputSource configFile) {
	    try {
	    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        try {
//	          dbf.setXIncludeAware(true);
	          dbf.setNamespaceAware(true);
	        } catch( UnsupportedOperationException e ) {
	        	log.warn( "XML parser doesn't support XInclude option" );
	        }
	      
	      DocumentBuilder builder = dbf.newDocumentBuilder();
	      Document document;
	      try {
	        document = builder.parse(configFile);
	      } finally {
	        IOUtils.closeQuietly(configFile.getByteStream());
	      }
	     readFromXml(document);
	      log.info("Data Configuration loaded successfully");
	    } catch (Exception e) {
	      throw new DataImportException(SEVERE,
	              "Data Config problem: " + e.getMessage(), e);
	    }
	  }
	 public void  readFromXml(Document xmlDocument) {
		 
		    Map<String, Map<String,String>> dataSources = new HashMap<String, Map<String,String>>();
		    //check xml file base attribute
		    NodeList dataConfigTags = xmlDocument.getElementsByTagName("dataConfig");
		    if(dataConfigTags == null || dataConfigTags.getLength() == 0) {
		      throw new DataImportException(SEVERE, "the root node '<dataConfig>' is missing");
		    }
		   /* Element e = (Element) dataConfigTags.item(0);
		    List<Element> documentTags = ConfigParseUtil.getChildNodes(e, "document");
		    if (documentTags.isEmpty()) {
			      throw new DataImportException(SEVERE,
			              "configuration file must have one <document> node.");
			    }
		    //初始化实体配置
		    List<Entity> modEntities = new ArrayList<Entity>();
		    Element documentElement =  documentTags.get(0);
		    List<Element> l = ConfigParseUtil.getChildNodes(documentElement, "entity");
		    for (Element et : l) {
		        Entity entity = new Entity(et);
		        modEntities.add(entity);
		      }
		    this.entities = Collections.unmodifiableList(modEntities);*/
		    
		    
		    Element e = (Element) dataConfigTags.item(0);
		    //初始化实体配置
		    List<Entity> modEntities = new ArrayList<Entity>();
		    List<Element> l = ConfigParseUtil.getChildNodes(e, "entity");
		    for (Element et : l) {
		        Entity entity = new Entity(et);
		        modEntities.add(entity);
		      }
		    this.entities = Collections.unmodifiableList(modEntities);
		    
		
		    //初始化datasource configuration   <dataSource name="" type="JdbcDataSource" driver=""  url=""   user="" password="" /> 
		    List<Element> dataSourceTags = ConfigParseUtil.getChildNodes(e, ConfigNameConstants.DATA_SRC);
		    if (!dataSourceTags.isEmpty()) {
		      for (Element element : dataSourceTags) {
		        Map<String,String> p = new HashMap<String,String>();
		        HashMap<String, String> attrs = ConfigParseUtil.getAllAttributes(element);
		        for (Map.Entry<String, String> entry : attrs.entrySet()) {
		          p.put(entry.getKey(), entry.getValue());
		          if("name".equals(entry.getKey())){
		        	  this.dataSourceName = entry.getValue();
		          }
		        }
		        dataSources.put(p.get("name"), p);
		      }
		    }
		    this.dataSourcesRule = Collections.unmodifiableMap(dataSources);
		    
 }

  public List<Entity> getEntities() {
    return entities;
  }
  public Map<String,Map<String,String>> getDataSourcesRule() {
    return dataSourcesRule;
  }

}