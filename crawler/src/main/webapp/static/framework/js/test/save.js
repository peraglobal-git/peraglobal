/**
 * 修改保存用户js
 */
define(function(require,exports,module){
	 var msgForm = null;
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
		msgForm.validate({
			   submitHandler:function(){
				   submitHandler();
			   }
			});
	};
	submitHandler = function(){
		   //使用ajax进行操作
		   //$("#saveBtn").attr("disabled","disabled");
		   //使用Util.ajax进行操作
		   Util.ajax.loadData("../test/saveOrUpdate.html",
				 function(data){
			    //$("#saveBtn").removeAttr("disabled");
	 			 //刷新页面
	 			if(!data.success=='false'){
	 				showErrorTip('代码生成成功，请到目标项目下查看！');
	 				return ;
	 			}
		   }, Util.ajax.serializable($('#form')));
	};
});
