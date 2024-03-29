package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import comunication.ChannelThreads;
import comunication.Connection;
import comunication.MakeBackup;
import comunication.MessageSubject;
import comunication.Peer;
import comunication.putchunkObserver;
import database.Info;
import database.Serial;

public class Client {
	
	
	private static final String CLRF = "\r\n";
	Peer peer;
	ChannelThreads MC;
	ChannelThreads MDB;
	ChannelThreads MDR;
	Connection con_MC;
	Connection con_MDB;
	Connection con_MDR;
	
	static String param[] = {"224.0.0.19", "8888", "224.0.0.3", "8032", "224.0.0.3", "8033", "65"};
	//static String param[] = {"224.0.0.19", "10001", "224.1.0.2", "10002", "224.1.0.3", "10003", "64"};
	
	public Client(Peer peer, String args[]) throws NumberFormatException, NoSuchAlgorithmException, IOException, InterruptedException{
		this.peer = peer;
		this.MC = peer.getMCThread();
		this.MDB = peer.getMDBThread();
		this.MDR = peer.getMDRThread();
		this.con_MC = peer.getMC();
		this.con_MDB = peer.getMDB();
		this.con_MDR = peer.getMDR();
		
		if(!checkArgsNum(args))//checks if there is a invalid number of arguments
			return;
		
		int peer_access_point;
		String sub_protocol;
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(args[0]);
		boolean b = m.matches();
		
		if(!b){
			System.out.println("Invalid <peer_ap>!");
			return;
		}
		peer_access_point = Integer.parseInt(args[0]);
		sub_protocol = args[1];
		param[6] = args[0];
		
		switch(sub_protocol){
			case "BACKUP":
				if(checkBackup(args[2],args[3]) && args.length == 4){
					System.out.println("OK- starting peer..");
					//startPeer();
					startBackup(peer_access_point,sub_protocol,args[2],Integer.parseInt(args[3]));
				}
				else
					System.out.println("Invalid <sub_protocol>");
				break;
			case "RESTORE":
				if(checkRestore(args[2]) && args.length == 3){
					System.out.println("OK");
					//startPeer();
					startRestore(peer_access_point,sub_protocol,args[2]);
				}	
				else
					System.out.println("Invalid <sub_protocol>");
				break;
			case "DELETE":
				if(checkDelete(args[2])&& args.length == 3){
					System.out.println("OK");
					//startPeer();
					startDelete(peer_access_point,sub_protocol,args[2]);
				}
				else
					System.out.println("Invalid <sub_protocol>");
				break;
			case "RECLAIM":
				if(args.length == 3){
					System.out.println("OK22");
					//startPeer();
					startReclaim(peer_access_point, sub_protocol, args[2]);
					//handleSubProtocol(peer_access_point,sub_protocol,args[2]);
					}else
					System.out.println("Invalid <sub_protocol>");
				break;
			default:
				System.out.println("Invalid <sub_protocol>");
				return;
		}
			
	}
	//END OF MAIN FUNCTION
	/*
	private static void startPeer(){
		peer = new Peer(param);
		
		MC = peer.getMCThread();
		MDB = peer.getMDBThread();
		MDR = peer.getMDRThread();
		
		MC.start();
		MDB.start();
		MDR.start();
		
		con_MC = peer.getMC();
		con_MDB = peer.getMDB();
		con_MDR = peer.getMDR();
	}*/
	public void startBackup(int peer_access_point, String sub_protocol, String filename, int replication) throws IOException, NoSuchAlgorithmException, InterruptedException {
		File file = new File(filename);
		double size = file.length()/64000;
		size = Math.ceil(size);
		System.out.println("Chunks Number: "+size);
		FileInputStream readFile = new FileInputStream(file);
		
		int chunkNO = 1;
		byte[] tempData;
		String hash = filename+peer_access_point;
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(hash.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
        String fileId = DatatypeConverter.printHexBinary(digest);
        peer.getInfo().getFileNames().put(fileId, filename);
        
        int numberBytes = (int) file.length();
        
        System.out.println("Tamanho: "+numberBytes);
        
       
		while(numberBytes != -1){
			
			String peerID;
			peerID = param[6];
			String send = new String("PUTCHUNK 1.0 "+peerID+" "+fileId+" "+chunkNO+" "+replication+" \r\n\r\n");
			
			if(numberBytes == 0){
				tempData = new byte[0];
			}else if(numberBytes >= 64000){
				tempData = new byte[64000];
				numberBytes-=tempData.length;
			}else{
				tempData = new byte[numberBytes];
				numberBytes-=tempData.length;
			}
			
			/*if(numberBytes == 0){
				tempData = new byte[0];
				numberBytes=-1;
			}
			else if(numberBytes >= 64000){
				tempData = new byte[64000-send.getBytes().length];
				numberBytes-=tempData.length;
			}
			else if(numberBytes+send.getBytes().length > 64000){
				tempData = new byte[numberBytes-send.getBytes().length];
				numberBytes-=tempData.length;
			}
			else{
				tempData = new byte[numberBytes];
				numberBytes=-1;
			}*/
				
			
				
			
			readFile.read(tempData);

			byte[] sendAll = new byte[send.getBytes().length+tempData.length];
			System.arraycopy(send.getBytes(), 0, sendAll, 0, send.getBytes().length);
			System.arraycopy(tempData, 0, sendAll, send.getBytes().length, tempData.length);
			
			MakeBackup mb = new MakeBackup("PUTCHUNK",fileId,chunkNO, replication, peer, con_MDB, sendAll);
			mb.start();
			if(numberBytes==0)
				numberBytes=-1;
			
			chunkNO++;
			
		}

		System.out.println("Ending Sending chunks! \n \n");
		System.out.println(chunkNO);
	
	}
	public void startDelete(int peer_access_point,String sub_protocol, String filename) throws NoSuchAlgorithmException, IOException{
		String hash = filename+peer_access_point;
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(hash.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
        String fileId = DatatypeConverter.printHexBinary(digest);
        String sendData = new String("DELETE "+1.0+" "+peer_access_point+" "+fileId+ " "+"\r\n\r\n");
        System.out.println(sendData);
        con_MC.send(sendData.getBytes());
		
	}
	
	public void handleSubProtocol(int peer_access_point,String sub_protocol, String filename,int replication){
		//TODO fazer o handler
		System.out.println("peer_access_point: "+ peer_access_point);
		System.out.println("sub_protocol: "+ sub_protocol);
		System.out.println("replication: "+ replication);
		
	}
	public void startRestore(int peer_access_point,String sub_protocol, String filename) throws NoSuchAlgorithmException, IOException, InterruptedException{
		String hash = filename+peer_access_point;
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(hash.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
        String fileId = DatatypeConverter.printHexBinary(digest);
        Serial serial = peer.getSerial();
        Info info = serial.getInfo();
        int totalChunks = info.getChunksNumOfFileId(fileId);
        System.out.println(fileId);
        
        for(int i = 1; i <= totalChunks;i++){
        	String sendData = new String("GETCHUNK "+"1.0 "+peer_access_point+" "+fileId+" "+i+" \r\n\r\n");
        	con_MDR.send(sendData.getBytes());
        	TimeUnit.MILLISECONDS.sleep(200);
        }
	}
	
	public void startReclaim(int peer_access_point, String sub_protocol, String space1) throws IOException {
		int freedSpace = 0;
		int space = Integer.parseInt(space1);
		
		while(freedSpace < space){
			File chunksDir = new File("Chunks");
			if (chunksDir.exists() && chunksDir.isDirectory()){
				File[] contents = chunksDir.listFiles();
				for (int i = 0; i < contents.length; i++){
					int rep = peer.getInfo().getFileRep().get(contents[i].getName());
					if (rep != 1){
						File[] chunks = contents[i].listFiles();
						for(File f : chunks){
							System.out.println("apagar file " + f.getName() + " with size: " + f.length());
							freedSpace+=f.length();
							String name = f.getName();
							f.delete();
							
							String sendData = new String("REMOVED " + "1.0 " + peer_access_point + " "+name+" "+i+" \r\n\r\n");
							con_MC.send(sendData.getBytes());
							
							if (freedSpace > space)
								return;
						}
					}
				}
			}
		}
		
	}

	private static boolean checkDelete(String filename) {
		if(!checkIfFileExists(filename))
			return false;
		return true;
		
	}

	private static boolean checkRestore(String filename){
		if(!checkIfFileExists(filename))
			return false;
		
			
		return true;
	}

	private static boolean checkBackup(String filename,String r) {
		int replication = Integer.parseInt(r);
		if(replication >9 || replication <0 || !checkIfFileExists(filename)){
			System.out.println("Invalid replication");
			return false; 
			}
		return true;
	}

	public static boolean checkArgsNum(String[] args){
		if(args.length <3 || args.length>4){
			printInvalidArgsNumberHelp();
			return false;
		}
		return true;
	}
	
	public static void printInvalidArgsNumberHelp(){
		System.out.println("Invalid number of arguments!");
		System.out.println("Try one of the following types: ");
		System.out.println("<peer_ap> <sub_protocol> <opnd_1> <opnd_2> ");
		System.out.println("Where: ");
		System.out.println("<peer_ap>");
		System.out.println("Is the local peer access point. This depends on the implementation. (Check the next section)");
		System.out.println("<sub_protocol>");
		System.out.println("Is the sub protocol being tested, and must be one of: BACKUP, RESTORE, DELETE, RECLAIM. In the case of enhancements, you must append the substring ENH at the end of the respecive subprotocol, e.g. BACKUPENH");
		System.out.println("<opnd_1>");
		System.out.println("Is either the path name of the file to backup/restore/delete, for the respective 3 subprotocols, or the amount of space to reclaim. In the latter case, the peer should execute the RECLAIM protocol, upon deletion of any chunk.");
		System.out.println("<opnd_2>");
		System.out.println("This operand is an integer that specifies the desired replication degree and applies only to the backup protocol (or its enhancement)");
	}
	
	public static boolean checkIfFileExists(String filename){
		File file = new File(filename);
		if(file.exists())
			return true;
		System.out.println("Non existing file");
		return false;
	}

}