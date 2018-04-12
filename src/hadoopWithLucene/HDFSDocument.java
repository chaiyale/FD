package hadoopWithLucene;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
 
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class HDFSDocument implements Writable{
    HashMap<String,String> fields = new HashMap<String, String>();
 
    public void setFields(HashMap<String,String> fields){
        this.fields = fields;
    }
    public HashMap<String,String> getFields(){
        return this.fields;
    }
 
    @Override
    public void readFields(DataInput in) throws IOException {
        fields.clear();
 
        String key = null, value = null;
 
        int size = WritableUtils.readVInt(in);
        for (int i = 0; i < size; i ++){
                // 依次读取两个字符串，形成一个Map值
                key = in.readUTF();
                value = in.readUTF();
                fields.put(key,value);
            }
    }
 
    @Override
    public void write(DataOutput out) throws IOException {
        String key = null, value = null;
 
        Iterator<String> iter = fields.keySet().iterator();
        while(iter.hasNext()){
            key = iter.next();
            value = fields.get(key);
             
            // 依次写入<Key,Value>两个字符串
            out.writeUTF(key);
            out.writeUTF(value);
        }
    }
}
