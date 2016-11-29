package com.peraglobal.km.crawler.db.biz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
/**
 * 元数据xml写操作
 * @author hadoop
 *
 */
public class MetaDataWriter {
	 private static final Logger log = LoggerFactory.getLogger(MetaDataWriter.class);

	
	private TransformerHandler handler = null;
	private OutputStream outStream = null;
	private static StringWriter writerStr = null;
	private String filePath;
	private AttributesImpl atts;
	private String rootElement;
	private String fieldsElement;
	private String writeType;
	private String xmlStr;
	public static String FILETYPE="file";
	public static String STRTYPE="str";
	
	
	
	//系统换行符，Windows为："\n"，Linux/Unix为："/n"  
    private static final String separator = System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1 ? "\n" : "/n";  
      
	public MetaDataWriter(String filePath, String rootElement,String fieldsElement,String writeType) {
		this.filePath = filePath;
		this.rootElement = rootElement;
		this.fieldsElement = fieldsElement;
		this.writeType = writeType;
		init();
	}
	public MetaDataWriter(String rootElement,String writeType) {
		this.rootElement = rootElement;
		this.writeType = writeType;
		init();
	}
	public MetaDataWriter(String rootElement,String fieldsElement,String writeType) {
		this.rootElement = rootElement;
		this.fieldsElement = fieldsElement;
		this.writeType = writeType;
		init();
	}

	public void init() {
		try {
			SAXTransformerFactory fac = (SAXTransformerFactory) SAXTransformerFactory.newInstance();  	//创建工厂
			
	        
			handler = fac.newTransformerHandler();
			Transformer transformer = handler.getTransformer();
			//transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			// 设置输出采用的编码方式
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");// 是否自动添加额外的空白
			transformer
					.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");// 是否忽略xml声明
			Result resultxml =null;
			if(STRTYPE.equals(writeType)){
				writerStr = new StringWriter();  
				resultxml = new StreamResult(writerStr);
				
			}
			if(FILETYPE.equals(writeType)){
				String dirs = filePath.substring(0,filePath.lastIndexOf(File.separator));
				File file = new File(dirs);
				if(file.mkdirs()){}
				outStream = new FileOutputStream(filePath);
				resultxml = new StreamResult(outStream);
			}
			
			handler.setResult(resultxml);
			atts = new AttributesImpl();
			start();
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void start() {
		try {
			handler.startDocument();
			handler.startElement("", "", rootElement, atts);
			handler.startElement("", "", fieldsElement, atts);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public void write(HashMap<String, String> map, String objectElement)
			throws SAXException {
		
		Set<String> keys = map.keySet();
		for( Iterator<String> it = keys.iterator();it.hasNext();) {
			String key = (String) it.next();
			String value = map.get(key);
			
			if(null!=key&&!"".equals(key)){
				key = key.trim();
			}
			if(null!=value&&!"".equals(value)){
				value = value.trim();
			}
			
			/*<field>
			 * 	<key>name</key>
			 * 	<value>孙尚如</value>
			 * </field>
			 * handler.startElement("", "", key, atts);
				handler.characters(value.toCharArray(), 0, value.length());
				handler.endElement("", "", key);
			*/
			
			//<field key="name" value="孙尚如"/>
			atts.addAttribute("", "", "key", "",key);
			atts.addAttribute("", "", "value", "",value);
		}
		if (objectElement != null) {
			handler.startElement("", "", objectElement, atts);
			handler.endElement("", "", objectElement);
		}
	}
	
	public void writeField(HashMap<String, String> map, String objectElement)
			throws SAXException {
		
		Set<String> keys = map.keySet();
		for( Iterator<String> it = keys.iterator();it.hasNext();) {
			String key = (String) it.next();
			String value = map.get(key);
			
		/*	if(null!=key&&!"".equals(key)){
				key = key.trim();
			}
			if(null!=value&&!"".equals(value)){
				value = value.trim();
			}*/
			if(null!=key&&!"".equals(key)&&null!=value&&!"".equals(value)){
				key = key.trim();
				value = value.trim();
				String keyV = key;
				String teptId = "";
				if(key.indexOf(":")!=-1){
					key = keyV.split(":")[0];
					teptId = keyV.split(":")[1];
				}
			
			handler.startElement("", "", objectElement, atts);  
            
			handler.startElement("", "", "key", atts);
            handler.characters(key.toCharArray(), 0, key.length());  
            handler.endElement("", "", "key");
			
            if(!teptId.equals("")){
            	handler.startElement("", "", "teptId", atts);
                handler.characters(teptId.toCharArray(), 0, teptId.length());  
                handler.endElement("", "", "teptId");
            }
            
            handler.startElement("", "", "value", atts);
            handler.characters(value.toCharArray(), 0, value.length());  
            handler.endElement("", "", "value");
            
            handler.endElement("", "", objectElement);
			}
		}
	}
	public String getXmlStr(){
		//return  ConverterUtil.encodingToUTF8(this.xmlStr);
		return  this.xmlStr;
		
	}

	public void end() {
		try {
			handler.endElement("", "", fieldsElement);
			handler.endElement("", "", rootElement);
			handler.endDocument();// 文档结束,同步到磁盘
			if(FILETYPE.equals(writeType)){
				outStream.close();
			}else if(STRTYPE.equals(writeType)){
				this.xmlStr = writerStr.getBuffer().toString();  
				writerStr.close();
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
