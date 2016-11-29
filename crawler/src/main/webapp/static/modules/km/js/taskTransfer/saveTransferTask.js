/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	var msgForm = null;
	var oldname = null;
	var groupId = null;
	var registerModel = "3";
	
	$(function(){
		// 选择知识源触发要转换的数据
		$("#knowledgeSource").change(function(){
			var sourceId = $("#knowledgeSource").find("option:selected").val();
			var sourceName = $("#knowledgeSource").find("option:selected").text();
			$("#sourceId").val(sourceId);
			$("#sourceName").val(sourceName);
			// 根据知识源 ID 获取采集任务列表
			$.ajax({
				type:'POST',
				url:"../taskJob/getJobListBysouceId.html",
				dataType:'json',
				data:{
					sourceId:sourceId,
					registermodel:'1' // 只要采集任务列表
				},
				success:function(result){
					var taskList = result.taskJobList;
					if(taskList == null || taskList.length == '0'){
						$("#TaskSelect").empty();
						$("#TaskSelect").append("<option value='0'>请选择</opion>");
					}else{
						$("#TaskSelect").empty();
						$("#TaskSelect").append("<option value='0'>请选择</opion>");
						for (var i = 0; i < taskList.length; i ++) {
							/*$("#TaskSelect").append("<option value=" + taskList[i].id + "," + taskList[i].name + ">"
								+ taskList[i].name + " [ " + taskList[i].knowledgeTypeName +"," + taskList[i].knowledgeModelName + " ]" + "</opion>");*/
							$("#TaskSelect").append("<option value=" + taskList[i].id + "," + taskList[i].name + ">"
									+ taskList[i].name + "</opion>");
						}
					}
					tablesclik();
				}
			});
		});
	});
	
	//初始化方法
	exports.init = function(){
		msgForm = $("#form");
		msgForm.validate({
			   rules:{
				   name:{
					   	required:true,
					   	maxlength: 50,remote:{
					   	type:"POST",
					    async:false,
					    url: "../taskJob/validateNameByAjax.html",
	                    data:{name:function(){
	                    	return $("#name").val();
	                    	},
	                    	oldName:oldname,
	                        groupId:groupId,
	                        registerModel:registerModel
	                    	}
				   }},
				   type:{required:true},
				   value:{required:true,maxlength: 50},
				   sortNum:{required:false,digits:true},
				   description :{required:false,maxlength: 1000}
			   },
			   
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
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
	
	submitHandler = function(flag){
		var TaskSelect;
		var editOrsave;
		if($("#id").val()==null||$("#id").val()==""){
			 TaskSelect=$("#TaskSelect").find("option:selected").val();
			 editOrsave="1";
		}else{
			TaskSelect=$("#TaskSelect").val();
			editOrsave="0";
		}
		
		// 选择知识源
		var sourceId = $("#knowledgeSource").find("option:selected").val();
		if(sourceId == 0){
			alert("请选择知识源！");
			return;
		}
		
		/*var systemId=$("#system").find("option:selected").val();
		if(TaskSelect==0){
			alert("请选择要传输的数据！");
			return;
		}
		var connectId=TaskSelect.split(",");
		$.ajax({
			type:'POST',
			url:"../taskJob/validateforTansfer.html",
			dataType:'json',
			data:{
				connectId:connectId[0],
				systemId:systemId,
				editOrsave:editOrsave,
			},
			success:function(result){
				tansferState=result.transferState;
				if(tansferState=="0"){
					alert("该存储系统已被传输!");
					return false;
				}
			}
		});*/
		var systemname;
		if($("#id").val()==null||$("#id").val()==""){
		 systemname=$("#system").find("option:selected").text();
		 $("#systemName").val(systemname);
		}else{
		 systemname=$("#systemName").val();
		 $("#systemName").val(systemname);
		}
		//alert(systemname);
		//是否启用定时
		cheedTrigger=$("#trigger").is(":checked");
		var triggerchecked=null;
		//选择的周数
		var checkWeek="";
		var monthradio="";
		var triggermonth="";
		var triggerday="";
		var triggerweek="";
		var triggerfew="";
		var startTime="";
		var stopTime="";
		if(cheedTrigger){
			triggerchecked=0;
			var checkradio=$("input[type='radio']:checked").val();
			if(checkradio==1){
				startTime=$("#startTime_hour").val();
				stopTime=$("#stopTime_hour").val();
				if(startTime==""||startTime==null||stopTime==""||stopTime==null){
					alert("请选择时间");
					return;
				}
				$("#trigger_timeforday").remove();
				$("#trigger_timeforweek").remove();
				$("#trigger_timeforone").remove();
			}else if(checkradio==2){
				var checkstate = 0;
				var cheed=true;
				startTime=$("#startTime_week").val();
				stopTime=$("#stopTime_week").val();
				$("input[name='triggerWeek']:checkbox").each(function(){ 
					cheed = $(this).is(":checked");
		            if($(this).is(":checked")){
		            	checkWeek += $(this).val()+",";
		            	if(cheed){
		            		checkstate ++;
		     		   }
		            }
		           });
				if(checkstate == 0){
					alert("请选择星期");
					return;
				}else if(startTime==""||startTime==null||stopTime==""||stopTime==null){
					alert("请选择时间");
					return;
				}
				
				$("#trigger_timeforhour").remove();
				$("#trigger_timeforday").remove();
				$("#trigger_timeforone").remove();
			}else if(checkradio==3){
				monthradio=$("input[name='monthType']:checked").val();
				triggermonth=$("#monthformonth").combobox('getValues');
				startTime=$("#startTime_day").val();
				stopTime=$("#stopTime_day").val();
				if(startTime==""||startTime==null||stopTime==""||stopTime==null){
					alert("请选择时间");
					return;
				}
				else if(triggermonth==""||triggermonth==null){
					alert("请选择月份");
					return;
				}else if (monthradio==1) {
					triggerday=$("#monthforday").combobox('getValues');
					if(triggerday==""||triggerday==null){
						alert("请选择日期");
						return;
					}
					$("#monthforweek_div").remove();
				}else if (monthradio==2) {
					triggerfew=$("#monthforweek").combobox('getValues');
					triggerweek=$("#monthforweek2").combobox('getValues');
					if(triggerfew==""||triggerfew==null){
						alert("请选择周期");
						return;
					}else if(triggerweek==""||triggerweek==null){
						alert("请选择星期");
						return;
					}
					$("#monthforday_div").remove();
				}
				$("#trigger_timeforhour").remove();
				$("#trigger_timeforweek").remove();
				$("#trigger_timeforone").remove();
			}else if(checkradio==4){
				startTime=$("#startTime_one").val();
				if(startTime==""||startTime==null){
					alert("请选择时间");
					return;
				}
				$("#trigger_timeforhour").remove();
				$("#trigger_timeforday").remove();
				$("#trigger_timeforweek").remove();
			}
		}else{
			$("#triggerdiv").remove();
		}
	
		$("#saveBtn").attr("disabled","disabled");
		//使用Util.ajax进行操作
		Util.ajax.loadData("../taskJob/saveTransferTaskJob.html?triggerchecked="+triggerchecked+"&checkWeek="+checkWeek+"&monthradio="+monthradio+"&triggermonth="+triggermonth+"&triggerweek="+triggerweek+"&triggerfew="+triggerfew+"&triggerday="+triggerday, 
				function(data){
					$("#saveBtn").removeAttr("disabled");
					if(!data.code=="ok"){
						alert(data.msg);
					}
					if(flag=="close"){
						/* 修改备注（yongqian.liu）：修改为 window.close 方式关闭窗口
						var id=data.id;
		 				$.dialog.data('method')({id:id});
						art.dialog.close();*/
						alert("保存成功！");
		 				window.opener.location.reload();
		 				window.close();
					} 
			}, Util.ajax.serializable($('#form')));
			
	};
});

