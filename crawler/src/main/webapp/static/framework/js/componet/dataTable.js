define(function(require, exports, module) {
	var _tableId=null;
	//加载完成后的默认方法
	var onLoadSuccess = function(settings ) {
	};

	var onLoadDraw = function(settings ){
		
	}
	//数据加载失败事件
	var onLoadError = function(jqXHR){
		Util.ajax.ajaxRequestError(jqXHR);
	};
	
	//grid的配置项
	var _option = {
			"ajax" : { //数据表格数据来源,json格式的数据
				"url" : '',
				"dataSrc" : ''
			},
			"order" : [[1, 'asc']],
			"bPaginate" : true,// 分页按钮
			"iDisplayLength" : 20,
			"bLengthChange" : false,// 每行显示记录数
			"sPaginationType" : "full_numbers",
			"bFilter" : false,// 搜索栏
			"bAutoWidth" : true, // 自适应宽度
			"bJQueryUI" : false,
			"bScrollInfinite" : false,
			"sScrollY" : "500px", // 表格的高度 不够的话用滚动条
			"bScrollCollapse" : false, // 当设置sScrolly时 如果数据没那么高 表格是否自适应高度
			"columns" : [],
			"columnDefs" : [],
			"dom" : "<'row'<'col-xs-2'l><'#mytool.col-xs-4'><'col-xs-6'f>r>"
					+ "t" + "<'row'<'col-xs-6'i><'col-xs-6'p>>",
			"language" : {
				"processing" : "加载中...",
				"lengthMenu" : "显示 _MENU_ 项结果",
				"zeroRecords" : "没有匹配结果",
				"info" : "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
				"infoEmpty" : "显示第 0 至 0 项结果，共 0 项",
				"infoFiltered" : "(由 _MAX_ 项结果过滤)",
				"infoPostFix" : "",
				"url" : "",
				"paginate" : {
					"first" : "首页",
					"previous" : "上一页",
					"next" : "下一页",
					"last" : "末页"
				}
			},
			//数据加载成功事件
			"initComplete" : onLoadSuccess,
			"drawCallback" : onLoadDraw
	};
	
	
	//获取所有的配置项
	exports.init = function(tableId,gridOptionsJson) {
		var opts={};
		if(tableId &&  typeof tableId!="string"){
			opts=tableId;
			tableId = null;
		}else if(gridOptionsJson){
			opts=gridOptionsJson;
		}
		if(opts.onLoadSuccess){
			_option.initComplete=opts.onLoadSuccess;
			delete _option.onLoadSuccess;
		}
		//合并参数
		$.extend(_option,opts,{});
		
		_option.initComplete=onLoadSuccess;
		//如果tableId为空则默认为example
		_tableId=tableId || 'example';
		
		return $('#'+_tableId).DataTable(_option);
	};
	
	
	
	
	
	
	
	/**
	 * 默认列格式化
	 */
	exports.formatterDefault=function(value,row,index,length){
		if(!value) return '';
		length=length||30;
		if(typeof value =='object')
			return value.chineseName||'';
		else if(typeof value =='string'){
			if(value.length > length){
				return '<span title="'+value+'">'+Util.string.cut(value,length)+'</span>';
			}else{
				return value;
			}
		}else{
			return '';
		}
	};
	
	/**
	 * 字典​查询名称方法
	 * 参数: 
	 * type：字典的类型
	 * value：字典的value值
	 */
	 exports.formatterDictionaryName = function(type, value) {
	 	var name;
		var url = '../dictionary/findDictionaryNameByTypeValue.html';
		var params = {
			type : type,
			value : value
		};
		var success = function(data) {
			name = data.name;
		}
		Util.ajax.syncAjax(url, success, params, {});
		return name;
	 };
	
});