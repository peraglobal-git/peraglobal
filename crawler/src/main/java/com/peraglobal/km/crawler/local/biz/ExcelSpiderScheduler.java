package com.peraglobal.km.crawler.local.biz;

import java.io.File;


import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.LocalEntity;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.util.DeCompressUtil;
import com.peraglobal.km.crawler.util.QuartzTaskJobScheduler;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
@Service("excelSpiderScheduler")
public class ExcelSpiderScheduler implements QuartzTaskJobScheduler {
	
	@Override
	public void start(JobExecutionContext context) {
		// 获得任务对象
    	TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
    	LocalEntity localEntity = (LocalEntity)context.getMergedJobDataMap().get(JobModel.RULE_KEY);
    	if(localEntity == null){
    		return;
    	}		
    	// 是否专家采集
    	String excelProperties = localEntity.getExcelProperties();
		// 多sheet页合并数据标识
    	String metaKey = localEntity.getMetaKey();
		// 得到上传的文件的文件名
		String fileName = localEntity.getFileName();
		// 得到上传文件的文件类型
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		
		if (fileType.equals("xls") || fileType.equals("xlsx")) {
			// 得到excel的文件流
			boolean isExpert = excelProperties.equals("expert") ? true : false;
			ExcelSpider.create()
					.setFileName(AppConfigUtils.get("conf.excelPath") + taskJob.getId() + "/" + fileName)
					.setExpert(isExpert).setMetaKey(metaKey).setTaskJob(taskJob).register();
			SpiderManager.start(taskJob.getId());
		} else if (fileType.equals("rar") || fileType.equals("zip")) {
			try {
				// 解压压缩文件
				String destDir = AppConfigUtils.get("conf.excelPath") + taskJob.getId() + "/";
				File dir = new File(destDir);
				if (!dir.exists() || !dir.isDirectory()) {
					dir.mkdirs();
				}
				//文件解压
				DeCompressUtil.deCompress(AppConfigUtils.get("conf.excelPath")+ taskJob.getId() + "/" + fileName,destDir);
				Thread.sleep(2000);
				ExcelSpider.create()
						.setFileName(destDir + "知识导入模板.xls")
						.setTaskJob(taskJob).setMetaKey(metaKey).register();
				SpiderManager.start(taskJob.getId());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void pause(JobExecutionContext context) {
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		
		String jobId = taskJob.getId();
		String jobState =  taskJob.getJobState();
		if(JobModel.STATE_PAUSE.equals(jobState)){
			SpiderManager.puase(jobId);
		}
	}

	@Override
	public void stop(JobExecutionContext context) {
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		String jobId = taskJob.getId();
		String jobState =  taskJob.getJobState();
		if(JobModel.STATE_STOP.equals(jobState)){
			SpiderManager.stop(jobId);
		}
	}

}
