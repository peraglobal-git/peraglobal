package com.peraglobal.km.crawler.db.biz;

import static com.peraglobal.km.crawler.db.biz.DataImportException.SEVERE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.peraglobal.km.crawler.db.model.DBSpiderRecord;
import com.peraglobal.km.crawler.db.model.Entity;
import com.peraglobal.km.crawler.db.model.EntityField;
import com.peraglobal.km.crawler.db.model.SpiderConfiguration;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.util.CrawlerUtil;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;

/**
 * 元数据构建
 * @author hadoop
 */
public class MetaDataBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(MetaDataBuilder.class);
  DataImporter dataImporter;
  private SpiderConfiguration config;
  private final RequestInfo params;
  @Resource
  private MgDatumBiz mgDatumBiz;
//  private Mongo db;
//  private static DB mydb;
  private Map<String ,Object> session = new HashMap<String ,Object>();//全局缓存
  //static final ThreadLocal<MetaDataBuilder> INSTANCE = new ThreadLocal<>();//本地线程变量
  private final static String FILETPYEDOT = ".";
  public MetaDataBuilder(DataImporter dataImporter, RequestInfo params) {
    this.dataImporter = dataImporter;
    this.params = params;
    mgDatumBiz = AdminConfigUtils.getBean("mgDatumBiz");
   /* try {
		db = new Mongo(AppConfigUtils.get("km.mongo.address"), Integer.valueOf(AppConfigUtils.get("km.mongo.port")));
		mydb = db.getDB(AppConfigUtils.get("km.mongo.dbName"));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
    initEntityProcessor(params.getTaskId());
  }
//实例化SqlEntityProcessor
  public EntityProcessor getEntityProcessor(Entity entity,SpiderConfiguration config) {
    EntityProcessor entityProcessor = new SqlEntityProcessor(entity,config);
    return entityProcessor;
  }

 /* public void closeMongo(){
  	db.close();
  }*/
  private void initEntityProcessor(String taskId) {
	    try {
	    
	      //ConcurrentMap<String, Spider>  spiderMap = new ConcurrentHashMap<String, Spider>();
	      this.config = dataImporter.getConfig();
	      
	      //实例化EntityProcessor 
	      MetaDataBuiderSpider  metaDataBuiderSpider = null;
	      for (Entity e : config.getEntities()) {
	    	  metaDataBuiderSpider = new MetaDataBuiderSpider(e.getName(),this.getEntityProcessor(e,config));
	    	 // spiderMap.put(e.getName(),metaDataBuiderSpider);
	    	  SpiderManager.register(taskId, metaDataBuiderSpider);
	      }
	      //注册组爬虫
	      //SpiderGroupManager.registerGroup(taskId, spiderMap);
	      
	    } catch(Exception e){
//	    	throw new DataImportException(DataImportException.SEVERE,e);
	    }
}
  
  private void assemblingMetaDataFields(Entity entity, MetaDataWrapper metaData,
                         Map<String, Object> arow) {
	MetaDataField metaDataField =null;
	FileDataField fileDataField = null;
	String mongoID=null;
	List<MetaDataField> rowMetaDataFields = new ArrayList<MetaDataField>();
	List<FileDataField> rowFileDataFields = new ArrayList<FileDataField>();
    for (Map.Entry<String, Object> arowData : arow.entrySet()) {
      String key = arowData.getKey();
      Object value = arowData.getValue();
      //if (value == null)  continue;
      Set<EntityField> field = entity.getNameVsField().get(key);
        if (field != null) {
          for (EntityField f : field) {
        	  String name = f.getName();//规则field name
        	  int type = f.getType();
        	  //收集文件数据
        	  if(name.equalsIgnoreCase(key)){
	        	  if(Types.BLOB==type||Types.CLOB==type){
	        		  fileDataField = new FileDataField();
	        		//  String dataFileTempFullPath = metaData.getDataFileTempPath()+IDGenerate.uuid();
	        		  if(null!=value)  {  
	  		        	//metaData. writeToFile((InputStream)value,dataFileTempFullPath);//临时存储文件
						/*GridFS myFS = new GridFS(mydb);
						GridFSInputFile inputFile = myFS.createFile((InputStream)value,f.getFilename());
						inputFile.save();*/
	        			GridFSFile inputFile = mgDatumBiz.storeFile((InputStream)value,f.getFilename());
						fileDataField.setDataTempPath(inputFile.getId());
						mongoID=inputFile.getId().toString();
	  		        }
	        		 
	        		  fileDataField.setType(f.getType());
	        		  fileDataField.setFileNameValue(mongoID);
	        		  fileDataField.setFileNameFieldName(f.getFilename());
	        		  fileDataField.setFileTypeFieldName(f.getFiletype());
	        		 // fileDataField.setName(name);
	        		  rowFileDataFields.add(fileDataField);
	        	  }
	        	  if(FileTypes==type){
	        		  fileDataField = new FileDataField();
	        		  if(null!=value){
	        			  String url = String.valueOf(value);
	        			  String temp =url;
	        			  String fileName =url.substring(url.lastIndexOf(File.separator)+1,url.lastIndexOf(FILETPYEDOT));
	        			  String fileType =  temp.substring(temp.lastIndexOf(FILETPYEDOT),temp.length());
	        			  HttpGet httpget = HttpDownloadHandler.getHttpGet(url);
	        			  String dataFileTempFullPath = metaData.getDataFileTempPath()+CrawlerUtil.getUUID();
	        			
	        			    Map map = getInputStream(url,httpget);
							try {
								 if(null!=map){
								 }
									//metaData. writeToFile(in,dataFileTempFullPath);//临时存储文件
									fileDataField.setDataTempPath(map.get("mongoID"));
							}finally{
								HttpDownloadHandler.releaseConnection(httpget);
							}
		        		
		        		  fileDataField.setType(type);
		        		  fileDataField.setName(map.get("fileName").toString());
		        		  fileDataField.setFileNameValue(map.get("mongoID").toString()); 
		        		  fileDataField.setFileTypeValue(map.get("fileType").toString());
		        		  rowFileDataFields.add(fileDataField);
	        		  }
	        	  }
	        	//收集元数据数据
	        	  String as = f.getAS();
	        	  metaDataField  =  new MetaDataField();
        		  metaDataField.setKey(as);
        		  metaDataField.setName(name);
        		  if(Types.BLOB==type||Types.CLOB==type){
        			  metaDataField.setValue(null!=value?null:"");//组装文件类型元数据输出格式:c:/test.txt
        		  }else{
            		  metaDataField.setValue(null!=value ?String.valueOf(value):"");
        		  }
        		  if(name.equalsIgnoreCase(entity.getPk().trim())){
        			  metaData.setPk(null!=value ?String.valueOf(value):"");
        		  }
        		  rowMetaDataFields.add(metaDataField);
        	  }
           }
        }
    }
    //填充文件类型关联的文件名称和文件类型
	  for(FileDataField fdf:rowFileDataFields){
		 // if(fdf.getType() == Types.BLOB || fdf.getType() == Types.CLOB) continue;
	    for (Map.Entry<String, Object> arowData : arow.entrySet()) {
	        String key = arowData.getKey();
	        Object value = arowData.getValue();
	        if (value == null)  continue;
	        if(key.equalsIgnoreCase(fdf.getFileNameFieldName())){
	        	fdf.setName(String.valueOf(value));//获取文件名称字段对应的value
	        }else if(key.equalsIgnoreCase(fdf.getFileTypeFieldName())){
	        	fdf.setFileTypeValue(String.valueOf(value).trim());//获取文件类型字段对应的value
	        }
	      }
	    //填充文件类型元数据value
	    for(MetaDataField mdf : rowMetaDataFields){
	    	String fileType=null;
	    	if(mdf.getName().equals(fdf.getName())){
	    		fileType=fdf.getFileTypeValue();
	    		if(null!=fileType&&fileType.indexOf(FILETPYEDOT)==-1){
					fileType = 	FILETPYEDOT+fileType;
				}
	    		mdf.setValue(metaData.getDataFilePath()+fdf.getFileNameValue()+fileType);
	    	}
	    }
	 
      }
    metaData.setRowFileDataFields(rowFileDataFields);
    metaData.setRowMetaDataFields(rowMetaDataFields);
  }
	private void generateMD5(MetaDataWrapper metaData) {
		StringBuffer sb = new StringBuffer();
		Object dataTempPath = null;
		List<FileDataField> fileDataFields = metaData.getRowFileDataFields();
		for(FileDataField fileDataField:fileDataFields){
				//加入文件md5
				 try {
					 dataTempPath =  fileDataField.getDataTempPath();
					 if(null!=dataTempPath){
						 GridFSDBFile dbFile = mgDatumBiz.findFileById(dataTempPath.toString());
									/*.findOne(new Query()
											.addCriteria(new Criteria("_id")
													.is(new ObjectId(dataTempPath.toString()))));*/
						 InputStream in = dbFile.getInputStream();
						 
						 
						 sb.append(IOUtils.toString(in));
						 IOUtils.closeQuietly(in);
					 }
				} catch (IOException e) {
					throw new DataImportException(DataImportException.SEVERE, e);
				}
		}
		String value =null;
		/*List<MetaDataField> metaDataFields = metaData.getRowMetaDataFields();
		for(MetaDataField metaDataField:metaDataFields){
			value = metaDataField.getValue();
			if(null!=value&&!"".equals(value)){
				sb.append(value);
			}
		}*/
		metaData.setKvs(metaData.createXML());
		metaData.setObjKvs(metaData.createObjKvs());
		sb.append(metaData.getKvs());
		if(!"".equals(sb.toString()))
		 metaData.setMd5(ConverterUtil.EncoderByMd5(sb.toString()));
		
	}
	 public Map getInputStream(String url,HttpGet httpget){
			CloseableHttpClient httpclient = HttpClients.createDefault();
			try {
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();
				
				String fileName = "";
		        String fileType = "";
				Header[] contentHeader = response.getHeaders("Content-Disposition");
		        //Content-Disposition: attachment; filename=%e6%97%a0%e4%be%b5%e5%ae%b3%e9%92%bb%e4%ba%95%e6%b6%b2%e6%8a%80%e6%9c%af%e7%a0%94%e7%a9%b6%e7%8e%b0%e7%8a%b6%e5%8f%8a%e5%b1%95.pdf
		        		        
		        for(int i = 0; i < contentHeader.length; i++){
		        	Header h = contentHeader[i];
		        	fileType = h.getValue().substring(h.getValue().lastIndexOf(".")+1, h.getValue().length());
//		        	System.out.println("fileType is:"+ fileType);
		        }
		      //取头信息
		    	
		    	if(fileType.equals("")){
		    		Header[] headers = response.getAllHeaders();
			    	for(int i=0;i<headers.length;i++) {
			    		System.out.println(headers[i].getName() +"=="+ headers[i].getValue());
			    		if(headers[i].getName().equals("Content-Type")){
			    			fileType = headers[i].getValue().substring(headers[i].getValue().indexOf("/")+1,headers[i].getValue().length());
			    			break;
			    		}
			    	}
		    	}
		        fileName = ConverterUtil.EncoderByMd5(url);
		        try {
		        	//Mongo db = new Mongo("127.0.0.1", 27017);
					//DB mydb = db.getDB("test");
					/*GridFS myFS = new GridFS(mydb);
					GridFSInputFile inputFile = myFS.createFile(in, fileName + "." + fileType);
					inputFile.save();*/
					GridFSFile inputFile = mgDatumBiz.storeFile(in, fileName + "." + fileType);
					Map map = new HashMap();
					map.put("fileName", fileName);
					map.put("fileType", fileType);
					map.put("mongoID", inputFile.getId());
					return map;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				//return in;
				} catch (IOException e) {  
					e.printStackTrace();
				} finally {
				}
			return null;
				
		}
  private final static int FileTypes = 3008;
  class MetaDataBuiderSpider extends SdcSpider{
	  
	  	private EntityProcessor ep;
	  	private MetaDataDBHandler metaDataDBHandler;
	  	public MetaDataBuiderSpider(String spiderName,EntityProcessor ep) {
	  			super.setSpiderName(spiderName);
				this.ep = ep;
				this.metaDataDBHandler = AdminConfigUtils.getBean("metaDataDBHandler");
				
		 }
		public EntityProcessor getEp() {
			return ep;
		}
		public void setEp(EntityProcessor ep) {
			this.ep = ep;
		}
		
		
	@Override
	public void execute() {
		long start = System.nanoTime();
		//初始化上下文 上下文的范围EntityProcessor
	    ContextImpl ctx = new  ContextImpl(ep, null,null, session, Context.FULL_DUMP);
	    //初始化sql实体处理类
	    ep.init(ctx);
	    SqlEntityProcessor sep = (SqlEntityProcessor)ep;
	    int count = sep.getMarkSize();
	    while (true) {
	    	MetaDataWrapper metaData = new MetaDataWrapper();
	        try {
	          init(metaData);
	          Map<String, Object> arow = sep.nextRow();
	          if (arow == null) {
	        	  destory(metaData);//释放资源
	        	  break;
	          }
	          if(spiderMonitor())break;
	          
	          assemblingMetaDataFields(sep.getEntity(), metaData, arow);
	          generateMD5(metaData);
	      
	        //持久化数据
	        metaDataDBHandler.storage(metaData);
	       	//记录断点位置
	    	sep.mark(count,this.getSpiderState(),metaData);
	        count++;
	        } catch (Exception t) {
		          throw new DataImportException(DataImportException.SEVERE, t);
		          
		    }
	     }
		 LOG.info("  run "+((SqlEntityProcessor)ep).getEntityName()+" end time "
		 		+ "["+TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS)+" ms]");//单位毫秒
	}
	private void init(Object obj){
		if(null!=obj){
			if(obj instanceof MetaDataWrapper){
				
				MetaDataWrapper metaData = (MetaDataWrapper)obj;
				SqlEntityProcessor sep = (SqlEntityProcessor)ep;
			    metaData.setTaskId(sep.getTaskId());
			    String dataFileRootPath = params.getMetaDateFilePath()+File.separator+sep.getEntityName()+File.separator;
			    metaData.setDataFilePath(dataFileRootPath);
			    String dataTempPath = params.getDataTempPath()+File.separator+sep.getEntityName()+File.separator;
			    metaData.setDataFileTempPath(dataTempPath);
			}
			
		}
	
	}
	
	
	private void destory(MetaDataWrapper metaData){
		 SqlEntityProcessor sep = (SqlEntityProcessor)ep;
		 ((QuartzScheduleBiz)AdminConfigUtils.getBean("quartzScheduleBiz")).updateTaskJobState(metaData.getTaskId(), JobModel.STATE_STOP);
    	DBSpiderRecord.removeRecord(config.getRequestInfo().getSpiderRecordFilePath()+sep.getTaskId()+DBSpiderRecord.LOGFILETYPE,sep.getTaskId());
    	metaData.deleteDir(new File(metaData.getDataFileTempPath()));
    	sep.getDataSource().close();
//    	closeMongo();
	}
	private void destoryAndPause(){
		 SqlEntityProcessor sep = (SqlEntityProcessor)ep;
		 ((QuartzScheduleBiz)AdminConfigUtils.getBean("quartzScheduleBiz")).markTaskJobError(sep.getTaskId(), JobModel.STATE_PAUSE,JobModel.JOB_CONECTION_ERROR);
		 DBSpiderRecord.removeRecord(config.getRequestInfo().getSpiderRecordFilePath()+sep.getTaskId()+DBSpiderRecord.LOGFILETYPE,sep.getTaskId());
//   	metaData.deleteDir(new File(metaData.getDataFileTempPath()));
		 sep.getDataSource().close();
//		 closeMongo();
	}
	
}
  
  /**
   * 	<field key="code" value="1668046"/>
   * @author hadoop
   *
   */
   class MetaDataField{
	    private String key;
	  	private String value;
	  	private String name;//字段名称
	    public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
  }
  class FileDataField{
	   private String fileNameValue;
	   private String fileTypeValue;
	   private Object dataTempPath;

	   private String fileNameFieldName;//文件名称字段
	   private String fileTypeFieldName;//文件类型字段
	   private int type;//数据类型
	   private String name;//数据字段名
	   
		public Object getDataTempPath() {
			return dataTempPath;
		}
		public void setDataTempPath(Object dataTempPath) {
			this.dataTempPath = dataTempPath;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
	
		public String getFileNameValue() {
			return fileNameValue;
		}
		public void setFileNameValue(String fileNameValue) {
			this.fileNameValue = fileNameValue;
		}
		public String getFileTypeValue() {
			return fileTypeValue;
		}
		public void setFileTypeValue(String fileTypeValue) {
			this.fileTypeValue = fileTypeValue;
		}
		public String getFileNameFieldName() {
			return fileNameFieldName;
		}
		public void setFileNameFieldName(String fileNameFieldName) {
			this.fileNameFieldName = fileNameFieldName;
		}
		public String getFileTypeFieldName() {
			return fileTypeFieldName;
		}
		public void setFileTypeFieldName(String fileTypeFieldName) {
			this.fileTypeFieldName = fileTypeFieldName;
		}
   }
  
   class MetaDataWrapper {

		private  List<MetaDataField> rowMetaDataFields  = new ArrayList<MetaDataField>() ;
	  	private List<FileDataField> rowFileDataFields = new ArrayList<FileDataField>();
	  	private String taskId;
	  	private String md5;
	  	private String pk;
	  	private String kvs;
	  	private DBObject objKvs;
	  	public String getPk() {
			return pk;
		}
		public void setPk(String pk) {
			this.pk = pk;
		}
		public String getMd5() {
			return md5;
		}
		public void setMd5(String md5) {
			this.md5 = md5;
		}
		public String getTaskId() {
			return taskId;
		}
		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}
		public List<MetaDataField> getRowMetaDataFields() {
	  		return rowMetaDataFields;
		}
		public void setRowMetaDataFields(List<MetaDataField> rowMetaDataFields) {
			this.rowMetaDataFields = rowMetaDataFields;
		}
		public List<FileDataField> getRowFileDataFields() {
			return rowFileDataFields;
		}
		public void setRowFileDataFields(List<FileDataField> rowFileDataFields) {
			this.rowFileDataFields = rowFileDataFields;
		}
		public String getDataFilePath() {
			return dataFilePath;
		}
		public void setDataFilePath(String dataFilePath) {
			this.dataFilePath = dataFilePath;
		}
		public String getMetaDataFilePath() {
			return metaDataFilePath;
		}
		public void setMetaDataFilePath(String metaDataFilePath) {
			this.metaDataFilePath = metaDataFilePath;
		}
		

		/*Map<String ,Object> session;
	    public void setSessionAttribute(String key, Object val){
	      if(session == null) session = new HashMap<String ,Object>();
	      session.put(key, val);
	    }

	    public Object getSessionAttribute(String key) {
	      return session == null ? null : session.get(key);
	    }*/
		
	    public DBObject getObjKvs() {
			return objKvs;
		}
		public void setObjKvs(DBObject objKvs) {
			this.objKvs = objKvs;
		}
		public String getKvs() {
			return kvs;
		}
		public void setKvs(String kvs) {
			this.kvs = kvs;
		}
		public  String  createXML() {
	    	if(null!=rowMetaDataFields&&rowMetaDataFields.size()>0){
	    		//this.createXML(rowMetaDataFields, this.metaDataFilePath);
	    		return this.createXML(rowMetaDataFields);
	    	}
	    	return null;
	    }
	    public  String createXML(List<MetaDataField> rowMetaDataFields) {
	    	MetaDataWriter xml = new MetaDataWriter(ROOTELEMENT,FIELDSELEMENT,MetaDataWriter.STRTYPE);
	    	  try {
	    		HashMap<String, String> map = null;
	    	   for(int i=0;i<rowMetaDataFields.size();i++){
	    		   MetaDataField metaDataField = rowMetaDataFields.get(i);
	    		   	  map = new HashMap<String, String>();
		        	  map.put(metaDataField.getKey(),metaDataField.getValue());
		        	  xml.writeField(map,FIELDELEMENT);
	    	   }
	    	   xml.end();
	    	  } catch (SAXException e) {
	    	   e.printStackTrace();
	    	  }finally{
	    	  }
	    	  
	    	  return xml.getXmlStr();
	    }
	    
	    public DBObject createObjKvs(){
	    	if(null!=rowMetaDataFields&&rowMetaDataFields.size()>0){
	    		return this.createObjKvs(rowMetaDataFields);
	    	}
	    	return null;
	    }
	    public DBObject createObjKvs(List<MetaDataField> rowMetaDataFields){
	    	DBObject kvs = new BasicDBObject();
	    	  try {
	    		  MetaDataField metaDataField = null;
	    		for(int i=0;i<rowMetaDataFields.size();i++){
	    		   metaDataField = rowMetaDataFields.get(i);
	    		   kvs.put(metaDataField.getKey(),metaDataField.getValue());
	    	   }
	    	  } catch (Exception e) {
	    	   e.printStackTrace();
	    	  }finally{
	    	  }
	    	  return kvs;
	    }
	    
	    public  void createXML(List<MetaDataField> rowMetaDataFields,String xmlFilePath) {
	    	MetaDataWriter xml = new MetaDataWriter(xmlFilePath,ROOTELEMENT,FIELDSELEMENT,MetaDataWriter.FILETYPE);
	    	  try {
	    		HashMap<String, String> map = null;
	    	   for(int i=0;i<rowMetaDataFields.size();i++){
	    		   MetaDataField metaDataField = rowMetaDataFields.get(i);
	    		    
	    		   map = new HashMap<String, String>();
		        	  map.put(metaDataField.getKey(),metaDataField.getValue());
		        	  xml.writeField(map,FIELDELEMENT);
	    	   }
	    	   xml.end();
	    	  } catch (SAXException e) {
	    	   e.printStackTrace();
	    	  }finally{
	    	  }
	    	
	    }	    
	    
		/*public void writeToFile() {
			if(null!=rowFileDataFields&&rowFileDataFields.size()>0){
					try {
						String dataFilePath = null;
						for(FileDataField fileDataField :rowFileDataFields){
								String 	fileType = fileDataField.getFileTypeValue();
								if(null!=fileType&&fileType.indexOf(FILETPYEDOT)==-1){
									fileType = 	FILETPYEDOT+fileType;
								}
								dataFilePath=this.dataFilePath+fileDataField.getFileNameValue()+fileType;
								if(null!=fileDataField.getDataTempPath()){
									File file = new File(fileDataField.getDataTempPath());
									if(file.exists()){
										InputStream input = new FileInputStream(file);
										this.writeToFile(input,dataFilePath);
										file.delete();//清除临时文件
									}
								}
						}
					} catch (FileNotFoundException e) {
						LOG.error(dataFilePath+" is not exists!");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}finally{}
					
			}
		}*/
	    /**
	     * 下载Blob,Clob
		 * filePath 为String类型的路径+文件名
		 * @throws FileNotFoundException 
		 * @throws UnsupportedEncodingException 
		 */
		public  void  writeToFile(InputStream input,String filePath) throws FileNotFoundException, UnsupportedEncodingException {
			//创建文件对象
			String dirs = filePath.substring(0,filePath.lastIndexOf(File.separator));
			File file = new File(dirs);
			if(file.mkdirs());
			
			 file = new File(filePath);
			//创建文件输出流
			FileOutputStream output = new FileOutputStream(file);
			//输出
			try {
				byte[] b = new byte[2048];//设置缓存
				int len = 0;//初始化读取字节数
				while((len = input.read(b))!=-1){//返回-1，则表示已经读到文件末尾，即读取完毕.
					output.write(b,0,len);
				}
			} catch (IOException e) {
				 throw new DataImportException(SEVERE,
			              "write file err",e);
			}finally{
				
					try {
						if(input!=null){
							input.close();
						}
						if(output!=null){
							output.close();
						}
					} catch (IOException e) {
						throw new DataImportException(SEVERE,
					              "close outputStream err",e);
						
					}
				
			}
			
		}
		
		/**
	     * 递归删除目录下的所有文件及子目录下所有文件
	     * @param dir 将要删除的文件目录
	     * @return boolean Returns "true" if all deletions were successful.
	     *                 If a deletion fails, the method stops attempting to
	     *                 delete and returns "false".
	     */
	    public  boolean deleteDir(File dir) {
	        if (dir.isDirectory()) {
	            String[] children = dir.list();
	            	//递归删除目录中的子目录下
	            for (int i=0; i<children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
	        // 目录此时为空，可以删除
	        return dir.delete();
	    }
	     public  boolean removeTemp(String filePath){
	      	  File fie = new File(filePath);
	   	  	   if(fie.exists()){
	   	  		 fie.delete();
	   	  		 return true;
	   	  	   }
	   	  	 return false;
	       }
		private final static String ROOTELEMENT = "metadata";
		private final static String FIELDSELEMENT = "fields";
	  	private final static String FIELDELEMENT = "field";
	  	public final static String FILETPYEDOT = ".";
		private  String dataFilePath;//文件输出路径 test use
		private String metaDataFilePath;//元数据输出路径 test use
		
		private String dataFileTempPath;//临时数据文件路径 system use
		public String getDataFileTempPath() {
			return dataFileTempPath;
		}
		public void setDataFileTempPath(String dataFileTempPath) {
			this.dataFileTempPath = dataFileTempPath;
		}
		
	 }
 
}
