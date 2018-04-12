package index;

import hadoopWithLucene.HDFSDocument;
import hadoopWithLucene.HDFSDocumentOutPut;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javaToHdfs.FsDirectory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.FileSplit;

public class IndexFiles2 {
	
	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
	{
		public void map(LongWritable key, Text value,
				OutputCollector<Text, Text> collector, Reporter reporter)
		throws IOException 
		{
			FileSplit filesplit = (FileSplit)reporter.getInputSplit();
			String filename=filesplit.getPath().getParent().getName();
			collector.collect(new Text(filename), value);
		}
		
	}
	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text,HDFSDocument>
	{
		public void reduce(Text key, Iterator<Text> values,
				OutputCollector<Text,HDFSDocument> collector, Reporter reporter)
				throws IOException 
				{
			        while (values.hasNext())
			        {      
				         HashMap<String,String> fields = new HashMap<String, String>();
				         String k = key.toString();
				         String v = values.next().toString();
				         fields.put(k,v);

				         System.out.println("key: "+ k);
				         System.out.println("value: "+v);

				         HDFSDocument doc = new HDFSDocument();
				         doc.setFields(fields);
				         collector.collect(key, doc);           
				    }
				}
		
	}
	public static void main(String[] args) throws Exception {
		//public void run() throws Exception{
		String indexPath = "hdfs://localhost:9000/user/root/DataIndex";
		String docsPath = "hdfs://localhost:9000/user/root";

		Configuration conf = new Configuration();
		conf.set("charset", "utf-8");
		
		FileSystem fs = FileSystem.get(conf);
		JobConf job = new JobConf(conf, IndexFiles2.class);
		job.setJarByClass(IndexFiles2.class);
		job.setJobName("ProblemIndexer");
		
		FsDirectory docsdir = new FsDirectory(fs, new Path(docsPath),false,conf);
		String[] files = docsdir.listAll(); //将文件夹底下的所有文件进行列举
		for (int i = 0; i < files.length; i++) {
		      String newpath = docsPath + "/" + files[i] + "/part-m-00000";
		      System.out.println(newpath);
		      FileInputFormat.addInputPath(job, new Path(newpath));
		  }
		
		Path outpath= new Path(indexPath);
		if(fs.exists(outpath))
		    fs.delete(outpath, true);
		FileOutputFormat.setOutputPath(job, outpath);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);    
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(HDFSDocument.class);
		job.setOutputFormat(HDFSDocumentOutPut.class);      

		job.setNumMapTasks(45);
		job.setNumReduceTasks(1);

		JobClient.runJob(job);
		}

}