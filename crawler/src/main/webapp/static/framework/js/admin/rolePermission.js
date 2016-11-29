/**
 * role 角色JS
 */
define(function(require,exports,module){

	//easyui Tree树
	var tree  = require('tree');
	
	var treeId = 'tree';
	var roleId = null;
	
	function onConfirmClick(roleId) {
		$('#saveBtn').on('click',function() {
			var rows = $('#' + treeId).tree('getChecked');
			var arr = [];
			if(rows.length > 0) {
				for(index in rows) {
					var attr = rows[index].attributes;
					if(attr && attr.levelNum==999){
						arr.push(rows[index].id);
					}
				}
			}
			var url = "../role/saveRolePermission.html?ids=" + arr.toString() + "&roleId=" + roleId;
			var success = function(data) {
				if(data.success = "success") {
	 				art.dialog.close();
				}
			};
			Util.ajax.loadData(url,success);
		});
	}
	
	
	//列
	var columns = [[
	                {field:'id',checkbox:true},
	                {field:'permissionName',halign:'center',align:'left',width:130,title:'权限名称'},
	                {field:'permissionCode',halign:'center',align:'left',width:130,title:'权限Code'}
	                ]];
	
	
	//初始化grid接口
	treeLoad = function() {
		
/*		//初始化
		return datagrid.init(gridId,{
			url:'../role/getPermissionList.html',
			fit:true,
			columns:columns,
			idField:'id',
			pagination:false,
			checkOnSelect:true,
			selectOnCheck:true,
			singleSelect:false,
			onLoadSuccess:function(data) {
				if(ids != "" && ids != undefined) {
					var idArr = ids.split(",");
					for(var i = 0 ; i < idArr.length ; i++) {
						$('#'+gridId).datagrid('selectRecord',idArr[i]);
					}
				}
			}
		});*/

		return tree.init('tree',{
			url:'../role/getPermissionList.html?id='+roleId,
			onlyLeafCheck:true,
			checkbox:true
		});

	};
	
	
	//初始化方法
	exports.init = function(rid) {
		roleId=rid;
		//显示确认角色权限
		onConfirmClick(roleId);
		treeLoad();
		
	};
});