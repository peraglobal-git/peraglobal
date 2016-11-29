/**
 * 修改保存字典js
 */
define(function(require,exports,module){
	exports.init = function(opts){
		var id=$("#id").val();
		var jobState=$("#jobState").val();
		var name=$("#name").val();
		var groupName=$("#groupName").val();
		var registerType=$("#registerType").val();
		var jobPriority=$("#jobPriority").val();
		
		$("#inputTaskName").val(name);
		 $("#inputGroupName").val(groupName);
		$("#selectTaskType option[value="+registerType+"]").attr("selected","selected");
		$("#selectJobState option[value="+jobState+"]").attr("selected","selected");
		$("#selectPriority option[value="+jobPriority+"]").attr("selected","selected");
	};
	$("#saveBtn").on("click",function(){
		xiaoyan();
	});
	xiaoyan = function(){
			var taskName= $("#inputTaskName").val();
			var groupName= $("#inputGroupName").val();
			var taskType= $("#selectTaskType").val();
			var jobState= $("#selectJobState").val();
			var priority= $("#selectPriority").val();
			var id= $("#id").val();
			if(taskName==null || taskName==""){
				alert("请输入任务名称！");
			}else if(groupName==null || groupName==""){
				alert("请输入组名称！");
			}
			else if(taskType==null || taskType=="-1"){
				alert("请输入任务类型！");
			}
			else if(jobState==null || jobState=="-1"){
				alert("请输入任务状态！");
			}
			else if(priority==null || priority=="-1"){
				alert("请输入任务优先级！");
			}
			else if(id==null || id==""){
				alert("请输入任务编号！");
			}else{
					 $.ajax({
						   type: "post",
						   url: "../taskJob/updateJobInfo.html", 
						   data: {
							   id:id,
							   taskName:taskName,
							   groupName:groupName,
							   taskType:taskType,
							   jobState:jobState,
							   priority:priority
						   },
						   dataType: "json"
				    });
					 art.dialog.close();
			}
		
	};
});
