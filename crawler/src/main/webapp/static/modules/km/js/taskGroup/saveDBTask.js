/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var oldname = null;
	 var groupId = new Array();
	 var registerModel = "1";
	 var arrayProperties = null;
	 
	 exports.tablesclik=$(function(){
			 //checkbox 全选功能
			$("#checkall").click(function(){
				var cheed = $(this).is(":checked");
				if(cheed){
					$("input[name='fieldCheck']").each(function(){
					   $(this).prop("checked",true);
					});
				}else{
					$("input[name='fieldCheck']").each(function(){
					   $(this).prop("checked",false);
					});
				}
			});

			// 附件二进制按钮选择
			$("#isUrl").click(function(){
				var cheed = $(this).is(":checked");
				if(cheed){
					$("#attach").hide();
				}else{
					$("#attach").show();
				};
			});
			
			
		});
		
		tablesclik=function(){
			 //checkbox 全选功能
			$("#checkall").click(function(){
				var cheed = $(this).is(":checked");
				if(cheed){
					$("input[name='fieldCheck']").each(function(){
					   $(this).prop("checked",true);
					});
				}else{
					$("input[name='fieldCheck']").each(function(){
					   $(this).prop("checked",false);
					});
				}
			});

			// 附件二进制按钮选择
			$("#isUrl").click(function(){
				var cheed = $(this).is(":checked");
				if(cheed){
					$("#attach").hide();
				}else{
					$("#attach").show();
				};
			});
			
			
		};
	
		/**
		 * 二级联动
		 */
		$(function(){
			// 选择知识源切换
			$("#knowledgeSource").change(function(){
				var oldSourceId = $("#sourceId").val();
				var oldSourceName = $("#sourceName").val();
				if(oldSourceId == ""){
					oldSourceId="0";
				}
				var sourceId=$("#knowledgeSource").find("option:selected").val();
				if(sourceId=='0'){
					return;
				}
				var sourceName=$("#knowledgeSource").find("option:selected").text();
				$("#sourceId").val(sourceId);
				$("#sourceName").val(sourceName);
				var sourceType = $("#knowledgeSource").find("option:selected").attr("type");
				var linkState = $("#knowledgeSource").find("option:selected").attr("linkState");
				//alert(sourceId+"--"+sourceName+"--"+sourceType+"--"+linkState);
				var url="";
				if(sourceType=="1"){
					// web知识源
					if(linkState=="1"){
						alert("知识源连接失败，无法创建采集任务！");
						$("#knowledgeSource").val(oldSourceId);
						$("#sourceId").val(oldSourceId);
						$("#sourceName").val(oldSourceName);
						return;
					}
					url = '../taskJob/toCrawlerWebJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;
				}else if(sourceType=="2"){
					// db知识源
					if(linkState=="1"){
						alert("知识源连接失败，无法创建采集任务！");
						$("#knowledgeSource").val(oldSourceId);
						$("#sourceId").val(oldSourceId);
						$("#sourceName").val(oldSourceName);
						return;
					}
					url = '../taskJob/toCrawlerDBJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;;
				}else if(sourceType=="3"){
					// 本地知识源
					url = '../taskJob/toCrawlerLocalJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;;
				}
				url=encodeURI(url);
				url=encodeURI(url); 
				window.location=url;
			});
		});
		
	
	//初始化方法
	exports.link=$(function(){
		var type=$("#type").val();
		var url=$("#url").val();
		var user=$("#user").val();
		var password=$("#password").val();
		$("#linkbutton").click(function(){
			$.ajax({
				type:'POST',
				url:"../taskJob/getTablesAndViews.html",
				dataType:'json',
				data:{
					url:url,
					type:type,
					user:user,
					password:password
				},
				success:function(result){
					alert("链接成功");
					$("#TaskSelect").empty();
					$("#TaskSelect").append("<option>"+"请选择"+"</option>");
					var list =result.tables;
					for (var int = 0; int < list.length; int++) {
						$("#TaskSelect").append("<option>"+list[int]+"</option>");
					}
				}
			});
		});
	});
	
	//属性模板查看
	exports.queryAttribute=$(function(){
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
	});
	//属性字段查看
	/*exports.queryAttributetable=$(function(){
		
	});*/
	//内容规则收起展开
	exports.contentRuleShowAndHide=$(function(){
		var aState=false;
		$("#contentRule_a").click(function(){
			if(aState){
				$("#contentRule_div").show();
				$("#contentRule_triangle").empty();
				$("#contentRule_triangle").append("\u25BC");
				aState=false;
			}else{
				$("#contentRule_div").hide();
				$("#contentRule_triangle").empty();
				$("#contentRule_triangle").append("\u25B2");
				aState=true;
			}
			
		});
	});
	exports.attachmentRuleShowAndHide2=$(function(){
		var aState=false;
		$("#attachmentRule_a").click(function(){
			if(aState){
				$("#attachmentRule_div").show();
				$("#attachmentRule_triangle").empty();
				$("#attachmentRule_triangle").append("\u25BC");
				aState=false;
			}else{
				$("#attachmentRule_div").hide();
				$("#attachmentRule_triangle").empty();
				$("#attachmentRule_triangle").append("\u25B2");
				aState=true;
			}
			
		});
	});
	exports.timingRuleShowAndHide2=$(function(){
		var aState=true;
		$("#timingRule_a").click(function(){
			if(aState){
				$("#timingRule_div").show();
				$("#timingRule_triangle").empty();
				$("#timingRule_triangle").append("\u25BC");
				aState=false;
			}else{
				$("#timingRule_div").hide();
				$("#timingRule_triangle").empty();
				$("#timingRule_triangle").append("\u25B2");
				aState=true;
			}
			
		});
	});
	//已设置属性字段个数
	exports.FiledSelect=$(function(){
		//alert("gyyyy");
		$("input[name='fieldCheck']").click(function(){
			var count=0;
			//alert("ggggg");
			$("input[name='fieldCheck']").each(function(){
				cheed = $(this).is(":checked");
			   if(cheed){
				   count ++;
			   }
			});
			$("#filedselect").empty();
			$("#filedselect").append(count);
		});
		
	});
	exports.changeSelect=$(function(){
		var sourceId=$("#sourceId").val();
		$("#TaskSelect").change(function(){
			var checkText=$("#TaskSelect").find("option:selected").text();
			$.ajax({
				async: false,
				type:'POST',
				url:"../taskJob/getFields.html",
				dataType:'json',
				data:{
					sourceId:sourceId,
					entityname:checkText
				},
				success:function(result){
					var list =result.fields;
					//var properties=$.parseJSON(result.properties);
					$("#fieldstable").empty();
					$("#attachmentName").empty();
					$("#attachmentFileName").empty();
					$("#attachmentFileType").empty();
					$("#attachmentAs").empty();
					$("#pkid").empty();
					$("#filedcount").empty();
					$("#filedselect").empty();
					$("#filedselect").append("0");
					/*for (var int1 = 0; int1 < properties.length; int1++) {
						$("#attachmentAs").append("<option value='"+properties[int1].key+"'>"+properties[int1].value+"</opion>");
					}*/
					
					/*$("#fieldstable").append("<tr>"
							+"<th width='70px' align='center'><input type='checkbox' name='checkall' id='checkall'></th>"
							+"<th width='250px'>别名</th>"
							+"<th width='220px'>表列名</th>"
							+"<th width='170px'>表类型</th>"
							+"</tr>");*/
					//增加数据库字段总个数
					$("#filedcount").append(list.length);
					//列表样式更改
					$("#fieldstable").append("<tr align='center'>"
							+"<th width='165px' ><span style='color:blue;'>对应列名称</span></th>"
							+"<th width='205px' ><span style='color:blue;'>字段</span></th>"
							//+"<th width='170px'>表类型</th>"
							+"<th width='50px' ><span style='color:blue;'>操作</span></th>"
							+"</tr>");
					$("#attachmentName").append(
							"<option value='0'>请选择</option>"
					);
					$("#attachmentFileName").append(
							"<option value='0'>请选择</option>"
					);
					$("#attachmentFileType").append(
							"<option value='0'>请选择</option>"
					);
					for (var int = 0; int < list.length; int++) {
						
						/*$("#fieldstable").append("<tr><td align='center'>"
								+"<input type='checkbox' name='fieldCheck' id='fieldCheck' value='"+list[int].fieldName+";"+list[int].fieldType+"'>"
								+"</td>"
								+"<td align='center'>"
								+"<input name='1"+list[int].fieldName+"' style='width: 200px;' id='1"+list[int].fieldName+"'>"
								+"</td>"
								+"<td align='center'>"
								+"<input type='hidden' id='fieldName' name='fieldName' value='"+list[int].fieldName+"'>"+list[int].fieldName
								+"</td>"
								+"<td align='center'>"
								+"<input type='hidden' id='fieldType' name='fieldType' value='"+list[int].fieldType+"'>"+list[int].fieldType
								+"</td></tr>");*/
						//列表样式修改
						$("#fieldstable").append("<tr>"
								+"<td >"
								+"<input type='hidden' id='fieldName' name='fieldName' value='"+list[int].fieldName+"'>"+list[int].fieldName
								+"</td>"
								+"<td >"
								+"<input name='1"+list[int].fieldName+"' id='1"+list[int].fieldName+"' type='text' class='txt'>"
								+"</td>"
								+"<td align='center'>"
								+"&nbsp;&nbsp;<input type='checkbox' name='fieldCheck' id='fieldCheck' value='"+list[int].fieldName+";"+list[int].fieldType+"'>"
								+"</td></tr>");		
						$("#attachmentName").append(
								"<option value='"+list[int].fieldName+"'>"
								+list[int].fieldName
								+"</option>"
						);
						
						$("#attachmentFileName").append(
								"<option value='"+list[int].fieldName+"'>"
								+list[int].fieldName
								+"</option>"
						);
						
						$("#attachmentFileType").append(
								"<option value='"+list[int].fieldName+"'>"
								+list[int].fieldName
								+"</option>"
						);
						
						$("#pkid").append(
								"<option value='"+list[int].fieldName+"'>"
								+list[int].fieldName
								+"</option>"
						);
						
					}	
					
					/*var url = "../taskJob/getproperties.html";
							var obj = eval(kmProperties); 
							$("#attachmentAs").combobox({
								data : obj,
								valueField:'PROP_ID',
								textField:'PROP_NAME',
								mode:'local',
								//editable:true,
								//hasDownArrow:false ,
								filter: function(q, row){
									var opts = $(this).combobox('options');
									return row[opts.textField].indexOf(q)==0;
								},
								formatter: function(row){
									var opts = $(this).combobox('options');
									return row[opts.textField];
								},
								 onLoadSuccess: function () { //数据加载完毕事件
						                     $("#cc").combobox('select', "作者");
						             }
									
								});*/
					/*for (var int = 0; int < list.length; int++) {
						
						$("#1"+list[int].fieldName).combobox({
						data : obj,
						valueField:'PROP_ID',
						textField:'PROP_NAME',
						mode:'local',
						filter: function(q, row){
							var opts = $(this).combobox('options');
							return row[opts.textField].indexOf(q)==0;
						},
						formatter: function(row){
							var opts = $(this).combobox('options');
							return row[opts.textField];
						},
						});
					}*/
//						});
					
					tablesclik();
				}
				
				
			});
			
			$("input[name='fieldCheck']").click(function(){
				var count=0;
				$("input[name='fieldCheck']").each(function(){
					cheed = $(this).is(":checked");
				   if(cheed){
					   count ++;
				   }
				});
				$("#filedselect").empty();
				$("#filedselect").append(count);
			});
		});
	});
	exports.isAttachment = $(function(){
		$("#isAttachment").on("click",function(){
			var check=$("#isAttachment").is(":checked");
			if(check){
				$("#isUrl").removeAttr("disabled");
				$("#attachmentAs").removeAttr("disabled");
				$("#attachmentName").removeAttr("disabled");
				$("#attachmentFileName").removeAttr("disabled");
				$("#attachmentFileType").removeAttr("disabled");
			}else{
				$("#isUrl").attr("disabled","disabled");
				$("#attachmentAs").attr("disabled","disabled");
				$("#attachmentName").attr("disabled","disabled");
				$("#attachmentFileName").attr("disabled","disabled");
				$("#attachmentFileType").attr("disabled","disabled");
			}
		});
	});
	$(function(){
		$("#isAttachment").click(function(){
			if($("#isAttachment").is(":checked")){
				$("#attachdiv").show();
			}else{
				$("#attachdiv").hide();
			}
		});
	});
	
	initButtonEvent=$(function(){
		$("#spiderTest").on("click",function(){
			return;
			$("#spiderTest").attr("disabled","disabled");
			//使用Util.ajax进行操作
			Util.ajax.loadData("../taskJob/spiderTest.html", 
					function(data){
						$("#spiderTest").removeAttr("disabled");
						if(!data.code=="ok"){
							alert(data.msg);
						}
				}, Util.ajax.serializable($('#form')));
		});
		$("#saveAndStart").click(function(){
			submitHandler("startAndclose");
		});
		$("#saveAndTest").click(function(){
			submitHandler("saveAndTest");
		});
	});
	exports.init = function(opts){
		msgForm = $("#form");
		oldname = opts.oldname;
		groupId = opts.groupId;
		saveid=opts.saveid;
		/*kmProperties=$.parseJSON(opts.kmProperties);
		inherentProp=$.parseJSON(opts.inherentProp);
		//属性全集
		arrayProperties=new Array();
		//固有属性
		arrayinherentProp=new Array();
		for (var int1 = 0; int1 < kmProperties.length; int1++) {
			arrayProperties[int1]=kmProperties[int1].PROP_NAME;
		}
		for (var int2 = 0; int2 < inherentProp.length; int2++) {
			arrayinherentProp[int2]=inherentProp[int2].PROP_NAME+"(INHERENT)";
		}*/
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
				   /*type:{required:true},
				   value:{required:true,maxlength: 50},
				   sortNum:{required:false,digits:true},
				   description :{required:false,maxlength: 1000}*/
			   },
			   
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
	};
	submitHandler = function(flag){
		var count = 0;
		var cheed = true;
		var Attachment=$("#isAttachment").is(":checked");
		$("input[name='fieldCheck']").each(function(){
			cheed = $(this).is(":checked");
		   if(cheed){
			   count ++;
		   }
		});
		/*if($("#knowledgeSource").find("option:selected").val() == 0){
			alert('请选择知识源！');
			return;
		}
		if($("#knowledgeType").find("option:selected").val() == 0){
			alert('请选择知识形态！');
			return;
		}
		if($("#knowledgeModel").find("option:selected").val() == 0){
			alert('请选择知识模板！');
			return;
		}
		if($("#system").find("option:selected").val() == 0){
			alert('请选择存储系统！');
			return;
		}*/
		if(Attachment){
			var AttachmentValue=$("#attachmentAs").val();
			var attachmentName =$("#attachmentName").val();
			var isUrl = $("#isUrl").is(":checked");
			if(AttachmentValue==null||AttachmentValue==""||attachmentName==0){
				alert("请填写完整的附件规则");
				return;
			}
			if(!isUrl){
				var attachmentFileName=$("#attachmentFileName").val();
				var attachmentFileType=$("#attachmentFileType").val();
				if(attachmentFileType==0||attachmentFileName==0){
					alert("请填写完整的附件规则");
					return;
				}
			}
		}
		if(count == 0){
			alert("最少采集一列属性！");
			return ;
		}
		else{
			var selectCheck=true;
			/*$("input[name='fieldCheck']:checkbox").each(function(){ 
	            if($(this).is(":checked")){
	            	var checkValue=$(this).val().split(";");
					var selectValue=$("#1"+checkValue[0]).combobox('getText');
					var arraystate=true;
					for (var int1 = 0; int1 < arrayinherentProp.length; int1++) {
						if(arrayinherentProp[int1]==selectValue){
							propstate=false;
							break;
						}
					}
	            	for (var int1 = 0; int1 < arrayProperties.length; int1++) {
						if(arrayProperties[int1]==selectValue){
							arraystate=false;
							break;
						}
					}
	            	
	            	if(selectValue==""||selectValue==null){
	            		alert("请选择字段名称");
	            		selectCheck=false;	
	            		return false;
	            	}else if(arraystate){
	            		alert("请选择正确的字段名称！");
	            		selectCheck=false;
	            		return false;
	            	}
	            }
	        });
			if (propstate){
        		alert("至少设置一条基本属性的采集内容规则！");
        		selectCheck=false;
        		return false;
        	}*/
			};
			if (selectCheck) {
			// 是否有附件属性
			cheed = $("#isAttachment").is(":checked");
			if(cheed){
				// 是否为URL字段列
				cheed = $("#isUrl").is(":checked");
				if(cheed){
					//alert("进入remove");
					$("#attach").remove();
				}
			}else{
				// 没有附件，则直接删除属性值
				$("#attachdiv").remove();
			}
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
		var fieldCheck="";
		var AttributesNameArray="";
		$("input[name='fieldCheck']:checkbox").each(function(){ 
            if($(this).is(":checked")){
            	var checkValue=$(this).val().split(";");
            	/*var selectValue=$("#1"+checkValue[0]).combobox('getValue');
            	var selectText = $("#1"+checkValue[0]).combobox('getText');*/
            	var selectValue=$("#1"+checkValue[0]).val();
            	AttributesNameArray += selectValue+";";
            	fieldCheck += $(this).val()+";"+selectValue+",";
            }
        });
		var field=fieldCheck.split(",");
		//alert(AttributesNameArray);
		//alert(AttributesNameArray.split(";"));
		
		//附件抽取
		var extractList = new Array();
		$("input[name='extractListDB']:checked").each(function(){ 
			extractList.push($(this).val());
        });
		var startTask = 0;
		if(flag=="close"){	
			$("#saveBtn").attr("disabled","disabled");
			$("#saveAndStart").attr("disabled","disabled");
		}else if(flag=="saveAndTest"){
			$("#saveAndTest").attr("disabled","disabled");
		}else{
			$("#saveAndStart").attr("disabled","disabled");
			$("#saveBtn").attr("disabled","disabled");
			startTask = 1;
		}
		
		//采集测试的ajax请求
		if(flag=="saveAndTest"){
			var url="../taskJob/DBSpiderTest.html?field="+field+"&AttributesNameArray="+AttributesNameArray;
			url=encodeURI(url); 
			url=encodeURI(url); 
			Util.ajax.loadData(url, 
					function(data){
				$("#saveAndTest").removeAttr("disabled");
				if(data!=null){
					showBg(data.titleList,data.contentList);
				}else{
					alert("采集异常");
				}
						
				}, Util.ajax.serializable($('#form')));
		}else{
		
		//使用Util.ajax进行操作
		var url="../taskJob/saveDBTaskJob.html?field="+field+"&extractList="+extractList+"&triggerchecked="+triggerchecked+"&checkWeek="+checkWeek+"&monthradio="+monthradio+"&triggermonth="+triggermonth+"&triggerweek="+triggerweek+"&triggerfew="+triggerfew+"&triggerday="+triggerday+"&startTask="+startTask;
		url=encodeURI(url); 
		url=encodeURI(url); 
		Util.ajax.loadData(url, 
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
						//var id=data.id;
		 				//$.dialog.data('method')({id:id});
						alert("保存成功，任务已开始！");
						window.opener.location.reload();
		 				window.close();
		 				//art.dialog.close();
					}
			}, Util.ajax.serializable($('#form')));};
	    };
	};
});

