package com.peraglobal.km.crawler.util;

import org.quartz.JobExecutionContext;

/**
 * 2015-7-1
 * @author yongqian.liu
 * @see 任务调度接口，对外使用不同采集类型实现不同的业务场景
 */
public interface QuartzTaskJobScheduler {

	/**
	 * 开始任务
	 * @param context 任务上下文
	 */
	public void start(JobExecutionContext context);
	
	/**
	 * 暂停任务
	 * @param context 任务上下文
	 */
	public void pause(JobExecutionContext context);
	
	/**
	 * 停止任务
	 * @param context 任务上下文
	 */
	public void stop(JobExecutionContext context);
}
