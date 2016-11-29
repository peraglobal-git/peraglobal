/**
 * module 导航JS
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
	
	//记录选中的节点
	var lastDataId = "";
	//判断删除后是否返回当前页
	var loadIsCreentPage=false;
	
	var deep = false;
	
	/**
	 * 刷新表格数据
	 */
	function reload(){
//		$("#"+gridId).datagrid("reload");
		var node = $('#'+treeId).tree('getSelected');
	    if(node)
		   $("#parentId").val(node.id);
		$("#search_button").click();
	}
	
	
	/**
	 * 刷新树数据
	 */
	function reloadTree() {
		$("#"+treeId).tree('reload');
	}
	
	
	/**
	 * 添加导航
	 */
	function addNavClick(){
    	var selectedTreeNode = $('#'+treeId).tree('getSelected');
    	if(selectedTreeNode != undefined) {
    		var dialogId = 'nav';
    		var param = "parentId=" + selectedTreeNode.id + "&levelNum="+ selectedTreeNode.attributes.levelNum +"&ran=" + Math.random();
    		var dialogTitle = '添加导航信息';
    		openDialog(dialogTitle,param,dialogId);
    	}else {
    		showErrorTip("请选择导航节点");
    	}
	}
	
	
	/**
	 * 修改导航
	 */
	function updateNavClick(){
		var selectedTreeNode = $('#'+treeId).tree('getSelected');
		var rows = $('#'+gridId).datagrid('getSelections');
		
		if(rows.length > 1) {
			showErrorTip("请选择一行");
			return;
		}
		
		var row = rows[0];
		if(row != undefined) {
    		var dialogId = 'nav';
    		lastDataId=row.id;
    		$.cookie("lastDataId", row.id);
    		//id:导航模块module的id,levelNum是模块层级
    		var param = "id="+row.id + "&parentId=" + selectedTreeNode.id + "&levelNum="+ selectedTreeNode.attributes.levelNum + "&ran="+Math.random();
    		var dialogTitle = '修改导航信息';
    		openDialog(dialogTitle,param,dialogId);
		}else {
			showErrorTip("请选择一行");
		}
		
	}
	
	/**
	 * 修改导航
	 */
	function updateNavRowClick(id){
		loadIsCreentPage=true;
		var selectedTreeNode = $('#'+treeId).tree('getSelected');
		var dialogId = 'nav';
		lastDataId=id;
		$.cookie("lastDataId", id);
		//id:导航模块module的id,levelNum是模块层级
		var param = "id="+id + "&parentId=" + selectedTreeNode.id + "&levelNum="+ selectedTreeNode.attributes.levelNum + "&ran="+Math.random();
		var dialogTitle = '修改导航信息';
		openDialog(dialogTitle,param,dialogId);
		
	}

	
	/**
	 * 删除模块
	 */
	function delNavClick() {
		loadIsCreentPage=true;
		var rows = $('#'+gridId).datagrid('getSelections');
		if(rows){
			if(1==rows.length){
				if(rows[0].isCanDelete && rows[0].isCanDelete.name == 'No'){
					showErrorTip("有初始化菜单不能被删除！");
					return;
				}
			}
			submitDel('../module/delModule.html',{
				rows:rows,
				callback:function(data){
					window.top.location.reload();
				}
			});
		}
	}
	
	/**
	 * 初始化按钮事件
	 */
	function initButtonClick(){
		$("#toolbar .easyui-linkbutton").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(id.indexOf("add")!=-1){
				target.on('click',function(){addNavClick();});
			}else if(id.indexOf("update")!=-1){
				target.on('click',function(){updateNavClick();});
			}else if(id.indexOf("delete")!=-1){
				target.on('click',function(){delNavClick();});
			}
		});
	}

	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		 $("#search").inputTextEvent({callback:function(val){
			   if(loadIsCreentPage){
				   $('#grid').datagrid("reload",Util.ajax.serializable($("#listForm")));
				   loadIsCreentPage=false;
			   }else{
				   $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
			   }
			   
		  }});
	}
	
	
	
	/**
	 * 上移按钮
	 */
	function onNavUpClick() {
		$('#navUp').on('click',function() {
			var row = $('#'+gridId).datagrid('getSelected');
			if(row != undefined) {
				var index = $('#'+gridId).datagrid('getRowIndex',row);
				if(index < 1) {
					showErrorTip("已经是最上方");
				}else {
					var rows = $('#'+gridId).datagrid('getRows');
					var targetRow = rows[index-1];
					changeIndex(row.id,targetRow.id,'up');
				}
			}else {
				showErrorTip("请选择一行");
			}
		});
	}

	
	/**
	 * 下移按钮
	 */
	function onNavDownClick() {
		$('#navDown').on('click',function() {
			var row = $('#'+gridId).datagrid('getSelected');
			if(row != undefined) {
				var index = $('#'+gridId).datagrid('getRowIndex',row);
				var rows = $('#'+gridId).datagrid('getRows');
				if((index+1) == rows.length) {
					showErrorTip("已经是最下方");
				}else {
					var targetRow = rows[index+1];
					changeIndex(row.id,targetRow.id,'down');
				}
			}else {
				showErrorTip("请选择一行");
			}
		});
	}
	
	/**
	 * 上移按钮
	 */
	function onNavRowUpClick(id) {
		var index = $('#'+gridId).datagrid('getRowIndex', id);
		if(index < 1) {
			showErrorTip("已经是最上方");
		}else {
			var rows = $('#'+gridId).datagrid('getRows');
			var targetRow = rows[index-1];
			changeIndex(id,targetRow.id,'up');
		}
	}

	
	/**
	 * 下移按钮
	 */
	function onNavRowDownClick(id) {
		var index = $('#'+gridId).datagrid('getRowIndex', id);
		var rows = $('#'+gridId).datagrid('getRows');
		if((index+1) == rows.length) {
			showErrorTip("已经是最下方");
		}else {
			var targetRow = rows[index+1];
			changeIndex(id,targetRow.id,'down');
		}
	}
	
	/**
	 * 启用
	 */
	function enableNavClick(id){
		updateStatusModule(id,"enable");
	}
	/**
	 * 停用
	 */
	function disableNavClick(id){
		updateStatusModule(id,"disable");
	}
	/**
	 * 启用停用
	 */
	function updateStatusModule(id,flag){
		var url = "../module/updateStatusModule.html?id=" + id + "&eod=" + flag;
		var success = function(data) {
			if(data.success = "success") {
				lastDataId = id;
				$.cookie("lastDataId", id);
				window.top.location.reload();
			}else {
				showErrorTip("操作失败或状态已更新");
			}
		};
		var params = Math.random();
		Util.ajax.loadData(url,success,params,{});
	}
	
	/**
	 * 更换排序
	 */
	function changeIndex(sourceId,targetId,flag) {
		var url = "../module/changeSortNum.html?sourceId=" + sourceId + "&targetId=" + targetId;
		var success = function(data) {
			if(data.success = "success") {
				lastDataId = sourceId;
				$.cookie("lastDataId", sourceId);
				//reloadTree();
				window.top.location.reload();
			}else {
				if(flag == 'up') {
					showErrorTip("上移失败");
				}
				if(flag == 'down') {
					showErrorTip("下移失败");
				}
			}
		};
		var params = Math.random();
		Util.ajax.loadData(url,success,params,{});
	}
	

	
	
	/**
	 * 弹出添加、修改dialog
	 */
	function openDialog(title,param,id){
		dialog.openDialog({
			url:'../module/toAddModulePage.html?'+param,
			title:title,
			id:id,
			width:'500px',
			height:'380px',
			reloadCallBack:function(data){
				if("添加导航信息" == title){
					$.cookie("lastIndex", true);
				}
				if(data.id){
					lastDataId = data.id;
					$.cookie("lastDataId", lastDataId);
				}
				//reloadTree();
				window.top.location.reload();
			}
		});
	}
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateNav")){//修改
				id = id.substring(7, id.length);
				target.on('click',function(){updateNavRowClick(id);});
			}else if(target.hasClass("enableNav")){//启用
				id = id.substring(7, id.length);
				target.on('click',function(){enableNavClick(id);});
			}else if(target.hasClass("disableNav")){//停用
				id = id.substring(7, id.length);
				target.on('click',function(){disableNavClick(id);});
			}else if(target.hasClass("upNav")){//上移
				id = id.substring(3, id.length);
				target.on('click',function(){onNavRowUpClick(id);});
			}else if(target.hasClass("downNav")){//下移
				id = id.substring(5, id.length);
				target.on('click',function(){onNavRowDownClick(id);});
			}
		});
	}
	
	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		$('#'+gridId).datagrid('clearSelections');
		var gridDataLength = data.rows.length;
		if(gridDataLength > 0) {
			if($.cookie("lastDataId"))
				lastDataId = $.cookie("lastDataId");
			if(lastDataId != "") {
				$('#'+gridId).datagrid('selectRecord',lastDataId);
			}else {
				$('#'+gridId).datagrid('selectRow',0);
			}
		}
		editColumnFn();
	}

	
	//列
	var columns = [[
	                {field:'id',checkbox:true},
	                {field:'moduleName',halign:'center',align:'left',width:120,title:'导航名称'},
	                {field:'path',halign:'center',align:'left',width:300,title:'访问地址'},
	                {field:'parentId',halign:'center',align:'left',width:140,title:'所属模块',hidden:true},
	                {field:'sortNum',hidden:true},
	        //        {field:'isCanDelete',halign:'center',align:'center',width:120,title:'是否可删除',formatter:datagrid.formatterDefault},
	                {field:'status',halign:'center',align:'center',width:80,title:'当前状态',formatter:function(value,rowData,rowIndex) {
	                	if(value)
			        		return value.chineseName;
			        	else
			        		return "";
	                }},
	                {field:'operate',halign:'center',align:'center',width:150,title:'操作',
		                formatter:function(val,row,index){
		                	var rows = $('#'+gridId).datagrid('getRows');
		                	var html = '<a id="update_'+row.id+'" class="easyui-linkbutton updateNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>&nbsp;';
		                	if(rows){
		                		if("Normal"== row.status.name){
		                			html+= '&nbsp;<a id="update_'+row.id+'" class="easyui-linkbutton disableNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">停用</a>&nbsp; ';
		                		}else{
		                			html+= '&nbsp;<a id="update_'+row.id+'" class="easyui-linkbutton enableNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">启用</a>&nbsp; ';
		                		}
	                			if(rows.length == 1){
	    		                	return html;
	                			}else{
	                				if(0==index){
	                					html+='<a id="up_'+row.id+'" hidden class="easyui-linkbutton upNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">上移</a>&nbsp; ';
	                					html+='<a id="down_'+row.id+'" class="easyui-linkbutton downNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">下移</a>&nbsp; ';
	                					return html;
	                				}else if(index == rows.length-1){
	                					html+='<a id="down_'+row.id+'" hidden class="easyui-linkbutton downNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">下移</a>&nbsp; ';
	                					html+='<a id="up_'+row.id+'" class="easyui-linkbutton upNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">上移</a>&nbsp; ';
	                					return html;
	                				}
	                				
	                			}
	                		}
		                	html+='<a id="up_'+row.id+'" class="easyui-linkbutton upNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">上移</a>&nbsp; ';
            				html+='<a id="down_'+row.id+'" class="easyui-linkbutton downNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">下移</a>&nbsp; ';
            				return html;
		                }
	                }
	              ]];

	//请求连接
	var url = '../module/getModuleList.html';
	
	//请求参数
	var param = {};
	
	
	//初始化grid接口
	exports.gridLoad = function() {
		
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			queryParams:param,
			toolbar:'#toolbar',
			pagination:true,
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess,
			onBeforeLoad:function(){
			 	var node = $('#'+treeId).tree('getSelected');
			 	if(node){
			 		return true;
			 	}
			 	return false;
			}
		});

	};
	
	
	
	//树 选中事件
	function onTreeSelect(node) {
		//$('#'+gridId).datagrid('clearSelections');
		//记录id
		lastTreeId = node.id;
		$.cookie("lastTreeId", lastTreeId);
		

		//重新加载grid
		reload();
		
		var level = 2;
		if(deep)level = 3;
		
		if(node.attributes.levelNum >= level){//二级菜单置灰按钮
			$("#toolbar .easyui-linkbutton").linkbutton("disable");
		}else{
			$("#toolbar .easyui-linkbutton").linkbutton("enable");			
		}
	}
	
	
	//树加载完成事件
	function onTreeLoadSuccess(node, data) {
		var treeDataLength = data.length;
		if(treeDataLength > 0) {
			if($.cookie("lastTreeId"))
				lastTreeId = $.cookie("lastTreeId");
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
	exports.treeLoad = function() {
		//请求连接
		var url = '../module/getModuleTreeList.html';
		
		return tree.init(treeId,{
			url:url,
			onSelect:onTreeSelect,
			onLoadSuccess:onTreeLoadSuccess
		});
	};
	
	
	
	
	//初始化方法
	exports.init = function(d) {
		deep = d;
		//初始化增 删  改事件
		initButtonClick();
		//上移
		onNavUpClick();
		//下移
		onNavDownClick();
		//搜索键
		onSearchButtonClick();
		
	};
	
});