package com.peraglobal.km.crawler.task.biz;

import java.util.List;

import javax.annotation.Resource;

import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.task.dao.TaskRegisterDao;
import com.peraglobal.km.crawler.task.model.TaskConfig;
import com.peraglobal.km.crawler.task.model.TaskRegister;
import com.peraglobal.km.crawler.util.Dom4jXmlUtil;
import com.peraglobal.pdp.core.BaseBiz;

/**
 * 2015-12-18
 * @author xiaoming.wang
 * 任务类型注册操作管理业务逻辑接口
 */
@Service
public class TaskRegisterBiz extends BaseBiz<TaskRegister, TaskRegisterDao>{
	
	@Resource
	private TaskRegisterDao taskRegisterDao;
	
	/**
	 * 添加任务注册类型
	 * @param tr 任务注册类型对象
	 * @return true or false
	 */
	public boolean addRegister(TaskRegister tr) {
		try {
			tr.setId(taskRegisterDao.createId());
			return taskRegisterDao.addRegister(tr) == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 编辑任务注册类型
	 * @param tr 任务注册类型对象
	 * @return true or false
	 */
	public boolean editRegister(TaskRegister tr) {
		try {
			return taskRegisterDao.editRegister(tr) == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除任务注册类型
	 * @param register_id 注册类型 ID
	 * @return true or false
	 */
	public boolean delRegister(String register_id) {
		try {
			return taskRegisterDao.delRegister(register_id) == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获得所有任务注册类型
	 * @return 任务注册类型集合
	 */
	public List<TaskRegister> queryRegisterList() {
		try {
			return taskRegisterDao.queryRegisterList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据任务注册类型 ID 获得任务注册类型
	 * @param register_id 任务注册类型 ID
	 * @return 任务注册类型实例
	 */
	public TaskRegister queryRegisterById(String register_id) {
		try {
			return taskRegisterDao.queryRegisterById(register_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 验证当前任务类型注册名称是否重复
	 * @param tr 任务注册类型对象
	 * @return 是否重复
	 */
	public boolean findRegisterName(TaskRegister tr) {
		try {
			
			// 新建时验证
			if(tr.getId() == null || tr.getId().equals("")){
				return taskRegisterDao.findRegisterNameByName(tr.getRegisterName()) == 0 ? true : false;
			}else{
				// 编辑时验证
				return taskRegisterDao.findRegisterNameById(tr.getId(), tr.getRegisterName()) == 0 ? true : false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 根据任务类型获得注册任务对象
	 * @param register_type 类型标示
	 * @return 任务注册类型实例
	 */
	public TaskRegister queryRegisterByType(String register_type) {
		try {
			return taskRegisterDao.queryRegisterByType(register_type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据任务注册类型获得任务配置XML对象
	 * @param register_type 类型标示
	 * @return 任务注册类型集合
	 */
	public List<List<TaskConfig>> getTaskConfigByType(String register_type) {
		try {
			if(register_type != null){
				Element root = Dom4jXmlUtil.readElements(register_type);
				return Dom4jXmlUtil.readXML(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
