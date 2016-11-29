/**
 * 常用的日期方法的封装,包含有日期格式化,字符串转日期,日期比较等方法
 * 									郭宏波		2015年4月23日
 */


/**  
* 日期格式化<br>
* 格式 YYYY/yyyy/YY/yy 表示年份<br>  
* MM/M 月份<br>
* W/w 星期<br>
* dd/DD/d/D 日期<br>  
* hh/HH/h/H 时间<br>
* mm/m 分钟<br>
* ss/SS/s/S 秒<br>
* @param	formatStr	传入的日期格式	&nbsp; 例:yyyy-MM-dd HH:mm:ss
* @return	格式化的日期字符串  
*/
Date.prototype.format = function(formatStr)  
{  
	 var str = formatStr;  
	 var Week = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];
	
	 str=str.replace(/yyyy|YYYY/,this.getFullYear());  
	 str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));  
	
	 str=str.replace(/MM/,(this.getMonth()+1)>9?(this.getMonth()+1).toString():'0' + (this.getMonth()+1));  
	 str=str.replac
	 .
	 e(/M/g,(this.getMonth()+1));  
	
	 str=str.replace(/w|W/g,Week[this.getDay()]);  
	
	 str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());  
	 str=str.replace(/d|D/g,this.getDate());  
	
	 str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());  
	 str=str.replace(/h|H/g,this.getHours());  
	 str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());  
	 str=str.replace(/m/g,this.getMinutes());  
	
	 str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());  
	 str=str.replace(/s|S/g,this.getSeconds());  
	
	 return str;  
};



/**
 * 转化日期字符串为标准字符串格式<br>
 * 转化前:1970-01-01<br>
 * 转化后:1970/01/01
 * @param str	转化前的字符串
 * @returns 	转化后的字符串	
 */
Date.exFormatDateStr = function(str) {
	if(typeof(str) === 'string') {
		var res = str.replace(/-/g,"/");
		return res;		
	}else {
		return str;
	}
};



/**
 * 将字符串转化为日期格式, 字符串的格式为1970-01-01
 * @param str	日期格式的字符串
 * @return 日期对象
 */
Date.strToDate = function(str) {
	var dateStr = null;
	if(typeof(str) === 'string') {
		dateStr = Date.exFormatDateStr(str);		
		if(dateStr.indexOf(" ") != -1) {
			var arr = dateStr.split(" ");
			var len = arr[1].length;
			if(len == 2) {
				dateStr = dateStr + ":00:00";
			}else if(len == 5) {
				dateStr = dateStr + ":00";
			}
		}
	}else {
		dateStr = str;
	}
	return new Date(dateStr);
};


/**
 * 获取日期是一周的第几天,周日是第一天
 * @returns	汉字:一,二,三,四,五,六,日
 */
Date.prototype.getWeek = function() {
	var Week = ['一','二','三','四','五','六','日'];
	return Week[this.getDay()];
};




/**
 * 比较两个日期,第一个传入的应该是早期的日期,第二个传入的应该是晚于第一个日期的
 * @param	dateOne	第一个日期
 * @param	dateTwo	第二个日期
 * @return	true 第一个日期小于第二个日期<br>&nbsp;&nbsp;&nbsp;
 * 			false 第一个日期大于第二个日期<br>&nbsp;&nbsp;&nbsp;
 * 			null 至少有一个日期为空<br>&nbsp;&nbsp;&nbsp;
 * 			"same" 字符串,表示两个日期相等<br>&nbsp;&nbsp;&nbsp;
 */
Date.compareDate = function(dateOne,dateTwo) {
	var dateO = null;
	var dateT = null;
	
	if(typeof(dateOne) === 'string') {
		dateO = Date.strToDate(dateOne);
	}else {
		dateO = dateOne;
	}
	
	if(typeof(dateTwo) === 'string') {
		dateT = Date.strToDate(dateTwo);
	}else {
		dateT = dateTwo;
	}
	
	if(dateO != null && dateT != null) {
		dateO = Date.UTC(dateO.getFullYear(),dateO.getMonth(),dateO.getDate(),dateO.getHours(),dateO.getMinutes(),dateO.getSeconds());
		dateT = Date.UTC(dateT.getFullYear(),dateT.getMonth(),dateT.getDate(),dateT.getHours(),dateT.getMinutes(),dateT.getSeconds());
		
		if(dateO > dateT) {
			return false; 
		}else if(dateT > dateO) {
			return true;
		}else {
			return "same";
		}
	}else {
		return null;
	}
};



