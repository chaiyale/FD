package search;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import javaToHdfs.FsDirectory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class LuceneOnHdfsSearch {
	
	public IndexReader myreader;
	public IndexSearcher mysearcher;
	public LuceneOnHdfsSearch(FsDirectory indexDir)throws CorruptIndexException,IOException
	{
		myreader = IndexReader.open(indexDir);
		mysearcher = new IndexSearcher(myreader);
	}
	public ArrayList<String> searchResult(String key)throws CorruptIndexException,IOException
	{
		ArrayList<String> result = new ArrayList<String>();		
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "dataterm", analyzer);
		//Term term = new Term("dataterm",key);
		//Query query = new TermQuery(term);
		System.out.println("Searching for: " + key);
		Query query;
		try {
			query = parser.parse(key);
			TopDocs hits = mysearcher.search(query,10);
			for(ScoreDoc scoreDoc:hits.scoreDocs)
			{
			    Document doc = mysearcher.doc(scoreDoc.doc);
			    result.add(doc.get("table"));
			    System.out.println(doc.get("table"));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public void close() throws IOException
	{
		// 关闭对象
		mysearcher.close();
		myreader.close();
	}
        /*public static void main(String[] args)
        {
                String indexPath = "hdfs://localhost:9000/user/root/DataIndex";
		         Configuration conf = new Configuration();
		         FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			FsDirectory dir = new FsDirectory(fs,new Path(indexPath),false,conf);
			LuceneOnHdfsSearch lohs = new LuceneOnHdfsSearch(dir);
			String[] searchword = {"0","ALGERIA","haggle","haggle.","carefully",
					"1","ARGENTINA","al","foxes","a","are","the"};
			for(int i=0;i<searchword.length;i++)
			{
				ArrayList<String> r = lohs.searchResult(searchword[i]);
			}
			lohs.close();
			//ArrayList<String> r = lohs.searchResult("199901");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }*/

}
