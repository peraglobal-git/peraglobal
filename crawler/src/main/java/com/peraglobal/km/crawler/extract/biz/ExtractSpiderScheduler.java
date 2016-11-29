package com.peraglobal.km.crawler.extract.biz;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.db.biz.SpiderManager;
import com.peraglobal.km.crawler.task.dao.TaskJobDao;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskRule;
import com.peraglobal.km.crawler.util.MarkBreakpoint;
import com.peraglobal.km.crawler.util.QuartzTaskJobScheduler;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

@Service("extractSpiderScheduler")
public class ExtractSpiderScheduler implements QuartzTaskJobScheduler {
	
	@Resource
	private TaskJobDao taskJobDao;
	
	@Override
	public void start(JobExecutionContext context) {
		// 获得任务对象
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(
				JobModel.JOB_KEY);
		Map map = new HashMap();
		map.put("jobId", taskJob.getId());
		TaskRule taskRule = taskJobDao.getTaskRuleByJobId(map);

		String filePath = AppConfigUtils.get("conf.extract.breakPage") + taskJob.getId()
				+ ".txt";
		// 断点位置 如果没有记录返回 1
		String markPage = MarkBreakpoint.readBreakPage(filePath);

		ExtractSpider.create().setTaskJob(taskJob).setTaskRule(taskRule)
				.setMarkPage(markPage).setFilePath(filePath).register();
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
		String filePath = AppConfigUtils.get("conf.extract.breakPage") + taskJob.getId()
				+ ".txt";
		//查不到数据
		MarkBreakpoint.writeBreakPage(filePath, "1");
	}

}
