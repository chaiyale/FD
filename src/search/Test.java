package search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.document.Document;
import javaToHdfs.FsDirectory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Test {
	
	public IndexReader myreader;
	public IndexSearcher mysearcher;
	public Test(FsDirectory indexDir)throws CorruptIndexException,IOException
	{
		myreader = IndexReader.open(indexDir);
		mysearcher = new IndexSearcher(myreader);
	}
	public String[] searchResult(String key)throws CorruptIndexException,IOException
	{
		String[] result = new String[10];
		Term term = new Term("content",key);
		Query query = new TermQuery(term);
		
		TopDocs hits = mysearcher.search(query,10);
		int i=0;
		for(ScoreDoc scoreDoc:hits.scoreDocs)
		{
		    Document doc = mysearcher.doc(scoreDoc.doc);
		    result[i] = doc.get("attr");
		    System.out.println(result[i]);
		    i++;
		}
		return result;
	}
	public void close() throws IOException
	{
		// 关闭对象
		mysearcher.close();
		myreader.close();
	}
        public static void main(String[] args)
        {
         String indexPath = "hdfs://localhost:9000/user/root/FormIndex";
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			FsDirectory dir = new FsDirectory(fs,new Path(indexPath),false,conf);
			Test lohs = new Test(dir);
                        String[] r = new String[10];
                        r = lohs.searchResult("student");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }

}
