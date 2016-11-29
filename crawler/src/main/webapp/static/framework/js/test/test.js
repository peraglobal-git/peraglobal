function del(id){
  var ids ="";
  if(typeof(id) != "undefined"){
   ids = id;
  }else{
   ids = getCheckBoxItem();
   if(ids==""){
    showWarningTip("请选择要删除的记录");
    return false;
   }
  }
  showQuestionTip({
   content : '确定要删除吗？',
   ok : function() {
    //使用ajax进行操作
     $.post("../test/doDelete.html",{random:Math.random(),ids:ids},function(data){
      //刷新页面
      if(data.code=="ok"){
        reloadCallBack();
      }else{
       showWarningTip({content:"删除漏洞信息失败，这可能是由于选择的漏洞下包含关联漏洞"});
        //reloadCallBack();
      }
     },"json");
    return true;
   }
  });
  
 
 }
 
 function saveHandler(id){
  $.dialog.data('method', function(){
   reloadCallBack();
  });
  $.dialog.data('saveHandlerMethod', function(){
   $("#saveAddFlag").val("true");
   reloadCallBack();
  });
  $.dialog.open("../test/saveTestPage.html?id="+id, {
   width : 900,
   height : 550,
   title : "添加/修改页面"/* ,
   close:function(t){
    $('form').submit();
    if(saveAddFlag){
     saveHandler();
    }
   } */
  }).lock();
 }