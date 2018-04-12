package hadoopWithLucene;

import hadoopWithLucene.LuceneWriter;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.mapred.RecordWriter;
//import org.apache.lucene.document.Fieldable;


public class HDFSDocumentOutPut extends FileOutputFormat<Text, HDFSDocument>{
	
	@Override
    public RecordWriter<Text, HDFSDocument> getRecordWriter(
            final FileSystem fs, JobConf job, String name, final Progressable progress)
                    throws IOException {
        // LuceneWriter是包含Lucene的IndexWriter对象的类
        final LuceneWriter lw = new LuceneWriter();
        // 完成索引前的配置工作
        lw.open(job, name);
         
        return new RecordWriter<Text, HDFSDocument>(){
 
        	@Override
        	public void close(final Reporter reporter) throws IOException {
                // 完成索引优化，关闭IndexWriter的对象
                lw.close();
            }
 
        	@Override
        	public void write(Text arg0, HDFSDocument doc) throws IOException {
                // 建立索引
                lw.write(doc);
            }  
        };
    }   

}
