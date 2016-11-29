package com.peraglobal.km.crawler.web.biz;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.MetaFormater;
import com.peraglobal.km.crawler.util.XMLFormater;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.processor.PageProcessor;
import com.peraglobal.km.crawler.web.webmagic.selector.Html;
import com.peraglobal.km.crawler.web.webmagic.selector.Selectable;
import com.peraglobal.km.mongo.model.KM_Datum;


public class GeneralProcessor implements PageProcessor {

	Map<String, Map<String, List<Map<String, Object>>>> ruleMap;

	MetaFormater formater = new XMLFormater();
	
	AttachProperty attachProperty;

	String taskID;

	public GeneralProcessor(
			Map<String, Map<String, List<Map<String, Object>>>> ruleMap) {

		this.ruleMap = ruleMap;

	}

	public GeneralProcessor(JobExecutionContext context) {
		TaskJob taskJob = (TaskJob) context.getMergedJobDataMap().get(
				JobModel.JOB_KEY);
		taskID = taskJob.getId();

		this.ruleMap = (Map<String, Map<String, List<Map<String, Object>>>>) context
				.getMergedJobDataMap().get(JobModel.RULE_KEY);
		// 采集附加属性
		this.attachProperty = context.getMergedJobDataMap().get(
				JobModel.ATTACH_PROPERTY_KEY) != null ? (AttachProperty) context
				.getMergedJobDataMap().get(JobModel.ATTACH_PROPERTY_KEY)
				: null;
	}

	public GeneralProcessor() {
	}

