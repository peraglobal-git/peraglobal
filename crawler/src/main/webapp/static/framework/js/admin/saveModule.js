/**
 * 修改保存导航js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var id = "";
	 var pid = "";
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
//		$("#saveBtn").on("click",function(){
//			submitHandler("close");
//		});
		id = opts.id;
		pid = opts.pid;
		msgForm.validate({
		   rules:{
			   moduleName:{required:true},
			   path:{required:true},
			   isPermission:{required:true},
			   isCanRefresh:{required:true}
		   },
		   submitHandler:function(){
			   submitHandler("close");
		   }
		});
		//绑定上传事件
		Util.formUpload.initEvent("#form",{type:'image',imageSize:'20x18'},
		{success:function(data){
			$("#moduleFilePath").val(data.url);
		}});
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
		   Util.ajax.loadData("../module/addOrUpdateModule.html?parentId="+pid+"&id="+id, 
				 function(data){
					$("#saveBtn").removeAttr("disabled");
		 			 //刷新页面
		 			if(data.success=='false'){
		 				showErrorTip(data.err);
		 				return ;
		 			}else if(data.success=='repeate'){
		 				alert("该导航名称已存在！");
						return ;
		 			}
		 			if(flag=="close"){
		 				var id=data.id;
		 				$.dialog.data('method')({id:id});
		 				$.dialog.close();
		 			} 
		   }, Util.ajax.serializable($('#form')));
		   
//		}
	};
});
