/**
 * user 用户JS
 */
define(function(require,exports,module){
	//artDialog
	var dialog  = require('artDialogJs');

	function openCardDialog(){

		   $("#labelBtn").on("click",function(){
               if(!expanded)  barSwap();
               //var card2 = $("#card2");
                var temp = [];
                $("#showDiv2").find(".yyyy div").each(function (i, obj){
            	   temp.push(obj.id);
		         });
				dialog.openDialog({
					title:'标签维护',
					width:'450px',
					height:'180px',
					content:document.getElementById('card2'),
					id:'cardDialog',
					close:function(){
						var arr = [];
		                $("#showDiv2").find(".yyyy div").each(function (i, obj){
		                    arr.push(obj.id);
		                });
		                var str = arr.join();
		                if(str !=temp){
		                	 var param = str.toString();
		                        Util.ajax.loadData("../personalCenter/saveCardInfo.html",
		                                function (data)
		                                {
		                                    if (data.success == "true")
		                                    {
		                                        location.href = "../personalCenter/toPersonalPage.html";
		                                    }
		                                }, {paramstr: param, ran: Math.random()}
		                        );
		                }
					}
				});
               $(".yyyy").shapeshift();
			   $("#card2 .del_me").remove();
		    });

	   }


	/**
	 * 启用按钮
	 */
	function onUserEnableClick() {
		$('#enableUser').on('click',function() {
			alert("启用按钮事件");
		});
	}

	//初始化方法
	exports.init = function() {
		onUserEnableClick();
		openCardDialog();
	};
});