package com.peraglobal.km.crawler.extract.biz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.peraglobal.km.crawler.db.biz.SdcSpider;
import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.extract.model.KnowledgeMetadata;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.MarkBreakpoint;
import com.peraglobal.km.crawler.util.MetaFormater;
import com.peraglobal.km.crawler.util.XMLFormater;
import com.peraglobal.km.crawler.web.biz.AttachmentCrawlerBiz;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.biz.MgKnowledgeMetadataBiz;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.km.mongo.model.KM_KnowledgeMetadata;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.id.IDGenerate;
import com.peraglobal.pdp.common.utils.DateUtils;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
import com.peraglobal.pdp.util.ExtractInfoMain;

public class ExtractSpider  extends SdcSpider {
	protected TaskJob taskJob;
	private TaskRule taskRule;
	private TaskJobMonitorBiz jobMonitorService;
	private QuartzScheduleBiz quartzScheduleService;
	private int markPage;
	private String filePath;
	MetaFormater formater = new XMLFormater();
	private DatumBiz datumBiz;
	private KnowledgeMetadataBiz knowledgeMetadataBiz;
	private final AtomicLong successCount = new AtomicLong(0);
	private final AtomicLong errorCount = new AtomicLong(0);
	private boolean isAdd = false;
	private boolean isUpdated = false;
	private TaskJobBiz taskJobBiz;
	private AttachmentCrawlerBiz attachmentCrawlerBiz;
	private ExtractInfoMain extractInfoMain;
	private static final String KNOWLEDGE_AUTHOR = "KNOWLEDGE_AUTHOR:INHERENT";
	private static final String KNOWLEDGE_KEYWORD = "KEYWORD:INHERENT";
	private static final String KNOWLEDGE_SUMMARY = "SUMMERY:INHERENT";
	private String classPath = "";
	private String extractFloder = "";
	private File floder = null;
	private MgDatumBiz mgDatumBiz;
	private MgKnowledgeMetadataBiz mgKnowledgeMetadataBiz;
	private int PAGE_SIZE = 10;
	private GridFsTemplate gridFsTemplate;
	
