/**
 * 修改保存用户js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var globalName = "";
	 var id = "";
	 var pid = "";
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
//		$("#saveBtn").on("click",function(){
//			var t = msgForm.form("validate");
//			msgForm.submit();
//		});
		globalName = opts.name;
		id = opts.id;
		pid = opts.pid;
		msgForm.validate({
			   rules:{
				   username:{required:true},
				   password:{required:true},
				   passwordconfirm:{required:true},
				   email:{email:true}
			   },
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
//		$(document).on('keyup',function(e){
//	          if(e.keyCode === 13){
//	        	  $('#saveBtn').trigger("click");
//	          }
//	    });
	};
	submitHandler = function(flag){
//		var v = $("#form").valid();
//		if(v){
		   //使用ajax进行操作
		   $("#saveBtn").attr("disabled","disabled");
		   //使用Util.ajax进行操作
		   Util.ajax.loadData("../user/saveOrUpdateUser.html",
				 function(data){
			    $("#saveBtn").removeAttr("disabled");
	 			 //刷新页面
	 			if(data.success=='false'){
	 				showErrorTip(data.err);
	 				return ;
	 			}else if(data.success=='repeate'){
	 				showErrorTip("账号已存在！");
					return ;
	 			}
	 			if(flag=="close"){
	 				var id=data.id;
	 				$.dialog.data('method')({id:id});
	 				art.dialog.close();
	 			} 
		   }, Util.ajax.serializable($('#form')));
//		}
	};
});
