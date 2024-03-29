package comunication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import MessageHandling.Message;
import MessageHandling.PutChunkMessage;

public class ChannelThreads extends Thread{
	private Thread t;
	private String threadName;
	private Peer peer;
	private Connection connection;
   
	ChannelThreads(Peer peer, String name){
		this.peer = peer;
		threadName = name;
		//System.out.println("Creating " +  threadName );
		switch(name){
			case "MC":
				connection = peer.getMC();
				break;
			case "MDB":
				connection = peer.getMDB();
				break;
			case "MDR":
				connection = peer.getMDR();
				break;
		}
	}
	
	
	public void run() {
		String str = "Nothing received";
		
		//System.out.println("Running " +  threadName );
		while(true){
			try {
				//System.out.println("Waiting a call in " + this.threadName);
				byte strB[] = connection.receive();

				Message msg = Message.parseMessage(strB);
				
				
				messageHandling msgRec = new messageHandling(peer, msg, msg.getType());
				msgRec.start();
				/*
				PutChunkMessage b = (PutChunkMessage)c;
				System.out.println("ChunkNO: "+b.getChunkNO());*/
				
					
				/*Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println("Thread " +  threadName + " interrupted.");*/
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//System.out.println("Thread " +  threadName + " exiting.");
			
		}
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
