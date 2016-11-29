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
	
	// 任务最大运行数
	var taskMaxNumber=0;
	
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
		var parentNodeCategory = node.attributes.type;
       // if(parentNodeCategory!=null && parentNodeCategory == "Dic"){
          if(node.id!=null){
        	 if(node.id=="0"){
        		 var options = $('#'+gridId).datagrid('options');
         		options.url = '../taskJob/findJosnList.html?registermodel=3';
         		//获取查询参数
         		options.queryParams = {
         				"conditions['groupId_eq'].value" : lastTreeId,
         				ran : Math.random()
         		};
         		reload();
         		//置灰按钮
            	$('#addDic').linkbutton('disable');
    			//$('#deleteDic').linkbutton('disable');
    			//$('#startTask').linkbutton('disable');
        	 }
        	 else{
        	//获取配置参数
    		var options = $('#'+gridId).datagrid('options');
    		//alert("111options="+options);
    		options.url = '../taskJob/findTreeJosnList.html?sourceId='+node.id+"&registermodel=3";
    	
    		//获取查询参数
    		options.queryParams = {
    				"conditions['groupId_eq'].value" : lastTreeId,
    				ran : Math.random()
    			};
    		//重新加载grid
    		reload();
    		$('#addDic').linkbutton('enable');
			$('#deleteDic').linkbutton('enable');
    		};
    		
    	
        }else{
        	//置灰按钮
        	$('#addDic').linkbutton('disable');
			//$('#deleteDic').linkbutton('disable');
			//清空datagrid
    		 $('#'+gridId).datagrid('loadData', { total: 0, rows: [] }); 
        }
        //设置优先采集
   		$("#PriorityToCollect").val("-1");
   		//设置采集状态
   		$("#collectState").val("-1");
   		//设置知识源
   		$("#knowledgeSource").val("-1");
   		//设置采集方式
   		$("#collectWay").val("-1");
	}
	
	/**
	 * 树的右键菜单
	 */
	function onTreeContextMenu(e, node){
		e.preventDefault();
		//有添加的快捷菜单
		$('#treeRootMenu').menu('show', {
			left: e.pageX,
			top: e.pageY
		});
		/*//e.stopPropagation();//阻止冒泡
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
        }*/
	}
	
	//树加载完成事件
	function onTreeLoadSuccess(node, data) {
		var treeDataLength = data.length;
		//alert("treeDataLength="+data.length);
		if(treeDataLength > 0) {
			if(lastTreeId == "") {
				//alert("0000");
				var roots=$('#tree').tree('getRoots');
				//alert("roots="+roots[0].target);
				$('#'+treeId).tree('select',roots[0].target);
				/*$('#addDic').linkbutton('enable');
				$('#deleteDic').linkbutton('enable');*/
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
					$('#addDic').linkbutton('enable');
					$('#deleteDic').linkbutton('enable');
				}
			}
		}
	}
	
	
	//初始化tree接口
	/*exports.treeLoad = function(inGridId,inGridUrl,parentIdNode) {
		if(parentIdNode != "" && parentIdNode != undefined && parentIdNode != null) lastTreeId = parentIdNode;
		//请求连接
		var url = inGridUrl;
		return tree.init(treeId,{
			url:url,
			onSelect:onTreeSelect,
			onContextMenu:onTreeContextMenu,
			onAfterEdit:onAfterEdit,
			onLoadSuccess:onTreeLoadSuccess
		});
	};*/
	
	
	
	//初始化方法
	exports.init = function(isShowValue,ntaskMaxNumber){
		showValue = isShowValue;
		taskMaxNumber = ntaskMaxNumber;
		initButtonEvent();
		onSearchButtonClick();
		//右键添加树节点
		addTreeNodeRight();
		//右键编辑树节点
		editTreeNodeRight();
		//右键删除树节点
		delTreeNodeRight();
		setInterval("foreachLoad();",30000); // 每10分钟刷新一次页面（600000）
	};
	// 定时刷新页面
	foreachLoad = function(){
		reload();
	};
	
	exports.gridLoad = function(type,value){
		var knowledgeSourceVal=$("#knowledgeSource option:selected").val();
		var collectWayVal="";
		var url='';
		var param;
		
		if(type=="PriorityToCollect" || type=="collectState" || type=="knowledgeSource" ||  type=='collectWay'){
			url="../taskJob/fuzzyQueryTaskList.html";
			param={'registermodel':3,"collectStateVal":value};
		}else if(type=='collectAutomatic'){
			url="../taskJob/fuzzyQueryTaskList.html";
			collectWayVal=value;
			param = {'registermodel':3,"collectWayVal":collectWayVal};
		}else if(type=='collectDate'){
			url="../taskJob/fuzzyQueryTaskList.html";
			var date=value.split(";");
			param={'registermodel':3,"startTime":date[0],"stopTime":date[1],"collectDateVal":1};
		}else if(type=="collectTasknameVal"){
			url="../taskJob/fuzzyQueryTaskList.html";
			param={'registermodel':3,"Namesearch":value};
		}else if(type=="search_button"){
			url="../taskJob/fuzzyQueryTaskList.html";
			param = {'registermodel':3,"search":value};
		}else if(type!=null && type!="" && type!=undefined){
			//请求连接
			 url = '../taskJob/positiveSequenceOrReverseSwitchTasks.html';
		}else{
			//请求连接
			 url = '../taskJob/findJosnList.html';
			 param = {'registermodel':3}
		}
		//获取输入的参数
		var search= $("#search").val();
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
		               /* {field:'systemName',halign:'left',align:'left',width:200,title:'传输系统'},
		                {field:'knowledgeModelName',halign:'left',align:'left',width:150,title:'知识模板'},*/
		                {field:'name',halign:'left',align:'left',width:200,title:'<a id="taskName" class="taskName" onClick="nameClick(event,this)">采集任务名称</a>',
		                	formatter:function(val,row){
		                		var _taskId = row.id;
		               			errorFlag="0";
		               			// 获取任务是否出错
		               			$.ajax({
		               				async:false,
		               				type:'POST',
		               				url:"../taskJob/getTaskErrorFlag.html",
		               				dataType:'json',
		               				data:{
		               					taskId:_taskId,
		               				},
		               				success:function(result){
		               					errorFlag=result.errorFlag;
		               				}
		               			});
		               			//是否有新数据
		               			var hasNewData='';
		               			if(row.jobState=='3'&&row.hasNewData=='1'){
		               				hasNewData='有新数据！';
		               			}
		                		var nameVal = val;
		                		var len = 15;
		                		if(val.length > len){
		                			nameVal = val.substr(0,len)+'...';
		                		}
		                		var rtnVal = '<a id="taskNameClick_'+row.id+'" class="easyui-linkbutton taskNameClick" iconCls="icon-edit" plain="true" href="javascript:void(0)" onClick="taskNameClick()"  title="' + val + '" >'+nameVal+'</a>';
		                		if(hasNewData != ''){
		                			rtnVal += '<sup style="color:red;">'+hasNewData+'</sup>';
		                		}
		                		if(errorFlag!="0"){
		                			rtnVal += '<sup style="color:red;">！！任务报错</sup>';
		                		}
		                		return rtnVal;
		                	}		
		               	},
		               	{field:'sourceName',halign:'left',align:'left',width:200,title:'知识源',
		               		formatter:function(val,row){
		               			var nameVal="";
		               			if(val!=null && val!="" && val!=undefined){
		               				//alert(val);
		               				nameVal= val;
			                		var len = 15;
			                		if(val.length > len){
			                			nameVal = val.substr(0,len)+'...';
			                		}
			                		return nameVal;
		               			}
		                	}	
		               	},
		               	/*{field:'createDate',halign:'center',align:'center',width:160,title:'<a id="createDateClick" class="createDateClick" onClick="createDateClick()">创建时间</a>'},*/
		               	{field:'createDate',halign:'center',align:'center',width:160,title:'<a id="createDateClick" class="createDateClick" onClick="createDateClick(event)">创建时间</a>'},
		               	{field:'automatic',halign:'center',align:'center',width:80,title:'<a id="checkAutomatic" class="automaticClick" onClick="automaticClick(event)">传输方式</a>',
		               		formatter:function(val,row){
		                		if(val=='0')
		                			return '自动';
		                		else 
		                			return "手动";
		                	}	
		               	},
		            	{field:'datumCount',halign:'center',align:'center',width:80,title:'传输数据量'},
		               	{field:'jobState',halign:'center',align:'center',width:80,title:'<a id="checkState" class="checkStateClick" onClick="checkStateClick(event)">状态</a>',
		               		formatter:function(val,row){
		                		if(val=='0'){return '就绪';}
		                		else if(val == '1'){return '进行中';}
		                		else if(val=='2'){return '暂停';}
		                		else if(val=='3'){
		                			return '停止';
		                		}/*else if(val=='4'){
		                			return "已完成";
		                		}else if(val == '5'){
		                			return "等待中";
		                		}*/
		                	}	
		               	},
		            	/*{field:'priority',halign:'center',align:'center',width:80,title:'优先级',
		               		formatter:function(val,row){
		               			if(val=='0'){
		               				return "<select class='easyui-combobox priority' id='dynamicPriority_"+row.id+"' style='width:66px;'>"
		               						+"<option value='0'>普通</option>"
		               						+"<option value='1'>优先</option>"
		               						+"</select>";
		               			}else{
		               				return "<select class='easyui-combobox priority' id='dynamicPriority_"+row.id+"' style='width:66px;'>"
		               				+"<option value='1'>优先</option>"
               						+"<option value='0'>普通</option>"
               						+"</select>";
		               			}
		               		}
		               	},*/
		               /*	{field:'priority',halign:'center',align:'center',width:60,title:'优先级',
		               		formatter:function(val,row){
		               			if(val=='0'){
		               				return "普通";
		               			}else if(val=='1'){
		               				return "高";
		               			}
		               		}
		               	},
		               	{field:'operateTransfer',halign:'center',align:'center',width:80,title:'操作',
		               		formatter:function(val,row){
			                	return '<a id="delete_'+row.id+'" class="easyui-linkbutton deleteDic" iconCls="icon-edit" plain="true" href="javascript:void(0)">删除</a><a id="update_'+row.id+'|'+row.jobState+'" class="easyui-linkbutton updateDic" iconCls="icon-edit" plain="true" href="javascript:void(0)">编辑</a>'; 
			                }
		                }
		               	{field:'operateTransfer',halign:'center',align:'center',width:135,title:'操作',
		                	formatter:function(val,row){
			                	return '<div style="width: 120px;"><a id="update_'+row.id+'|'+row.jobState+'" class="tb-btn tb-btn-edit updateDic" href="javascript:void(0)">编辑</a></div><a id="delete_'+row.id+'" class="tb-btn tb-btn-del deleteDic" href="javascript:void(0)">删除</a>';
			                }
		                }*/
		               	{field:'operateExtract',halign:'center',align:'center',width:135,title:'操作',
		                	formatter:function(val,row){
			                	return '<div style="width: 120px;"><a id="update_'+row.id+'|'+row.jobState+'" class="tb-btn tb-btn-edit updateDic" href="javascript:void(0)">编辑</a><a id="delete_'+row.id+'" class="tb-btn tb-btn-del deleteDic" href="javascript:void(0)">删除</a></div>';
			                }
		                }
		               	
		              ]];
		/*//列
		var columns = [[
		                //要显示的列
		                {field:'id',checkbox:true},
		               	{field:'name',halign:'center',align:'left',width:120,title:'任务名称'},
		               	{field:'jobState',halign:'center',align:'left',width:120,title:'状态'},
		               	{field:'operate',halign:'center',align:'center',width:150,title:'操作',
			                formatter:function(val,row){
			                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateDic" iconCls="icon-edit" plain="true" href="javascript:void(0)">编辑</a>'; 
			                }
		                },{field:'monitoring',halign:'center',align:'center',width:150,title:'任务监控',
			                formatter:function(val,row){
			                	return '<a id="select_'+row.id+'" class="easyui-linkbutton select" iconCls="icon-edit" plain="true" href="javascript:void(0)">查看</a>'; 
			                }
		                }
		              ]];*/
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
			singleSelect:false,
			onLoadSuccess:onGridLoadSuccess
		}); 
	};
	//状态选择
	checkStateClick = function(e){
		if((navigator.userAgent.indexOf('MSIE') >= 0) 
			    && (navigator.userAgent.indexOf('Opera') < 0)){
			 window.event.cancelBubble = true;
		}else{
			e.stopPropagation();
		}
		$('#checkRegisterSlt').remove();
		$('.combo_date').remove();
		$('#combo_search').remove();
		$('#checkAutomaticSlt').remove();
		if($('#checkStateSlt').length == 1){
			return false;
		}
		var newSlt = $('<select id="checkStateSlt"><option>请选择</option><option value="-1">全部</option><option value="0">就绪</option><option value="1">进行中</option><option value="2">暂停</option><option value="3">停止</option></select>');
		$('.datagrid-view2').append(newSlt);
		newSlt.css({
			'position' : 'absolute',
			'left' : '80.3%',
			'width' : '74px',
			'top' : '40px',
			'marginLeft' : '0px',
			'z-index' : 100
		});
		$('#checkStateSlt').on('click',function(e){
			var oEvent = e || window.event;
			oEvent.stopPropagation();
		});
		$('#checkStateSlt').on('change',function(){
			var theVal = parseInt($(this).children('option:selected').val());
			switch(theVal){
				case -1: exports.gridLoad("collectState",-1);
				break;
				case 0: exports.gridLoad("collectState",0);
				break;
				case 1: exports.gridLoad("collectState",1);
				break;
				case 2: exports.gridLoad("collectState",2);
				break;
				case 3: exports.gridLoad("collectState",3);
				break;
			}
			newSlt.remove();
		})
	};
	//转换方式选择
	automaticClick = function(e){
		if((navigator.userAgent.indexOf('MSIE') >= 0) 
			    && (navigator.userAgent.indexOf('Opera') < 0)){
			 window.event.cancelBubble = true;
		}else{
			e.stopPropagation();
		}
		$('#checkRegisterSlt').remove();
		$('.combo_date').remove();
		$('#combo_search').remove();
		$('#checkStateSlt').remove();
		if($('#checkAutomaticSlt').length == 1){
			return false;
		}
		var newSlt = $('<select id="checkAutomaticSlt"><option>请选择</option><option value="-1">全部</option><option value="1">手动</option><option value="0">自动</option></select>');
		$('.datagrid-view2').append(newSlt);
		newSlt.css({
			'position' : 'absolute',
			'left' : '70.3%',
			'width' : '74px',
			'top' : '40px',
			'marginLeft' : '0px',
			'z-index' : 100
		});
		$('#checkAutomaticSlt').on('click',function(e){
			var oEvent = e || event;
			oEvent.stopPropagation();
		});
		$('#checkAutomaticSlt').on('change',function(){
			var theVal = parseInt($(this).children('option:selected').val());
			switch(theVal){
				case -1: exports.gridLoad("collectAutomatic",-1);
				break;
				case 0: exports.gridLoad("collectAutomatic",0);
				break;
				case 1: exports.gridLoad("collectAutomatic",1);
				break;
			}
			newSlt.remove();
		})
	};
	//点击时间排序
	createDateClick=function(e){
		if((navigator.userAgent.indexOf('MSIE') >= 0) 
			    && (navigator.userAgent.indexOf('Opera') < 0)){
			 window.event.cancelBubble = true;
		}else{
			e.stopPropagation();
		}
		$('#checkRegisterSlt').remove();
		$('.combo_date').remove();
		$('#combo_search').remove();
		$('#checkAutomaticSlt').remove();
		$('#checkStateSlt').remove();
		if($('.combo_date').length == 1){
			return false;
		}
		var today = new Date;
		var todayVal = today.getFullYear()+"-"+(today.getMonth()+1)+"-"+today.getDate();
		var startTime = '';
		var endTime = todayVal;
		var newDate = $('<div class="combo_date"><input id="startTime" type="text"></input>-<input id="endTime" type="text"></input></div>')
		$('.datagrid-view2').append(newDate);
		//创建开始日期输入框
		$('#startTime').datebox({ required : true});
		//创建截止日期输入框
		$('#endTime').datebox({required : true});
		//设置默认截止日期
		$('#endTime').datebox('setValue',endTime);
		//选定开始日期
		$('#startTime').datebox({
	    	onSelect: function(date){
		        startTime = date.getFullYear()+"/"+(date.getMonth()+1)+"/"+date.getDate();
		        if(endTime != todayVal){
	        		newDate.remove();
	        		exports.gridLoad("collectDate", startTime+";"+endTime);
		        }
		    }
		});
		//选定截止日期
		$('#endTime').datebox({
	    	onSelect: function(date){
		        endTime = date.getFullYear()+"/"+(date.getMonth()+1)+"/"+date.getDate();
		        if(startTime != ''){
		        	
		        	newDate.remove();
		        	exports.gridLoad("collectDate", startTime+";"+endTime);
		        }
		    }
		});
		

		var w = $('#startTime').width();
		$('.combo_date').css({
			'position' : 'absolute',
			'left' : '60%',
			'top' : '41px',
			'marginLeft' : -w + 'px',
			'z-index' : 100
		});
		$('.combo_date').on('click',function(e){
			var oEvent = e || event;
			oEvent.stopPropagation();
		});
		$('.combo-p').on('click',function(e){
			var oEvent = e || event;
			oEvent.stopPropagation();
		});
	};
	//点击任务名称表头筛选
	nameClick = function(e,ele){
		if((navigator.userAgent.indexOf('MSIE') >= 0) 
			    && (navigator.userAgent.indexOf('Opera') < 0)){
			 window.event.cancelBubble = true;
		}else{
			e.stopPropagation();
		}
		var newTxt = $('<div id="combo_search" class="combo_search"><input id="searchByThVal" type="text" class="txt" style="width:145px;color:#CCC" value="输入筛选关键字"></div>')
		$(ele).parent().append(newTxt);
		$(ele).parent().css({
			'position' : 'relative'
		})
		$(ele).siblings('.combo_search').css({
			/*'position' : 'absolute',
			'left' : 0,
			'top' : '-9px',
			'z-index' : 100*/
			'width' : '145px',
			'position' : 'absolute',
			'left' : 0,
			'top' : '-5px',
			'z-index' : 100
		});
		//点击默认提示字符消失
		$("#searchByThVal").focus(function(){
			this.value="";
			$("#searchByThVal").css({"color":"#000"});
		});
			
		
		//回车搜索
		$('#searchByThVal').on('click',function(event){
			var oEvent = event || widnow.event;
			oEvent.stopPropagation();
		});
		$('#searchByThVal').on('keydown',function(e){
			var oEvent = e || window.event;
			if(oEvent.keyCode==13){
				exports.gridLoad("collectTasknameVal", $(this).val());
				newTxt.remove();
			}
		});
		};
		//点击隐藏
		document.onclick = function(){
			$('#checkStateSlt').remove();
			$('#checkRegisterSlt').remove();
			$('.combo_date').remove();
			$('#checkAutomaticSlt').remove();
			$('#combo_search').remove();
		};
	taskMonitoring = function(title,param){
		/* 修改备注（yongqian.liu）：修改为 window.open 方式打开窗口
		pdpDialog.openDialog({url:'../taskJobMonitor/toTaskJobMonitorPage.html?'+param,title:title,
			width:'790px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				if(lastTreeId == "0") {
					reloadTree();
				}else {
					reload();
				}
			}});*/
		var url = '../taskJobMonitor/toTaskJobMonitorPage.html?'+param;
		var iWidth=800; //弹出窗口的宽度;
		var iHeight=600; //弹出窗口的高度;
		var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		var layout = 'width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no';
		window.open(url,title,layout);
	};
	
	var sortType = 'asc';
	//点击类型排序
	typeClick=function(){
		if(sortType == 'asc'){
			//desc
			sortType= 'desc';
			exports.gridLoad("asc");
		}else{
			//asc
			sortType = 'asc';
			exports.gridLoad("desc");
		}
	};
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
	function ajaxFunction(dataId){
		$.ajax({  
		    url: '../taskJob/deleteTaskJob.html',
		    data: {
		    	"listId":dataId,
		    	"type":'3'
		    },  
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
	//操作列表调用的方法
	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			//点击修改触发
			if(target.hasClass("updateDic")){
				id = id.substring(7, id.length);
				var strs=new Array();
				strs=id.split("|");
				var jobId=strs[0];
				var jobState=strs[1];
				target.on('click',function(){updateDicRowClick(jobId,jobState);});
			}else if(target.hasClass("deleteDic")){
				id = id.substring(7, id.length);
				target.on('click',function(){deleteClick(id);});
				//点击查看触发
			}else if(target.hasClass("select")){
					id = id.substring(7, id.length);
					target.on('click',function(){getMonitoringInfo(id);});
			}else if(target.hasClass("taskNameClick")){
				id = id.substring(14, id.length);
				target.on('click',function(){taskNameClick(id);});
			}
		});
		//修改优先级
		$("#js_center .datagrid-btable select").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			if(target.hasClass("priority")){
				id = id.substring(16, id.length);
				target.on('change',function(){
					var priority= $(this).find("option:selected").val();
					updateTaskJobPriorityById(id,priority);
				});
			}
			
		});
	};
	
	//修改字典
	function taskNameClick(id){
		if(id==null || id=="" || id==undefined){
			alert("编号不能为空！");
			return false;
		}
    	taskMonitoring("",'id='+id+"&registerType="+null+"&registerModel=3");
	}
	//修改优先级
	function updateTaskJobPriorityById(id,priority){
		
	 	$.ajax({
	 		   type: "POST",
	 		   url: "../taskJob/updateTaskJobPriorityById.html", 
	 		   dataType: "json",
	 		  data: {
			    	"id":id,
			    	"priority":priority
			   },  
	 		   success: function(msg){
	 			  reload();
	 		   }
	     });
	}
	
