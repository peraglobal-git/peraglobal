package com.peraglobal.km.crawler.local.biz;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSFile;
import com.peraglobal.km.crawler.db.biz.SdcSpider;
import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.task.biz.TaskRuleBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.MetaFormater;
import com.peraglobal.km.crawler.util.POIExcelUtil;
import com.peraglobal.km.crawler.util.XMLFormater;
import com.peraglobal.km.crawler.web.biz.AttachmentCrawlerBiz;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.id.IDGenerate;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

public class ExcelSpider extends SdcSpider {
	protected String fileName;
	protected boolean isExpert = false;
	protected String metaKey;
	protected TaskJob taskJob;
	private QuartzScheduleBiz quartzScheduleService;
	private TaskJobMonitorBiz jobMonitorService;
	private final AtomicLong successCount = new AtomicLong(0);
	private final AtomicLong errorCount = new AtomicLong(0);
	MetaFormater formater = new XMLFormater();
	private TaskRuleBiz taskRuleBiz;
	private AttachmentCrawlerBiz attachmentCrawlerBiz;
	private DatumBiz datumBiz;
	private MgDatumBiz mgDatumBiz;
	
	public ExcelSpider(){
		taskRuleBiz = AdminConfigUtils.getBean("taskRuleBiz");
		this.quartzScheduleService = AdminConfigUtils.getBean("quartzScheduleBiz");
    	this.jobMonitorService = AdminConfigUtils.getBean("taskJobMonitorBiz");
    	attachmentCrawlerBiz = AdminConfigUtils.getBean("attachmentCrawlerBiz");
    	datumBiz = AdminConfigUtils.getBean("datumBiz");
    	mgDatumBiz = AdminConfigUtils.getBean("mgDatumBiz");
	}
	
	@Override
	public void execute() {
		this.run();
	}
	
