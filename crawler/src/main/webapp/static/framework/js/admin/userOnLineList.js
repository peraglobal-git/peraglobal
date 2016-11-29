define(function(require,exports,module){
	//artDialog
	var dialog  = require('artDialogJs');

	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	
	/**
	 * 刷新表格数据
	 */
	function reload(){
		$("#"+gridId).datagrid("reload");
	}
	
	function refresh(){
		$("#js_refresh").on("click",function(){
			$("#"+gridId).datagrid("reload");//重新加载表格
		});
	}
	function offline(){
		/*强制下线 */
	    $("#js_offline").on("click",function(){
	    	//获取复选框值
	 	  
				var row = $('#'+gridId).datagrid("getChecked");
				if (row.length == 0) {
					showErrorTip("请选择至少一行数据");
					return false;
				}
				var sessionIdArr = "";
				var contentList = ['要踢出的用户里包含自己，执行后自己不会被踢出！','确认踢出选中的用户？'];
				var content = contentList[1];
			       if(row.length>0){
			    	   for(var i=0;i<row.length;i++){
			    	   	if(row[i].sessionid == null){
			    	   		if(row.length == 1){
				    	   		showErrorTip("要踢出的用户是自己，不能执行！");
								return false;
			    	   		}
			    	   		if(row.length > 1){
			    	   			content = contentList[0];
			    	   		}
			    	   	}
			    		   sessionIdArr+=row[i].sessionid;
			    		   if(i<row.length-1){
			    			   sessionIdArr+=",";
			    		   }
			    	   }
			    	   showQuestionTip({
							content:content,
							ok:function(){
								$.ajax({
									type : "POST",
									dataType : "json",
									url : "../user/offline.html",
									data : "sessionIds=" + sessionIdArr,
									success : function(data) {
										if (data.success == "ok") {
											showSuccessTip("用户已被迫离线！");
											reload();
										}
									}
								});
							}
						});
			      }
			});
	}
	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		$('#login_count').html(data.total);
	}
	
	function onSelect(index,row){
	}
	//初始化grid接口
	exports.gridLoad = function() {
		//请求连接
		var url = '../user/onLineList.html';
		
		//请求参数
		var param = {};
		
		//列
		var columns=[[
						{field:'sessionid',checkbox:true},
		                {field:'username',align:'center',width:270,title:'用户名'},
		                {field:'startTimestamp',align:'center',width:270,title:'创建时间'},
		                {field:'lastAccessTime',align:'center',width:260,title:'最后访问时间'},
		                {field:'ip',align:'center',width:260,title:'登陆IP'}
		            ]];
		
		//初始化
		return datagrid.init(gridId,{
			url:url,
			columns:columns,
			fitColumns:true,
			queryParams:param,
			idField:'',
			toolbar:'#toolbar',
			fit:true,
            striped: true,
            pagination:true,
            pageSize:10,
            pageList:[10,20,30,40,50],
            rownumbers:true,
            singleSelect:true,
            checkOnSelect:true,
            scrollbarSize:0,
       		selectOnCheck:false,
            height:$("#js_center").height(),
			onLoadSuccess:onGridLoadSuccess,
			onSelect:onSelect
		});
	};
	//初始化方法
	exports.init = function() {
		refresh();
		offline();
	};
	
})