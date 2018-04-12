package index;

import gragh.makeGragh;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import database.dataAccess;

import javaToHdfs.FsDirectory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Field;

public class MakeForm {
	
	private static Map<String,String[]> FKmap = new HashMap<String,String[]>();
	private static Map<String,String> Attrmap = new HashMap<String,String>();
	private static Map<String,String[]> OperationQuery = new HashMap<String,String[]>();
	private static Map<String,String> Graghmap = new HashMap<String,String>();
	
	public static void Form()
	{
		dataAccess db = new dataAccess();
		String table[] = db.getAllTableName2(); //获得数据库中表的名称	
		for(String t:table)
		{
			int AC = db.AbsoluteCardinality(t); //实体的绝对基数
			
			String[] attr = db.getAllAttribute(t);//获得表的属性的名称
			String attrtxt = "";
			for(int i=0;i<attr.length;i++)
			{
				String[] Query = new String[2];
				double NA = db.AttributeNecessity(attr[i], t, AC);
				double AR = db.AttributeRange(attr[i], t, AC);
				double QA_S = NA*AR; //基于选择操作的属性的查询意向度
				Query[0] = QA_S+"";
				int NR;
				if(db.IsNum(t, attr[i])==true && AR<1)
					NR = 1;
				else
					NR = 0;
				double QA_A = NA*NR;  //基于聚合操作的属性的查询意向度
				double QA_AS = 0.5*QA_S + 0.5*QA_A;
				Query[1] = QA_AS+"";
				OperationQuery.put(attr[i], Query);
			}
			for(int i=0;i<attr.length-1;i++)
			{
				attrtxt += attr[i] + ",";
			}
			attrtxt += attr[attr.length-1];
			Attrmap.put(t,attrtxt);
			
			String FK[] = db.getAllFK(t); //获得数据库中表的名称
			FKmap.put(t, FK);
		}
		Graghmap = makeGragh.Graph(table, FKmap);
		db.close();
	}
	
