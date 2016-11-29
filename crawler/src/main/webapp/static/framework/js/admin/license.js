/**
 * license导入js
 */
define(function(require,exports,module){
	
    function submitBtnButton(){
   	 /*提交license*/
   	    $("#submitBtn").click( function () {
   	    	$('#formUploadLic').form({
   	    	    url:"../license/getLicense.html",
   	    	    onSubmit: function(){
   	    	    	return $(this).form("validate");
   	    	    },
   	    	    success:function(data){
   	    	    	var data=$.parseJSON(data);
   	    	    	if(data.success == "false"){
   		 				// 提交失败
   	    	    		showErrorTip({content:'license文件提交失败！'});
   		 				return;
   	    	    	} else {
   		 				$("#userAmountdisp").val(data.license.userAmountdisp);
   		 			    $("#projectAmountdisp").val(data.license.projectAmountdisp);
   		 				$("#canUseModules").val(data.license.canUseModules);
   		 				$("#connectAmountdisp").val(data.license.connectAmountdisp);
   		 				$("#notAfterdisp").val(data.license.notAfterdisp);
   						showSuccessTip({content:'license文件提交成功!',ok:function(){window.location.href = '../login/logout.html';}});
   		 			}
   	    	    }
   	    	});
   	    	var file = $('#importFile').val();
   			if (file == '') {
   				showWarningTip("请选择你要导入的license文件！");
   				return;
   		    }
   			var fileArr = file.split("\\"); 
   			var fileTArr = fileArr[fileArr.length-1].toLowerCase().split("."); 
   			var filetype = fileTArr[fileTArr.length-1]; 
   			var fileName = fileArr[fileArr.length-1].toLowerCase();
   			if (filetype != "lic") {
   				showErrorTip({content:'请选择正确格式的license文件！'});
   				return;
   			}else if (fileName != "license.lic") {
   				showErrorTip({content:'所选文件的文件名验证错误！'});
   				return;
   			} else {
   		    	//提交表单
   		    	$('#formUploadLic').submit();
   			}
   	    });
    }
  
	//初始化方法
	exports.init = function() {
		submitBtnButton();
	}
});
