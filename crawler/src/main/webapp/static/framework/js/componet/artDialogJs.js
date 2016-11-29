define(function(require,exports,module){
	//默认参数
	var _defaults = {
		//id:null,//id 防止重复弹出
		lock:true,//遮罩
		title:'提示',//弹出框标题
		width:'500px',
		height:'350px',
		//opacity:0.6,
		//ok:function(){return true;},
		//okVal:'确定',//确定按钮 文本
		//cancel:function(){return true;},
		//cancelVal:'取消',//取消按钮文本
		close:function(){return true;}//关闭事件，无论是确定、取消还是点x关闭均触发
	};
	
	/**
	 * 设置一个参数，可以不通过后台直接在打开的目标窗口获取值
	 * 注意：key是全局的
	 */
	exports.setParam=function(key,value){
		 $.dialog.data(key,value);
	};
	
	/**
	 * 获取一个参数值
	 * @setParam
	 */
	exports.getParam=function(key){
		return  $.dialog.data(key);
	};
	
	/**
	 * 打开一个对话框
	 * opts:{
	 *    url: '需要打开的url',
          title: 标题,
          ...等等参考 _defaults
	 * }
	 */
	exports.openDialog= function(opts){
		opts = opts || {};
		//初始化配置参数
		opts = $.extend({},_defaults,opts);
		//如果没有传入回调方法名称采用默认的method
		var mName = opts.methodName || 'method';
		 $.dialog.data(mName, function(args){
			 if(Util.fn.isFn(opts.reloadCallBack)){
				 opts.reloadCallBack(args);
			 }else{
				 $("#grid").datagrid("reload");
			 }
		 });
		 var win = top || parent || window;
		 var overflowVal = win.$("body").css("overflow");
		 win.$("body").css("overflow","hidden");
		 var closeFun = opts.close;
		 //关闭是重置滚动条样式
		 opts.close=function(){
			 if(!overflowVal)
				 win.$("body").css("overflow","");
			 else
				 win.$("body").css("overflow",overflowVal);
			 win = null;
			 if(closeFun && Util.fn.isFn(closeFun)){
				 closeFun();
			 }
		 };
		 if(opts.url){
			 if(top && top.$)
				return  top.$.dialog.open(opts.url,opts);
			 else
				 return $.dialog.open(opts.url,opts);
		 }else{
			 //如果是不是打开新的URL 暂时不在最顶层窗口弹出，因为应用原页面的样式会失效
			 	if(top && top.$)
				 top.$.dialog(opts);
			 else
				return $.dialog(opts);
		 }
		//var obj = new dialogObj(_opts);
		//obj.init();
	};
	
	
});