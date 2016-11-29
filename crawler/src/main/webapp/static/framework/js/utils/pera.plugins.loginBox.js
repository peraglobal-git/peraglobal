/*
* pera plugins login
* create by guoshun.hou
* create on 20130910
*/
(function($){
	/*
	 * xupp
	 * $代表当前调用插件函数的对象
	 */
	/* private method */
	function init(jq, options) {
		return jq.each(function() {
			var $this = $(this);
			var opts = $.extend({}, $.fn.loginBox.defaults, options, $.parseOptionsToJOSN($this.data('options')));
			//if(typeof console!='undefined')console.dir(opts);
			//struct login box
			$this.parents('body').addClass('peraplugins-loginbox-page');
			var box = $('<div class="peraplugins-panel peraplugins-loginbox'+(opts.boxType?(' peraplugins-'+opts.boxType):'')+'"/>').addClass($this.attr('class')).css({width:opts.width,height:opts.height}).appendTo(document.body);
			$this.css({width:opts.width,height:opts.height,backgroundColor:opts.bgBoxColor,backgroundImage:(opts.bgBoxImg=='none'?opts.bgBoxImg:'url('+opts.bgBoxImg+')'),backgroundRepeat:opts.bgBoxRepeat,position:'absolute',zIndex:10000}).appendTo(box);
			$this.find('h2').css({backgroundImage:(opts.bgBoxImg=='none'?opts.bgBoxImg:'url('+opts.bgBoxImg+')')});
			$this.children().wrapAll('<div class="peraplugins-panel peraplugins-loginbox-body"/>');
			var shadow = $('<div class="peraplugins-panel peraplugins-loginbox-shadow" />').css({width:box.outerWidth(),height:box.outerHeight()}).appendTo(document.body);
			box[0].base = $this;
			box[0].shadow = shadow;
			var bg = $('<div class="peraplugins-loginbox-bg" />').css({width:box.outerWidth(),height:box.outerHeight(),backgroundColor:opts.bgColor,opacity:opts.bgOpacity/(opts.bgOpacity<=1?1:100),filter:'alpha(opacity='+opts.bgOpacity*(opts.bgOpacity<=1?100:1)+')',backgroundImage:(opts.bgImg=='none'?opts.bgImg:'url('+opts.bgImg+')'),backgroundRepeat:opts.bgRepeat,backgroundPosition:'center center'}).appendTo(document.body);
			if(opts.fullscreen){bg.css({width:'100%',height:'100%',left:0,top:0,position:'static'});}
			setPosition($this, 'center');
			if(opts.fadeIn){
				box.hide().fadeIn(1200);
				shadow.hide().fadeTo(1200,0.3,function(){});
			}
			$(window).resize(function(){setPosition($this, 'center');});
			//component propety
			var inputName = $('#'+opts.inputName);
			var inputPswd = $('#'+opts.inputPswd);
			var linkForget = $('#'+opts.linkForget);
			$this.find('.ipt').each(function(){
				$(this).focus(function(){$(this).addClass('on').select(); $(this).removeClass('warn'); $('.hint_'+this.id).remove();});
				$(this).blur(function(){$(this).removeClass('on'); if($(this).is('.validatebox'))
					//validate($this, {input:this.id,url:opts.validateUserUrl});
					var a="";
				});
			});
			$('.focus:first', $this).focus();
		
			
			$('input[type="reset"]', $this).click(function(){$('.ipt', $this).removeClass('warn'); $('.hint', $this).remove();});
		});
	}
	function box(jq){
		return jq.parents('.peraplugins-loginbox');
	}
	function shadow(jq){
		return box(jq)[0].shadow;
	}
	function setPosition(jq, alignType){
		var w = $(window).width(),h = $(window).height();
		switch(alignType){
			case 'center':
			$('.peraplugins-loginbox,.peraplugins-loginbox-shadow,.peraplugins-loginbox-bg').css({left: (w-jq.width())/2, top: (h-jq.height())/2});
			break;
		}
	}
	function shake(jq){
		var $box = box(jq);
		if($box.is(":animated"))return;
		var speed = 90;
		var shadow = $box[0].shadow;
		var orgPosition = {left:shadow.position().left,top:shadow.position().top};
		shadow.css({left:0,top:0}).appendTo($box);
		$box.animate({left:$box.position().left-40},speed,function(){$box.animate({left:$box.position().left+70},speed,function(){$box.animate({left:$box.position().left-40},speed,function(){$box.animate({left:$box.position().left+13},speed,function(){$box.animate({left:$box.position().left-6},speed,function(){$box.animate({left:$box.position().left+3},speed,function(){shadow.css(orgPosition).insertAfter($box);});});});});});});
	}
	function warn(jq, argData){
		var input = $('#'+argData.input, jq);
		$('.hint_'+argData.input, jq).remove();
		if(argData.hint)$('<span class="hint hint_'+argData.input+'"/>').text(argData.hint).insertAfter(input);
		input.addClass('warn');
//		input.val('');
//		if(!argData.noshake)shake(jq);
	}
	function validate(jq, argData){
		var input = $('#'+argData.input,jq)[0];
		if(input.value){
			if(!argData.url){alert('没有设置"validateUserUrl"值，无法验证登录名！'); return;}
			var iptObj = {};
			iptObj[input.name] = input.value;
			//alert(argData.input);
			if(argData.input=="pswd"){
				iptObj["username"] = $("#userName").val();
			}
			$.get(argData.url,iptObj,function(data){
				data = jQuery.parseJSON(data);
				if(data.error){
					warn(jq,{input:argData.input,hint:data.errorText});
					mysubmit=false;
					}else{mysubmit=true;};
			});
		}
	}
	//interface
	$.fn.loginBox = function(options){
		//parse arg
		var method = arguments[0];
		var methods = $.fn.loginBox.methods;
        if(methods[method]) {
            method = methods[method];
            arguments = Array.prototype.slice.call(arguments, 1);
        } else if( typeof(method) == 'object' || !method ) {
            method = methods.init;
        } else {
            $.error( 'Method ' +  method + ' does not exist on plugins.loginBox' );
            return this;
        }
        return method.apply(this, arguments);
	};
	$.fn.loginBox.methods = {
		init: function(options){
			return init(this, options);
		},
		box: function(){
			return box(this);
		},
		shadow: function(){
			return shadow(this);
		},
		shake: function(){
			return this.each(function(){shake(this);	});
		},
		warn: function(data){
			return this.each(function(){warn(this, data);});	
		},
		validate: function(data){
			return this.each(function(){validate(this, data);});	
		}
	};
	$.fn.loginBox.defaults = {
		width:565,
		height:257,
		bgBoxColor:'#EEE',
		bgBoxImg:'none',
		bgBoxRepeat:'no-repeat',
		fullscreen:true,
		bgColor:'#000',
		bgImg:'none',
		bgRepeat:'no-repeat',
		bgOpacity:1,
		fadeIn:false,
		inputName: 'userName',
		inputPswd: 'pswd',
		inputValidcode: 'validcode',
		linkForget: 'forget',
		validateUserUrl:'',
		forgetPswdUrl:'',
		onShow:undefined,
		onSubmit:undefined,
		onForgetPswd:undefined,
		mysubmit:true
	};
	/* reset password box */
	$.fn.resetPswdBox = function(options){
		var opts = $.extend(options, {boxType:'resetpswd-box'});
		$.fn.loginBox.call(this, opts);
	};
	/* extend method */
	$.extend({
		 parseOptionsToJOSN: function(strData){
			 var obj = {};
			 if(strData){
				 strData = strData.split(',');
				 for(var i=0; i<strData.length; i++){
					 var data = strData[i].split(':');
					 if(/^\d+$/.test(data[1])){obj[data[0]] = parseInt(data[1]);}
					 else if(/^\d+\.\d+$/.test(data[1])){obj[data[0]] = parseFloat(data[1]);}
					 else if(/^['"](.*)['"]$/.test(data[1])){obj[data[0]] = RegExp.$1;}
					 else if(/^true||false$/.test(data[1])){obj[data[0]] = data[1]=='true';}
				 }
			 }
			 return obj;
		 }
	});
})(jQuery);