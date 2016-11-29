/**
 * Jquery Validate 自定义验证规则
 * 
 * addMethod(name,method,message)方法：
 * 参数name 是添加的方法的名字
 * 参数:
 * method是一个函数,接收三个参数(value,element,param) 
 * value 是元素的值,element是元素本身 
 * param 是参数,我们可以用addMethod 来添加除built-in Validation methods 之外的验证方法
 * 
 * @author TingHe.Mei
 * 
 */

/* 例：	比如有一个字段,只能输一个字母,范围是a-f,写法如下:
	$.validator.addMethod("af",function(value,element,params){
		if(value.length>1){
			return false;
		}
		if(value>=params[0] && value<=params[1]){
			return true;
		}else{
			return false;
		}
	},"必须是一个字母,且a-f");
	用的时候,比如有个表单字段的id=”username”,则在rules 中写
	username:{
	af:["a","f"]
	}
 	方法
	addMethod 的第一个参数,就是添加的验证方法的名子,这时是af
	addMethod 的第三个参数,就是自定义的错误提示,这里的提示为:”必须是一个字母,且a-f”
	addMethod 的第二个参数,是一个函数,这个比较重要,决定了用这个验证方法时的写法
	如果只有一个参数,直接写,如果af:"a",那么a 就是这个唯一的参数,如果多个参数,用在[]里,用逗号分开
*/

