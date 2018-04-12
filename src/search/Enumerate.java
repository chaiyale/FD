package search;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

public class Enumerate {
	
	private ArrayList<String[]> Sq = new ArrayList<String[]>();	
	
	public Enumerate(Map<String,ArrayList<String>> Sqi) 
	{		
		String[] q = new String[Sqi.size()];
		Iterator<String> iter = Sqi.keySet().iterator();
		int i=0;
		String[] keys = new String[Sqi.size()];
		while(iter.hasNext())
		{
			String k = iter.next();
            keys[i] = k;
            i++;
		}
		ArrayList<String> arr1 = Sqi.get(keys[0]);
		Order(Sqi,arr1,keys,q);
	}
	
	public void Order(Map<String,ArrayList<String>> list,ArrayList<String> arr,String[] keys,String[] str)
	{
		for(int i=0;i<list.size();i++)
		{
			if(arr.equals(list.get(keys[i])))
			{
				for(String st:arr)
				{
					str[i]=st;
					if(i<list.size()-1)
					{
						Order(list,list.get(keys[i+1]),keys,str);
					}
					else if(i==list.size()-1)
					{
						String[] ss= new String[str.length]; 
						for(int j=0;j<str.length;j++)
						{
							ss[j] = str[j];
						}
						Sq.add(ss);
					}
				}
			}
		}
	}
	
	public ArrayList<String[]> getSq()
	{
		/*Iterator<String[]> iter = Sq.iterator();
		while(iter.hasNext())
		{
			String[] k = iter.next();
			for(String tmp:k)
				System.out.print(tmp);
			System.out.println("");
		}*/
		return Sq;
	}
	
	/*public static void main(String[] args)
	{
		Map<String,ArrayList<String>> Sqi = new HashMap<String,ArrayList<String>>();
		ArrayList<String> arr1 = new ArrayList<String>();
		arr1.add("1");
		arr1.add("2");
		arr1.add("a");
		ArrayList<String> arr2 = new ArrayList<String>();
		arr2.add("a");
		arr2.add("b");
		arr2.add("1");
		ArrayList<String> arr3 = new ArrayList<String>();
		arr3.add("A");
		arr3.add("B");
		String[] keys = {"one","two","three"};
		Sqi.put(keys[0], arr1);
		Sqi.put(keys[1], arr2);
		Sqi.put(keys[2], arr3);
		Enumerate en = new Enumerate(Sqi);
		en.getSq();
	}*/

}
