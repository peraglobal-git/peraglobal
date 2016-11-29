package com.peraglobal.km.crawler.util;

import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.JobDetail;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.task.model.TaskTrigger;

/**
 * 定时任务规则转换类
 * @author yongqian.liu
 */
public class CronExpConversion {

	/**
	 * 简单的触发器
	 * @param job 任务对象
	 * @return
	 */
	public static Trigger triggerToSimple(TaskJob job){
        Trigger trigger = TriggerBuilder
        		.newTrigger()
        		.withIdentity(job.getId() + JobModel.ID_START, job.getGroupId())
        		.build();
        return trigger;
	}

	/**
	 * 创建天定时触发器实现
	 * @param jobDetail 任务详细信息
	 * @param taskTrigger 定时任务对象
	 * @param TaskJob job 任务对象
	 * @param flgId 组合ID值
	 * @return 实现每天触发的
	 */
	public static Trigger triggerToOne(TaskJob job, JobDetail jobDetail, String dateTime) {
		/***************************
    	 * 功能：触发一次
    	 * 参数：秒 分 时 日 月 年
    	 * 示例：00 00 08 01 07 2015 触发时间在 2015-7-1 8:00:00
    	 ***************************/
		
		String time = dateTime.replaceAll("-", ":").replaceAll(" ", ":");
		String[] hms = time.split(":"); // 时 分 秒 转换字符串数组
		String cron = hms[5] + " " + hms[4] + " " + hms[3] + " " + hms[2] + " " + hms[1] + " ? " + hms[0];
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(job.getId() + JobModel.ID_START, job.getGroupId())
				.withSchedule(
						CronScheduleBuilder                                 
								.cronSchedule(cron)).forJob(jobDetail).build(); 
		return trigger;
	}
	
