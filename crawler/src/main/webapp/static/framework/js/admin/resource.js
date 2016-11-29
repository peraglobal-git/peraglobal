/**
 * 资源js
 */
define(function(require,exports,module){
	//dialog
	var pdpDialog  = require('artDialogJs');
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	
	//链接
	var gridUrl = '';
	
	//记录选中的节点
	var lastDataId = "";
	
	var loadIsCreentPage=false;
	
	
	/**
	 * 刷新表格数据
	 */
	function reload(){
		$("#"+gridId).datagrid("reload");
	}
	
	
	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		 $("#search").inputTextEvent({callback:function(val){
			    $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
			    loadIsCreentPage=false;
		  }});
	}
	
	//初始化方法
	init = function(){
		initButtonEvent();
		onSearchButtonClick();
	};
	
	
	exports.gridLoad = function(){
		//请求连接
		var url = '../resource/findResourceJosnList.html';
		
		//请求参数
		var param = {};
		
		//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		               	{field:'resourceName',halign:'center',align:'left',width:120,title:'名称'},
		               	{field:'url',halign:'center',align:'left',width:120,title:'URL'},
		               	{field:'createDate',halign:'center',align:'left',width:120,title:'创建时间'},
		               	{field:'createBy',halign:'center',align:'left',width:120,title:'操作人',formatter:function(value,rowData,rowIndex) {
		          		  var user = datagrid.formatterUserName(value);
		        		  return user.realName;
		    		  }},
		               	{field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateResource" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
			                }
		                }
		              ]];
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			queryParams:param,
			toolbar:'#toolbar',
			pagination:true,
			selectFirstRow:false,
			onLoadSuccess:onGridLoadSuccess
		}); 
	};
	
	//操作列表调用的方法
	function editColumnFn(){
		$(".datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateResource")){
				id = id.substring(7, id.length);
				target.on('click',function(){updateResourceRowFn(id);});
			}
		});
	}

	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		//选中最后选择的记录
		if(lastDataId){
			$('#'+gridId).datagrid('selectRecord',lastDataId);
		}else{
			if(data.total>0)
				$('#'+gridId).datagrid('selectRow',0);
		}
		editColumnFn();
	}

	
	/**
	 * 添加资源
	 */
	initButtonEvent = function(){
		  //添加方法
	    $("#addResource").on("click",function(){
	    	saveDicMethod("添加资源");
	    });
	    //修改方法
	    $("#updateResource").on("click",function(){
	    	loadIsCreentPage=true;
	    	var rows = $("#"+gridId).datagrid("getSelections");
	    	if(rows.length > 1) {
				showErrorTip("请选择一行");
				return;
			}
	    	lastDataId=rows[0].id;
	    	var resourceId = 'id='+rows[0].id;
	    	saveDicMethod("修改资源",resourceId);
	    });
	    $("#deleteResource").on("click",function(){
	    	loadIsCreentPage=true;
			var rows = $('#'+gridId).datagrid('getChecked');
			submitDel('../resource/delResource.html',{
				rows:rows,
				callback:function(data){
					reload();
				}
			});
	    });
	}; 
	
	/**
	 * 修改资源
	 */
	function updateResourceRowFn(id){
		loadIsCreentPage=true;
    	lastDataId=id;
    	var resourceId = 'id='+id;
    	saveDicMethod("修改资源",resourceId);
	}
	
	/**
	 * 添加修改资源处理
	 */
	saveDicMethod = function(title,param){
		pdpDialog.openDialog({url:'../resource/saveResourcePage.html?'+param,title:title,
			width:'500px',
			height:'350px',
			id:"resource",
			reloadCallBack:function(data){
				if(data.id)
					lastDataId = data.id;
				reload();
			}});
	};
	
	
	init();
});