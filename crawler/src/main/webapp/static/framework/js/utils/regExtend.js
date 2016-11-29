/**
 * 验证一个字符串时候是email 
 * @param str
 * @returns
 */
RegExp.isEmail = function (str) {

    var emailReg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)*\.[\w-]+$/i;
    return emailReg.test(str);
};

/**
 * 验证一个字符串是否是URL
 * @param str
 * @returns
 */
RegExp.isUrl = function (str) {

    var patrn = /^http(s)?:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\:+!]*([^<>])*$/;
    return patrn.exec(str);
};

/**
 * 验证一个字符串是否是电话或传真
 * @param str
 * @returns
 */
RegExp.isTel = function (str) {

    var pattern = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
    return pattern.exec(str);
};

/**
 * 验证一个字符串是否是手机号码
 * @param str
 * @returns
 */
RegExp.isMobile = function (str) {

    var patrn = /^((13[0-9])|(15[0-35-9])|(18[0,2,3,5-9]))\d{8}$/;
    return patrn.exec(str);
};

/**
 * 验证一个字符串是否是汉字
 * @param str
 * @returns
 */
RegExp.isZHCN = function (str) {

    var p = /^[\u4e00-\u9fa5\w]+$/;
    return p.exec(str);
};

/**
 * 验证一个字符串是否是数字
 * @param str
 * @returns
 */
RegExp.isNum = function (str) {

    var p = /^\d+$/;

    return p.exec(str);

};

/**
 * 验证一个字符串是否是纯英文
 * @param str
 * @returns
 */
RegExp.isEnglish = function (str) {

    var p = /^[a-zA-Z., ]+$/;

    return p.exec(str);

};

/**
 * 判断是否为对象类型
 * @param obj
 * @returns {Boolean}
 */
RegExp.isObject = function (obj) {

    return (typeof obj == 'object') && obj.constructor == Object;
};

/**
 * 验证字符串是否不包含特殊字符 返回bool
 * @param str
 * @returns
 */
RegExp.isUnSymbols = function (str) {

    var p = /^[\u4e00-\u9fa5\w \.,()，ê?。¡ê（ê¡§）ê?]+$/;
    return p.exec(str);
};


/**
 * 验证数字
 * @param minLen:最小长度,不校验传0;maxLen:最大长度,不校验传0
 */
RegExp.isDigit = function(obj,minLen,maxLen,strNote) { 

	var patrn = /^\d|([1-9][0-9]{1,})$/;
	if (!patrn.exec(obj)) {
		alert(strNote + "必须由数字组成!");
		return false;
	}
	// 长度验证
	return RegExp.lengthLimit(obj, minLen, maxLen, strNote);
	return true; 
};


/**
 * 校验登录名：只能输入5-20个以字母开头、可带数字、“_”、“.”的字串 
 */
RegExp.checkUserName = function(obj) { 

	var patrn = /^[a-zA-Z]{1}([a-zA-Z0-9]|[._]){4,19}$/;
	if (!patrn.exec(obj)) {
		alert("登录名不合法！登录名只能由5-20个字母、数字、下划线组成，并以字母开头！");
		return false;
	}
	return true;
};


/**
 * 校验密码：只能输入6-20个字母、数字、下划线 
 */
RegExp.checkPwd = function(obj) { 

	var patrn = /^(\w){6,20}$/;
	if (!patrn.exec(obj)) {
		alert("密码格式不合法！只能输入字母、数字、下划线！");
		return false;
	}
	return true;
};


/**
 * 校验密码：两次密码是否一致
 */
RegExp.checkRepeatPwd = function(obj1,obj2) { 

	if (obj1.value != obj2.value) {
		alert("密码不一致！请重新输入");
		obj1.value = "";
		obj2.value = "";
		return false;
	}
	return true;
}; 


/**
 * 校验普通电话、传真号码：可以“+”开头，除数字外，可含有“-” 
 * @param canImpty:1,可空;0,必填
 */
RegExp.checkTel = function(obj,canImpty) { 

	if (canImpty == 1) {
		if (obj == "") {
			return true;
		}
	}
	var patrn = /^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
	if (!patrn.exec(obj)) {
		alert("电话/传真号码格式不正确！");
		return false;
	}
	return true;
};


/**
 * 验证手机号
 * @param canImpty:1,可空;0,必填
 */
RegExp.checkMobile = function(obj,canImpty) {	

	if (canImpty == 1) {
		if (obj == "") {
			return true;
		}
	}
	if (!(/^1[\d]{10}$/g.test(obj))) {
		alert("手机号码格式不正确，请确认！");
		return false;
	}
};


