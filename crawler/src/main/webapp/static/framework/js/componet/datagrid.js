define(function(require, exports, module) {
	var _gridId=null;
	//加载完成后的默认方法
	var onLoadSuccess = function(data) {
		var grid = $(this);
		//先取消所有选择的节点
		grid.datagrid('clearSelections');
		var rowlength = grid.datagrid("getRows").length;
		if(_option.selectFirstRow){
			if( rowlength > 0) {
				grid.datagrid('selectRow',0);
			}
		}
		//处理翻页后删除数据后，出现分页问题
		var pager = grid.datagrid("getPager");
		if(pager.length>0){
			var pagerOpt = pager.pagination("options");
			if(pagerOpt.pageNumber>1 && rowlength<=0){
				var pageNum = pagerOpt.pageNumber-1;
				//重写选择上一页
				pager.pagination('select',pageNum);	
			}
		}
		//处理单元格内容超出隐藏
		datagridCellTextHidden(grid.datagrid("options").columnWidth);
		if(_option.onLoadSuccessCall && Util.fn.isFn(_option.onLoadSuccessCall)){
			_option.onLoadSuccessCall(data);
		}
		
		var lastIndex = $.cookie("lastIndex");
 		var pager = grid.datagrid("getPager");
		if("true" == lastIndex){
			if(pager.length>0){
			 	var options = pager.pagination("options");
				var total = data.total;
				var max = Math.ceil(total/options.pageSize);
				var curr = options.pageNumber;
				if(curr != max){
					pager.pagination('select',max);
					return false;
				}
			}
			$.cookie("lastIndex", "false");
	 	}
	 	
	 	/* datagrid 行号列宽自适应（处理大数据行号显示不全的问题） */
		if(pager.length>0){
		 	var options = pager.pagination("options");
		 	var rowN = grid.datagrid('getRowNum');
		 	if('' == rowN){
		 		rowN = $("#datagrid-row-r1-1-0 td.datagrid-td-rownumber").text();
		 	}
		 	var pageS = parseInt(rowN) + parseInt(options.pageSize) - parseInt(1); //本页开始，本页结束 --行号
		 	$(".datagrid-cell-rownumber").width(pageS.toString().length+'5');
		 	$(".datagrid-header-rownumber").width(pageS.toString().length+'5');
		}
		
	 	/* 
		 * 操作列'权限过滤（rowPermission） 
		 * 强调(操作列field必须为"operate"，class必须为"easyui-linkbutton permissionCode")
		 * “permissionCode” 为引用
		 * 例如：
		 * {field:'operate',halign:'center',align:'center',width:150,title:'操作',
                formatter:function(val,row){
                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>&nbsp; <a id="up_'+row.id+'" class="easyui-linkbutton upNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">上移</a>&nbsp; <a id="down_'+row.id+'" class="easyui-linkbutton downNav" iconCls="icon-edit" plain="true" href="javascript:void(0)">下移</a>'; 
                }
            }
		 */
		if(_option.rowPermission== undefined || _option.rowPermission==true){
			grid.datagrid('rowPermission');
		}
		
		/*
		 * 处理删除后返回页面加载
		 */
		if(/^[0-9]\d*$/.test($.cookie("delPager"))){
			var delrowscon = Math.ceil($.cookie("delPager"));
			var pager = grid.datagrid("getPager");
			$.cookie("delPager", "false");
			if(pager.length>0){
			 	var options = pager.pagination("options");
				var curr = options.pageNumber;
				if(grid.datagrid("getRows").length+1>delrowscon){
					pager.pagination('select',curr);
					return false;
				}else{
					if(curr>1){
						pager.pagination('select',Math.ceil(curr));
					}else{
						pager.pagination('select',1);
					}
					return false;
				}
			}
	 	}
	};

	//数据加载失败事件
	var onLoadError = function(jqXHR){
		Util.ajax.ajaxRequestError(jqXHR);
	};
	
	//grid的配置项
	var _option = {
			url:'',		//数据表格数据来源,json格式的数据
			fit:true,						//数据表格自动适应大小
			striped: true,					//是否以斑马线样式显示数据
			pagination:true,				//是否使用分页显示
			rownumbers:true,				//是否显示行号
			singleSelect:false,				//是否单行选择
			columns:[[{field:'id',checkbox:true}]],//加载的列
			frozenColumns:[[]],				//冻结的列
			fitColumns:false,				//列自适应宽度
			resizeHandle:'right',			//调整列的位置，可用的值有：'left','right','both'
			autoRowHeight:'false',			//定义设置行的高度，根据该行的内容。设置为false可以提高负载性能
			method:'post',					//设置请求参数类型
			nowrap:false,					//是否在同一行显示数据，true可以提高性能
			loadMsg:'加载中，请稍候',			//加载数据过程显示
			pageNumber:1,					//在设置分页属性的时候初始化页码
			pageSize:20,					//在设置分页属性的时候初始化页面大小
			pageList:[20, 30, 40, 50, 100],		//分页选择显示条数
			queryParams:{},					//在请求远程数据的时候发送额外的参数
			sortName:null,					//定义哪些列可以进行排序
			idField:'id',					//id字段 支持记忆选择
			remoteSort:false,				//定义从服务器对数据进行排序
			showHeader:true,				//定义是否显示行头
			showFooter:false,				//定义是否显示行脚
			scrollbarSize:18,				//滚动条的宽度(当滚动条是垂直的时候)或高度(当滚动条是水平的时候)。
			selectFirstRow:true,           //默认表格加载完成后选择第一行
			//数据加载成功事件
			onLoadSuccess:onLoadSuccess,
			//数据加载失败事件
			onLoadError:onLoadError
	};
	
	
	//获取所有的配置项
	exports.init = function(gridId,gridOptionsJson) {
		var opts={};
		if(gridId &&  typeof gridId!="string"){
			opts=gridId;
			gridId = null;
		}else if(gridOptionsJson){
			opts=gridOptionsJson;
		}
		if(opts.onLoadSuccess){
			_option.onLoadSuccessCall=opts.onLoadSuccess;
			delete _option.onLoadSuccess;
		}
		var b = false;
		if(opts.forceSingleSelect)b = true;
		var def = {checkOnSelect:true,singleSelect:b,selectOnCheck:true};
		
		//合并参数
		$.extend(_option,opts,def);
		_option.onLoadSuccess=onLoadSuccess;
		//如果gridId为空则默认为grid
		_gridId=gridId || 'grid';
		//如果需要自动调整列宽，则绑定单机时间
		if(!Util.array.isEmpty(_option.columnWidth)){
			bindCellEvent(_option);
		}
		//处理动态列
		dynamicPropertyHandler(_option);
		return $('#'+_gridId).datagrid(_option);
	};
	/**
	 * 默认列格式化
	 */
	exports.formatterDefault=function(value,row,index,length){
		if(!value) return '';
		length=length||30;
		if(typeof value =='object')
			return value.chineseName||'';
		else if(typeof value =='string'){
			if(value.length > length){
				return '<span title="'+value+'">'+Util.string.cut(value,length)+'</span>';
			}else{
				return value;
			}
		}else{
			return '';
		}
	};
	
	/**
	 * 字典查询名称方法
	 * 参数: 
	 * type：字典的类型
	 * value：字典的value值
	 */
	 var dicNameBuffer = {};
	 exports.formatterDictionaryName = function(type, value) {
		if(!type || !value) return '';
		if(dicNameBuffer[type+"_"+value]) return dicNameBuffer[type+"_"+value];
	 	var name = '';
		var url = '../dictionary/findDictionaryNameByTypeValue.html';
		var params = {
			type : type,
			value : value
		};
		var success = function(data) {
			name = data.name;
		};
		Util.ajax.syncAjax(url, success, params, {});
		dicNameBuffer[type+"_"+value] = name;
		return name;
	 };

	 /**
	  * 根据用户id查询用户信息用户名和真实姓名
	  * id：用户Id值
	  * return 返回用户对象{username:用户名,realName:真实姓名}
	  */
	 var userNameBuffer = {};
	 exports.formatterUserName = function(value) {
		 if(!value) return '';
		 if(userNameBuffer[value]) return userNameBuffer[value];
		 var user = {};
		 var url = '../user/findUserById.html';
		 var params = {
				 id : value
		 };
		 var success = function(data) {
			 if(data){
				 user.username = data.username;
				 user.realName = data.realName;
			 }
		 };
		 Util.ajax.syncAjax(url, success, params, {});
		 userNameBuffer[value] = user;
		 return user;
	 };

	 /**
	  * 如果lastDataId ！= "",跳转到最后一页
	  */
	 exports.formatterSelectIndexRow = function(grid_m,lastIndex,data) {
	 	var grid = $('#'+grid_m);
	 	if(lastIndex.toString() != "false"){
	 		var pager = grid.datagrid("getPager");
			if(pager.length>0){
			 	var options = pager.pagination("options");
				var total = data.total;
				var max = Math.ceil(total/options.pageSize);
				var curr = options.pageNumber;
				if(curr != max){
					return pager.pagination('select',max);
				}
			}
	 	}
	 };
	 
	 /**
	  * 处理datagrid动态属性
	  * @param opts
	  */
	dynamicPropertyHandler=function(opts){
		if(!opts.dynamicProperty){
			return ;
		}
		var model = opts.dynamicProperty.model;
		if(!model) return ;
		var lastE = opts.columns[0].pop();
		Util.ajax.syncAjax("../dynamicModelProperty/findDynamicPropertyByClass.html",function(data){
			if(data){
				$.each(data,function(i,dmp){
					if(dmp.isListShow.name=="Yes"){
						opts.columns[0].push({field:dmp.propertyCode,halign:'center',align:'left',title:dmp.propertyName});
					}
				});
			}
		},{model:model});
		//添加操作列
		opts.columns[0].push(lastE);
	};
	 
	
	datagridCellTextHidden=function(columnWidth){
		if(columnWidth){
			$.each(columnWidth,function(i,field){
				var cellDiv =$("td[field='"+field+"'] div");  
				cellCssHandler(false,cellDiv);
			});
		}
	};
	
	cellCssHandler=function(deleted,cellDiv){
		if(!deleted){
			cellDiv.css({  
                "width":cellDiv.width(),  
                "white-space":"nowrap",  
                "text-overflow":"ellipsis",  
                "-o-text-overflow":"ellipsis",  
                "overflow":"hidden"  
               });    
		}else{
			cellDiv.css({  
                "width":'',  
                "white-space":'',  
                "text-overflow":'',  
                "-o-text-overflow":'',  
                "overflow":''
            });    
		}
	};
	
	/**
	 * 绑定单元格事件
	 * @param _option
	 */
	bindCellEvent=function(_option){
		//如果是双击则监听双击事件,否则为单机事件
		if(_option.autoDBClick){
			var old = null;
			if(Util.fn.isFn(_option.onDblClickCell)){
				 old = _option.onDblClickCell;
			}
			_option.onDblClickCell = function(rowIndex, field, value){
				autoColumnWidth(_option.columnWidth,field,value);
				//调用用户定义的双击事件
				if(old)
					old(rowIndex, field, value);
			};
		}else{
			var old = null;
			if(Util.fn.isFn(_option.onClickCell)){
				 old = _option.onClickCell;
			}
			_option.onClickCell = function(rowIndex, field, value){
				autoColumnWidth(_option.columnWidth,field,value);
				//调用用户定义的双击事件
				if(old)
					old(rowIndex, field, value);
			};
			
		}
	};
	
	/**
	 * 根据内容自动调整列宽
	 * @param rIndex
	 * @param field
	 * @param value
	 */
	autoColumnWidth = function (columnWidth,field,value){
		//如果存在要调整的当前列
		if(Util.array.indexOf(columnWidth,field)!=-1){
			var cellDiv=$("td[field='"+field+"'] div");  
			cellCssHandler(true,cellDiv);
			var dg = $("#"+_gridId);
			dg.datagrid('autoSizeColumn', field);
			/*var col = dg.datagrid('getColumnOption', field);
			var width = (value.length*14)+20,length=0;
			if(col.width< width){
				$.each(dg.datagrid("getRows"),function(i,row){
					length = length<row[field].length?row[field].length:length;
				});
				col.width = (length*14)+20;
				dg.datagrid();
			}*/
		}
	};
	
});