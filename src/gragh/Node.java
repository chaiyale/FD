package gragh;

public class Node  //每个实体就是一个节点
{ 
	private int number;
	private String table;
	private boolean visited;
	public Node(int num,String t,boolean v)
	{
		this.number = num;
		this.table = t;
		this.visited = false;
	}
	public void setnumber(int num)
	{
		this.number = num;
	}
	public void settablename(String t)
	{
		this.table = t;
	}
	public void setvisited(boolean v)
	{
		this.visited = v;
	}
	public int getnumber()
	{
		return this.number;
	}
	public String gettablename()
	{
		return this.table;
	}
	public boolean getvisited()
	{
		return this.visited;
	}
}
