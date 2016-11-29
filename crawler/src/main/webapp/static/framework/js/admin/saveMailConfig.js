/**
 * 保存邮件配置
 */
define(function(require,exports,module){
	 var msgForm =null;
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
		msgForm.validate({
			   rules:{
				   name:{required:true},
				   type:{required:true}
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
	   Util.ajax.loadData("../mail/saveMailConfig.html?ran=" + Math.random(), 
			 function(data){
		    $("#saveBtn").removeAttr("disabled");
			//刷新页面
			if (data.success == 'false')
			{
				showErrorTip(data.err);
				return;
			} else if (data.success == 'repeate')
			{
				alert("该邮件配置名称已存在！");
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
