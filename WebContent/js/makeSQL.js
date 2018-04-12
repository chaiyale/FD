function makesql()
 {
	 var myselect=document.getElementById('<%=ops[i]%>');
	 var myindex=window.document.getElementById('<%=ops[i]%>').selectedIndex;
	 var v=myselect.options[myindex].value;

	 $("p#demo").append("append: "+ v);
 }