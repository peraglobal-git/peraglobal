/**
 * user 用户JS
 */
define(function(require,exports,module){
	
	function AutoResize(maxWidth,maxHeight,objImg){
		var img = new Image();
		img.src = objImg.src;
		
		objImg.height = h;
		objImg.width = w;
	}

	
	function popupDiv(div_obj) {  
		var windowWidth = document.body.clientWidth;       
		var windowHeight = document.body.clientHeight;  
		var popupHeight = div_obj.height();       
		var popupWidth = div_obj.width(); 
		var maxWidth = document.body.clientWidth*0.9; 
		var maxHeight = document.body.clientHeight*0.9; 
		var hRatio;
		var wRatio;
		var Ratio = 1;
		var w = popupWidth;
		var h = popupHeight;
		wRatio = maxWidth / w;
		hRatio = maxHeight / h;
		if (maxHeight ==0 && maxWidth==0){
			Ratio = 1;
		}else if (maxWidth==0){
			Ratio = hRatio;
		}else if (maxHeight==0){
			Ratio = wRatio;
		}else {
			Ratio = (wRatio<=hRatio?wRatio:hRatio);
		}
		popupWidth = w * Ratio;
		popupHeight = h * Ratio;
		var startTop = div_obj.offset().top;
		var startLeft = div_obj.offset().left;
		var options = div_obj.find('.panel-body').panel('options');
		var p = $('<div />').appendTo('body');
		p.panel({
				id:"mask"+options.id,
				title:options.title,
				content:options.content,
				height:popupHeight,
				width:popupWidth,
				tools:[{
					iconCls:'panel-tool-min',
					toolPosition:'right',
					handler:function(){
						var div = $('#mask'+options.id).parent();
						hideDiv(div,startTop,startLeft);
					}
				}]
		});   
		var div = $('#mask'+options.id).parent();
		$("<div id='mask'></div>").addClass("mask")   
								  .width(windowWidth + document.body.scrollWidth)   
								  .height(windowHeight + document.body.scrollHeight)   
								  .click(function() {
										hideDiv(div,startTop,startLeft);
								  })   
								  .appendTo("body")   
								  .fadeIn(200);   
		div.addClass("pop-box").css({"top":startTop,"left":startLeft,"height":popupHeight,"width":popupWidth})
			   .animate({left: windowWidth/2-popupWidth/2,    
						 top: windowHeight/2-popupHeight/2, opacity: "show" }, "slow");   
		
                    
	}  
	
	function hideDiv(div,startTop,startLeft){
		$("#mask").remove();   
		div.animate({left: startLeft, top: startTop, opacity: "hide" }, "slow",function(){
				div.find('.panel-body').panel('destroy');
		});
	}

	function add(columns){
		for(var i=0; i<columns.length; i++){
			var panels = columns[i];
			for(var j=0;j<panels.length;j++){
				var panel = panels[j];
				var content = '';
				if(panel.content){
					content = panel.content;
				}else if(panel.url){
					//本地url
					if(panel.url.indexOf("http:")==-1){
						panel.url=SERVER_CONTEXT+panel.url;
					}
					content = '<iframe name="iframe'+panel.attrId+'" id="iframe'+panel.attrId+'" src="'+panel.url+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>';
				}
				var p = $('<div />').appendTo('body');
				p.panel({
					id:panel.attrId,
					title:panel.name,
					content:'<div style="height:100%">'+content+'</div>',
					height:panel.height,
					closable:true,
					tools:[{
						iconCls:'panel-tool-max',
						toolPosition:'right',
						handler:function(){
							popupDiv($(this).parent().parent().parent());
						}
					}],
					onClose:function(){
						saveLayout();
					}
				});
				$('#pp').portal('add', {
					panel:p,
					columnIndex:i
				});
			}
			
		}
		$('#pp').portal('resize');
	}
	
	function saveLayout(){
		var panels = $('#pp').portal('getPanels');
		var layoutS = '[';
		if(panels.length>0){
			for(var i=0; i<panels.length; i++){
				var p = panels[i];
				if(p.parent().css("display")=="none"){
					continue;
				}
				var rowIndex = p.parent().parent().children().index(p.parent());
				var colIndex = p.parent().parent().parent().parent().children().index(p.parent().parent().parent());
				layoutS += '{"id":"'+p.panel('options').id+'","colIndex":'+colIndex +',"rowIndex":'+rowIndex+'},';
			}
			if(layoutS.length>1){
				layoutS = layoutS.substring(0, layoutS.length-1);
			}
		}
		layoutS += ']';
		Util.ajax.loadData("../personalCenter/saveLayout.html", 
			function(data){
				if(data.success=="true"){
				
				}
			}, {layoutS:layoutS,ran:Math.random()}
		);
	}
	
	//初始化grid接口
	exports.init = function(opts) {
		//请求参数
		var columns = eval(opts.columns);
		$('#pp').portal({
			border:false,
			fit:true,
			onStateChange:function(){
				saveLayout();
			}
		});
		add(columns);

	};
});