/**
 * 用户调换部门
 */
define(function(require,exports,module){

	//easyui Tree树
	var tree  = require('tree');
	var userIds = null;
	var treeId = 'tree';
	
	/**
	 * 用户移动到新部门
	 */
	function onConfirmClick(userIds) {
		$('#saveBtn').on('click',function() {
			var orgrow = $('#' + treeId).tree('getSelected');
			if(orgrow) {
				var url = "../user/updateUserOrg.html?ids=" +userIds + "&orgId=" + orgrow.id;
				var success = function(data) {
					if(data.success = "success") {
						art.dialog.data('orgId', orgrow.id); 
		 				art.dialog.close();
					}
				};
				Util.ajax.loadData(url,success);
			}
		});
	}
	
	
	//列
	var columns = [[
	                {field:'id',hidden:true},
	                {field:'name',halign:'center',align:'left',width:130,title:'部门名称'},
	                {field:'orgCode',halign:'center',align:'left',width:130,title:'部门编号'}
	                ]];
	
	
	//初始化grid接口
	treeLoad = function(status) {
		return tree.init('tree',{
			url:'../org/getOrgTree.html?status='+status
		//	onlyLeafCheck:true,
			//checkbox:true
		});
	};
	
	//初始化方法
	exports.init = function(ids) {
		var userIds = ids;
		//显示确认角色权限
		onConfirmClick(userIds);
		treeLoad('Normal');
	};
});