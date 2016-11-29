package com.peraglobal.km.crawler.util;

import javax.annotation.Resource;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import com.peraglobal.km.crawler.db.biz.DBSpider;
import com.peraglobal.km.crawler.extract.biz.ExtractSpiderScheduler;
import com.peraglobal.km.crawler.local.biz.ExcelSpiderScheduler;
import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.transfer.biz.TransferSpiderScheduler;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;

/**
 * 2015-7-1
 * @author yongqian.liu
 * @see 任务调度器程序人口
 */
@DisallowConcurrentExecution
public class QuartzJobFactory implements Job {
	
	@Resource
    private QuartzTaskJobScheduler quartzScheduler;
	
	@Resource
    private QuartzScheduleBiz quartzScheduleBiz;
	
	/**
	 * 所有任务触发操作执行入口
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
    	// 获得任务对象
    	TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(JobModel.JOB_KEY);
		System.out.println(taskJob.getId());
        this.quartzScheduleBiz = AdminConfigUtils.getBean("quartzScheduleBiz");
        
        // 如果是手动触发任务则不处理定时任务
        if(taskJob.isHand()){
        	// 判断是否为手动触发任务后，恢复任务的是否手动触发任务标记
        	taskJob.setHand(false);
        	context.getMergedJobDataMap().put(JobModel.JOB_KEY, taskJob);
        }else{
        	
        	// 处理定时任务，判断是开始任务或停止任务
        	quartzScheduleBiz.fixedTimeUpJob(taskJob.getId());
        }
        
        /******根据不同类型的任务调度不同的节点实现*****/
		if(taskJob.getRegisterType().equals(JobModel.TYPE_WEB)){
			// WEB 采集接口实现
			quartzScheduler = (QuartzTaskJobScheduler) AdminConfigUtils
					.getBean("webSpiderScheduler");
		}else if(taskJob.getRegisterType().equals(JobModel.TYPE_DB)){
			// DB 采集接口实现
			quartzScheduler = (QuartzTaskJobScheduler) AdminConfigUtils
					.getBean("dBSpider");
		}else if(taskJob.getRegisterType().equals(JobModel.TYPE_LOCAL)){
			// 本地采集接口实现
			quartzScheduler = (QuartzTaskJobScheduler) AdminConfigUtils
					.getBean("excelSpiderScheduler");
		}else if(taskJob.getRegisterType().equals(JobModel.TYPE_EXTRACT)){
			// 转换任务接口实现
			quartzScheduler = (QuartzTaskJobScheduler) AdminConfigUtils
					.getBean("extractSpiderScheduler");
		}else if(taskJob.getRegisterType().equals(JobModel.TYPE_TRANSFER)){
			// 传输任务接口实现
			quartzScheduler = (QuartzTaskJobScheduler) AdminConfigUtils
					.getBean("transferSpiderScheduler");
		}
        
        /******根据不同的状态调用不同的处理*****/
		if(taskJob.getJobState().equals(JobModel.STATE_STRAT)){
			// 开始任务之前判断开始任务是否超出30个
//			boolean up = quartzScheduleBiz.startBeforeJob(taskJob);
//			if(up){
//				quartzScheduler.start(context); // 开始任务
//			}
			quartzScheduler.start(context); // 开始任务
		}else if(taskJob.getJobState().equals(JobModel.STATE_PAUSE)){
			// 暂停任务
        	quartzScheduler.pause(context);
//        	quartzScheduleBiz.startAfterJob(); // 开启优先级高的任务 （去掉此逻辑by jingxd 2016-4-8）
		}else if(taskJob.getJobState().equals(JobModel.STATE_STOP)){
			// 停止任务
			quartzScheduler.stop(context);
//			quartzScheduleBiz.startAfterJob(); // 开启优先级高的任务 （去掉此逻辑by jingxd 2016-4-8）
		}
    }
}