//显示灰色 jQuery 遮罩层
function showBg(titleList,contentList) {
var bh = $("body").height();
var bw = $("body").width();
$("#fullbg").css({
height:bh,
width:bw,
display:"block"
});
$("#testContent").empty();// 清空数据  
//$("#testContent").append(content);


// $("#testContent").append("<table style='margin-left:20px;width:78%; border-collapse: collapse;border-width:1px;'>");
// $("#testContent").append("<tr class='tableContent_tr'>");
// $("#testContent").append("<td class='tableContent_th'><h4 style='text-align:center;'>序号</h4></td>");
var tableWidth = titleList.length * 200 + 40;
var oTable = $('<table style="width:'+tableWidth+'px;border:solid 1px #F2F2F2;"></table>');
var oTr = $('<tr style="color:#0000FF;background-color:#F9F9F9;"></tr>');
var oTh = $('<td style="width:40px">序号</td>');

oTr.append(oTh);

for (var int = 0; int < titleList.length; int++) {
	var titleStr = titleList[int];
	if(titleStr.length>12){
		titleStr = titleStr.substring(0,12)+"…";;
	}
	oTr.append(
			"<td style='width:200px'>"+titleStr+"</td>"
	);
}

oTable.append(oTr);



for (var int2 = 0; int2 < contentList.length; int2++) {
	var oTr2 = $("<tr></tr>");
	oTable.append(oTr2);
	oTr2.append("<td style='width:40px;'>"+(int2+1)+"</td>");
	for (var int3 = 0; int3 < contentList[int2].length; int3++) {
		var contentStr = contentList[int2][int3];
		if(contentStr!=null&&contentStr.length>12){
			contentStr = contentStr.substring(0,12)+"…";
		}
		if(contentStr==null){
			oTr2.append("<td style='width:200px;'></td>");
		}else{
			oTr2.append("<td style='width:200px;'>"+contentStr+"</td>");
		}
		
	};
}

$('#testContent').append(oTable);
$("#dialog").show();
}
//关闭灰色 jQuery 遮罩
function closeBg() {
$("#fullbg,#dialog").hide();
} 
