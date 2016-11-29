/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var oldname = null;
	 var groupId = null;
	 var registerModel = "2";
	 // 采集任务 ID
	 var transformDataId = null;
	 // 操作标记，add：添加；update：编辑
	 var operationType =$("#operationType").val();
	 
	 /**
	 * 二级联动
	 */
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
					var JobList = result.taskJobList;
					if(JobList == null || JobList.length == '0'){
						$("#transformData").empty();
						$("#transformData").append("<option value='0'>请选择</opion>");
					}else{
						$("#transformData").empty();
						$("#transformData").append("<option value='0'>请选择</opion>");
						for (var i = 0; i < JobList.length; i ++) {
							$("#transformData").append("<option value="+JobList[i].id+">"+JobList[i].name+"</opion>");
						}
					}
					tablesclik();
				}
			});
		});
		
		// 选择要转换的数据动态添加内容规则数据
		$("#transformData").change(function(){
			transformDataId = $("#transformData").find("option:selected").val();
			var transformName = $("#transformData").find("option:selected").text();
			var typeId = $("#knowledgeModel").find("option:selected").val();
			// 采集任务名称赋值
			$("#taskJobName").val(transformName);
			if(transformDataId== 0){
				$("#fieldstable").empty();
				$("#contentRule_div").hide();
			}else{
				$.ajax({
					type:'POST',
					url:"../taskJob/getModelsByTypeId.html",
					dataType:'json',
					data:{
						propertyId:typeId,
						id:transformDataId,
						typeId:typeId
					},
					success:function(result){
						var map =result.resultMap;
						$("#contentRule_div").show(); // 显示内容规则
						$("#fieldstable").empty(); // 清空内容规则数据
						// 初始化内容规则数据
						$("#fieldstable").append("<tr>"
							+"<th style='width:100px;border:1px solid #CCCCCC;'><input type='checkbox' name='checkall' id='checkall'></th>"
							+"<th style='width:150px;border:1px solid #CCCCCC;color:#00F;'>字段</th>"
							+"</tr>"
						);
						for (var int = 0; int < map.length; int++) {
							if(map[int].hide=="true"){
								$("#fieldstable").append("<tr>"
									+"<td style='border:1px solid #CCCCCC;'><input disabled='true' type='checkbox' name='fieldCheck' id='fieldCheck' value="+map[int].key+"></td>"
									+"<td style='border:1px solid #CCCCCC;'><input type='hidden' id='fieldType' name='fieldType' value="+map[int].key+">"+map[int].value+"</td>"
									+"</tr>"
								);							
							}else{
								$("#fieldstable").append("<tr>"
									+"<td style='border:1px solid #CCCCCC;'><input type='checkbox' name='fieldCheck' id='fieldCheck' value="+map[int].key+"></td>"
									+"<td style='border:1px solid #CCCCCC;'><input type='hidden' id='fieldType' name='fieldType' value="+map[int].key+">"+map[int].value+"</td>"
									+"</tr>"
								);		
							}
						}
						tablesclik();
					}
				});
			}
		});
		
		// 选择知识形态触发加载知识模板，初始化内容规则
		$("#knowledgeType").change(function(){
			transformDataId=$("#transformData").find("option:selected").val();
			var typeId=$("#knowledgeType").find("option:selected").val();
			var typeName=$("#knowledgeType").find("option:selected").text();
			$("#knowledgeTypeName").val(typeName);
			$.ajax({
				type:'POST',
				url:"../taskJob/getModelsByTypeId.html",
				dataType:'json',
				data:{
					typeId:typeId,
					id:transformDataId,
					propertyId:"0",
					operationType:operationType
				},
				success:function(result){
					var models = $.parseJSON(result.models);
					if(typeId == '0'){
						$("#knowledgeModel").empty();
						$("#knowledgeModel").append("<option value='0'>请选择</opion>");
					}else{
						$("#knowledgeModel").empty();
						$("#knowledgeModel").append("<option value='0'>请选择</opion>");
						for (var i = 0; i < models.length; i++) {
							$("#knowledgeModel").append("<option value="+models[i].TEPT_ID+">"+models[i].TEPT_NAME+"</opion>");
						}
					}
					var map= result.resultMap;
					$("#fieldstable").empty();
					// 初始化内容规则数据
					$("#fieldstable").append("<tr>"
						+"<th style='width:100px;border:1px solid #CCCCCC;'><input type='checkbox' name='checkall' id='checkall'></th>"
						+"<th style='width:150px;border:1px solid #CCCCCC;color:#00F;'>字段</th>"
						+"</tr>"
					);
					for (var int = 0; int < map.length; int++) {
						$("#fieldstable").append("<tr>"
							+"<td style='border:1px solid #CCCCCC;'><input type='checkbox' name='fieldCheck' id='fieldCheck' value="+map[int].key+"></td>"
							+"<td style='border:1px solid #CCCCCC;'><input type='hidden' id='fieldType' name='fieldType' value="+map[int].key+">"+map[int].value+"</td>"
							+"</tr>"
						);	
					}
					tablesclik();
				}
			});
		});
		
		// 选择知识模板，设置内容规则是否可以选择并初始化
		$("#knowledgeModel").change(function(){
			transformDataId=$("#transformData").val();
			var typeId=$("#knowledgeModel").find("option:selected").val();
			var modelName=$("#knowledgeModel").find("option:selected").text();
			$("#knowledgeModelName").val(modelName);
			$.ajax({
				type:'POST',
				url:"../taskJob/getModelsByTypeId.html",
				dataType:'json',
				data:{
					propertyId:typeId,
					typeId:typeId,
					id:transformDataId,
					operationType:operationType
				},
				success:function(result){
					var map= result.resultMap;
					$("#fieldstable").empty();
					$("#fieldstable").append("<tr>"
						+"<th style='width:100px;border:1px solid #CCCCCC;'><input type='checkbox' name='checkall' id='checkall'></th>"
						+"<th style='width:150px;border:1px solid #CCCCCC;color:#00F;'>字段</th>"
						+"</tr>"
					);
					if(typeId==0){
						for (var int = 0; int < map.length; int++) {							
							$("#fieldstable").append("<tr>"
								+"<td style='border:1px solid #CCCCCC;'><input type='checkbox' name='fieldCheck' id='fieldCheck' value="+map[int].key+"></td>"
								+"<td style='border:1px solid #CCCCCC;'><input type='hidden' id='fieldType' name='fieldType' value="+map[int].key+">"+map[int].value+"</td>"
								+"</tr>"
							);	
						}
					}else{
						for (var int = 0; int < map.length; int++) {
							if(map[int].hide=="false"){
								$("#fieldstable").append("<tr>"
									+"<td style='border:1px solid #CCCCCC;'><input type='checkbox' name='fieldCheck' id='fieldCheck' value="+map[int].key+"></td>"
									+"<td style='border:1px solid #CCCCCC;'><input type='hidden' id='fieldType' name='fieldType' value="+map[int].key+">"+map[int].value+"</td>"
									+"</tr>"
								);				
							}else{
								$("#fieldstable").append("<tr>"
									+"<td style='border:1px solid #CCCCCC;'><input disabled='true' type='checkbox' name='fieldCheck' id='fieldCheck' value="+map[int].key+"></td>"
									+"<td style='border:1px solid #CCCCCC;'><input type='hidden' id='fieldType' name='fieldType' value="+map[int].key+">"+map[int].value+"</td>"
									+"</tr>"
								);		
							}
						}
					}
					tablesclik();
				}
			});
		});
		
		// 选择存储系统
		$("#system").change(function(){
			var systemName=$("#system").find("option:selected").text();
			$("#systemName").val(systemName);
		});
	});

	/*修改备注（yongqian.liu）：注释此功能 属性模板查看
	exports.queryAttribute=function(){
		$("input[name='attributes']").bind("click", function(){  
			var modelId=$("#knowledgeModel").find("option:selected").val();
			if(modelId == 0){
				alert('请选择知识模板！');
				return;
			}else{
				var title="模板属性字段";
				var param="modelId="+modelId;
				require('artDialogJs').openDialog({url:'../taskJob/getModelPropertiesById.html?'+param,title:title,
					width:'300px',
					height:'250px',
				});
			};
		});
	};*/
	
	//初始化方法
	exports.init = function(opts){
		tablesclik();
		msgForm = $("#form");
		oldname = opts.oldname;
		groupId = opts.groupId;
		msgForm.validate({
		   rules:{
			   name:{
				   	required:true,
				   	titleInput:true,
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
		   },
		   submitHandler:function(){
			   submitHandler("close");
		   }
		});
	};
	
	// 提交方法
	submitHandler = function(flag){
		// 选择知识源
		var knowledgeSource = $("#knowledgeSource").find("option:selected").val();
		if(knowledgeSource == 0){
			alert("请选择知识源！");
			return false;
		}
		// 选择知识源
		var transformDataId = $("#transformData").find("option:selected").val();
		if(transformDataId == 0){
			alert("请选择要转换的数据！");
			return false;
		}
		// 选择知识形态
		var knowledgeTypeId= $("#knowledgeType").find("option:selected").val();
		if(knowledgeTypeId == 0){
			alert("请选择知识形态！");
			return false;
		}
		// 选择知识模板
		var knowledgeModelId = $("#knowledgeModel").find("option:selected").val();
		if(knowledgeModelId == 0){
			alert("请选择知识模板！");
			return false;
		}
		// 操作标记，add：添加；update：编辑
		var operationType = $("#operationType").val();
		
		// 获取定时任务
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
					alert("请选择时间!");
					return false;
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
					alert("请选择星期!");
					return false;
				}else if(startTime==""||startTime==null||stopTime==""||stopTime==null){
					alert("请选择时间!");
					return false;
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
					alert("请选择时间!");
					return false;
				}
				else if(triggermonth==""||triggermonth==null){
					alert("请选择月份!");
					return false;
				}else if (monthradio==1) {
					triggerday=$("#monthforday").combobox('getValues');
					if(triggerday==""||triggerday==null){
						alert("请选择日期!");
						return false;
					}
					$("#monthforweek_div").remove();
				}else if (monthradio==2) {
					triggerfew=$("#monthforweek").combobox('getValues');
					triggerweek=$("#monthforweek2").combobox('getValues');
					if(triggerfew==""||triggerfew==null){
						alert("请选择周期!");
						return false;
					}else if(triggerweek==""||triggerweek==null){
						alert("请选择星期!");
						return false;
					}
					$("#monthforday_div").remove();
				}
				$("#trigger_timeforhour").remove();
				$("#trigger_timeforweek").remove();
				$("#trigger_timeforone").remove();
			}else if(checkradio==4){
				startTime=$("#startTime_one").val();
				if(startTime==""||startTime==null){
					alert("请选择时间!");
					return false;
				}
				$("#trigger_timeforhour").remove();
				$("#trigger_timeforday").remove();
				$("#trigger_timeforweek").remove();
			}
		}else{
			$("#triggerdiv").remove();
		}

		var data=new Array();
		var i=0;
		$("input[name='fieldCheck']:checkbox").each(function(){
	        if($(this).is(":checked")){
	         	i++;
	         	var checkValue=$(this).val();
	         	data[i]=checkValue;
	        }
        });
		var extractList = new Array();
		$("input[name='extractList_E']:checked").each(function(){ 
			extractList.push($(this).val());
        });	
		// 修改转换任务功能
		if(operationType=='update'){
			
			//使用Util.ajax进行操作
			Util.ajax.loadData("../taskJob/addTransformTask.html?"+
				"&triggerchecked="+triggerchecked+
				"&checkWeek="+checkWeek+
				"&monthradio="+monthradio+
				"&triggermonth="+triggermonth+
				"&triggerweek="+triggerweek+
				"&triggerfew="+triggerfew+
				"&triggerday="+triggerday+
				"&triggerType="+checkradio+
				"&id="+transformDataId+
				"&typeId="+2+
				"&sourceId="+$("#sourceId").val()+
				"&propertyString="+data+
				"&operationType="+$("#operationType").val()+"&extractList="+extractList,
				function(data){
					$("#saveBtn").removeAttr("disabled");
					if(!data.code=="ok"){
						alert(data.msg);
					}
					if(flag=="close"){
		 				alert("保存成功！");
		 				window.opener.location.reload();
		 				window.close();
					}else{
		 				alert("保存成功，任务已开始！");
		 				window.opener.location.reload();
		 				window.close();
					}
				}, Util.ajax.serializable($('#form')));
			
		// 添加转换任务功能
		}else{
			//验证转换任务名称
			$.ajax({
		 		type: "POST",
		 		url: "../taskJob/verifyTheConversionTaskName.html", 
		 		dataType: "json",
		 		data: {
		 			name:$("#transformData").find("option:selected").text(),
		 			sourceId:$("#sourceId").val(),
		 			knowledgeModel:knowledgeModelId,
		 			registerModel:"2"
		 		},
		 		success: function(result){
		 			if(result.count>0){
		 				alert("该任务名称已使用!");
		 				return false;
		 			}else{
		 				 
		 			//使用Util.ajax进行操作
		 			Util.ajax.loadData("../taskJob/addTransformTask.html?"+		 					
	 					"&triggerchecked="+triggerchecked+
	 					"&checkWeek="+checkWeek+
	 					"&monthradio="+monthradio+
	 					"&triggermonth="+triggermonth+
	 					"&triggerweek="+triggerweek+
	 					"&triggerfew="+triggerfew+
	 					"&triggerday="+triggerday+
	 					"&triggerType="+checkradio+
	 					"&id="+transformDataId+
	 					"&typeId="+2+
	 					"&sourceId="+$("#sourceId").val()+
	 					"&propertyString="+data+
	 					"&operationType="+$("#operationType").val()+
	 					"&transitionId="+$("#transitionId").val()+"&extractList="+extractList,
	 					function(data){
	 						$("#saveBtn").removeAttr("disabled");
	 						if(!data.code=="ok"){
	 							alert(data.msg);
	 						}
	 						if(flag=="close"){
	 							alert("保存成功！");
	 			 				window.opener.location.reload();
	 			 				window.close();
	 						}else{
	 							alert("保存成功，任务已开始！");
	 			 				window.opener.location.reload();
	 			 				window.close();
	 						}
		 				}, Util.ajax.serializable($('#form')));
		 			}
		 		}
			});
		}
	};
	
	/**
	 * 全选功能
	 */
	tablesclik=function(){
		$("#checkall").on("click",function(){
			var cheed = $(this).is(":checked");
			if(cheed){
				$("input[name='fieldCheck']").each(function(){
					if(!$(this).attr("disabled")){
						$(this).prop("checked",true);
					}
				});
			}else{
				$("input[name='fieldCheck']").each(function(){
				   $(this).prop("checked",false);
				});
			}
		});
	};
	//编辑赋值规则
	$(function(){
		var operationType =$("#operationType").val();
		if(operationType=='update'){
			var jobState=$("#jobState").val();
			if(jobState=='2'){//暂停的状态下
				$("#transformData").attr("disabled",true);//要转换的数据
				$("#knowledgeType").attr("disabled",true);//知识形态
				$("#knowledgeModel").attr("disabled",true);//知识模板
				$("#system").attr("disabled",true);//存储系统
				$("input[type='checkbox']").each(function(){//内容规则
					 $(this).prop("disabled",true);
				});
				$("#trigger").attr("disabled",false);//任务定时
			}else if(jobState=='3' || jobState=='0'){//停止或者就緒的状态
				$("#system").attr("disabled",true);//存储系统
				$("#transformData").attr("disabled",true);//要转换的数据
				$("input[name='checkbox']").each(function(){//内容规则
					 $(this).prop("disabled",true);
				});
			}else{
				$("#transformData").attr("disabled",true);//要转换的数据
				$("#knowledgeType").attr("disabled",true);//知识形态
				$("#knowledgeModel").attr("disabled",true);//知识模板
				$("#system").attr("disabled",true);//存储系统
				$("input[type='checkbox']").each(function(){//内容规则
					 $(this).prop("disabled",true);
				});
				$("#trigger").attr("disabled",true);//任务定时
			}
		}
		var modelName=$("#knowledgeModel").find("option:selected").text();//知识模板
		$("#knowledgeModelName").val(modelName);
		
		var typeName=$("#knowledgeType").find("option:selected").text();//知识形态
		$("#knowledgeTypeName").val(typeName);
	});
});


