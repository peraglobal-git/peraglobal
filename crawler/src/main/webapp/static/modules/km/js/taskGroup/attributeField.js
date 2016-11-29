/**
 * 模板属性字段
 */
define(function(require,exports,module){
	exports.queryAttribute=function(){
		$("input[name='attributes']").bind("click", function(){  
			var modelId=$("#knowledgeModel").find("option:selected").val();
			if(modelId == 0){
				alert('请选择知识模板！');
				return;
			}else{
					var title="模板属性字段";
					var param="modelId="+modelId;
					require('artDialogJs').openDialog({url:'../taskJob/getModelPropertiesById.html?'+param,title:title,
						width:'300px',
						height:'250px',
						
					});
			};
		});
	};
});