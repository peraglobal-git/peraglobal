/**
 * 保存web采集页面
 */
define(function(require,exports,module){
	 var msgForm = null;
	 var name = null;
	 var groupId = null;
	 var registerModel = "1";
	/* var kmProperties=null;
	 var obj_json=null;*/
	 var contentIndex;
	 var contentIdsArray = new Array();
		
	//初始化方法
	exports.init = function(opts){
		msgForm = $("#form");
		oldname = opts.oldname;
		groupId = opts.groupId;
		contentIndex = opts.ruleArrayLength;
//		alert(contentIndex);
		for(var i = 1; i < contentIndex; i++){
			contentIdsArray.push(i);
		}
		/*obj_json = eval(opts.kmProperties); 
		kmProperties=$.parseJSON(opts.kmProperties);
		arrayProperties=new Array();
		for (var int1 = 0; int1 < kmProperties.length; int1++) {
			arrayProperties[int1]=kmProperties[int1].PROP_NAME;
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
				   seed_url:{required:true},
				   list_url:{required:true},
				   detail_url:{required:true},
				 //  detail_content:{required:true},
//				   detail_content_Val:{required:true},
				   threadNum:{required:true,digits:true}
			   },
			   
			   submitHandler:function(){
				   submitHandler("close");
			   }
			});
//		operateContent();
		initButtonEvent(contentIdsArray);
		Array.prototype.indexOf = function(val) {
            for (var i = 0; i < this.length; i++) {
                if (this[i] == val) return i;
            }
            return -1;
        };
        Array.prototype.remove = function(val) {
            var index = this.indexOf(val);
            if (index > -1) {
                this.splice(index, 1);
            }
        };
	};
	
	//属性字段查看
	exports.queryAttributetable=$(function(){
		$("#knowledgeType").change(function(){
				$("#attributetable").empty();
		});
		$("#knowledgeModel").change(function(){
			var modelId=$("#knowledgeModel").find("option:selected").val();
			if(modelId==0){
				$("#attributetable").empty();
			}else{
				$.ajax({
					type:'POST',
					url:"../taskJob/getAttrubuteById.html",
					dataType:'json',
					data:{
						modelId:modelId
					},
					success:function(result){
						var properties=$.parseJSON(result.properties);
						$("#attributetable").empty();
						$("#attributetable").append("<tr>"
								+"<td width='60px' class='attributetd' align='center'>扩展属性</td>"
								+"<td class='attributetd' align='center'>属性名</td></tr>"
								);
						for (var int = 0; int < properties.length; int++) {
							$("#attributetable").append("<tr>"
									+"<td class='attributetd' align='center'></td><td class='attributetd' align='center'>"+properties[int].PROP_NAME+"</td></tr>"
									);
						}
					}
				});
			}
		});
	});
	
	// 网址规则展开、收起 add by yangxc
	exports.urlRuleShowAndHide=$(function(){
		var aState=false;
		$("#urlRule_a").click(function(){
			if(aState){
				$("#urlRule_div").show();
				$("#urlRule_triangle").empty();
				$("#urlRule_triangle").append("\u25BC");
				aState=false;
			}else{
				$("#urlRule_div").hide();
				$("#urlRule_triangle").empty();
				$("#urlRule_triangle").append("\u25B2");
				aState=true;
			}
			
		});
	});
	
	// 内容规则展开、收起 add by yangxc
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
	
	// 附件抽取规则展开、收起 add by yangxc
	exports.attachmentRuleShowAndHide3=$(function(){
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
	
	// 高级设置展开、收起 add by yangxc
	exports.timingRuleShowAndHide2=$(function(){
		var aState=true;
		$("#advancedSetting_a").click(function(){
			if(aState){
				$("#advancedSetting_div").show();
				$("#advancedSetting_triangle").empty();
				$("#advancedSetting_triangle").append("\u25BC");
				aState=false;
			}else{
				$("#advancedSetting_div").hide();
				$("#advancedSetting_triangle").empty();
				$("#advancedSetting_triangle").append("\u25B2");
				aState=true;
			}
			
		});
	});
	
	exports.ifDownloadAttachment = $(function(){
		$("#downloadAttachment").on("click",function(){
			var check=$("#downloadAttachment").is(":checked");
			if(check){
				$("#attachment_content_Val").removeAttr("disabled");
				$("#attachment_content_type").removeAttr("disabled");
			}else{
				$("#attachment_content_Val").attr("disabled","disabled");
				$("#attachment_content_type").attr("disabled","disabled");
			}
		});
	});
	
	$(function(){
		// 增加一行
		$("#addContent").bind('click', function() {
			var divTest = $("#contentRuleCopy");
			/*var newDiv = divTest.clone(true);
			newDiv.css({"display":"#block"});
			if(newDiv.find("#detail_content_Val")!=undefined){
				newDiv.find("#detail_content_Val").val("");
			}
			if(newDiv.find("#detail_content")!=undefined){
				var inputContent1='<input id="'+contentIndex+'_detail_content_Val" name="detail_content_Val" type="text" class="txt" />';
				newDiv.find('td:nth-child(1)').html('');
				newDiv.find('td:nth-child(1)').prepend(inputContent1);
				var inputContent2="";
				newDiv.find('td:nth-child(2)').html('<select id="'+contentIndex+'_detail_content_type" name="detail_content_type" style="width:100px;">'
						+'<option value="xpath">XPATH</option>'
						+'<option value="regex">正则表达式</option>'
						+'<option value="css">CSS</option></select>');
				newDiv.find('td:nth-child(2)').prepend(inputContent2);
				var inputContent3 = "<input name='detail_content' id='"+contentIndex+"_detail_content'  style='width:100px;height:30px' />";
				newDiv.find('td:nth-child(3)').html('');
				newDiv.find('td:nth-child(3)').prepend(inputContent3);
				var inputContent4 = "<input type='checkbox' id='"+contentIndex+"_rc' name='contentRuleCheck' value='"+contentIndex+"'/>";
				newDiv.find('td:nth-child(4)').html('');
				newDiv.find('td:nth-child(4)').prepend(inputContent4);
			}*/
			var newDiv1 = $('<tr></tr>');
			var td1 = $('<td><input id="'+contentIndex+'_detail_content_Val" name="detail_content_Val" type="text" class="txt" /></td>');
			var td2 = $('<td><select id="'+contentIndex+'_detail_content_type" name="detail_content_type" style="width:100px;">'
					+'<option value="xpath">XPATH</option>'
					+'<option value="regex">正则表达式</option>'
					+'<option value="css">CSS</option></select></td>');
			var td3 = $('<td><input type="text" name="detail_content" id="'+contentIndex+'_detail_content" class="txt"/></td>');
			var td4 = $('<td><input type="checkbox" id="'+contentIndex+'_rc" name="contentRuleCheck" value="'+contentIndex+'"/></td>');
			newDiv1.append(td1);
			newDiv1.append(td2);
			newDiv1.append(td3);
			newDiv1.append(td4);
	        divTest.parent().append(newDiv1);
	        contentIdsArray.push(contentIndex);
	        var detail_content= contentIndex+'_detail_content';
//	        alert(detail_content);
	       /* $("#"+detail_content).combobox({
				data : obj_json,
				valueField:'PROP_ID',
				textField:'PROP_NAME',
				mode:'local',
				//editable:true,
				//hasDownArrow:false ,
				filter: function(q, row){
					var opts = $(this).combobox('options');
					var filterStr = row[opts.textField];
					return filterStr.indexOf(q)==0;
				},
				formatter: function(row){
					var opts = $(this).combobox('options');
					return row[opts.textField];
				},
				});*/
	    	contentIndex++;
	    	$("input[name='contentRuleCheck']").bind('click', function() {
				var count=0;
				$("input[name='contentRuleCheck']").each(function(){
					cheed = $(this).is(":checked");
				   if(cheed){
					   count ++;
				   }
				});
				$("#filedcount").empty();
				$("#filedcount").append(count);
			});
		});
		$("input[name='contentRuleCheck']").bind('click', function() {
			var count=0;
			$("input[name='contentRuleCheck']").each(function(){
				cheed = $(this).is(":checked");
			   if(cheed){
				   count ++;
			   }
			});
			$("#filedcount").empty();
			$("#filedcount").append(count);
		});
		
		// 删除div
		$("input[name='del']").bind('click',function() {
			if($(this).parent().find("#detail_content_Val").val()!=undefined && $("input[name='detail_content_Val']").length==1){
				alert("请至少保留一个内容规则！");
				return;
			}
			$(this).parent().parent().remove();
		});
		
		// 复制div
		$("input[name='add']").bind('click', function() {
			var divTest = $(this).parent().parent();
	        var newDiv = divTest.clone(true);
	        divTest.after(newDiv);
		});
	});
	/**
	 * 二级联动
	 */
	$(function(){
		function RndNum(n){
		  var rnd="";
		  for(var i=0;i<n;i++)
		     rnd+=Math.floor(Math.random()*10);
		  return rnd;
		}
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
				// db知识源
				if(linkState=="1"){
					alert("知识源连接失败，无法创建采集任务！");
					$("#knowledgeSource").val(oldSourceId);
					$("#sourceId").val(oldSourceId);
					$("#sourceName").val(oldSourceName);
					return;
				}
				var d = new Date();
				var vYear = d.getFullYear();
				var vMon = d.getMonth() + 1;
				var vDay = d.getDate(); 
				var taskName=vYear+(vMon<10 ? "0" + vMon : vMon)+(vDay<10 ? "0"+ vDay : vDay);
				taskName = sourceName+'_'+taskName+'_'+RndNum(3);
				$("#name").val(taskName);
				// 初始化不显示抽取规则和高级设置 
				/*$("#attachmentRule_div").hide();
				$("#attachmentRule_triangle").empty();
				$("#attachmentRule_triangle").append("\u25B2");
				$("#advancedSetting_div").hide();
				$("#advancedSetting_triangle").empty();
				$("#advancedSetting_triangle").append("\u25B2");*/
			}else if(sourceType=="2"){
				// db知识源
				if(linkState=="1"){
					alert("知识源连接失败，无法创建采集任务！");
					$("#knowledgeSource").val(oldSourceId);
					$("#sourceId").val(oldSourceId);
					$("#sourceName").val(oldSourceName);
					return;
				}
				url = '../taskJob/toCrawlerDBJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;
				url=encodeURI(url);
				url=encodeURI(url); 
				window.location=url;
			}else if(sourceType=="3"){
				// 本地知识源
				url = '../taskJob/toCrawlerLocalJobPage.html?sourceId='+sourceId+'&registerType=3'+'&sourceName='+sourceName;;
				url=encodeURI(url);
				url=encodeURI(url); 
				window.location=url;
			}
		});
	});
	submitHandler = function(flag){
		var submitFlg = true;
		var detail_content_Val = new Array();
		var detail_content_type = new Array();
		var detail_content = new Array();
		$('input[name="contentRuleCheck"]:checked').each(function(){
			var contentRuleId = $(this).val();
			var detailConVal = $("#"+contentRuleId+"_detail_content_Val").val();
			if(detailConVal == ''){
				detail_content_Val.push("null");
			}else{
				detail_content_Val.push(detailConVal);
			}
			detail_content_type.push($("#"+contentRuleId+"_detail_content_type").val());
			if($("#"+contentRuleId+"_detail_content").val()!=""){
				detail_content.push($("#"+contentRuleId+"_detail_content").val());
			}
		});
		if(!submitFlg){
			return;
		}
		// 是否下载附件
		var isDownloadAtta = $("#downloadAttachment").is(":checked");
		if(isDownloadAtta && $("#attachment_content_Val").val()==''){
			alert('请输入附件下载规则！');
			return;
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
		var extractList = new Array();
		$("input[name='extractList']:checked").each(function(){ 
			extractList.push($(this).val());
        });
		var startTask = 0;
		$("#saveBtn").attr("disabled","disabled");
		$("#saveAndStart").attr("disabled","disabled");
		if(flag=="startAndclose"){	
			startTask = 1;
		}
		
		// 内容字段key
		/*var detail_content = "";
		
		for (var int = 0; int < contentIdsArray.length; int++) {
			if(contentIdsArray[int]==''){
				 continue;
			}
			detail_content +=$("#"+ contentIdsArray[int] +"_detail_content").combobox('getValue')+",";
		}
		detail_content = detail_content.substring(0, detail_content.length-1);
		// 内容字段值
		var detail_content_Val = "";
		$("input[name='detail_content_Val']").each(function(){ 
			detail_content_Val += $(this).val()+",";
        });
		detail_content_Val = detail_content_Val.substring(0, detail_content_Val.length-1);
		// 内容字段类型
		var detail_content_type = "";
		$("select[name='detail_content_type'] option:selected").each(function(){ 
			detail_content_type += $(this).val()+",";
        });
		detail_content_type = detail_content_type.substring(0, detail_content_type.length-1);*/
		//代理服务器ip
		var ipAddress = "";
		$("input[name='ipAddress']").each(function(){ 
			ipAddress += $(this).val()+",";
        });
		ipAddress = ipAddress.substring(0, ipAddress.length-1);
		//代理服务器端口
		var ipPort = "";
		$("input[name='ipPort']").each(function(){ 
			ipPort += $(this).val()+",";
        });
		ipPort = ipPort.substring(0, ipPort.length-1);
		//使用Util.ajax进行操作
		var url = "../taskJob/saveWebTaskJob.html?ipAddress="+ipAddress+"&ipPort="+ipPort+"&detail_content_Val="+detail_content_Val+"&detail_content="+detail_content+"&detail_content_type="+detail_content_type+"&triggerchecked="+triggerchecked+"&checkWeek="+checkWeek+"&monthradio="+monthradio+"&triggermonth="+triggermonth+"&triggerweek="+triggerweek+"&triggerfew="+triggerfew+"&triggerday="+triggerday+"&startTask="+startTask+"&extractList="+extractList;
		url=encodeURI(url);
		url=encodeURI(url); 
		Util.ajax.loadData(url, 
				function(data){
					$("#saveBtn").removeAttr("disabled");
					$("#saveAndStart").removeAttr("disabled");
					if(!data.code=="ok"){
						alert(data.msg);
					}
					if(flag=="close"){
//						var id=data.id;
//		 				$.dialog.data('method')({id:id});
		 				alert("保存成功！");
//						art.dialog.close();
		 				window.opener.location.reload();
		 				window.close();
					}else{
//						var id=data.id;
//		 				$.dialog.data('method')({id:id});
						alert("保存成功，任务已开始！");
//		 				art.dialog.close();
						window.opener.location.reload();
		 				window.close();
					}
			}, Util.ajax.serializable($('#form')));
	};
});

