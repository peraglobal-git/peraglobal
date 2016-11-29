/**
 * user 用户JS
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
	
	//树 选中事件
	function onTreeSelect(node) {
		//记录id
		lastTreeId = node.id;
		//重新加载grid
		reload();
		//$("#parentId").val(node.id);
		//部门的启用状态
		var nodeStatus = node.attributes.status;
		//只查询字典项
        if(nodeStatus!=null && nodeStatus == "Normal"){
    		$('#addUser').linkbutton('enable');
        }else{
        	//置灰按钮
        	$('#addUser').linkbutton('disable');
        }
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
		var url = '../org/getOrgTree.html';
		
		return tree.init(treeId,{
			url:url,
			onSelect:onTreeSelect,
			onLoadSuccess:onTreeLoadSuccess
		});
	};
	/**
	 * 初始化按钮事件
	 */
	function initButtonClick(){
		$("#toolbar .easyui-linkbutton").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(id.indexOf("add")!=-1){
				target.on('click',function(){addUserMethod();});
			}else if(id.indexOf("update")!=-1){
				target.on('click',function(){updateUserMethod();});
			}else if(id.indexOf("delete")!=-1){
				target.on('click',function(){delUserMethod();});
			}
		});
	}
	
	/**
	 * 添加用户
	 */
	function addUserMethod(){
		//添加方法
    	var selectedTreeNode = $('#'+treeId).tree('getSelected');
    	if(selectedTreeNode != undefined) {
    		var dialogId = 'user';
    		var param = "orgId=" + selectedTreeNode.id + "&orgName=" + encodeURI(encodeURI(selectedTreeNode.text)) + "&ran=" + Math.random();
    		var dialogTitle = '添加用户';
    		openDialog(dialogTitle,param,dialogId);
    	}else {
    		showErrorTip("请选择一行");
    	}
}
	
	/**
	 * 修改部门
	 */
	function updateUserMethod(){
		//修改方法
		var selectedTreeNode = $('#'+treeId).tree('getSelected');
		var rows = $('#'+gridId).datagrid('getSelections');
		
		if(rows.length > 1) {
			showErrorTip("请选择一行");
			return;
		}
		
		var row = rows[0];
		if(row != undefined) {
    		var dialogId = 'user';
    		lastDataId=row.id;
    		var param = "id="+row.id + "&orgId=" + selectedTreeNode.id + "&orgName=" + encodeURI(encodeURI(selectedTreeNode.text))  + "&ran="+Math.random();
    		var dialogTitle = '修改用户';
    		openDialog(dialogTitle,param,dialogId);
		}else {
			showErrorTip("请选择一行");
		}
			
	}
	
	/**
	 * 修改用户
	 */
	function updateUserRowFn(id){
		loadIsCreentPage=true;
		//修改方法
		var selectedTreeNode = $('#'+treeId).tree('getSelected');
		var dialogId = 'user';
		lastDataId=id;
		var param = "id="+id + "&orgId=" + selectedTreeNode.id + "&orgName=" + encodeURI(encodeURI(selectedTreeNode.text))  + "&ran="+Math.random();
		var dialogTitle = '修改用户';
		openDialog(dialogTitle,param,dialogId);
	}

	/**
	 * 删除用户
	 */
	function delUserMethod() {
		loadIsCreentPage=true;
		var rows = $('#'+gridId).datagrid('getSelections');
		submitDel('../user/delUser.html',{
			rows:rows,
			callback:function(data){
				reload();
			}
		});
	}
	
	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		
		 $("#search").inputTextEvent({callback:function(val){
				var node = $('#'+treeId).tree('getSelected');
		 		$("#orgId").val(node.id);
			 if(loadIsCreentPage){
				 $('#grid').datagrid("reload",Util.ajax.serializable($("#listForm")));
				 loadIsCreentPage=false;
			 }else{
				 $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
			 }
			 
//			   var node= $('#'+treeId).tree('getSelected');
//			   if(node) {
//			    $("#orgId").val(node.id);
//			    $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
//			    loadIsCreentPage=false;
//			   }
		  }});
	}
	
	/**
	 * 启用按钮
	 */
	function onUserEnableClick() {
		$('#enableUser').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			if(rows.length > 0) {
				showQuestionTip({
					content:'确认启用选中的用户？',
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
	function onUserDisableClick() {
		$('#disableUser').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			if(rows.length > 0) {
				showQuestionTip({
					content:'确认停用选中的用户？',
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
			var row = $('#'+gridId).datagrid('getSelections');
			if(row != undefined) {
				arr.push(row.id);
			}else {
				showErrorTip("请选择至少一行数据");
			}
		}
		enableRole(arr,flag);
	}
	
	/**
	 * 启用或者停用
	 */
	function enableRole(arr,flag) {
		var url = "../user/enableOrDisableRole.html?ids=" + arr.toString() + "&eod=" + flag;
		var success = function(data) {
			if(data.success = "success") {
				if(arr.toString()){
					lastDataId = arr[0].toString();
				}
				reload();
			}else {
				if(flag == 'enable') {
					showErrorTip("启用用户失败");
				}
				if(flag == 'disable') {
					showErrorTip("停用用户失败");
				}
			}
		};
		var params = Math.random();
		Util.ajax.loadData(url,success,params,{});
	}
	
	/**
	 * 重置密码按钮
	 */
	function onResetPasswordClick() {
		$('#resetPassword').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			var arr = [];
			if(rows.length < 1 || rows.length > 1) {
				showErrorTip("请选择一行");
				return;
			}else{
				dialog.openDialog({
					url:'../user/toReSetPassword.html?id=' + rows[0].id,
					title:'重置密码',
					id:'reset',
					width:'465px',
					height:'240px',
					reloadCallBack:function(){
						
					}
				});
			}
		});
	}

	/**
	 * 设置密集
	 */
	function onSetDegreeClick() {
		$('#setDegree').on('click',function() {
			alert("暂时未做");
		});
	}	

	
	/**
	 * 角色分配按钮
	 */
	function onSetUserRoleClick() {
		$('#setUserRole').on('click',function() {
			var row = $('#'+gridId).datagrid('getSelected');
			if(row != undefined) {
				dialog.openDialog({
					url:'../user/toSetUserRole.html?id=' + row.id,
					title:'角色分配',
					id:'setRole',
					width:'350px',
					height:'480px',
					reloadCallBack:function(){
						reload();
					}
				});
			}
		});
	}	
	
	/**
	 * 角色分配按钮
	 */
	function onSetUserRoleRowClick(id) {
		dialog.openDialog({
			url:'../user/toSetUserRole.html?id=' + id,
			title:'角色分配',
			id:'setRole',
			width:'350px',
			height:'480px',
			reloadCallBack:function(){
				if(id)
					lastDataId = id;
				reload();
			}
		});
	}	
	
   function onImportUserClick(){
	   $("#importUserList").on("click",function(){
			dialog.openDialog({
				url:'../user/toImportUserPage.html',
				title:'用户导入',
				width:'450px',
				height:'180px',
				reloadCallBack:function(){
					reload();
					showSuccessTip('用户导入成功!');
				}
			});
			
	    });
   }
   
   function onSendEmailClick(){
	   $("#sendMail").on("click", function ()
	   {
		   var grid = $('#'+gridId);
		   var idArr = [];
		   var rowArr = grid.datagrid('getSelections');
		   if (rowArr.length > 0)
		   {
			   for (var index in rowArr)
			   {
				   idArr.push(rowArr[index].id);
			   }
		   } else
		   {
			   var row = grid.datagrid('getSelected');
			   if (row != undefined)
				   idArr.push(row.id);
		   }
		   var ids = idArr.toString();
		   var dialogId = 'sendMailDlg';
		   var param = "ran=" + Math.random() + "&userId=" + ids + "&sendTo=user";
		   var dialogTitle = '发送邮件';
		   openSendDialog(dialogTitle, param, dialogId);
	   });
   }
   
    /**
     * 弹出发送窗口
     */
    function openSendDialog(title, param, id)
    {
        dialog.openDialog({
            url: '../mail/toSendPage.html?' + param,
            title: title,
            id: id,
            width: '480px',
            height: '350px',
            reloadCallBack: function ()
            {
            	reload();
            }
        });
    }
	
	
	/**
	 * 弹出添加、修改dialog
	 */
	function openDialog(title,param,id){
		dialog.openDialog({
			url:'../user/toAddUserPage.html?'+param,
			title:title,
			id:id,
			width:'766px',
			height:'350px',
			reloadCallBack:function(data){
				if(data.id)
					lastDataId = data.id;
				reload();
			}
		});
	}
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateUser")){
				id = id.substring(7, id.length);
				target.on('click',function(){updateUserRowFn(id);});
			}else if(target.hasClass("setUserRole")){
				id = id.substring(8, id.length);
				target.on('click',function(){onSetUserRoleRowClick(id);});
			}
		});
	}

	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		var gridDataLength = data.total;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId).datagrid('selectRecord',lastDataId);
			}else {
				$('#'+gridId).datagrid('selectRow',0);
			}
		}
		editColumnFn();
	}
	
	//初始化grid接口
	exports.gridLoad = function() {
		//请求连接
		var url = '../user/getUserList.html';
		//列
		var columns = [[
		    			{field:'id',checkbox:true},
		                {field:'realName',halign:'center',align:'left',width:120,title:'姓名'
		                	,formatter:function(value,rowData,rowIndex) {
		                		var islock = rowData.isLock;
		                		if(islock!=null){
		                			var islockval = islock.name;
		                			if("Yes"==islockval){
		                				//错位放置-IE6
		                				var font = '<font style="display:inline-block;height:18px;">'+value+'</font>'
		                				var imgYes = '<img src="../skins/default/images/lock.png" style="float: right;vertical-align: middle;height:18px;"/>';
		                				return imgYes+font;
		                			}
		                		}
		                		return value;
			                }},
		                {field:'username',halign:'center',align:'left',width:120,title:'账号'},
		                {field:'orgName',halign:'center',align:'left',width:150,title:'所属部门'},
		                {field:'orgId',hidden:true},
		                {field:'createDate',hidden:true,sortable:true},
		                {field:'roleName',halign:'center',align:'left',width:220,title:'角色名称'},
		                {field:'status',halign:'center',align:'center',width:80,title:'状态',formatter:datagrid.formatterDefault},
		                {field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateUser" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>&nbsp; <a id="roleFor_'+row.id+'" class="easyui-linkbutton setUserRole" iconCls="icon-edit" plain="true" href="javascript:void(0)">角色分配</a>'; 
			                }
		                }
		            ]];
		
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			toolbar:'#toolbar',
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
	
	/**
	 * 用户移动到部门的点击事件
	 */
	function onMoveUserClick() {
		$('#moveUser').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			var arr = [];
			if(rows.length > 0) {
				for(index in rows) {
					arr.push(rows[index].id);
				}
			}else {
				showErrorTip("请选择至少一行数据");
				return;
			}
			
			dialog.openDialog({
					url:'../user/toMoveUserOrgList.html?ids='+ arr.toString(),
					title:'移动到',
					id:'moveUser',
					width:'350px',
					height:'480px',
					reloadCallBack:function(){
					},
					close:function(){
						var bValue = art.dialog.data('orgId'); // 读取页面返回的数据  
						if (bValue!=null && bValue!=undefined){ 
							var treeNode = $('#'+treeId).tree('find',bValue);
							if(treeNode != undefined) {
								$('#'+treeId).tree('select', treeNode.target);
							}
						}  
					}
			});
		});
	}
	
	
	
	function onUnlockUserClick() {
		$('#unlockUser').on('click',function() {
			var rows = $('#'+gridId).datagrid('getSelections');
			if(rows.length > 0) {
				showQuestionTip({
					content:'确认解除用户锁定？',
					ok:function(){
						unlockUser();
					}
				});
			}else{
				showErrorTip("请选择至少一行数据");
			}
			
		});
	}
	
	function unlockUser() {
		var rows = $('#'+gridId).datagrid('getSelections');
		var arr = [];
		if(rows.length > 0) {
			for(index in rows) {
				arr.push(rows[index].id);
			}
		}else {
			showErrorTip("请选择至少一行数据");
		}
		var url = "../user/unlockUser.html?ids=" + arr.toString();
		var success = function(data) {
			if(data.success = "success") {
				if(arr.toString()){
					lastDataId = arr[0].toString();
				}
				reload();
			}else {
				showErrorTip("用户解锁失败");
			}
		};
		var params = Math.random();
		Util.ajax.loadData(url,success,params,{});
	}
	
	
	//初始化方法
	exports.init = function() {
		initButtonClick();
		//搜索按钮点击事件
		onSearchButtonClick();
		//用户启用
		onUserEnableClick();
		//用户停用
		onUserDisableClick();
		//用户重置密码
		onResetPasswordClick();
		//用户设置密集
		onSetDegreeClick();
		//用户设置角色
		onSetUserRoleClick();
		
		onImportUserClick();
        //发送邮件
        onSendEmailClick();
        //将用户移动到
        onMoveUserClick();
        //解除用户锁定
        onUnlockUserClick();
	};
	
	
});