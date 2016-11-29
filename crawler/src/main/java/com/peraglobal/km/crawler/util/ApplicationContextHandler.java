package com.peraglobal.km.crawler.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 *
 * 1.ApplicationContext ctx = new ClassPathXmlApplicationContext("../../WEB-INF/classes/mvc-context.xml");
 * 2.ApplicationContext ctx = ApplicationContextHandler.getApplicationContext();
 *  －建议把使用第２种方式获取spring 容器修改为第１种方式。
 *  －第１种方式会重新创建spring容器，实例化容器中的所有bean,多次调用时会挤爆内存。
 * @author hadoop
 *
 */
@Component
public class ApplicationContextHandler implements  ApplicationContextAware {
			private static  ApplicationContext applicationContext;
			private ApplicationContextHandler(){}
    	  @Override
    		public void setApplicationContext(ApplicationContext applicationContext)
    				throws BeansException {
    	    	ApplicationContextHandler.applicationContext = applicationContext;		
    			
    		}
    	  public static ApplicationContext getApplicationContext() {
    		  return applicationContext;
    	  }
    	  
}
