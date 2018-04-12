<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, java.sql.*, java.io.*;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>显示结果</title>
<style type="text/css">
  table{
    border: 2px #CCCCCC solid;
    width: 360px;
  }
  th{
    height: 30px;
    border: 1px #CCCCCC solid;
  }
  td{
    height: 30px;
    border: 1px #CCCCCC solid;
    text-align: center;
  }
</style>
</head>
<body>
    <%
        String table = (String)session.getAttribute("table");
        String[] attrs = (String[])session.getAttribute("attrs");
        String[] ops = (String[])session.getAttribute("ops");
        String[] val = (String[])session.getAttribute("val");
        String[] sqlss = (String[])session.getAttribute("sqlss");
        String[][] sqls = (String[][])session.getAttribute("sqls");
        boolean[] ISNUM = (boolean[])session.getAttribute("ISNUM");
        String[] realops = { "<", "<=", "=", ">", ">=", "!=" };
        
        int attrnum=attrs.length;
        int sqlnum=sqlss.length;
        int i=0;
        String sqlstring="select ";
        String sqlstring2="";
        
        if(sqlnum==1)
        {
        	if(sqlss[0].equals("group by"))
        	{
        		for(i=0;i<attrnum;i++)
            	{
            		String[] picked = request.getParameterValues(sqls[i][0]);
            		if(picked!=null)
            		{
            			sqlstring2 += " group by " + attrs[i];
            			sqlstring += attrs[i];
            			break;
            		}
            	}
        		if(i==attrnum)
        		{
        			sqlstring += "* ";
        		}
        		else
        		{
        			for(i=i+1;i<attrnum;i++)
                	{
                		String[] picked = request.getParameterValues(sqls[i][0]);
                		if(picked!=null)
                		{
                			sqlstring += ", " + attrs[i];
                			sqlstring2 += ", " + attrs[i];
                		}
                	}
        		}
        	}
        	else
        	{
        		for(i=0;i<attrnum;i++)
            	{
            		String[] picked = request.getParameterValues(sqls[i][0]);
            		if(picked!=null)
            		{
            			sqlstring += sqlss[0] + "(" + attrs[i] + ") ";
            			break;
            		}
            	}
        		if(i==attrnum)
        		{
        			sqlstring += "* ";
        		}
        		else
        		{
        			for(i=i+1;i<attrnum;i++)
                	{
                		String[] picked = request.getParameterValues(sqls[i][0]);
                		if(picked!=null)
                		{
                			sqlstring += ", " + sqlss[0] + "(" + attrs[i] + ") ";
                		}
                	}
        		}
        	}
        }
        else if(sqlnum==2)
        {
        	for(i=0;i<attrnum;i++)
        	{
        		String[] picked = request.getParameterValues(sqls[i][0]);
        		if(picked!=null)
        		{
        			sqlstring2 += " group by " + attrs[i];
        			sqlstring += attrs[i];
        			break;
        		}
        	}
    		if(i==attrnum)
    		{
    			for(i=0;i<attrnum;i++)
            	{
            		String[] picked = request.getParameterValues(sqls[i][1]);
            		if(picked!=null)
            		{
            			sqlstring += sqlss[1] + "(" + attrs[i] + ") ";
            			break;
            		}
            	}
        		if(i==attrnum)
        		{
        			sqlstring += "* ";
        		}
        		else
        		{
        			for(i=i+1;i<attrnum;i++)
                	{
                		String[] picked = request.getParameterValues(sqls[i][1]);
                		if(picked!=null)
                		{
                			sqlstring += ", " + sqlss[1] + "(" + attrs[i] + ") ";
                		}
                	}
        		}
    		}
    		else
    		{
    			for(i=i+1;i<attrnum;i++)
            	{
            		String[] picked = request.getParameterValues(sqls[i][0]);
            		if(picked!=null)
            		{
            			sqlstring += ", " + attrs[i];
            			sqlstring2 += ", " + attrs[i];
            		}
            	}
    			for(i=0;i<attrnum;i++)
            	{
            		String[] picked = request.getParameterValues(sqls[i][1]);
            		if(picked!=null)
            		{
            			sqlstring += ", " + sqlss[1] + "(" + attrs[i] + ") ";
            		}
            	}
    		}
        }
        else
        {
        	sqlstring = "* ";
        }
       
        sqlstring += " from " + table;
        
        i=0;
        while( i<attrnum && request.getParameter(val[i])=="" )
        {
        	i++;
        }
        if(i!=attrnum)
        {
        	String op = request.getParameter(ops[i]);
            String realop = realops[Integer.parseInt(op)];
        	String va = request.getParameter(val[i]);
        	if(ISNUM[i] == false)
        	{
        		sqlstring += " where " + attrs[i] + realop + "'" + va + "'";
        	}
        	else
        	{
        		sqlstring += " where " + attrs[i] + realop + va;
        	}
        	for(i=i+1;i<attrnum;i++)
            {
            	op = request.getParameter(ops[i]);
            	realop = realops[Integer.parseInt(op)];
            	va = request.getParameter(val[i]);
            	if(request.getParameter(val[i])!="")
            	{
            		if(ISNUM[i] == false)
                	{
                		sqlstring += " and " + attrs[i] + realop + "'" + va + "'";
                	}
                	else
                	{
                		sqlstring += " and " + attrs[i] + realop + va;
                	}
            	}
            }
        }  	
        else
        {
        	System.out.println("None");
        }
        sqlstring += sqlstring2;    
        System.out.println(sqlstring);
        
        String driverName = "com.mysql.jdbc.Driver";
		String dbURL = "jdbc:mysql://localhost:3306/tpch?characterEncoding=utf-8";
		String userName = "root";
		String userPassWord = "hadoop";
		Class.forName(driverName);
		Connection conn = DriverManager.getConnection(dbURL, userName, userPassWord);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sqlstring);
    %>
    <br>
    <br>
    <table align="center">
    <%
        if(!rs.wasNull())
	    {
		    ResultSetMetaData rsmd = rs.getMetaData();
		    int colcount = rsmd.getColumnCount(); //获得表的属性的总数
		    String[] s = new String[colcount]; 
    %>
    <tr>
    <%
		    for(i=0;i<colcount;i++)
		    {
		    	s[i]= rsmd.getColumnName(i+1);
    %>
                    <th>
                        <%=s[i]%>
                    </th>
    <%
		    }
    %>
    </tr>
    <%
		    while(rs.next())
		    {
	%>
	<tr>
	<%
			     for(i=0;i<colcount;i++)
			     {
				    String t=rs.getString(i+1);
	%>
	                <td>
	                    <%=t%>
	                </td>
	<%
			      }
		      }
	    }
    %>
    </table>
	<%
	     rs.close();
	     st.close();
	     conn.close();
    %>
</body>
</html>