	/**
	 * 每天定时任务规则触发器
	 * @param job 任务对象
	 * @param jobDetail 在调度器中的任务详细对象
	 * @param taskTrigger 任务定时对象
	 * @param flgId 组合 ID：为实现开始和结束触发器生成使用
	 * @return 天规则的触发器对象
	 */
	public static Trigger triggerToDay(TaskJob job, JobDetail jobDetail, TaskTrigger taskTrigger, String flgId) {
    	/***************************
    	 * 功能：按照天触发任务
    	 * 参数：秒 分 时
    	 * 参数：隔多少天
    	 ***************************/
		
		// 判断是开始时间或结束时间
		String dateTime = flgId.equals(JobModel.ID_START) ? taskTrigger
				.getStartTime() : taskTrigger.getStopTime();
		
		// 格式化时间字符串		
		int[] hms = formatHms(dateTime);
		TimeOfDay timeOfDay = new TimeOfDay(hms[0], hms[1], hms[2]);
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(job.getId() + flgId, job.getGroupId())
				.withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
						.startingDailyAt(timeOfDay) // 触发时间
						.withIntervalInHours(24) // 间隔  space * 24 = space * (1 * 24) 
						.withRepeatCount(-1) // 一直执行
						).forJob(jobDetail).build();
		return trigger;
	}
	
	/**
	 * 每周定时任务规则触发器
	 * @param job 任务对象
	 * @param jobDetail 在调度器中的任务详细对象
	 * @param taskTrigger 任务定时对象
	 * @param flgId 组合 ID：为实现开始和结束触发器生成使用
	 * @return 周规则的触发器对象
	 */
	public static Trigger triggerToWeek(TaskJob job, JobDetail jobDetail, TaskTrigger taskTrigger, String flgId) {
    	/***************************
    	 * 功能：按照周触发任务
    	 * 参数：秒 分 时
    	 * 参数：周一、周二、周三、周四、周五、周六、周日
    	 * 示例：45 10 09 * * ? 2 触发时间在 每隔一周的周一 9:10:45 触发，是每隔一个周
    	 ***************************/
		
		// 判断是开始时间或结束时间
		String dateTime = flgId.equals(JobModel.ID_START) ? taskTrigger
				.getStartTime() : taskTrigger.getStopTime();
		
		// 规则表达式  + 时分秒
		String[] hms = dateTime.split(":");
		String cron = hms[2] + " " + hms[1] + " " + hms[0] + " ? * "; // ”时 分 秒 日 月 年“ 
		
		// 组合周表达式
		String[] weeks = taskTrigger.getTriggerWeek().split(",");
		for (int i = 0; i < weeks.length; i++) {
			if(null != weeks[i]){
				if((i + 1) != weeks.length){
					cron += weeks[i] + ","; // 周
				}else{
					cron += weeks[i];
				}
			}
		}
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(job.getId() + flgId, job.getGroupId())
				.withSchedule(
						CronScheduleBuilder                                 
								.cronSchedule(cron)).forJob(jobDetail).build();
		return trigger;
	}
	
	/**
	 * 每月定时任务规则触发器
	 * @param job 任务对象
	 * @param jobDetail 在调度器中的任务详细对象
	 * @param 任务定时对象
	 * @param flgId 组合 ID：为实现开始和结束触发器生成使用
	 * @return 月规则的触发器对象
	 */
	public static Trigger triggerToMonth(TaskJob job, JobDetail jobDetail, TaskTrigger taskTrigger, String flgId) {
    	/***************************
    	 * 功能：按照月的日或月的第几周的周几触发任务
    	 * 参数：秒 分 时
    	 * 参数：月 1-12
    	 * 参数：日 1-31
    	 * 参数：周 1-7
    	 * 示例：00 00 08 1,2 7,8 ? 触发时间在 7,8月的1,2号的 8:00:00
    	 * 示例：00 00 08 * 7 ? 2#1,2#3 触发时间在 7月的第一个周的周一 和 第三个周的周二 的 8:00:00
    	 ***************************/
		String dateTime = flgId.equals(JobModel.ID_START) ? taskTrigger
				.getStartTime() : taskTrigger.getStopTime();
		
		String[] hms = dateTime.split(":");
		String cron = hms[2] + " " + hms[1] + " " + hms[0]+ " "; //  + " * " + taskTrigger.getTrigger_month(); // 时 分 秒 日 表达式
		// 如果选择的是1-31日则为表达式添加日和月的规则
		if(taskTrigger.getTriggerDay() != null && !taskTrigger.getTriggerDay().equals("")){
			cron += taskTrigger.getTriggerDay() + " " + taskTrigger.getTriggerMonth() + " ?";
		}else{
			// 每月的第几周的周一到周日的表达式
			cron += "? " + taskTrigger.getTriggerMonth() + " "; // 把日 月 年 添加到表达式中
			String[] fews = taskTrigger.getTriggerFew().split(",");
			String[] weeks = taskTrigger.getTriggerWeek().split(",");
			for (int i = 0; i < fews.length; i++) {
				if(fews[i] != null){
					for (int j = 0; j < weeks.length; j++) {
						if(weeks[i] != null){
							if(weeks[i].equals("5") || weeks[i].equals("L")){
								cron += weeks[i] + "L" + ",";
							}else{
								cron += weeks[i] + "#" + fews[i] + ","; // 30 14 17 ? 7 2#4
							}
						}
					}
				}
			}
			cron = cron.substring(0, cron.length() - 1); // 去掉最后一个多余的逗号 ”,“
		}
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(job.getId() + flgId, job.getGroupId())
				.withSchedule(
						CronScheduleBuilder                                 
						.cronSchedule(cron)).forJob(jobDetail).build();
		return trigger;
	}
	
	
	
	/**
	 * 时分秒格式的字符串格式为 时分秒的数组
	 * @param time 当前时间
	 * @return {hh,mm,ss} 时间格式的数组
	 */
	private static int[] formatHms(String time){
		try {
			if(null == time)
				return null;
			String times[] = time.split(":");
			int hms[] = new int[times.length];
			for (int i = 0; i < times.length; i++) {
				hms[i] = Integer.parseInt(times[i]);
			}
			return hms;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
