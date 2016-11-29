/**
 * 全局配置js
 */
define(function(require,exports,module){
	
	//artDialog
	var dialog  = require('artDialogJs');
	
	//datagrid 表格
	var datagrid  = require('datagrid');
	
	//datagrid table Id
	var gridId = "grid";
	
	//最后选择的ID
	var lastDataId = "";
	
	/**
	 * 重载
	 */
	function reload() {
		$('#'+gridId).datagrid('reload');
	}
	
	/**
	 * 判断是否是启用状态,如果是启用状态,则不能删除
	 */
	function isNormal(row) {
		if(row.status.name == "Normal") {
			return true;
		}else {
			return false;
		}
	}

	
	/**
	 * 修改状态处理
	 * flag传入的是Normal或者Disable,代表启用或者停用
	 */
	function statusChangeHandler(flag){
		var rows = $('#'+gridId).datagrid('getChecked');
		if(rows.length==1){
			if(rows[0].status.name==flag){
				showWarningTip("状态已经变更，不要再点了。");
				return ;
			}
		}
		var arr = [];
		$.each(rows,function(i,obj){
			if(rows[0].status.name!=flag){
				arr.push(obj.id);
			}
		});
		if(arr.length==0){
			showWarningTip("看起来不用做什么操作！");
			return ;
		}
		if(arr.length>0){
			showQuestionTip({
				content:'确认'+(flag=='Normal'?'启用':'停用')+'选中的属性？',
				ok:function(){
					enableRole(arr,flag);
				}
			});
		}
	
	}
	
	/**
	 * 启用或者停用
	 */
	function enableRole(arr,flag) {
		var url = "../dynamicModelProperty/updateStatus.html";
		var success = function(data) {
			if(data.code = "ok") {
				reload();
			}else {
				if(flag == 'Normal') {
					showErrorTip("启用角色失败");
				}
				if(flag == 'Disable') {
					showErrorTip("停用角色失败");
				}
			}
		};
		Util.ajax.loadData(url,success,{id:arr.join(","),status:flag},{});
	}
	
	
	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		var gridDataLength = data.rows.length;
		if(gridDataLength > 0) {
			if(lastDataId != "") {
				$('#'+gridId).datagrid('selectRecord',lastDataId);
			}else {
				$('#'+gridId).datagrid('selectRow',0);
			}
		}
		loadSuccessInit();
	}
	
	//选择表格事件
	function onSelect(rowIndex, rowData) {
		lastDataId = rowData.id;
	}

	//列
	var columns = [[
	                {field:'id',checkbox:true},
	                {field:'propertyName',halign:'center',align:'left',width:200,title:'属性名称'},
	                {field:'propertyCode',halign:'center',align:'left',width:160,title:'属性编码'},
	                {field:'modelClassName',halign:'center',align:'left',width:160,title:'实体对象'},
	                {field:'status',halign:'center',align:'center',width:100,title:'属性状态',formatter:datagrid.formatterDefault},
	                {field:'createBy',halign:'center',align:'left',width:120,title:'创建人',formatter:function(value,rowData,rowIndex) {
	          		  var user = datagrid.formatterUserName(value);
	        		  return user.realName;
	    		  }},
	                {field:'updateDate',halign:'center',align:'center',width:100,title:'修改时间'},
	                {field:'operate',halign:'center',align:'center',width:150,title:'操作',
		                formatter:function(val,row){
		                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateDynamicProperty" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
		                }
	                }
	              ]];

	
	//初始化grid接口
	exports.gridLoad = function() {
		//初始化
		return datagrid.init(gridId,{
			url:'../dynamicModelProperty/findList.html',
			columns:columns,
			idField:'id',
			rowPermission:false,
			toolbar:'#toolbar',
			onSelect:onSelect,
			onLoadSuccess:onGridLoadSuccess
		});

	};
	
	function loadSuccessInit(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateDynamicProperty")){
				id = id.substring(7, id.length);
				target.on('click',function(){
					var param = "id=" + lastDataId;
		    		var dialogTitle = '修改动态属性';
		    		openDialog(dialogTitle,param);
				});
			}
		});
	}
	
	/**
	 * 弹出添加、修改dialog
	 */
	function openDialog(title,param,id){
		dialog.openDialog({
			url:'../dynamicModelProperty/savePage.html?'+param,
			title:title,
			id:id,
			width:'800px',
			height:'480px',
			reloadCallBack:function(){
				reload();
			}
		});
	}
	
	
	
	//初始化方法
	exports.init = function(){
		//添加方法
	    $("#addDynamicProperty").on("click",function(){
    		var dialogTitle = '新增动态属性';
    		openDialog(dialogTitle,'');
	    });
	    
	    //删除
		$("#deleteDynamicProperty").on("click",function(){
			var rows = $('#'+gridId).datagrid('getChecked');
			$.each(rows,function(i,obj){
				if(isNormal(obj)) {
					canBeDeleted = false;
					return false;
				}
			});
   	    	if(!canBeDeleted) {
   	    		showErrorTip("已启用变量不能被删除");
   	    		return ;
   	    	}
			submitDel('../dynamicModelProperty/delete.html',{
				rows:rows,
				callback:function(data){
					reload();
				}
			});
		});
		
		/**
		 * 启用按钮
		 */
		$('#enableDynamicProperty').on('click',function() {
			statusChangeHandler('Normal');
		});

		/**
		 * 停用按钮
		 */
		$('#disableDynamicProperty').on('click',function() {
			statusChangeHandler('Disable');
		});
		//搜索
		 $("#search").inputTextEvent({callback:function(val){
			 $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
		 }});
		 
	};

});