	@Override
	public void execute() {
		String connectId = this.taskJob.getConnectId();
		String ruleContent = "";
		try {
			
			if(this.taskRule!=null){
				byte[] ruleByte=this.taskRule.getRuleContent();
				if(ruleByte.length>0){
					ruleContent = new String(ruleByte,"utf-8");
				}
			}
			Map<String,String> fieldMap = Dom4jXmlUtil.generateMetaMap(ruleContent);
			KM_KnowledgeMetadata km = null;
			KM_KnowledgeMetadata kmData_old = null;
			List<KnowledgeMetadata> kmList = null;
			while(true) {
				if(spiderMonitor()) break;
				kmList = new ArrayList<KnowledgeMetadata>();
				// 一次取100条数据
				/*ListPageQuery list=new ListPageQuery();
				list.setPage(this.markPage);
				list.setRows(10);
				Condition con=Condition.parseCondition("taskId_string_eq");
				con.setValue(connectId);
				list.getConditions().getItems().add(con);
				List<Datum> datumList = datumBiz.find(list.getPagination(), con);*/
				int skip = (markPage-1) * PAGE_SIZE;
				List<KM_Datum> kmDatumList = mgDatumBiz.findByCondition(new Criteria("taskId").is(connectId),skip, PAGE_SIZE);
				
//				if(datumList == null || datumList.size() == 0){
				if(kmDatumList == null || kmDatumList.size() == 0){
					//查不到数据
					MarkBreakpoint.writeBreakPage(filePath, "1");
					//采集完成
					quartzScheduleService.updateTaskJobState(taskJob.getId(), JobModel.STATE_STOP);
					break;
				}
				// 抽取附件规则
				String[] extractList = null;
				if(taskJob.getExtractList() != null && !taskJob.getExtractList().equals("")){
					extractList = this.taskJob.getExtractList().split(",");
				}
				List<AttachmentCrawler> attachmentList = null;
//				for(Datum d : datumList){
				for(KM_Datum km_datum : kmDatumList){
					km = new KM_KnowledgeMetadata();
					boolean isUpdate = false;
					// 根据转换映射，生成知识元数据
					DBObject objKvs = km_datum.getKvs();
					attachmentList = attachmentCrawlerBiz.findAllByDatumId(km_datum.getId());
					String author = "";
					String keyword = "";
					String summary = "";
					if(attachmentList!= null && attachmentList.size()>0){
						extractFloder = AppConfigUtils.get("conf.extract.document")+taskJob.getId()+"/";
						floder = new File(extractFloder);
						if(!floder.exists()){
							floder.mkdirs();
						}
						for(AttachmentCrawler ac :attachmentList){
							if(ac.getPath()==null || ac.getPath().equals("")){
								continue;
							}
							GridFSDBFile dbFile = gridFsTemplate
									.findOne(new Query()
											.addCriteria(new Criteria("_id")
													.is(new ObjectId(ac
															.getPath()))));
							//只处理附件类型为pdf和doc/docx的
							String fileType = dbFile.getFilename().substring(dbFile.getFilename().lastIndexOf(".")+1);
							if(!fileType.equals("pdf") && !fileType.equals("doc") && !fileType.equals("docx")){
								continue;
							}
							// 从MongoDB获取文件
							if(extractList!=null){
								InputStream is = dbFile.getInputStream();
								String fileName = extractFloder+dbFile.getFilename();
								FileOutputStream fos = new FileOutputStream(fileName);
								byte[] b = new byte[1024];
								while((is.read(b)) != -1){
									fos.write(b);
								}
								is.close();
								fos.close();
								System.out.println("抽取开始："+DateUtils.formatToSecond(new Date()));
								Map<String,String> datumInfo = new HashMap<String,String>();
//								datumInfo = extractInfoMain.extractInfo(fileName, this.classPath+"extractRule.xml");
								// 单独开启抽取的线程类，如果超时将终止抽取方法，超时时间为：30秒
								ExtractPDF extractPdf = new ExtractPDF(fileName, this.classPath+"extractRule.xml");
								// 创建一个执行任务的服务
						        ExecutorService es = Executors.newFixedThreadPool(3);
						        Future future1 = es.submit(extractPdf);
						        int timeLimit = 30;
						        long startTime = System.currentTimeMillis();
						        long endTime = System.currentTimeMillis();
						        boolean timeout = endTime - startTime > timeLimit * 1000;
						        try {
						            // 如果子线程未运行完毕，且未超时，则继续等待子线程。
						            while (!future1.isDone() && !timeout) {
						                endTime = System.currentTimeMillis();
						                timeout = endTime - startTime > timeLimit * 1000;
						            }
						        } catch (Exception e) {
						            e.printStackTrace();
						        }
						        if (timeout) {
						        	es.shutdown();
						            System.out.println("timeout and stopped.");
						        }else{
						        	try {
						        		datumInfo = (Map<String,String>)future1.get();
									} catch (Exception e) {
										e.printStackTrace();
									}
						        }
//								datumInfo = extractInfoMain.extractInfo(fileName, this.classPath+"extractRule.xml");
								System.out.println("抽取结束："+DateUtils.formatToSecond(new Date()));
								for(int i= 0; i< extractList.length ; i++){
									if(extractList[i].equals("3")){
										author = datumInfo.get("author");
									}
									if(extractList[i].equals("4")){
										keyword = datumInfo.get("keyword");
									}
									if(extractList[i].equals("5")){
										summary = datumInfo.get("summary");
									}
								}
								File file2 = new File(fileName);
								if(file2.exists()){
									file2.delete();
								}
							}
							// 如果抽取过关键字作者等信息，会存在一个txt文件
							String txtFile = extractFloder + dbFile.getFilename().substring(0,dbFile.getFilename().lastIndexOf(".")) + ".txt";
							File file1 = new File(txtFile);
							if(!file1.exists()){
								// 抽取txt
								txtFile = extractInfoMain.coveredTotxt(extractFloder, dbFile.getFilename());
								file1 = new File(txtFile);
							}else{
								InputStream input = new FileInputStream(file1);
						        GridFSFile inputFile = null;
								inputFile = gridFsTemplate.store(input, file1.getName());
//								inputFile.save();
								input.close();
								file1.delete();
								ac.setFileSize(String.valueOf(dbFile.getLength()));
								ac.setTxt(inputFile.getId().toString());
								this.attachmentCrawlerBiz.save(ac);
							}
							
						}
					}
//					Map<String,String> knowledgeFields = Dom4jXmlUtil.generateKnowledageMeta(kvs, fieldMap);
					Map<String,String> knowledgeFields = new HashMap<String,String>();
					DBObject knowledgeKvs = new BasicDBObject();
					Map knowledgeKvsMap = objKvs.toMap();
					for(Object key : knowledgeKvsMap.keySet()){
//						System.out.println(key+"==="+knowledgeKvsMap.get(key).toString());
						if(key.toString().indexOf(":")!=-1){
							if(fieldMap.get(key.toString().substring(0, key.toString().indexOf(":")))!=null){
								knowledgeKvs.put(key.toString(), objKvs.get(key.toString()));
							}
						}
					}
					if(!author.equals("")&&(knowledgeKvs.get(KNOWLEDGE_AUTHOR)==null || knowledgeKvs.get(KNOWLEDGE_AUTHOR).toString().equals(""))){
						// 如果元数据信息中没有作者，则用抽取的作者
						knowledgeKvs.put(KNOWLEDGE_AUTHOR, author);
					}
					if(!keyword.equals("")&&(knowledgeKvs.get(KNOWLEDGE_KEYWORD)==null || knowledgeKvs.get(KNOWLEDGE_KEYWORD).toString().equals(""))){
						// 如果元数据信息中没有关键词，则用抽取的关键词
						knowledgeKvs.put(KNOWLEDGE_KEYWORD, keyword);
					}
					if(!summary.equals("")&&(knowledgeKvs.get(KNOWLEDGE_SUMMARY)==null || knowledgeKvs.get(KNOWLEDGE_SUMMARY).toString().equals(""))){
						// 如果元数据信息中没有摘要，则用抽取的摘要
						knowledgeKvs.put(KNOWLEDGE_SUMMARY, summary);
					}
					if(this.taskJobBiz.findById(connectId).getRegisterType().equals(JobModel.TYPE_LOCAL)){
						knowledgeKvs = objKvs;
					}
					km.setId(IDGenerate.uuid());
					km.setMd5(ConverterUtil.EncoderByMd5(knowledgeKvs.toString()));
					km.setDatumId(km_datum.getId());
//					kmData_old = knowledgeMetadataBiz.getKnowledgeMdByDatumId(taskJob.getId(),km_datum.getId());
					Query query = new Query();
					query.addCriteria(new Criteria("datumId").is(km_datum.getId()).and("taskId").is(taskJob.getId()));
					List<KM_KnowledgeMetadata> kmDataOldList = mgKnowledgeMetadataBiz.findByCondition(query);
					if(kmDataOldList!=null && kmDataOldList.size()>0){
						kmData_old = kmDataOldList.get(0);
						if(!kmData_old.getMd5().equals(km.getMd5())){
							// 转换规则有变化，不全数据
							km.setId(kmData_old.getId());
							km.setTransferFlag(kmData_old.getTransferFlag()); //是否传输过
							isUpdate = true;
							
							// 对已编辑过的数据更新处理，需判断是否已经添加过值
							if(kmData_old.getIsUpdate()!=null && kmData_old.getIsUpdate().equals("0")){
								DBObject oldKvs = kmData_old.getKvs();
								Map oldMapKvs = oldKvs.toMap();
								for(Object key : oldMapKvs.keySet()){
									if(oldMapKvs.get(key)!=null && !oldMapKvs.get(key).toString().equals("") && knowledgeKvs.get(key.toString())!=null && knowledgeKvs.get(key.toString()).equals("")){
										// 将编辑过的值 赋值到当前转换的对象中
										knowledgeKvs.put(key.toString(), oldMapKvs.get(key));
									}
								}
							}
							
							// 有新数据
//							isAdd = true;
						}else{
							continue;
						}
					}else{
						// 有新数据
						isAdd = true;
					}
					/*try {
						km.setMetadata(metadata.getBytes("GBK"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}*/
					km.setKvs(knowledgeKvs);
					//验证数据完整性
					String FullState=Dom4jXmlUtil.checkIsFull(knowledgeKvs);
					km.setIsFull(FullState);
					if(!isUpdate){
						km.setCreateDate(new Date());
						if("0".equals(FullState)){
							km.setIsUpdate("1");
							km.setIsChecked("0");
						}else{
							km.setIsUpdate("1");
							km.setIsChecked("1");
						}
					}
					String fileName = ConverterUtil.EncoderByMd5(formater.format(knowledgeFields));
//					km.setFileId(""); //km.setFileId(fileId);
					km.setTaskId(taskJob.getId());
					km.setTransferFlag("0");
					this.mgKnowledgeMetadataBiz.save(km);
					successCount.addAndGet(1);
				}
//				knowledgeMetadataBiz.saveBatch(kmList);
//				successCount.addAndGet(kmList.size());
				markPage ++;
				// 记录断点位置
				MarkBreakpoint.writeBreakPage(filePath, markPage+"");
				//修改成功数
				jobMonitorService.updateFullAndFailed(taskJob.getId(),
						Integer.parseInt(successCount.toString()),
						Integer.parseInt(errorCount.toString()));
				if(isAdd&&!isUpdated){
					// 更新采集任务的转换任务(状态为停止)：有新数据提醒
					this.taskJobBiz.updateJobHaveNewData(taskJob.getId());
					isUpdated = true;
				}
			}
//			db.close(); //关闭数据库
		} catch (Exception e) {
			//转换出异常
			quartzScheduleService.markTaskJobError(taskJob.getId(), JobModel.STATE_PAUSE, JobModel.JOB_CONECTION_ERROR);
//			db.close();
			e.printStackTrace();
		}
		
	}
	
