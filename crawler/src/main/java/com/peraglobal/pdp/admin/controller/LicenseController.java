package com.peraglobal.pdp.admin.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.LicenseBiz;
import com.peraglobal.pdp.admin.model.License;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;

/**
 *  <code>LicenseController.java</code>
 *  <p>功能:软件许可Controller
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 侯志强 zhiqiang.hou@peraglobal.com 时间 2015-7-31
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class LicenseController extends ExtendedMultiActionController{

	private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);

	/**
	 * 转到license页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView tolicensePage(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView view = new ModelAndView(getSkinPageName()+"license/licensePage");
		// 获取软件许可信息
		License license = new License();
		try {
			license = LicenseBiz.readLicenseFile();
			view.addObject("license",license);
		} catch (Exception e) {
			view.addObject("license",license);
			logger.error("license许可信息读取失败！",e);
		}
		
		return view;
	}

	/**
	 * 获取license信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getLicense(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();

		License license;
		try {
			// 获取软件许可信息
			LicenseBiz.uploadLicenseFile(request);
			license = LicenseBiz.readLicenseFile();
			
			if(license!=null){
				result.put("success","true");
				result.put("license",license);
			}
			//转换成Json格式返回
			return JsonModelAndView.newSingle(result);
		} catch (Exception e) {
			logger.error("license信息获取失败！");
			result.put("success", "false");
			return JsonModelAndView.newSingle(result);
		}
	}

}
