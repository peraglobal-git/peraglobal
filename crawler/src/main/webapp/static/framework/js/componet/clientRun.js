define(function(require,exports,module){
	/**
	 * ie浏览器运行方式
	 */
	ieRun = function (commond) {
		try{
	        var objShell = new ActiveXObject("wscript.shell");  
	        objShell.run(commond);
	        //objShell.run("http://192.168.60.66:8080/ppm/");  
	        objShell = null;  
		}catch(e){
			alert(e);
		}
    };
	
    netscapeRun = function(commond) {  
        try {  
            netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");  
            var file = Components.classes["@mozilla.org/file/local;1"]  
                    .createInstance(Components.interfaces.nsILocalFile);  
            file.initWithPath(commond);  
            var process = Components.classes['@mozilla.org/process/util;1']  
                    .createInstance(Components.interfaces.nsIProcess);  
            process.init(file);  
            var arguments = [ url ];  
            process.run(false, arguments, arguments.length);  
        } catch (e) {  
            alert(e);  
        }  
    };
    
    exports.runCmd = function(cmd){
    	if(Util.browser.isIE()){
    		ieRun(cmd);
    	}else if(Util.browser.isFirefox()){
    		netscapeRun(cmd);
    	}else{
    		alert("目前只支持在IE和火狐浏览器下调用本地应用程序");
    	}
    };
});