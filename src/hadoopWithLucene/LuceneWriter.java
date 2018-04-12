package hadoopWithLucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
 
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class LuceneWriter {
    
    private Path perm;
    private Path temp;
    private FileSystem fs;
    private IndexWriter writer;
     
    public void open(JobConf job, String name) throws IOException{
        this.fs = FileSystem.get(job);
        perm = new Path(FileOutputFormat.getOutputPath(job), name);
         
        // 临时本地路径，存储临时的索引结果
        temp = job.getLocalPath("index2/_" + Integer.toString(new Random().nextInt()));
        fs.delete(perm, true); 
         
        writer = new IndexWriter(FSDirectory.open(new File(fs.startLocalOutput(perm, temp).toString())),
        		new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.LIMITED);
    }
    public void close() throws IOException{
        // 索引优化和IndexWriter对象关闭
        writer.close();
         
        // 将本地索引结果拷贝到HDFS上
        fs.completeLocalOutput(perm, temp);
        fs.createNewFile(new Path(perm,"index.done"));     
    }
     
    /*
    * 接受HDFSDocument对象，从中读取信息并建立索引
    */
    public void write(HDFSDocument doc) throws IOException{
 
        String key = null;
        HashMap<String, String> fields = doc.getFields();
        Iterator<String> iter = fields.keySet().iterator();
        String path = "hdfs://localhost:9000/user/root/DataIndex" + key;
        while(iter.hasNext()){
            key = iter.next();
                         
            Document luceneDoc = new Document();
            
            // 如果使用Field.Index.ANALYZED选项，则默认情况下会对中文进行分词。
            // 如果这时候采用Term的形式进行检索，将会出现检索失败的情况。
            luceneDoc.add(new Field("table", key, Field.Store.YES,Field.Index.NOT_ANALYZED));
            luceneDoc.add(new Field("dataterm", fields.get(key), Field.Store.YES,Field.Index.ANALYZED)); 
            luceneDoc.add(new Field("path", path , Field.Store.YES, Field.Index.NOT_ANALYZED));
            //Field pathField = new StringField("path", "student", Field.Store.YES);
            //luceneDoc.add(pathField);
            writer.addDocument(luceneDoc);
        }
    }
}