(function($) {
	
	// sql验证（不允许执行select）
	$.validator.addMethod("createSql", function(value, element) {
		return this.optional(element) ||  /^(?!select)/i.test(value);
	}, "不允许执行查询");
	
	// 账号只能输入：字母数字下划线，点，@
	$.validator.addMethod("usernamevalidate", function(value, element) {
		return this.optional(element) ||  /^[a-zA-Z0-9._@]+$/.test(value);
	}, "账号只能输入：字母数字下划线，点，@");
	
	// 配置库名只能输入：字母数字下划线，点，@
	$.validator.addMethod("confignamevalidate", function(value, element) {
		return this.optional(element) ||  /^[a-zA-Z0-9._@]+$/.test(value);
	}, "配置库名只能输入：字母数字下划线，点，@");
	
	// 只允许输入汉字、英文字母、数字及下划线，点，@
	$.validator.addMethod("titleInput", function(value, element) {
		return this.optional(element) ||  /^[a-zA-Z0-9._@\u4E00-\u9FA5]+$/.test(value);
	}, "只允许输入汉字、英文字母、数字及下划线，点，@");
	
	// 身份证号码验证
	$.validator.addMethod("isIdCardNo", function(value, element) {
		return this.optional(element) || isIdCardNo(value);
	}, "请正确输入您的身份证号码");

	// 只能输入中文
	$.validator.addMethod("CHS", function(value, element) {
		return this.optional(element) ||  /^[\u0391-\uFFE5]+$/.test(value);
	}, "只能输入汉字");
	
	// 不允许输入中文
	$.validator.addMethod("noCHS", function(value, element) {
		return this.optional(element) ||  /[\u0391-\uFFE5]+$/.test(value);
	}, "不允许输入汉字");
	
	// 邮政编码
	$.validator.addMethod("IsZip", function(value, element) {
		return this.optional(element) ||  /^\d{6}$/.test(value);
	}, "邮政编码格式不正确");
	
	// 手机号
	$.validator.addMethod("moblie", function(value, element) {
		return this.optional(element) ||  /^[1][358]d{9}$/.test(value);
	}, "输入的手机号格式不正确");
	
	// 联系电话(手机/电话皆可)验证
	$.validator.addMethod("isPhone", function(value,element) {
		var length = value.length;
		var mobile = /^(((13[0-9]{1})|(15[0-9]{1}))+d{8})$/;
		var tel = /^d{3,4}-?d{7,9}$/;
		return this.optional(element) || (tel.test(value) || mobile.test(value));
	}, "请正确填写您的联系电话");
	
	//联系电话(手机/电话皆可)验证  13812341234 或者 010-12345678 或者 (0432)1234567-1234
	$.validator.addMethod("isPhoneWay", function(value,element) {
		var mobile = /^1\d{10}$|^(0\d{2,3}-?|\(0\d{2,3}\))?[1-9]\d{4,7}(-\d{1,8})?$/;
		return this.optional(element) || mobile.test(value);
	}, "请正确填写您的联系方式如手机号，座机号");
	
	// 日期格式 yyyy-MM-dd HH:mm:ss
	$.validator.addMethod("isDateTime", function(value, element) {
		return this.optional(element) ||  /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/.test(value);
	}, "输入的日期格式不正确");
	
	// 日期格式 yyyy-MM-dd
	$.validator.addMethod("idDate", function(value, element) {
		return this.optional(element) ||  /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/.test(value);
	}, "输入的日期格式不正确");
	
	// 日期格式 yyyy-MM-dd
	$.validator.addMethod("noDoubleQuotes", function(value, element) {
		return this.optional(element) ||  /^[^\"]*$/.test(value);
	}, "输入的内容不能带有英文状态的双引号");
	
	// 验证IP是否正确
	$.validator.addMethod("noIpAddress", function(value, element) {
		return this.optional(element) ||  /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/.test(value);
	}, "请输入正确的IP格式");
	
	/*// 验证数字
	$.validator.addMethod("number", function(value, element) {
		return this.optional(element) ||  /(0|^[1-9]\d*$)/.test(value);
	}, "只允许输入数字");
	*/
	// 字符最小长度验证（一个中文字符长度为2）
	$.validator.addMethod("stringMinLength", function(value, element, param) {
		var length = value.length;
		for ( var i = 0; i < value.length; i++) {
			if (value.charCodeAt(i) > 127) {
				length++;
			}
		}
		return this.optional(element) || (length >= param);
	}, $.validator.format("长度不能小于{0}!"));
	
	// 字符最大长度验证（一个中文字符长度为2）
	$.validator.addMethod("stringMaxLength", function(value, element, param) {
		var length = value.length;
		for ( var i = 0; i < value.length; i++) {
			if (value.charCodeAt(i) > 127) {
				length++;
			}
		}
		return this.optional(element) || (length <= param);
	}, $.validator.format("长度不能大于{0}!"));
	
	// 字符验证
	$.validator.addMethod("string", function(value, element) {
		return this.optional(element) || /^[u0391-uFFE5w]+$/.test(value);
	}, "不允许包含特殊符号!");
	
	// 验证两次输入值是否不相同
	$.validator.addMethod("notEqualTo", function(value, element, param) {
		return value != $(param).val();
	}, $.validator.format("两次输入不能相同!"));
	
	// 验证值不允许与特定值等于
	$.validator.addMethod("notEqual", function(value, element, param) {
		return value != param;
	}, $.validator.format("输入值不允许为{0}!"));
	
	// 验证值必须大于特定值(不能等于)
	$.validator.addMethod("gt", function(value, element, param) {
		return value > param;
	}, $.validator.format("输入值必须大于{0}!"));

	//自定义校验数字加.小数(param[id],param[位数])
	/**
	 * 离焦之后 ---> 验证
	 */
	$.validator.addMethod("fnumber",function(value,element,param){
		if(/^-?\d+(\.\d+)?$/.test(value)){
			if(null!=value&&''!=value){
				$("#"+param[0]).blur(function(){
					$("#"+param[0]).val(Math.round(value * Math.pow(10, param[1])) / Math.pow(10, param[1]));
					if(!isNaN($("#"+param[0]).val())){
						var dot = $("#"+param[0]).val().indexOf(".");
						if(dot != -1){
						    var dotCnt = $("#"+param[0]).val().substring(dot+1,$("#"+param[0]).val().length);
						    if(dotCnt.length < param[1]){
								var f = "";
								var N = Number(param[1])-Number(dotCnt.length);
								if("0" != N){
									for(var i = 0;i<N;i++){
										f+="0";
									}
								}
								$("#"+param[0]).val($("#"+param[0]).val()+f);
						    }
						}
					}
					if(/^-?[0-9]\d*$/.test($("#"+param[0]).val())){
						var f = ".";
							if("0" != param[1]){
								for(var i = 0;i<param[1];i++){
									f+="0";
								}
							}
						$("#"+param[0]).val($("#"+param[0]).val()+f);
					}
				});
			}
			
		}
		return this.optional(element) ||  /^-?\d+(\.\d+)?$/.test(value);
	},"只允许输入数字");
	
	// 日期格式 yyyy-MM-dd
	$.validator.addMethod("anumber", function(value, element) {
		return this.optional(element) ||  /^-?[0-9]\d*\.?\d*\%?$/.test(value);
	}, "只允许输入数字或'%'");
	
	//select 下拉框验证 不能为空  （不包含easyUI的select）
	$.validator.addMethod("select",function(value,element){
		if(value == ""){
			return false;
		}else{
			return true;
		}
	},"不能为空");

	// 验证文件路径未过滤c:\windows c:\program files
	$.validator.addMethod("filePath", function(value, element) {
		return this.optional(element) ||  /^[a-zA-Z]:[\\/]((?! )(?![^\\/]*\s+[\\/])[\w-]+[\\/])*(?! )(?![^.]+\s+\.)[\w -]+$/.test(value);
	}, "请输入正确的文件路径");
	
	// 工时格式
	$.validator.addMethod("workload", function(value, element) {
		return this.optional(element) || /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d)?$/.test(value);
	}, "工时必须为整数或者一位小数");
	
})(jQuery);