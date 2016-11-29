/**
 *基于easyui扩展插件，提供以下功能
 * datagrid工具栏权限验证
 * 
 */
(function($){
	
	/**
	 * 权限对象,负责获取当前user中有权限的模块，提供权限检查方法
	 */
	var permission = {
			moduleCodes :[],
			_opts : {
				type : 'json',//返回数据类型
				timeout : 5000//加载超时时间
			},
			requestData:function(){
				Util.ajax.syncAjax(SERVER_CONTEXT+"/main/getPermission.html",function(json){
					if(json.permissions){
						permission.permissionHandler(json.permissions);
					}
				},{},permission._opts);
			},
			modulesCodeHandler:function(modules){
				var thiz = permission;
				$.each(modules,function(i,module){
					if(module.moduleCode){
						thiz.moduleCodes.push(module.moduleCode);
					}
					if(module.childList.length>0){
						thiz.modulesCodeHandler(module.childList);
					}
				});
			},
			permissionHandler:function(permissionArr){
				var thiz = permission;
				$.each(permissionArr,function(i,perm){
					if(perm.permissionCode){
						thiz.moduleCodes.push(perm.permissionCode);
					}
				});
			},
			/**
			 * 检查是否有相应权限
			 * @param code 权限对已的功能模块编码
			 * return false没有该权限，true有该权限
			 */
			checkPermission:function(code){
				if(!permission.moduleCodes.length>0){
					permission.requestData();
				}
				for(var i=0,len=permission.moduleCodes.length;i<len;i+=1){
					if(code==permission.moduleCodes[i]){
						return true;
					}
				};
				return false;
			},
			checkRowPermission:function(){
				if(!permission.moduleCodes.length>0){
					permission.requestData();
				}
				for(var i=0,len=permission.moduleCodes.length;i<len;i+=1){
					$(".datagrid-cell-c1-operate:first a").each(function(index,element) {
						if(!$(this).hasClass(permission.moduleCodes[i])){
							$("a." + permission.moduleCodes[i]).remove();
						}
					});
					
				};
			}
	};
	/**
	 * 扩展datagrid插件支持工具栏权限过滤
	 * 用法：在初始化完datagrid后调用toolbarPermission 如：grid.datagrid("toolbarPermission",{toolbarDel:true});
	 * 注意：1.其中toolbarDel:true表示如果工具栏中没有任何按钮是删除工具栏div
	 *     2.目前验证是基于toolbar中的按钮id来进行的，这意味着需要把id和数据库module中的按钮功能一一对应
	 *     3.如果按钮未指定id会抛出“datagrid toolbar button id  is not definition”错误
	 *     4.
	 * 
	 */
	$.extend($.fn.datagrid.methods, {
		toolbarPermission:function(jq,opts){
			jq.each(function () {
				var target = $.data(this, "datagrid");
				var _toolbar = target.options.toolbar;
				if(_toolbar){
					if(typeof _toolbar=='string'){
						_toolbar = $(_toolbar).find("a");
					}
					//移除datagrid中的工具栏
					$.each(_toolbar,function(i,obj){
						if(typeof obj!="string"){
							if(!obj.id){
								jQuery.error('datagrid toolbar button id  is not definition');
							}
							//检查是否有权限,没有权限则移除相应按钮 //过滤更多功能按钮不被移除
							if(!permission.checkPermission(obj.id) && "showMoreBtn" != obj.id){
								var _dataToolbar = $("div.datagrid-toolbar" , target.options.panel);
								_dataToolbar.find("a#"+obj.id).remove();
								//判断第一个按钮是不是分隔符
								var _btnSeparator = _dataToolbar.find("div:eq(0)");
								if(_btnSeparator && _btnSeparator.hasClass("datagrid-btn-separator")){
									_btnSeparator.parent().remove();
								}
									if(opts.toolbarDel && _dataToolbar.find("a").length==0)
										_dataToolbar.remove();
							}
						}
						
					});
				}
				
			});
		},
		/** 
		 * 扩展datagrid插件
		 * 操作列'权限过滤（rowPermission） 
		 * 强调(操作列field必须为"operate"，class必须为"easyui-linkbutton permissionCode")
		 * “permissionCode” 为引用
		 * 例如：
		 * {field:'operate',halign:'center',align:'center',width:150,title:'操作',
                formatter:function(val,row){
                	return '<a id="update_'+row.id+'" class="easyui-linkbutton updateModule" iconCls="icon-edit" plain="true" href="javascript:void(0)">修改</a>&nbsp; <a id="up_'+row.id+'" class="easyui-linkbutton upModule" iconCls="icon-edit" plain="true" href="javascript:void(0)">上移</a>&nbsp; <a id="down_'+row.id+'" class="easyui-linkbutton downModule" iconCls="icon-edit" plain="true" href="javascript:void(0)">下移</a>'; 
                }
            }
		 * */
		rowPermission : function(jq) {
			$(".datagrid-cell-c1-operate:first a").each(function() {
				var _classN = this.className;
				_classN = _classN.substring(18, _classN.length);
				if (!permission.checkPermission(_classN)) {
					$("a." + _classN).remove();
				}
			});
		//	permission.checkRowPermission();
		},
		/**
		 * 获取选中的行号
		 */
		getRowNum:function(jq){  
	            var opts=$.data(jq[0],"datagrid").options;  
	            var array = new Array();  
	            opts.finder.getTr(jq[0],"","selected",1).each(function(){  
	                array.push($(this).find("td.datagrid-td-rownumber").text());  
	            });  
	            return array.join(",");  
	    }
	});
})(jQuery);

