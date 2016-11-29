/**
 * js
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

	//判断记录的节点是否置空
	var movelog=true;
	
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
		}});
	}
	
	//初始化方法
	exports.init = function(){
		initButtonEvent();
		onSearchButtonClick();
	};
	
	
	exports.gridLoad = function(){
		//请求连接
		var url = '';
		//请求参数
		var param = {};
		//列
		var columns = [[
			//要显示的列
   		{field:'id',checkbox:true},
    	{field:'attachmentName',halign:'center',align:'left',width:120,title:'attachmentName'},
    	{field:'fileSize',halign:'center',align:'left',width:120,title:'fileSize'},
    	{field:'createDate',halign:'center',align:'left',width:120,title:'createDate'},
     	{field:'operate',halign:'center',align:'center',width:150,title:'操作',
            formatter:function(val,row){
            	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateGeneateDemo" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
            }
          }
     ]];
		//初始化
		return datagrid.init(gridId,{
			url:'../geneateDemo/findGeneateDemoJosnList.html',
			columns:columns,
			queryParams:param,
			toolbar:'#toolbar',
			pagination:true,
			// autoDBClick:true, // 双击改变列宽适应当前内容
			// columnWidth:['geneateDemoName'], // 需要支持改变列宽的类可以多个用逗号隔开
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess
		}); 
	};
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateGeneateDemo")){
				id = id.substring(7, id.length);
				target.on('click',function(){updateRowClick(id);});
			}
		});
	}

	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		$('#'+gridId).datagrid('clearSelections');
		var gridDataLength = data.total;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId).datagrid('selectRecord',lastDataId);
			}else {
				$('#'+gridId).datagrid('selectRow',0);
			}
		}
		editColumnFn();
	}
	
	/**
	 * 添加
	 */
	initButtonEvent = function(){
		  //添加方法
	    $("#addGeneateDemo").on("click",function(){
	    	saveGeneateDemoMethod("添加");
	    });
	    //修改方法
	    $("#updateGeneateDemo").on("click",function(){
	    	var rows = $("#"+gridId).datagrid("getSelections");
	    	if(rows.length > 1) {
				showErrorTip("请选择一行");
				return;
			}
	    	lastDataId=rows[0].id;
	    	var param = 'id='+rows[0].id;
	    	saveGeneateDemoMethod("修改",param);
	    });
	    
	    //删除方法
	    $("#deleteGeneateDemo").on("click",function(){
			var rows = $('#'+gridId).datagrid('getChecked');
			submitDel('../geneateDemo/delGeneateDemo.html',{
				rows:rows,
				callback:function(data){
					reload();
				}
			});
	    });
	}; 
	
	//修改
	function updateRowClick(id){
		lastDataId=id;
		var param = "id="+id;
		saveGeneateDemoMethod("修改",param);
	}
	
	/**
	 * 添加修改处理
	 */
	saveGeneateDemoMethod = function(title,param){
		pdpDialog.openDialog({url:'../geneateDemo/saveGeneateDemoPage.html?'+param,title:title,
			width:'500px',
			height:'350px',
			reloadCallBack:function(data){
				lastDataId = data.id;
				reload();
			}});
	};

	
});