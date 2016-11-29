/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var dictionaryName = null;
	 var value = null;
	 var dictionaryParentId = null;
	 
	 var saveload = "" ;
	//初始化方法
	exports.init = function(opts){
		 msgForm = $("#form");
		dictionaryName = opts.dictionaryName;
		value = opts.value;
		dictionaryParentId = opts.dictionaryParentId;
		
		$("#saveBtn").on("click",function(){
			saveload = false;
			msgForm.submit();
		});
		
		$("#saveBtn_reload").on("click",function(){
			saveload = true;
			msgForm.submit();
		});
		
		msgForm.validate({
			   rules:{
				   dictionaryName:{required:true,maxlength: 50,remote:{
					    async:false,method:'post',
					    url: "../dictionary/validateNameByAjax.html",
	                    data:{dictionaryName:function(){return $("#dictionaryName").val();},oldDicName:dictionaryName,dictionaryParentId:dictionaryParentId}
				   }},
				   dictionaryType:{required:true},
				   value:{required:true,maxlength: 50,remote:{
					    async:false,method:'post',
					    url: "../dictionary/validateCodeByAjax.html",
	                    data:{value:function(){return $("#value").val();},oldDicName:value,dictionaryParentId:dictionaryParentId}
				   }},
				   sortNum:{required:false,digits:true},
				   description :{required:false,maxlength: 1000}
			   },
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
	submitHandler = function(flag){
		if(saveload){
			   $("#saveBtn_reload").attr("disabled","disabled");
		   }else{
			   $("#saveBtn").attr("disabled","disabled");
		   }
		//使用Util.ajax进行操作
		Util.ajax.loadData("../dictionary/saveDictionary.html", 
				function(data){
			if(saveload){
			    $("#saveBtn_reload").removeAttr("disabled");
		 	}else{
			    $("#saveBtn").removeAttr("disabled");
		 	}
					if(!data.code=="ok"){
						showErrorTip(data.msg);
					}
					if(flag=="close"){
						if(saveload){
							var id=data.id;
							$.dialog.data('method')({id:id});
			 				window.location.reload();
		 				}else{
		 					var id=data.id;
			 				$.dialog.data('method')({id:id});
							art.dialog.close();
		 				}
					
					} 
			}, Util.ajax.serializable($('#form')));
	};
});
