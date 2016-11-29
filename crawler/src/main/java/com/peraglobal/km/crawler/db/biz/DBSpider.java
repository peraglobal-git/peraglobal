package com.peraglobal.km.crawler.db.biz;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.db.model.DBSpiderRecord;
import com.peraglobal.km.crawler.db.model.SpiderConfiguration;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.util.QuartzTaskJobScheduler;
import com.peraglobal.pdp.core.utils.AppConfigUtils;
@Service("dBSpider")
public class DBSpider implements QuartzTaskJobScheduler {
	private SpiderConfiguration config;
	@Override
	public void start(JobExecutionContext context) {
		
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		String rulexml = (String) context.getMergedJobDataMap().get(JobModel.RULE_KEY);
		Map<String,Object> params = new HashMap<String, Object>();
		String jobId = taskJob.getId();
		params.put("taskId", jobId);
		params.put("metaDateFilePath", AppConfigUtils.get("conf.filePath"));
		params.put("dbRuleXml", rulexml);
		
		String jobState =  taskJob.getJobState();
		if(JobModel.STATE_STRAT.equals(jobState)){
			this.config = new SpiderConfiguration(params);
			new DataImporter(config).doImport();
			SpiderManager.start(jobId);
		}
	}

	@Override
	public void pause(JobExecutionContext context) {
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		
		String jobId = taskJob.getId();
		String jobState =  taskJob.getJobState();
		if(JobModel.STATE_PAUSE.equals(jobState)){
			SpiderManager.puase(jobId);
			SpiderManager.remove(jobId);
		}
	}

	@Override
	public void stop(JobExecutionContext context) {
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		String jobId = taskJob.getId();
		String jobState =  taskJob.getJobState();
		if(JobModel.STATE_STOP.equals(jobState)){
			SpiderManager.stop(jobId);
			SpiderManager.remove(jobId);
			if(null==config){
				String rulexml = (String) context.getMergedJobDataMap().get(JobModel.RULE_KEY);
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("taskId", jobId);
				params.put("metaDateFilePath", AppConfigUtils.get("conf.filePath"));
				params.put("dbRuleXml", rulexml);
				this.config = new SpiderConfiguration(params);
			}
			DBSpiderRecord.removeRecord(config.getRequestInfo().getSpiderRecordFilePath()+jobId+DBSpiderRecord.LOGFILETYPE,jobId);
		}
	}
	public static void main(String[] args) {
		Map<String,Object> params = new HashMap<String, Object>();
		//params.put("command","fullimport");
		params.put("taskId","001");
		params.put("metaDateFilePath", "/home/hadoop/data");
		//params.put("threadNum", 1);//设置线程池线程个数
		params.put("spiderRecordFilePath", "/home/hadoop/log");//爬虫过程log文件输出路径
		//params.put("fetchSize", 3);
		//params.put("logRate",2);
		new DataImporter(new SpiderConfiguration(params)).doImport();
		//SpiderGroupManager.startGroup(String.valueOf(params.get("taskId")));
		//SpiderGroupManager.shutdownGroup();
		SpiderManager.start(String.valueOf(params.get("taskId")));
		//SpiderManager.puase(String.valueOf(params.get("taskId")));
		//SpiderManager.recover(String.valueOf(params.get("taskId")));
		//SpiderManager.stop(String.valueOf(params.get("taskId")));
		SpiderManager.shutdown();
	}
}