initButtonEvent = function(contentIdsArray){
	$("#spiderTest").on("click",function(){
//		$("#spiderTest").attr("disabled","disabled");
		// 起始网址 
		if($("textarea[name='seed_url']").val() == ''){
			alert("请输入起始网址！");
			return;
		}
		// 列表网址 
		if($("input[id='list_url']").val() == ''){
			alert("请输入列表网址规则！");
			return;
		}
		// 详情页网址网址 
		if($("input[id='detail_url']").val() == ''){
			alert("请输入详情网址规则！");
			return;
		}
		var submitFlg = true;
		var hasInherentProp = false;
		var detail_content_Val = new Array();
		var detail_content_type = new Array();
		var detail_content = new Array();
		var detail_content_Name = new Array();
		$('input[name="contentRuleCheck"]:checked').each(function(){
			var contentRuleId = $(this).val();
			var detailConVal = $("#"+contentRuleId+"_detail_content_Val").val();
			if(detailConVal == ''){
				detail_content_Val.push("null");
			}else{
				detail_content_Val.push(detailConVal);
			}
			detail_content_type.push($("#"+contentRuleId+"_detail_content_type").val());
			
			if($("#"+ contentRuleId +"_detail_content").combobox('getValue')==''){
				alert('请选择字段！');
				submitFlg = false;
				return false;
			}
			detail_content.push($("#"+ contentRuleId +"_detail_content").combobox('getValue'));
			detail_content_Name.push($("#"+ contentRuleId +"_detail_content").combobox('getText'));
		});
		alert('detail_content:'+detail_content);
		alert('detail_content_Name:'+detail_content_Name);
		if(!submitFlg){
			return;
		}
		/*if(!hasInherentProp){
			alert('至少要设置一条基本属性的采集内容规则！');
			return;
		}*/
		//代理服务器ip
		var ipAddress = "";
		$("input[name='ipAddress']").each(function(){ 
			ipAddress += $(this).val()+",";
        });
		ipAddress = ipAddress.substring(0, ipAddress.length-1);
		//代理服务器端口
		var ipPort = "";
		$("input[name='ipPort']").each(function(){ 
			ipPort += $(this).val()+",";
        });
		ipPort = ipPort.substring(0, ipPort.length-1);
		$("#spiderTest").attr("disabled","disabled");
		//使用Util.ajax进行操作
		detail_content_Name=encodeURI(detail_content_Name);
		detail_content_Name=encodeURI(detail_content_Name);
//		alert(detail_content_Name);
		var url = "../taskJob/spiderTest.html?ipAddress="+ipAddress+"&ipPort="+ipPort+"&detail_content_Val="+detail_content_Val+"&detail_content="+detail_content+"&detail_content_Name="+detail_content_Name+"&detail_content_type="+detail_content_type;
		Util.ajax.loadData(url,
				function(data){
//					$("#spiderTest").removeAttr("disabled");
					if(!data.code=="ok"){
						alert(data.uuid);
					}
					$("#spiderTest").removeAttr("disabled");
//					alert(data.result);
					
					//openDialog('dialog',data.result);
					showBg(data.result);
			}, Util.ajax.serializable($('#form')));
	});
	$("#saveAndStart").on("click",function(){
		submitHandler("startAndclose");
	});
};
//显示灰色 jQuery 遮罩层
function showBg(content) {
var bh = $("body").height();
var bw = $("body").width();
$("#fullbg").css({
height:bh,
width:bw,
display:"block"
});
$("#testContent").text("");// 清空数据
$("#testContent").append(content);
$("#dialog").show();
}
//关闭灰色 jQuery 遮罩
function closeBg() {
$("#fullbg,#dialog").hide();
} 
/*function operateContent(){
	// 删除div
	$("input[name='del']").bind('click',function() {
		alert($(this).parent().find("#detail_content_Val").val());
		if($(this).parent().find("#detail_content_Val").val()!=undefined && $("input[name='detail_content_Val']").length==1){
			alert("请至少保留一个内容规则！");
			return;
		}
		$(this).parent().parent().remove();
	});
} */