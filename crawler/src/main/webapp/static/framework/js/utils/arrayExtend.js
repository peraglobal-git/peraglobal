/**
 *	对JS中的Array进行扩展,包含有插入,删除,替换,截取等方法
 *							郭宏波 2015年4月22日
 *
 *
 *
 *
 *
 */




/**
 * 在指定的位置插入元素
 * @param startIndex	插入位置的索引
 * @param data			插入的元素
 * @returns {Array}
 */
Array.prototype.insert = function(startIndex, data) {
	this.splice(startIndex, 0, data);
};

/**
 * 删除指定位置的一个或者多个元素
 * @param startIndex	删除项的索引
 * @param delLen		删除的元素
 * @returns {Array}	
 */
Array.prototype.del = function(startIndex, delLen) {
	this.splice(startIndex, delLen);
};

/**
 * 替换指定位置的元素
 * @param index			替换项的索引
 * @param obj			替换元素
 * @returns {Array}
 */
Array.prototype.replace = function(index, obj) {
	this.splice(index, 1, obj);
};

/**
 * 截取指定位置,指定长度的数组
 * @param startIndex	起始位置索引
 * @param len			截取长度
 * @returns
 */
Array.prototype.subArray = function(startIndex, len) {
	this.slice(startIndex, startIndex + len);
};

/**
 * 根据元素取得在数组中的索引
 * @param obj			匹配的元素
 * @returns {Number}
 */
Array.prototype.getIndex = function (obj) {
    for (var i = 0; i < this.length; i++) {
		if (obj === this[i]) {
			return i;
		}
    }
    return -1;
};

/**
 * 判断元素是否在数组中
 * @param obj			用于匹配的元素
 * @returns {Boolean}
 */
Array.prototype.contains = function (obj) {
    for (var i = 0; i < this.length; i++) {
		if (obj === this[i]) {
			return true;
		}	
    }
    return false;
};

/**
 * 判断数组是否为空
 * @returns {Boolean}
 */
Array.prototype.isEmpty = function() {
    if (this.length == 0)
        return true;
    else
        return false;
};

/**
 * 检查某个值是否在数组中
 * @param value 要检查的值
 * @return 检查结果，如果为-1 则不在，否则返回位置
 */
Array.prototype.indexOf = function(value){
	//如果要检查的值异常
	if(!value){
		return -1;
	}
	for(var i = 0;i < this.length;i ++){
		if(value == this[i]){
			return i;
		}
	}
	return -1;
};


/**
 * 对数组的项拼接文字,如果index = -1 则为所有的元素拼接相同的值
 * 用法:
 * 	  var a = ['1','2','3','4','5']; a.joinstr(1,'文字');  给第二项拼接文字
 * 	  var a = ['1','2','3','4','5']; a.joinstr(-1,'文字');  给所有拼接文字
 * @param str
 * @returns {Array}
 */
Array.prototype.joinstr = function(index,str) {
	if(index !== -1) {
		for (var i = 0; i < this.length; i++) {
			if(index == i) {
				this[i] = this[i] + str;
			}
		}
	}else {
		for (var i = 0; i < this.length; i++) {
			this[i] = this[i] + str;
		}
	}
};