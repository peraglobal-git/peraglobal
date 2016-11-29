/**
 * 修改保存js
 */
define(function(require,exports,module){
	 var msgForm = null;
	//初始化方法
	exports.init = function(opts){
		 msgForm = $("#form");
		msgForm.validate({
			   rules:{
				   /*dictionaryName:{required:true,maxlength: 50,remote:{
					    async:false,
					    url: "../dictionary/validateNameByAjax.html",
	                    data:{dictionaryName:function(){return $("#dictionaryName").val();},oldDicName:dictionaryName,dictionaryParentId:dictionaryParentId}
				   }},
				   value:{required:true,maxlength: 50},
				   sortNum:{required:false,digits:true},
				   description :{required:false,maxlength: 1000}
				   */
			   },
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
	submitHandler = function(flag){
		$("#saveBtn").attr("disabled","disabled");
		//使用Util.ajax进行操作
		Util.ajax.loadData("../geneateDemo/saveGeneateDemo.html",
				function(data){
					$("#saveBtn").removeAttr("disabled");
					if(!data.code=="ok"){
						showErrorTip(data.msg);
					}
					if(flag=="close"){
						var id=data.id;
		 				$.dialog.data('method')({id:id});
						$.dialog.close();
					} 
			}, Util.ajax.serializable($('#form')));
	};
});
