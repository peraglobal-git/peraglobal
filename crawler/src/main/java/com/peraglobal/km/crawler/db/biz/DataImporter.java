package com.peraglobal.km.crawler.db.biz;

import static com.peraglobal.km.crawler.db.biz.DataImportException.SEVERE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peraglobal.km.crawler.db.model.SpiderConfiguration;

/**
 * @author hadoop
 *
	rule-*-config.xml
	
<?xml version="1.0" encoding="UTF-8" ?>
<dataConfig>  
    <dataSource name="gecko" type="JdbcDataSource" driver="oracle.jdbc.driver.OracleDriver"  
        url="jdbc:oracle:thin:@192.168.50.96:1521:gecko"  
        user="gecko" password="gecko"  batchSize="5" /> 
        <document name="gecko">  
        	<entity name="datatest" query="select * from DATATEST t"  deltaQuery=""	pk="" >
          		<field name = "id" as="唯一标识" type="-5"/> 
          		<field name = "name" as="记录名" type="12"/> 
          		<field name = "file_data" as="数据" type="2004"  filetype="file_type"  filename="file_name"/>
          		<field name = "file_name" as="文件名" type="12"/> 
          		<field name = "file_type" as="文件类型" type="1"/> 
          	</entity>
          	
        </document>
</dataConfig> 

 *
 */
public class DataImporter {

	private static final Logger log = LoggerFactory.getLogger(DataImporter.class);
	private SpiderConfiguration config;
	public MetaDataBuilder metaDataBuilder;
	
	public DataImporter(SpiderConfiguration conf){
		this.config = conf;
	}
	/**
	  * 执行导入
	  * @param requestParams
	  */
	  public void  doImport() {
		  this.doImport(config.getRequestInfo());
	  }  
	 /**
	  * 执行导入
	  * @param requestParams
	  */
	  private void  doImport(RequestInfo params) {
		    log.info("Starting Import");
		    try {
		      new MetaDataBuilder(this,params);
		    } catch (Exception e) {
		    	throw new DataImportException(SEVERE, "Exception  Import", e);
		    }
	  }  
	 
	 public SpiderConfiguration getConfig() {
		return config;
	 }
}
