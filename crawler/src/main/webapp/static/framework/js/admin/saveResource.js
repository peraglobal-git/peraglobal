/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var url = null;
	 var id = null;
	//初始化方法
	exports.init = function(opts){
	 msgForm = $("#form");
//		$("#saveBtn").on("click",function(){
//			msgForm.submit();
//		});
		url = opts.url;
		id = opts.id;
		msgForm.validate({
			 rules:{
				 url:{required:true,remote:{
					 	async:false,
					    url: "../resource/validateNameByAjax.html",
	                    data:{url:function(){return $("#url").val();},oldUrl:url}
				   }},
				   fullCode:{required:true},
				   sortNum:{required:true,digits:true}
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
		   $("#saveBtn").attr("disabled","disabled");
		   //使用Util.ajax进行操作
		   Util.ajax.loadData("../resource/saveResource.html", 
				 function(data){
					$("#saveBtn").removeAttr("disabled");
		 			 //刷新页面
		 			if(data.success=='fail'){
		 				showErrorTip(data.err);
		 				return ;
		 			}
		 			if(flag=="close"){
		 				var id=data.id;
		 				$.dialog.data('method')({id:id});
		 				$.dialog.close();
		 			} 
		   }, Util.ajax.serializable($('#form')));
	};

});
