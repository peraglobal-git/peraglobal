/**
 * 用户授权 JS
 */
define(function(require,exports,module){
	
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//grid ID
	var gridId = 'grid';
	var userId = null;
	var checkSubmitFlg = false;
	
	function onConfirmClick() {
		if(checkSubmitFlg)
		{ 
			return false;
		}else{
			checkSubmitFlg = true; 
		}
		var rows = $('#' + gridId).datagrid('getChecked');
		var cou =0;
		var arr = [];
		if(rows.length > 0) {
			for(index in rows) {
				if(rows[index].id=='10004001' ||rows[index].id=='10004002' || rows[index].id=='10004003' ){
					cou++;
				}
				arr.push(rows[index].id);
			}
		}
		if(cou>1){
			showErrorTip("同一用户不允许设置多个三员角色.");
			return ;
		}
		
		var url = "../user/saveUserRole.html?ids=" + arr.toString() + "&userId=" + userId;
		var success = function(data) {
			if(data.success = "success") {
 				$.dialog.data('method')({id:userId});
 				art.dialog.close();
			}
		};
		Util.ajax.loadData(url,success);
	}
	
	
	//列
	var columns = [[
	                {field:'id',checkbox:true},
	                {field:'roleName',halign:'center',align:'left',width:130,title:'角色名称'},
	                {field:'roleType',halign:'center',align:'left',width:130,title:'类型'}
	                ]];
	
	
	//初始化grid接口
	exports.gridLoad = function(ids) {
		
		//初始化
		return datagrid.init(gridId,{
			url:'../role/getRoleMapList.html',
			fit:true,
			columns:columns,
			idField:'id',
			pagination:false,
			selectFirstRow:false,
			onLoadSuccess:function(data) {
				if(ids != "" && ids != undefined) {
					var idArr = ids.split(",");
					for(var i = 0 ; i < idArr.length ; i++) {
						$('#'+gridId).datagrid('selectRecord',idArr[i]);
					}
				}
			}
		});

	};
	
	
	//初始化方法
	exports.init = function(roleId) {
		userId = roleId;
		msgForm = $("#form");
		msgForm.validate({
		   submitHandler:function(){
			   onConfirmClick();
		   }
		});
		$("#form").on("keydown",function(event){
			if(event.keyCode==13){
				//$("#form").submit();
				onConfirmClick();
			}
		});
				
	};
});