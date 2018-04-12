package index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Field;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javaToHdfs.FsDirectory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class indexFiles {
	
	private indexFiles() {}
	
	  public static String indexPath = "hdfs://localhost:9000/user/root/DataIndex";
	  public static String docsPath = "hdfs://localhost:9000/user/root";
	  
	  /** Index all text files under a directory. */
	  public static void main(String[] args) {
	    
	    Date start = new Date();
	    try {
	      //Directory indexdir = FSDirectory.open(new File(indexPath));
	      
	      Configuration conf = new Configuration();
	      FileSystem fs = FileSystem.get(conf);
	      
	      FsDirectory indexdir = new FsDirectory(fs,new Path(indexPath),false,conf);
	      IndexWriter writer = new IndexWriter(indexdir, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
	      System.out.println("Indexing to directory '" +indexPath+ "'...");
	      
	      FsDirectory docsdir = new FsDirectory(fs, new Path(docsPath),false,conf);
	      
	      indexDocs(writer, docsdir,docsPath);
	      System.out.println("Optimizing...");
	      writer.optimize();
	      writer.close();

	      Date end = new Date();
	      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	    } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	    }
	  }

	  static void indexDocs(IndexWriter writer, FsDirectory dir, String FatherPath)
	    throws IOException {
	    // do not try to index files that cannot be read
		  if(dir.IsDir())
		  {
			  //System.out.println("I am a dir");
			  String[] files = dir.listAll();
			  if(files!=null)
			  {
				  for (int i = 0; i < files.length; i++) {
					  //System.out.println(files[i]);
					  Configuration conf = new Configuration();
				      FileSystem fs = FileSystem.get(conf);
				      String newpath = FatherPath + "/" + files[i];
				      //System.out.println(newpath);
				      FsDirectory dirs = new FsDirectory(fs, new Path(newpath),false,conf);
			          indexDocs(writer, dirs,newpath);
				  }
			  }
		  }
		  else//is a file
		  {
			  //System.out.println("I am a file");
			  String path = dir.getPath().toString();
			  String table = path.replace("hdfs://localhost:9000/user/root/", "");
			  table = table.replace("/part-m-00000", "");
			  if(path.indexOf("part-m-00000")!=-1)
			  {
			      System.out.println("adding " + dir.getPath());
			      try{
				      Document doc = new Document();
				      doc.add(new Field("table", table, Field.Store.YES, Field.Index.NOT_ANALYZED));
				      //doc.add(new Field("dataterm",new BufferedReader(new InputStreamReader(dir.readFile(),StandardCharsets.UTF_8))));
				      BufferedReader data = new BufferedReader(new InputStreamReader(dir.readFile(),StandardCharsets.UTF_8));
				      String mystring;
				      String line="";
				      while((mystring = data.readLine())!= null)
				      {
				    	  String[] words = mystring.split(",");
				    	  for(String w:words)
				    	  {
				    		  line += w+ " ";
				    	  }
				      }
				      System.out.println(line);
				      doc.add(new Field("dataterm", line, Field.Store.YES, Field.Index.ANALYZED));
				      doc.add(new Field("path", path, Field.Store.YES, Field.Index.NOT_ANALYZED));
				      writer.addDocument(doc);
			          }
			          finally{}
			  }
		  }
	     }
}
