/**
 * role 角色JS
 */
define(function(require,exports,module){
	//artDialog
	var dialog  = require('artDialogJs');
	//easyui Tree树
	var tree  = require('tree');
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	
	//tree ul Id
	var treeId = "tree";
	
	//上一次点击树的节点ID
	var lastTreeId = "",lastDataId="";
	//判断删除后是否返回当前页
	var loadIsCreentPage=false;
	/**
	 * 刷新表格数据
	 */
	function reload(){
 		//$("#"+gridId).datagrid("reload",Util.ajax.serializable($("#listForm")));
		var node = $('#'+treeId).tree('getSelected');
		if(node.attributes.parentId == "0") {
 			$("#parentId").val(node.id);
 			$("#id").val("");
 		}else {
 			$("#parentId").val("");
 			$("#id").val(node.id);
 		}
		$("#search_button").click();
		
	}
	
	
	/**
	 * 刷新树数据
	 */
	function reloadTree() {
		$("#"+treeId).tree('reload');
	}
	
	
	/**
	 * 添加角色
	 */
	function addRoleClick(){
    	var selectedTreeNode = $('#'+treeId).tree('getSelected');
    	if(selectedTreeNode != undefined) {
    		var dialogId = 'role';
    		var param = "parentId=" + selectedTreeNode.id + "&ran=" + Math.random();
    		var dialogTitle = '添加角色';
    		openDialog(dialogTitle,param,dialogId);
    	}else {
    		showErrorTip("请选择一行");
    	}
	}

	
	/**
	 * 修改角色
	 */
	function updateRoleClick(){
		var selectedTreeNode = $('#'+treeId).tree('getSelected');
		var rows = $('#'+gridId).datagrid('getSelections');
		
		if(rows.length > 1) {
			showErrorTip("请选择一行");
			return;
		}
		
		var row = rows[0];
		lastDataId = row.id;
		if(row != undefined) {
    		var dialogId = 'role';
    		var param = "id="+row.id + "&parentId=" + selectedTreeNode.id + "&ran=" + Math.random();
    		var dialogTitle = '修改角色';
    		openDialog(dialogTitle,param,dialogId);
		}else {
			showErrorTip("请选择一行");
		}
			
	}
	/**
	 * 修改角色
	 */
	function updateRoleRowClick(id){
		loadIsCreentPage=true;
		var selectedTreeNode = $('#'+treeId).tree('getSelected');
		lastDataId = id;
		var dialogId = 'role';
		var param = "id="+id + "&parentId=" + selectedTreeNode.id + "&ran=" + Math.random();
		var dialogTitle = '修改角色';
		openDialog(dialogTitle,param,dialogId);
	}

	
	/**
	 * 删除角色
	 */
	function delRoleClick() {
		loadIsCreentPage=true;
		var rows = $('#'+gridId).datagrid('getSelections');
		submitDel('../role/delRole.html',{
			rows:rows,
			callback:function(data){
				reloadTree();
			}
		});
	}
	
	/**
	 * 初始化按钮事件
	 */
	function initButtonClick(){
		$("#toolbar .easyui-linkbutton").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(id.indexOf("add")!=-1){
				target.on('click',function(){addRoleClick();});
			}else if(id.indexOf("update")!=-1){
				target.on('click',function(){updateRoleClick();});
			}else if(id.indexOf("delete")!=-1){
				target.on('click',function(){delRoleClick();});
			}
		});
	}

	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		 $("#search").inputTextEvent({callback:function(val){
			 //如果不是修改则
			 if(loadIsCreentPage){
				 $('#grid').datagrid("reload",Util.ajax.serializable($("#listForm")));
				 loadIsCreentPage=false;
			 }else{
				 $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
			 }
		  }});
	}
	 
	
	
	/**
	 * 启用按钮
	 */
	function onRoleEnableClick() {
		$('#roleEnable').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			if(rows.length > 0) {
				showQuestionTip({
					content:'确认启用选中的角色？',
					ok:function(){
						getRowsToEnOrDis('enable');
					}
				});
			}else{
				showErrorTip("请选择至少一行数据");
			}
			
		});
	}
	

	
	/**
	 * 停用按钮
	 */
	function onRoleDisableClick() {
		$('#roleDisable').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			if(rows.length > 0) {
				showQuestionTip({
					content:'确认停用选中的角色？',
					ok:function(){
						getRowsToEnOrDis('disable');
					}
				});
			}else{
				showErrorTip("请选择至少一行数据");
			}
		});
	}
	
	/**
	 * 用于启用或者停用按钮的点击事件,flag传入的是enable或者disable,代表启用或者停用
	 */
	function getRowsToEnOrDis(flag) {
		var rows = $('#'+gridId).datagrid('getSelections');
		var arr = [];
		if(rows.length > 0) {
			for(index in rows) {
				arr.push(rows[index].id);
			}
		}else {
			showErrorTip("请选择至少一行数据");
		}
		enableRole(arr,flag);
	}
	
	
	
	/**
	 * 启用或者停用
	 */
	function enableRole(arr,flag) {
		var url = "../role/enableOrDisableRole.html?ids=" + arr.toString() + "&eod=" + flag;
		var success = function(data) {
			if(data.success = "success") {
				reloadTree();
			}else {
				if(flag == 'enable') {
					showErrorTip("启用角色失败");
				}
				if(flag == 'disable') {
					showErrorTip("停用角色失败");
				}
			}
		};
		Util.ajax.loadData(url,success);
	}
	
	

	
	
	/**
	 * 权限分配
	 */
	function onSetSystemPermissionClick() {
		$('#setSystemPermission').on('click',function() {
			var row = $('#'+gridId).datagrid('getSelections');
			if(row.length != 1) {
				showErrorTip("请选择一行");
				return;
			}
			dialog.openDialog({
					url:'../role/toPermission.html?id=' + row[0].id,
					title:'权限分配',
					id:'permission',
					width:'350px',
					height:'480px',
					reloadCallBack:function(){
					}
			});
		});
	}

	/**
	 * 显示停用角色
	 */
	function onShowDisabledRoleClick() {
		$('#showDisabledRole').on('click',function() {
			$("#status").val("Disable");
			reload();
		});
	}

	/**
	 * 显示启用角色
	 */
	function onShowEnableRoleClick() {
		$('#showEnableRole').on('click',function() {
			$("#status").val("Normal");
			reload();
		});
	}	

	
	/**
	 * 弹出添加、修改dialog
	 */
	function openDialog(title,param,id){
		dialog.openDialog({
			url:'../role/toAddRolePage.html?'+param,
			title:title,
			id:id,
			width:'500px',
			height:'350px',
			reloadCallBack:function(data){
				/*if("添加角色" == title){
					$.cookie("lastIndex", true);
				}*/
				if(data.id)
					lastDataId = data.id;
				reloadTree();
			}
		});
	}
	
	
	
	
	//树 选中事件
	function onTreeSelect(node) {
		$('#'+gridId).datagrid('clearSelections');
		//记录id
		lastTreeId = node.id;
		//重新加载grid
		reload();
	}
	
	
	//树加载完成事件
	function onTreeLoadSuccess(node, data) {
		var treeDataLength = data.length;
		if(treeDataLength > 0) {
			if(lastTreeId == "") {
				var roots=$('#tree').tree('getRoots');
				$('#'+treeId).tree('select',roots[0].target);
			}else {
				var treeNode = $('#'+treeId).tree('find',lastTreeId);
				if(treeNode != undefined) {
					$('#'+treeId).tree('select', treeNode.target);
				}
			}
		}
	}
	
	
	//初始化tree接口
	exports.treeLoad = function(inGridId,inGridUrl) {
		//请求连接
		var url = '../role/getRoleTree.html';
		return tree.init(treeId,{
			url:url,
			onSelect:onTreeSelect,
			onLoadSuccess:onTreeLoadSuccess
		});
	};
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			if(target.hasClass("updateRole")){
				var id = target.attr("id");
				id = id.substring(7, id.length);
				target.on('click',function(){updateRoleRowClick(id);});
			}
		});
	}
	
	//初始化grid接口
	exports.gridLoad = function() {
		//请求连接
		var url = '../role/getDataList.html';
		//列
		var columns = [[
		                {field:'id',checkbox:true},
		                {field:'roleName',halign:'center',align:'left',width:120,title:'名称'},
		                {field:'createBy',halign:'center',align:'left',width:120,title:'创建人',formatter:function(value,rowData,rowIndex) {
		          		  var user = datagrid.formatterUserName(value);
		        		  return user.realName;
		    		  }},
		                {field:'createDate',halign:'center',align:'center',width:140,title:'创建时间'},
		                {field:'status',halign:'center',align:'center',width:80,title:'当前状态',formatter:function(value,rowData,rowIndex) {
		                	return value.chineseName;
		                }},
		                {field:'des',halign:'center',align:'left',width:230,title:'备注'},
		                {field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateRole" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
			                }
		                }
		               ]];
		
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			queryParams:Util.ajax.serializable($("#listForm")),
			toolbar:'#toolbar',
			pagination:true,
			selectFirstRow:false,
			onLoadSuccess:function(data){
				//选中最后选择的记录
				if(lastDataId){
					$('#'+gridId).datagrid('selectRecord',lastDataId);
				}else{
					if(data.total>0)
						$('#'+gridId).datagrid('selectRow',0);
				}
				editColumnFn();
			},
			onBeforeLoad:function(){
			 	var node = $('#'+treeId).tree('getSelected');
			 	if(node){
			 		return true;
			 	}
			 	return false;
			 	
			}
		});

	};
	
	
	//初始化方法
	exports.init = function() {

		initButtonClick();
		//启用角色
		onRoleEnableClick();
		//禁用角色
		onRoleDisableClick();
		//搜索
		onSearchButtonClick();
		//设置权限
		onSetSystemPermissionClick();
		//显示禁用的角色
		onShowDisabledRoleClick();
		//显示启用的角色
		onShowEnableRoleClick();
		
	};
});