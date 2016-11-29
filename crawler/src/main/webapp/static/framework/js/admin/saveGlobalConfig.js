/**
 * 保存全局属性配置
 */
define(function(require,exports,module){
	 var msgForm = $("#form");
	 
	//初始化方法
	exports.init = function(opts){
//		$("#saveBtn").on("click",function(){
//			msgForm.submit();
//		});
		msgForm.validate({
			   rules:{
				   name:{required:true},
				   code:{required:true},
				   value:{required:true}
			   },
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
	var submitHandler = function (flag)
	{
		//使用ajax进行操作
		$("#saveBtn").attr("disabled", "disabled");
		//使用Util.ajax进行操作
	   Util.ajax.loadData("../globalConfig/saveGlobalConfig.html?ran=" + Math.random(), 
			 function(data){
		    $("#saveBtn").removeAttr("disabled");
			//刷新页面
			if (data.success == 'false')
			{
				showErrorTip(data.err);
				return;
			} else if (data.success == 'repeate')
			{
				showErrorTip("该全局属性CODE已存在！");
				//alert("该全局属性CODE已存在！");
				return;
			}
			if (flag == "close")
			{
				var id=data.id;
 				$.dialog.data('method')({id:id});
				art.dialog.close();
			}
	   }, Util.ajax.serializable($('#form')));
	};
});
