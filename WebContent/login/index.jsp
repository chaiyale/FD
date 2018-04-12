<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>表单检索系统</title>
</head>
<body>
<h1 align="center">表单检索系统</h1>
<form action="http://localhost:8081/FD/searchFiles" method="post">
<br>
<p align="center">请输入要检索的数据库内容的关键词：</p>
<br>
<p align="center">
				<input type="text" name="keyword">
				<input type="submit" value="提  交">
</p>
<br>
	</form>
</body>
</html>