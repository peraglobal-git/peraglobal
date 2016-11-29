package com.peraglobal.km.crawler.web.biz;


import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.web.dao.AttachmentCrawlerDao;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.core.BaseBiz;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
/**
 * 2016-1-8
 * @author xiaodan.jing
 * 附件逻辑处理类
 */
@Service("attachmentCrawlerBiz")
public class AttachmentCrawlerBiz extends BaseBiz<AttachmentCrawler,AttachmentCrawlerDao> {
	@Resource
	private AttachmentCrawlerDao attachmentCrawlerDao;
	@Resource
	private MgDatumBiz mgDatumBiz;
	/**
	 * 功能：根据任务ID和mongo objectId获取附加
	 * 作者：井晓丹 2016-3-22
	 * @param map
	 * @return
	 */
	public AttachmentCrawler getAttachmentByFilePath(String taskId,String filePath){
		Map<String,String> map = new HashMap<String,String>();
		map.put("taskId", taskId);
		map.put("path", filePath);
		List<AttachmentCrawler> list = attachmentCrawlerDao.getAttachmentByFilePath(map);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 功能：删除采集附件
	 * 作者 段政 2016-3-21
	 * @param list
	 * @return
	 */
	public int deleteATTACHMENTCRAWLERByList(List<String> list){
		//Mongo db;
		try {
			//db = new Mongo(AppConfigUtils.get("km.mongo.address"), Integer.valueOf(AppConfigUtils.get("km.mongo.port")));
			//DB mydb = db.getDB(AppConfigUtils.get("km.mongo.dbName"));
			//GridFS myFS = new GridFS(mydb);
			ObjectId mongoId=null;
			for (int i = 0; i < list.size(); i++) {
				String taskId=list.get(i);
				Condition condition = Condition.parseCondition("taskid_string_eq");
				condition.setValue(taskId);
				List<AttachmentCrawler> crawler=find(condition);
				//判断是否存在附件
				if(crawler!=null){
					for (AttachmentCrawler attachmentCrawler : crawler) {
						String pathId=attachmentCrawler.getPath();
						if(pathId!=null && pathId!=""){
							mongoId=new ObjectId(pathId);
							mgDatumBiz.deleteFiles(mongoId);
							//myFS.remove(mongoId);
						}
					}
				}
			}
			//db.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attachmentCrawlerDao.deleteATTACHMENTCRAWLERByList(list);
		
	}
	/**
	 * 根据元数据获取附件列表
	 * @param datumId
	 * @return
	 */
	public List<AttachmentCrawler> findAllByDatumId(String datumId){
		Map<String,String> map = new HashMap<String,String>();
		map.put("datumId", datumId);
		List<AttachmentCrawler> list = attachmentCrawlerDao.findAllByDatumId(map);
		return list;
	}
	
}
