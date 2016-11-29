package com.peraglobal.km.crawler.web.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.km.crawler.web.model.Picture;
import com.peraglobal.km.crawler.web.webmagic.ResultItems;
import com.peraglobal.km.crawler.web.webmagic.Task;
import com.peraglobal.km.crawler.web.webmagic.pipeline.Pipeline;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;

public class OraclePipeline implements Pipeline{
	private static Logger logger = LogManager.getLogger(OraclePipeline.class);
	private TaskJobMonitorBiz jobMonitorBiz;
	private String filePath;
	private final AtomicLong pageCount = new AtomicLong(0);
    private final AtomicLong failPageCount = new AtomicLong(0);
    private boolean isAdd = false;
    private boolean isUpdated = false;
	@Resource
    private DatumBiz datumBiz;
	@Resource
	AttachmentCrawlerBiz attachmentCrawlerBiz;
	@Resource
	private TaskJobBiz taskJobBiz;
	@Resource
    private MgDatumBiz mgDatumBiz;
	
	public String getFilePath() {
		return filePath;
	}

	public TaskJobMonitorBiz getJobMonitorService() {
		return jobMonitorBiz;
	}

	public void setJobMonitorService(TaskJobMonitorBiz jobMonitorService) {
		this.jobMonitorBiz = jobMonitorService;
	}

	public OraclePipeline(){
		this.jobMonitorBiz = AdminConfigUtils.getBean("taskJobMonitorBiz");
		this.datumBiz = AdminConfigUtils.getBean("datumBiz");
		this.attachmentCrawlerBiz = AdminConfigUtils.getBean("attachmentCrawlerBiz");
		this.taskJobBiz = AdminConfigUtils.getBean("taskJobBiz");
		this.mgDatumBiz = AdminConfigUtils.getBean("mgDatumBiz");
	}
	
