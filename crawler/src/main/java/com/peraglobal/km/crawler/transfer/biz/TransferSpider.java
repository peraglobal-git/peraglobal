package com.peraglobal.km.crawler.transfer.biz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;

import com.peraglobal.km.crawler.db.biz.SdcSpider;
import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.task.biz.TransferConfigBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TransferConfig;
import com.peraglobal.km.crawler.util.CrawlerMD5Utils;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.JdbcConnectionUtil;
import com.peraglobal.km.crawler.util.MarkBreakpoint;
import com.peraglobal.km.crawler.util.MetaFormater;
import com.peraglobal.km.crawler.util.XMLFormater;
import com.peraglobal.km.crawler.web.biz.AttachmentCrawlerBiz;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.km.webservice.model.AttachmentMeta;
import com.peraglobal.km.webservice.model.KnowledgeMeta;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

public class TransferSpider extends SdcSpider {
	
	
	protected TaskJob taskJob;
	private int markPage;
	private String filePath;
	private TaskJobMonitorBiz jobMonitorService;
	private QuartzScheduleBiz quartzScheduleService;
	private AttachmentCrawlerBiz attachmentCrawlerBiz;
	private MgDatumBiz mgDatumBiz;
//	private TransferConfigBiz tcBiz;
	
	private final AtomicLong successCount = new AtomicLong(0); // 成功数
	private final AtomicLong errorCount = new AtomicLong(0); // 失败数
	private int PAGE_SIZE = 100;
	
	MetaFormater formater = new XMLFormater();
	
	Connection con = null;  
    ResultSet rs = null;  
    Statement stmt = null;
	
	public TransferSpider(){
		this.jobMonitorService = AdminConfigUtils.getBean("taskJobMonitorBiz");
    	this.quartzScheduleService = AdminConfigUtils.getBean("quartzScheduleBiz");
    	this.attachmentCrawlerBiz =  AdminConfigUtils.getBean("attachmentCrawlerBiz");
    	this.mgDatumBiz =  AdminConfigUtils.getBean("mgDatumBiz");
//    	this.tcBiz =  AdminConfigUtils.getBean("tcBiz");
	}
	
	/**
	 * 构建实例方法
	 * @return
	 */
	public static TransferSpider create(){
		return new TransferSpider();
	}
	
	public TransferSpider setTaskJob(TaskJob taskJob) {
		this.taskJob = taskJob;
		return this;
	}
	public TransferSpider setMarkPage(String markPage) {
		this.markPage = Integer.parseInt(markPage);
		return this;
	}
	
