/**
 * 修改保存用户js
 */
define(function(require,exports,module){
	 var msgForm = null;
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
		msgForm.validate({
			   rules:{
				   password:{required:true},
				   newPassword:{required:true},
				   newConfirmPassword:{required:true}
			   },
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
	submitHandler = function(flag){
//		var v = $("#form").valid();
		var newPassword = $('#newPassword').val();
		var newConfirmPassword = $('#newConfirmPassword').val();
		
		if(newPassword != newConfirmPassword) {
			showErrorTip("两次密码输入不一致");
			return ;
		}
//		if(v){
		   //使用ajax进行操作
		   $("#saveBtn").attr("disabled","disabled");
		   Util.ajax.loadData("../user/saveChangePassword.html", 
					function(data){
					   $("#saveBtn").removeAttr("disabled");
			 			 //刷新页面
			 			if(data.success=='false'){
			 				showErrorTip(data.err);
			 				return ;
			 			}
			 			if(flag=="close"){
			 				if(data.success=='success'){
			 					art.dialog.data('resetPwd', 'success'); 
				 			}else{
				 				art.dialog.data('resetPwd', 'false'); 
				 			}
			 				art.dialog.close();
			 			} 
				}, Util.ajax.serializable($('#form')));
//		}
	};
});