	public static void indexDocs(IndexWriter writer,Map<String,String> Attrmap,Map<String,String> Graphmap) 
	throws CorruptIndexException, IOException
	{
		String[] sql = {"","avg","count","max","min","sum","group by","group by,avg",
				"group by,count","group by,max","group by,min","group by,sum"};
		
		//首先完成单表的表单
		Iterator<String> iter1 = Attrmap.keySet().iterator();
		String k,v,allv;
		int e,j=0;
		while(iter1.hasNext())
		{
			k = iter1.next();
			v = Attrmap.get(k);
			allv = v;
			String[] attr = v.split(",");
			
			if(attr.length > 6) //表单上显示的属性数目不应大于5个
			{
				//只进行选择操作
				for(int i=0;i<attr.length-1;i++) //利用冒泡排序将分数按照Q_S排序
				{
					for(int w=0;w<attr.length-1-i;w++)
					{
						String[] Query1 = OperationQuery.get(attr[w]);
						String[] Query2 = OperationQuery.get(attr[w+1]);
						double s1 = Double.parseDouble(Query1[0]);
						double s2 = Double.parseDouble(Query2[0]);
						if(s1<s2)
						{
							String temp = attr[w];
							attr[w] = attr[w+1];
							attr[w+1] = temp;
						}
					}
				}
				v = "";
				for(int i=0;i<5;i++)
				{
					//System.out.print(attr[i]+" ");
					//String[] Query = OperationQuery.get(attr[i]);
					//System.out.println(Query[0]);
					v += attr[i] + ",";
				}
				v += attr[5];
			}			
			
			for(int i=0;i<sql.length;i++)
			{
				String all = k+","+allv+","+sql[i];
				String con = k+","+v+","+sql[i];
				e = 12*j+(i+1);
				String formID = "form_" + e;
				String descriptionA = "show " + v + " from " + k ;
				String descriptionB = "use '" + sql[i] + "' when do searching in the database";
				try {
					Document doc = new Document();
					doc.add(new Field("formID",formID, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("descriptionA",descriptionA, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("descriptionB",descriptionB, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("table", k, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("attr", v, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("sql", sql[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("content", con, Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("type", "1", Field.Store.YES, Field.Index.NOT_ANALYZED));
					System.out.print("formID:"+formID+" ");
					System.out.print("content:"+con);
					System.out.print("type:"+ "1");
					System.out.println("");
					writer.addDocument(doc);
				} finally{}
			}
			j++;
		}
		
		//然后完成多表的表单
		Iterator<String> iter2 = Graphmap.keySet().iterator();
		while(iter2.hasNext())
		{
			String alla="",attrs="",table="",table_FK;
			k = iter2.next();
			table_FK = Graphmap.get(k);
			String[] tables = k.split(",");
			for(int i=0;i<tables.length-1;i++)
			{
				table += tables[i]+",";
				String at = Attrmap.get(tables[i]);
				attrs += at+","; 
			}
			table += tables[tables.length-1];
			attrs += Attrmap.get(tables[tables.length-1]);
			alla = attrs;
			String[] attr = attrs.split(",");
			//System.out.println("************************************************");
			//System.out.println("table: "+table);
			//System.out.println("attr: "+attrs);
			
			if(attr.length > 6) //表单上显示的属性数目不应大于5个
			{
				//只进行选择操作
				for(int i=0;i<attr.length-1;i++) //利用冒泡排序将分数按照Q_S排序
				{
					for(int w=0;w<attr.length-1-i;w++)
					{
						String[] Query1 = OperationQuery.get(attr[w]);
						String[] Query2 = OperationQuery.get(attr[w+1]);
						double s1 = Double.parseDouble(Query1[0]);
						double s2 = Double.parseDouble(Query2[0]);
						if(s1<s2)
						{
							String temp = attr[w];
							attr[w] = attr[w+1];
							attr[w+1] = temp;
						}
					}
				}
				attrs = "";
				for(int i=0;i<5;i++)
				{
					//System.out.print(attr[i]+" ");
					//String[] Query = OperationQuery.get(attr[i]);
					//System.out.println(Query[0]);
					attrs += attr[i] + ",";
				}
				attrs += attr[5];
			}
			
			for(int i=0;i<sql.length;i++)
			{
				e = 12*j+(i+1);
				String formID = "form_" + e;
				String con = table + "," + attrs + "," + sql[i];
				String all = table + "," + alla + "," + sql[i];
				String descriptionA = "show " + attrs + " ...from " + table;
				String descriptionB = "use '" + sql[i] + "' when do searching in the database";
				try {
					Document doc = new Document();
					doc.add(new Field("formID",formID, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("descriptionA",descriptionA, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("descriptionB",descriptionB, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("table",table, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("attr", attrs, Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("sql", sql[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("content", con, Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED));
					doc.add(new Field("type", "2", Field.Store.YES, Field.Index.NOT_ANALYZED));
					doc.add(new Field("FK", table_FK , Field.Store.YES, Field.Index.NOT_ANALYZED));
					System.out.print("formID:"+formID+" ");
					System.out.print("content:"+con);
					System.out.print("type:"+ "2");
					System.out.println("");
					writer.addDocument(doc);
				} finally{}
			}
			j++;
		}
	}
	
	public static void main(String[] args)
	{
		Date start = new Date();
		String indexPath = "hdfs://localhost:9000/user/root/FormIndex";
		try {
			Configuration conf = new Configuration();
		    FileSystem fs = FileSystem.get(conf);
		    
		    FsDirectory indexdir = new FsDirectory(fs,new Path(indexPath),false,conf);
		    IndexWriter writer = new IndexWriter(indexdir, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
		    System.out.println("Indexing to directory '" +indexPath+ "'...");
		    
		    Form();
		    indexDocs(writer, Attrmap,Graghmap);
		    writer.optimize();
		    writer.close();
            Date end = new Date();
		    System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
		      System.out.println(" caught a " + e.getClass() +
		   	       "\n with message: " + e.getMessage());
		 }
	}

}
