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

	
	/**
	 * 刷新表格数据
	 */
	reload=function(){
 		$("#"+gridId).datagrid("reload");

	};
	
	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		 $("#search").inputTextEvent({callback:function(val){
			 //如果不是修改则
			 if(loadIsCreentPage){
				 $('#grid').datagrid("reload",Util.ajax.serializable($("#listForm")));
				 loadIsCreentPage=false;
			 }else{
				 $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
			 }
		  }});
	}
	//初始化方法
	exports.init = function() {
		//搜索
		onSearchButtonClick();
	};
	
	/**
	 * 添加字典
	 */
	initButtonEvent = $(function(){
		  //添加方法
	    $("#addDic").on("click",function(){
	    	//定位文字
	    	var node = $('#'+treeId).tree('find',lastTreeId);
	    	saveTaskMethod("新建知识源",null);
	    });
	}); 
	
	editTaskMethod=function(title,param){
		pdpDialog.openDialog({url:'../source/toKnowledgeEditPage.html?'+param,title:title,
			width:'790px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
//				lastDataId = data.id;
				reload();
			}});
	};
	
	saveTaskMethod = function(title,param){
		pdpDialog.openDialog({url:'../source/toKnowledgeAddPage.html?'+param,title:title,
			width:'790px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
				//lastDataId = data.id;
				//reload();
				exports.gridLoad();
			}});
	};
	viewTaskMethod = function(title,param){
		pdpDialog.openDialog({url:'../count/toTaskDataDetailPage.html?'+param,title:title,
			width:'790px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				gridLoad(lastDataId);
			}});
	};
	exports.gridLoad = function(type){

		//请求参数
			
		var param = {sourceId:type};

		//请求连接
		var url = '../count/getTaskDataList.html';
		//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		               	{field:'name',halign:'center',align:'left',width:300,title:'采集任务名称',
		                	formatter:function(val,row){
		                		var nameVal = val;
		                		var len = 15;
		                		if(val.length > len){
		                			nameVal = val.substr(0,len)+'...';
		                		}
			                	return '<a title="' + val + '" >'+ nameVal +'</a>';
			                }
		                },
		            	{field:'knowledgeModelName',halign:'center',align:'left',width:135,title:'知识模板',
		                },
		                {field:'taskjobDataCount',width:120,
		                	halign:'center',align:'center',width:135,title:'采集数据量'}
		              ]];

			//初始化
			return datagrid.init(gridId,{
				url:url,
				columns:columns,
				queryParams:param,
				toolbar:'#toolbar',
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
			}else {
				//$('#'+gridId).datagrid('selectRow',0);
			}
		}
//		editColumnFn();
	}
});
	