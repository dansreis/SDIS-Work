package MessageHandling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import Protocol.SenderId;
import Protocol.Version;
import main.Chunk;

public class PutChunkMessage extends Message{
	
	Version messageVersion;
	SenderId senderId;
	String fileId;
	int chunkNo;
	int replicationDeg;
	byte[] bytes;
	
	
	public Version getMessageVersion() {
		return messageVersion;
	}
	public void setMessageVersion(Version messageVersion) {
		this.messageVersion = messageVersion;
	}
	public SenderId getSenderId() {
		return senderId;
	}
	public void setSenderId(SenderId senderId) {
		this.senderId = senderId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public int getChunkNo() {
		return chunkNo;
	}
	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}
	public int getReplicationDeg() {
		return replicationDeg;
	}
	public void setReplicationDeg(int replicationDeg) {
		this.replicationDeg = replicationDeg;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public PutChunkMessage(Version messageVersion, SenderId senderId,
			String fileId, int chunkNo, int replicationDeg, byte[] bytes) {
		this.messageVersion = messageVersion;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
		this.bytes = bytes;
		this.setType("PUTCHUNK");
	}
	
	public void doIt(){
		File ChunkDir = new File("Chunks");
		if (!(ChunkDir.exists() && ChunkDir.isDirectory())) {
			//System.out.println("Creating chunks directory");
			ChunkDir.mkdir();
		}
		File FileDir = new File("Chunks//" + new String(this.fileId));
		if (!(FileDir.exists() && FileDir.isDirectory())) {
			System.out.println("Creating File directory: " + "/Chunks/" + new String(this.fileId));
			FileDir.mkdirs();
		}
		try {
			System.out.println("Creating Chunk file with size: " + bytes.length);
			OutputStream os = new FileOutputStream("Chunks//" + new String(this.fileId) + "//" + chunkNo + ".chk");
			
			
			os.write(bytes,0,bytes.length);
			os.close();
			//Chunk ck = new Chunk(this.fileId, this.chunkNo, this.replicationDeg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
