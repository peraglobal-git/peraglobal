package com.peraglobal.pdp.admin.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.PersonalCenterBiz;
import com.peraglobal.pdp.admin.enums.ModuleType;
import com.peraglobal.pdp.admin.utils.AdminConfigUtils;
import com.peraglobal.pdp.admin.vo.PersonalCenterVo;
import com.peraglobal.pdp.common.json.JsonUtils;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;

/**
 * 
 * 个人中心控制类
 *
 */
@Controller
public class PersonalCenterController extends ExtendedMultiActionController{
	
	private static final Logger logger = LoggerFactory.getLogger(PersonalCenterController.class);
	
	public static final int[] columns = new int[]{30,40,30};
	
	@Resource
	private PersonalCenterBiz personalCenterBiz;
	
	/**
	 * 查询首页数据信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toPersonalPage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView view = new ModelAndView("personalCenter");
		try {
			String userId = AdminConfigUtils.getCurrentUser().getId();
		   Map<String,Object> map  = personalCenterBiz.findPersonalCenterVoListByUserId(userId);
			//当前用户正在引用的标签
		    view.addObject("usedCard", map.get("usedCard"));
		    //当前用户正在引用的模块
		    List<PersonalCenterVo> usedModule = (List<PersonalCenterVo>)map.get("usedModule");
		    Collections.sort(usedModule);
		    List<List<PersonalCenterVo>> re = new ArrayList<List<PersonalCenterVo>>();
		    for(int i=0;i<columns.length;i++){
		    	re.add(new ArrayList<PersonalCenterVo>());
		    }
		    if(usedModule!=null && usedModule.size()>0 && re.size()>0){
		    	for(PersonalCenterVo pcv : usedModule){
		    		int colNum = pcv.getColNum();
		    		if(colNum<0){
		    			colNum=0;
		    		}else if(colNum>=re.size()){
		    			colNum=re.size()-1;
		    		}
		    		re.get(colNum).add(pcv);
		    	}
		    }
		

			view.addObject("usedModule", JsonUtils.getJsonValue(re));
			view.addObject("columns", columns);
			//所有未被引用的标签
			view.addObject("noCard", map.get("noCard"));
			//所有未被引用的模块
			view.addObject("noModule", map.get("noModule"));
			//模块、标签中需要引用到模块数据
			view.addObject("moduleItem", map.get("usedModule"));
		
			view.setViewName(getSkinPageName()+"personal/personalPage");
			
		} catch (Exception  e) {
			e.printStackTrace();
		}
		//转到页面
		return view;
	}
	/**
	 * 个人首页，主区域保存方法
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveLayout(HttpServletRequest request,HttpServletResponse response) {
		String layoutS = request.getParameter("layoutS");
		Map<String,String> result = new HashMap<String,String>();
		try {
			if(StringUtils.isNotEmpty(layoutS)) {
				List<Map<String,Object>> list= JsonUtils.readObject(layoutS, ArrayList.class);
				if(list!=null && list.size()>0){
					List<List<PersonalCenterVo>> re = new ArrayList<List<PersonalCenterVo>>();
					List<PersonalCenterVo> paramList = new ArrayList<PersonalCenterVo>();
					for(int i=0;i<columns.length;i++){
					    re.add(new ArrayList<PersonalCenterVo>());
					}
					if(re.size()>0){
						List<PersonalCenterVo> l = new ArrayList<PersonalCenterVo>();
						for(Map<String,Object> m : list){
							Integer deleted = (Integer)m.get("deleted");
							if(deleted!=null && deleted.equals(1)){
								
							}else{
								PersonalCenterVo pcv = new PersonalCenterVo();
								pcv.setAttrId((String)m.get("id"));
								pcv.setColNum((Integer)m.get("colIndex"));
								pcv.setSortNum((Integer)m.get("rowIndex"));
								l.add(pcv);
							}
						}
						Collections.sort(l);
						for(PersonalCenterVo pcv : l){
							int colNum = pcv.getColNum();
				    		if(colNum<0){
				    			colNum=0;
				    		}else if(colNum>=re.size()){
				    			colNum=re.size()-1;
				    		}
				    		re.get(colNum).add(pcv);
						}
						
						int ord = 0;
						for(List<PersonalCenterVo> colPanels : re){
							for(PersonalCenterVo pcv : colPanels){
								ord++;
								pcv.setSortNum(ord);
								paramList.add(pcv);
							}
						}
					}
					personalCenterBiz.saveLayout(paramList);
				}
				result.put("success", "true");
			}else {
				result.put("success", "false");
			}
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			e.printStackTrace();
			return JsonModelAndView.newSingle(result);
		}
	}
	/**
	 * 跳转到模块或者卡片的弹出页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView openModuleAndCardByFrom(HttpServletRequest request,HttpServletResponse response){
		ModelAndView view = new ModelAndView("personalCenter");
		try {
			String userId = AdminConfigUtils.getCurrentUser().getId();
			String type = request.getParameter("type");
			String from = request.getParameter("from");
		   Map<String,Object> map  = personalCenterBiz.findPersonalCenterVoListByType(userId,type);
			//当前用户正在引用的标签
		    view.addObject("usedCard", map.get("usedCard"));
		    //当前用户正在引用的模块
			view.addObject("usedModule", map.get("usedModule"));
			//所有未被引用的标签
			view.addObject("noCard", map.get("noCard"));
			//所有未被引用的模块
			view.addObject("noModule", map.get("noModule"));
			if("card".equals(from)){
				view.setViewName("");//需定义页面
			}
			if("module".equals(from)){
				view.setViewName("");
			}
		} catch (Exception  e) {
			e.printStackTrace();
		}
		//转到页面
		return view;
	}
	
	/**
	 * 模块、标签弹出框保存方法
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveModuleAndCardInfo(HttpServletRequest request,HttpServletResponse response){
		//前台返回的json串
		String paramstr = request.getParameter("paramstr");
		Map<String,String> result = new HashMap<String,String>();
		List<PersonalCenterVo> l = new ArrayList<PersonalCenterVo>();
		try {
			if(StringUtils.isNotEmpty(paramstr)) {
				int col=0;
				String[] arr = paramstr.split(",");
				for(int i=0;i<arr.length;i++){
					String str = arr[i];
					if(StringUtils.isNotEmpty(str)){
						String[] value = str.split("_");
						PersonalCenterVo pcv = new PersonalCenterVo();
						pcv.setAttrId(value[0]);
						//type是表示是 Module:模型;Card:标签
						pcv.setType(value[1]);
						pcv.setSortNum(i+1);
						if(i==0 || i%columns.length==0){
							col=0;
						}
						pcv.setColNum(col);
						l.add(pcv);
						col++;
					}
				}
			}
			personalCenterBiz.saveModuleAndCardInfo(l);
			result.put("success", "true");
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			e.printStackTrace();
			return JsonModelAndView.newSingle(result);
		}
	}
	
	/**
	 * 模块、标签弹出框保存方法
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveCardInfo(HttpServletRequest request,HttpServletResponse response){
		//前台返回的json串
		String paramstr = request.getParameter("paramstr");
		Map<String,String> result = new HashMap<String,String>();
		try {
			List<PersonalCenterVo> l = new ArrayList<PersonalCenterVo>();
			if(StringUtils.isNotEmpty(paramstr)) {
				int col=0;
				String[] arr = paramstr.split(",");
				for(int i=0;i<arr.length;i++){
					String str = arr[i];
					if(StringUtils.isNotEmpty(str)){
						String[] value = str.split("_");
						PersonalCenterVo pcv = new PersonalCenterVo();
						pcv.setAttrId(value[0]);
						pcv.setType(ModuleType.Card.toString());
						pcv.setSortNum(i+1);
						if(i==0 || i%columns.length==0){
							col=0;
						}
						pcv.setColNum(col);
						l.add(pcv);
						col++;
					}
				}
			}
			personalCenterBiz.saveCardInfo(l);
			result.put("success", "true");
			return JsonModelAndView.newSingle(result);
		}catch(Exception e) {
			result.put("success", "false");
			e.printStackTrace();
			return JsonModelAndView.newSingle(result);
		}
	}
	/**
	 * 查询首页数据信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView refleshData(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			String userId = AdminConfigUtils.getCurrentUser().getId();
		   Map<String,Object> map  = personalCenterBiz.findPersonalCenterVoListByUserId(userId);
			//当前用户正在引用的标签
		    result.put("usedCard", map.get("usedCard"));
		    //当前用户正在引用的模块
			//所有未被引用的标签
		    result.put("noCard", map.get("noCard"));
			//所有未被引用的模块
		    result.put("noModule", map.get("noModule"));
			//模块、标签中需要引用到模块数据
		    result.put("moduleItem", map.get("usedModule"));
		
			result.put("success", "true");
			
		} catch (Exception  e) {
			result.put("success", "false");
			e.printStackTrace();
		}
		//转到页面
		return JsonModelAndView.newSingle(result);
	}
}
