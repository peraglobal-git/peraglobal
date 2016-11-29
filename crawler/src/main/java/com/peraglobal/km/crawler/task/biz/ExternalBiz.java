package com.peraglobal.km.crawler.task.biz;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peraglobal.km.webservice.KnowledgeWS;
import com.peraglobal.km.webservice.model.KnowledgeMeta;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

@Service("externalRestBiz")
public class ExternalBiz{
	
	/**
	 * 打开webService远程服务
	 * 作者：井晓丹 2016.3.18
	 * @return 接口类
	 */
	private KnowledgeWS getKnowledgeWS() {
		JaxWsProxyFactoryBean svr = new JaxWsProxyFactoryBean();  
        svr.setServiceClass(KnowledgeWS.class);  
        svr.setAddress(AppConfigUtils.get("kmWebService"));
//        svr.setAddress("http://192.168.113.146:8090/km/services/KnowledgeWS");
        KnowledgeWS hw = (KnowledgeWS) svr.create();
        
        Client client = ClientProxy.getClient(hw);   
        HTTPConduit http = (HTTPConduit) client.getConduit();   
        HTTPClientPolicy httpClientPolicy =  new  HTTPClientPolicy();   
        httpClientPolicy.setConnectionTimeout( 3600000 );   
        httpClientPolicy.setAllowChunking( false );   
        httpClientPolicy.setReceiveTimeout( 3200000 );   
        http.setClient(httpClientPolicy);
        
        
        return hw;
	}
	
	public static void main(String[] args){
		ExternalBiz er = new ExternalBiz();
//		er.getTypeList(); //获取所有知识形态
//		er.getModelListByTypeId("101");
//		er.getAllModelList();
		er.getModelProperties();
//		er.getModelProperties("578915");
	}
	/**
	 * 
	 * @param json 格式的字符串 
	 * @return  List<HashMap> 集合
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static List<HashMap<String,Object>> strToListMap(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		List<HashMap<String,Object>> readValue = om.readValue(json, new TypeReference<List<HashMap<String,Object>>>() {});
		return readValue;
	}
	/**
	 * 功能：获取KM模板属性全集
	 * 作者：井晓丹 2016.3.18
	 * @param url
	 * @return
	 */
	public String getModelProperties(){
        KnowledgeWS hw = getKnowledgeWS();
        // PROP_ID=13641 PROP_NAME=知识文本 TEPT_ID=1 TEPT_NAME=知识采集模板
        String allTempProp = hw.getAllTempProp();
        return allTempProp;
	}
	/**
	 * 功能：获取知识形态列表
	 * 作者：井晓丹 2016.3.18
	 * @return
	 */
	public String getTypeList() {
		KnowledgeWS hw = getKnowledgeWS();
		// CLASSIFY_ID=101   CLASSIFY_NAME=数据类
		String allTempProp = hw.getKnowledgeClassify();
		return allTempProp;
        
	}
	
	/**
	 * 功能：根据知识形态获取模板列表
	 * 作者：井晓丹 2016.3.18
	 * @param typeId 知识形态ID
	 * @return
	 */
	public String getModelListByTypeId(String classifyId) {
		KnowledgeWS hw = getKnowledgeWS();
		// TEPT_ID=1002 TEPT_NAME=数据类采集模版
        String allTempProp = hw.getPublishedTempByCid(classifyId);
        return allTempProp;
        
	}
	/**
	 * 功能：获取全部知识模板列表
	 * 作者：井晓丹 2016.3.18
	 * @param typeId 知识形态ID
	 * @return
	 */
	public String getAllModelList() {
		KnowledgeWS hw = getKnowledgeWS();
		// TEPT_ID=1002 TEPT_NAME=数据类采集模版
        String allTempProp = hw.getAllPublishedTemp();
        return allTempProp;
	}
	/**
	 * 功能：获取KM模板属性
	 * 作者：井晓丹 2016.3.18
	 * @param 模板id
	 * @return
	 */
	public String getModelProperties(String modelId){
		KnowledgeWS hw = getKnowledgeWS();
		// PROP_ID=13641 PROP_NAME=知识文本 TEPT_ID=1002
        String allTempProp = hw.getTempPropByTempId(modelId);
        return allTempProp;
	}
	
	/**
	 * 传输数据接口
	 * 作者：井晓丹 2016.3.18
	 * @param metadataIds
	 * @return
	 */
	public String saveMetadataList(List<KnowledgeMeta> transferList) {
//		return "[1001,1002,1003]";
		String result="";
		try {
			KnowledgeWS hw = getKnowledgeWS();
			result = hw.transform(transferList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 功能：获取KM固有属性
	 * 作者：井晓丹 2016.3.18
	 * @param 模板id
	 * @return
	 */
	public String getInherentProp(){
		KnowledgeWS hw = getKnowledgeWS();
		// PROP_ID=13641 PROP_NAME=知识文本 TEPT_ID=1002
        String inherentProp = hw.getInherentProp();
        return inherentProp;
	}
	
	/**
	 * 功能：新增(更新)知识源
	 * 作者：井晓丹 2016.4.27
	 * @param 知识源id,知识源名称,采集服务器id
	 * @return
	 */
	public boolean addOrUpdateSource(String sourceId,String sourceName){
		String serverId=AppConfigUtils.get("serverId");
		KnowledgeWS hw = getKnowledgeWS();
		return hw.syncSource(sourceId,sourceName,serverId);
//		String inherentProp = hw.getInherentProp();
      
	}
	
}
