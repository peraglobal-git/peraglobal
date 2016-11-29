/**
 * 字典js
 */
define(function(require,exports,module){
	//dialog
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
	function reload(){
		$("#"+gridId).datagrid("reload");
	}
	
	/**
	 * 刷新树数据
	 */
	function reloadTree() {
		$("#"+treeId).tree('reload');
	}
	
	/**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		 $("#search").inputTextEvent({callback:function(val){
			   var  node= $('#'+treeId).tree('getSelected');
			   if(node) {
			    $("#parentId").val(node.id);
			    $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
			   }
		  }});
	}
	
	//树 选中事件
	function onTreeSelect(node) {
		$('#'+gridId).datagrid('clearSelections');
		//判断是否置空
		if(movelog){
			lastDataId="";
		}
		movelog=true;
		//记录id
		lastTreeId = node.id;
		var parentNodeCategory = node.attributes.dicCategory;
		//只查询字典项
        if(parentNodeCategory!=null && parentNodeCategory == "Dic"){
        	//获取配置参数
    		var options = $('#'+gridId).datagrid('options');
    		
    		options.url = gridUrl;
    		
    		//获取查询参数
    		options.queryParams = {
    				"conditions['parentId_eq'].value" : lastTreeId,
    				ran : Math.random()
    		};
    	
    		//重新加载grid
    		reload();
    		$('#addDic').linkbutton('enable');
			$('#deleteDic').linkbutton('enable');
        }else{
        	//置灰按钮
        	$('#addDic').linkbutton('disable');
			$('#deleteDic').linkbutton('disable');
			//清空datagrid
    		 $('#'+gridId).datagrid('loadData', { total: 0, rows: [] }); 
        }
	}
	
	/**
	 * 树的右键菜单
	 */
	function onTreeContextMenu(e, node){
		e.preventDefault();
		//e.stopPropagation();//阻止冒泡
		// 查找节点
		$('#tree').tree('select', node.target);
 		var parentNodeCategory = node.attributes.dicCategory;
        if(parentNodeCategory == undefined){
        	//有添加的快捷菜单
    		$('#edit_treeNode').hide();
    		$('#del_treeNode').hide();
			$('#treeRootMenu').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
        }else if(parentNodeCategory!=null && parentNodeCategory == "Dic"){
        	//无添加的快捷菜单
        	$('#treeNodeMenu').menu('show', {
     			left: e.pageX,
     			top: e.pageY
     		});
        }else if(parentNodeCategory!=null && parentNodeCategory == "DicItem"){
        	return;
        }else{
        	//有添加的快捷菜单
        	$('#edit_treeNode').show();
        	$('#del_treeNode').show();
        	 $('#treeRootMenu').menu('show', {
 	    			left: e.pageX,
 	    			top: e.pageY
 	    		});
        }
	}
	
	//树加载完成事件
	function onTreeLoadSuccess(node, data) {
		var treeDataLength = data.length;
		if(treeDataLength > 0) {
			if(lastTreeId == "") {
				var roots=$('#tree').tree('getRoots');
				$('#'+treeId).tree('select',roots[0].target);
				$('#addDic').linkbutton('disable');
				$('#deleteDic').linkbutton('disable');
			}else {
				var treeNode = $('#'+treeId).tree('find',lastTreeId);
				if(treeNode != undefined) {
				    $("#tree").tree('expandTo', treeNode.target); //展开选择的节点
				    $("#tree").tree('expand', treeNode.target); //展开选择的节点
				    $('#'+treeId).tree('select', treeNode.target);
				}
				var nodeCategory = treeNode.attributes;
				if(nodeCategory!=null){
					var parentNodeCategory = treeNode.attributes.dicCategory;
					//只字典项按钮可用
			        if(parentNodeCategory!=null && parentNodeCategory == "Dic"){
			        	$('#addDic').linkbutton('enable');
						$('#deleteDic').linkbutton('enable');
			        }
				}else{
					$('#addDic').linkbutton('disable');
					$('#deleteDic').linkbutton('disable');
				}
			}
		}
	}
	
	
	//初始化tree接口
	exports.treeLoad = function(inGridId,inGridUrl,parentIdNode) {
		if(inGridId != "" && inGridId != undefined && inGridId != null)	gridId = inGridId;
		
		if(inGridUrl != "" && inGridUrl != undefined && inGridUrl != null) gridUrl = inGridUrl;
		if(parentIdNode != "" && parentIdNode != undefined && parentIdNode != null) lastTreeId = parentIdNode;
		//请求连接
		var url = '../dictionary/getDictionaryTree.html';
		
		return tree.init(treeId,{
			url:url,
			onSelect:onTreeSelect,
			onContextMenu:onTreeContextMenu,
			onAfterEdit:onAfterEdit,
			onLoadSuccess:onTreeLoadSuccess
		});
	};
	
	
	
	//初始化方法
	exports.init = function(isShowValue){
		showValue = isShowValue;
		initButtonEvent();
		onSearchButtonClick();
		//右键添加树节点
		addTreeNodeRight();
		//右键编辑树节点
		editTreeNodeRight();
		//右键删除树节点
		delTreeNodeRight();
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
		               	{field:'dictionaryName',halign:'center',align:'left',width:120,title:'名称'},
		               	{field:'dictionaryType',halign:'center',align:'left',width:120,title:'类型'},
		               	{field:'value',halign:'center',align:'left',width:120,title:'字典值'},
		               	{field:'description',halign:'center',align:'left',width:120,title:'描述'},
		               	{field:'updateDate',halign:'center',align:'left',width:120,title:'更新时间'},
		               	{field:'createBy',halign:'center',align:'left',width:120,title:'操作人',formatter:function(value,rowData,rowIndex) {
		          		  var user = datagrid.formatterUserName(value);
		        		  return user.realName;
		    		  }},
		               	{field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateDic" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>'; 
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
			autoDBClick:true, // 双击改变列宽适应当前内容
			columnWidth:['dictionaryName'], // 需要支持改变列宽的类可以多个用逗号隔开
			singleSelect:true,
			onLoadSuccess:onGridLoadSuccess
		}); 
	};
	
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("updateDic")){
				id = id.substring(7, id.length);
				target.on('click',function(){updateDicRowClick(id);});
			}
		});
	}

	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		if(showValue == 'true'){
			$('#'+gridId).datagrid('showColumn','value');
		}else{
			$('#'+gridId).datagrid('hideColumn','value');
		}
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
	 * 添加字典
	 */
	initButtonEvent = function(){
		  //添加方法
	    $("#addDic").on("click",function(){
	    	//定位文字
	    	var node = $('#'+treeId).tree('find',lastTreeId);
	    	var parentNode = $('#'+treeId).tree('getParent',node.target);
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
	    	saveDicMethod("添加字典",dicType);
	    });
	    //修改方法
	    $("#updateDic").on("click",function(){
	    	var rows = $("#"+gridId).datagrid("getSelections");
	    	if(rows.length > 1) {
				showErrorTip("请选择一行");
				return;
			}
	    	lastDataId=rows[0].id;
	    	var dicType = 'id='+rows[0].id+'&dictionaryType=1000';
	    	saveDicMethod("修改字典",dicType);
	    });
	    //删除方法
	    $("#deleteDic").on("click",function(){
			var rows = $('#'+gridId).datagrid('getChecked');
			submitDel('../dictionary/delDictionary.html',{
				rows:rows,
				callback:function(data){
					reload();
				}
			});
	    });
	}; 
	
	//修改字典
	function updateDicRowClick(id){
		lastDataId=id;
		var node = $('#'+treeId).tree('find',lastTreeId);
    	var parentNode = $('#'+treeId).tree('getParent',node.target);
    	var parenttitle = "已选择字典- "+parentNode.text+" - "+node.text;
    	parenttitle =encodeURI(parenttitle); 
    	parenttitle =encodeURI(parenttitle);
		var dicType = "id="+id+"&pointDic="+parenttitle;
		saveDicMethod("修改字典",dicType);
	}
	
	/**
	 * 添加修改字典处理
	 */
	saveDicMethod = function(title,param){
		pdpDialog.openDialog({url:'../dictionary/toSaveDictionaryPage.html?'+param,title:title,
			width:'500px',
			height:'350px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				if(lastTreeId == "0") {
					reloadTree();
				}else {
					reload();
				}
			}});
	};
	
