/**
 * 保存web采集页面
 */
define(function(require,exports,module){
	/**
	 * 二级联动
	 */
	$(function(){
		function RndNum(n){
		  var rnd="";
		  for(var i=0;i<n;i++)
		     rnd+=Math.floor(Math.random()*10);
		  return rnd;
		}
		// 选择知识源切换
		$("#knowledgeSource").change(function(){
			var oldSourceId = "0";
			var sourceId=$("#knowledgeSource").find("option:selected").val();
			if(sourceId=='0'){
				return;
			}
			var sourceName=$("#knowledgeSource").find("option:selected").text();
			$("#sourceId").val(sourceId);
			$("#sourceName").val(sourceName);
			var sourceType = $("#knowledgeSource").find("option:selected").attr("type");
			var linkState = $("#knowledgeSource").find("option:selected").attr("linkState");
			//alert(sourceId+"--"+sourceName+"--"+sourceType+"--"+linkState);
			var url="";
			if(sourceType=="1"){
				// web知识源
				if(linkState=="1"){
					alert("知识源连接失败，无法创建采集任务！");
					$("#knowledgeSource").val(oldSourceId);
					return;
				}
				url = '../taskJob/toCrawlerWebJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;;
			}else if(sourceType=="2"){
				// db知识源
				if(linkState=="1"){
					alert("知识源连接失败，无法创建采集任务！");
					$("#knowledgeSource").val(oldSourceId);
					return;
				}
				url = '../taskJob/toCrawlerDBJobPage.html?sourceId='+sourceId+'&sourceName='+sourceName;
			}else if(sourceType=="3"){
				// 本地知识源
				url = '../taskJob/toCrawlerLocalJobPage.html?sourceId='+sourceId+'&registerType=3'+'&sourceName='+sourceName;;
			}
			url=encodeURI(url);
			url=encodeURI(url); 
			window.location=url;
		});
	});
});