	@Override
	public void run() {
		// sheet页总数
		try {
			int totalSheets = new POIExcelUtil().getTotalSheets(fileName);
			if(this.isExpert && totalSheets == 5){
				// 专家采集
				expertSpider();
				return;
			}
			
			// 基本信息
			Map<String,Object> data = null;
			Map<String,List<String[]>> sheetData = null;
			List<Map<String,List<String[]>>> sheetLst = new ArrayList<Map<String,List<String[]>>>(); 
			Map<String,String> titleMap = new HashMap<String,String>();
			List<String[]> dataLst = null;
			
			for(int i = 0; i < totalSheets; i++){
				if(i > 0){
					sheetData = new POIExcelUtil().readExpert(fileName, i);
					if(sheetData == null || sheetData.size() == 0){continue;}
					sheetLst.add(sheetData);
					if(this.metaKey == null || this.metaKey.equals("")){continue;}
					String[] sheetTitle = sheetData.get(this.metaKey).get(0);
					getTitleMap(sheetTitle, titleMap);
				}else{
					data = new POIExcelUtil().read(fileName, i);
					dataLst = (List<String[]>)data.get("dataLst");
					String[] sheetTitle = dataLst.get(0);
					getTitleMap(sheetTitle, titleMap);
				}
				
			}
			// 生成key规则
			generateTaskRule(titleMap);
			//表头
			String[] titleLst=null;
			if(dataLst.size()>0){
				titleLst = dataLst.get(0);
			}
			Datum d = null;
			KM_Datum km_datum = null;
			AttachmentCrawler a = null;
			for (int m=1;m<dataLst.size();m++) 
			{
				if(spiderMonitor()) break;
				String[] innerLst = dataLst.get(m);
				StringBuffer metadata = new StringBuffer();
//				d = new Datum();
				km_datum = new KM_Datum();
				DBObject kvs = new BasicDBObject();
				String attachmentName = "";
				metadata.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\r");
				metadata.append("<metadata><fields>" + "\r");
				for (int n=0;n<titleLst.length;n++) 
			    {
			       String name = titleLst[n];
			       if(name.equals("")){continue;}
			       String value = innerLst[n];
			       if(name.equals("附件名称")){
			    		attachmentName = value;
			    		continue;
					}
			      /* if(name.indexOf("(")!=-1){
			    	   name = name.substring(name.indexOf("(")+1, name.indexOf(")"));
			       }*/
			       metadata.append("<field>");
			       metadata.append("<key>"+name+"</key>");
//			       metadata.append("<teptId>INHERENT</teptId>");
			       metadata.append("<value>"+value+"</value>");
			       metadata.append("</field>" + "\r");
			       kvs.put(name, value);
//	           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
			    }
				for(int k = 0; k <sheetLst.size(); k ++ ){
					Map<String,List<String[]>> jianli = sheetLst.get(k);
					
					List<String[]> jianliLst = jianli.get(innerLst[0]);
					if(jianliLst != null && jianliLst.size() > 0){
//					metadata.append("<jianlis>");
						if(jianli.get(this.metaKey) == null){
							break;
						}
						String[] jianliTitle = jianli.get(this.metaKey).get(0);
						for(int i = 0; i < jianliLst.size(); i++){
							String[] jianliAry = jianliLst.get(i);
//						metadata.append("<jianli>");
							for (int j = 1; j < jianliTitle.length; j++) 
					        {
					           String name = jianliTitle[j].trim();
					           if(name.equals("")){continue;}
					           String value = jianliAry[j].trim();
					           metadata.append("<field>");
					           metadata.append("<key>"+name+"</key>");
					           metadata.append("<value>"+value+"</value>");
					           metadata.append("</field>");
//				           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
					        }
//						metadata.append("</jianli>");
						}
//					metadata.append("</jianlis>");
					}
				}
				metadata.append("</fields></metadata>");
//			String kvs = formater.format(xmlDataMap);
				System.out.println(metadata);
				//d.setId(ConverterUtil.EncoderByMd5(metadata.toString()));
				/*try {
					d.setKvs(metadata.toString().getBytes("GBK"));
				} catch (Exception e) {
					quartzScheduleService.markTaskJobError(taskJob.getId(), JobModel.STATE_STOP,JobModel.JOB_CONECTION_ERROR);
					File defile=new File(AppConfigUtils.get("conf.excelPath") + taskJob.getId());
					 //删除临时文件
					deleteAll(defile);
					//关闭mongo
					closeMongo();
					e.printStackTrace();
				}
				d.setTaskId(taskJob.getId());
				datumBiz.save(d);*/
				
				km_datum.setId(IDGenerate.uuid());
				km_datum.setTaskId(taskJob.getId());
				km_datum.setKvs(kvs);
				km_datum.setIsFull(Dom4jXmlUtil.checkIsFull(kvs));
				km_datum.setCreateDate(new Date());
				this.mgDatumBiz.insert(km_datum);
				// 获取附件
				if(!attachmentName.equals("")){
					File file = new File(AppConfigUtils.get("conf.excelPath") + taskJob.getId() + "/"+attachmentName);
					InputStream f;
					try {
						f = new FileInputStream(file);
						GridFSFile inputFile = mgDatumBiz.storeFile(f, attachmentName);
						 a = new AttachmentCrawler();
						 a.setDatumID(km_datum.getId());
						 a.setTaskId(taskJob.getId());
						 a.setName(attachmentName.substring(0,attachmentName.lastIndexOf(".")));
						 a.setType(attachmentName.substring(attachmentName.lastIndexOf(".")));
						 a.setPath(inputFile.getId().toString()); // 改为mongoobjectid
						 attachmentCrawlerBiz.save(a); // 保存
						 f.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				successCount.incrementAndGet();
				//修改成功数
				jobMonitorService.updateFullAndFailed(taskJob.getId(),
						Integer.parseInt(successCount.toString()),
						Integer.parseInt(errorCount.toString()));
				System.out.println("采集本地实例："+ successCount);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			//采集完成
			quartzScheduleService.updateTaskJobState(taskJob.getId(), JobModel.STATE_STOP);
			File defile=new File(AppConfigUtils.get("conf.excelPath") + taskJob.getId());
			 //删除临时文件
			deleteAll(defile);
		}
	}
	
	 /**
     * 删除一个目录下面的所有文件 
     * @param file 将要删除的文件目录
     */
	public static void deleteAll(File file) {

		if (file.isFile() || file.list().length == 0) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteAll(files[i]);
				files[i].delete();
			}
			if (file.exists()) // 如果文件本身就是目录 ，就要删除目录
				file.delete();
		}
	}
 
	private void generateTaskRule(Map<String, String> titleMap) {
		// 生成key值 配置
		StringBuffer ruleContent = new StringBuffer();
		ruleContent.append("<dataConfig><entity>");
		for(String key : titleMap.keySet()){
			// <field name="NAME" as="名称" type="12" />
			if(key.trim().equals("附件名称")) continue;
			ruleContent.append("<field as=\""+key + "\" />");
		}
		ruleContent.append("</entity></dataConfig>");
		TaskRule taskRule = new TaskRule();
		taskRule.setJobId(taskJob.getId());
		taskRule.setRuleType(taskJob.getRegisterType());
		try {
			taskRule.setRuleContent(ruleContent.toString().getBytes());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		taskRuleBiz.save(taskRule);
	}
	
	public void expertSpider(){
		// 基本信息
    	Map<String,Object> data = new POIExcelUtil().read(fileName, 0);
    	
    	// 简历信息
    	Map<String,List<String[]>> jianli = new POIExcelUtil().readExpert(fileName, 1);
    	
    	// 论文论著信息
    	Map<String,List<String[]>> lwlz = new POIExcelUtil().readExpert(fileName, 2);
    	
    	// 专利信息
    	Map<String,List<String[]>> zhuanli = new POIExcelUtil().readExpert(fileName, 3);
    	
    	// 专业技术成果及获奖情况
    	Map<String,List<String[]>> jscg = new POIExcelUtil().readExpert(fileName, 4);
    	
    	List<String[]> dataLst = (List<String[]>)data.get("dataLst");
		//表头
		String[] titleLst = dataLst.get(0);
		
		Map<String,String> titleMap = new HashMap<String,String>();
		titleMap = getTitleMap(titleLst, titleMap);
		titleMap = getTitleMap(jianli.get("人员编号").get(0), titleMap);
		titleMap = getTitleMap(lwlz.get("人员编号").get(0), titleMap);
		titleMap = getTitleMap(zhuanli.get("人员编号").get(0), titleMap);
		titleMap = getTitleMap(jscg.get("人员编号").get(0), titleMap);
		this.generateTaskRule(titleMap);
		
		Datum d = null;
		for (int m=1;m<dataLst.size();m++) 
        {
			if(spiderMonitor()) break;
			d = new Datum();
			StringBuffer metadata = new StringBuffer();
			metadata.append("<metadata><fields>");
			String[] innerLst = dataLst.get(m);
			for (int n=0;n<titleLst.length;n++) 
	        {
	           String name = titleLst[n];
	           if(name.equals("")){continue;}
	           String value = innerLst[n];
	           metadata.append("<field>");
	           metadata.append("<key>"+name+"</key>");
	           metadata.append("<value>"+value+"</value>");
	           metadata.append("</field>");
//	           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
	        }
			metadata.append("<richtext>");
			// 根据人员编号获取 简历信息
			List<String[]> jianliLst = jianli.get(innerLst[0]);
			if(jianliLst != null && jianliLst.size() > 0){
				metadata.append("<jianlis>");
				String[] jianliTitle = jianli.get("人员编号").get(0);
				for(int i = 0; i < jianliLst.size(); i++){
					String[] jianliAry = jianliLst.get(i);
					metadata.append("<jianli>");
					for (int j = 3; j < jianliTitle.length; j++) 
			        {
			           String name = jianliTitle[j].trim();
			           if(name.equals("")){continue;}
			           String value = jianliAry[j].trim();
			           metadata.append("<field>");
			           metadata.append("<key>"+name+"</key>");
			           metadata.append("<value>"+value+"</value>");
			           metadata.append("</field>");
//			           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
			        }
					metadata.append("</jianli>");
				}
				metadata.append("</jianlis>");
			}
			// 根据人员编号获取 论文论著信息
			List<String[]> lwlzLst = lwlz.get(innerLst[0]);
			if(lwlzLst != null && lwlzLst.size() > 0){
				metadata.append("<lwlzs>");
				String[] lwlzTitle = lwlz.get("人员编号").get(0);
				for(int i = 0; i < lwlzLst.size(); i++){
					String[] lwlzAry = lwlzLst.get(i);
					metadata.append("<lwlz>");
					for (int j = 3; j < lwlzTitle.length; j++) 
			        {
			           String name = lwlzTitle[j].trim();
			           if(name.equals("")){continue;}
			           String value = lwlzAry[j].trim();
			           metadata.append("<field>");
			           metadata.append("<key>"+name+"</key>");
			           metadata.append("<value>"+value+"</value>");
			           metadata.append("</field>");
//			           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
			        }
					metadata.append("</lwlz>");
				}
				metadata.append("</lwlzs>");
			}
			
			// 根据人员编号获取 专利信息
			List<String[]> zhuanliLst = zhuanli.get(innerLst[0]);
			if(zhuanliLst != null && zhuanliLst.size() > 0){
				metadata.append("<zhuanlis>");
				String[] zhuanliTitle = zhuanli.get("人员编号").get(0);
				for(int i = 0; i < zhuanliLst.size(); i++){
					String[] zhuanliAry = zhuanliLst.get(i);
					metadata.append("<zhuanli>");
					for (int j = 3; j < zhuanliTitle.length; j++) 
			        {
			           String name = zhuanliTitle[j].trim();
			           if(name.equals("")){continue;}
			           String value = zhuanliAry[j].trim();
			           metadata.append("<field>");
			           metadata.append("<key>"+name+"</key>");
			           metadata.append("<value>"+value+"</value>");
			           metadata.append("</field>");
//			           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
			        }
					metadata.append("</zhuanli>");
				}
				metadata.append("</zhuanlis>");
			}
			
			// 根据人员编号获取 专业技术成果及获奖情况
			List<String[]> jscgLst = jscg.get(innerLst[0]);
			if(jscgLst != null && jscgLst.size() > 0){
				metadata.append("<jscgs>");
				String[] jscgTitle = jscg.get("人员编号").get(0);
				for(int i = 0; i < jscgLst.size(); i++){
					String[] jscgAry = jscgLst.get(i);
					metadata.append("<jscg>");
					for (int j = 3; j < jscgTitle.length; j++) 
			        {
			           String name = jscgTitle[j].trim();
			           if(name.equals("")){continue;}
			           String value = jscgAry[j].trim();
			           metadata.append("<field>");
			           metadata.append("<key>"+name+"</key>");
			           metadata.append("<value>"+value+"</value>");
			           metadata.append("</field>");
//			           metadata.append("<field key=\"" + name + "\" value=\"" + value + "\" />");
			        }
					metadata.append("</jscg>");
				}
				metadata.append("</jscgs>");
			}
			metadata.append("</richtext>");
	        metadata.append("</fields></metadata>");
	        d.setId(ConverterUtil.EncoderByMd5(metadata.toString()));
	        try {
				d.setKvs(metadata.toString().getBytes("GBK"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        d.setTaskId(taskJob.getId());
	        System.out.println("专家："+metadata.toString());
	        this.datumBiz.save(d);
	        successCount.incrementAndGet();
	        System.out.println("采集专家数："+ successCount);
	        //修改成功数
			jobMonitorService.updateFullAndFailed(taskJob.getId(),
					Integer.parseInt(successCount.toString()),
					Integer.parseInt(errorCount.toString()));
        }
		//采集完成
		quartzScheduleService.updateTaskJobState(taskJob.getId(), JobModel.STATE_STOP);
	}

	private Map<String,String> getTitleMap(String[] titleLst, Map<String, String> titleMap) {
		for (int n=0;n<titleLst.length;n++) 
        {
			if(titleLst[n].equals("")){continue;}
			titleMap.put(titleLst[n], titleLst[n]);
        }
		return titleMap;
	}
	
	public void start() {
        runAsync();
    }
	
	public void runAsync() {
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }
	
	public static ExcelSpider create(){
		return new ExcelSpider();
	}


	public ExcelSpider setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public ExcelSpider setExpert(boolean isExpert) {
		this.isExpert = isExpert;
		return this;
	}

	public ExcelSpider setMetaKey(String metaKey) {
		this.metaKey = metaKey;
		return this;
	}

	public ExcelSpider setTaskJob(TaskJob taskJob) {
		this.taskJob = taskJob;
		return this;
	}
	
	public void register(){
		SpiderManager.register(this.taskJob.getId(), this);
	}

	

	/**
	 *  线程业务处理
	 */
//	public abstract void execute();
	
}
