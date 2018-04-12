<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*, org.apache.lucene.document.Document;"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">  
a { text-decoration:none; }  
a, a:visited { color:#000; background:inherit; }  
body { margin:0; padding:20px; font:12px tahoma, 宋体, sans-serif; }  
dt { font-size:22px; font-weight:bold; margin:0 0 0 15px; }  
dd { margin:0 0 0 15px; }  
h4 { margin:0; padding:0; font-size:18px; text-align:center; }  
p { margin:0; padding:0 0 0 18px; }  
p a, p a:visited { color:#00f; background:inherit; }  

.CNLTreeMenu img.s { cursor:pointer; vertical-align:middle; }  
.CNLTreeMenu ul { padding:0; }  
.CNLTreeMenu li { list-style:none; padding:0; }  
.Closed ul { display:none; }  
.Child img.s { background:none; cursor:default; }  
#CNLTreeMenu1 ul { margin:0 0 0 17px; }  
#CNLTreeMenu1 img.s { width:20px; height:15px; }  
#CNLTreeMenu1 .Opened img.s { background:url(http://www.zzsky.cn/effect/images/treemenu/opened1.gif) no-repeat 0 0; }  
#CNLTreeMenu1 .Closed img.s { background:url(http://www.zzsky.cn/effect/images/treemenu/closed1.gif) no-repeat 0 0; }  
#CNLTreeMenu1 .Child img.s { background:url(http://www.zzsky.cn/effect/images/treemenu/child1.gif) no-repeat 3px 5px; }  
/*CNLTreeMenu End*/   
/*Temp CSS for View Demo*/   
#CNLTreeMenu1 { float:left;}  
#CNLTreeMenu1 { padding-bottom:15px; }  
.ViewCode { clear:both; border:1px solid #FFB900; background:#FFFFCC; color:inherit; margin:3px; padding:3px; }  
.ViewCode h6 { color:#00f; }  
</style>  
<title>候选表单</title>
</head>
<body>
<h1 align="center">候选表单</h1>
<br>
<p align="center">请从以下候选表单中选择一项：</p>
<table>
    <% 
        ArrayList<Document> docs = (ArrayList<Document>)request.getAttribute("Docs");
        Iterator<Document> iter2 = docs.iterator();
        Document[][] tree = new Document[docs.size()][docs.size()];
        boolean first = true;
        int treenum1=0;
        int[] treenum2 = new int[docs.size()];
        for(int i=0;i<docs.size();i++)
        	treenum2[i]=0;
        String formID = "", descriptionB="", table="", attr="", sql="", type="", FK="";
	    while(iter2.hasNext())
	    {
		    Document dd = iter2.next();
		    table=dd.get("table");
		    boolean bo = false;
		    
		    if(first==false)
		    {
		    	for(int i=0;i<=treenum1;i++)
		    	{
		    		for(int j=0;j<=treenum2[treenum1];j++)
		    		{
		    			if(table.equals(tree[i][j].get("table")))
		    			{
		    				treenum2[treenum1]++;
		    				tree[treenum1][treenum2[treenum1]] = dd;
		    				bo = true;
		    				break;
		    			}
		    		}
		    	}
		    	if(bo==false)
		    	{
		    		treenum1++;
    				tree[treenum1][treenum2[treenum1]] = dd;
		    	}
		    }
		    else
		    {
		    	tree[treenum1][treenum2[treenum1]] = dd; 
		    	first = false;
		    }
	    }
	    String[] ptree = new String[treenum1+1];
	    for(int i=0;i<=treenum1;i++)
    	{
    		ptree[i] = tree[i][0].get("descriptionA");
	    	//System.out.println(i+" : "+tree[i][0].get("descriptionA"));
	    	for(int j=0;j<=treenum2[i];j++)
    		{
	    		System.out.println(tree[i][j].get("descriptionB"));
    		}
	    	//System.out.println("*************************************");
    	}
    %>
</table>
<br>
<div class="CNLTreeMenu" id="CNLTreeMenu1">  
  <h4>请从以下候选表单中选择一项：</h4>  
  <p><a id="AllOpen_1" href="#" onClick="MyCNLTreeMenu1.SetNodes(0);Hd(this);Sw('AllClose_1');">全部展开</a><a id="AllClose_1" href="#" onClick="MyCNLTreeMenu1.SetNodes(1);Hd(this);Sw('AllOpen_1');" style="display:none;">全部折叠</a></p>  
  <ul>  
    <li class="Opened">ALL 
      <ul>  
<%
          for(int i=0;i<=treenum1;i++)
          {
%>
          <li><a href="#"><%=ptree[i]%></a>  
              <ul>  
<%
                for(int j=0;j<=treenum2[i];j++)
                {
                	Document d = tree[i][j];
                	table=d.get("table");
        	    	attr=d.get("attr");
        	    	sql=d.get("sql");
        		    formID = d.get("formID");
        		    type = d.get("type");
        		    FK = d.get("FK");
        		    descriptionB = d.get("descriptionB");
%>
                <li class="Child">
<%
                   if(type.equals("1"))
                   {
%>
                        <a href="http://localhost:8081/FD/search/form.jsp?table=<%=table%>
		                 &attr=<%=attr%>&sql=<%=sql%>"><%=descriptionB%></a></li>  
<%                 }
		           else
		           {
%>
		                 <a href="http://localhost:8081/FD/search/form2.jsp?table=<%=table%>
		                 &attr=<%=attr%>&sql=<%=sql%>&FK=<%=FK%>"><%=descriptionB%></a></li>
<%
		           }
                }
%>
              </ul>  
            </li>  
<%
          }
%>
      </ul>  
    </li>  
    <!--Sub Node 1 -->  
  </ul>  
</div>  
<script type="text/javascript">  
    <!--  
    function Ob(o){  
        var o = document.getElementById(o) ? document.getElementById(o) : o;  
        return o;  
    }  
      
    function Hd(o){  
        Ob(o).style.display = "none";  
    }  
      
    function Sw(o){  
        Ob(o).style.display = "";  
    }  
      
    function ExCls(o, a, b, n){  
        var o = Ob(o);  
        for (i = 0; i < n; i++) {  
            oo = o.parentNode;  
        }  
        oo.className = o.className == a ? b : a;  
    }  
      
    function CNLTreeMenu(id, TagName0){  
        this.id = id;  
        this.TagName0 = TagName0 == "" ? "li" : TagName0;  
        this.AllNodes = Ob(this.id).getElementsByTagName(TagName0);  
        this.InitCss = function(ClassName0, ClassName1, ClassName2, ImgUrl){  
            this.ClassName0 = ClassName0;  
            this.ClassName1 = ClassName1;  
            this.ClassName2 = ClassName2;  
            this.ImgUrl = ImgUrl || "http://www.zzsky.cn/effect/images/treemenu/s.gif";  
            this.ImgBlankA = "<img src=\"" + this.ImgUrl + "\" class=\"s\" onclick=\"ExCls(this,'" + ClassName0 + "','" + ClassName1 + "',1);\" alt=\"展开/折叠\" />";  
            this.ImgBlankB = "<img src=\"" + this.ImgUrl + "\" class=\"s\" />";  
            for (i = 0; i < this.AllNodes.length; i++) {  
                this.AllNodes[i].className == "" ? this.AllNodes[i].className = ClassName1 : "";  
                this.AllNodes[i].innerHTML = (this.AllNodes[i].className == ClassName2 ? this.ImgBlankB : this.ImgBlankA) + this.AllNodes[i].innerHTML;  
            }  
        }  
        this.SetNodes = function(n){  
            var sClsName = n == 0 ? this.ClassName0 : this.ClassName1;  
            for (i = 0; i < this.AllNodes.length; i++) {  
                this.AllNodes[i].className == this.ClassName2 ? "" : this.AllNodes[i].className = sClsName;  
            }  
        }  
    }  
      
    var MyCNLTreeMenu1 = new CNLTreeMenu("CNLTreeMenu1", "li");  
    MyCNLTreeMenu1.InitCss("Opened", "Closed", "Child", "http://www.zzsky.cn/effect/images/treemenu/s.gif");  
    -->  
</script>  
</body>
</html>