	public TransferSpider setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}
	/**
	 * 获取导出数据库链接
	 */
	public TransferSpider getconnection(TransferConfig tc){
		if(tc!=null){
			//获取xml解析对象
			Dom4jXmlUtil dm=new Dom4jXmlUtil();
			
			//获取MD5加密对象
			CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
			
			//将数据库中的xml文件转换成map集合
			Map<String, String> map=dm.generateMap(tc.getLinkContent());
			String url=map.get("url");
			String port=map.get("port");
			String DBtype=map.get("type");
			String username=tc.getUsername();
			String password=md5Utils.Decryption(tc.getPassword());
			String dataname=map.get("name");
			
			//获取数据库链接
			con=new JdbcConnectionUtil().getconnection(DBtype, url, port, dataname, username, password);
			if(con!=null){
				try {
					stmt = con.createStatement();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return this;
	}

	/**
	 * 注册到线程池中
	 */
	public void register(){
		SpiderManager.register(this.taskJob.getId(), this);
	}
	
	/**
	 * 主方法，业务实现
	 */
	@Override
	public void execute() {
		String taskId = taskJob.getId(); // 传输任务 ID
		String connectId = taskJob.getConnectId(); // 转换任务 ID
		try {
			List<KM_Datum> kmList = null;
			List<KnowledgeMeta> transferList = null;
			List transferIds = null;
			JSONArray jsonArray = null;
			JSONObject jsonObject = null;
			String reuslt = "";
			KnowledgeMeta knowledgeMate = null;
			String taskName = taskJob.getName();
			while(true) {
				if(spiderMonitor()) break;
				// 一次取100条数据
				/*ListPageQuery list=new ListPageQuery();
				list.setPage(1);
				list.setRows(100);
				List<Condition> cons = new ArrayList<Condition>();
				Condition con=Condition.parseCondition("taskId_string_eq");
				con.setValue(connectId);
				cons.add(con);
				Condition con1=Condition.parseCondition("transferFlag_string_eq");
				con1.setValue("0");
				cons.add(con1);
				kmList = knowledgeMetadataBiz.find(list.getPagination(), cons);*/
				
				int skip = (markPage-1) * PAGE_SIZE;
				kmList = mgDatumBiz.findByCondition(
						new Criteria("taskId").is(connectId), skip,
						PAGE_SIZE);
//				mgKnowledgeMetadataBiz.findByCondition(criteria, skip, limit);
				
				
				transferList = new ArrayList<KnowledgeMeta>();
				transferIds = new ArrayList();
				if(kmList == null || kmList.size() == 0){
					// 查不到数据
					MarkBreakpoint.writeBreakPage(filePath, "1");
					// 更新传输任务状态为结束
					quartzScheduleService.updateTaskJobState(taskJob.getId(), JobModel.STATE_STOP);
					break;
				}
				// 组织数据，并传输
				String kmMate = "";
				List<AttachmentCrawler> attachmentList = null;
				List<AttachmentMeta> attachmentMetaList = null;
				// insert int Table_Name('title','publishTime') values ('标题','发布时间')
				String appendSQL = "";
				String[] keys = {"标题","发布时间"};
				for (KM_Datum km : kmList) {
					String insertSQL = "insert into DataTest(title,publishTime) values(";
					if(km.getKvs()!=null){
						Map kvs = km.getKvs().toMap();
						for(int i = 0; i < keys.length; i++){
							insertSQL+="'"+ kvs.get(keys[i]) +"',";
						}
						insertSQL = insertSQL.substring(0, insertSQL.length()-1);
						insertSQL += ");";
						if(con !=null){
							//创建Statement对象
							int result = stmt.executeUpdate(insertSQL);
							if(result==1){
								successCount.incrementAndGet();
							}else{
								errorCount.incrementAndGet();
							}
						}
					}
					
					attachmentList = attachmentCrawlerBiz.findAllByDatumId(km.getId());
					if(attachmentList!= null && attachmentList.size()>0){
						attachmentMetaList = new ArrayList<AttachmentMeta>();
						AttachmentMeta am = null;
						for(AttachmentCrawler ac :attachmentList){
							//只处理附件类型为pdf和doc/docx的
							String fileType = ac.getType().substring(ac.getType().lastIndexOf(".")+1);
							if(!fileType.equals("pdf") && !fileType.equals("doc") && !fileType.equals("docx")){
								continue;
							}
							am = new AttachmentMeta();
							am.setFileId(ac.getPath());
							am.setFileName(ac.getName()+ac.getType());
							am.setFileServerPath(AppConfigUtils.get("crawlerIp")+":"+AppConfigUtils.get("crawlerPort"));
							am.setTxtId(ac.getTxt());
							if(ac.getFileSize()!=null){
								am.setFileSize(Long.valueOf(ac.getFileSize()));
							}else{
								am.setFileSize(0);
							}
							attachmentMetaList.add(am);
						}
						knowledgeMate.setAttachment(attachmentMetaList);
					}
					/*knowledgeMate.setBatchId(batchId);
					transferList.add(knowledgeMate);
					transferIds.add(km.getId().trim());*/
				}
				// 传输数据，返回数据格式为：示例：[1000,1001]
				/*reuslt = externalRestService.saveMetadataList(transferList);
				// 返回成功和失败数处理
				if(reuslt != null && StringUtils.isNotBlank(reuslt) && !reuslt.equals("[]")){
					// 去掉 [ ] 中括号
					reuslt = reuslt.substring(1, reuslt.length() - 1);
					String[] success = reuslt.split(",");
					List listIds = new ArrayList();
					for(int i = 0; i < success.length ;i++){
						listIds.add(success[i].toString().trim());
						transferIds.remove(success[i].toString().trim());
					}
//					knowledgeMetadataBiz.updateKnowledgeMdTransferSucess(listIds);
					mgKnowledgeMetadataBiz.updateBatchTransferFlag("1", listIds);
					successCount.addAndGet(success.length); // 成功数
					// 设置失败数
					int error = kmList.size() - success.length;
					if(error > 0){
//						knowledgeMetadataBiz.updateKnowledgeMdTransferFailed(transferIds);
						mgKnowledgeMetadataBiz.updateBatchTransferFlag("2", transferIds);
						errorCount.addAndGet(error);
					}
				}else{
					// 全部传输失败
					mgKnowledgeMetadataBiz.updateBatchTransferFlag("2", transferIds);
					errorCount.addAndGet(transferIds.size());
				}*/
				/*if(kmList!=null && kmList.size()==100){
					markPage ++; // 自增一页
				}*/
				markPage ++; // 自增一页
				// 记录断点位置
				MarkBreakpoint.writeBreakPage(filePath, markPage+"");
				
				// 更新成功传输数据功能
				jobMonitorService.updateFullAndFailed(taskId,
						Integer.parseInt(successCount.toString()),
						Integer.parseInt(errorCount.toString()));
			}
			if(con!=null){
				con.close();
			}
		
		} catch (Exception e) {
			// 更新传输任务状态为暂停
			quartzScheduleService.markTaskJobError(taskJob.getId(), JobModel.STATE_PAUSE,JobModel.JOB_CONECTION_ERROR);
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		String s = "fdsfd,dfs,";
		s = s.substring(0,s.length()-1);
		System.out.println(s);
	}
}
