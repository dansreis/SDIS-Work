package main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class Connection {
	private MulticastSocket recSocket;
	private DatagramSocket sendSocket;
	private InetAddress mAddr;
	
	private String mcast_addr;
	private int mcast_port;
	
	public Connection(String mcast_addr, int mcast_port) throws IOException{
		this.mcast_addr = mcast_addr;
		this.mcast_port = mcast_port;
		
		mAddr = InetAddress.getByName(mcast_addr);
		recSocket = new MulticastSocket(mcast_port);
		sendSocket = new DatagramSocket();	
		
		recSocket.joinGroup(mAddr);
		System.out.println("connected");
	}

	public void send(String msg) throws IOException{
		DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.length(), mAddr, mcast_port);
		recSocket.send(msgPacket); 
	
        System.out.println("Server sent packet with msg: " + msg);
	}
	
	public String receive() throws IOException{
		byte[] buf = new byte[1024];
		
		DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
		sendSocket.receive(msgPacket);

        String msg = new String(buf, 0, buf.length);
        System.out.println("Socket 1 received msg: " + msg);
        
        return msg;
	}
}