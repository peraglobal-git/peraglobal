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
	    	/*var parentNode = $('#'+treeId).tree('getParent',node.target);
	    	var parenttitle = "已选择字典- "+parentNode.text+" - "+node.text;
	    	parenttitle = encodeURI(parenttitle); 
	    	parenttitle = encodeURI(parenttitle);
	    	//父级类型
	    	var parentDicType = '';
	    	if(parentNode.attributes!=null){
	    		parentDicType = node.attributes.dictionaryType;
		  	}else{
		  		parentDicType = '1000';
		  	}
	    	parentDicType = encodeURI(parentDicType); 
	    	parentDicType = encodeURI(parentDicType);
	    	var dicType = 'dictionaryType='+parentDicType+"&parentId="+lastTreeId+"&dicCategory=DicItem"+"&pointDic="+parenttitle;
	    	*/
	    	saveTaskMethod("新建知识源",null);
	    });
	}); 
	/*
	 * 编辑知识源（废弃）
	 */
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
	/*
	 * 编辑知识源
	 */
	editTaskMethod=function(title,param){
		var url = '../source/toKnowledgeEditPage.html?'+param;
		var iWidth=800; //弹出窗口的宽度;
		var iHeight=600; //弹出窗口的高度;
		var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		param = 'width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no';
		window.open (url,title,param);
	};
	
	
	/*
	 * 新增知识源（废弃）
	 */
	saveTaskMethod_old = function(title,param){
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
	/*
	 * 新增知识源
	 */
	saveTaskMethod = function(title,param){
		var url = '../source/toKnowledgeAddPage.html?'+param;
		var iWidth=800; //弹出窗口的宽度;
		var iHeight=600; //弹出窗口的高度;
		var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		param = 'width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no';
		window.open (url,title,param);
	};
	/*
	 * 废弃
	 */
	viewTaskMethod = function(title,param){
		pdpDialog.openDialog({url:'../source/toKnowledgeEditPage.html?'+param,title:title,
			width:'790px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				gridLoad(lastDataId);
			}});
	};
	/*
	 * 查看知识源
	 */
	viewTaskMethod = function(title,param){
		var url = '../source/toKnowledgeEditPage.html?'+param;
		var iWidth=800; //弹出窗口的宽度;
		var iHeight=600; //弹出窗口的高度;
		var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		param = 'width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no';
		window.open (url,title,param);
	};
	exports.gridLoad = function(type){
		if(type=="sort"){
			//请求参数
			var param = {type:null};
		}else{
			//请求参数
			var param = {type:type};
		}
		//请求连接
		var url = '../source/queryKnowledgeSourceList.html';
		//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		                {field:'index',width:40,halign:'center',align:'center',title:'序号',
							formatter : function(val, row, index) {
								var pageSize = $('.pagination').pagination("options").pageSize;
								var pageNumber = $('.pagination').pagination("options").pageNumber;
								return pageSize*(pageNumber - 1)+index+1;
							}
	     			    },
		               	{field:'name',halign:'left',align:'left',width:300,title:'知识源名称',
		                	formatter:function(val,row){
		                		var nameVal = val;
		                		var len = 15;
		                		if(val.length > len){
		                			nameVal = val.substr(0,len)+'...';
		                		}
		                		if (row.linkState == 1) {
		                			return '<a id="view_'+row.id+'" class="easyui-linkbutton viewDic" iconCls="icon-edit" plain="true" href="javascript:void(0)" title="' + val + '" >'+ nameVal +'</a>&nbsp;&nbsp;<sup style="color:red;">连接异常</sup>';
		                		} else {
		                			return '<a id="view_'+row.id+'" class="easyui-linkbutton viewDic" iconCls="icon-edit" plain="true" href="javascript:void(0)" title="' + val + '" >'+ nameVal +'</a>';
		                		}
			                }
		                },
		               	{field:'linkType',halign:'left',align:'left',width:120,title:'<a id="typeClick" class="typeClick" onClick="typeClick()">类型</a>',
		                	formatter:function(val,row){
		                		if(val=='1'){return '互联网'}
		                		else if(val == '2'){return '数据类'}
		                		else{return '本地文件'}
		                	}
		               	},
		            	/*{field:'operateSource',halign:'center',align:'center',width:120,title:'操作',
			                formatter:function(val,row){
			                		return '<a id="update_'+row.id+'" class="easyui-linkbutton updateDic" iconCls="icon-edit" plain="true" href="javascript:void(0)">编辑</a>&nbsp;&nbsp;&nbsp;&nbsp;<a id="delete_'+row.id+'" class="easyui-linkbutton deleteDic" iconCls="icon-edit" plain="true" href="javascript:void(0)">删除</a>';
			                }
		                }*/
		               	{field:'operateSource',halign:'center',align:'center',width:135,title:'操作',
		                	formatter:function(val,row){
			                	return '<div><a id="update_'+row.id+'" class="tb-btn tb-btn-edit updateDic" href="javascript:void(0)">编辑</a><a id="delete_'+row.id+'" class="tb-btn tb-btn-del deleteDic" href="javascript:void(0)">删除</a></div>';
			                }
		                }
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
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("deleteDic")){
				id = id.substring(7, id.length);
				target.on('click',function(){deleteClick(id);});
			}else if(target.hasClass("updateDic")){
				id = id.substring(7, id.length);
				target.on('click',function(){editClick(id);});
			}else if(target.hasClass("viewDic")){
				id = id.substring(5, id.length);
				target.on('click',function(){viewClick(id);});
			}
		});
	}
	
	
	//点击单行删除
	function deleteClick(id){
		if(id==null || id=="" || id==undefined){
			alert("编号不能为空！");
			return false;
		}
		var flag = confirm('确定要删除吗？');
		if(flag){
			var dataId=new Array();
			dataId[0]=id;
			ajaxFunction(dataId);
		}
		/*showQuestionTip({
			content:'确定要删除吗？',
			ok:function(){
				var dataId=new Array();
				dataId[0]=id;
				ajaxFunction(dataId);
			}
		});*/
	}
	//点击多行删除
	function deleteAllClick(){
		var rows = $("#"+gridId).datagrid("getSelections");
		if(rows.length < 1) {
			alert("请选择一行或多行!");
			return;
		}
		forVal(rows);
	}
	function forVal(rows){
		var dataId=[];
		var i=0;
		for (var int = 0; int < rows.length; int++) {
			var id=rows[int].id;
			if(!(id==null||id.length==0)){
				i++;
				dataId[i]=id;
			}
		}
		/*rows.forEach(function(e){
			i++;
			var j= e.id;
			if(j!=null && j!=''){
				dataId[i]=j;
			}
		});*/
		if(dataId.length>0){
			var flag = confirm('确定要删除吗？');
			if(flag){
				ajaxFunction(dataId);
			}
			/*showQuestionTip({
				content:'确定要删除吗？',
				ok:function(){
					ajaxFunction(dataId);
				}
			});*/
		}
	}
	function ajaxFunction(dataId){
		$.ajax({  
		    url: '../source/delKnowledgeSourceById.html',
		    data: {"listId":dataId}, 
		    dataType: "json",  
		    type: "POST",  
		    traditional: true,
		    success: function (responseJSON) {
		    	reload();
		    	if(responseJSON.msg!=null && responseJSON.msg!=undefined && responseJSON.msg!=""){
		    		alert(responseJSON.msg);
		    	}
		    } 
		});  
	}
	//点击单行编辑
	function editClick(id){
		//id=id.trim();  ie浏览器不支持trim()方法
		var jobType='id='+id+'&clicktype=edit';
		if(id==null || id=="" || id==undefined){
			alert("编号不能为空！");
			return false;
		}
    	editTaskMethod("编辑知识源",jobType);
	}
	//点击单行查看详情
	function viewClick(id){
		//id=id.trim();
		var jobType='id='+id+'&clicktype=view';
		if(id==null || id=="" || id==undefined){
			alert("编号不能为空！");
			return false;
		}
    	viewTaskMethod("知识源详情",jobType);
	}
	//查询事件
	$(function(){
		$("#search_button").on("click",function(){
			exports.gridLoad("sort");
		});
	});
	
});
	