package index;

import database.dataAccess;


public class IsNum {
	
	public static boolean Isnum(String tablename,String attrname)
	{
		dataAccess db = new dataAccess();
		boolean attr = db.IsNum(tablename,attrname);
		return attr;
	}
	/*public static String[] whatt(String tablename)
	{
		dataAccess db = new dataAccess();
		String[] attr = db.what(tablename);
		return attr;
	}
	public static void main(String[] args)
	{
		String[] ISNUM = IsNum.whatt("lineitem");
		for(String b:ISNUM)
			System.out.println(b);
		boolean[] ISNUM2 = IsNum.Isnum("nation,region");
		for(boolean bb:ISNUM2)
			System.out.println(bb);
	}*/

}
