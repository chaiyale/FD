package javaToHdfs;

import java.io.*;
import java.util.Random;
import javaToHdfs.HadoopFSUtil;

import org.apache.lucene.store.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.Configuration;

/** Reads a Lucene index stored in DFS. */
public class FsDirectory extends Directory {

  private FileSystem fs;
  private Path directory;
  private int ioFileBufferSize;

  public FsDirectory(FileSystem fs, Path directory, boolean create, Configuration conf)
    throws IOException {

    this.fs = fs;
    this.directory = directory;
    this.ioFileBufferSize = conf.getInt("io.file.buffer.size", 4096);
    
    if (create) {
      create();
    }
  }

  private void create() throws IOException {
    if (!fs.exists(directory)) {
      fs.mkdirs(directory);
    }

    if (!fs.getFileStatus(directory).isDir())
      throw new IOException(directory + " not a directory");

    // clear old files
    FileStatus[] fstats = fs.listStatus(directory, HadoopFSUtil.getPassAllFilter());
    Path[] files = HadoopFSUtil.getPaths(fstats);
    for (int i = 0; i < files.length; i++) {
      if (!fs.delete(files[i], false))
        throw new IOException("Cannot delete " + files[i]);
    }
  }
  
  public boolean IsDir() throws IOException{
	  if (fs.getFileStatus(directory).isDir())
		  return true;
	  else
		  return false;
  }
  
  public Path getPath() throws IOException{
	  return this.directory;
  }

  public String[] listAll() throws IOException {
    FileStatus[] fstats = fs.listStatus(directory, HadoopFSUtil.getPassAllFilter());
    Path[] files = HadoopFSUtil.getPaths(fstats);
    if (files == null) return null;

    String[] result = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      result[i] = files[i].getName();
    }
    return result;
  }

  public boolean fileExists(String name) throws IOException {
    return fs.exists(new Path(directory, name));
  }

  public long fileModified(String name) {
    throw new UnsupportedOperationException();
  }

  public void touchFile(String name) {
    throw new UnsupportedOperationException();
  }

  public long fileLength(String name) throws IOException {
    return fs.getFileStatus(new Path(directory, name)).getLen();
  }

  public void deleteFile(String name) throws IOException {
    if (!fs.delete(new Path(directory, name), false))
      throw new IOException("Cannot delete " + name);
  }

  public void renameFile(String from, String to) throws IOException {
    // DFS is currently broken when target already exists,
    // so we explicitly delete the target first.
    Path target = new Path(directory, to);
    if (fs.exists(target)) {
      fs.delete(target, false);
    }
    fs.rename(new Path(directory, from), target);
  }

  public InputStream readFile() throws IOException{
	  InputStream in = null;
	  try
	  {
		  in = fs.open(this.directory);
		  return in;
	  }
	  finally
	  {
	  }
  }
  
  public IndexOutput createOutput(String name) throws IOException {
    Path file = new Path(directory, name);
    if (fs.exists(file) && !fs.delete(file, false))      // delete existing, if any
      throw new IOException("Cannot overwrite: " + file);

    return new DfsIndexOutput(file, this.ioFileBufferSize);
  }


  public IndexInput openInput(String name) throws IOException {
    return new DfsIndexInput(new Path(directory, name), this.ioFileBufferSize);
  }

  public Lock makeLock(final String name) {
    return new Lock() {
      public boolean obtain() {
        return true;
      }
      public void release() {
      }
      public boolean isLocked() {
        throw new UnsupportedOperationException();
      }
      public String toString() {
        return "Lock@" + new Path(directory, name);
      }
    };
  }

  public synchronized void close() throws IOException {
    fs.close();
  }

  public String toString() {
    return this.getClass().getName() + "@" + directory;
  }


  public class DfsIndexInput extends BufferedIndexInput {

    /** Shared by clones. */
    public class Descriptor {
      public FSDataInputStream in;
      public long position;                       // cache of in.getPos()
      public Descriptor(Path file, int ioFileBufferSize) throws IOException {
        this.in = fs.open(file);
      }
    }

    private final Descriptor descriptor;
    private final long length;
    private boolean isClone;

    public DfsIndexInput(Path path, int ioFileBufferSize) throws IOException {
      descriptor = new Descriptor(path,ioFileBufferSize);
      length = fs.getFileStatus(path).getLen();
    }

    public void readInternal(byte[] b, int offset, int len)
      throws IOException {
      synchronized (descriptor) {
        long position = getFilePointer();
        if (position != descriptor.position) {
          descriptor.in.seek(position);
          descriptor.position = position;
        }
        int total = 0;
        do {
          int i = descriptor.in.read(b, offset+total, len-total);
          //if (i == -1)
            //throw new IOException("read past EOF");
          descriptor.position += i;
          total += i;
        } while (total < len);
      }
    }

    public void close() throws IOException {
      if (!isClone) {
        descriptor.in.close();
      }
    }

    protected void seekInternal(long position) {} // handled in readInternal()

    public long length() {
      return length;
    }

    protected void finalize() throws IOException {
      close();                                      // close the file
    }

    public Object clone() {
      DfsIndexInput clone = (DfsIndexInput)super.clone();
      clone.isClone = true;
      return clone;
    }
  }

  private class DfsIndexOutput extends BufferedIndexOutput {
    private FSDataOutputStream out;
    private RandomAccessFile local;
    private File localFile;

    public DfsIndexOutput(Path path, int ioFileBufferSize) throws IOException {
      
      // create a temporary local file and set it to delete on exit
      String randStr = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
      localFile = File.createTempFile("index_" + randStr, ".tmp");
      localFile.deleteOnExit();
      local = new RandomAccessFile(localFile, "rw");

      out = fs.create(path);
    }

    public void flushBuffer(byte[] b, int offset, int size) throws IOException {
      local.write(b, offset, size);
    }

    public void close() throws IOException {
      super.close();
      
      // transfer to dfs from local
      byte[] buffer = new byte[4096];
      local.seek(0);
      int read = -1;
      while ((read = local.read(buffer)) != -1) {
        out.write(buffer, 0, read);
      }
      out.close();
      local.close();
    }

    public void seek(long pos) throws IOException {
      super.seek(pos);
      local.seek(pos);
    }

    public long length() throws IOException {
      return local.length();
    }

  }

}
