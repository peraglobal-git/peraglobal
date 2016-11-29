/**
 * 重置密码js
 */
define(function(require,exports,module){

//	 var password = "";
//	 var confirmPassword = "";
	//初始化方法
	exports.init = function(){
		var msgForm = $("#form");
		msgForm.validate({
			   rules:{
				   password:{required:true},
				   passwordconfirm:{required:true,equalTo : "#password"}
			   },
			   submitHandler:function(){
				   if($('#password').val() == $('#confirmPassword').val()) {
					   submitHandler("close");
				   }else {
					   showErrorTip("两次输入的密码不一致");
				   }
			   }
			});
	};
	submitHandler = function(flag){
//		var v = $("#form").valid();
//		if(v){
			$("#saveBtn").attr("disabled","disabled");
			Util.ajax.loadData("../user/saveSetPassword.html", 
				function(data){
					$("#saveBtn").removeAttr("disabled");
		 			 //刷新页面
		 			if(data.success=='false'){
		 				showErrorTip(data.err);
		 				return ;
		 			}
		 			if(flag=="close"){
		 				art.dialog.close();
		 				$.dialog.data('method')();
		 			} 
			}, Util.ajax.serializable($('#form')));
		
		   //使用ajax进行操作
//		   $("#saveBtn").attr("disabled","disabled");
//	 		$.post("../user/saveSetPassword.html",$('#form').serialize(),function(data){
//	 			$("#saveBtn").removeAttr("disabled");
//	 			 //刷新页面
//	 			if(data.success=='false'){
//	 				showErrorTip(data.err);
//	 				return ;
//	 			}
//	 			if(flag=="close"){
//	 				art.dialog.close();
//	 				$.dialog.data('method')();
//	 			} 
//	 		},"json");
//		}
	};
});
