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
        grid.datagrid('reload');
    }


    /**
     * 提交删除
     */
    function submitDel(rows)
    {
    	var flag = false;
    	showQuestionTip({
			content:'确认删除选中的数据？',
			ok:function(){
				 flag = true;
			}
		});
        if (flag)
        {
            var idArr = [];
            if (rows.length > 0)
            {
                for (var index in rows)
                {
                    idArr.push(rows[index].id);
                }
            } else
            {
                idArr.push(rows.id);
            }
            var ids = idArr.toString();

            Util.ajax.loadData("../mail/delMailConfig.html",
                function (data)
                {
                    if (data.success == "false")
                    {
                        showErrorTip(data.msg);
                    } else
                    {
                        reload();
                    }
                }, {id: ids, ran: Math.random()}
            );
        }
    }

    /**
     * 发送邮件
     */
    function submitSend(rows)
    {
        if (window.confirm('小心，我要扔闪光了'))
        {
            var idArr = [];
            if (rows.length > 0)
            {
                for (var index in rows)
                {
                    idArr.push(rows[index].id);
                }
            } else
            {
                idArr.push(rows.id);
            }
            var ids = idArr.toString();

            Util.ajax.loadData("../mail/sendMail.html",
                function (data)
                {
                    if (data.success == "false")
                    {
                        showErrorTip(data.msg);
                    } else
                    {
                        reload();
                    }
                }, {id: ids, ran: Math.random()}
            );
        }
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
        {field: 'name', halign: 'center', align: 'left', width: 120, title: '配置名称'},
        {field: 'smtp', halign: 'center', align: 'left', width: 130, title: '发送服务器地址'},
        {field: 'smtpSSL', halign: 'center', align: 'left', width: 100, title: '发送服务器端口'},
        {field: 'pop3', halign: 'center', align: 'left', width: 130, title: '接收服务器地址'},
        {field: 'pop3SSL', halign: 'center', align: 'left', width: 100, title: '接收服务器端口'}
    ]];

    //请求连接
    var url = '../mail/findMailConfigList.html';

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
            checkOnSelect: false,
            selectOnCheck: false,
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
            url: '../mail/toAddPage.html?' + param,
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
     * 弹出发送窗口
     */
    function openSendDialog(title, param, id)
    {
        dialog.openDialog({
            url: '../mail/toSendPage.html?' + param,
            title: title,
            id: id,
            width: '480px',
            height: '350px',
            reloadCallBack: function ()
            {
                reload();
            }
        });
    }


    //初始化方法
    exports.init = function ()
    {
        //添加方法
        $("#addMailConfig").on("click", function ()
        {
            var dialogId = 'mailConfig';
            var param = "ran=" + Math.random();
            var dialogTitle = '添加邮件配置';
            openAddDialog(dialogTitle, param, dialogId);
        });

        $("#deleteMailConfig").on("click", function ()
        {
            var rows = grid.datagrid('getChecked');
            if (rows.length > 0)
            {
                submitDel(rows);
            } else
            {
                var row = grid.datagrid('getSelected');
                if (row != undefined)
                {
                    submitDel(row);
                }
            }
        });

        $("#testSendMailConfig").on("click", function ()
        {
            var idArr = [];
            var rowArr = grid.datagrid('getChecked');
            if (rowArr.length > 0)
            {
                for (var index in rowArr)
                {
                    idArr.push(rowArr[index].id);
                }
            } else
            {
                var row = grid.datagrid('getSelected');
                if (row != undefined)
                    idArr.push(row.id);
            }
            var ids = idArr.toString();
            var dialogId = 'sendMailDlg';
            var param = "ran=" + Math.random() + "&mailConfigId=" + ids + "&sendTo=server";
            var dialogTitle = '发送邮件';
            openSendDialog(dialogTitle, param, dialogId);
        });

        $("#testPageMailConfig").on("click", function ()
        {
            window.showModalDialog("../mail/toOpenDiv.html",window,"dialogWidth:1024px;dialogHeight:1024px;scroll:yes;status:no");
        });
        $("#testNavMailConfig").on("click", function ()
        {
            window.showModalDialog("../mail/toOpenNav.html",window,"dialogWidth:1024px;dialogHeight:1024px;scroll:yes;status:no");
        });
    };
});