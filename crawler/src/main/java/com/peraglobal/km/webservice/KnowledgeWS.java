package com.peraglobal.km.webservice;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.peraglobal.km.webservice.model.KnowledgeMeta;



@WebService
@SOAPBinding(style = Style.RPC)
public interface KnowledgeWS {

	
	/**
	 * 获取知识形态 
	 * @return JSON格式的字符串
	 */
	public String getKnowledgeClassify();
	
	/**
	 * 获取所有知识形态下的所有已发布的模板
	 * @return JSON格式的字符串
	 */
	public String getAllPublishedTemp();
	

	/**
	 * 获取对应知识形态下的所有模板
	 * @param classifyId
	 * @return
	 */
	public String getPublishedTempByCid(String classifyId);
	
	
	/**
	 * 根据模板ID获取对应模板下的属性
	 * @param teptId
	 * @return JSON格式的字符串
	 */
	public String getTempPropByTempId(String teptId);
	
	/**
	 * 获取所有模板下的所有属性
	 * @return
	 */
	public String getAllTempProp();
	
	/**
	 * 知识任务传输
	 * @param list
	 * @return
	 */
	public String transform(List<KnowledgeMeta> list);
	
	/**
	 * 获取固有属性
	 * @return
	 */
	public String getInherentProp();
	/**
	 * 同步知识源接口
	 * @return	同步成功或者失败  true or false
	 */
	public boolean  syncSource(String sourceid , String sourcename,String serverid);
	
}
