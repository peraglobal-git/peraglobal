/**
 * 部门树JS
 * 可以传入两个参数
 * 	1、gridId    传入数据表格ID
 *  2、gridUrl   传入数据表格URL 链接
 *  
 *  左边树,右边表格结构,表格先初始化,不加载数据,然后根据树的点击加载表格的数据
 */
define(function(require,exports,module){
	//easyui Tree树
	var tree  = require('tree');

	//datagrid table Id
	var gridId = "grid";
	
	//tree ul Id
	var treeId = "tree";
	
	//上一次点击树的节点ID
	var lastTreeId = "";
	
	//链接
	var gridUrl = '';
	
	//使用的模块
	var useModel = '';
	
	
	
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
	

	//树 选中事件
	function onTreeSelect(node) {
		//记录id
		lastTreeId = node.id;
		
		//获取配置参数
		var options = $('#'+gridId).datagrid('options');
		
		options.url = gridUrl;
		
		
		//获取查询参数
		if(useModel == "org") {
			options.queryParams = {
					"conditions['parentId_eq'].value" : node.id,
					ran : Math.random()
			};
		}
		
		if(useModel == "user") {
			options.queryParams = {
					"conditions['orgId_eq'].value" : node.id,
					ran : Math.random()
			};
		}
		
	
		//重新加载grid
		//reload();
		//$("#parentId").val(node.id);
		$('#grid').datagrid("load");//,Util.ajax.serializable($("#listForm")));
	}
	
	
	//树加载完成事件
	function onTreeLoadSuccess(node, data) {
		var treeDataLength = data.length;
		if(treeDataLength > 0) {
			if(lastTreeId == "") {
				var roots=$('#tree').tree('getRoots');
				$('#'+treeId).tree('select',roots[0].target);
			}else {
				var treeNode = $('#'+treeId).tree('find',lastTreeId);
				if(treeNode != undefined) {
					$('#'+treeId).tree('select', treeNode.target);
				}
			}
		}
	}
	
	
	//初始化tree接口
	exports.treeLoad = function(inGridId,inGridUrl,model) {
		useModel = model;
		
		if(inGridId != "" && inGridId != undefined && inGridId != null)	gridId = inGridId;
		
		if(inGridUrl != "" && inGridUrl != undefined && inGridUrl != null) gridUrl = inGridUrl;
		
		//请求连接
		var url = '../org/getOrgTree.html';
		
		return tree.init(treeId,{
			url:url,
			onSelect:onTreeSelect,
			onLoadSuccess:onTreeLoadSuccess
		});
	};
	

});