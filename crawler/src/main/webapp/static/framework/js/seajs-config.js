/**
 * seajs全局配置文件,对包引用路径进行配置
 */
seajs.config({
  alias:{
	  'artDialogJs':SERVER_CONTEXT+'/static/framework/js/componet/artDialogJs',
	  'clientRun':SERVER_CONTEXT+'/static/framework/js/componet/clientRun',
	  'combobox':SERVER_CONTEXT+'/static/framework/js/componet/combobox',
	  'tree':SERVER_CONTEXT+'/static/framework/js/componet/tree',
	  'treegrid':SERVER_CONTEXT+'/static/framework/js/componet/treegrid',
	  'datagrid':SERVER_CONTEXT+'/static/framework/js/componet/datagrid'
  },	
  //配置模块加载路径
  paths: {
    'framework': SERVER_CONTEXT+'/static/framework/js',
    'modules': SERVER_CONTEXT+'/static/modules/'
  }
});
