/**
 * permission  权限JS
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
	var lastTreeId = "";
	
	
	/**
	 * 刷新表格数据
	 */
	function reload(){
		$("#"+gridId).datagrid("reload");
	}
	
	
	/**
	 * 刷新树数据
	 */
	function reloadTree() {
		$("#"+treeId).tree('reload');
	}

	/**
	 * 提交删除
	 */
	function submitDel(rows) {
		var flag = false;
    	showQuestionTip({
			content:'确认删除选中的数据？',
			ok:function(){
				 flag = true;
			}
		});
		if (flag){
   	    	var idArr = [];
   	    	if(rows.length > 0) {
   	    		for(var index in rows) {
   	    			idArr.push(rows[index].id);
   	    		}
   	    	}else {
   	    		idArr.push(rows.id);
   	    	}
   	    	var ids = idArr.toString();
   	    	
   	    	
			 Util.ajax.loadData("../permission/delPermission.html", 
				function(data){
					if(data.success=="false"){
						showErrorTip(data.msg);
						return;
					}else{
						reloadTree();
					}
				}, {id:ids,ran:Math.random()}
			 );
   	    	
/*			$.getJSON("../permission/delPermission.html",{ id: ids,ran:Math.random() } , function(data){
				if(data.success=="false"){
					showErrorTip(data.msg);
					return;
				}else{
					reloadTree();
				}
			});*/
   	    }
	}
	
	
	/**
	 * 弹出添加、修改dialog
	 */
	function openDialog(title,param,id){
		
		
		dialog.openDialog({
			url:'../permission/toAddPermissionPage.html?'+param,
			title:title,
			id:id,
			width:'500px',
			height:'350px',
			reloadCallBack:function(){
				reload();
			}
		});
	}
	
	
	/**
	 * 修改权限
	 */
	function updatePermissionRowFn(id){
		var dialogId = 'permission';
		var param = "id="+id+"&moduleId="+lastTreeId+"&ran="+Math.random();
		var dialogTitle = '修改权限';
		openDialog(dialogTitle,param,dialogId);
	}
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updatePermission")){
				id = id.substring(7, id.length);
				target.on('click',function(){updatePermissionRowFn(id);});
			}
		});
	}
	
	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		//$("#search").val("输入权限名称");
		var gridDataLength = data.total;
		if(gridDataLength > 0) {
			$('#'+gridId).datagrid('selectRow',0);
		}
		editColumnFn();
	}
	
	
	//初始化grid接口
	exports.gridLoad = function() {
		//请求连接
		var url = '';
		
		//请求参数
		var param = {};
		
		//列
		var columns = [[
						{field:'id',checkbox:true},
				        {field:'permissionName',halign:'center',align:'left',width:120,title:'权限名称'},
				        {field:'permissionCode',halign:'center',align:'left',width:120,title:'权限Code'},
				        {field:'permissionModuleName',halign:'center',align:'center',width:120,title:'权限所属模块'},
				        {field:'createDate',halign:'center',align:'center',width:140,title:'创建时间'},
				        {field:'moduleId',hidden:true},
				        {field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updatePermission" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
			                }
		                }
				    ]];
		
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			queryParams:param,
			idField:'',
			toolbar:'#toolbar',
			pagination:true,
			checkOnSelect:false,
			selectOnCheck:false,
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess
		});

	};
	
	
	//初始化方法
	exports.init = function() {

		
		
		/**
		 * 添加权限
		 */
	    $("#addPermission").on("click",function(){
	    	var selectedTreeNode = $('#'+treeId).tree('getSelected');
	    	var treeNodeId = selectedTreeNode.id;
	    	if(treeNodeId == 0) {
	    		showErrorTip("系统节点不能添加权限");
	    		return false;
	    	}
	    	
	    	if(selectedTreeNode != undefined) {
	    		var dialogId = 'permission';
	    		var param = "moduleId=" + treeNodeId + "&ran=" + Math.random();
	    		var dialogTitle = '添加权限';
	    		openDialog(dialogTitle,param,dialogId);
	    	}else {
	    		showErrorTip("请选择一行");
	    	}
	    });
		
		
		/**
		 * 修改权限
		 */
		$("#updatePermission").on("click",function(){
			var rows = $('#'+gridId).datagrid('getSelections');
			
			if(rows.length > 1) {
				showErrorTip("请选择一行");
				return;
			}
			
			var row = rows[0];
			if(row != undefined) {
	    		var dialogId = 'permission';
	    		var param = "id="+row.id+"&moduleId="+lastTreeId+"&ran="+Math.random();
	    		var dialogTitle = '修改权限';
	    		openDialog(dialogTitle,param,dialogId);
			}else {
				showErrorTip("请选择一行");
			}
		});

		
		/**
		 * 删除权限
		 */
		$("#deletePermission").on("click",function(){
			var rows = $('#'+gridId).datagrid('getChecked');
			if(rows.length > 0) {
				submitDel(rows);
			}else {
				var row = $('#'+gridId).datagrid('getSelected');
				if(row != undefined) {
					submitDel(row);
				}
			}
		});
		

		/**
		 * 输入结束后
		 */
		$("#search").on("keyup",function(e){ 
			var ev = document.all ? window.event : e;
			if(ev.keyCode==13&&$("#search").is(":focus")) {
				$("#search_button").trigger("click");
			}
		});
		
		
		/**
		 * 搜索框聚焦事件
		 */
		$("#search").on("focus",function() {
			var keytext = $("#search").val();
			if(keytext=="输入权限名称"){
				$("#search").val("");    		
			}
		});

		
		/**
		 * 搜索框离焦事件
		 */
		$("#search").on("blur",function() {
			var keytext = $("#search").val();
			if(keytext==""){
				$("#search").val("输入权限名称");
			}
		});
		
		
		/**
		 * 搜索按钮点击
		 */
		$("#search_button").on("click",function() {
			var keytext = $.trim($("#search").val());
			if(keytext=="输入权限名称"){
				keytext="";
				return;
			}

			$('#'+gridId).datagrid('load', Util.ajax.serializable($("#listForm")));
		});
		
	};

});