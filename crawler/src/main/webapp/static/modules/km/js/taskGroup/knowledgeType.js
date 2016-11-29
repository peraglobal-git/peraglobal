/**
 * 保存web采集页面
 */
define(function(require,exports,module){
		exports.changeSelect=$(function(){
			$("#knowledgeType").change(function(){
				var typeId=$("#knowledgeType").find("option:selected").val();
				var typeName=$("#knowledgeType").find("option:selected").text();
				$("#knowledgeTypeName").val(typeName);
				if(typeId == '0'){
					$("#knowledgeModel").empty();
					$("#knowledgeModel").append("<option value='0'>请选择</opion>");
					return;
				}
				$.ajax({
					type:'POST',
					url:"../taskJob/getModelsByTypeId.html",
					dataType:'json',
					data:{
						typeId:typeId
					},
					success:function(result){
						var models = $.parseJSON(result.models);
						$("#knowledgeModel").empty();
						$("#knowledgeModel").append("<option value='0'>请选择</opion>");
						for (var i = 0; i < models.length; i++) {
							$("#knowledgeModel").append("<option value='"+models[i].id+"'>"+models[i].name+"</opion>");
						}
					}
				});
					
			});
			$("#knowledgeModel").change(function(){
				var modelId=$("#knowledgeModel").find("option:selected").val();
				var modelName=$("#knowledgeModel").find("option:selected").text();
				$("#knowledgeModelName").val(modelName);
			});
			$("#system").change(function(){
				var systemId=$("#system").find("option:selected").val();
				var systemName=$("#system").find("option:selected").text();
				$("#systemName").val(systemName);
			});
		});
	});
