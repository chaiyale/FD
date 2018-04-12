package search;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.document.Document;
import javaToHdfs.FsDirectory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FormIndex {
	
	public ArrayList<Document> getDocs(ArrayList<String[]> Sq)
	{
		ArrayList<Document> docs= new ArrayList<Document>();
		String indexPath = "hdfs://localhost:9000/user/root/FormIndex";
		Configuration conf = new Configuration();
		FileSystem fs;
		try {
			fs = FileSystem.get(conf);
			FsDirectory dir = new FsDirectory(fs,new Path(indexPath),false,conf);
			LuceneOnHdfsSearch2 lohs = new LuceneOnHdfsSearch2(dir);
			docs =lohs.searchResult(Sq);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docs;
	}
}