/**
 * 求两个时间的天数差 日期格式为 yyyy-MM-dd 或 YYYY-MM-dd HH:mm:ss 或 date类型
 * @param dateOne	前一个日期
 * @param dateTwo	后一个日期
 * @returns	两个日期之间的相差的天数
 */
Date.daysBetween = function(dateOne,dateTwo) {
	var dateO = null;
	var dateT = null;
	var days = null;
	if(typeof(dateOne) === 'string') {
		dateO = Date.strToDate(dateOne);
	}else {
		dateO = dateOne;
	}
	
	if(typeof(dateTwo) === 'string') {
		dateT = Date.strToDate(dateTwo);
	}else {
		dateT = dateTwo;
	}
	
	if(dateO != null && dateT != null) {
		dateO = Date.UTC(dateO.getFullYear(),dateO.getMonth(),dateO.getDate());
		dateT = Date.UTC(dateT.getFullYear(),dateT.getMonth(),dateT.getDate());
		
		if(dateO > dateT) {
			days = eval((dateO - dateT)/86400000);
		}else {
			days = eval((dateT - dateO)/86400000);
		}
		
		return days;
	}else {
		return null;
	}
};


/**
 * 求两个时间的小时数差 日期格式为 yyyy-MM-dd 或 YYYY-MM-dd HH:mm:ss 或 date类型
 * @param dateOne	前一个日期
 * @param dateTwo	后一个日期
 * @returns	两个日期之间的相差的小时数
 */
Date.hoursBetween = function(dateOne,dateTwo) {
	var dateO = null;
	var dateT = null;
	var hours = null;
	if(typeof(dateOne) === 'string') {
		dateO = Date.strToDate(dateOne);
	}else {
		dateO = dateOne;
	}
	
	if(typeof(dateTwo) === 'string') {
		dateT = Date.strToDate(dateTwo);
	}else {
		dateT = dateTwo;
	}
	
	if(dateO != null && dateT != null) {
		dateO = Date.UTC(dateO.getFullYear(),dateO.getMonth(),dateO.getDate(),dateO.getHours());
		dateT = Date.UTC(dateT.getFullYear(),dateT.getMonth(),dateT.getDate(),dateT.getHours());
		
		if(dateO > dateT) {
			hours = eval((dateO - dateT)/3600000);
		}else {
			hours = eval((dateT - dateO)/3600000);
		}
		
		return hours;
	}else {
		return null;
	}
};


/**
 * 根据年份和月份获取某个月的天数。
 * 
 * @param year
 * @param month
 * @returns
 */
Date.getMonthDays = function(year, month) {
	if (month < 0 || month > 11) {
		return 30;
	}
	var arrMon = new Array(12);
	arrMon[0] = 31;
	if (year % 4 == 0) {
		arrMon[1] = 29;
	} else {
		arrMon[1] = 28;
	}
	arrMon[2] = 31;
	arrMon[3] = 30;
	arrMon[4] = 31;
	arrMon[5] = 30;
	arrMon[6] = 31;
	arrMon[7] = 31;
	arrMon[8] = 30;
	arrMon[9] = 31;
	arrMon[10] = 30;
	arrMon[11] = 31;
	return arrMon[month];
};


/**
 * 计算日期为当年的第几周,每周从周日开始
 * 
 * @param year 年
 * @param month 月
 * @param day 日
 * @returns
 */
Date.getWeekOfYear = function(year, month, day) {

	var date1 = new Date(year, 0, 1);
	var date2 = new Date(year, month - 1, day, 1);
	var dayMS = 24 * 60 * 60 * 1000;
	var firstDay = (7 - date1.getDay()) * dayMS;
	var weekMS = 7 * dayMS;
	date1 = date1.getTime();
	date2 = date2.getTime();
	return Math.ceil((date2 - date1 - firstDay) / weekMS) + 1;
};


/**
 * 获取服务器时间					暂时还没有写
 * @param formatStr	时间的格式
 * @returns
 */
Date.getHostDate = function(formatStr) {
	var hostDate;
	$.ajax({
		   type: "POST",
		   url: "",
		   async:false,
		   dataType:"text",
		   success: function(dateStr){
			   hostDate = dateStr;
		   }
	});
	return hostDate;
};



/**
 * 获取日期的字符串
 * @returns
 */
Date.prototype.getDateString = function() {
	return this.toLocaleDateString();
};

/**
 * 获取时间的字符串
 * @returns
 */
Date.prototype.getTimeString = function() {
	return this.toLocaleTimeString();
}