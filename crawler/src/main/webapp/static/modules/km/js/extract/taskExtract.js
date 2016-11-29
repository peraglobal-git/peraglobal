/**
 * 字典js
 */
define(function(require,exports,module){
	var pdpDialog  = require('artDialogJs');
	
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
	
	//链接
	var gridUrl = '';
	
	//记录选中的节点
	var lastDataId = "";

	//判断记录的节点是否置空
	var movelog=true;
	
	//是否显示字典值
	var showValue = false;
	exports.gridLoad = function(id){
		//请求连接
		var url = '../taskJob/getTaskJobListById.html';
		//请求参数
		var param = {'id':id};
		//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		               	{field:'jobName',halign:'center',align:'left',width:120,title:'任务名称'},
		               	{field:'failedNumber',halign:'center',align:'left',width:120,title:'失败条数'},
		               	{field:'fullNumber',halign:'center',align:'left',width:120,title:'成功条数'},
		               	{field:'jobState',halign:'center',align:'left',width:120,title:'状态'},
		               	{field:'startTime',halign:'center',align:'left',width:120,title:'开始时间'},
		               {field:'stopTime',halign:'center',align:'left',width:120,title:'结束时间'}
		              ]];
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			queryParams:param,
			toolbar:'#toolbar',
			pagination:true,
			fit:true,
			fitColumns:true,
			autoDBClick:true, // 双击改变列宽适应当前内容
			columnWidth:['dictionaryName'], // 需要支持改变列宽的类可以多个用逗号隔开
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess
		});
	};
	
	function onGridLoadSuccess(data) {
		/*if(showValue == 'true'){
			$('#'+gridId).datagrid('showColumn','value');
		}else{
			$('#'+gridId).datagrid('hideColumn','value');
		}*/
		$('#'+gridId).datagrid('clearSelections');
		var gridDataLength = data.total;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId).datagrid('selectRecord',lastDataId);
			}else {
				$('#'+gridId).datagrid('selectRow',0);
			}
		}
	}
	
});