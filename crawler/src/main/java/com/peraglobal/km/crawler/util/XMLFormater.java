package com.peraglobal.km.crawler.util;

import java.util.Map;


public class XMLFormater implements MetaFormater {

	public String format(Object o) {
		Map<String, String> fields = (Map<String, String>) o;
		if(fields == null || fields.size() == 0) return "";
		StringBuffer metadata = new StringBuffer();
		metadata.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\r");
		metadata.append("<metadata><fields>");
		for(Map.Entry<String, String> entry : fields.entrySet()){
			//<field key="code" value="1668046" />
			metadata.append("<field>");
			if(entry.getKey().indexOf(":")!=-1){
				metadata.append("<key>" + entry.getKey().split(":")[0] + "</key>");
				metadata.append("<teptId>" + entry.getKey().split(":")[1] + "</teptId>");
			}else{
				metadata.append("<key>" + entry.getKey() + "</key>");
			}
			String value = entry.getValue() !=null ? entry.getValue() : "";
			if(value != null && !value.equals("") && value.indexOf("<") != -1){
				value = "<![CDATA[" + value + "]]>";
			}
			metadata.append("<value>" + value + "</value>");
//			metadata.append("<field key=\"" + entry.getKey() +"\" value=\"" + value +"\" />");
			metadata.append("</field>");
		}
		metadata.append("</fields></metadata>");
		System.out.println(metadata.toString());
		/*ByteArrayInputStream input = new ByteArrayInputStream(metadata.toString().getBytes());
		File file = new File("D://sdc/11xxx.xml");
        FileOutputStream output = null;
        try {
        	output = new FileOutputStream(file);
        	IOUtils.copy(input, output);  
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {  
            // 关闭低层流。
			IOUtils.closeQuietly(output); 
			IOUtils.closeQuietly(input);  
        }*/
		return metadata.toString();
	}
	public String formatKm(Object o) {
		Map<String, String> fields = (Map<String, String>) o;
		if(fields == null || fields.size() == 0) return "";
		StringBuffer metadata = new StringBuffer();
		metadata.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "\r");
		metadata.append("<metadata><fields>");
		for(Map.Entry<String, String> entry : fields.entrySet()){
			//<field key="code" value="1668046" />
			metadata.append("<field>");
			if(entry.getKey().indexOf(":")!=-1){
				metadata.append("<key>" + entry.getKey().split(":")[0] + "</key>");
			}else{
				metadata.append("<key>" + entry.getKey() + "</key>");
			}
			String value = entry.getValue() !=null ? entry.getValue() : "";
			if(value != null && !value.equals("") && value.indexOf("<") != -1){
				value = "<![CDATA[" + value + "]]>";
			}
			metadata.append("<value>" + value + "</value>");
//			metadata.append("<field key=\"" + entry.getKey() +"\" value=\"" + value +"\" />");
			metadata.append("</field>");
		}
		metadata.append("</fields></metadata>");
		System.out.println(metadata.toString());
		/*ByteArrayInputStream input = new ByteArrayInputStream(metadata.toString().getBytes());
		File file = new File("D://sdc/11xxx.xml");
        FileOutputStream output = null;
        try {
        	output = new FileOutputStream(file);
        	IOUtils.copy(input, output);  
        } catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {  
            // 关闭低层流。
			IOUtils.closeQuietly(output); 
			IOUtils.closeQuietly(input);  
        }*/
		return metadata.toString();
	}


}