	public ExtractSpider(){
		this.classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		this.quartzScheduleService = AdminConfigUtils.getBean("quartzScheduleBiz");
		this.jobMonitorService = AdminConfigUtils.getBean("taskJobMonitorBiz");
		this.datumBiz = AdminConfigUtils.getBean("datumBiz"); 
		this.knowledgeMetadataBiz = AdminConfigUtils.getBean("knowledgeMetadataBiz");
		this.taskJobBiz = AdminConfigUtils.getBean("taskJobBiz");
		this.attachmentCrawlerBiz =  AdminConfigUtils.getBean("attachmentCrawlerBiz");
		this.extractInfoMain = new ExtractInfoMain();
		this.mgDatumBiz = AdminConfigUtils.getBean("mgDatumBiz");
		this.mgKnowledgeMetadataBiz = AdminConfigUtils.getBean("mgKnowledgeMetadataBiz");
		this.gridFsTemplate = AdminConfigUtils.getBean("gridFsTemplate");
		
		/*try {
			db = new Mongo(AppConfigUtils.get("km.mongo.address"), Integer.valueOf(AppConfigUtils.get("km.mongo.port")));
			mydb = db.getDB(AppConfigUtils.get("km.mongo.dbName"));
			myFS = new GridFS(mydb);
		} catch (Exception e) {
			//转换出异常
			quartzScheduleService.markTaskJobError(taskJob.getId(), JobModel.STATE_STOP, JobModel.JOB_CONECTION_ERROR);
			db.close();
			e.printStackTrace();
		}*/
	}
	
	public static ExtractSpider create(){
		return new ExtractSpider();
	}
	public ExtractSpider setTaskJob(TaskJob taskJob) {
		this.taskJob = taskJob;
		return this;
	}
	public void register(){
		SpiderManager.register(this.taskJob.getId(), this);
	}

	public ExtractSpider setMarkPage(String markPage) {
		this.markPage = Integer.parseInt(markPage);
		return this;
	}

	public ExtractSpider setTaskRule(TaskRule taskRule) {
		this.taskRule = taskRule;
		return this;
	}

	public ExtractSpider setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}
	
	

}