	public void process(ResultItems resultItems, Task task) {
		Object d = resultItems.get("$_meta");

		if (d != null){
			if (d instanceof KM_Datum) {
				Document doc = Jsoup.parse(((KM_Datum) d).getKvs().toString());
				Elements imgEles = doc.getElementsByTag("img");
				for (Element imgEle : imgEles) {
					//是否提取图片
//					this.saveImage(imgEle.attr("src"),task,(Datum)d);
				}
				/*Map<String,String> map = new HashMap<String,String>();
				map.put("taskId", ((KM_Datum) d).getTaskId());
				map.put("url", ((KM_Datum) d).getUrl());*/
				Query query = new Query();
				query.addCriteria(new Criteria("url").is(((KM_Datum) d).getUrl()).and("taskId").is(((KM_Datum) d).getTaskId()));
				List<KM_Datum> kmDatumList = this.mgDatumBiz.findByCondition(query);
//				KM_Datum _d = (KM_Datum)datumBiz.getDatumByUrl(map);
				KM_Datum _d = null;
				if(kmDatumList != null && kmDatumList.size() > 0){
					_d = kmDatumList.get(0);
				}
				try {
					if(_d == null){
						mgDatumBiz.insert((KM_Datum) d);
						isAdd = true;
					}else if(_d !=null && !_d.getMd5().equals(((KM_Datum) d).getMd5())){
						((KM_Datum) d).setId(_d.getId());
						
						/*Query query1 = new Query();
				        query1.addCriteria(new Criteria("_id").is(_d.getId()));
				        
				        Update update = new Update();
				        update.set("md5", ((KM_Datum) d).getMd5());
				        
				        mgDatumBiz.update(query1, update);*/
						mgDatumBiz.save((KM_Datum) d);
						isAdd = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
				pageCount.incrementAndGet();
				
				//修改成功数
				jobMonitorBiz.updateFullAndFailed(((KM_Datum) d).getTaskId(),
						Integer.parseInt(pageCount.toString()),
						Integer.parseInt(failPageCount.toString()));
				if(isAdd&&!isUpdated){
					// 更新采集任务的转换任务(状态为停止)：有新数据提醒
					this.taskJobBiz.updateJobHaveNewData(((KM_Datum) d).getTaskId());
					isUpdated = true;
				}
			} else {
//				attachmentCrawlerBiz.save((AttachmentCrawler) d);
				AttachmentCrawler _a = attachmentCrawlerBiz.getAttachmentByFilePath(((AttachmentCrawler) d).getTaskId(), ((AttachmentCrawler) d).getPath());
				if(_a == null){
					attachmentCrawlerBiz.save((AttachmentCrawler) d);
				}else{
					((AttachmentCrawler) d).setId(_a.getId());
					attachmentCrawlerBiz.save((AttachmentCrawler) d);
				}
				
			}
		}
	}
	
	/*@Override
	public void process(ResultItems resultItems, Task task) {
		Object d = resultItems.get("$_meta");

		if (d != null){
			if (d instanceof Datum) {
				Document doc = Jsoup.parse(((Datum) d).getKvs().toString());
				Elements imgEles = doc.getElementsByTag("img");
				for (Element imgEle : imgEles) {
					//是否提取图片
//					this.saveImage(imgEle.attr("src"),task,(Datum)d);
				}
				Map<String,String> map = new HashMap<String,String>();
				map.put("taskId", ((Datum) d).getTaskId());
				map.put("url", ((Datum) d).getUrl());
				Datum _d = (Datum)datumBiz.getDatumByUrl(map);
				if(_d == null){
					datumBiz.add((Datum) d);
					isAdd = true;
				}else if(_d !=null && !_d.getMd5().equals(((Datum) d).getMd5()) && !_d.getKvs().equals(((Datum) d).getKvs())){
					((Datum) d).setId(_d.getId());
					datumBiz.save((Datum) d);
					isAdd = true;
				}
				pageCount.incrementAndGet();
				
				//修改成功数
				jobMonitorBiz.updateFullAndFailed(((Datum) d).getTaskId(),
						Integer.parseInt(pageCount.toString()),
						Integer.parseInt(failPageCount.toString()));
				if(isAdd&&!isUpdated){
					// 更新采集任务的转换任务(状态为停止)：有新数据提醒
					this.taskJobBiz.updateJobHaveNewData(((Datum) d).getTaskId());
					isUpdated = true;
				}
			} else {
//				attachmentCrawlerBiz.save((AttachmentCrawler) d);
				AttachmentCrawler _a = attachmentCrawlerBiz.getAttachmentByFilePath(((AttachmentCrawler) d).getTaskId(), ((AttachmentCrawler) d).getPath());
				if(_a == null){
					attachmentCrawlerBiz.save((AttachmentCrawler) d);
				}else{
					((AttachmentCrawler) d).setId(_a.getId());
					attachmentCrawlerBiz.save((AttachmentCrawler) d);
				}
				
			}
		}
	}*/
	
	private void saveImage(String imgUrl, Task task, Datum datum){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(imgUrl);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream in = entity.getContent();
			System.out.println(this.filePath);
			String _folderPath = "D://data/ImgDown/" + task.getUUID() + "/";
	        File floder = new File(_folderPath);
	        if (!floder.exists()) {
	        	floder.mkdirs();
	        }
			
			String fileName = ConverterUtil.EncoderByMd5(imgUrl);
	        String fileType = imgUrl.substring(imgUrl.lastIndexOf(".")+1,imgUrl.length());
	        fileName = fileName + "." + fileType;
	        File file = new File(_folderPath + fileName);

			try {
			    FileOutputStream fout = new FileOutputStream(file);
			    int l = -1;
			    byte[] tmp = new byte[1024];
			    while ((l = in.read(tmp)) != -1) {
			        fout.write(tmp,0,l);
			    }
			    fout.flush();
			    fout.close();
			    
			    Picture pic = new Picture();
			    pic.setDatumID(datum.getId());
			    pic.setName(fileName);
			    pic.setType(fileType);
			    pic.setPath(_folderPath);
			    pic.setTaskId(task.getUUID());
//			    this.generalDao.save(pic);
			} finally {
			    in.close();
			    httpget.releaseConnection();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
