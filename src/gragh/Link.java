package gragh;

public class Link  //每个关系就是一条边
{
	private int number;
	private int fromTableNum;
	private int ToTableNum;
	private String tableFK;
	public Link(int num,int fromNum,int ToNum,String FK)
	{
		this.number = num;
		this.fromTableNum = fromNum;
		this.ToTableNum = ToNum;
		this.tableFK = FK;
	}
	public int getnumber()
	{
		return this.number;
	}
	public int getfromtableNum()
	{
		return this.fromTableNum;
	}
	public int getToTableNum()
	{
		return this.ToTableNum;
	}
	public String gettableFK()
	{
		return this.tableFK;
	}
}
