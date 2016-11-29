/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var oldname = null;
	 var linkState=null;
	//根据知识源类型切换页面
	exports.changeSelect=$(function(){
		$("#SelectType").change(function(){
			var checkText=$("#SelectType").find("option:selected").val();
				if(checkText==1){
						$("#webdiv").show();
						$("#dbdiv").hide();	
				}
				else if(checkText==2){
						$("#userdiv").hide();
						$("#webdiv").hide();
						$("#dbdiv").show();
				}
				else if(checkText==3){
						$("#userdiv").hide();
						$("#webdiv").hide();
						$("#dbdiv").hide();
				}
//				$("div.qtip").remove();
		});
	 });
	
	//web知识源是否需要用户登录
	exports.checkradio=$(function(){
		 $(".userradio").click(function(){
			 var checkradio=$("input[type='radio']:checked").val();
				if(checkradio==0){
					$("#userdiv").hide();
				}else if(checkradio==1){
					$("#userdiv").show();
				};
		 });
	});
	
	//输入框提示
	exports.prompt=$(function(){
		/*$('#input_password').bind({
			
			focus:function(){
				if (this.value == "******"){
					this.value="";
				}
			},
			blur:function(){
				if ($('#input_password').val()==""){
					$('#input_password').val("******");
				}
			}
		});*/
		
		// 网站名称显示逻辑 added by yangxc
		$('#webname').bind({
			focus:function(){
				if (this.value == "请输入网站名称"){
					$("#webname").css({"color":"#000"});
					this.value="";
				}
			},
			blur:function(){
				if (this.value == ""){
					$("#webname").css({"color":"#CCC"});
					$("#webname").val("请输入网站名称");
				}
			}
		});
		// 网站地址显示逻辑 added by yangxc
		$('#weburl').bind({
			focus:function(){
				if (this.value == "请输入地址"){
					$("#weburl").css({"color":"#000"});
					this.value="";
				}
			},
			blur:function(){
				if (this.value == ""){
					$("#weburl").css({"color":"#CCC"});
					$("#weburl").val("请输入地址");
				}
			}
		});
		// 用户名表单项显示逻辑 added by yangxc
		$('#user_k').bind({
			focus:function(){
				if (this.value == "表单项名称"){
					$("#user_k").css({"color":"#000"});
					this.value="";
				}
			},
			blur:function(){
				if (this.value == ""){
					$("#user_k").css({"color":"#CCC"});
					$("#user_k").val("表单项名称");
				}
			}
		});
		// 用户名表单值显示逻辑 added by yangxc
		$('#username_v').bind({
			focus:function(){
				if (this.value == "表单值"){
					$("#username_v").css({"color":"#000"});
					this.value="";
				}
			},
			blur:function(){
				if (this.value == ""){
					$("#username_v").css({"color":"#CCC"});
					$("#username_v").val("表单值");
				}
			}
		});
		// 密码表单项显示逻辑 added by yangxc
		$('#password_k').bind({
			focus:function(){
				if (this.value == "表单项名称"){
					$("#password_k").css({"color":"#000"});
					this.value="";
				}
			},
			blur:function(){
				if (this.value == ""){
					$("#password_k").css({"color":"#CCC"});
					$("#password_k").val("表单项名称");
				}
			}
		});
		// 密码表单值显示逻辑 added by yangxc
		$("input[name='password_v']").bind({
			focus:function(){
				if (this.value == "表单值"||this.value=="******"){
					$("input[name='password_v']").eq(0).css({"color":"#000"});
					$("input[name='password_v']").eq(0).val("");
				}
			},
			blur:function(){
				if(this.defaultValue=="******"&&this.value==""){
					$("input[name='password_v']").eq(0).val("******");
				}else if(this.defaultValue="表单值"&&this.value==""){
					$("input[name='password_v']").eq(0).css({"color":"#CCC"});
					$("input[name='password_v']").eq(0).val("表单值");
				}
			}
		});
	});
	
	//验证数据库链接
	exports.linkDB=$(function(){
		$("#linkbutton").click(function(){
			var DBSelectType =$("#DBSelectType").find("option:selected").val();
			var dburl=$("#dburl").val();
			var port=$("#port").val();
			var dataname=$("#dataname").val();
			var username=$("#username").val();
			var input_password=$("#input_password").val();
			var password=null;
			if(input_password=="******"){
				password=$("#password").val();
			}else{
				password=$("#input_password").val();
			}
			var SelectType=$("#SelectType").find("option:selected").val();
			if(SelectType==2&&DBSelectType==0){
				alert("请选择数据库类型");
				return;
			}else{
			$.ajax({
				type:'POST',
				url:"../source/getLinkDB.html",
				dataType:'json',
				data:{
					DBSelectType:DBSelectType,
					dburl:dburl,
					port:port,
					dataname:dataname,
					username:username,
					password:password,
				},
				success:function(result){
					//result.state;
					if(result.state==0){
						alert("链接成功！");
					//	$("#linkState").val("0");
					}else {
						alert("链接异常，请检查您填写的数据！");
					//	$("#linkState").val("1");
					}
				}
			});
			}
		});
	});
	exports.linkWeb=$(function(){
		//验证web登录
		$("#loginButton").click(function(){
			var loginRequest=$("#loginrequest").val();
			var user_k=$("#user_k").val();
			var password_k=$("#password_k").val();
			var username=$("#username_v").val();
			var input_password=$("#input_password").val();
			var password=null;
			if(input_password=="******"){
				password=$("#password").val();
			}else{
				password=$("input[name='password_v']").val();
			}
			var loginSubmit=$("#loginsubmit").val();
			$.ajax({
				type:'POST',
				url:"../source/getLinkWeb.html",
				dataType:'json',
				data:{
					loginRequest:loginRequest,
					user_k:user_k,
					password_k:password_k,
					username:username,
					password:password,
					loginSubmit:loginSubmit,
				},
				success:function(result){
					//result.state;
					if(result.state==0){
						alert("链接成功！");
						$("#linkState").val("0");
					}else {
						alert("链接异常，请检查您填写的数据！");
						$("#linkState").val("1");
					}
				}
			});
		});
	});
	
	//初始化方法
	exports.init = function(opts){
		
		msgForm = $("#form");
		oldname = opts.oldname;
		msgForm.validate({
			   rules:{
				   name:{
					   	required:true,
					   	titleInput:true,
					   	maxlength: 80,remote:{
					   	type:"POST",
					    async:false,
					    url: "../source/validateNameByAjax.html",
	                    data:{name:function(){
	                    	return $("#name").val();
	                    	},
	                    	oldName:oldname,
	                    	}
				   }},
				   dataname:{required:true,titleInput:true},
				   dburl:{required:true},
				   port:{required:true,digits:true},
				   username:{required:true},
				   password:{required:true},
				   webname:{required:true,titleInput:true,webname_va:true},
				   weburl:{required:true,weburl_va:true,url:true},
				   user_k:{required:true,k_va:true},
				   password_k:{required:true,k_va:true},
				   loginrequest:{required:true,url:true},
				   loginsubmit:{required:true,url:true},
				   password_v:{required:true,v_va:true},
				   username_v:{required:true,v_va:true}
			   },
			   
			   submitHandler:function(){
				   
				   submitHandler("close");
			   }
			});
		//增加网站名称输入框提示信息为灰色 added by yangxc
		if($("#webname").val()=="请输入网站名称"){
			$("#webname").css({"color":"#CCC"}); 
		}
		//增加网站地址输入框提示信息为灰色 added by yangxc
		if($("#weburl").val()=="请输入地址"){
			$("#weburl").css({"color":"#CCC"}); 
		}
		//增加用户名输入框提示信息为灰色 added by yangxc
		if($("#user_k").val()=="表单项名称"){
			$("#user_k").css({"color":"#CCC"}); 
		}
		if($("#username_v").val()=="表单值"){
			$("#username_v").css({"color":"#CCC"}); 
		}
		//增加密码输入框提示信息为灰色 added by yangxc
		if($("#password_k").val()=="表单项名称"){
			$("#password_k").css({"color":"#CCC"}); 
		}
		// $("#input_password").css({"color":"#CCC"}); 
		if($("input[name='password_v']").val()=="表单值"){
			$("input[name='password_v']").css({"color":"#CCC"}); 
		}
		
	};
	
	(function($) {
		
		$.validator.addMethod("webname_va", function(value, element) {
			return this.optional(element) ||  /^(?!请输入网站名称)/i.test(value);
		}, "不能为空");
		$.validator.addMethod("weburl_va", function(value, element) {
			return this.optional(element) ||  /^(?!请输入地址)/i.test(value);
		}, "不能为空");
		$.validator.addMethod("v_va", function(value, element) {
			return this.optional(element) ||  /^(?!表单值)/i.test(value);
		}, "不能为空");
		$.validator.addMethod("k_va", function(value, element) {
			return this.optional(element) ||  /^(?!表单项名称)/i.test(value);
		}, "不能为空");

	})(jQuery);
	
	submitHandler = function(flag){
		
		var DBtype=$("#DBSelectType").val();
		var Linktype=$("#SelectType").find("option:selected").val();
		var checkradio=$("input[type='radio']:checked").val();
		var password=$("#input_password").val();
		if(Linktype==2&&DBtype==0){
			alert("请选择数据库类型");
			return;
		}else{
		
		if(password=="******"){
			$("#input_password").remove();
		}else{
			$("#password").remove();
		}
		if (Linktype==2) {
			$("#webdiv").remove();
			$("#userdiv").remove();
		}else if (Linktype==1) {
			$("#dbdiv").remove();
			if(checkradio==0){
				$("#userdiv").remove();
			};
		} else if (Linktype==3) {
			$("#webdiv").remove();
			$("#dbdiv").remove();
			$("#userdiv").remove();
		}
		
		//$("#saveBtn").attr("disabled","disabled");
		$(":button").attr("disabled","disabled");
		//使用Util.ajax进行操作	
		Util.ajax.loadData("../source/saveKnowledgeSource.html?checkstate="+checkradio, 
				function(data){
					$("#saveBtn").removeAttr("disabled");
					if(!data.code=="ok"){
						alert(data.msg);
					}
					if(flag=="close"){
						alert("保存成功！");
						var id=data.id;
//						$.dialog.data('method')({id:id});
//						art.dialog.close();
						window.opener.location.reload();
		 				window.close();
					}
						$.messager.progress('close');
			}, Util.ajax.serializable($('#form')));
		}
	};
});
