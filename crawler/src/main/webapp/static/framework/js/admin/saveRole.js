/**
 * 修改保存部门js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var id = "";
	 var pid = "";
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
		/*msgForm.submit(function(){
			alert()
			//submitHandler();
		});*/
		/*$("#saveBtn").on("click",function(){
		});*/
		id = opts.id;
		pid = opts.pid;
		msgForm.validate({
			   rules:{
				   roleName:{required:true}
			   },
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
//		$("#form").find("textarea").each(function(){
//			$(this).blur(function(){
//				$(document).on('keyup',function(e){
//			          if(e.keyCode === 13){
//			        	  $('#saveBtn').trigger("click");
//			          }
//			    });
//			}); 
//		});
		
	};
	submitHandler = function(flag){
//		var v = $("#form").valid();
//		if(v){
		   //使用ajax进行操作
		   $("#saveBtn").attr("disabled","disabled");
		   //使用Util.ajax进行操作
		   Util.ajax.loadData("../role/addOrUpdateRole.html?parentId="+pid+"&id="+id, 
				function(data){
				   	$("#saveBtn").removeAttr("disabled");
		 			 //刷新页面
		 			if(data.success=='false'){
		 				showErrorTip(data.err);
		 				return ;
		 			}else if(data.success=='repeate'){
						alert("该角色名称已存在！");
						return ;
		 			}
		 			if(flag=="close"){
		 				var id=data.id;
		 				$.dialog.data('method')({id:id});
		 				art.dialog.close();
		 			} 
			}, Util.ajax.serializable($('#form')));
		//}
	};
});
