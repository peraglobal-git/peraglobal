define(function(require, exports, module) {
	
	
	//grid的配置项
	var _option = {
			url:'',		//数据表格数据来源,json格式的数据
			width:auto,
			height:auto,
			panelWidth:null,
			panelHeight:auto,
			multiple:false,
			separator:",",
			esitable:true,
			disabled:false,
			hasDownArrow:true,
			value:"",
			delay:200,
			valueField:'id',
			textField:'name',
			method:'POST'
	};
	

	//获取传入的json数据
	function getFieldJson(json) {
		if(json != undefined && json != null && json !="") {
			for(var key in json) {
				_option[key] = json[key];
			} 
		}
	}

	//获取所有的配置项
	exports.init = function(comboId,comboOptionsJson) {
		getFieldJson(comboOptionsJson);
		return $('#'+gridId).combobox(_option);
	};
});