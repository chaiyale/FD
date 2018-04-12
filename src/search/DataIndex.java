package search;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javaToHdfs.FsDirectory;

import javax.servlet.http.HttpServletRequest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DataIndex {
	//功能：查找关键字所在的表table
	private Map<String,ArrayList<String>> Sqi = new HashMap<String,ArrayList<String>>();
	private ArrayList<String> FormTerm = new ArrayList<String>();
	
	public boolean execute(HttpServletRequest request)
			throws UnsupportedEncodingException {
		boolean flag = false;
		request.setCharacterEncoding("utf-8");
		String keyword = request.getParameter("keyword");
		if(keyword==null || keyword.length()<=0) //查询的内容为空
			return flag;
		else
		{
			String[] keys = keyword.split(","); //以逗号区分开关键字
			for(String k:keys)
			{
				ArrayList<String> a = LuceneOnhdfs(k);
				ArrayList<String> b = new ArrayList<String>();
				for(String t:a)
				{
					if(!exist(FormTerm,t)) //table不在FormTerm中则加入
					{
						FormTerm.add(t);
					}
					b.add(t);
				}
				Sqi.put(k, b);
				
				if(!exist(FormTerm,k)) //qi不在FormTerm中则加入
				{
					FormTerm.add(k);			
					b.add(k);
					Sqi.put(k,b);
				}
			}
			
			/*Iterator<String> iter = Sqi.keySet().iterator();
			String k;
			while(iter.hasNext())
			{
				k = iter.next();
				ArrayList<String> ar = Sqi.get(k);
				System.out.print("k:" + k +"v:");
				for(String tmp:ar)
					System.out.print(tmp);
			}*/
			
			flag = true;
		}
		return flag;	
	}
	
	public ArrayList<String> getFormTerm()
	{
		return FormTerm;
	}
	
	public Map<String,ArrayList<String>> getSqi() 
	{
		return Sqi;
	}
	
	public boolean exist(ArrayList<String> a, String s) //判断是否已经存在在数组中
	{
		boolean flag = a.contains(s);
		return flag;
	}
	
	public ArrayList<String> LuceneOnhdfs(String s)
	{
		ArrayList<String> r = new ArrayList<String>();
		String indexPath = "hdfs://localhost:9000/user/root/DataIndex";
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			FsDirectory dir = new FsDirectory(fs,new Path(indexPath),false,conf);
			LuceneOnHdfsSearch lohs = new LuceneOnHdfsSearch(dir);
			r = lohs.searchResult(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}
}
