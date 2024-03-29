package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import database.Info;

public class ThreadFileWrite extends Thread{
		private Thread t;
		private String threadName;
		private File file;
		private boolean flag;
		private Info info;
		
	   
		public ThreadFileWrite(File file,Info info,String threadName){
			this.threadName = threadName;
			this.file = file;
			this.flag=true;
			this.info = info;
		}
		
		
		public void run() {
				try {
					FileOutputStream  fileOutput= new FileOutputStream(file);
					FileLock isLocked;
					while(flag){
						try {
							fileOutput = new FileOutputStream(file);
							isLocked = fileOutput.getChannel().tryLock();
							if(isLocked == null) //file it is locked!!
								continue;
							else{
								ObjectOutputStream out = new ObjectOutputStream(fileOutput);
						        out.writeObject(info);
						        out.close();
								flag = false; // to exit cicle
							}
						    try {
						    	
						    } finally {
						    	isLocked.release();
						    }
						}catch(OverlappingFileLockException e){
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						} finally {
							try {
								fileOutput.close();
							} catch (IOException e) {
								//e.printStackTrace();
							}
						}
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			System.out.println("Finished thread: "+threadName);
		}
	   
		public void start ()
		{
			//System.out.println("Starting " +  threadName );
			if (t == null)
			{
				t = new Thread (this, threadName);
				t.start ();
			}
		}
	

}




/*FileOutputStream fileOut = new FileOutputStream(path);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this.info);
	         out.close();
	         fileOut.close();
	         System.out.println("Serialized data is saved in " + path);*/
