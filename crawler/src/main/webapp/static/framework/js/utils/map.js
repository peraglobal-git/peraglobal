/**
 * 	封装一个基于js的map
 * 
 * 	提供对应map的方法:
 * 		1、size方法：获得map的数据数量
 * 		2、isEmpty：是否为空
 * 		3、containsKey：是否包含对应的key键
 * 		4、containsValue：是否包含对应的value值
 * 		5、get：获取对应的值
 * 		6、put：添加值
 * 		7、remove：删除某个值
 * 		8、putAll：将一个map中所有的值添加到当前的map中
 * 		9、clear：清除所有的数据
 * 		10、values：获得所有的值数组
 * 		11、keySet：获得所有的key值数组
 * 		12、toString：将map中的数据转换为json格式的字符串
 * 		13、valueOf：将map中的数据以字符串的方式返回
 * 		14、toUrlString：将map中的数据转换成url请求参数的格式
 */

(function(win) {
    var Map = function() {
        this.count = 0;
        this.entrySet = {};
    };

    /**
     * 获得map中保存的数据数量
     * @returns {Number}
     */
    Map.prototype.size = function() {
        return this.count;
    };

    /**
     * 判断map是否为空
     * @returns {Boolean}
     */
    Map.prototype.isEmpty = function() {
        return this.count === 0;
    };

    /**
     * 是否包含对应的key键
     * @param key
     * @returns {Boolean}
     */
    Map.prototype.containsKey = function(key) {
        if (this.isEmpty()) {
            return false;
        }

        for ( var prop in this.entrySet) {
            if (prop === key) {
                return true;
            }
        }

        return false;
    };

    /**
     * 是否包含对应的value值
     * @param value
     * @returns {Boolean}
     */
    Map.prototype.containsValue = function(value) {
        if (this.isEmpty()) {
            return false;
        }

        for ( var key in this.entrySet) {
            if (this.entrySet[key] === value) {
                return true;
            }
        }

        return false;
    };

    /**
     * 获取map中对应key的值
     * @param key
     * @returns
     */
    Map.prototype.get = function(key) {
        if (this.isEmpty()) {
            return null;
        }

        if (this.containsKey(key)) {
            return this.entrySet[key];
        }

        return null;
    };

    
    /**
     * 添加新的键值对到map中
     * @param key
     * @param value
     */
    Map.prototype.put = function(key, value) {
        this.entrySet[key] = value;
        this.count++;
    };

    
    /**
     * 删除map中对应key的值
     * @param key
     */
    Map.prototype.remove = function(key) {
        if (this.containsKey(key)) {
            delete this.entrySet[key];
            this.count--;
        }
    };

    
    /**
     * 将另一个map中保存的数据添加到map中
     * @param map
     */
    Map.prototype.putAll = function(map) {
    	if(!map instanceof Map) {
    		return ;
    	}

        for ( var key in map.entrySet) {
            this.put(key, map.entrySet[key]);
        }
    };

    
    /**
     * 清除map中保存的数据
     */
    Map.prototype.clear = function() {
        for ( var key in this.entrySet) {
            this.remove(key);
        }
    };

    
    /**
     * 获取map中所有值的数组
     * @returns {Array}
     */
    Map.prototype.values = function() {
        var result = [];

        for ( var key in this.entrySet) {
            result.push(this.entrySet[key]);
        }

        return result;
    };

    
    /**
     * 获取map中所有key的键数组
     * @returns {Array}
     */
    Map.prototype.keySet = function() {
        var result = [];

        for ( var key in this.entrySet) {
            result.push(key);
        }

        return result;
    };

    
    /**
     * 转换为json格式的字符串
     */
    Map.prototype.toString = function() {
        var result = [];
        for ( var key in this.entrySet) {
            result.push("\""+key+"\"" + ":" + "\""+this.entrySet[key]+"\"");
        }

        return "{" + result.join() + "}";
    };

    
    /**
     * 转换为json格式的字符串
     */
    Map.prototype.valueOf = function() {
        return this.toString();
    };
    
    
    /**
     * 转换请求链接使用的参数
     * 
     * @returns
     */
    Map.prototype.toUrlString = function() {
        var result = [];
        for ( var key in this.entrySet) {
            result.push(key + "=" + this.entrySet[key]+"&");
        }
        if(result.length > 0) {
        	var len = result.length-1;
        	result[len] = result[len].replace("&","");
        	
        	return result.join("");
        }else {
        	return "";
        }
    };
    
    
    

    win.Map = Map;
})(window);