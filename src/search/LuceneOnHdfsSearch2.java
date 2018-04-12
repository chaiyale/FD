package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import javaToHdfs.FsDirectory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class LuceneOnHdfsSearch2 {
	
	public IndexReader myreader;
	public IndexSearcher mysearcher;
	public LuceneOnHdfsSearch2(FsDirectory indexDir)throws CorruptIndexException,IOException
	{
		myreader = IndexReader.open(indexDir);
		mysearcher = new IndexSearcher(myreader);
	}
	public ArrayList<Document> searchResult(ArrayList<String[]> Sq)throws CorruptIndexException,IOException
	{
		ArrayList<Document> docs= new ArrayList<Document>();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "all", analyzer);
		BooleanQuery bq = new BooleanQuery();
		Iterator<String[]> iter = Sq.iterator();
		while(iter.hasNext())
		{
			String[] s = iter.next();
			System.out.print("Now we search: ");
			BooleanQuery query = new BooleanQuery();
			for(String tmp:s)
			{
				System.out.print(tmp + " ");
				//Term term = new Term("content",tmp);
				//TermQuery q = new TermQuery(term);
				Query q;
				try {
					q = parser.parse(tmp);
					query.add(q, BooleanClause.Occur.MUST);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("");
			bq.add(query, BooleanClause.Occur.SHOULD); //所有查询做AND查询，结果取并集
		}
		TopDocs hits = mysearcher.search(bq,50);
		for(ScoreDoc scoreDoc:hits.scoreDocs)
		{
		    Document doc = mysearcher.doc(scoreDoc.doc);
		    docs.add(doc);
		}
		return docs;
	}
	public void close() throws IOException
	{
		// 关闭对象
		mysearcher.close();
		myreader.close();
	}
	
    /*public static void main(String[] args)
    {
    	Map<String,ArrayList<String>> Sqi = new HashMap<String,ArrayList<String>>();
		ArrayList<String> arr1 = new ArrayList<String>();
		arr1.add("region");
		String[] keys = {"ASIA"};
		Sqi.put(keys[0], arr1);
    	Enumerate en = new Enumerate(Sqi);
		ArrayList<String[]> Sq = en.getSq();
		
    	String indexPath = "hdfs://localhost:9000/user/root/FormIndex";
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			FsDirectory dir = new FsDirectory(fs,new Path(indexPath),false,conf);
			LuceneOnHdfsSearch2 lohs = new LuceneOnHdfsSearch2(dir);
			ArrayList<Document> d =lohs.searchResult(Sq);
			Iterator<Document> iter2 = d.iterator();
			while(iter2.hasNext())
			{
				Document dd = iter2.next();
				System.out.print(dd.get("formID")+"  ");
				System.out.println(dd.get("content"));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
