/**
 * 
 */
define(function(require,exports,module){
	 /*下载模板*/
	function downButton(){
		 $("#downloadTemplate").click( function () {
		    	window.location.target="_self";
		    	window.location.href = "../user/downLoadTemplateSystemUser.html";
		    });
	}
     function impUserButton(){
    	 /*导入用户*/
    	    $("#impUser").click( function () { 
    	    	$('#impForm').form({
    	    	    url:"../user/importSystemUser.html",
    	    	    onSubmit: function(){
    	    	    	return $(this).form("validate");
    	    	    },
    	    	    success:function(data){
    	    	    	var data=$.parseJSON(data);
    	    	    	if(data.success == "false"){
    	    	    		if(data.errmsg!=null&&data.errmsg!=''){
    	    	    			showWarningTip(data.errmsg);
    	    	    		}else{
    	    	    			showErrorTip("导入失败,请重新检查导入文件!");
    	    	    		}
    	    	    	}
    	    	    	if(data.success == "true"){
    	    	    		art.dialog.close();
    	    	    		$.dialog.data('method')();
    	    	    	} 
    	    	    }
    	    	});
    	    	var file = $('#importExcel').val();
    			if(file == ''){
    				showSuccessTip('请选择你要上传的文件');
    				return;
    		    }
    			var fileArr = file.split("\\"); 
    			var fileTArr = fileArr[fileArr.length-1].toLowerCase().split("."); 
    			var filetype = fileTArr[fileTArr.length-1]; 
    			if(filetype != "xls"){
    				showSuccessTip('请选择Excel格式的文件');
    				return;
    			}else{
    		    	//提交表单
    		    	$('#impForm').submit();
    			}
    	    });
     }
   
   function cancleButton(){
	   $("#cancel").on("click",function(){
		   art.dialog.close();
	  });
   }
	//初始化方法
	exports.init = function() {
		/*downButton();
		impUserButton();
		cancleButton();*/
	};
   
});
