/**
 * 修改保存动态属性js
 */
define(function(require,exports,module){
	 var msgForm = $("#form");
	//初始化方法
	exports.init = function(opts){
		msgForm.validate({
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
	submitHandler = function(flag){
	   $("#saveBtn").attr("disabled","disabled");
	   //使用Util.ajax进行操作
	   Util.ajax.loadData("../dynamicModelProperty/save.html", 
			 function(data){
				$("#saveBtn").removeAttr("disabled");
	 			 //刷新页面
	 			if(data.code=='ok'){
	 				$.dialog.data('method')();
	 				$.dialog.close();
	 			} 
	   }, Util.ajax.serializable($('#form')));
	};
});
