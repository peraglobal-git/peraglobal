/**
 * 国际化资源文件js
 */
define(function(require,exports,module){
	var gridId = "grid";
	//datagrid 表格
	var datagrid  = require('datagrid');
	var map = new Map();
	exports.gridLoad = function(){
		//请求连接
		var url = SERVER_CONTEXT+'/lang/findResourceMessageList.html';
		//列
		var columns = [[
		               	{field:'key',halign:'center',align:'left',width:180,title:'资源KEY'},
		               	{field:'value',halign:'center',align:'left',width:300,title:'资源Value', 
		               		editor : {
		               			type : 'validatebox',
		                    	options : {required : true,validType:'noDoubleQuotes'}
		               		}
		               	}
		              ]];
		//初始化grid
		return datagrid.init({
			idField:'key',
			url:url,
			toolbar:'#toolbar',
			columns:columns,
			selectFirstRow:false,
			onDblClickRow:onDblClickRow,
			onAfterEdit:onAfterEdit
		}); 
	};
	initButtonClick = function (){
		$("#toolbar .easyui-linkbutton").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(id.indexOf("save")!=-1){
				target.on('click',function(){saveClickHandler();});
			}
		});
	};
	onDblClickRow = function(rowIndex,rowData){
		/*if(lastIndex){
			$('#'+gridId).datagrid('endEdit',lastIndex);
		}*/
		$('#'+gridId).datagrid('beginEdit',rowIndex);
		lastIndex = rowIndex;
	};
	endEditAll = function(){
		$.each($('#'+gridId).datagrid("getRows"),function(i,obj){
			$('#'+gridId).datagrid('endEdit',i);
		});
	};
	
	onAfterEdit = function(rowIndex,rowData,changes){
		if(changes.value!=null && changes.value!=undefined){
			map.put(rowData.key,changes.value);
		}
	};

	/**
	 * 搜索查询处理
	 */
	onSearchHandler = function (opts) {
		$("#key").inputTextEvent({callback:function(){
			$('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
		}});
	};
	
	saveClickHandler=function(){
		endEditAll();
		if(map.size()>0){
			Util.ajax.loadData(SERVER_CONTEXT+"/lang/saveResourceMessage.html",function(json){
				$("#"+gridId).datagrid("reload");
				map.clear();
			},{data:map.toString()});
		}
	};
	exports.init = function(){
		initButtonClick();
		onSearchHandler();
	};
	
});