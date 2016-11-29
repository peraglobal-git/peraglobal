define(function(require, exports, module) {
	
	var loadSuccessDefault  = function (node, data) {
		var treeObj = $("#"+_option.treeId).tree("collapseAll");
		var rootNode = treeObj.tree("getRoot");
		if(rootNode){
			$("#"+_option.treeId).tree("expand",rootNode.target);
		}
		if(_option.onLoadSuccessCall && Util.fn.isFn(_option.onLoadSuccessCall)){
			_option.onLoadSuccessCall(node, data);
		}
	};
	
	var  onLoadErrorDefault= function (jqXHR) {
		Util.ajax.ajaxRequestError(jqXHR);
	};
	
	var _option = {
		onLoadError:onLoadErrorDefault,
		onLoadSuccess:loadSuccessDefault,
		treeId:"tree"
	};
	
	

	//设置使用自定义toolbar
	exports.init = function(treeId,opts) {
		//getFieldJson(treeOptionsJson);
		if(opts.onLoadSuccess){
			_option.onLoadSuccessCall=opts.onLoadSuccess;
			//opts.onLoadSuccess = null;
		}
		_option = $.extend(_option,opts);
		_option.onLoadSuccess=loadSuccessDefault;
		return $('#'+treeId).tree(_option);
	};

});