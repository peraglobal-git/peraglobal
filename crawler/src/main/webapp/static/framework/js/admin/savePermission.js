/**
 * 修改保存部门js
 */
define(function(require,exports,module){
	 var msgForm = $("#form");
	//初始化方法
	exports.init = function(opts){
//		$("#saveBtn").on("click",function(){
//			msgForm.submit();
//		});

		msgForm.validate({
			submitHandler:function(){
				submitHandler("close");
			}
		});
	};
	submitHandler = function(flag){
//		var v = $("#form").valid();
//		if(v){
		   //使用ajax进行操作
		   $("#saveBtn").attr("disabled","disabled");
		   //使用Util.ajax进行操作
		   Util.ajax.loadData("../permission/saveOrUpdate.html", 
				 function(data){
			    $("#saveBtn").removeAttr("disabled");
	 			 //刷新页面
	 			if(data.success=='false'){
	 				showErrorTip(data.err);
	 				return ;
	 			}else if(data.success=='repeate'){
					alert("该权限名称已存在！");
					return ;
	 			}
	 			if(flag=="close"){
	 				$.dialog.data('method')();
	 				art.dialog.close();
	 			} 
		   }, Util.ajax.serializable($('#form')));
//		}
	};
});
