define(function(require,exports,module){
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	exports.gridLoad = function(){
		//请求连接
		var url = SERVER_CONTEXT+'/operationLog/findOperationLogJosnList.html';
		//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		               	{field:'modelName',halign:'center',align:'left',width:120,title:'模块名称'},
		               	/*{field:'parentModelName',halign:'center',align:'left',width:120,title:'关联模块名称'},*/
		               	{field:'operationType',halign:'center',align:'left',width:120,title:'操作类型',formatter:datagrid.formatterDefault},
		               	{field:'logText',halign:'center',align:'left',width:250,title:'详细内容'},
		               	{field:'createDate',halign:'center',align:'left',width:180,title:'操作时间'},
		               	{field:'createBy',halign:'center',align:'left',width:120,title:'操作人',formatter:function(value,rowData,rowIndex) {
		          		  var user = datagrid.formatterUserName(value);
		        		  return user.realName;
		    		  }}
		              ]];
		//初始化grid
		return datagrid.init({
			url:url,
			columns:columns,
			toolbar:'#toolbar',
			pagination:true,
			autoDBClick:true, // 双击改变列宽适应当前内容
			columnWidth:['logText'], // 需要支持改变列宽的类可以多个用逗号隔开
			checkOnSelect:false,
			selectOnCheck:false,
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess
		}); 
	};

	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		$('#'+gridId).datagrid('clearSelections');
		var gridDataLength = data.total;
		if(gridDataLength > 0) {
			$('#'+gridId).datagrid('selectRow',0);
		}
	}
	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		$("#search_button").on("click",function() {
			$('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
		});
	}
	
	exports.init = function() {
		onSearchButtonClick();
	};
});
