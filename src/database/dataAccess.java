package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import database.dataAccessBase;

public class dataAccess {
	
	private dataAccessBase db = new dataAccessBase();
	public dataAccess()
	{
	}
	
	public int getAllTableNum() //获得数据库中表的总数
	{
		String sql = "show tables;";
		ResultSet rs = db.executeQuery(sql);
		int sum=0;
		try {
			while(rs.next())
			{
				sum++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("s="+sum);
		return sum;
	}
	
	public String[] getAllTableName2() //获得数据库中表的名称
	{
		int j = getAllTableNum();
	    String s[] = new String[j];
		String sql = "show tables;";
	    //System.out.println(sql);
	    ResultSet rs = db.executeQuery(sql);
	    int i=0;
	    try {
			while(rs.next())
			{
				s[i] = rs.getString(1);
				//System.out.println(s[i]);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public String[] getAllAttribute(String tablename)//获得表的属性的名称
	{
		String sql = "select * from "+ tablename;
	    //System.out.println(sql);
	    ResultSet rs = db.executeQuery(sql);
	    ResultSetMetaData rsmd = null;
	    int colcount = 0;
	    try {
			rsmd = rs.getMetaData();
			colcount = rsmd.getColumnCount(); //获得表的属性的总数
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] s = new String[colcount]; 
		for(int i=0;i<colcount;i++)
		{
			try {
				s[i]= rsmd.getColumnName(i+1);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    return s;
	}
	
	public boolean IsNum(String tablename,String attrname) //判断字段的数据类型是否为数值型的
	{
		String sql = "select " +attrname+ " from "+ tablename;
	    ResultSet rs = db.executeQuery(sql);
	    ResultSetMetaData rsmd = null;
	    boolean s = false;
	    int colcount = 0;
	    try {
			rsmd = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
				String ss = rsmd.getColumnTypeName(1);
				if(ss=="INT" || ss=="BIT" || ss=="DEC" || ss=="DOUBLE" || ss=="FLOAT" || ss=="NUMERIC" || ss=="DECIMAL")
				{
					s=true;
				}
				else
				{
					s=false;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    return s;
	}
	
	public String[] getAllFK(String tablename) //获得数据库中表的外键约束
	{
		String sql1= "select count(*) from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where REFERENCED_TABLE_NAME='"
	                 + tablename + "';";
		ResultSet rs1 = db.executeQuery(sql1);
		String rownum = null;
		try {
			while(rs1.next())
			{
				rownum = rs1.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql = "select TABLE_NAME,COLUMN_NAME,REFERENCED_COLUMN_NAME from INFORMATION_SCHEMA.KEY_COLUMN_USAGE where REFERENCED_TABLE_NAME='"
			         + tablename + "';";
		ResultSet rs = db.executeQuery(sql);
		int w = Integer.parseInt(rownum);
		//System.out.println(w);
		String s[] = new String[w*3];
	    int i=0;
	    try {
			while(rs.next())
			{
				s[i++] = rs.getString(1);
				s[i++] = rs.getString(2);
				s[i++] = rs.getString(3);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;		
	}
	
	public int AbsoluteCardinality(String tablename) //实体的绝对基数
	{
		String sql = "select count(*) from " + tablename;
		ResultSet rs = db.executeQuery(sql);
		String sum;
		int w = 0;
		try {
			while(rs.next())
			{
				sum = rs.getString(1);
				w = Integer.parseInt(sum);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("s="+sum);
		return w;
	}
	
	public double AttributeNecessity(String attrname,String tablename,int AC) //计算属性在实体中的必要性：非空属性实例个数/实体的绝对基数
	{
		String sql = "select " + attrname +" from "+ tablename;
	    ResultSet rs = db.executeQuery(sql);
	    int sum = 0;
		try {
			while(rs.next())
			{
				String s = rs.getString(1);
				if(s!=null)
					sum++;
				if(s.length()>=20) //长度大于15个字符直接排除
					return 0;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double NA = sum*1.0/AC;
		return NA;
	}
	
	public double AttributeRange(String attrname,String tablename,int AC) //计算属性的可选择性，Range范围（属性在数据库中非重复个数）/实体的绝对基数
	{
		String sql = "select distinct " + attrname +" from "+ tablename;
	    ResultSet rs = db.executeQuery(sql);
	    int sum = 0;
		try {
			while(rs.next())
			{
				String s = rs.getString(1);
				sum++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double AR = sum*1.0/AC;
		return AR;
	}
	
	public void close() { 
		db.close();
	}

}
