
/**
 * 根据复选框的名称获取选中值，使用逗号分隔。
 * 
 * @param name input标签的name
 * @returns {String}
 */
jQuery.getChkValue = function(name) {
	var str = "";
	$('input[type="checkbox"][name=' + name + ']').each(function() {
		if ($(this).attr('checked')) {
			str += $(this).val() + ",";
		}
	});
	if (str != "")
		str = str.substring(0, str.length - 1);
	return str;
};

/**
 * 根据名称获取下拉框的列表的值，使用逗号分隔。
 * 
 * @param name	下拉框中的name
 * @returns {String}
 */
jQuery.getSelectValue = function(name) {
	var str = "";
	$('select[name=' + name + '] option').each(function() {
		str += $(this).val() + ",";
	});
	if (str != "")
		str = str.substring(0, str.length - 1);
	return str;
};

/**
 * 复制到剪贴板
 */
jQuery.copyToClipboard = function(txt) {
	if (window.clipboardData) {
		window.clipboardData.clearData();
		window.clipboardData.setData("Text", txt);
		return true;
	} else if (navigator.userAgent.indexOf("Opera") != -1) {
		window.location = txt;
		return false;
	} else if (window.netscape) {
		try {
			netscape.security.PrivilegeManager
					.enablePrivilege("UniversalXPConnect");
		} catch (e) {
			alert($lang.tip.msg,$lang_js.util.copyToClipboard.netscape);
			return false;
		}
		var clip = Components.classes['@mozilla.org/widget/clipboard;1']
				.createInstance(Components.interfaces.nsIClipboard);
		if (!clip)
			return false;
		var trans = Components.classes['@mozilla.org/widget/transferable;1']
				.createInstance(Components.interfaces.nsITransferable);
		if (!trans)
			return false;
		trans.addDataFlavor('text/unicode');

		var str = Components.classes["@mozilla.org/supports-string;1"]
				.createInstance(Components.interfaces.nsISupportsString);
		var copytext = txt;
		str.data = copytext;
		trans.setTransferData("text/unicode", str,
				copytext.length * 2);
		var clipid = Components.interfaces.nsIClipboard;
		if (!clip)
			return false;
		clip.setData(trans, null, clipid.kGlobalClipboard);
		return true;
	} else {
		alert($lang.tip.msg,$lang_js.util.copyToClipboard.notCopy);
		return false;
	}
};

/**
 * 拷贝指定文本框的值。
 * 
 * @param objId
 */
jQuery.copyInputTxt = function(objId) {
	var str = $("#" + objId).val();
	var rtn = jQuery.copyToClipboard(str);
	if (!rtn) {
		alert("拷贝至剪贴板失败");
	}
};

/**
 * <img src="img/logo.png" onload="$.fixPNG(this);"/>
 * 解决图片在ie中背景透明的问题。
 * 
 * @param imgObj
 */
jQuery.fixPNG = function(imgObj) {
	var arVersion = navigator.appVersion.split("MSIE");
	var version = parseFloat(arVersion[1]);
	if ((version >= 5.5) && (version < 7)
			&& (document.body.filters)) {
		var imgID = (imgObj.id) ? "id='" + imgObj.id + "' " : "";
		var imgClass = (imgObj.className) ? "class='"
				+ imgObj.className + "' " : "";
		var imgTitle = (imgObj.title) ? "title='" + imgObj.title
				+ "' " : "title='" + imgObj.alt + "' ";
		var imgStyle = "display:inline-block;"
				+ imgObj.style.cssText;
		var strNewHTML = "<span "
				+ imgID
				+ imgClass
				+ imgTitle
				+ " style=\""
				+ "width:"
				+ imgObj.width
				+ "px; height:"
				+ imgObj.height
				+ "px;"
				+ imgStyle
				+ ";"
				+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
				+ "(src=\'" + imgObj.src
				+ "\', sizingMethod='scale');\"></span>";
		imgObj.outerHTML = strNewHTML;
	}
};




/**
 * 通过js设置表单的值。
 * 
 * @param data	json格式的表单数据
 */
jQuery.setFormByJson = function(data) {
	var json = data;
	if (typeof (data) == "string") {
		json = jQuery.parseJSON(data);
	}

	for ( var p in json) {

		var value = json[p];
		var frmElments = $("input[name='" + p+ "'],textarea[name='" + p + "']");
		if (frmElments[0]) {
			frmElments.val(value);
		}
	}
};


/**
 * 清除表单
 */
jQuery.clearQueryForm = function(){
	$("input[name^='Q_'],select[name^='Q_']").each(function(){
		$(this).val('');
	});
};



/**
 * 获取浏览器窗口的宽和高
 * @return {
	height:myHeight,
	width:myWidth
  }
 */
jQuery.getWindowRect = function() {
  var myWidth = 0, myHeight = 0;
  if( typeof( window.innerWidth ) == 'number' ) {
    //Non-IE
    myWidth = window.innerWidth;
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myWidth = document.documentElement.clientWidth;
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myWidth = document.body.clientWidth;
    myHeight = document.body.clientHeight;
  }
  return {
	height:myHeight,
	width:myWidth
  };
};



/**
 * 禁用刷新。通过传入浏览器类型 来指定禁用某个浏览器的刷新
 * @param exp 浏览器类型<br> ie "MSIE" , firefox "Firefox" , Chrome "Chrome" , Opera  "Opera" , Safari  "Safari"
 * @return 无
 */
jQuery.forbidF5 = function(exp) {
	var currentExplorer = window.navigator.userAgent;

	if (currentExplorer.indexOf(exp) >= 0) {
		document.onkeydown = function(e) {
			var ev = window.event || e;
			var code = ev.keyCode || ev.which;
			if (code == 116) {
				ev.keyCode ? ev.keyCode = 0 : ev.which = 0;
				cancelBubble = true;
				return false;
			}
		};
	}
};

