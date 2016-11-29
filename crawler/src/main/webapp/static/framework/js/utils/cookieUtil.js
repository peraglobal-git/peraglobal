

/**
 * 设置cookie
 * 
 * @param name
 * @param value
 */
Cookie.setCookie = function(name, value) {
	var expdate = new Date();
	var argv = arguments;
	var argc = arguments.length;
	var expires = (argc > 2) ? argv[2] : null;
	var path = (argc > 3) ? argv[3] : null;
	var domain = (argc > 4) ? argv[4] : null;
	var secure = (argc > 5) ? argv[5] : false;
	if (expires != null)
		expdate.setTime(expdate.getTime() + (expires * 1000));

	document.cookie = name
			+ "="
			+ escape(value)
			+ ((expires == null) ? "" : (";  expires=" + expdate
					.toGMTString()))
			+ ((path == null) ? "" : (";  path=" + path))
			+ ((domain == null) ? "" : (";  domain=" + domain))
			+ ((secure == true) ? ";  secure" : "");
};

/**
 * 删除cookie
 * 
 * @param name
 */
Cookie.delCookie = function(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = GetCookie(name);
	document.cookie = name + "=" + cval + ";  expires="
			+ exp.toGMTString();

};

/**
 * 读取cookie
 * 
 * @param name
 * @returns
 */
Cookie.getCookie = function(name) {
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
	var i = 0;
	while (i < clen) {
		var j = i + alen;
		if (document.cookie.substring(i, j) == arg)
			return $.getCookieVal(j);
		i = document.cookie.indexOf("  ", i) + 1;
		if (i == 0)
			break;
	}
	return null;

};
