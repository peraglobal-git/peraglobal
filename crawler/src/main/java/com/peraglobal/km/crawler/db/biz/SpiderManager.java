package com.peraglobal.km.crawler.db.biz;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬虫线程管理
 * @author hadoop
 */
public class SpiderManager{
	
	private static final Logger log = LoggerFactory.getLogger(SpiderManager.class);
	private static ConcurrentMap<String,SdcSpider> spiderMap = new ConcurrentHashMap<String,SdcSpider>(); //多线程并发安全，写与写互斥
	private static ExecutorService execService = Executors.newCachedThreadPool();
	private static Lock lock = new ReentrantLock();
	private static ConcurrentMap<String,SdcSpider> destorySpiderMap = new ConcurrentHashMap<String,SdcSpider>(); //多线程并发安全，写与写互斥
	public static ExecutorService getExecService() {
		return execService;
	}
	public static void  register(String key,SdcSpider spider){
		spiderMap.put(key,spider);
	}
	public static SdcSpider get(String key){
		return spiderMap.get(key);
	}
	public  static void remove(String key){
		SdcSpider spider  =get(key);
		if(spider==null){
			log.info("not found  "+key+"  spider task!");
			return;				
		}
		stop(key);//停止爬虫线程
		spiderMap.remove(key);
	}
	/**
	 * 销毁所有爬虫
	 */
	public static void destoryAll(){
		Set<String> keys = spiderMap.keySet();
		for(String key:keys){
			stop(key);//停止所有爬虫线程
		}
		spiderMap.clear(); 
		getExecService().shutdown();
		
	}
	public static void shutdown(){
		Set<String > keys = destorySpiderMap.keySet();
		for(String key:keys)
		spiderMap.remove(key);
		getExecService().shutdown();
	}
	
	/**
	 * 启动爬虫
	 * @param key 唯一标识
	 * @param threadNum 执行线程数
	 */
	public static void start(String key){
		lock.lock();
		try{
			SdcSpider spider  = get(key);
			if(spider==null){
				log.error("not found  "+key+"  spider task!");
				return;				
			}
				destorySpiderMap.put(key, spider);//待销毁的爬虫
				spider.setSpiderState(START);
				execService.execute(spider);
				log.info(spider.getSpiderName()+" spider already run!");
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 重新开始
	 * @param key
	 * @param threadNum
	 *//*
	public static void restart(String key){
		lock.lock();
		try{
			Spider spider  = get(key);
			if(spider==null){
				log.error("not found  "+key+"  spider task!");
				return;				
			}
			stop(key);//停止爬虫线程
			//删除数据重新开始
			start(key);
		}finally{
			lock.unlock();
		}
	}*/
	/**
	 * 恢复爬虫
	 * @param key
	 */
	public static void recover(String key){
		lock.lock();
		try{
			SdcSpider spider  = get(key);
			if(spider==null){
				log.error("not found  "+key+"  spider task!");
				return;
			}
				destorySpiderMap.put(key, spider);//待销毁的爬虫
				spider.setSpiderState(START);
				execService.execute(spider);
				log.error(spider.getSpiderName()+"  spider already  recover!");
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 停止爬虫
	 * @param key
	 */
	public static void stop(String key){
		lock.lock();
		try{
			SdcSpider spider   = get(key);
			if(spider==null){
				log.info("not found  "+key+"  spider task!");
				return;
			}
				spider.setSpiderState(STOP);			
				log.info(spider.getSpiderName()+"  spider already  stop!");
		}finally{
			lock.unlock();
		}
	}
	/**
	 *  暂停爬虫
	 * @param key
	 */
	public static void puase(String key){
		lock.lock();
		try{
			SdcSpider spider =get(key);
			if(spider==null){
				log.info("not found  "+key+"  spider task!");
				return;
			}
			spider.setSpiderState(PUASE);
			log.info(spider.getSpiderName()+"  spider already pause!");
		}finally{
			lock.unlock();
		}
	}
	
	public final static String START 	= "start";
	public final static String STOP 	= "stop";
	public final static String PUASE 	= "puase";
	public final static String RECOVER ="recover";
}