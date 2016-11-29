package com.peraglobal.km.crawler.task.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import com.peraglobal.km.crawler.task.biz.TaskJobBiz;
import com.peraglobal.km.crawler.task.dao.TaskGroupDao;
import com.peraglobal.km.crawler.task.model.TaskGroup;
import com.peraglobal.km.crawler.task.model.TaskJob;
import com.peraglobal.pdp.common.bean.TreeNode;
import com.peraglobal.pdp.core.BaseBiz;
import com.peraglobal.pdp.core.BaseDao;
import com.peraglobal.pdp.core.condition.Condition;
import com.peraglobal.pdp.core.model.BaseIncrementIdModel;
import com.peraglobal.pdp.modeler.enums.DicCategoryEnum;
import com.peraglobal.pdp.modeler.model.Dictionary;



/**
 * 2015-12-16
 * @author zheng.duan
 * 任务组操作管理业务逻辑实现
 */
@Service
public class TaskGroupBiz extends BaseBiz<TaskGroup,TaskGroupDao>{

	@Resource
	private TaskGroupDao taskGroupDao;
	
	@Resource
	private TaskJobBiz taskJobBiz;
	
	
	/**
	 * 查询字典树
	 * @return
	 */
	public List<TreeNode> getTreeList(String typeId) {
		//获取所有的节点
		//	List<Dictionary> list = this.find(new SingleFieldCondition());
		//不读字典项
		String query = "group_nq";
		Condition condition = Condition.parseCondition(query);
		//condition.setValue(DicCategoryEnum.DicItem);
		List<TaskGroup> list = this.find(condition);
		//获取到节点
		if(list.size() > 0) {
			//用于比较节点的map,字符串类型的id作为key
			Map<String, TreeNode> treeMap = new HashMap<String, TreeNode>();
			List<TreeNode>  childs = new ArrayList<TreeNode> ();
			for (TaskGroup group : list) {
				String id = group.getId();
				TreeNode currentNode = treeMap.get(id+"");
				if (currentNode == null) {
					currentNode = new TreeNode();
					currentNode.setId(id);
					currentNode.setText(group.getName());
					treeMap.put(id+"", currentNode);
				}else{
					currentNode.setId(id);
					currentNode.setText(group.getName());
					treeMap.put(id+"", currentNode);
				}
				currentNode.setIconCls("icon-system");
				String parentId = group.getPid();
				if(parentId!=null){
					if (parentId.equals(typeId)) {
						childs.add(currentNode);
					} else {
						TreeNode parent = treeMap.get(parentId);
						if (parent == null) {
							parent = new TreeNode();
							parent.setId(parentId);
							treeMap.put(parentId, parent);
						}
						parent.addChildren(currentNode);
						treeMap.put(parentId, parent);
					}
				}
			}
			return childs;
		}else {
			return new ArrayList<TreeNode>();
		}
	}
	
	
	/**
	 * 添加任务组实例
	 * @param qgd 组对象
	 * @return 组 ID
	 */
	
	public String addGroup(Map map) {
		TaskGroup taskGroup=new TaskGroup();
		try {
			//taskGroup.setId(taskGroupDao.createId());
			int result = taskGroupDao.addGroup(map);
			if(result == 1){
				return taskGroup.getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 编辑任务组实例
	 * @param qgd 组对象
	 * @return true or false
	 */
	public boolean editGroup(TaskGroup qgd) {
		try {
			int result = taskGroupDao.editGroup(qgd);
			return result == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	
	/**
	 * 根据父节点获得所有组
	 * @param group_perant_id 父组 ID
	 * @return 组集合
	 */
	public List<TaskGroup> queryGroupList(String group_perant_id){
		try {
			return taskGroupDao.queryGroupList(group_perant_id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据组ID获得组实例
	 * @param group_id 组 ID
	 * @return 组实例
	 */
	public TaskGroup queryGroupByGroupId(Map map) {
		try {
			return taskGroupDao.queryGroupByGroupId(map);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据组父ID判断子组的数量
	 * @param group_perant_id 父组 ID
	 * @return count
	 */
	public int queryGroupCountByGroupPerantId(String group_perant_id) {
		try {
			return taskGroupDao.queryGroupCountByGroupPerantId(group_perant_id);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 验证当前节点组名称是否重复，修改组名称和创建时使用
	 * @param taskGroup 任务组对象
	 * @return true or false
	 */
	public boolean findGroupName(TaskGroup taskGroup) {
		try {
			// 编辑组时验证功能
			if(taskGroup.getId() != null 
					&& !taskGroup.getId().equals("")){
				return taskGroupDao.findCountByPidAndId(taskGroup) > 0 ? false : true;
				
			}else{ // 新建组时验证功能
				return taskGroupDao.findCountByPid(taskGroup) > 0 ? false : true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 根据ID返回相应的type
	 * @param id 
	 */
	public String findTypeById(String id){
		try {
			Map map = new HashMap();
			map.put("id", id);
			return taskGroupDao.queryGroupByGroupId(map).getType();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
