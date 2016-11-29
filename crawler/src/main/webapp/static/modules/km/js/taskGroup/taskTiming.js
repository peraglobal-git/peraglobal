/**
 * 任务定时
 */
define(function(require,exports,module){
	 
	
	exports.checkTrigger=$(function(){
		$("#trigger").on("click",function(){
			var checkradio=$("input[name='monthType']:checked").val();
			var cheed=$("#trigger").is(":checked");
			if(cheed){
				$("input[name='triggerType']").removeAttr("disabled");
				$(".monthType").removeAttr("disabled");
				$("input[name='startTime']").removeAttr("disabled");
				$("input[name='stopTime']").removeAttr("disabled");
				$("input[name='triggerWeek']").removeAttr("disabled");
				$("#monthformonth").combobox('enable'); 
				if(checkradio==1){
					$("#monthforday").combobox('enable'); 
				}
				else if(checkradio==2){
					$("#monthforweek").combobox('enable'); 
					$("#monthforweek2").combobox('enable'); 
				}
				
			}else{
				$("input[name='triggerType']").attr("disabled","disabled");
				$(".monthType").attr("disabled","disabled");
				$("input[name='startTime']").attr("disabled","disabled");
				$("input[name='stopTime']").attr("disabled","disabled");
				$("input[name='triggerWeek']").attr("disabled","disabled");
				$("#monthformonth").combobox('disable');
				$("#monthforday").combobox('disable');
				$("#monthforweek").combobox('disable');
				$("#monthforweek2").combobox('disable');
			}
		});
		
	});
	
	
	exports.changeTriggerType=$(function(){
			 $("input[name='triggerType']").click(function(){
				 var checkradio=$("input[type='radio']:checked").val();
					if(checkradio==1){
						$("#trigger_timeforhour").show();
						$("#trigger_timeforday").hide();
						$("#trigger_timeforweek").hide();
						$("#trigger_timeforone").hide();
					}else if(checkradio==2){
						$("#trigger_timeforhour").hide();
						$("#trigger_timeforday").hide();
						$("#trigger_timeforweek").show();
						$("#trigger_timeforone").hide();
					}else if(checkradio==3){
						$("#trigger_timeforhour").hide();
						$("#trigger_timeforday").show();
						$("#trigger_timeforweek").hide();
						$("#trigger_timeforone").hide();
					}else if(checkradio==4){
						$("#trigger_timeforhour").hide();
						$("#trigger_timeforday").hide();
						$("#trigger_timeforweek").hide();
						$("#trigger_timeforone").show();
					}
			 });
		});
		
	
		
		
		
	exports.changeradio=$(function(){
			$(".monthType").click(function(){
				var checkradio=$("input[name='monthType']:checked").val();
				if(checkradio==1){
					$("#monthforweek").combobox('disable');
					$("#monthforweek2").combobox('disable');
					$("#monthforday").combobox('enable'); 
				}
				else if(checkradio==2){
					$("#monthforday").combobox('disable');
					$("#monthforweek").combobox('enable'); 
					$("#monthforweek2").combobox('enable'); 
					
				}
			});
			
		});
	});
