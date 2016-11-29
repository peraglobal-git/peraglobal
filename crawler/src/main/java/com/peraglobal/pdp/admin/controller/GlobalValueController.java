package com.peraglobal.pdp.admin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.GlobalValueBiz;
import com.peraglobal.pdp.admin.model.GlobalValue;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;

/**
 *  <code>GlobalValueController.java</code>
 *  <p>功能:全局配置值Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 郭宏波 hongbo.guo@peraglobal.com 时间 2015-6-11	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class GlobalValueController extends ExtendedMultiActionController{
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalValueController.class);

	//全局值
	@Resource
	private GlobalValueBiz globalValueBiz;

	
	/**
	 * 保存属性值
	 * @param request
	 * @param response
	 */
	public ModelAndView saveValue(HttpServletRequest request,HttpServletResponse response) {
		
		Map<String,String> map = new HashMap<String,String>();
		try {
			//全局ID
			String globalId = getStringValue(request, "id");

			//保存的值类型
			String valueType = request.getParameter("type");
			//保存的值
			String data = request.getParameter("data");
			
			Condition condition = Condition.parseCondition("globalId_eq");
			condition.setValue(globalId);
			//查找是否有相同的值
			List<GlobalValue> globalValue = globalValueBiz.find(condition);
			if(globalValue.size() > 0) {
				GlobalValue gValue = globalValue.get(0);
				gValue.setGlobalId(globalId);
				gValue.setEntityId(1l);
				setValue(data, valueType, gValue);
				List<GlobalValue> list = new ArrayList<GlobalValue>();
				list.add(gValue);
				globalValueBiz.updateBatch(list);
			}else {
				GlobalValue value = new GlobalValue();
				value.setGlobalId(globalId);
				value.setEntityId(1l);
				setValue(data, valueType, value);
				globalValueBiz.save(value);
			}
			map.put("success", "success");
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			map.put("success", "false");
			map.put("msg", "保存数据失败");
			LOG.error(e.getMessage(),e);
			return JsonModelAndView.newSingle(map);
		}
	}
	
	
	/**
	 * 根据值类型,设置值
	 * @param data
	 * @param valueType
	 * @param value
	 */
	private void setValue(String data,String valueType,GlobalValue value) {
		if(valueType.toLowerCase().equals("varchar")) {
			value.setValueString(data);
		}
		if(valueType.toLowerCase().equals("text")) {
			value.setValueText(data);
		}
		if(valueType.toLowerCase().equals("datetime")) {
			long parse = Date.parse(data);
			Date d = new Date(parse);
			value.setValueDate(d);
		}
		if(valueType.toLowerCase().equals("int")) {
			Integer parseInt = Integer.parseInt(data);
			value.setValueInteger(parseInt);
		}
		if(valueType.toLowerCase().equals("bigint")) {
			Long parseLong = Long.parseLong(data);
			value.setValueLong(parseLong);
		}
	}
	
}
