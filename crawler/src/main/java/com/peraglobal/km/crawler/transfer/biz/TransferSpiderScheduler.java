package com.peraglobal.km.crawler.transfer.biz;

import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.source.biz.KnowledgeSourceBiz;
import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.biz.TransferConfigBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TransferConfig;
import com.peraglobal.km.crawler.util.MarkBreakpoint;
import com.peraglobal.km.crawler.util.QuartzTaskJobScheduler;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
@Service("transferSpiderScheduler")
public class TransferSpiderScheduler implements QuartzTaskJobScheduler {
	
	@Resource
	private TaskJobBiz taskJobBiz;
	@Resource
	private KnowledgeSourceBiz sourceBiz;
	@Resource
	private TransferConfigBiz tcBiz;
	
	@Override
	public void start(JobExecutionContext context) {
		
		// 获得任务对象
    	TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
    	
    	TransferConfig tc = tcBiz.findByTaskId(taskJob.getId());
    	
    	// 记录当前传输任务标记文件路径
    	String filePath = AppConfigUtils.get("conf.transfer.breakPage") + taskJob.getId()
				+ ".txt";
    	
    	// 断点位置 如果没有记录返回 1
    	String markPage = MarkBreakpoint.readBreakPage(filePath);
    	
    	// 记录当前传输任务标记文件路径
    	String batchPath = AppConfigUtils.get("conf.transfer.breakPage") + taskJob.getId()
				+ "_batch.txt";
    	
    	// 传输批次 如果没有记录返回 1
    	String batchId = MarkBreakpoint.readBreakPage(batchPath);
    	
    	// 创建传输任务对象实例
		TransferSpider.create().setTaskJob(taskJob).getconnection(tc).setMarkPage(markPage)
			.setFilePath(filePath).register();
		
		SpiderManager.start(taskJob.getId());
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
		
		String filePath = AppConfigUtils.get("conf.transfer.breakPage") + taskJob.getId()
				+ ".txt";
		//查不到数据
		MarkBreakpoint.writeBreakPage(filePath, "1");
	}

}