	@Override
	public void process(Page page) {
		try {

			if (page.getDatumId() == null) {

				AttachmentCrawler a = new AttachmentCrawler();
				a.setDatumID(page.getResultItems().get("datumId").toString());
				a.setPath(page.getResultItems().get("filePath").toString());
				a.setName(page.getResultItems().get("fileName").toString());
				a.setType(page.getResultItems().get("fileType").toString());
				a.setTaskId(page.getTaskId());

				page.putField("$_meta", a);

				return;

			}
			Map<String, List<Map<String, Object>>> listurls = ruleMap
					.get(Rule.LIST_URL_OPT);
			String l = listurls !=null ? 
					listurls.get(Rule.RULE_TYPE_REGEX).get(0).get("rule")
					.toString() : "";
			if(l.indexOf("期")!=-1){
            	try {
					String issnum = URLEncoder.encode("期", "GB2312"); //%C6%DA
					String year = URLEncoder.encode("年", "GB2312"); //%C4%EA
					l = l.replace("期", issnum).replace("年", year);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
			String seedUrl = ruleMap.get(Rule.SEED_URL).get(Rule.RULE_TYPE_URL)
					.get(0).get("rule")
					.toString().trim();
			Map<String, List<Map<String, Object>>> listcontent = ruleMap
					.get(Rule.LIST_CONTENT_OPT);
			Map<String, List<Map<String, Object>>> detailurls = ruleMap
					.get(Rule.DETAIL_URL_OPT);
			Map<String, List<Map<String, Object>>> detailmoreurls = ruleMap
					.get(Rule.DETAIL_MORE_URL_OPT);
			String detailUrl = detailurls!=null ? detailurls.get(Rule.RULE_TYPE_REGEX).get(0).get("rule").toString() : "";
			String detailMoreUrl = detailmoreurls!=null ? detailmoreurls.get(Rule.RULE_TYPE_REGEX).get(0).get("rule").toString() : "";
			
			// 目标页处理
			Map<String, List<Map<String, Object>>> detailcontents = ruleMap
					.get(Rule.DETAIL_CONTENT_OPT);
			Html html = page.getHtml();
			Object t = null;
			if ((!l.equals("") && page.getUrl().regex(l).match())
					|| seedUrl.indexOf(page.getUrl().toString().trim())!=-1) {
				Selectable listblock = null;
				// 百度文库抓取
				/*String pifix = page.getUrl().toString().substring(0,page.getUrl().toString().length()-1);
				int pageNum = Integer.parseInt(page.getUrl().toString().substring(page.getUrl().toString().length()-1));
				if(pageNum == 1){
					for(int i=2;i<=10;i++){
						page.addTargetRequest(pifix + i);
					}
				}*/
				//列表页面查找详情页链接
				if (listurls != null && listurls.size() > 0) {
					for (List<Map<String, Object>> vl : listurls.values())
						for (Map<String, Object> map : vl)
							page.addTargetRequests((List) (t = (t = html
									.getClass().getMethod("links", null)
									.invoke(html, null))
									.getClass()
									.getMethod(
											map.get("type").toString(),
											map.get("type").toString()
													.getClass())
									.invoke(t, map.get("rule"))).getClass()
									.getMethod("all", null).invoke(t, null));
				}
				// 列表页区域
				if (listcontent != null && listcontent.size() > 0){
					for (List<Map<String, Object>> lc : listcontent.values()){
						for (Map<String, Object> map : lc){
							if(map.get("rule")!=null && !map.get("rule").toString().equals("")){
							for (List<Map<String, Object>> vl : detailurls
									.values()){
								for (Map<String, Object> dmap : vl){
									page.addTargetRequests((List) (t = (t = (t = (listblock = (Selectable) html
											.getClass()
											.getMethod(
													map.get("type").toString(),
													map.get("type").toString()
															.getClass())
											.invoke(html,
													map.get("rule").toString()))
											.getClass()
											.getMethod("links", null)
											.invoke(listblock, null))
											.getClass()
											.getMethod(
													dmap.get("type").toString(),
													dmap.get("type").toString()
															.getClass())
											.invoke(t, dmap.get("rule")))
											.getClass().getMethod("all", null)
											.invoke(t, null)));
								}
							}
							}
						}
					}
				}		
				if (detailurls != null && detailurls.size() > 0) {
					for (List<Map<String, Object>> vl : detailurls.values())
						for (Map<String, Object> map : vl)
							page.addTargetRequests((List) (t = (t = html
									.getClass().getMethod("links", null)
									.invoke(html, null))
									.getClass()
									.getMethod(
											map.get("type").toString(),
											map.get("type").toString()
													.getClass())
									.invoke(t, map.get("rule"))).getClass()
									.getMethod("all", null).invoke(t, null));

				} else
					page.addTargetRequests(html.links().all());
			} else if((!detailUrl.equals("") && page.getUrl().regex(detailUrl).match()) 
					|| !detailMoreUrl.equals("") && page.getUrl().regex(detailMoreUrl).match()) {
				Map<String, String> fields = new HashMap<String, String>();
				DBObject kvs = new BasicDBObject();
				if(detailcontents != null && detailcontents.size() > 0){
					for (List<Map<String, Object>> lc : detailcontents.values()){
						for (Map<String, Object> map : lc){
							if(map.get("rule")==null || map.get("rule").toString().equals("")){
								fields.put(map.get("name").toString(),"");
								kvs.put(map.get("name").toString(),"");
							}else{
								fields.put(
										map.get("name").toString(),
										html.getClass()
												.getMethod(
														map.get("type").toString(),
														map.get("type").toString()
																.getClass())
												.invoke(html,
														map.get("rule").toString())
												.toString());
								kvs.put(map.get("name").toString(), html.getClass()
										.getMethod(
												map.get("type").toString(),
												map.get("type").toString()
														.getClass())
										.invoke(html,
												map.get("rule").toString())
										.toString());
							}
						}
					}
				}
				/*Datum d = new Datum();
				d.setTaskId(taskID);
				d.setUrl(page.getRequest().getUrl());
				d.setHtmlMeta(page.getRawText().getBytes("GBK"));
				d.setMd5(ConverterUtil.EncoderByMd5(page.getRawText()));
				d.setKvs(formater.format(fields).getBytes("GBK"));
				d.setId(page.getDatumId());
				if(page.getMoreDatumId()!=null){
					d.setDatumId(page.getMoreDatumId());
				}
				page.putField("$_meta", d);*/
				KM_Datum km_datum = new KM_Datum();
				km_datum.setTaskId(taskID);
				km_datum.setUrl(page.getRequest().getUrl());
				km_datum.setHtmlMeta(page.getRawText().getBytes("GBK"));
				km_datum.setMd5(ConverterUtil.EncoderByMd5(page.getRawText()));
//				km_datum.setKvs(formater.format(fields).getBytes("GBK"));
				km_datum.setKvs(kvs);
				km_datum.setIsFull(Dom4jXmlUtil.checkIsFull(kvs));
				km_datum.setCreateDate(new Date());
				km_datum.setId(page.getDatumId());
				if(page.getMoreDatumId()!=null){
					km_datum.setDatumId(page.getMoreDatumId());
				}
				page.putField("$_meta", km_datum);
				
				/*File file = new File("D:/spider/"+taskID+"/"+page.getDatumId()+".txt");
				if(!file.exists()){file.createNewFile();}
				FileWriter fw = new FileWriter(file,true);
				BufferedWriter bufferWritter = new BufferedWriter(fw);
				for(Map.Entry<String, String> entry : fields.entrySet()){
					//if(entry.getKey().equals("title")){
					if(entry.getValue().equals("") || entry.getValue() == null || entry.getValue().equals("null")) continue;
					String value = entry.getValue();
					if(value.indexOf("_百度百科") != -1){
						value = value.substring(0, value.indexOf("_百度百科"));
					}
					bufferWritter.write(value);
					bufferWritter.newLine();
					//}
				}
				bufferWritter.close();
				*/
				if(page.getUrl().regex(detailUrl).match()){
					// 在目标页发现文章更多分页
					if (detailmoreurls != null && detailmoreurls.size() > 0) {
						for (List<Map<String, Object>> vl : detailmoreurls.values())
							for (Map<String, Object> map : vl)
								page.addTargetRequests((List) (t = (t = html
										.getClass().getMethod("links", null)
										.invoke(html, null))
										.getClass()
										.getMethod(
												map.get("type").toString(),
												map.get("type").toString()
														.getClass())
										.invoke(t, map.get("rule"))).getClass()
										.getMethod("all", null).invoke(t, null),km_datum.getId()+"_more");
					}
				}
				Map<String, List<Map<String, Object>>> attclurls = ruleMap
						.get(Rule.ATTACHMENT_URL_OPT);
				// 详情页发现附件
				if (attclurls != null)
					for (List<Map<String, Object>> au : attclurls.values())
						for (Map<String, Object> map : au)
							try {
								page.addTargetRequests(
										(List) (t = (t = html.getClass()
												.getMethod("links", null)
												.invoke(html, null))
												.getClass()
												.getMethod(
														map.get("type")
																.toString(),
														map.get("type")
																.toString()
																.getClass())
												.invoke(t,
														map.get("rule")
																.toString()))
												.getClass()
												.getMethod("all", null)
												.invoke(t, null), km_datum.getId());
							} catch (Exception e) {
								page.addTargetRequests(
										(List) (t = (t = (t = html
												.getClass()
												.getMethod(
														map.get("type")
																.toString(),
														map.get("type")
																.toString()
																.getClass())
												.invoke(html,
														map.get("rule")
																.toString()))
												.getClass()
												.getMethod("links", null)
												.invoke(t, null)).getClass()
												.getMethod("all", null)
												.invoke(t, null)), km_datum.getId());
							}

			}else{
				//无列表页和目标页
				page.addTargetRequests(page.getHtml().links().all());
				Map<String, String> fields = new HashMap<String, String>();
				DBObject kvs = new BasicDBObject();
				for (List<Map<String, Object>> lc : detailcontents.values()){
					for (Map<String, Object> map : lc){
						if(map.get("rule")==null || map.get("rule").toString().endsWith("")){
							continue;
						}
						fields.put(
								map.get("name").toString(),
								html.getClass()
										.getMethod(
												map.get("type").toString(),
												map.get("type").toString()
														.getClass())
										.invoke(html,
												map.get("rule").toString())
										.toString());
						kvs.put(map.get("name").toString(),
								html.getClass()
								.getMethod(
										map.get("type").toString(),
										map.get("type").toString()
												.getClass())
								.invoke(html,
										map.get("rule").toString())
								.toString());
					}
				}
				/*Datum d = new Datum();
				d.setTaskId(taskID);
				d.setUrl(page.getRequest().getUrl());
				d.setHtmlMeta(page.getRawText().getBytes("GBK"));
				d.setMd5(ConverterUtil.EncoderByMd5(page.getRawText()));
				d.setKvs(formater.format(fields).getBytes("GBK"));
				d.setId(page.getDatumId());
				page.putField("$_meta", d);*/
				
				KM_Datum km_datum = new KM_Datum();
				km_datum.setTaskId(taskID);
				km_datum.setUrl(page.getRequest().getUrl());
				km_datum.setHtmlMeta(page.getRawText().getBytes("GBK"));
				km_datum.setMd5(ConverterUtil.EncoderByMd5(fields.toString()));
//				km_datum.setKvs(formater.format(fields).getBytes("GBK"));
				km_datum.setKvs((DBObject)fields);
				km_datum.setIsFull(Dom4jXmlUtil.checkIsFull((DBObject)fields));
				km_datum.setId(page.getDatumId());
				page.putField("$_meta", km_datum);
				
				/*File file = new File("D:/spider/"+taskID+"/"+page.getDatumId()+".txt");
				if(!file.exists()){file.createNewFile();}
				FileWriter fw = new FileWriter(file,true);
				BufferedWriter bufferWritter = new BufferedWriter(fw);
				for(Map.Entry<String, String> entry : fields.entrySet()){
					//if(entry.getKey().equals("title")){
						if(entry.getValue().equals("") || entry.getValue() == null || entry.getValue().equals("null")) continue;
						String value = entry.getValue();
						if(value.indexOf("_百度百科") != -1){
							value = value.substring(0, value.indexOf("_百度百科"));
						}
						bufferWritter.write(value);
						bufferWritter.newLine();
					//}
				}
				bufferWritter.close();*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Site site = Site
			.me()
//			.setDomain("blog.sina.com.cn")
			.setCharset(this.attachProperty != null ? this.attachProperty.getCharSet() : null)
			.setSleepTime(500)
			.setRetryTimes(3)
			.setCycleRetryTimes(3)
			.setUserAgent(
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
//					.setHttpProxyPool(Lists.newArrayList(new String[]{"192.168.134.1","808"}, new String[]{"192.168.134.1","808"}));

	@Override
	public Site getSite() {
		return site;
	}

	

}
