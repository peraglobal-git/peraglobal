package com.peraglobal.km.crawler.util;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.peraglobal.km.crawler.quartz.biz.QuartzScheduleBiz;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;

/**
 * 2015-7-1
 * @author yongqian.liu
 * @see 重写 spring 监听器在服务启动或者新增加一个线程时会触发
 * 此类主要用于容器启动注入所有bean后初始化任务调度器并加载所有任务
 */
public class QuartzTaskJobProcessor implements ApplicationListener<ContextRefreshedEvent> {

	@Resource
    private QuartzScheduleBiz quartzScheduleBiz;
	
	// 是否初始化
	private static boolean isStart = false;
	 
	/**
	 * SpringMvc 工程启动时注入所有任务，此方法会重复调用多次，
	 * 使用静态变量控制任务只在系统启动时，触发加载任务工作
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		// 系统启动时执行
		if (isStart) { 
			isStart= false;
			
			// 初始化任务
			quartzScheduleBiz.initJob();
			
			// 启动调度器
			quartzScheduleBiz.start();
		}
	}
}
