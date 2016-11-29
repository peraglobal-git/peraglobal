/**
 * 字典js
 */
define(function(require,exports,module){
	var pdpDialog  = require('artDialogJs');
	
	//easyui Tree树
	var tree  = require('tree');
	
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	
	//tree ul Id
	var treeId = "tree";
	
	//上一次点击树的节点ID
	var lastTreeId = "";
	
	//链接
	var gridUrl = '';
	
	//记录选中的节点
	var lastDataId = "";

	//判断记录的节点是否置空
	var movelog=true;
	
	//是否显示字典值
	var showValue = false;
	
	
	
	exports.gridLoad = function(id){
		//请求连接
		var url = '../taskJobMonitor/taskJobMonitorList.html';
		//请求参数
		var param = {'jobid':id};
		//列
		var columns = [[
		                //要显示的列
		               /* {field:'id',checkbox:true},*/
		               	/*{field:'jobName',halign:'center',align:'left',width:200,title:'任务名称',
		                	formatter:function(val,row){
		               			var nameVal = val;
		                		var len = 15;
		                		if(val.length > len){
		                			nameVal = val.substr(0,len)+'...';
		                		}
		                		return '<a id="taskNameClick" class="easyui-linkbutton taskNameClick" iconCls="icon-edit" plain="true" href="javascript:void(0)" onClick="taskNameClick()"  title="' + val + '" >'+nameVal+'</a>';
		                	}		
		               	},*/
		                {field:'index',width:40,halign:'center',align:'center',title:'序号',
							formatter : function(val, row, index) {
								var pageSize = $('.pagination').pagination("options").pageSize;
								var pageNumber = $('.pagination').pagination("options").pageNumber;
								return pageSize*(pageNumber - 1)+index+1;
							}
	     			    },
		               	{field:'startTime',halign:'center',align:'center',width:200,title:'开始时间'},
		                {field:'stopTime',halign:'center',align:'center',width:200,title:'结束时间'},
		                {field:'fullNumber',halign:'center',align:'center',width:120,title:'传输成功数'},
		               	{field:'failedNumber',halign:'center',align:'center',width:120,title:'传输失败数'},
		               	{field:'errorFlag',halign:'center',align:'center',width:100,title:'错误日志',
		               		formatter:function(val,row){
		                		if(val=="1"||val=="2"){
		                			return '<a id="'+row.id+'" class="easyui-linkbutton errorClick" iconCls="icon-edit" plain="true" href="javascript:void(0)">查看</a>';
		                		}
		                	}
		               	}
		               	/*{field:'jobState',halign:'center',align:'center',width:60,title:'状态',
		               		formatter:function(val,row){
		                		if(val=='0'){return '就绪';}
		                		else if(val == '1'){return '进行中';}
		                		else if(val=='2'){return '暂停';}
		                		else if(val=='3'){
		                			return '停止';
		                		}else if(val=='4'){
		                			return "已完成";
		                		}else if(val == '5'){
		                			return "等待中";
		                		}
		                	}	
		               	},*/
		               	
		              ]];
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			queryParams:param,
			toolbar:'#toolbar',
			rownumbers:false,
			pagination:true,
			fit:true,
			fitColumns:true,
			autoDBClick:true, // 双击改变列宽适应当前内容
			columnWidth:['dictionaryName'], // 需要支持改变列宽的类可以多个用逗号隔开
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess
		});
	};
	
	function onGridLoadSuccess(data) {
		/*if(showValue == 'true'){
			$('#'+gridId).datagrid('showColumn','value');
		}else{
			$('#'+gridId).datagrid('hideColumn','value');
		}*/
		$('#'+gridId).datagrid('clearSelections');
		var gridDataLength = data.total;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId).datagrid('selectRecord',lastDataId);
			}/*else {
				$('#'+gridId).datagrid('selectRow',0);
			}*/
		}
		editColumnFn();
	}
	
	//绑定监控信息
	exports.info=function(){
	 	/*var id=$("#id").val();
		var jobType=$("#jobType").val();
		var state=$("#state").val();
		var ruleContent=$("#ruleContent").val();
		var automatic=$("#automatic").val();
		if(automatic!=null && automatic!='' && automatic=='0'){
			$("#job").val("自动");
		}else if(automatic!=null && automatic!='' && automatic=='1'){
			$("#job").val("手动");
		}*/
		
		/*if(state!=null && state!='' && state=='0'){
			$("#cjState").val("就绪");
		}else if(state!=null && state!='' && state=='1'){
			$("#cjState").val("进行中");
		}
		else if(state!=null && state!='' && state=='2'){
			$("#cjState").val("暂停");
		}
		else if(state!=null && state!='' && state=='3'){
			$("#cjState").val("停止");
		}*/
		/*else if(state!=null && state!='' && state=='4'){
			$("#cjState").val("已完成");
		}else if(state!=null && state!='' && state=='5'){
			$("#cjState").val("等待中");
		}*/
		
		/*if(jobType!=null && jobType!='' && jobType=='1'){
			$("#type").val("互联网");
		}else if(jobType!=null && jobType!='' && jobType=='2'){
			$("#type").val("数据库");
		}else if(jobType!=null && jobType!='' && jobType=='3'){
			$("#type").val("本地知识");
		}else if(jobType!=null && jobType!='' && jobType=='4'){
			$("#type").val("转换任务");
		}else if(jobType!=null && jobType!='' && jobType=='5'){
			$("#type").val("传输任务");
		}*/
	};
	
	// 查看监控日志
	function errorClick(jobMonitorId){
		var url="../taskJobMonitor/getJobMonitorErrors.html?monitorId="+jobMonitorId;
		$.getJSON(url, function(json) {
			var errorLst = json.errors;
			var contentStr = '<table style="margin-left:20px;width:78%; border-collapse: collapse;">';
			contentStr +='<tr  align="center" style="height:30px;">';
            contentStr +='<td style="border:1px solid #000;">序号</td>';
            contentStr +='<td style="border:1px solid #000;">时间</td>';
            contentStr +='<td style="border:1px solid #000;">日志</td>';
            contentStr +='</tr>';
			$.each(json.errors, function(i, item) {
	            contentStr +='<tr  align="center" style="height:30px;">';
	            contentStr +='<td style="border:1px solid #000;">'+(i+1)+'</td>';
	            contentStr +='<td style="border:1px solid #000;">'+item.createDateStr+'</td>';
	            contentStr +='<td style="border:1px solid #000;">'+item.content+'</td>';
	            contentStr +='</tr>';
	        });
			contentStr+='</table>';
			showBg(contentStr);
		});
		 
	}
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
		$("#dialog").show().css({
			left:320
		});
	}
	//关闭灰色 jQuery 遮罩
	$("#closeBg").click(function(){
		$("#fullbg,#dialog").hide();
	});
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("errorClick")){
				target.on('click',function(){errorClick(id);});
			}
		});
	};
});