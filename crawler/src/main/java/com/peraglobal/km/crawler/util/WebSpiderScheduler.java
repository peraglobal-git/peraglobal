package com.peraglobal.km.crawler.util;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.web.biz.GeneralProcessor;
import com.peraglobal.km.crawler.web.biz.OraclePipeline;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.km.crawler.web.webmagic.Spider;
import com.peraglobal.km.crawler.web.webmagic.scheduler.FileCacheQueueScheduler;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

@Service("webSpiderScheduler")
public class WebSpiderScheduler implements QuartzTaskJobScheduler {

	@Override
	public void start(JobExecutionContext context) {
		// 获得任务对象
    	TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		// 任务状态为开始时创建采集任务
		if (taskJob != null
				&& taskJob.getJobState().equals(JobModel.STATE_STRAT)) {
			
			// 采集规则
			Map<String, Map<String, List<Map<String, Object>>>> ruleMap = (Map<String, Map<String, List<Map<String, Object>>>>) context
					.getMergedJobDataMap().get(JobModel.RULE_KEY);
			
			// 采集附加属性
			AttachProperty attachProperty = context.getMergedJobDataMap().get(JobModel.ATTACH_PROPERTY_KEY) != null ? (AttachProperty) context
					.getMergedJobDataMap().get(JobModel.ATTACH_PROPERTY_KEY)
					: null;
			taskJob.setAttachProperty(attachProperty);
//			String seedUrl = ruleMap.get(Rule.SEED_URL).get(Rule.RULE_TYPE_URL).get(0).get("rule").toString();
			
			String[] seedUrls;
			List<Map<String,Object>> seedList = ruleMap.get(Rule.SEED_URL).get(Rule.RULE_TYPE_URL);
			if(seedList != null && seedList.size() > 0){
				/*seedUrls = new String[seedList.size()];
				for(int i = 0; i < seedList.size(); i++){
					Map<String,Object> seed = seedList.get(i);
					seedUrls[i] = seed.get("rule").toString();
				}*/
				seedUrls = seedList.get(0).get("rule").toString().split(",");
				
				Spider.create(new GeneralProcessor(context)).setTaskJob(taskJob)
				.addPipeline(new OraclePipeline())
//				.addPipeline(new FilePipeline("D:\\data\\fileDownload"))
				.scheduler(new FileCacheQueueScheduler(AppConfigUtils.get("conf.filePath")))
				.setContext(context)
				.addUrl(seedUrls).thread(attachProperty!=null ? attachProperty.getThreadNum() : 1).register();
				SpiderManager.start(taskJob.getId());
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
