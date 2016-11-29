package com.peraglobal.pdp.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.pdp.admin.biz.TestBiz;
import com.peraglobal.pdp.admin.biz.CreateTableSqlBiz;
import com.peraglobal.pdp.admin.endpoint.AdminOutEndpoint;
import com.peraglobal.pdp.admin.enums.TestStatus;
import com.peraglobal.pdp.admin.model.Test;
import com.peraglobal.pdp.admin.model.TestM;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.common.utils.StringUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

/**
 *  <code>TestpdpController.java</code>
 *  <p>功能:增删改查Demo
 *  
 *  <p>Copyright 安世亚太 2015 All right reserved.
 *  @author 文齐辉 qihui.wen@peraglobal.com 时间 2015-4-30	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
@Controller
public class TestController extends ExtendedMultiActionController{

	
	@Resource
	private TestBiz testBiz ;
	
	@Resource
	private AdminOutEndpoint outEndpoint ;
	
	@Resource
	private CreateTableSqlBiz tableSqlBiz;
	
	/**
	 * 功能:保存页面跳转
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveTestPage(HttpServletRequest request,HttpServletResponse response){
		String id = getStringValue(request,"id");
		ModelAndView view = new ModelAndView(getSkinPageName()+"test/saveTest");
		if(!StringUtil.isEmpty(id)){
			Test test = testBiz.findById(id);
			view.addObject("obj",test);
		}
		return view;
	}
	
	/**
	 * 功能:保存页面跳转
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public void saveTest(HttpServletRequest request,HttpServletResponse response,Test test){
		Map<String, String> result = new HashMap<String, String>();
		String operateStr = null;
		if (test.getId() == null) {
			operateStr = "添加";
		} else {
			operateStr = "修改";
		}
		try {
			test.setStatus(TestStatus.Normal);
			testBiz.save(test);
			result.put("code", "ok");
			operateStr += "成功！";
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveTest save exception", e);
		}
		result.put("msg", operateStr);
		outJSON(response, result);
	}
	
	/**
	 * 功能:查找列表
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView findTestList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		/*动态添加条件
		 *  Condition condition = Condition.parseCondition("deleted_int_eq");
		  condition.setValue(1);
		  parameters.getConditions().getItems().add(condition);*/
		List<Test> list = testBiz.find(parameters.getPagination(), parameters.getConditions());
		ModelAndView modelAndView =  this.putToModelAndView(getSkinPageName()+"test/findTestList",list, parameters);
		return modelAndView;
	}
	
	
	/**
	 * 功能:查找列表
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getTestList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		new ServletRequestDataBinder(parameters).bind(request);
		/*动态添加条件
		 *  Condition condition = Condition.parseCondition("deleted_int_eq");
		  condition.setValue(1);
		  parameters.getConditions().getItems().add(condition);*/
		List<Test> list = testBiz.find(parameters.getPagination(), parameters.getConditions());
		ModelAndView modelAndView = this.putToModelAndView(getSkinPageName()+"test/findTestList",list, parameters);
		return modelAndView;
	}
	
	/**
	 * 功能:删除，支持批量
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> result = new HashMap<String, String>();
		List<String> ids = CollectionUtil.toStringCollection(request.getParameter("ids"));
		testBiz.deleteByIds(ids);
		result.put("code", "ok");
		outJSON(response, result);
	}
	
	/**
	 * 功能:验证用户名是否重复
	 * <p>作者 文齐辉 2015-5-5
	 * @param request
	 * @param response
	 */
	public void validateNameByAjax(HttpServletRequest request, HttpServletResponse response){
		String username = request.getParameter("username");
		Condition condition = Condition.parseCondition("username_eq");
		condition.setValue(username);
		List<Test> testList = testBiz.find(null, condition);
		Map<String, String> result = new HashMap<String, String>();
		if(!CollectionUtil.isEmpty(testList)){
			result.put("code", "fail");
			result.put("msg", "用户名重复");
		}
		outJSON(response, result);
	}
	
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response){
		ModelAndView view = new ModelAndView(getSkinPageName()+"test/index");
		TestM config = (TestM)request.getSession().getAttribute("config");
		if(config!=null){
			view.addObject("config",config);
		}
		return view;
	}
	
	public ModelAndView saveOrUpdate(HttpServletRequest request,HttpServletResponse response,TestM test) {
		//返回信息
		Map<String,String> map = new HashMap<String,String>();
		try{
			request.getSession().setAttribute("config", test);
			String tableSQL = getStringValue(request,"sql");
			if(!StringUtil.isEmpty(tableSQL)){
				outEndpoint.callGenerateCode(test, tableSQL);
			}
			map.put("success", "success");
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			e.printStackTrace();
			map.put("success", "false");
			return JsonModelAndView.newSingle(map);
		}
	}
	
	/**
	 * createSql页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView createTablePage(HttpServletRequest request,HttpServletResponse response){
		ModelAndView view = new ModelAndView(getSkinPageName()+"test/createSql");
		return view;
	}
	
	/**
	 * 创建表
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView createTable(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		try{
			String tableSQLAll = getStringValue(request,"createSql");
			if(!StringUtil.isEmpty(tableSQLAll)){
				String[] split = tableSQLAll.split(";");
				for(String sql : split) {
					//获得当前使用数据库类型
					String dbType = AppConfigUtils.get("db.url.master.default").split(":")[1]; 
					//获取表名
					String table = getTableName(sql.split("\n")[0]);
					String tableType = sql.split(" ")[0];
					String type = getSQLType(sql);
					if("CREATE".equals(tableType) || "create".equals(tableType)){
						if(MYSQL_TYPE.equals(type) && "mysql".equals(dbType)){
							int count = tableSqlBiz.existTableMySql(table);
							if(count != 0){
								map.put("success", "exist");
								return JsonModelAndView.newSingle(map);
							}
						}else{
							if(!"mysql".equals(dbType)){
								int count = tableSqlBiz.existTableOracle(table);
								if(count != 0){
									map.put("success", "exist");
									return JsonModelAndView.newSingle(map);
								}
							}else{
								map.put("success", "dberror");
								return JsonModelAndView.newSingle(map);
							}
						}
					}
					tableSqlBiz.executeCreateTableSql(sql);
				}
			}
			map.put("success", "success");
			return JsonModelAndView.newSingle(map);
		}catch(Exception e) {
			e.printStackTrace();
			map.put("success", "false");
			return JsonModelAndView.newSingle(map);
		}
	}
	
	/**
	 * 暂时拿出来
	 * */
	private static final Pattern TABLE_PATTERN=Pattern.compile("TABLE\\s+([^\\s.]+)\\s*|\\.+([^\\s]+)\\s*",Pattern.CASE_INSENSITIVE);
	private String getTableName(String target){
		Matcher matcher = TABLE_PATTERN.matcher(target);
		String table=null;
		while(matcher.find()){
			if(matcher.group(2)!=null)
				table = matcher.group(2);
			else
				table = matcher.group(1);
			if(!StringUtil.isEmpty(table)){
				table = table.replaceAll("\\s|\\(|\"","");
			}else{
				table = matcher.group(0).replaceAll("\\s|\\(|\"","");
			}
		}
		return table;
	}
	
	/**
	 * 功能:获取sql类型
	 * <p>作者文齐辉 2015年10月19日 下午5:24:42
	 * @param tableSQL
	 * @return MySQL,Oracle
	 */
	private static final String MYSQL_TYPE="MySQL";
	private String getSQLType(String tableSQL){
		boolean oracle = tableSQL.lastIndexOf("TABLESPACE")!=-1 || tableSQL.indexOf("NVARCHAR2")!=-1 || tableSQL.indexOf("NUMBER")!=-1;
		return oracle?"Oracle":"MySQL";
	
	}
}
