/**
 * 发送邮件
 */
define(function(require,exports,module){
	 var msgForm = $("#form");
	 
	//初始化方法
	exports.init = function(opts){
		$("#sendBtn").on("click",function(){
			msgForm.submit();
		});
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
		var v = msgForm.valid();
		if (v)
		{
			//使用ajax进行操作
			$("#sendBtn").attr("disabled", "disabled");
			$.post("../mail/sendMailToUser.html?ran=" + Math.random(), $('#form').serialize(), function (data)
			{
				$("#saveBtn").removeAttr("disabled");
				//刷新页面
				if (data.success == 'false')
				{
					showErrorTip(data.msg);
					return;
				}
				if (flag == "close")
				{
					art.dialog.close();
					$.dialog.data('method')();
				}
			}, "json");
		}
	};
	
});
