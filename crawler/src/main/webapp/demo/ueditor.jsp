<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ECharts · Demo</title>

    <script type="text/javascript" charset="utf-8" src="../static/public/ueditor/ueditor.config.js"></script>
 	<script type="text/javascript" charset="utf-8" src="../static/public/ueditor/editor_api.js"></script>
  	<script type="text/javascript" charset="utf-8" src="../static/public/ueditor/lang/zh-cn/zh-cn.js"></script>

</head>

<body>
    <!-- 加载编辑器的容器 -->
    <script id="container" name="content" type="text/plain">
       	 这里写你的初始化内容
    </script>
    <!-- 实例化编辑器 -->
    <script type="text/javascript">
        var ue = UE.getEditor('container');
    </script>
</body>
</html>
