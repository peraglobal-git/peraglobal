/**
 * org 部门JS
 */
define(function(require,exports,module){
	//artDialog
	var dialog  = require('artDialogJs');
	
	//treegrid 表格
	var treegrid  = require('treegrid');
	
	//treegrid table Id
	var gridId = "grid";
	//上一次点击树的节点ID
	var lastDataId = "";
	/**
	 * 刷新表格数据
	 */
	function reload(){
		//$("#"+gridId).treegrid("reload");
		 $("#search_button").click();
	}
	
	function initButtonClick(){
		$("#toolbar .easyui-linkbutton").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(id.indexOf("add")!=-1){
				target.on('click',function(){addOrgMethod();});
			}else if(id.indexOf("update")!=-1){
				target.on('click',function(){updateOrgMethod();});
			}else if(id.indexOf("delete")!=-1){
				target.on('click',function(){delOrgMethod();});
			}
		});
	}
	
	/**
	 * 添加部门
	 */
	function addOrgMethod(){
    	var selectedTreeNode = $('#'+gridId).datagrid('getSelected');
    	if(selectedTreeNode != undefined) {
    		var dialogId = 'org';
    		var param = "pid=" + selectedTreeNode.id + "&ran=" + Math.random();
    		var dialogTitle = '添加部门';
    		openDialog(dialogTitle,param,dialogId);
    	}else {
    		showErrorTip("请选择一行");
    	}
	}
	
	
	/**
	 * 修改部门
	 */
	function updateOrgMethod(){
		var rows = $('#'+gridId).datagrid('getSelections');
		if(rows.length > 1) {
			showErrorTip("请选择一行");
			return;
		}
		var row = rows[0];
		if(row) {
    		var dialogId = 'org';
    		lastDataId=row.id;
    		var param = "id="+row.id+"&pid="+row.parentId+"&ran="+Math.random();
    		var dialogTitle = '修改部门';
    		openDialog(dialogTitle,param,dialogId);
		}else {
			showErrorTip("请选择一行");
		}
			
	}

	/**
	 * 修改部门
	 */
	function updateOrgRowFn(id, parentId){
		var dialogId = 'org';
		lastDataId=id;
		var param = "id="+lastDataId+"&pid="+parentId+"&ran="+Math.random();
		var dialogTitle = '修改部门';
		openDialog(dialogTitle,param,dialogId);
	}
	
	/**
	 * 删除部门
	 */
	function delOrgMethod() {
		var rows = $('#'+gridId).datagrid('getSelections');
		var row = rows[0];
		if(row.id=="1"){
			showErrorTip("根节点不允许删除");
			return;
		}
		submitDel('../org/delOrg.html',{
			rows:rows,
			callback:function(data){
				reload();
			}
		});
	}
	
	
	/**
	 * 弹出添加、修改dialog
	 */
	function openDialog(title,param,id){
		dialog.openDialog({
			url:'../org/toAddOrgPage.html?'+param,
			title:title,
			id:id,
			width:'500px',
			height:'350px',
			reloadCallBack:function(data){
				movelog=false;
				if(data.id)
					lastDataId = data.id;
				reload();
			}
		});
	}
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_layout .datagrid-body .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateOrg")){
				id = id.substring(7, id.length);
				var ids = id.split("_");
				target.on('click',function(){updateOrgRowFn(ids[0], ids[1]);});
			}
		});
	}

	
	function ontreegridSelect(row,data){
		var root = $('#'+gridId).treegrid('getRoot');
		if(row.id==root.id){
			//置灰按钮
        	$('#orgEnable').linkbutton('disable');
        	$('#orgDisable').linkbutton('disable');
        	$('#deleteorg').linkbutton('disable');
		}else{
			$('#orgEnable').linkbutton('enable');
        	$('#orgDisable').linkbutton('enable');
        	$('#deleteorg').linkbutton('enable');
		}
	}
	
	
	//treegrid 加载成功事件
	function onGridLoadSuccess(row,total) {
		//根节点
		var root = $('#'+gridId).treegrid('getRoot');
		var gridDataLength = total.total;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId).treegrid('select',lastDataId);
				
			}else {
				//默认选中根节点
				$('#'+gridId).treegrid('selectRow',root.id);
			}
		}
		editColumnFn();
	}
	
	  var param = {};
	  function treeLoad(){
		  treegrid = $('#'+gridId).treegrid({
	            url:'../org/getTreeDataList.html',        //数据表格数据来源,json格式的数据
	            method: 'post', 
	            fit:true,
	            //rownumbers: true,                //是否显示行号
	            idField:'id',                      //对应存储id的字段
	            treeField:'name',                //对应要形成树的显示名称字段
	            toolbar:'#toolbar',
	            queryParams:param,
	            columns:[[
	  	    			{field:'id',hidden:true},
				        {field:'name',halign:'center',align:'left',width:160,title:'名称'},
				        {field:'orgCode',halign:'center',align:'left',width:120,title:'部门编号'},
				        {field:'status',halign:'center',align:'center',width:80,title:'当前状态',formatter:function(value,rowData,rowIndex) {
				        	if(rowData.id=="1"){
				        		return "";
				        	}else{
				        		if(value)
					        		return value.chineseName;
					        	else
					        		return "";
				        	}
		                }},
				        {field:'chargeMan',halign:'center',align:'left',hidden:true,width:20,title:'管理者'},
				        {field:'createDate',halign:'center',align:'center',width:140,title:'创建时间'},
				        {field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'_'+row.parentId+'" class="easyui-linkbutton updateOrg" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
			                }
		                }
	  	            ]],
	  	          onSelect:ontreegridSelect,
	  	          onLoadSuccess:onGridLoadSuccess
	  	          
	        });
	    };
	     
	
	
	exports.treegridLoad = function(){
		
		treeLoad();
	
    };
	
	/**
	 * 搜索查询处理
	 */
	var onSearchHandler = function (opts) {
		$("#search").inputTextEvent({callback:function(val){
			//获取查询参数
			param = Util.ajax.serializable($("#listForm"));
			treeLoad();
		  }});
	};
	
	/**
	 * 启用按钮
	 */
	function onOrgEnableClick() {
		$('#orgEnable').on('click',function() {
			showQuestionTip({
				content:'确认启用选中的部门？',
				ok:function(){
					getRowsToEnOrDis('enable');
				}
			});
		});
	}
	

	
	/**
	 * 停用按钮
	 */
	function onOrgDisableClick() {
		$('#orgDisable').on('click',function() {
			showQuestionTip({
				content:'确认停用选中的部门？',
				ok:function(){
					getRowsToEnOrDis('disable');
				}
			});
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
			var row = $('#'+gridId).datagrid('getSelections');
			if(row != undefined) {
				arr.push(row.id);
			}else {
				showErrorTip("请选择至少一行数据");
			}
		}
		enableOrg(arr,flag);
	}
	
	/**
	 * 启用或者停用
	 */
	function enableOrg(arr,flag) {
		var url = "../org/enableOrDisableOrg.html?ids=" + arr.toString() + "&eod=" + flag;
		var success = function(data) {
			if(data.success = "success") {
				if(arr.length > 0)
					lastDataId = arr.toString();
				reload();
			}else {
				if(flag == 'enable') {
					showErrorTip("启用部门失败");
				}
				if(flag == 'disable') {
					showErrorTip("停用部门失败");
				}
			}
		};
		lastDataId = $('#'+gridId).datagrid('getSelected').id;
		var params = Math.random();
		Util.ajax.loadData(url,success,params,{});
	}
	
	//初始化方法
	exports.init = function() {
		//初始化按钮事件
		initButtonClick();
		//搜索框和按钮处理
		onSearchHandler();
		//启用角色
		onOrgEnableClick();
		//禁用角色
		onOrgDisableClick();
	};

});