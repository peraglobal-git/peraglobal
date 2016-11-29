define(function(require,exports,module){
	 var msgForm = null;
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
		msgForm.validate({
			rules:{
				createSql:{required:true,createSql:true}
			   },
			   submitHandler:function(){
				   submitHandler();
			   }
		});
	};
	submitHandler = function(){
		
		   Util.ajax.loadData("../test/createTable.html",
				 function(data){
	 			if(data.success=='success'){
	 				showSuccessTip('建表成功');
	 				return ;
	 			}if(data.success=='exist'){
	 				showErrorTip('表以存在，不许创建！');
	 				return ;
	 			}if(data.success=='false'){
	 				showErrorTip('建表失败！');
	 				return ;
	 			}if(data.success=='dberror'){
	 				showErrorTip('脚本与使用数据库不一致！');
	 				return ;
	 			}
		   }, Util.ajax.serializable($('#form')));
	};
});
