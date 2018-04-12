<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, org.apache.lucene.document.Document, index.IsNum;"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="<%=basePath%>/js/makeSQL.js" type="text/javascript"></script>
<script src="<%=basePath%>/js/jquery-1.11.2.min.js" type="text/javascript"></script>
<title>填写表单</title>
</head>
<body>
<h1 align="center">填写表单</h1>
<form action="http://localhost:8081/FD/search/result.jsp" method="post">
<br>
<p align="center">请将下面的表单填好，如果不清楚的可以不填：</p>
     <% 
         String table = request.getParameter("table");
         String attr = request.getParameter("attr");
         String sql = request.getParameter("sql");
         
         String[] attrs= attr.split(",");
         int attrnum=attrs.length;
         String[] ops=new String[attrnum];
         String[] val=new String[attrnum];
         for(int j=0;j<attrnum;j++)
         {
        	 ops[j] = "op" + j;
        	 val[j] = "val" + j;
         }
         
         String[] sqlss= sql.split(",");
         int sqlnum=sqlss.length;
         String[][] sqls = new String[attrnum][sqlnum];
         for(int i=0;i<attrnum;i++)
         {
        	 for(int j=0;j<sqlnum;j++)
        	 {
        		 sqls[i][j] = "sqls" + i + j;
        	 }
         }
      
         boolean[] ISNUM = new boolean[attrs.length];
         for(int i=0;i<attrs.length;i++)
         {
        	 ISNUM[i] = IsNum.Isnum(table,attrs[i]);
         }
         String[] SQL = {"avg","count","max","min","sum"};
         
         session.setAttribute("table",table);
         session.setAttribute("attrs",attrs);
         session.setAttribute("ops",ops);
         session.setAttribute("val",val);
         session.setAttribute("sqlss",sqlss);
         session.setAttribute("sqls",sqls);
         session.setAttribute("ISNUM",ISNUM);
     %>
     <div border="3" margin=120px align="center">
        <p><%=table%></p>
     <%
         for(int i=0;i<attrnum;i++)
         {
     %>
        	<p><%=attrs[i]%>
        	<select name='<%=ops[i]%>'>
        	        <option value="0">&lt</option> 
        	        <option value="1">&lt=</option> 
        	        <option value="2" selected="selected">=</option>   
        	        <option value="3">&gt</option> 
        	        <option value="4">&gt=</option> 
        	        <option value="5">!=</option> 
        	 </select>
        	    <input type="text" name='<%=val[i]%>'>
      <%
                for(int j=0;j<sqlnum;j++)
                {
                	boolean NeedNum = false;
                	for(String s:SQL)
                	{
                		if(s.equals(sqlss[j]))
                		{
                			NeedNum = true;
                		}
                	}
                	if(NeedNum==true && ISNUM[i]==false)
                	{
      %>
                		<%=sqlss[j]%><input type="checkbox" name='<%=sqls[i][j]%>' value="1" disabled="disabled">
      <%
                	}
                	else
                	{
      %>
                		<%=sqlss[j]%><input type="checkbox" name='<%=sqls[i][j]%>' value="1">
      <%
                	}
                }
      %>
        	</p> 
      <%
         } 
      %>
      <input type="submit" value="提  交">
      </div>
 </form>
</body>
</html>