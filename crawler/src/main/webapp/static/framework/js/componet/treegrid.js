define(function(require, exports, module) {
	
	
	//treegrid的配置项
	var _option = {
			url:'',							//数据表格数据来源,json格式的数据
			fit:true,						//数据表格自动适应大小
			striped: true,					//是否以斑马线样式显示数据
			pagination:false,				//是否使用分页显示
			rownumbers:true,				//是否显示行号
			singleSelect:false,				//是否单行选择
			columns:[[{field:'itemid',checkbox:true}]],//加载的列
			frozenColumns:[[]],				//冻结的列
			fitColumns:false,				//列自适应宽度
			resizeHandle:'right',			//调整列的位置，可用的值有：'left','right','both'
			autoRowHeight:'false',			//定义设置行的高度，根据该行的内容。设置为false可以提高负载性能
			method:'post',					//设置请求参数类型
			nowrap:false,					//是否在同一行显示数据，true可以提高性能
			loadMsg:'加载中，请稍候',			//加载数据过程显示
			pageNumber:1,					//在设置分页属性的时候初始化页码
			pageSize:20,					//在设置分页属性的时候初始化页面大小
			pageList:[20, 30, 40, 50, 100],		//分页选择显示条数
			queryParams:{},					//在请求远程数据的时候发送额外的参数
			sortName:null,					//定义哪些列可以进行排序
			idField:'id',					//id字段 支持记忆选择
		//	parentField:'parentId',
			treeField:'name',				//树节点字段,形成树形结构必须的属性
			animate:false,					//节点展开或者折叠的时候是否显示动画
			
			remoteSort:false,				//定义从服务器对数据进行排序
			showHeader:true,				//定义是否显示行头
			showFooter:false,				//定义是否显示行脚
			scrollbarSize:18,				//滚动条的宽度(当滚动条是垂直的时候)或高度(当滚动条是水平的时候)。
			checkOnSelect:true,			//如果为true，当用户点击行的时候该复选框就会被选中或取消选中。如果为false，当用户仅在点击该复选框的时候才会呗选中或取消。
			selectOnCheck:true,				//如果为true，单击复选框将永远选择行。如果为false，选择行将不选中复选框。
			
			
			
			//数据加载成功事件
			onLoadSuccess:onLoadSuccess,
			
			//数据加载失败事件
			onLoadError:onLoadError
	};
	
	//获取传入的json数据
	function getFieldJson(json) {
		if(json != undefined && json != null && json !="") {
			for(var key in json) {
				_option[key] = json[key];
			} 
		}
	}

	//加载完成后的默认方法
	var onLoadSuccess = function(row,data) {
		var _dataLength = data.total;
		if( _dataLength > 0) {
			$(target).treegrid('selectRow',0);
		}
		
	};

	//数据加载失败事件
	var onLoadError = function(jqXHR){
		Util.ajax.ajaxRequestError(jqXHR);
	};

	//获取所有的配置项
	exports.init = function(treegridId,gridOptionsJson) {
		getFieldJson(gridOptionsJson);
		return $('#'+treegridId).treegrid(_option);
		loadTreegrid();
	};
	
	
	function loadTreegrid(treegridId,param){
		return $('#'+treegridId).treegrid(_option);
	}
});