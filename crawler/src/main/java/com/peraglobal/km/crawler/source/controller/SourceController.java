package com.peraglobal.km.crawler.source.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.peraglobal.km.crawler.source.biz.KnowledgeSourceBiz;
import com.peraglobal.km.crawler.source.model.KnowledgeSource;
import com.peraglobal.km.crawler.source.model.WebSiteLoginEntity;
import com.peraglobal.km.crawler.util.CrawlerMD5Utils;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.km.crawler.util.JdbcConnectionUtil;
import com.peraglobal.pdp.common.utils.CollectionUtil;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.condition.ListPageQuery;
import com.peraglobal.pdp.core.spring.ExtendedMultiActionController;
import com.peraglobal.pdp.core.spring.JsonModelAndView;
/**
 * 知识源Controller类
 * @author DuanZheng
 *
 */
@Controller
public class SourceController extends ExtendedMultiActionController {
	@Resource
	private KnowledgeSourceBiz knowledgeSourceBiz;
	/**
	 * 跳转知识源页面
	 * author duanzheng
	 */
	public ModelAndView knowledgeSource(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("km/crawler/source/main");
	}
	/**
	 * 功能:查询知识源列表
	 * author duanzheng
	 * @return  List<KnowledgeSource> 
	 */
	public ModelAndView queryKnowledgeSourceList(HttpServletRequest request,HttpServletResponse response){
		ListPageQuery parameters = new ListPageQuery();
		List<KnowledgeSource> kn=null;
		try {
			new ServletRequestDataBinder(parameters).bind(request);
			 List<Condition> list=parameters.getConditions().getItems();
			 String typeSort=getStringValue(request, "type");
			 Map map=new HashMap();
			 //模糊查询
			 if(list.size()>0 && list!=null){
				 String value=""+parameters.getConditions().getItems().get(0).getValue();
				 value=value.trim();
				 if(value.equals("输入知识源名称进行筛选")){
					 parameters.setConditions(null);
				 }
				 kn= knowledgeSourceBiz.find(parameters.getPagination(),parameters.getConditions());
			 }
			 //查询全部
			 else if(typeSort==null ||typeSort=="" && list.size()<=0){
				 kn= knowledgeSourceBiz.find(parameters.getPagination(),parameters.getConditions());
				 //倒序查询
			}else if(typeSort!=null && typeSort.equals("typeSort") &&typeSort!=""){
				 map.put("typeSort", "desc");
				 kn=knowledgeSourceBiz.findKnowledgeListOrderType(parameters.getPagination());
				 //正序查询
			}else if(typeSort!=null && typeSort.equals("type") &&typeSort!=""){
				map.put("typeSort", "asc");
				kn=knowledgeSourceBiz.findKnowledgeListOrderTypeAsc(parameters.getPagination());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.putToModelAndViewJson(kn, parameters);
	}
	
	/**
	 * 功能：根据批量id删除批量知识源
	 * @param id
	 * author duanzheng
	 */
	public ModelAndView delKnowledgeSourceById(HttpServletRequest request,HttpServletResponse response){
		Map map=new HashMap();
		try {
			String[] array= request.getParameterValues("listId");
			List list=new ArrayList();
		if(array!=null){
			Map mapTo=null;
			for (int i = 0; i <array.length; i++) {
				String id= array[i];
				if(!(id.equals(""))){
					mapTo=new HashMap();
					mapTo.put("id", id);
					Map resultMap=knowledgeSourceBiz.queryTaskJobBySourceId(mapTo);
					if(Integer.parseInt(""+resultMap.get("SOURCEID"))>0){
						String count=""+resultMap.get("SOURCEID");
						String name=""+resultMap.get("NAME");
						 map.put("msg", "删除失败！"+name+"知识源下共有"+count+"条关联信息，请您删除关联信息后在删除知识源！");
						return this.putToModelAndViewJson(map);
					}
					list.add(id);
				}
			}
		}
		
			knowledgeSourceBiz.deleteByIds(list);
			map.put("msg", "删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("msg", "删除失败！");
		}
		
		return this.putToModelAndViewJson(map);
	}
	/**
	 * 功能：模糊查询信息列表
	 * @param nameValue
	 * @return List<Source>
	 * author duanzheng
	 */
	public ModelAndView fuzzyQueryInformationList(HttpServletRequest request,HttpServletResponse response){

		//获取分页对象
		ListPageQuery parameters = new ListPageQuery();
		//将分页对象和request绑定
		new ServletRequestDataBinder(parameters).bind(request);

		//分页查询方式,第一个参数是分页,如果不需要分页则传null,第二个参数是查询条件集合
		List<KnowledgeSource> roleList = knowledgeSourceBiz.find(parameters.getPagination(),parameters.getConditions());

		//Map<String, KnowledgeSource> map = BeanUtil.convertListToMap(roleList);
		//将分页结果返回到页面
		return this.putToModelAndViewJson(roleList, parameters);
	}
	
	/**
	 * 功能:跳转到新建知识源页面
	 * <p>作者 王晓鸣 2016-1-13
	 * @param request
	 * @param response
	 */
	public ModelAndView toKnowledgeAddPage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("km/crawler/source/saveKS");
		return model;
	}
	/**
	 * 功能:跳转到知识源编辑页面
	 * <p>作者 王晓鸣 2016-1-18
	 * @param request
	 * @param response
	 */
	public ModelAndView toKnowledgeEditPage(HttpServletRequest request,HttpServletResponse response){
		String id=getStringValue(request, "id");
		//获取点击状态     edit 编辑,view 查看  
		String clicktype=getStringValue(request, "clicktype");
		KnowledgeSource tjs = knowledgeSourceBiz.findById(id);
		String linktype=tjs.getLinkType();
		ModelAndView model = new ModelAndView();
		model.addObject("id", tjs.getId());
		model.addObject("name", tjs.getName());
		model.addObject("state", tjs.getLinkState());
		//增加判断此知识源是否关联任务逻辑 by yangxc
		//关联任务设置hasTasks为1，否则为0
		Map<String, String> mapTo = new HashMap<String, String>();
		mapTo.put("id", id);
		Map resultMap=knowledgeSourceBiz.queryTaskJobBySourceId(mapTo);
		if (Integer.parseInt(""+resultMap.get("SOURCEID")) > 0) {
			model.addObject("hasTasks", 1);
		} else {
			model.addObject("hasTasks", 0);
		}
		//获取xml解析对象
		Dom4jXmlUtil dm=new Dom4jXmlUtil();
		//获取MD5加密对象
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		//根据不同的知识源类型进入不同的编辑界面
		if (linktype.equals("1")) {
			//将数据库中的xml文件转换成map集合
			Map<String, String> map=dm.generateMap(tjs.getLinkContent());
			int maplength=map.size();
			if (maplength==2) {
				model.addObject("webname", map.get("webname"));
				model.addObject("weburl", map.get("weburl"));
				model.addObject("radiotype", "0");
			}else if(maplength==6){
				model.addObject("radiotype", "1");
				model.addObject("webname", map.get("webname"));
				model.addObject("weburl", map.get("weburl"));
				model.addObject("loginrequest", map.get("loginrequest"));
				model.addObject("user_k", map.get("user_k"));
				model.addObject("password_k", map.get("password_k"));
				model.addObject("loginsubmit", map.get("loginsubmit"));
				model.addObject("username", tjs.getUsername());
				model.addObject("password", md5Utils.Decryption(tjs.getPassword()));
			}
			if ("view".equals(clicktype)) {
				model.setViewName("km/crawler/source/viewWeb");
			}else if ("edit".equals(clicktype)) {
				model.setViewName("km/crawler/source/editWeb");
			}
			
		}else if (linktype.equals("2")) {		
			Map<String, String> map=dm.generateMap(tjs.getLinkContent());
			/*String[] url_str=map.get("url").split(":");
			String url=url_str[3].substring(1);
			String port=url_str[4];
			String DBtype=url_str[1];*/
			String url=map.get("url");
			String port=map.get("port");
			String DBtype=map.get("type");
			model.addObject("username", tjs.getUsername());
			model.addObject("password", md5Utils.Decryption(tjs.getPassword()));
			model.addObject("dataname", map.get("name"));
			model.addObject("url", url);
			model.addObject("port", port);
			model.addObject("DBtype", DBtype);
			if ("view".equals(clicktype)) {
				model.setViewName("km/crawler/source/viewDB");
			}else if ("edit".equals(clicktype)) {
				model.setViewName("km/crawler/source/editDB");
			}
		}else if (linktype.equals("3")) {
			if ("view".equals(clicktype)) {
				model.setViewName("km/crawler/source/viewLocal");
			}else if ("edit".equals(clicktype)) {
				model.setViewName("km/crawler/source/editLocal");
			}
		}
		return model;
		
	}
	/**
	 * 功能:验证任务名是否重复
	 * <p>作者 王晓鸣 2016-1-13
	 * @param request
	 * @param response
	 */
	public ModelAndView validateNameByAjax(HttpServletRequest request,
			HttpServletResponse response) {
		String name = getStringValue(request, "name");
		String oldName = getStringValue(request, "oldName");
		// 如果没有修改名称则直接返回
		if (name.equals(oldName)) {
			return JsonModelAndView.newSingle(true);
		}
		// 验证知识源名称 中文、英文、数字
		/*String regex = "^[\u4e00-\u9fa5_a-zA-Z0-9_]{0,100}";
		if (!Pattern.matches(regex, name)) {
			return JsonModelAndView.newSingle("请输入正确的任务名称");
		}*/
		Condition condition = Condition.parseCondition("name_eq");
		condition.setValue(name);
		List<Condition> conList = new ArrayList<Condition>();
		conList.add(condition);
		List<KnowledgeSource> jobList = knowledgeSourceBiz.find(null, conList);
		if (!CollectionUtil.isEmpty(jobList)) {
			return JsonModelAndView.newSingle("任务名称重复");
		}
		return JsonModelAndView.newSingle(true);
	}
	/**
	 * 功能:新建、编辑知识源
	 * <p>作者 王晓鸣 2016-1-14
	 * @param request
	 * @param response
	 */		
	public ModelAndView saveKnowledgeSource(HttpServletRequest request,HttpServletResponse response,KnowledgeSource knowledgeSource){
		Map<String, Object> result = new HashMap<String, Object>();
		Dom4jXmlUtil dm=new Dom4jXmlUtil();
		CrawlerMD5Utils md5Utils = new CrawlerMD5Utils();
		String linktype=request.getParameter("SelectType");
		String linkstate=getStringValue(request, "linkState");
		String oldName=getStringValue(request, "oldname");
		if ("2".equals(linktype)) {
			String DBtype=request.getParameter("DBSelectType");
			String driver=null;
			if (DBtype.equals("oracle")) {
				driver="oracle.jdbc.driver.OracleDriver";
			}else if (DBtype.equals("mysql")) {
				driver="com.mysql.jdbc.Driver";
			}else if (DBtype.equals("sqlserver")) {
				driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
				//String url="jdbc:sqlserver://localhost:1433; DatabaseName=UniversityDB";
			}
			String name=getStringValue(request, "dataname");
			String port=getStringValue(request, "port");
			String dburl=getStringValue(request, "dburl");
			String[] filedNames=new String[5];
			String[] knowledgeKeys= new String[5];
			filedNames[0]="name";
			knowledgeKeys[0]=name;
			filedNames[1]="type";
			knowledgeKeys[1]=DBtype;
			filedNames[2]="driver";
			knowledgeKeys[2]=driver;
			filedNames[3]="url";
			knowledgeKeys[3]=dburl;
			filedNames[4]="port";
			knowledgeKeys[4]=port;
			//再次进行数据库链接验证
			String state=new JdbcConnectionUtil().LinkVerification(DBtype, dburl, port, name, knowledgeSource.getUsername(), knowledgeSource.getPassword());
			String linkcontent=dm.extractMapping(filedNames, knowledgeKeys);
			knowledgeSource.setPassword(md5Utils.encrypt(knowledgeSource.getPassword()));
			knowledgeSource.setLinkContent(linkcontent);
			knowledgeSource.setLinkType(linktype);
			knowledgeSource.setLinkState(state);
		}
		else if("1".equals(linktype)){
			String checkstate=getStringValue(request, "checkstate");
			String webname=getStringValue(request, "webname");
			String weburl=getStringValue(request, "weburl").replaceAll("&", "&amp;");
			
			String[] filedNames;
			String[] knowledgeKeys;
			int state = 0;
			if ("0".equals(checkstate)) {
				filedNames=new String[2];
				knowledgeKeys= new String[2];
				filedNames[0]="webname";
				knowledgeKeys[0]=webname;
				filedNames[1]="weburl";
				knowledgeKeys[1]=weburl;
			}else {
				String loginRequest=getStringValue(request, "loginrequest").replaceAll("&", "&amp;");
				String user_k=getStringValue(request, "user_k");
				String user_v=getStringValue(request, "username_v");
				String password_k=getStringValue(request, "password_k");
				String password_v=getStringValue(request, "password_v");
				String loginSubmit=getStringValue(request, "loginsubmit").replaceAll("&", "&amp;");
				filedNames=new String[6];
				knowledgeKeys= new String[6];
				filedNames[0]="webname";
				knowledgeKeys[0]=webname;
				filedNames[1]="weburl";
				knowledgeKeys[1]=weburl;
				filedNames[2]="loginrequest";
				knowledgeKeys[2]=loginRequest;
				filedNames[3]="user_k";
				knowledgeKeys[3]=user_k;
				filedNames[4]="password_k";
				knowledgeKeys[4]=password_k;
				filedNames[5]="loginsubmit";
				knowledgeKeys[5]=loginSubmit;
				
				knowledgeSource.setUsername(user_v);
				knowledgeSource.setPassword(password_v!=null ? md5Utils.encrypt(password_v) : "");
				WebSiteLoginEntity entity = new WebSiteLoginEntity();
				entity.setLoginRequest(loginRequest);
				entity.setUser_k(user_k);
				entity.setPassword_k(password_k);
				entity.setUsername(user_v);
				entity.setPassword(password_v);
				entity.setLoginSubmit(loginSubmit);
				try {
					state = this.knowledgeSourceBiz.loginWebSite(entity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String linkcontent=dm.extractMapping(filedNames, knowledgeKeys);
			knowledgeSource.setLinkContent(linkcontent);
			knowledgeSource.setLinkType(linktype);
			knowledgeSource.setLinkState(state+"");
		}
		else if("3".equals(linktype)){
			knowledgeSource.setLinkType(linktype);
			knowledgeSource.setLinkState(linkstate);
		}
		String operateStr = null;
		String id =knowledgeSource.getId();
		if (id == null) {
			knowledgeSource.setId(null);
			operateStr = "添加";
		} else {
			knowledgeSource.setId(id);
			operateStr = "修改";
		}
		try {
			knowledgeSourceBiz.save(knowledgeSource);
//			knowledgeSourceBiz.saveKnowledgeSource(knowledgeSource, oldName);
			result.put("code", "ok");
			operateStr += "成功！";
		} catch (Exception e) {
			result.put("code", "fail");
			operateStr += "失败！";
			logger.error(" saveTransferTaskJob save exception", e);
		}
		result.put("id", knowledgeSource.getId());
		result.put("msg", operateStr);
		return this.putToModelAndViewJson(result);
		
	}
	
	/**
	 * 功能:验证数据库链接
	 * <p>作者 王晓鸣 2016-1-15
	 * @param request
	 * @param response
	 */		
	public ModelAndView getLinkDB(HttpServletRequest request,HttpServletResponse response){
		String name=getStringValue(request, "dataname");
		String DBtype=request.getParameter("DBSelectType");
		String port=getStringValue(request, "port");
		String dburl=getStringValue(request, "dburl");
		String user=getStringValue(request, "username");
		String password=getStringValue(request, "password");
		String state=new JdbcConnectionUtil().LinkVerification(DBtype, dburl, port, name, user, password);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", state);
		return this.putToModelAndViewJson(result);
		
	}
	
	/**
	 * 功能:验证网页链接
	 * <p>作者 井晓丹 2016-1-20
	 * @param request
	 * @param response
	 */		
	public ModelAndView getLinkWeb(HttpServletRequest request,HttpServletResponse response){
		String loginRequest=getStringValue(request, "loginRequest");
		String user_k=getStringValue(request, "user_k");
		String password_k=getStringValue(request, "password_k");
		String username=getStringValue(request, "username");
		String password=getStringValue(request, "password");
		String loginSubmit=getStringValue(request, "loginSubmit");
		
		WebSiteLoginEntity entity = new WebSiteLoginEntity();
		entity.setLoginRequest(loginRequest);
		entity.setUser_k(user_k);
		entity.setPassword_k(password_k);
		entity.setUsername(username);
		entity.setPassword(password);
		entity.setLoginSubmit(loginSubmit);
		
		int state = 0;
		try {
			state = this.knowledgeSourceBiz.loginWebSite(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("state", state);
		return this.putToModelAndViewJson(result);
	}
	
}
