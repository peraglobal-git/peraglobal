/**
 * 邮件配置js
 */
define(function (require, exports, module)
{

    //artDialog
    var dialog = require('artDialogJs');

    //datagrid 表格
    var dataGrid = require('datagrid');

    //datagrid table Id
    var gridId = "grid";
    var grid = $('#' + gridId);

    //最后选择的ID
    var lastDataId = "";
    

    /**
     * 重载
     */
    function reload()
    {
      //  grid.datagrid('reload');
        $("#search_button").click();
    }

    //datagrid 加载成功事件
    function onGridLoadSuccess(data)
    {
    	$('#'+gridId).datagrid('clearSelections');
        var gridDataLength = data.rows.length;
        if (gridDataLength > 0)
        {
            if (lastDataId != "")
            {
                grid.datagrid('selectRecord', lastDataId);
            } else
            {
                grid.datagrid('selectRow', 0);
            }
        }
    }

    //选择表格事件
    function onSelect(rowIndex, rowData)
    {
        //获取行数据ID
        lastDataId = rowData.id;
    }

    //列
    var columns = [[
        {field: 'id', checkbox: true},
        {field: 'name', halign: 'center', align: 'left', width: 220, title: '名称'},
        {field: 'code', halign: 'center', align: 'left', width: 220, title: '编码'},
        {field: 'value', halign: 'center', align: 'left', width: 230, title: '值'}
    ]];

    //请求连接
    var url = '../globalConfig/getGlobalConfigList.html';

    //请求参数
    var param = {};


    //初始化grid接口
    exports.gridLoad = function ()
    {
        //初始化
        return dataGrid.init(gridId, {
            url: url,
            columns: columns,
            queryParams: param,
            toolbar:'#toolbar',
            pagination: true,
            singleSelect: true,
            onLoadSuccess: onGridLoadSuccess,	
            onSelect: onSelect
        });
    };


    /**
     * 弹出添加、修改dialog
     */
    function openAddDialog(title, param, id)
    {
        dialog.openDialog({
            url: '../globalConfig/toAddPage.html?' + param,
            title: title,
            id: id,
            width: '480px',
            height: '360px',
            reloadCallBack: function (data)
            {
				lastDataId = data.id;
                reload();
            }
        });
    }


    /**
	 * 搜索按钮点击
	 */
	function onSearchButtonClick() {
		 $("#search").inputTextEvent({callback:function(val){
			    $('#grid').datagrid("load",Util.ajax.serializable($("#listForm")));
		  }});
	}
	
	function deleteCustomConfig() {
		$("#deleteCustomConfig").on("click", function ()
        {
			var rows = $('#'+gridId).datagrid('getSelections');
			submitDel('../globalConfig/delGlobalConfig.html',{
				rows:rows,
				callback:function(data){
					reload();
				}
			});
        });
	}
	
    //初始化方法
    exports.init = function ()
    {
    	onSearchButtonClick();
    	deleteCustomConfig();
    
        //添加方法
        $("#addCustomConfig").on("click", function ()
        {
            var dialogId = 'mailConfig';
            var param = "ran=" + Math.random();
            var dialogTitle = '添加自定义参数配置';
            openAddDialog(dialogTitle, param, dialogId);
        });
    };
});