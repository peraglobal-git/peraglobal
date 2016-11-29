package com.peraglobal.km.crawler.db.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;
import com.peraglobal.km.crawler.db.biz.MetaDataBuilder.FileDataField;
import com.peraglobal.km.crawler.db.biz.MetaDataBuilder.MetaDataWrapper;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobMonitorBiz;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.web.biz.AttachmentCrawlerBiz;
import com.peraglobal.km.crawler.web.biz.DatumBiz;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.km.mongo.model.KM_Datum;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.common.id.IDGenerate;
@Scope("prototype")
@Component("metaDataDBHandler")
public class MetaDataDBHandler  {
	@Resource
	private DatumBiz datumBiz;
	@Resource 
	private AttachmentCrawlerBiz attachmentCrawlerBiz;
	@Resource
	private TaskJobBiz taskJobBiz;
	@Resource
	private MgDatumBiz mgDatumBiz;
	private  AtomicLong successCount = new AtomicLong(0);
    private  AtomicLong failCount = new AtomicLong(0);
    private boolean isAdd = false;
    private boolean isUpdated = false;
    //修改成功数
   	private void monitor(MetaDataWrapper metaData){
   		((TaskJobMonitorBiz)AdminConfigUtils.getBean("taskJobMonitorBiz")).updateFullAndFailed(metaData.getTaskId(),
   				Integer.parseInt(String.valueOf(successCount)),
   				Integer.parseInt(String.valueOf(failCount)));
   	}
   	protected void onSuccess() {
		successCount.incrementAndGet();
    }
	 protected void onError() {
		 failCount.incrementAndGet();
	}
	public void storage(final MetaDataWrapper metaData){
		try{
//			Datum d = datumBiz.getDatumByPK(metaData.getTaskId(), metaData.getPk());
			Query query = new Query();
			query.addCriteria(new Criteria("dbpk").is(metaData.getPk()).and("taskId").is(metaData.getTaskId()));
			List<KM_Datum> kmDatumList = this.mgDatumBiz.findByCondition(query);
//			KM_Datum _d = (KM_Datum)datumBiz.getDatumByUrl(map);
			KM_Datum d = null;
			if(kmDatumList != null && kmDatumList.size() > 0){
				d = kmDatumList.get(0);
			}
			if(null==d){//创建
				String uuid = IDGenerate.uuid();
				AttachmentCrawler a =null;
				List<FileDataField> rowFileDataFields = metaData.getRowFileDataFields();
				List<String> attachmentIds = new ArrayList<String>();
				for(FileDataField fileDataField :rowFileDataFields){
					 a = new AttachmentCrawler();
					//a.setId(IDGenerate.uuid());
					a.setDatumID(uuid);
					String 	fileType = fileDataField.getFileTypeValue();
					if(null!=fileType&&fileType.indexOf(MetaDataWrapper.FILETPYEDOT)==-1){
						fileType = 	MetaDataWrapper.FILETPYEDOT+fileType;
					}
					a.setPath(fileDataField.getFileNameValue());
					a.setName(fileDataField.getName()+"."+fileDataField.getFileTypeValue());
					a.setTaskId(metaData.getTaskId());
					a.setType("."+fileDataField.getFileTypeValue());
				}
				d = new KM_Datum();
				d.setId(uuid);
				d.setTaskId(metaData.getTaskId());
				d.setKvs(metaData.getObjKvs());
				d.setIsFull(Dom4jXmlUtil.checkIsFull(metaData.getObjKvs()));
				d.setDbpk(metaData.getPk());
				d.setMd5(metaData.getMd5());
				d.setCreateDate(new Date());
				mgDatumBiz.insert(d);
				/*d =new Datum();
				d.setId(uuid);
				d.setTaskId(metaData.getTaskId());
				d.setKvs(metaData.getKvs().getBytes("GBK"));
				d.setDbpk(metaData.getPk());
				d.setMd5(metaData.getMd5());
//				if(null!=attachmentIds&&attachmentIds.size()!=0){
//					d.setAttachmentIds(attachmentIds.toArray(new String[attachmentIds.size()] ));
//				}
				datumBiz.add(d);*/
				if(a!=null){
					attachmentCrawlerBiz.save(a);
					attachmentIds.add(a.getId());
				}
				onSuccess();
				isAdd = true;
				/*new Thread(new Runnable(){
						@Override
						public void run() {
							metaData.writeToFile();
						}
					}
					).start();*/
				
				
			}else if(metaData.getTaskId().equals(d.getTaskId())
						&&!metaData.getMd5().equals(d.getMd5())
						&&metaData.getPk().equals(d.getDbpk())){//更新
				
				
				List<String> attachmentIds = new ArrayList<String>();
				List<FileDataField> rowFileDataFields = metaData.getRowFileDataFields();
				for(FileDataField fileDataField :rowFileDataFields){
					//更新文件
					String 	fileType = fileDataField.getFileTypeValue();
					if(null!=fileType&&fileType.indexOf(metaData.FILETPYEDOT)==-1){
						fileType = 	metaData.FILETPYEDOT+fileType;
					}
					String dataFilePath=metaData.getDataFilePath()+fileDataField.getFileNameValue()+fileType;
					/*if(null!=fileDataField.getDataTempPath()){
						File file = new File(fileDataField.getDataTempPath());
						if(file.exists()){
							InputStream input;
							try {
								input = new FileInputStream(file);
								metaData.writeToFile(input,dataFilePath);
							} catch (FileNotFoundException e) { 
								e.printStackTrace();
							} catch (UnsupportedEncodingException e) { 
								e.printStackTrace();
							}
							file.delete();//清除临时文件
						}
					}*/
					
					//文件数据入库
					for(String attachmentId :d.getAttachmentIds()){
						AttachmentCrawler a = attachmentCrawlerBiz.findById(attachmentId);
						if(null==a){
							a= new AttachmentCrawler();
							a.setId(IDGenerate.uuid());
							a.setDatumID(d.getId());
							fileType = fileDataField.getFileTypeValue();
							if(null!=fileType&&fileType.indexOf(MetaDataWrapper.FILETPYEDOT)==-1){
								fileType = 	MetaDataWrapper.FILETPYEDOT+fileType;
							}
							a.setPath(metaData.getDataFilePath()+fileDataField.getFileNameValue()+fileType);
							a.setName(fileDataField.getFileNameValue());
							a.setTaskId(metaData.getTaskId());
							a.setType(fileType);
//							String aid = generalDao.save(a);
							attachmentCrawlerBiz.add(a);
							attachmentIds.add(a.getId());
						}else{
							
							fileType = fileDataField.getFileTypeValue();
							if(null!=fileType&&fileType.indexOf(MetaDataWrapper.FILETPYEDOT)==-1){
								fileType = 	MetaDataWrapper.FILETPYEDOT+fileType;
							}
							a.setPath(metaData.getDataFilePath()+fileDataField.getFileNameValue()+fileType);
							a.setName(fileDataField.getFileNameValue());
							a.setType(fileType);
							attachmentCrawlerBiz.updateIncrement(a);
						}
						
					}
				}
				
				
//				d.setKvs(metaData.getKvs().getBytes("GBK"));//创建元数据xml
				d.setKvs(metaData.getObjKvs());
				d.setIsFull(Dom4jXmlUtil.checkIsFull(metaData.getObjKvs()));
				d.setMd5(metaData.getMd5());
				//更新
				if(null!=attachmentIds&&attachmentIds.size()!=0){
					String[] oldAttachIds = d.getAttachmentIds();
					String[] newAttachIds = attachmentIds.toArray(new String[attachmentIds.size()]);
					d.setAttachmentIds(getMergeArray(oldAttachIds,newAttachIds));
				}
//				datumBiz.save(d);
				this.mgDatumBiz.save(d);
				onSuccess();
				isAdd = true;
				}
			if(isAdd&&!isUpdated){
				// 更新采集任务的转换任务(状态为停止)：有新数据提醒
				this.taskJobBiz.updateJobHaveNewData(((KM_Datum) d).getTaskId());
				isUpdated = true;
			}
			}catch(Exception e1){
				e1.printStackTrace();
				onError();
			}finally{
				monitor(metaData);
			}
		  
	}
	public String[] getMergeArray(String[] al,String[] bl) {
		  String[] a = al;
		  String[] b = bl;
		  String[] c = new String[a.length + b.length];
		  System.arraycopy(a, 0, c, 0, a.length);
		  System.arraycopy(b, 0, c, a.length, b.length);
		  return c;
		 }
}