///////////////树右键事件////////////////////	
	
	 
	 /**
	  * 右键编辑树节点
	  */
	function editTreeNodeRight(){
		$("#edit_treeNode").on("click",function(){
			 editTreeNode();
		 });
		$("#edit_treeNode1").on("click",function(){
			 editTreeNode();
		 });
	}
	 
	 /**
	  * 右键删除树节点
	  */
	function delTreeNodeRight(){
		 $("#del_treeNode").on("click",function(){
			 delTreeNode();
		 });
		 
		 $("#del_treeNode1").on("click",function(){
			 delTreeNode();
		 });
	} 
	 /**
	  * 添加节点
	  */
	 function addTreeNodeRight(){
		 $("#add_treeNode").on("click",function(){
			 	var isRoot = false;
				var r = "newTreeNode";	//设置新增加的节点的ID
		        var treeNode = $('#tree').tree('getSelected'); //获取选择的节点
		        if (treeNode == null) {
		            $.messager.alert("提示", "请选择要添加节点的位置", "info");
		            return false;
		        }
		        var parentNodeCategory = treeNode.attributes.dicCategory;
		        if(parentNodeCategory!=null){
		        	 if(parentNodeCategory == "Category"){
		 	        	parentNodeCategory = "Dic";
		 	        }else if (parentNodeCategory == "Dic"){
		 	        	showErrorTip("字典项不加子节点");
		 				return;
		 	        }
		        }else{
		        	//根节点
		        	parentNodeCategory = "Category";
		        	isRoot = true;
		        }
		        if(isRoot){	//如是根节点则直接添加字典类别
		        	$("#tree").tree('append', { //添加新增的节点
			            parent : treeNode.target, //父节点对象
			            data : [ { //节点数据
			                id : r, //节点id
			                text : "新建节点", //节点名称
			                attributes: {"parentId":treeNode.id,"dicCategory":parentNodeCategory},
			                iconCls : "treeproj-icon icon-free",	 //节点图标
			                state:open
			            } ]
			        });
			        $("#tree").tree('expand', treeNode.target);
			        var node = $('#tree').tree('find', r); //找到对应的节点
			        $('#tree').tree('beginEdit', node.target); //开始编辑节点
		        }else{//添加字典时需添加字典类型
		        	var dialogTreeId = 'dictionaryTree';
		    		var paramTree = "parentId=" + treeNode.id + "&dicCategory="+parentNodeCategory+"&ran=" + Math.random();
		    		var dialogTreeTitle = '新增';
		    		openDialogTree(dialogTreeTitle,paramTree,dialogTreeId);
		        }
		 });
	 }
	 
	 /**
	  * 左侧树弹框
	  */
	 function openDialogTree(title,param,id){
		 pdpDialog.openDialog({
				url:'../dictionary/toAddDicTreePage.html?'+param,
				title:title,
				id:id,
				width:'450px',
				height:'200px',
				reloadCallBack:function(data){
					movelog=false;
					reloadTree();
				}
			});
		}
	 
	 /**
	  * 编辑树节点
	  */
	 function editTreeNode(){
		 	var treeNode = $('#tree').tree('getSelected'); //获取选择的节点
	        if (treeNode == null) {
	            $.messager.alert("提示", "请选择要修改节点的位置", "info");
	            return false;
	        }
	        var parentNodeCategory = treeNode.attributes.dicCategory;
	        if(parentNodeCategory!=null && parentNodeCategory == "Dic"){
	        	var dialogTreeId = 'dictionaryTree';
	    		var paramTree = "id=" + treeNode.id +"&ran=" + Math.random();
	    		var dialogTreeTitle = '修改';
	    		openDialogTree(dialogTreeTitle,paramTree,dialogTreeId);
	        }else{
	        	$('#tree').tree('beginEdit',treeNode.target); //开始编辑节点
	        }
	 }
	 
	 	
	 
	 /**
	  * 删除树节点
	  */
	 function delTreeNode(){
		 var treeNode = $('#tree').tree('getSelected'); //获取选择的节点
		 var delParentTreeNode = $('#tree').tree('getParent',treeNode.target);
		 var delParentTreeNodeId = delParentTreeNode.id;
	        if (treeNode == null) {
	            $.messager.alert("提示", "请选择要删除节点的位置", "info");
	            return false;
	        }
	        var children = $('#tree').tree('getChildren', treeNode.target); //获取选择节点的子节点
	        if (children.length > 0) { //如果子节点的数量大于零,则表明有子节点,不能被删除
	            $.messager.alert("提示", "请先删除子节点", "error");
	            return false;
	        }
	        $('#tree').tree('remove', treeNode.target); //删除选择的节点
	        $.ajax({
	 		   type: "POST",
	 		   url: "../dictionary/delDictionary.html", 
	 		   dataType: "json",
	 		   data: "id="+treeNode.id,
	 		   success: function(msg){
	 			  lastTreeId = delParentTreeNodeId;
	 			  reloadTree();
	 		   }
	     });
	 }

		/**
		 * 编辑后事件,保存新增节点
		 */
		function onAfterEdit(node) {
			var js_dicName = node.text;
			var js_id = '';
			var js_parentId = '';
			var js_dicCategory = '';
	  	if(node.attributes!=null){
	  		js_id = node.id;
	  		js_parentId = node.attributes.parentId;
			js_dicCategory = node.attributes.dicCategory;
	  	}
	    $.ajax({
			   type: "POST",
			   url:"",
			   url: "../dictionary/saveDictionary.html", 
			   dataType: "json",
			   data: {
				   id:js_id,
				   dictionaryName:js_dicName,
				   parentId:js_parentId,
				   dicCategory:js_dicCategory
			   },
			   success: function(msg){
				   lastTreeId = msg.id;
				   reloadTree();
			   }
	    });
	}
	
});