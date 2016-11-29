package com.peraglobal.km.crawler.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static String timePattern = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 获取当前时间
	 * @return
	 */
	public static Timestamp getTimeNow() {
		return new Timestamp(System.currentTimeMillis());
	}
	/**
	 * 将数据库时间字段转为字符串
	 * @param time
	 * @return
	 */
	public static final String getTimeStampStr(Timestamp time) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (time != null) {
			df = new SimpleDateFormat(timePattern);
			returnValue = df.format(time);
		}

		return (returnValue);
	}
	/**
	 * 将日期对象根据特定格式格式化成字符串
	 * 
	 * @param source
	 *            要格式化的日期对象
	 * @param pattern
	 *            格式化模式，默认为{@value #PATTERN_YYYY_MM_DD_HH_MM_SS}
	 * @return 格式化后的字符串
	 */
	public static String format(final Date source, String pattern) {
		// 检查value是否为空
		if (source == null) {
			return null;
		}
		// 如果pattern为空
		if (pattern == null) {
			// 设置pattern为yyyy-MM-dd HH:mm:ss
			pattern = timePattern;
		}
		// 初始化一个format类
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(source);
	}
	
	
}
