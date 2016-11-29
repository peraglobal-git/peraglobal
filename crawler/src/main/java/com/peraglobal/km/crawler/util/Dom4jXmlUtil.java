package com.peraglobal.km.crawler.util;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mongodb.DBObject;
import com.peraglobal.km.crawler.task.model.JdbcEnity;
import com.peraglobal.km.crawler.task.model.JobModel;
import com.peraglobal.km.crawler.task.model.TaskConfig;
import com.peraglobal.km.crawler.task.model.TaskOption;

/**
 * 2015-7-2
 * @author yongqian.liu
 * 使用 Dom4j 解析 xml 文件
 */
public class Dom4jXmlUtil {
	private static final String KNOWLEDGE_AUTHOR = "KNOWLEDGE_AUTHOR:INHERENT";
	private static final String KNOWLEDGE_KEYWORD = "KEYWORD:INHERENT";
	private static final String KNOWLEDGE_SUMMARY = "SUMMERY:INHERENT";
	/**
	 * 通过一个路径获得一个 Element 对象
	 * @param xmlPath
	 * @return
	 */
	public static Element readElements(String register_type) {
		try {
			String u = Dom4jXmlUtil.class.getClassLoader().getResource("rule").getPath();
			String rule = "";
			if(register_type.equals(JobModel.MODEL_CRAWLER)){
				rule = "web";
			}else{
				rule = "db";
			}
			String xmlPath = u + rule + ".xml";
			SAXReader reader = new SAXReader();
            Document document = reader.read(new File(xmlPath));
			Element root = document.getRootElement();
			return root;
		} catch (DocumentException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解析 xml 对象
	 * @param root
	 * @return
	 */
	public static List<List<TaskConfig>> readXML(Element root) {
		List<List<TaskConfig>> list = new ArrayList<List<TaskConfig>>();
		List<TaskConfig> row = null;
		TaskConfig field = null;
		for (Iterator<?> iter = root.elementIterator(); iter.hasNext();) {
			row = new ArrayList<TaskConfig>();
			Element vals = (Element) iter.next();
			for (Iterator<?> fiald = vals.elementIterator(); fiald.hasNext();) {
				field = new TaskConfig();
				
				// 获得列节点
				Element node = (Element) fiald.next();
				String flag = node.getName();
				
				// 设置当前列的类型标记
				field.setFlag(flag);
				if(flag.equals("label")){
					
					// 普通 label
					field.setValue(node.getText());
				}else if(flag.equals("text")){
					
					// 文本框
					field.setName(node.attributeValue("name"));
					field.setSize(node.attributeValue("size"));
					field.setValue(node.attributeValue("value") == null ? "" : node.attributeValue("value"));
				}else if(flag.equals("select")){
					
					// 下拉框
					field.setName(node.attributeValue("name"));
					
					// option 遍历
					@SuppressWarnings("unchecked")
					List<Element> elements = node.elements();
					
					List<TaskOption> tos = new ArrayList<TaskOption>();
					TaskOption option = null;
					for (int i = 0; i < elements.size(); i++) {
						Element opt = elements.get(i);
						option = new TaskOption();
						option.setValue(opt.attributeValue("value"));
						option.setText(opt.getText());
						tos.add(option);
					}
					field.setTaskOption(tos);
				}else{
					
					field.setName(node.attributeValue("name"));
					// 是否自增长,是否自删除
					field.setValue(node.getText());
				}
				row.add(field);
			}
			list.add(row);
		}
		return list;
	}
	
	/**
	 * 解析 xml 对象
	 * @param root
	 * @return
	 */
	public static String putXML(JdbcEnity jdbc) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\r");
		sb.append("<dataConfig>" + "\r");
		sb.append("<dataSource name=\"").append(jdbc.getName()).append("\" ");
		sb.append("type=\"").append(jdbc.getType()).append("\" ");
		sb.append("driver=\"").append(jdbc.getDriver()).append("\" ");
		sb.append("url=\"").append(jdbc.getUrl()).append("\" ");
		sb.append("user=\"").append(jdbc.getUser()).append("\" ");
		sb.append("password=\"").append(jdbc.getPassword()).append("\" />" + "\r");
		
		// 属性字段
		sb.append("<entity name=\"").append(jdbc.getEntityName()).append("\" ");
		sb.append("query=\"select * from ").append(jdbc.getEntityName()).append(" t\" ");
		sb.append("pk=\"").append(jdbc.getPkid()).append(" \" >"  + "\r");
		for (int i = 0; i < jdbc.getFieldAs().length; i++) {
			sb.append("<field name=\"").append(jdbc.getFieldName()[i]).append("\" ")
				.append("as=\"").append(jdbc.getFieldAs()[i]).append("\" ")
				.append("type=\"").append(JdbcConnectionUtil.getType(jdbc.getFieldType()[i])).append("\" />"  + "\r");
		}
		
		// 附件部分
		if(jdbc.getAttachmentName() != null){
			// URL地址的附件类型
			sb.append("<field name=\"").append(jdbc.getAttachmentName()).append("\" ")
			.append("as=\"").append(jdbc.getAttachmentAs()).append("\" ")
			.append("type=\"");
			if(jdbc.getAttachmentFileName() != null){
				// blob 类型的附件
				sb.append(JdbcConnectionUtil.getType("BLOB")).append("\" ").append("filetype=\"").append(jdbc.getAttachmentFileType())
				.append("\" ").append("filename=\"").append(jdbc.getAttachmentFileName());
			}else{
				// varchar URL 地址类型附件
				sb.append(JdbcConnectionUtil.getType("PATH")).append("\" ").append("url=\"yes");
			}
			sb.append("\" />"  + "\r");
		}
		sb.append("</entity>" + "\r");
		sb.append("</dataConfig>");
		return sb.toString();
	}
	/**
	 * 转换规则生成XML
	 * @param filedNames
	 * @param knowledgeKeys
	 * @return
	 */
	public static String extractMapping(String[] filedNames,String[] knowledgeKeys){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\r");
		sb.append("<mappingConfig>" + "\r");
		for (int i = 0; i < filedNames.length; i++) {
			sb.append("<field name=\""+ filedNames[i] +"\" as=\""+ knowledgeKeys[i] +"\" />"+ "\r");
		}
		sb.append("</mappingConfig>");
		return sb.toString();
	}
	
	/**
	 * 转换规则生成XML
	 * @param filedNames
	 * @param knowledgeKeys
	 * @return
	 */
	public static String extractXMLMapping(List knowledgeKeys){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\r");
		sb.append("<mappingConfig>" + "\r");
		String str = "";
		for (int i = 0; i < knowledgeKeys.size(); i++) {
			str = knowledgeKeys.get(i).toString();
			if(str.indexOf(":")!=-1){
				sb.append("<field as=\""+ str.split(":")[0] +"\" teptId=\""+ str.split(":")[1]+"\" />"+ "\r");
			}else{
				sb.append("<field as=\""+ str +"\" />"+ "\r");
			}
			
		}
		sb.append("</mappingConfig>");
		return sb.toString();
	}
	/**
	 * 解析DB规则，获取采集元数据字段
	 * @param dbConfig
	 * @return
	 */
	public static String resolveDbConfig(String dbConfig){
		JSONArray jsonArray = new JSONArray();
		
		try {
			Document document = DocumentHelper.parseText(dbConfig);
			Element dataConfig = document.getRootElement();
			Element entity = dataConfig.element("entity");
			List nodes = entity.elements("field");
			JSONObject jsonParam = null;
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element field = (Element) it.next();
				Attribute metaKey = field.attribute("as");
				jsonParam = new JSONObject();
				jsonParam.put("name", metaKey.getText());
				jsonArray.add(jsonParam);
			}  
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray.toString();
	}
	/**
	 * 将转换规则XML转换为map
	 * @param mappingRule
	 * @return
	 */
	public static Map<String,String> generateMetaMap(String mappingRule){
		Map<String,String> fieldMap = new HashMap<String,String>();
		try {
			if(mappingRule!=null && mappingRule!=""){
				Document document = DocumentHelper.parseText(mappingRule);
				Element mappingConfig = document.getRootElement();
				List nodes = mappingConfig.elements("field");
				for (Iterator it = nodes.iterator(); it.hasNext();) {
					Element field = (Element) it.next();
					Attribute as = field.attribute("as");
//					Attribute teptId = field.attribute("teptId");
					//fieldMap.put("teptId", teptId.getText());
					fieldMap.put(as.getText(), as.getText());
				} 
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldMap;
	}
	/**
	 * 将转换规则XML转换为map
	 * @param mappingRule
	 * @return
	 */
	public static Map<String,String> generateMap(String mappingRule){
		Map<String,String> fieldMap = new HashMap<String,String>();
		try {
			Document document = DocumentHelper.parseText(mappingRule);
			Element mappingConfig = document.getRootElement();
			List nodes = mappingConfig.elements("field");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element field = (Element) it.next();
				Attribute name = field.attribute("name");
				Attribute as = field.attribute("as");
				fieldMap.put(name.getText(), as.getText());
			}  
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldMap;
	}
	/**
	 * 根据映射规则生成知识元数据
	 * @param crawlerMeta
	 * @param mappingRule
	 * @return
	 */
	public static Map<String,String> generateKnowledageMeta(String crawlerMeta,Map<String,String> fieldMap){
		Map<String,String> knowledgeFields = new HashMap<String,String>();
		try {
//			crawlerMeta = "<metadata><fields><field><key>title</key><value><![CDATA[<p class=\"titlenew\">国土资源部两家重点实验室在吉挂牌</p>]]></value></field><field><key>contentBody</key><value><![CDATA[<p class=\"content\">国土资源部两家重点实验室在吉挂牌国土资源部两家重点实验室在吉挂牌</p>]]></value></field></fields></metadata>";
			Document document = DocumentHelper.parseText(crawlerMeta);
			Element metadata = document.getRootElement();
			Element fields = metadata.element("fields");
			List nodes = fields.elements("field");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element field = (Element) it.next();
				Element key = field.element("key");
				Element teptId = field.element("teptId");
				Element value = field.element("value");
				if(fieldMap.get(key.getText())!=null){
//					knowledgeFields.put(fieldMap.get(key.getText()), value.getText());
					knowledgeFields.put(key.getText()+":"+teptId.getText(), value.getText());
				}
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return knowledgeFields;
	}
	
	/**
	 * 功能:将DB采集规则的xml文件转换成map
	 * <p>作者 王晓鸣 2016-1-28
	 * @param mappingRule DB规则的xml文件
	 */
	public static Map<String,String> generateMapForDB(String mappingRule){
		Map<String,String> fieldMap = new HashMap<String,String>();
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonParam1 = new JSONObject();
			
			Document document = DocumentHelper.parseText(mappingRule);
			Element mappingConfig = document.getRootElement();
			Element entity = mappingConfig.element("entity");
			String entityname=entity.attribute("name").getText();
			fieldMap.put("entityname", entityname);
			List nodes = entity.elements("field");
			//List nodes = mappingConfig.elements("field");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element field = (Element) it.next();
				Attribute name = field.attribute("name");
				Attribute as = field.attribute("as");
				Attribute filetype=field.attribute("filetype");
				Attribute filename=field.attribute("filename");
				Attribute url=field.attribute("url");
				if(filetype!=null&&filename!=null){
					fieldMap.put("filekey", name.getText());
					fieldMap.put("filevalue", as.getText());
					fieldMap.put("filetype", filetype.getText());
					fieldMap.put("filename", filename.getText());
				}else if(url!=null){
					fieldMap.put("filekey", name.getText());
					fieldMap.put("filevalue", as.getText());
				}
				else{
					jsonParam1.put("key", name.getText());
					jsonParam1.put("value", as.getText());
					jsonArray.add(jsonParam1);
					fieldMap.put("json", jsonArray.toString());
				}
			}  
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldMap;
	}
	public static void main(String[] args){
		/*Map<String,String> fieldMap = new HashMap<String,String>();
		fieldMap.put("title", "active");
		Dom4jXmlUtil.generateKnowledageMeta("ss", fieldMap);*/
		System.out.println(Dom4jXmlUtil.getTextFromTHML(null));
	}
	/**
	 * 功能：解析xml格式字符串
	 * <p> 作者：段政 2016-2-29
	 * @param protocolXML
	 */
	public static List parse(String protocolXML) {  
		 List list=null;
         try {  
              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
              DocumentBuilder builder = factory.newDocumentBuilder();  
              org.w3c.dom.Document doc=builder.parse(new InputSource(new StringReader(protocolXML)));  
            org.w3c.dom.Element root= doc.getDocumentElement();  
              NodeList books = root.getChildNodes();  
             if (books != null) {
            	 list=new ArrayList();
                 for (int i = 0; i < books.getLength(); i++) {
                	 NamedNodeMap booksMap=books.item(i).getAttributes();
                	 if(booksMap!=null){  
                		 Node node= booksMap.getNamedItem("as");
                		 if(node!=null){
                			 String nodeValue= node.getNodeValue();
                			 list.add(nodeValue); 
                		 }
                	 }
                      Node book =null;
                      NodeList no=  books.item(i).getChildNodes();
                      for (int j = 0; j < no.getLength(); j++) {
                    	  book= no.item(j);
                    	  NamedNodeMap map=  book.getAttributes();
                    	  if(map!=null){
                    		 Node node= map.getNamedItem("as");
                    		  if(node!=null){
                    			  String nodeValue= node.getNodeValue();
                     			 	list.add(nodeValue); 
                    		  }
                          }
                      }
                  }  
              }  
          } catch (Exception e) {  
              e.printStackTrace();  
          }  
         return list;
      }
	/**
	 * 功能：解析xml格式字符串
	 * <p> 作者：井晓丹 2016-3-30
	 * @param protocolXML
	 */
	public static List getKeyFromXMl(String protocolXML) {  
		 List list=null;
         try {  
        	 if(protocolXML!=null && protocolXML!=""){
        		list=new ArrayList();
        		Document document = DocumentHelper.parseText(protocolXML);
				Element dataConfig = document.getRootElement();
				Element entity = dataConfig.element("entity")==null ? dataConfig : dataConfig.element("entity");
				List nodes = entity.elements("field");
				for (Iterator it = nodes.iterator(); it.hasNext();) {
					Element field = (Element) it.next();
					Attribute as = field.attribute("as");
					list.add(as.getText());
				} 
 			}  
          } catch (Exception e) {  
              e.printStackTrace();  
          }  
         return list;
      }
	
	public static String getTextFromTHML(String htmlStr) {
		if(htmlStr == null || htmlStr.equals("")){
			return "";
		}
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlStr);
		String text = doc.text();
		// remove extra white space
		StringBuilder builder = new StringBuilder(text);
		int index = 0;
		while (builder.length() > index) {
			char tmp = builder.charAt(index);
			if (Character.isSpaceChar(tmp) || Character.isWhitespace(tmp)) {
				builder.setCharAt(index, ' ');
			}
			index++;
		}
		text = builder.toString().replaceAll(" +", " ").trim();
		return text;
	}
	
	/**
	 * 检查数据完整性
	 * @param kvs
	 * @return
	 */
	public static String checkIsFull(DBObject kvs){
		Map knowledgeKvsMap = kvs.toMap();
		for(Object key : knowledgeKvsMap.keySet()){
			if(knowledgeKvsMap.get(key)==null || knowledgeKvsMap.get(key).toString().equals("")){
				return "1";
			}
		}
		return "0";
	}
}
