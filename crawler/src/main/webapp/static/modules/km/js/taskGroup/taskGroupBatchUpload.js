define(function(require,exports,module){
	var msgForm = null;
	var oldname = null;
	 var groupId = new Array();
	/**
	 * 二级联动
	 */
	$(function(){
		$("#knowledgeType").change(function(){
			var typeId=$("#knowledgeType").find("option:selected").val();
			var typeName=$("#knowledgeType").find("option:selected").text();
			$("#knowledgeTypeName").val(typeName);
			if(typeId == '0'){
				$("#knowledgeModel").empty();
				$("#knowledgeModel").append("<option value='0'>请选择</opion>");
				return;
			}
			$.ajax({
				type:'POST',
				url:"../taskJob/getModelsByTypeId.html",
				dataType:'json',
				data:{
					typeId:typeId
				},
				success:function(result){
					var models = $.parseJSON(result.models);
					$("#knowledgeModel").empty();
					$("#knowledgeModel").append("<option value='0'>请选择</opion>");
					for (var i = 0; i < models.length; i++) {
						$("#knowledgeModel").append("<option value='"+models[i].TEPT_ID+"'>"+models[i].TEPT_NAME+"</opion>");
					}
				}
			});
				
		});
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
				url = '../taskJob/toCrawlerWebJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;;
				url=encodeURI(url);
				url=encodeURI(url); 
				window.location=url;
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
				url=encodeURI(url);
				url=encodeURI(url); 
				window.location=url;
			}else{
				if(sourceId!="0"){
					var d = new Date();
					var vYear = d.getFullYear();
					var vMon = d.getMonth() + 1;
					var vDay = d.getDate(); 
					var taskName=vYear+(vMon<10 ? "0" + vMon : vMon)+(vDay<10 ? "0"+ vDay : vDay);
					taskName = sourceName+'_'+taskName+'_'+RndNum(3);
					$("#name").val(taskName);
				}
			}
		});
		$("#knowledgeModel").change(function(){
			var modelName=$("#knowledgeModel").find("option:selected").text();
			$("#knowledgeModelName").val(modelName);
		});
		$("#system").change(function(){
			var systemName=$("#system").find("option:selected").text();
			$("#systemName").val(systemName);
		});
	});
	
	function RndNum(n){
		  var rnd="";
		  for(var i=0;i<n;i++)
		     rnd+=Math.floor(Math.random()*10);
		  return rnd;
		}
	/**
	 * 初始化
	 */
	exports.init=function(opts){
		var value= $("#resultType").val();
		if(value=='close'){
			setTimeout("alert('保存成功，任务已开始！');",2000);
			window.opener.location.reload();
			window.close();
		}
		oldname = opts.oldname;
		groupId = opts.groupId;
		
		var isSave = 0;
		$("#saveBtn").on("click",function(){
			if($("#name").val()==""||$("#name").val()==null){
				alert("任务名称不能为空！");
				return false;
			}
			$.ajax({
				async:false,
				type:'POST',
				url:"../taskJob/validateLocalNameByAjax.html",
				dataType:'json',
				data:{name:function(){
                	return $("#name").val();
            		},
					oldName:oldname,
	                groupId:groupId
				},
				success:function(result){
					if(result.state=="1"){
						alert("任务名称已存在！");
						return false;
					}else if(result.state=="2"){
						alert("只允许输入汉字、英文字母、数字及下划线，点，@");
						return false;
					}else{
						var knowledgeSource=$("#knowledgeSource option:selected").val();
						if(knowledgeSource==null || knowledgeSource=='' || knowledgeSource=='0'){
							alert("请选择知识源！");
							return false;
						}
						var knowledgeTypeVal=$("#knowledgeType option:selected").val();
						if(knowledgeTypeVal==null || knowledgeTypeVal=='' || knowledgeTypeVal=='0'){
							alert("请选择知识形态！");
							return false;
						}
						var knowledgeModelVal=$("#knowledgeModel option:selected").val();
						if(knowledgeModelVal==null || knowledgeModelVal=='' || knowledgeModelVal=='0'){
							alert("请选择知识模板！");
							return false;
						}
						if(isSave == 1){return false;}
						var filepath = $("input[name='file']").val();
						if(filepath == ''){
							alert("请选择文件！");
							return false;
						}
						var extStart = filepath.lastIndexOf(".");
			            // 文件后缀名
			            var ext = filepath.substring(extStart, filepath.length).toUpperCase();
			            if(!(ext=='.XLS' || ext=='.XLSX' || ext=='.RAR' || ext=='.ZIP')){
			            	alert("只能上传Excel文件（后缀名：.xlsx和.xls）和压缩文件（后缀名：.rar和.zip）。");
							return false;
			            }
			            $("#saveBtn").attr("disabled","disabled");
			            $("#uploadFile").submit();
						isSave = 1;
//						window.opener.location.reload();
//						window.close();
					}
				}
			});
			
			/*// 文件属性
			var excelPro = $("select[name='excelProperties']").val();
			if(excelPro == "ontology"){
				if(ext != ".XLS" && ext != ".XLSX"){
					alert("【本体】允许上传的文件格式：XLS XLSX");
					return false;
				}
			}else if(excelPro == "knowledge"){
				if(ext != ".XLS" && ext != ".XLSX" && ext != ".ZIP" && ext != ".RAR"){
					alert("【知识】允许上传的文件格式：XLS XLSX ZIP RAR");
					return false;
				}
			}else if(excelPro == "expert"){
				if(ext != ".XLS" && ext != ".XLSX"){
					alert("【专家】允许上传的文件格式：XLS XLSX");
					return false;
				}
			}*/
           
		});
		// 选择数据
		$("#excelProperties").change(function(){
			// 文件属性
			var excelPro = $("select[name='excelProperties']").val();
			if(excelPro == "knowledge"){
				$("input[name='merge']").attr("disabled","false");
			}else{
				$("input[name='merge']").attr("disabled","true");
			}
		});
	};
});