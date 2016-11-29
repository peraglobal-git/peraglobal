define(function(require,exports,module){
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	exports.gridLoad = function(){
		//请求连接
		var url = '../login/findLoginLogList.html';
		//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		               	{field:'createByrealName',halign:'center',align:'center',width:120,title:'用户姓名'},
		               	{field:'logText',halign:'center',align:'center',width:250,title:'登录概述'},
		               	{field:'createDate',halign:'center',align:'center',width:180,title:'登录时间'},
		               	{field:'ip',halign:'center',align:'center',width:120,title:'IP'}
		              ]];
		//初始化grid
		return datagrid.init({
			url:url,
			columns:columns,
			fitColumns:true,
			toolbar:'#toolbar',
			pagination:true,
			autoDBClick:true, // 双击改变列宽适应当前内容
			columnWidth:['logText'], // 需要支持改变列宽的类可以多个用逗号隔开
			checkOnSelect:false,
			selectOnCheck:false,
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess,
			onResizeColumn:function onResizeColumn(field, width){datagridCellTextHidden($('#grid').datagrid("options").columnWidth);}//当用户调整列宽时触发
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
	/**
	 * 导出
	 */
	function onExportLogClick() {
		$("#importLoginLog").on("click",function() {
			showQuestionTip({
				content:'确认导出所有日志信息?',
				ok:function(){
					  location.href = "../login/exportLoginLogList.html";
				}
			});

		});
	}
	/**
	 * 清空
	 */
	function onDelLogClick() {
		$("#deleteLoginLog").on("click",function() {
			var rows = $('#'+gridId).datagrid("getSelections");
//			showQuestionTip({
//				content:'确认清空日志信息？',
//				ok:function(){
				submitDel('../login/deleteLoginLog.html',{
					rows: rows,
					callback: function(data){
						$('#grid').datagrid("load");
					}
				});
//				}
//			});
		});
	}
	
	exports.init = function() {
		onSearchButtonClick();
		onExportLogClick();
		onDelLogClick();
	};
});