/*	function editColumnFn(){
		$("#js_center .datagrid-btable a").each(function(i,obj){
			var target = $(obj);
			var id = target.attr("id");
			//点击查看触发
			if(target.hasClass("select")){
				id = id.substring(7, id.length);
				target.on('click',function(){getMonitoringInfo(id);});
			}
		});
		
	}*/
	
	

	//datagrid 加载成功事件
	function onGridLoadSuccess(data) {
		/* if(showValue == 'true'){
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
	
	/**
	 * 添加字典
	 */
	initButtonEvent = function(){
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
	    	// 修改备注（yongqian.liu）: groupId 参数删除
	    	saveTaskMethod("添加传输任务");
	    });
	    /* 修改备注（yongqian.liu）: 此方法暂未使用
	    $("#updateDic").on("click",function(){
	    	var rows = $("#"+gridId).datagrid("getSelections");
	    	if(rows.length > 1) {
				alert("只能选择一条数据！");
				return;
			}
	    	lastDataId=rows[0].id;
	    	var jobType = 'id='+rows[0].id+'&groupId='+lastTreeId+'&name='+rows[0].name;
	    	saveTaskMethod("修改任务",jobType);
	    });*/
	    //删除方法
	    $("#deleteDic").on("click",function(){
/*			var rows = $('#'+gridId).datagrid('getChecked');
			submitDel('../taskJob/delTransferTaskJob.html',{
				rows:rows,
				callback:function(data){
					reload();
				}
			});*/
	    	var rows = $("#"+gridId).datagrid("getSelections");
			if(rows.length < 1) {
				alert("至少选择一条数据！");
				return;
			}
			var dataId=[];
			var i=0;
			for (var int = 0; int < rows.length; int++) {
				var id=rows[int].id;
				if(!(id==null||id.length==0)){
					i++;
					dataId[i]=id;
				}
			}
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
	    });
	    /*$("a[name='startTask']").on("click",function(){
		  	var rows = $("#"+gridId).datagrid("getSelections");
		  	var idArr = [];
		  	if(Util.array.isArray(rows)) {
		  		$.each(rows,function(i,obj){
		  			if(obj.priority == '1'){
		  				idArr.unshift(obj.id);
		  			}else{
		  				idArr.push(obj.id);
		  			}
		  		});
		  	}else {
		  		if(opts.rows.priority == '1'){
		  			idArr.unshift(opts.rows.id);
		  		}else{
		  			idArr.push(opts.rows.id);
		  		}
		  	}
		  	if(idArr.length<=0){
		  		alert("请选择要开始的任务！");		
		  		return ;
		  	}
		  	var ids = idArr.join(',');
		  	$.ajax({
			 		   type: "POST",
			 		   url: "../quartzSchedule/startTask.html", 
			 		   dataType: "json",
			 		   data: "id="+ids,
			 		   success: function(msg){
			 			  reload();
			 		   }
			     });
		  	});*/
	    $("a[name='startTask']").on("click",function(){
	    	var nRunCountSum = 0;
	    	$.ajax({
		 		   type: "POST",
		 		   async: false,
		 		   url: "../taskJob/getRunTaskCount.html", 
		 		   dataType: "json",
		 		   data: {},
		 		   success: function(result){
		 			  nRunCountSum = result.count;
		 		   }
		     });
	    	//alert(nRunCountSum);
	    	var rows = $("#"+gridId).datagrid("getSelections");
		  	var idArr = [];
		  	var selRunSum = 0;
		  	if(Util.array.isArray(rows)) {
		  		$.each(rows,function(i,obj){
		  			/*if(obj.priority == '1'){
		  				idArr.unshift(obj.id);
		  			}else{
		  				idArr.push(obj.id);
		  			}*/
		  			//alert(obj.jobState);
	  				idArr.push(obj.id);
	  				if(obj.jobState == '1'){
	  					selRunSum = selRunSum + 1;
	  				}
		  		});
		  	}else {
		  		/*if(opts.rows.priority == '1'){
		  			idArr.unshift(opts.rows.id);
		  		}else{
		  			idArr.push(opts.rows.id);
		  		}*/
		  		idArr.push(opts.rows.id);
		  		if(obj.jobState == '1'){
  					selRunSum = selRunSum + 1;
  				}
		  	}
		  	var nSum = 0;
		  	if(idArr.length<=0){
		  		alert("请选择要开始的任务！");		
		  		return ;
		  	}else{
		  		nSum = idArr.length + nRunCountSum - selRunSum;
		  		if(nSum > taskMaxNumber){
		  			alert("服务器最多可支持" + taskMaxNumber + "个任务同时运行，您勾选的任务启动失败");	
			  		return ;
			  	}
		  		}
		  	
		  	
		  	var ids = idArr.join(',');
		  	$.ajax({
			 		   type: "POST",
			 		   url: "../quartzSchedule/startTask.html", 
			 		   dataType: "json",
			 		   data: "id="+ids,
			 		   success: function(msg){
			 			  reload();
			 			 alert("任务启动成功！");
			 		   }
			     });
		  	});
	    $("a[name='pauseTask']").on("click",function(){
		  	var rows = $("#"+gridId).datagrid("getSelections");
		  	var idArr = [];
		  	if(Util.array.isArray(rows)) {
		  		$.each(rows,function(i,obj){
		  			idArr.push(obj.id);
		  		});
		  	}else {
		  		idArr.push(opts.rows.id);
		  	}
		  	if(idArr.length<=0){
		  		alert("请选择要暂停的任务！");		
		  		return ;
		  	}
		  	var ids = idArr.join(',');
		  	$.ajax({
			 		   type: "POST",
			 		   url: "../quartzSchedule/pauseTask.html", 
			 		   dataType: "json",
			 		   data: "id="+ids,
			 		   success: function(msg){
			 			  reload();
			 			 alert("任务暂停成功！");
			 		   }
			     });
		  	});
		  	$("a[name='stopTask']").on("click",function(){
			  	var rows = $("#"+gridId).datagrid("getSelections");
			  	var idArr = [];
			  	if(Util.array.isArray(rows)) {
			  		$.each(rows,function(i,obj){
			  			idArr.push(obj.id);
			  		});
			  	}else {
			  		idArr.push(opts.rows.id);
			  	}
			  	if(idArr.length<=0){
			  		alert("请选择要停止的任务！");		
			  		return ;
			  	}
			  	var ids = idArr.join(',');
			  	$.ajax({
				 		   type: "POST",
				 		   url: "../quartzSchedule/stopTask.html", 
				 		   dataType: "json",
				 		   data: "id="+ids,
				 		   success: function(msg){
				 			  reload();
				 			 alert("任务停止成功！");
				 		   }
				     });
			  	});
	}; 
	
	//获取id
	function getMonitoringInfo(id){
		var rows = $("#"+gridId).datagrid("getSelections");
		if(rows.length>1){
			alert("只能选择一条数据！");
			return;
		}
		getMonitoring("","id="+id);
	}
	//弹出一个窗口
	getMonitoring=function(title,param){
		pdpDialog.openDialog({url:'../taskJob/toTaskTransmissionJobPage.html?'+param,
			title:title,
			width:'790px',
			height:'550px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				if(lastTreeId == "0") {
					reloadTree();
				}else {
					reload();
				}
			}});
	}
	
	
	
	
	//修改字典
	function updateDicRowClick(id,jobState){
		if(id==null || id=="" || id==undefined){
			alert("编号不能为空！");
			return false;
		}
		//var sourceId=$('#'+treeId).tree('find',lastTreeId);
		/*var node = $('#'+treeId).tree('find',lastTreeId);
    	var parentNode = $('#'+treeId).tree('getParent',node.target);
    	var parenttitle = "已选择字典- "+parentNode.text+" - "+node.text;
    	parenttitle =encodeURI(parenttitle); 
    	parenttitle =encodeURI(parenttitle);*/
    	if(jobState=="0"||jobState=="2"||jobState=="3"){
    		// 修改备注（yongqian.liu）：groupId 参数删除
    		var dicType = "id="+id; // +'&groupId='+lastTreeId +"&pointDic="+parenttitle;
    		editTaskMethod("修改任务",dicType);
    	}else{
    		alert("任务正在进行中，不可修改！");
    	}
		
	}
	
	
	/**
	 * 添加传输任务处理
	 */
	saveTaskMethod = function(title,param){
		/* 修改备注（yongqian.liu）：修改为 window.open 方式打开窗口
		pdpDialog.openDialog({url:'../taskJob/toSaveTransferTaskPage.html?'+param,
			title:title,
			width:'650px',
			height:'420px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				if(lastTreeId == "0") {
					reloadTree();
				}else {
					reload();
				}
			}});*/
		var url = '../taskJob/toSaveTransferTaskPage.html?'+param;
		var iWidth=800; //弹出窗口的宽度;
		var iHeight=600; //弹出窗口的高度;
		var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		var layout = 'width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no';
		window.open(url,title,layout);
	};
	
	/**
	 * 编辑传输任务处理
	 */
	editTaskMethod = function(title,param){
		/* 修改备注（yongqian.liu）：修改为 window.open 方式打开窗口
		pdpDialog.openDialog({url:'../taskJob/toEditTransferTaskPage.html?'+param,
			title:title,
			width:'650px',
			height:'420px',
			reloadCallBack:function(data){
				movelog=false;
				lastDataId = data.id;
				if(lastTreeId == "0") {
					reloadTree();
				}else {
					reload();
				}
			}});*/
		var url = '../taskJob/toEditTransferTaskPage.html?'+param;
		var iWidth=800; //弹出窗口的宽度;
		var iHeight=600; //弹出窗口的高度;
		var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
		var layout = 'width='+iWidth+',height='+iHeight+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no';
		window.open(url,title,layout);
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
		 	        	alert("字典项不加子节点");
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
	 		   url: "../taskGroup/delTaskGroup.html", 
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
			var treeNode = $('#tree').tree('getSelected'); //获取选择的节点
			var pid=null;
			var type=null;
			if(treeNode.text=="数据转换"){
				pid="2";
				type="2";
			}
			else if(treeNode.text=="数据采集"){
				pid="1";
				type="1";
			}
			else if(treeNode.text=="数据传输"){
				pid="3";
				type="3";
			}
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
			   url: "../taskGroup/saveOrUpdateGroup.html", 
			   dataType: "json",
			   data: {
				   id:js_id,
				   dictionaryName:js_dicName,
				   parentId:js_parentId,
				   dicCategory:js_dicCategory,
				   pid:pid,
				   type:type
			   },
			   success: function(msg){
				   lastTreeId = msg.id;
				   reloadTree();
			   }
	    });
	}
		//查询事件
		$(function(){
			$("#search_button").on("click",function(){
				//获取输入的参数
				var search= $("#search").val();
				exports.gridLoad("search_button",search);
			});
		});
		//条件筛选
		 $(function(){
			//优先采集改变事件
			$("#PriorityToCollect").change(function(){
				exports.gridLoad("PriorityToCollect");
			});
			//采集状态改变事件
			$("#collectState").change(function(){
				exports.gridLoad("collectState");
			});
			//知识源改变事件
			$("#knowledgeSource").change(function(){
				exports.gridLoad("knowledgeSource");
			});
			//采集方式改变事件
			$("#collectWay").change(function(){
				exports.gridLoad("collectWay");
			});
		});
	
});