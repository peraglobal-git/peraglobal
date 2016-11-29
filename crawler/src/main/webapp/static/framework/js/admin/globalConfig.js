                                    /**
 * 全局配置js
 */
define(function(require,exports,module){

	//artDialog
	var dialog  = require('artDialogJs');
	
	//datagrid 表格
	var dataGrid  = require('datagrid');
	var gridId = "grid";

	
    exports.init = function(){
    	 
    	$("#globalConfigTabs").tabs({
    		 border:false,
    		onSelect:function(title){
    			if(title=="自定义参数配置"){
    				
    			//	 $("#frame_config").height($('#js_layout').height()-24);
    				var frame = $("#frame_config") ;
    				var src = frame.attr("src") ;
    				if(src != '../globalConfig/toGlobalConfigList.html'){
    					frame.attr("src","../globalConfig/toGlobalConfigList.html") ;
    				}
    			}
    		}
    	}) ;
    	
		
		 
		
		//绑定上传事件
		Util.formUpload.initEvent("#basicForm",{type:'image'},
		{success:function(data){
			if(data.fileId == "backFile"){
				$("#backModuleFilePath").val(data.url);
			}else if(data.fileId == "logoFile"){
				$("#logoModuleFilePath").val(data.url);
			}
//			$("#backModuleFilePath").val(data.url);
		}});
		//设置checkbox框对应的隐藏域值
		$("#globalConfigTabs :checkbox").on("change",function(){
			var id = this.id;
			$("#"+id+"_hidden").val((this.checked));
			if($("#three_role_disabled_no_closed_hidden").val()=='true'){
				showWarningTip({content:'<font color="red">你选择了三员初始化功能，这代表需要重新初始化用户权限，此操作不能撤销。</font>'});
			}
			if(this.checked && this.value=="on"){
				if(id == "email_disabled_no_closed"){
					$("#email_server_password").attr("readonly",false) ;
					$("#email_server_username").attr("readonly",false) ;
					$("#email_server_address").attr("readonly",false) ;
					
				}else{
					$("#ad_server_name").attr("readonly",false) ;
					$("#ad_server_port").attr("readonly",false) ;

				}
				
			}else{
				if(id == "email_disabled_no_closed"){
					$("#email_server_password").attr("readonly",true) ;
					$("#email_server_username").attr("readonly",true) ;
					$("#email_server_address").attr("readonly",true) ;
					
				}else{
					$("#ad_server_name").attr("readonly",true) ;
					$("#ad_server_port").attr("readonly",true) ;

				}
			}
		});
		//保存按钮
	
		$("#saveBtnMail").on("click",function(){
			showQuestionTip({content:'你确定要保存配置信息吗？',ok:function(){
				$.post("../globalConfig/saveMailConfig.html",$('#mail_form').serialize(),function(data){
					if(data.success=="success"){
						showSuccessTip("保存成功") ;
					}
		 		},"json");
				}
			});
		});
		$("#saveBtnAd").on("click",function(){
			showQuestionTip({content:'你确定要保存配置信息吗？',ok:function(){
					$.post("../globalConfig/saveServiceConfig.html",$('#service_form').serialize(),function(data){
						if(data.success=="success"){
							showSuccessTip("保存成功") ;
						}
			 		},"json");
				}
			});
		});
		/*$("#easyBu").on("click",function(){
			showQuestionTip({content:'你确定要保存配置信息吗？',ok:function(){
				$.post("../globalConfig/saveGlobalConfigBatch.html",$('#form_').serialize(),function(data){
					alert(data.code) ;
					$("#formTable").css("display","none") ;
			    	$("#easyBu").attr("disabled",true) ;
		 		},"json");
			}});
		});*/
		$("#basicButton").on("click",function(){
			showQuestionTip({content:'你确定要保存配置信息吗？',ok:function(){
				$.post("../globalConfig/saveBasicGlobalConfigBatch.html",$('#basicForm').serialize(),function(data){
					if(data.code=="success"){
						showSuccessTip("保存成功") ;
					}
		 		},"json");
			}});
		});
	};
	
	/**
	 * 选择一个选项
	 */
	exports.selectTab = function(id){
		if(!id){
			$("#globalConfigTabs").tabs("select",0);
			return ;
		}
		var tabs = $("#globalConfigTabs").tabs("tabs");
		$.each(tabs,function(i,obj){
			if($(obj).attr("id")==id){
				$("#globalConfigTabs").tabs("select",i);
				return false;
			}
			
		});
		
	};
	
});