/**
 * 文本框得到或者失去焦点以及默认值设置插件
 */
(function($){
	//插件入口
	$.fn.inputTextEvent=function(options){
		if(options && typeof options == 'string'){
			options = {searchBtn:options};
		}
		var thiz = $(this);
		//插件默认参数
		var defaults = {
			searchBtn:'search_button',	
			events:['keyup','focus','blur'],  //默认支持的事件类型
			callback:function(v){}
		};
		defaults.id=thiz.attr('id');
		defaults.value=thiz.val();
		var opts = $.extend({},defaults,options);
		$.each(opts.events,function(i,obj){
			var type = opts.events[i];
			thiz.on(type,function(e){
				$.fn.inputTextEvent.methods[type](e,opts);
			});
		});
		//绑定按钮事件,处理完值回调对应方法
		$("#"+opts.searchBtn).on("click",function(e){
			$.fn.inputTextEvent.methods['click'](e,opts);
			thiz.trigger("blur");
		});
		//设置当前文本框默认字体颜色
		thiz.css("color","#808080");
			
	};
	$.fn.inputTextEvent.methods={
			//键盘按下
			 keyup:function(e,opts){
				var ev = document.all ? window.event : e;
				if(ev.keyCode==13) {
					$("#"+opts.searchBtn).trigger("click");
				}
			},
			//得到焦点
			focus:function(e,opts){
				var keytext = $("#"+opts.id).val();
				if(keytext==opts.value){
					$("#"+opts.id).val("").css("color","");    		
				}
			},
			//失去焦点
			blur:function(e,opts){
				var keytext = $("#"+opts.id).val();
				if(keytext==""){
					$("#"+opts.id).val(opts.value).css("color","#808080");
				}
			},
			//搜索按钮点击事件
			click:function(e,opts){
				var keytext = $.trim($("#"+opts.id).val());
				if(keytext==opts.value){
					$("#"+opts.id).val("");
				}
				opts.callback.call(keytext);
			}
	};
})(jQuery);

(function($) {
	/**
	 * JQuery的html方法加强
	 * @param $
	 */
	var oldHTML = $.fn.html;
	$.fn.formhtml = function() {
		if (arguments.length)
			return oldHTML.apply(this, arguments);
		$("input,textarea,button", this).each(function() {
			this.setAttribute('value', this.value);
		});
		$(":radio,:checkbox", this).each(function() {
			if (this.checked)
				this.setAttribute('checked', 'checked');
			elsethis.removeAttribute('checked');
		});
		$("option", this).each(function() {
			if (this.selected)
				this.setAttribute('selected', 'selected');
			elsethis.removeAttribute('selected');
		});
		return oldHTML.apply(this);
	};
	/*
	*wresize
	*/
	$.fn.wresize = function(f){
		function handleWResize(e){
			if(f){
				if(f.tid)clearTimeout(f.tid);
				f.tid=setTimeout(function(){
					f.call([e]);
				},100);
			}
		}
		this.each(function(){
			if (this == window){
				$(this).resize(handleWResize);
			}else{
				$(this).resize(f);
			}
		});
		return this;
	};
})(jQuery);
