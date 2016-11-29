
/**
 * 功能：给url添加jsessionId 防止session丢失。
 * @returns {String}
 */
String.prototype.getSessionUrl=function(){
	//jsessionid
	var url = this;
	if(url.indexOf(";jsessionid=")!=-1){
		return url;
	}
	if(url.indexOf("?")==-1){
		url+=";jsessionid=" +__jsessionid;
	}
	else{
		var aryUrl=url.split("?");
		url=aryUrl[0] +";jsessionid=" +__jsessionId +"?" + aryUrl[1];
	}
	return url;
};

/**
 * 功能：移除首尾空格
 */
String.prototype.trim = function() {
	this.replace(/(^\s*)|(\s*$)/g, "");
};
/**
 * 功能:移除左边空格
 */
String.prototype.lTrim = function() {
	this.replace(/(^\s*)/g, "");
};
/**
 * 功能:移除右边空格
 */
String.prototype.rTrim = function() {
	this.replace(/(\s*$)/g, "");
};



/**
 * 判断是否以str结尾
 * 
 * @param str
 * @param isCasesensitive
 * @returns {Boolean}
 */
String.prototype.endWith = function(str, isCasesensitive) {
	if (str == null || str == "" || this.length == 0
			|| str.length > this.length)
		return false;
	var tmp = this.substring(this.length - str.length);
	if (isCasesensitive == undefined || isCasesensitive) {
		return tmp == str;
	} else {
		return tmp.toLowerCase() == str.toLowerCase();
	}

};
/**
 * 判断是否以str开始
 * 
 * @param str
 * @param isCasesensitive
 * @returns {Boolean}
 */
String.prototype.startWith = function(str, isCasesensitive) {
	if (str == null || str == "" || this.length == 0
			|| str.length > this.length)
		return false;
	var tmp = this.substr(0, str.length);
	if (isCasesensitive == undefined || isCasesensitive) {
		return tmp == str;
	} else {
		return tmp.toLowerCase() == str.toLowerCase();
	}
};

/**
 * 在字符串左边补齐指定数量的字符
 * 
 * @param c
 *            指定的字符
 * @param count
 *            补齐的次数 使用方法： var str="999"; str=str.leftPad("0",3); str将输出 "000999"
 * @returns
 */
String.prototype.leftPad = function(c, count) {
	if (!isNaN(count)) {
		var a = "";
		for ( var i = this.length; i < count; i++) {
			a = a.concat(c);
		}
		a = a.concat(this);
		return a;
	}
	return null;
};

/**
 * 在字符串右边补齐指定数量的字符
 * 
 * @param c
 *            指定的字符
 * @param count
 *            补齐的次数 使用方法： var str="999"; str=str.rightPad("0",3); str将输出
 *            "999000"
 * @returns
 */
String.prototype.rightPad = function(c, count) {
	if (!isNaN(count)) {
		var a = this;
		for ( var i = this.length; i < count; i++) {
			a = a.concat(c);
		}
		return a;
	}
	return null;
};

/**
 * 对html字符进行编码 用法： str=str.htmlEncode();
 * 
 * @returns
 */
String.prototype.htmlEncode = function() {
	this.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g,
			"&gt;").replace(/\"/g, "&#34;").replace(/\'/g, "&#39;");
};

/**
 * 对html字符串解码 用法： str=str.htmlDecode();
 * 
 * @returns
 */
String.prototype.htmlDecode = function() {
	this.replace(/\&amp\;/g, '\&').replace(/\&gt\;/g, '\>').replace(
			/\&lt\;/g, '\<').replace(/\&quot\;/g, '\'').replace(/\&\#39\;/g,
			'\'');
};


/**
 * 字符串替换
 * 
 * @param str 替换的字符
 * @returns
 */
String.prototype.replaceAll = function(str) {
	this.replace(new RegExp(this, "gm"), str);
};

