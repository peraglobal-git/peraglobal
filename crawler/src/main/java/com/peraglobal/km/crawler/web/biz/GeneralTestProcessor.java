package com.peraglobal.km.crawler.web.biz;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.quartz.JobExecutionContext;

import com.google.common.collect.Lists;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.km.crawler.util.ConverterUtil;
import com.peraglobal.km.crawler.util.MetaFormater;
import com.peraglobal.km.crawler.util.XMLFormater;
import com.peraglobal.km.crawler.web.model.AttachProperty;
import com.peraglobal.km.crawler.web.model.AttachmentCrawler;
import com.peraglobal.km.crawler.web.model.Datum;
import com.peraglobal.km.crawler.web.model.Rule;
import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.processor.PageProcessor;
import com.peraglobal.km.crawler.web.webmagic.selector.Html;
import com.peraglobal.km.crawler.web.webmagic.selector.Selectable;


public class GeneralTestProcessor implements PageProcessor {
	
	private final AtomicLong pageCount = new AtomicLong(0);
	Map<String, Map<String, List<Map<String, Object>>>> ruleMap;

	MetaFormater formater = new XMLFormater();
	
	AttachProperty attachProperty;

	String taskID;

	public GeneralTestProcessor(Map<String, Map<String, List<Map<String, Object>>>> ruleMap) {
		
		this.ruleMap = ruleMap;
		// 采集附加属性
		this.attachProperty = null;
	}
	
	public GeneralTestProcessor() {
		
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
					.toString();

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
										.values())
									for (Map<String, Object> dmap : vl)
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
												.invoke(t, null)),"detailUrl");
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
									.getMethod("all", null).invoke(t, null),"detailUrl");

				} else{
					page.addTargetRequests(html.links().all());
				}
			} else if((!detailUrl.equals("") && page.getUrl().regex(detailUrl).match()) 
					|| !detailMoreUrl.equals("") && page.getUrl().regex(detailMoreUrl).match()) {
				Map<String, String> fields = new HashMap<String, String>();
				if(detailcontents != null && detailcontents.size() > 0){
					for (List<Map<String, Object>> lc : detailcontents.values()){
						for (Map<String, Object> map : lc){
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
						}
					}
				}
				Datum d = new Datum();
				d.setTaskId(taskID);
				d.setUrl(page.getRequest().getUrl());
				d.setId(page.getDatumId());
				if(page.getMoreDatumId()!=null){
					d.setDatumId(page.getMoreDatumId());
				}
				page.putField("", formater.format(fields));
				
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
										.getMethod("all", null).invoke(t, null),d.getId()+"_more");
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
												.invoke(t, null), d.getId());
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
												.invoke(t, null)), d.getId());
							}

			}else{
				//无列表页和目标页
				page.addTargetRequests(page.getHtml().links().all());
				Map<String, String> fields = new HashMap<String, String>();
				for (List<Map<String, Object>> lc : detailcontents.values()){
					for (Map<String, Object> map : lc){
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
					}
				}
				Datum d = new Datum();
				d.setTaskId(taskID);
				d.setId(page.getDatumId());
				page.putField("$_meta", d);
			}
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Site site = Site
			.me()
//			.setDomain("blog.sina.com.cn")
			.setCharset(this.attachProperty != null ? this.attachProperty.getCharSet() : null)
			.setSleepTime(500)
			.setUserAgent(
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
//					.setHttpProxyPool(Lists.newArrayList(new String[]{"192.168.134.1","808"}, new String[]{"192.168.134.1","808"}));

	@Override
	public Site getSite() {
		return site;
	}

	

}
