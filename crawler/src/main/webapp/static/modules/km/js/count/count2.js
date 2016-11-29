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
	
	var gridId2 = "grid2";
	
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
	function onclick_a(){
		$("#knowledgesource_a").click(function(){
			window.location="../count/countPage.html";
			//self.location.href="../count/countPage2.html";
		});
		$("#atrribute_a").click(function(){
			window.location="../count/countPage2.html";
		});
	}
	//初始化方法
	exports.init = function() {
		//搜索
		onSearchButtonClick();
		onclick_a();
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
		pdpDialog.openDialog({url:'../count/toTaskDataDetailPageByKnowledge.html?'+param,title:title,
			width:'650px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				gridLoad(lastDataId);
			}});
	};
	exports.gridLoad = function(type){
		
		//请求参数
		var param = {knowledgeModel:type};

		//请求连接
		var url = '../count/queryCountListByKnowledge.html';
		//列
		var columns = [[
		                //要显示的列
		                //{field:'knowledgeModel',checkbox:true},
		                {field:'index',width:40,halign:'center',align:'center',title:'序号',
							formatter : function(val, row, index) {
								var pageSize = $('.pagination').pagination("options").pageSize;
								var pageNumber = $('.pagination').pagination("options").pageNumber;
								return pageSize*(pageNumber - 1)+index+1;
							}
	     			    },
		               	{field:'knowledgeModelName',halign:'center',align:'left',width:300,title:'知识模板名称',
		                	formatter:function(val,row){
		                		var nameVal = val;
		                		var len = 15;
		                		if(val.length > len){
		                			nameVal = val.substr(0,len)+'...';
		                		}
			                	return '<a  title="' + val + '" >'+ nameVal +'</a>';
			                }
		                },
		               	{field:'taskcount',halign:'center',align:'center',width:120,title:'<a id="typeClick" class="typeClick" onClick="typeClick()">采集任务数</a>',
		                	formatter:function(val,row){
		                		var nameVal = val;
		                		if(nameVal==0||nameVal==""){
				                	return '<a >'+ nameVal +'</a>';
		                		}else{
				                	return '<a id="view_'+row.knowledgeModel+'" class="easyui-linkbutton viewDic" iconCls="icon-edit" plain="true" href="javascript:void(0)" title="点击查看详情" style="text-decoration:underline;">'+ nameVal +'</a>';
		                		}
			                }
		               	},
		            	{field:'sourcecount',halign:'center',align:'center',width:120,title:'知识源数量',
		                },
		                {field:'taskdatacountall',width:120,
		                	halign:'center',align:'center',width:120,title:'采集数据总量'}
		              ]];
		if(type=="sort"){
			//初始化
			return datagrid.init(gridId,{
				url:url,
				columns:columns,
				queryParams:Util.ajax.serializable($("#listForm")),
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
		}else{
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
		}
	};
	var sortType = 'asc';
	//点击类型排序
	typeClick=function(){
		if(sortType == 'asc'){
			//desc
			sortType= 'desc';
			exports.gridLoad("typeSort");
		}else{
			//asc
			sortType = 'asc';
			exports.gridLoad("type");
		}
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
		editColumnFn();
	}
	$(function(){
		$("#deleteDic").on("click",function(){
			deleteAllClick();
		});
	});
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_north .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("viewDic")){
				id = id.substring(5, id.length);
				target.on('click',function(){viewClick(id);});
			}
		});
	}
	//点击单行查看详情
	function viewClick(id){
		//id=id.trim();
		var jobType='id='+id+'&clicktype=view';
		if(id==null || id=="" || id==undefined){
			alert("编号不能为空！");
			return false;
		}
    	//viewTaskMethod("采集数据详情",jobType);
		exports.gridLoad2(id);
	}
	//查询事件
	$(function(){
		$("#search_button").on("click",function(){
			exports.gridLoad("sort");
		});
	});
	
	//列表2
	
	exports.gridLoad2 = function(type){

		//请求参数
			
		var param = {knowledgeModel:type};

		//请求连接
		var url = '../count/getTaskDataListByKnowledge.html';
		//列
		var columns = [[
		                //要显示的列
		                //{field:'id',checkbox:true},
		                {field:'index',width:40,halign:'center',align:'center',title:'序号',
							formatter : function(val, row, index) {
								var pageSize = $('.pagination').pagination("options").pageSize;
								var pageNumber = $('.pagination').pagination("options").pageNumber;
								return pageSize*(pageNumber - 1)+index+1;
							}
	     			    },
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
		            	{field:'sourceName',halign:'center',align:'left',width:135,title:'知识源名称',
		            	},
		                {field:'taskjobDataCount',width:120,
		                	halign:'center',align:'center',width:135,title:'采集数据量'}
		              ]];

			//初始化
			return datagrid.init(gridId2,{
				url:url,
				columns:columns,
				queryParams:param,
				toolbar:'#toolbar2',
				rownumbers:false,
				pagination:true,
				fit:true,
				fitColumns:true,
				autoDBClick:true, // 双击改变列宽适应当前内容
				columnWidth:['dictionaryName'], // 需要支持改变列宽的类可以多个用逗号隔开
				singleSelect:true,
				onLoadSuccess:onGridLoadSuccess2
			});

	};
	
	function onGridLoadSuccess2(data) {
		/*if(showValue == 'true'){
			$('#'+gridId).datagrid('showColumn','value');
		}else{
			$('#'+gridId).datagrid('hideColumn','value');
		}*/
		$('#'+gridId2).datagrid('clearSelections');
		var gridDataLength = data.total;
		lastDataId=data.id;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId2).datagrid('selectRecord',lastDataId);
			}else {
				//$('#'+gridId).datagrid('selectRow',0);
			}
		}
	//	editColumnFn();
	}
});
	