/**
 * 只能是汉字
 */
RegExp.isChinese = function(obj,strNote) {
	var reg = /^[\u4e00-\u9fa5]+$/gi;
	if (!reg.test(obj)) {
		alert("【" + strNote + "】必须为汉字!");
		// obj.select();
		return false;
	}
	return true;
};



/**
 * 长度限制
 * @param obj:对象,minLen:最小长度,maxLen:最大长度
 */
RegExp.lengthLimit = function(obj,minLen,maxLen,strNote) {
	if (minLen > 0 && maxLen > 0) {
		if (obj.length < minLen || obj.length > maxLen) {
			alert("【" + strNote + "】长度必须是" + minLen + "至" + maxLen + "个字符!");
		}
	} else if (maxLen > 0) {
		if (obj.length > maxLen) {
			alert("【" + strNote + "】不能超过" + maxLen + "个字符！");
			// obj.select();
			return false;
		}
	} else if (minLen > 0) {
		if (obj.length < minLen) {
			alert("【" + strNote + "】不能少于" + minLen + "个字符！");
			// obj.select();
			return false;
		}
	}
	return true;
};



/**
 * 验证邮箱格式
 * @param canImpty:1,可空;0,必填
 */
RegExp.isEmail = function(obj, canImpty) {
	if (canImpty == 1) {
		if (obj == "") {
			return true;
		}
	}
	if (obj.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1) {
		return true;
	} else {
		alert("邮箱格式不正确!");
		// obj.select();
		return false;
	}
};


/**  
 * 身份证15位编码规则：dddddd yymmdd xx p   
 * dddddd：地区码   
 * yymmdd: 出生年月日   
 * xx: 顺序类编码，无法确定   
 * p: 性别，奇数为男，偶数为女  
 * <p />  
 * 身份证18位编码规则：dddddd yyyymmdd xxx y   
 * dddddd：地区码   
 * yyyymmdd: 出生年月日   
 * xxx:顺序类编码，无法确定，奇数为男，偶数为女   
 * y: 校验码，该位数值可通过前17位计算获得  
 * <p />  
 * 18位号码加权因子为(从右到左) Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2,1 ]  
 * 验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]   
 * 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )   
 * i为身份证号码从右往左数的 2...18 位; Y_P为脚丫校验码所在校验码数组位置  
 *   
 * @param canImpty:1,可空;0,必填
 */  
RegExp.checkIdCard = function(obj,canImpty) {   

	    idCard = RegExp.trim(obj.replace(/ /g, ""));
	if (canImpty == 1) {
		if (obj == "") {
			return true;
		}
	}
	if (idCard.length == 15) {
		return RegExp.isValidityBrithBy15IdCard(idCard);
	} else if (idCard.length == 18) {
		var a_idCard = idCard.split("");// 得到身份证数组
		if (RegExp.isValidityBrithBy18IdCard(idCard)
				&& RegExp.isTrueValidateCodeBy18IdCard(a_idCard)) {
			return true;
		} else {
			return false;
		}
	} else {
		return false;
	}
};

/**
 * 日期验证
 * 
 * @param :obj,日期对象,值格式:YYYY-mm-dd
 */
RegExp.checkDate = function(obj) {
	var strDate = obj;
	var result = strDate
			.match(/((^((1[8-9]\d{2})|([2-9]\d{3}))(-)(10|12|0?[13578])(-)(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(-)(11|0?[469])(-)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(-)(0?2)(-)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)(-)(0?2)(-)(29)$)|(^([3579][26]00)(-)(0?2)(-)(29)$)|(^([1][89][0][48])(-)(0?2)(-)(29)$)|(^([2-9][0-9][0][48])(-)(0?2)(-)(29)$)|(^([1][89][2468][048])(-)(0?2)(-)(29)$)|(^([2-9][0-9][2468][048])(-)(0?2)(-)(29)$)|(^([1][89][13579][26])(-)(0?2)(-)(29)$)|(^([2-9][0-9][13579][26])(-)(0?2)(-)(29)$))/);
	if (result == null) {
		alert("请输入正确的日期格式！,例如：2015-01-01。\r\n 或从时间列表中选择日期。");
		return false;
	}
	return true;
};
/**
 * 月份验证
 * 
 * @param :obj,月份对象,格式:YYYY-mm
 */
RegExp.checkMon = function(obj) {
	var strDate = obj;
	var result = strDate.match(/^(19|20)\d{2}-((0[1-9])|(1[0-2]))$/);
	if (result == null) {
		alert("请输入正确的日期格式！如：2015-05");
		return false;
	}
	return true;
};