/**
 * 获取url参数
 * 
 * @returns {object} json格式的请求参数
 */
String.prototype.getArgs = function() {
	var args = {};
	if(this.indexOf("?")>-1){
		var argStr = this.split("?")[1],
			argAry = argStr.split("&");
		
		for(var i=0,c;c=argAry[i++];){
			var pos = c.indexOf("=");
			if(pos==-1)continue;
			var argName = c.substring(0,pos),
				argVal = c.substring(pos+1);
			argVal = decodeURIComponent(argVal);
			args[argName] = argVal;
		}
	}
	return args;
};


/**
 * 将一个字符串用给定的字符变成数组
 * @param str	分隔标志
 * @returns 数组
 */
String.prototype.toArray = function (str) {
    if (this.indexOf(str) != -1) {
        return this.split(str);
    }else {
    	if (this != '') {
    		return [this.toString()];
    	}else {
            return [];
        }
    }
};


/**
 * 将json格式的字符串转换为json格式
 * @param str	json格式的字符串
 * @return json
 */
String.prototype.toJson = function() {
	return eval('(' + this + ')');  
};




/**
 * 对url链接编码,可以将包含有中文的url进行编码,后台获取请求参数需要解码 URLDecoder.decode(参数, "UTF-8");
 * @return encodeURI编码后的字符串
 */
String.prototype.urlEncode = function() {
	return encodeURI(encodeURI(this));
};


/**
 * 对已经经过encodeURI方法编码的url进行解码
 * @return decodeURI解码后的字符串
 */
String.prototype.urlDecode = function() {
	return decodeURI(decodeURI(this));
};



/**
 * 	将带有下划线的字符串转换为首字母小写没有下划线的字符串<br>
 * 	例: Abc_BBc  转换以后:abcBBc
 * @return String
 */
String.prototype.getFirstLower = function(){
	var value="";
	if(this.indexOf('_')!=-1){
		var ary=this.split('_');
		for(var i=0;i<ary.length;i++){
			var tmp=ary[i];
			if(i==0){
				value+=tmp.charAt(0).toLowerCase() + tmp.substring(1,tmp.length);
			}else{
				value+=tmp;
			}
		}
	}else{
		value=this.substring(0,1).toLowerCase()+this.substring(1,this.length);
	}
	return value;
};


/**
 * 	将带有下划线的字符串转换为首字母小写没有下划线的字符串<br>
 * 	例: abc_BBc  转换以后:AbcBBc
 * @returns String
 */
String.prototype.getFirstUpper = function(){
	var value="";
	if(this.indexOf('_')!=-1){
		var ary=this.split('_');
		for(var i=0;i<ary.length;i++){
			var tmp=ary[i];
			value+=tmp.substring(0,1).toUpperCase()+tmp.substring(1,tmp.length+1);
		}
	}else{
		value=this.substring(0,1).toUpperCase()+this.substring(1,this.length);
	}
	return value;
};



/**
 * 验证字符串是否为空
 * @return {Boolean}	true:空 false:非空
 */
String.isEmpty = function(value) {
    
	return value === null || value === undefined || value === '';
};


/**
 * 验证字符串是否为空
 * @return {Boolean}	true:非空 false:空
 */
String.isNotEmpty = function(value) {
    
	return value !== null && value !== undefined && value !== '';
};


/**
 * 获取UUID
 * @returns	uuid字符串
 */
String.getUUID = function() {
	var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

	var chars = CHARS, uuid = new Array(36), rnd=0, r;
	for (var i = 0; i < 36; i++) {
	  if (i==8 || i==13 ||  i==18 || i==23) {
		uuid[i] = '-';
	  } else if (i==14) {
		uuid[i] = '4';
	  } else {
		if (rnd <= 0x02) rnd = 0x2000000 + (Math.random()*0x1000000)|0;
		r = rnd & 0xf;
		rnd = rnd >> 4;
		uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
	  }
	}
	return uuid.join('');
};

