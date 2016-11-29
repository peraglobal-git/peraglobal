
$(function(){  
    // 设置jQuery Ajax全局的参数  
    $.ajaxSetup({  
        error: function(jqXHR, textStatus, errorThrown){  
        	Util.ajax.ajaxRequestError(jqXHR, textStatus, errorThrown); 
        }  
    });
    //清空查询表单
    $('input[type=button].clearForm').bind('click', function(){
		$('#listForm :input:not([type=submit]):not([type=button])').val('');
	});
});  
     var Util = {
		object : {
			isObject : function(obj){
				return jQuery.isPlainObject(obj);
			},
			newDeferred : function(fn){
				if(!Util.fn.isFn(fn)){
					jQuery.error('fn is not fn');
				}
				else{
					var _deferred = jQuery.Deferred();
					fn(_deferred);
					return _deferred.promise();
				}
			}
		},
		/**
		 * ajax util
		 */
		ajax : {
			/**
			 * 后台获取数据
			 * @param url ajax uri
			 * @param success 成功后的回调函数，有一个参数 json格式
			 * @param params 需要传递到服务器的参数
			 * @param opts 扩展参数
			 */
			loadData : function(url,success,params,opts){
				var _default = {
					cached:false,
					method:'post',
					isError:function(json){//返回异常消息，如果异常消息不存在，则为正常状态
						return json.error;
					},
					type:'json',
					error:function(message){
						showErrorTip(message);
					},
					/**
					 * 必须执行的函数
					 */
					finalCall : function(){
						
					}
				};
				if(!Util.fn.isFn(success)){
					params = success;
					success = null;
				}
				opts = opts || {};
				params = params || {};
				opts = $.extend(_default,opts);
				var data = {};
				data = $.extend(data,params);
				if(!opts.cached){
					data._random = Math.random();
				}
				data.contentType = 'text/plain;charset=UTF-8';
				var _method = 'post';
				if(opts.method == 'get'){
					_method = 'get';
				}
				return Util.object.newDeferred(function(dtd){
					$[_method](url,data,function(json){
						var _t = null;
						if((_t = opts.isError(json)) != undefined){
							if(typeof opts.error == 'function'){
								opts.error(_t);
							}
							dtd.reject(json);
						}else{
							if(typeof success == 'function'){
								success(json);
							}
							dtd.resolve(json);
						}
						opts.finalCall(json);
					},opts.type).error(function(){
						showErrorTip('加载' + url + ' 数据失败');
						opts.finalCall();
						dtd.reject();
					});
				});
			},
			syncAjax:function(url,success,params,opts){
				var _default = {
						cached:false,
						contentType:'text/plain;charset=UTF-8',
						dataType:'json',
						async:false
					};
				opts = opts || {};
				params = params || {};
				opts = $.extend(_default,opts);
				$.ajax({
					url:url,
					contentType:opts.contentType,
					dataType:opts.dataType,
					async:opts.async,
					data:params,
					success:function(json){
						success(json);
					}
				});
			},
			/**
			 * 将form表单元素序列化成json，如果一个key有多个值（如 checkbox），则key:[value1,value2]形式
			 * @param form
			 * @returns json
			 */
			serializable : function($form){
				var data = {};
				if($form.length > 0){
					var _serial = $form.serializeArray();
					var _t = null;
					var _old = null;
					for(var i = 0;i < _serial.length;i ++){
						_t = _serial[i];
						_old = data[_t.name];
						if(!_old){
							data[_t.name] = _t.value;
						}else{
							if(Util.array.isArray(_old)){
								data[_t.name].push(_t.value);
							}else{
								_old = data[_t.name];
								data[_t.name] = [];
								data[_t.name].push(_old);
								data[_t.name].push(_t.value);
							}
						}
					}
				}
				return data;
			},
			ajaxRequestError:function(jqXHR, textStatus, errorThrown){
				var errorJson={}; 
				if(jqXHR.responseText && jqXHR.responseText!=""){
					errorJson = $.parseJSON(jqXHR.responseText);
				}
	            switch (jqXHR.status){  
	                case(500):  
	                	showErrorTip(errorJson.errorMsg||'服务器系统内部错误');  
	                    break;  
	                case(401):  
	                	showErrorTip({content:errorJson.errorMsg||'会话已过时,请重新登录',ok:function(){
	                		localhost.href=SERVER_CONTEXT;
	                	}});  
	                    break;  
	                case(403):  
	                	showErrorTip(errorJson.errorMsg||"无权限执行此操作");  
	                    break;  
	                default:  
	                	showErrorTip(errorJson.errorMsg||"未知错误");  
	            }  
			}
		},
		/**
		 * 函数工具类
		 */
		fn : {
			/**
			 * 是否函数
			 * @param fn
			 * @returns {Boolean}
			 */
			isFn : function(fn){
				return typeof fn == 'function';
			},
			/**
			 * 解析函数，将字符串解析成函数
			 * <p>
			 * 	要解析的函数必须可以全局访问
			 * </p>
			 * @param fn
			 * @returns
			 */
			parse : function(fn){
				if(Util.fn.isFn(fn)){
					return fn;
				}else{
					try{
						fn = eval(fn);
					}catch(e){
						fn = null;
					}
					if(Util.fn.isFn(fn)){
						return fn;
					}
					return null;
				}
			}
		},
		/**
		 * window 相关工具类 
		 */
		window:{
			/**
			 * 获取查询参数
			 * @returns  参数列表
			 */
			getQueryParams : function () {
			    var qs = document.location.search.split("+").join(" ");
			    var params = {}, tokens,re = /[?&]?([^=]+)=([^&]*)/g;
			    while (tokens = re.exec(qs)) {
			        params[decodeURIComponent(tokens[1])] = decodeURIComponent(tokens[2]);
			    }
			    return params;
			},
			/**
			 * 跳转到新的url
			 * @param url
			 * @param opts {isNewWindow:是否新窗口打开,method:提交方式post、get}
			 */
			goTo : function(url,opts){
				/**
				 * 默认参数
				 */
				var _defaults = {
					method:'GET',
					isNewWindow:true
				};
				opts = opts || {};
				_defaults = $.extend(_defaults,opts); 
				var _form = $('<form method="' + _defaults.method + '" action="' + url + '" ' + (_defaults.isNewWindow? 'target="_blank"': '') + '></form>');
				$('body').append(_form);
				_form.submit();
				_form.remove();
			},
			/**
			 * 重新加载
			 */
			reload : function(){
				window.location.href = window.location.href;
			},
			/**
			 * 获取当前host地址
			 */
			getHost : function(){
				try{
					return window.location.protocol + "://" + window.location.host + SERVER_CONTEXT;
				}catch(e){
					return window.location.protocol + "://" + window.location.host;
				}
			}
		},
		/**
		* 日期工具类
		*/
		date : {
			/**
			 * 是否日期
			 * @param value
			 */
			isDate : function(value){
				return value && value.constructor == Date;
			},
			/**
			 * 获取中文月份
			 * @param date
			 * @returns
			 */
			getChineseMonth : function(date){
				//是否数字
				if(Util.number.isNumber(date)){
					date = new Date(date);
				}
				//是否日期
				else if(!Util.date.isDate(date)){
					return '';
				}
				return Util.number.toChinese(date.getMonth() + 1) + '月';
			},
			/**
			 * 相距天数
			 * @param date 可以是毫秒数或者Date对象
			 * @param before 可以是毫秒数或者Date对象
			 * @returns {Number} 天数
			 */
			distanceDay : function(date,before){
				//将毫秒转换成date对象
				if(Util.number.isNumber(date)){
					date = new Date(date);
				}
				if(Util.number.isNumber(before)){
					before = new Date(before);
				}
				/**
				 * 检查参数类型
				 */
				if(!Util.date.isDate(date) || !Util.date.isDate(before)){
					return 0;
				}
				date.setHours(0);
				before.setHours(0);
				var _unit = 24 * 60 * 60 * 1000;
				return parseInt((before.getTime() - date.getTime()) / _unit);
				
			},
			/**
			 * 获取星期
			 * @param date
			 * @returns {String}
			 */
			getChineseWeekday : function(date){
				//是否数字
				if(Util.number.isNumber(date)){
					date = new Date(date);
				}
				//是否日期
				else if(!Util.date.isDate(date)){
					return '';
				}
				return '星期' + date.getDay() == 0 ? '日' : Util.number.toChinese(date.getDay());
			},
			/**
			 * 初始化一个date
			 */
			newDate : function(time){
				if(time == undefined){
					return new Date();
				}
				if(Util.date.isDate(time)){
					return time;
				}
				else if(Util.number.isNumber(time)){
					return new Date(time);
				}
				return null;
			},
			/**
			 * 单位毫秒数
			 */
			unit : {
				second : {
					value : function(){
						return 1000;
					},
					name : function(){
						return '秒';
					}
				},
				minus : {
					value : function(){
						return Util.date.unit.second.value() * 60;
					},
					name : function (){
						return '分钟';
					}
				},
				hour : {
					value : function(){
						return Util.date.unit.minus.value() * 60;
					},
					name : function(){
						return '小时';
					}
				},
				day : {
					value : function(){
						return Util.date.unit.hour.value() * 24;
					},
					name : function(){
						return '天';
					}
				}
			},
			/**
			 * 日期格式化
			 */
			format : {
				/**
				 * 简单模式
				 * <p>
				 * 		1分钟以内，为“刚刚”
				 * 		1小时以内，为“xx分钟前”,
				 *      1天以内，为“xx小时前”,
				 *      调用local
				 * </p>
				 * @param date
				 * @param now
				 */
				simple : function(date,now){
					date = Util.date.newDate(date);
					now = Util.date.newDate(now);
					if(date == null || now == null){
						return '非日期类型';
					}
					var name = '前';
					var time = now.getTime() - date.getTime();
					if(time < Util.date.unit.minus.value()){
						return '刚刚';
					}
					else if(time < Util.date.unit.hour.value()){
						return parseInt(time / Util.date.unit.minus.value()) + Util.date.unit.minus.name() + name;
					}
					else if(time < Util.date.unit.day.value()){
						return parseInt(time / Util.date.unit.hour.value()) + Util.date.unit.hour.name() + name;
					}
					else{
						return Util.date.format.local(date);
					}
				},
				/**
				 * 本地格式
				 * @param date
				 * @returns {String}
				 */
				local : function(date){
					if(Util.number.isNumber(date)){
						date = new Date(date);
					}
					if(!Util.date.isDate(date)){
						return '非日期类型';
					}
					var _d = date;
					return (_d.getMonth() + 1) + '月' + _d.getDate() + '日 ' + _d.getHours() + ':' + _d.getMinutes();
				},
				/**
				 * 转换成yyyy-MM-dd HH:mm:ss
				 * @param date
				 */
				toSecond : function(date){
					if(Util.number.isNumber(date)){
						date = new Date(date);
					}
					if(!Util.date.isDate(date)){
						return '非日期类型';
					}
					function _toXX(value){
						if(value < 10){
							return '0' + value;
						}
						return value;
					}
					var _d = date;
					return _d.getFullYear() + '-' + _toXX(_d.getMonth() + 1) + '-' + _toXX(_d.getDate()) + ' ' + _toXX(_d.getHours()) + ':' + _toXX(_d.getMinutes()) + ':' + _toXX(_d.getSeconds());
				},
				/**
				 * 转换成 yyyy-MM-dd HH:mm
				 * @param date
				 * @returns {String}
				 */
				toMinute : function(date){
					if(Util.number.isNumber(date)){
						date = new Date(date);
					}
					if(!Util.date.isDate(date)){
						return '非日期类型';
					}
					function _toXX(value){
						if(value < 10){
							return '0' + value;
						}
						return value;
					}
					var _d = date;
					return _d.getFullYear() + '-' + _toXX(_d.getMonth() + 1) + '-' + _toXX(_d.getDate()) + ' ' + _toXX(_d.getHours()) + ':' + _toXX(_d.getMinutes());
				}
			}
		},
		array : {
			/**
			 * 是否数组对象
			 * @param array
			 * @returns {Boolean}
			 */
			isArray : function(array){
				return array && array.constructor == Array;
			},
			/**
			 * 是否空的数据，如果不是数组也返回true
			 * @param array
			 * @returns {Boolean}
			 */
			isEmpty : function(array){
				return !Util.array.isArray(array) || array.length == 0;
			},
			/**
			 * 在数组中查找一个对象，如果找到返回下标(下标从0开始)，否则返回-1
			 * 注意：目前是用==比较，对象比较是内存地址，和java一样
			 */
			indexOf : function(array,obj){
				for(var len=array.length,i=0;i<len;i+=1){
					if(obj==array[i]){
						return i;
					}
				};
				return -1;
			}
		},
		string : {
			/**
			 * 是否字符串，typeof str 实现 
			 * @param str
			 * @returns {Boolean}
			 */
			isString : function(str){
				return str && typeof str == 'string';
			},
			/**
			 * 是否空字符串，如果不是字符串或者字符串长度为空则返回true
			 * @param str
			 * @returns {Boolean}
			 */
			isEmpty : function(str){
				return !Util.string.isString(str) || str.length == 0;
			},
			/**
			 * 格式化html特殊字符
			 * @param str
			 * @returns
			 */
			formatHtml : function(str){
				//是否为空
				if(Util.string.isTrimEmpty(str)){
					return '';
				}
				//替换
				str = Util.string.replaceAll(str,'&','&amp;');
				str = Util.string.replaceAll(str,'<','&lt;');
				str = Util.string.replaceAll(str,'>','&gt;');
				return str;
			},
			/**
			 * 截取字符串
			 * @param str
			 * @param maxLength
			 * @param replace
			 * @returns
			 */
			cut : function(str,maxLength,replace){
				if(Util.string.isEmpty(str)){
					return str;
				}
				if(!replace){
					replace = '...';
				}
				if(str.length > maxLength){
					str = str.substring(0, maxLength) + replace;
				}
				return str;
			},
			/**
			 * 是否为空字符串，如果全为空字符也返回true
			 * @param str
			 * @returns {Boolean}
			 */
			isTrimEmpty : function(str){
				return Util.string.isEmpty(Util.string.trim(str));
			},
			/**
			 * 截去前后空格
			 * @param str
			 */
			trim : function(str){
				if(!Util.string.isEmpty(str)){
					str = str.replace(/^\s+/,'');
					str = str.replace(/\s+$/,'');
				}
				return str;
			},
			/**
			 * 去除 html 特殊字符
			 * @param str
			 */
			trimHtml : function(str){
				return Util.string.replaceAll(str, '<[^>]+>', '');
			},
			/**
			 * 全局替换
			 * @param source 替换源
			 * @param regex 匹配表达式
			 * @param replacement 替换表达式
			 * @param ignoreCase 是否忽略大小写
			 * @return 替换结果
			 */
			replaceAll : function(source,regex,replacement,ignoreCase){
				if(!Util.string.isEmpty(source)){
					var _regex;
					if(ignoreCase){
						_regex = new RegExp(regex,'ig');
					}else{
						_regex = new RegExp(regex,'g');
					}
					source = source.replace(_regex,replacement);
				}
				return source;
			}
		},
		/**
		 * 数字工具类
		 */
		number : {
			/**
			 * 是否数字
			 * @param value
			 * @returns {Boolean}
			 */
			isNumber : function(value){
				return typeof value  == 'number' || /^\d+$/.test(value);
			},
			/**
			 * 数字转中文
			 * @number {Integer} 形如123的数字
			 * @return {String} 返回转换成的形如 一百二十三 的字符串
			 */
			toChinese : function(number) {
				/*
				 * 单位
				 */
				var units = '个十百千万@#%亿^&~';
				/*
				 * 字符
				 */
				var chars = '零一二三四五六七八九';
				var a = (number + '').split(''), s = [];
				if (a.length > 12) {
					throw new Error('too big');
				} else {
					for ( var i = 0, j = a.length - 1; i <= j; i++) {
						if (j == 1 || j == 5 || j == 9) {// 两位数 处理特殊的 1*
							if (i == 0) {
								if (a[i] != '1')
									s.push(chars.charAt(a[i]));
							} else {
								s.push(chars.charAt(a[i]));
							}
						} else {
							s.push(chars.charAt(a[i]));
						}
						if (i != j) {
							s.push(units.charAt(j - i));
						}
					}
				}
				// return s;
				return s.join('').replace(/零([十百千万亿@#%^&~])/g, function(m, d, b) {// 优先处理 零百 零千 等
					b = units.indexOf(d);
					if (b != -1) {
						if (d == '亿')
							return d;
						if (d == '万')
							return d;
						if (a[j - b] == '0')
							return '零';
					}
					return '';
				}).replace(/零+/g, '零').replace(/零([万亿])/g, function(m, b) {// 零百 零千处理后 可能出现 零零相连的 再处理结尾为零的
					return b;
				}).replace(/亿[万千百]/g, '亿').replace(/[零]$/, '').replace(/[@#%^&~]/g, function(m) {
					return {
						'@' : '十',
						'#' : '百',
						'%' : '千',
						'^' : '十',
						'&' : '百',
						'~' : '千'
					}[m];
				}).replace(/([亿万])([一-九])/g, function(m, d, b, c) {
					c = units.indexOf(d);
					if (c != -1) {
						if (a[j - c] == '0')
							return d + '零' + b;
					}
					return m;
				});
			}
		},
		
		
		/**
		 * 表单上传
		 * @param 
		 */
			
		formUpload :{
			opts:{
				fileNum:1
			},
			initEvent:function(formId,param,options){
				var id = formId||"#form";
				//注册file事件
				$(id+" :file").change(function(){
					$(id+" :file").attr("disabled",true);
					var fileValue = $(this).attr("disabled",false).val();
					if(fileValue){
						Util.formUpload.uploadHandler(id,param,options,this);
					}
				});
				
			},
			uploadHandler:function(formId,param,options,target) {
				var fileId = $(target).attr("id");
				fileId = fileId || "file"+(Util.formUpload.opts.fileNum++);
				var thiz = $(formId);
				//如果是form才继续
				if(!thiz[0].tagName=="form"){
					showErrorTip("请在form对象上调用此方法");
					return;
				}
				//如果设置了上传前处理，则调用,如果调用完后返回false则放弃上传处理
				if(options.beforeUpload && Util.fn.isFn(options.beforeUpload)){
					var rs = options.beforeUpload();
					if(!rs && rs==false) return ;
				}
				var defaults={
					url:SERVER_CONTEXT+'/attachment/upload.html',
					success:function(data){
						
					},
					error:function(data){
						showErrorTip(data.message);
					}
				};
				//默认参数
				var  defaultParam = {type:'file'};
				var opts = $.extend({},defaults,options);
				var data = $.extend(defaultParam,param);
				opts.enctype = thiz.attr("enctype");
				thiz.attr("enctype","multipart/form-data").attr("encoding","multipart/form-data");
				//通过jquery.form进行异步上传
				 thiz.ajaxSubmit({
			            url: opts.url,
			            data:data,
			            iframe: true,
			            success: function(data) {
			            	$(formId+" :file").attr("disabled",false);
			            	data.fileId = fileId;
							if(data.error!=0){
								opts.error(data);
							}else{
								opts.success(data);
							}
							//还原form对象属性
							thiz.attr("enctype",opts.enctype);
			            },
			            error: function(arg1, arg2, ex) {
			            	showErrorTip("上传文件错误！");
			            },
			    dataType: 'json'});
			}
		},
		
		/*
		 * 获取浏览类型
		 */
		browser:{
			getExplorerType:function () {
				var explorer = window.navigator.userAgent;
				// ie
				if (explorer.indexOf("MSIE") >= 0) {
					return "IE";
				}
				// firefox
				else if (explorer.indexOf("Firefox") >= 0) {
					return "Firefox";
				}
				// Chrome
				else if (explorer.indexOf("Chrome") >= 0) {
					return "Chrome";
				}
				// Opera
				else if (explorer.indexOf("Opera") >= 0) {
					return "Opera";
				}
				// Safari
				else if (explorer.indexOf("Safari") >= 0) {
					return "Safari";
				}
			},
			isIE:function(){
				return Util.browser.getExplorerType()=="IE";
			},
			isFirefox:function(){
				return Util.browser.getExplorerType()=="Firefox";
			},
			isChrome:function(){
				return Util.browser.getExplorerType()=="Chrome";
			}
		}
     };



/**
 * 调用直接显示log 列表
 * @param modelId 模型ID
 * @param modelName 模型名称
 */
function showLog(modelId, modelName,elementName) {
	if(elementName){
		var _elementName = $('input[type=checkbox][name=' + elementName + ']:checked');
		var size = _elementName.length;
		if(size!=1){
			showWarningTip({content:"请选择一项查看日志！"});
			return false;
		}else{
			modelId = _elementName.val();
		}
	}
	
	var url = "../operationLog/getLogList.do?conditions['parentModelId_int_eq'].value="
			+ modelId
			+ "&conditions['parentModelName_string_eq'].value="
			+ modelName;
	art.dialog.open(url, {
		width : 1200,
		height : 600,
		title : "查看日志"
	}).lock();
}

/**
 * 级联菜单初始函数
 * 
 * @param DestID
 *            初始的select 元素ID
 * @param showValue
 *            要显示的值
 * @param checkValue
 *            选中的值 用于回显
 */
function cascadeSelectInit(DestID, showValue, checkValue) {
	var value = showValue;
	if (value == null || value == "" || value == 0)
		value = 1;
	$.post("../area/getAreaList.do", {
		parentId : value,
		random : Math.random()
	}, function(data) {
		$("#" + DestID).empty();
		$("#" + DestID).append("<option value=''>请选择</option>");
		$.each(data, function(i, item) {
			if (item.id == checkValue)
				$("#" + DestID).append(
						"<option selected=selected value='" + item.id + "'>"
								+ item.areaName + "</option>");
			else
				$("#" + DestID).append(
						"<option  value='" + item.id + "'>" + item.areaName
								+ "</option>");
		});
		$("#" + DestID).change();
	}, "json");
}
/**
 * 级联显示的select 调用函数
 * 
 * @param SourceId
 *            触发发事件的select 元素 ID
 * @param DestID
 *            响应事件展示数据的select 元素ID
 * @param checkValue
 *            选中的值 用于回显
 */
function cascadeSelect(SourceId, DestID, checkValue) {
	$("#" + DestID).empty();
	$("#" + DestID).append("<option value=''>请选择</option>");
	$("#" + SourceId).bind(
			"change",
			function() {
				var value = this.value;
				if (value == null || value == "" || value == 0){
					$("#" + DestID).empty();
					$("#" + DestID).append("<option value=''>请选择</option>");
					$("#" + DestID).change();
					return false;
				}
				$.post("../area/getAreaList.do", {
					parentId : value,
					random : Math.random()
				}, function(data) {
					$("#" + DestID).empty();
					$("#" + DestID).append("<option value=''>请选择</option>");
					$.each(data, function(i, item) {
						if (item.id == checkValue)
							$("#" + DestID).append("<option selected=selected value='"+ item.id + "'>" + item.areaName+ "</option>");
						else
							$("#" + DestID).append("<option  value='" + item.id + "'>"+ item.areaName + "</option>");
					});
					$("#" + DestID).change();
				}, "json");
			});
}

// 用于获取标注DHTML对象
function _(id) {
	return document.getElementById(id);
}

// 关闭当前页面(不提示您查看的网页正在试图关闭窗口,是否关闭窗口)
function closeWin() {
	window.opener = null;
	window.open('', '_self');
	window.close();
}

/**
 * 导出xls列表
 * @param url 请求的url地址
 * @param formAction 原始查询表单的url地址
 * @param formId form表单的Id ,默认为 listForm,可不传
 */
function exportXls(url,formAction, formId) {
	var fid = "listForm";
	if(formId != undefined && formId != null && formId != "")fid = formId;
	url += "?x=" + Math.random();
	$("#"+fid+"").attr("action", url);
	$("#"+fid+"").attr("method","post");
	$("#"+fid+"").submit();
	$("#"+fid+"").attr("action",formAction);
}



/**
 * 操作提示基本方法
 * 
 * 所有操作提示都是基于此方法进行封装
 * 
 **/
function showTip(opts){
	//默认参数
	var _defaults = {
		id:null,//id 防止重复弹出
		content:'操作成功！',//提示消息,可以使用html 字符
		icon:'succeed',//图标
		lock:true,//遮罩
		title:'提示',//弹出框标题
		time:3,//显示时间，如果0表示不自动关闭
		ok:function(){return true;},
		okVal:'确定',//确定按钮 文本
		cancel:function(){return true;},
		cancelVal:'取消',//取消按钮文本
		mask:false,//是否遮罩，该遮罩操作在点击确定时遮罩
		close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
	};
	opts = opts || {};
	//初始化配置参数
	opts = $.extend({},_defaults,opts);
	//逐个覆盖所有参数
	for(var i = 1;i < arguments.length;i ++){
		opts = $.extend({},opts,arguments[i]);
	}
	var dialog = {};
	/*var masks = false;
	var container = null;
	//监听ok,cancel,close事件
	var tok = opts.ok;
	var tcan = opts.cancel;
	var tclose = opts.close;
	//重写事件
	opts.ok = function(e,target){
		if(top.$.dialog){
			top.$.dialog.close();
		}
		var rs;
		if($.isFunction(tok)){
			 rs = tok.call(dialog,dialog,target);
		}
		//检查返回值，如果没有返回值，则默认为true
		if(rs = undefined){
			rs = true;
		}
		return rs;
	};
	opts.cancel = function(e,target){
		tcan.call(dialog,dialog,target);
	};
	opts.close = function(e,target){
		tclose.call(dialog,dialog,target);
	};*/
	if(top && top.$)
		dialog.dialog = top.$.dialog(opts);
	else
		dialog.dialog = $.dialog(opts);

	/**
	 * 获取容器
	 * @returns
	 */
	function getContainer(){
		if(container == null){
			container = $('body');
		}
		return container;
	};
	return dialog;
};

/**
 * 操作失败提示
 * 
 * 当操作出现异常时，使用此提示方式
 * 
 * content:'操作失败',
 * title:'操作失败提示',
 * time:3,
 * ok:function(){return true;},
 * okVal:'确定',//确定按钮 文本
 * close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
 * lock:true
 * mask:false,遮罩，是否需要遮罩，一般对于有后台ajax操作时，打开此开关，该操作会在确定时遮罩,在ajax处理完成后，请使用obj.unmask()去除遮罩
 */
function showErrorTip(opts){
	//可配置参数，默认值
	var _defaults = {
		id:null,//防止重复弹出
		content:'操作失败',
		title:'操作失败提示',
		time:3,
		ok:function(){return true;},
		okVal:'确定',//确定按钮 文本
		lock:true,
		mask:false,
		close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
	};
	//不可配置参数
	var settings = {
		icon:'error',
		cancel:null
	};
	//如果是string型，则默认为只设置content内容
	if(typeof opts == 'string'){
		opts = {
			content:opts
		};
	}
	opts = opts || {};
	//初始化参数
	opts = $.extend({},_defaults,opts);
	return showTip(opts,settings);
};

/**
 * 操作成功提示
 * 
 * 操作成功时，使用此提示方式
 * 
 * content:'操作成功',
 * title:'操作成功提示',
 * time:3,
 * ok:function(){return true;},
 * okVal:'确定',//确定按钮 文本
 * close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
 * lock:true
 */
function showSuccessTip(opts){
	//可配置参数，默认值
	var _defaults = {
		id:null,//防止重复弹出
		content:'操作成功',
		title:'操作成功提示',
		time:3,
		ok:function(){return true;},
		okVal:'确定',//确定按钮 文本
		lock:true,
		mask:false,
		close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
	};
	//不可配置参数
	var settings = {
		icon:'succeed',
		cancel:null
	};
	//如果是string型，则默认为只设置content内容
	if(typeof opts == 'string'){
		opts = {
			content:opts
		};
	}
	opts = opts || {};
	//初始化参数
	opts = $.extend({},_defaults,opts);
	return showTip(opts,settings);
};

/**
 * 操作警告
 * 
 * 当操作可能出现未知状况时，使用此提示方式
 * content:'操作警告',
 * title:'警告提示',
 * ok:null,
 * okVal:null,//确定按钮 文本
 * cancel:function(){return true;},
 * cancelVal:'确定',
 * close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
 * lock:true
 */
function showWarningTip(opts){
	//可配置参数，默认值
	var _defaults = {
		id:null,//防止重复弹出
		content:'操作警告',
		title:'警告提示',
		ok:null,
		okVal:null,//确定按钮 文本
		cancel:function(){return true;},
		cancelVal:'确定',
		lock:true,
		mask:false,
		close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
	};
	//不可配置参数
	var settings = {
		icon:'warning',
		time:0
	};
	//如果是string型，则默认为只设置content内容
	if(typeof opts == 'string'){
		opts = {
			content:opts
		};
	}
	opts = opts || {};
	//初始化参数
	opts = $.extend({},_defaults,opts);
	return showTip(opts,settings);
};

/**
 * 提问操作
 * 
 * 当操作需要用户进行选择时，使用此提示方式
 * content:'是否确定此操作？',
 * title:'警告提示',
 * ok:function(){return true;},
 * okVal:'操作提示',//确定按钮 文本
 * cancel:function(){return true;},
 * cancelVal:'取消',
 * close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
 * lock:true
 */
function showQuestionTip(opts){
	//可配置参数，默认值
	var _defaults = {
		id:null,//防止重复弹出
		content:'是否确定此操作？',
		title:'操作提示',
		ok:function(){return true;},
		okVal:'确定',//确定按钮 文本
		cancel:function(){return true;},
		cancelVal:'取消',
		lock:true,
		mask:false,
		close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
	};
	//不可配置参数
	var settings = {
		icon:'question',
		time:0
	};
	//如果是string型，则默认为只设置content内容
	if(typeof opts == 'string'){
		opts = {
			content:opts
		};
	}
	opts = opts || {};
	//初始化参数
	opts = $.extend({},_defaults,opts);
	return showTip(opts,settings);
};

//--- 以下对一个些通用操作方法提示进行处理
/**
 * 对于某些操作必须选择一个数据，而用户未选择的
 */
function noSelectedTip(){
	showWarningTip({
		content:'请选择一项再进行操作！'
	});
};

/**
 * 功能 图片自动按宽高比例缩小的(一般在 img 标签 onload事件中调用)
 * @param img 图片对象
 * @param width 最大宽度
 * @param height 最大高度
 */
function setImgSize(img,width,height){ 
	img.style.display = 'block';
	var MaxWidth=width;//设置图片宽度界限 
	var MaxHeight=height;//设置图片高度界限 
	var HeightWidth=img.offsetHeight/img.offsetWidth;//设置高宽比 
	var WidthHeight=img.offsetWidth/img.offsetHeight;//设置宽高比 
	if(img.offsetWidth>MaxWidth){ 
		img.width=MaxWidth; 
		img.height=MaxWidth*HeightWidth; 
	} 
	if(img.offsetHeight>MaxHeight){ 
		img.height=MaxHeight; 
		img.width=MaxHeight*WidthHeight; 
	} 
}



/**
 * 跳转到一个新的页面
 */
function gotoUrl(url){
	showSuccessTip({
		close:function(){
			window.location.href = url;
			return true;
		}
	});
}



/**
 * 将form表单中的元素序列化成json 数据
 * @param form jquery form 或 selector
 * @return 序列化结果,json object
 */
function serializeFormToJson(form){
	//如果是selector，将此selector转换成jquery form
	if(typeof form == 'string'){
		form = $(form);
	}
	var serializeArrays = form.serializeArray();
	var rs = {};
	var t = null;
	for(var i = 0;i < serializeArrays.length;i ++){
		t = serializeArrays[i];
		rs[t.name] = t.value;
	}
	return rs;
};
/**
 * 根据form 表单值反序列化，即根据值设置表单元素
 * @param form jquery form 或 selector
 * @param json 值
 */
function deserializeFormByJson(form,json){
	//如果是selector，将此selector转换成jquery form
	if(typeof form == 'string'){
		form = $(form);
	}
	var t = null;
	var elt = null;
	for(var p in json){
		t = json[p];
		elt = form.find('[name=' + p + ']');
		//如果元素存在
		if(elt.length > 0){
			elt.each(function(){
				if("span" == $(this)[0].tagName.toLowerCase()){
					$(this).text(t);
				}else{
					$(this).val(t);
				}
			});
		}
	}
};

/**
 * 提交删除
 */
var submitDel = function(url,opts) {
	var idArr = [];
	var msg = opts.msg||"确定要删除吗？";
	if(Util.array.isArray(opts.rows)) {
		$.each(opts.rows,function(i,obj){
			idArr.push(obj.id);
		});
	}else {
		idArr.push(opts.rows.id);
	}
	if(Util.array.isEmpty(idArr)){
		showWarningTip("请选择要删除的数据");		
		return ;
	}
	showQuestionTip({content:msg,ok:function(){
	    var ids = idArr.join(',');
  	    Util.ajax.loadData(url, 
    	    function(data){
  	    		var fail = data.code||data.success;
 				if(fail=="fail" || fail=="false" && data.msg){
					showErrorTip(data.msg);
					return;
				}else{
					if(opts.callback && typeof opts.callback == 'function'){
						opts.callback(data);
					}
				}
    	     },{id:ids});
	   		 }
	});
};

/**
 * 设置验证框架默认参数
 */
if(jQuery.validator){
	jQuery.validator.setDefaults({
		errorPlacement: function(error, element)  {  
			var elem = $(element)/*,  
             corners = ['left center', 'right center'],  
             flipIt = elem.parents('span.right').length > 0*/;  
			
			// Check we have a valid error message  
			if(!error.is(':empty')) {  
				// Apply the tooltip only if it isn't valid  
				elem.first().qtip({  
					overwrite: false,  
					content: error,  
					position: {  
						my: 'left center',  
						at: 'right center',  
						viewport: $(window)  
					}, 
					show: {  
						event: false,  
						ready: true  
					},  
					hide: false,  
					style: {  
						classes: 'qtip-white' // 设置样式
					}  
				})  
				.qtip('option', 'content.text', error);  
			}  
			
			// If the error is empty, remove the qTip  
			else { elem.qtip('destroy'); }  
		},
		success: $.noop
	});
}
/**
 * 设置qtip默认参数

$.fn.qtip.defaults = {  
		   content: {  
		      text: true,   
		      attr: 'title',   
		      ajax: false,  
		      title: {  
		         text: false,   
		         button: false  
		      }  
		   },  
		   position: {  
		      my: 'top left',   
		      at: 'bottom right',  
		   },  
		   show: {  
		      event: 'mouseenter',  
		      solo: false,  
		      ready: false,  
		      modal: false  
		   },  
		   hide: {  
		      event: 'mouseleave'  
		   },  
		   style: 'ui-tooltip-default'  
		};  
*/