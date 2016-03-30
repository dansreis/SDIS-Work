package MessageHandling;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import Protocol.SenderId;
import Protocol.Version;

public class Message {
	
	public static final String CRLF = "\r\n";
	
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static Message parseMessage(byte[] data){
		String dataString = new String(data);
		String[] dataArgs = dataString.split(CRLF+CRLF+" "); //separates the message into header [0] and body [1] OMG... FDS faltava a merda de um " " para qe funcionasse -.-
		
		String[] inputHeaders = dataArgs[0].split(CRLF);  //only the header at[0] will be processed and the other are considered erroneous
		String[] header = inputHeaders[0].split("\\s+"); //separates all header parts
		
		String body = null;
		if(dataArgs.length > 1)
			body = dataArgs[1];
		
		
		return getMessageType(header, body);		
		
		
	}

	private static Message getMessageType(String[] header, String body) {
		switch(header[0]){
			case "PUTCHUNK":
				return parsePUTCHUNK(header, body);
			case "STORED":
				return parseSTORED(header);
			case "GETCHUNK":
				return parseGETCHUNK(header);
			case "CHUNK":
				return parseCHUNK(header, body);
			case "DELETE":
				return parseDELETE(header);
			case "REMOVED":
				return parseREMOVED(header);
			default: break;
		}
		
		return null;
	}
	
	private static PutChunkMessage parsePUTCHUNK(String header[], String body) {
		
		if(header.length != 6)
			return null;
		
		String[] versionValues = header[1].split("\\.");
		
		Version messageVersion = new Version(Byte.parseByte(versionValues[0]), Byte.parseByte(versionValues[1]));
		
		SenderId senderId = new SenderId(header[2]);
		
		char[] fileId = header[3].toCharArray();
		
		int chunkNo = Integer.parseInt(header[4]);
		
		int replicationDeg = Integer.parseInt(header[5]);
		
		
		return new PutChunkMessage(messageVersion, senderId, fileId, chunkNo, replicationDeg, body.getBytes());
	}
	
	private static StoredMessage parseSTORED(String[] header) {
		if(header.length != 5)
			return null;
		
		
		String[] versionValues = header[1].split("\\.");
		Version messageVersion = new Version(Byte.parseByte(versionValues[0]), Byte.parseByte(versionValues[1]));
		
		SenderId senderId = new SenderId(header[2]);
		
		char[] fileId = header[3].toCharArray();
		int chunkNo = Integer.parseInt(header[4]);
		
		return new StoredMessage(messageVersion, senderId, fileId, chunkNo);
	}
	
	private static GetChunkMessage parseGETCHUNK(String[] header) {
		if(header.length != 5)
			return null;
		
		
		String[] versionValues = header[1].split("\\.");
		Version messageVersion = new Version(Byte.parseByte(versionValues[0]), Byte.parseByte(versionValues[1]));
		
		SenderId senderId = new SenderId(header[2]);
		
		char[] fileId = header[3].toCharArray();
		int chunkNo = Integer.parseInt(header[4]);
		
		return new GetChunkMessage(messageVersion, senderId, fileId, chunkNo);
	}
	
	private static ChunkMessage parseCHUNK(String[] header, String body){
		
		if(header.length != 5)
			return null;
		
		
		String[] versionValues = header[1].split("\\.");
		Version messageVersion = new Version(Byte.parseByte(versionValues[0]), Byte.parseByte(versionValues[1]));
		
		SenderId senderId = new SenderId(header[2]);
		
		char[] fileId = header[3].toCharArray();
		
		int chunkNo = Integer.parseInt(header[4]);
		
		return new ChunkMessage(messageVersion, senderId, fileId, chunkNo, body.getBytes());
	}

	private static DeleteMessage parseDELETE(String[] header) {
		if(header.length != 4)
			return null;
		
		
		String[] versionValues = header[1].split("\\.");
		Version messageVersion = new Version(Byte.parseByte(versionValues[0]), Byte.parseByte(versionValues[1]));
		
		SenderId senderId = new SenderId(header[2]);
		
		char[] fileId = header[3].toCharArray();
		
		return new DeleteMessage(messageVersion, senderId, fileId);
	}

	private static RemovedMessage parseREMOVED(String[] header) {
		if(header.length != 4)
			return null;
		
		
		String[] versionValues = header[1].split("\\.");
		Version messageVersion = new Version(Byte.parseByte(versionValues[0]), Byte.parseByte(versionValues[1]));
		
		SenderId senderId = new SenderId(header[2]);
		
		char[] fileId = header[3].toCharArray();
		
		int chunkNo = Integer.parseInt(header[4]);
		
		return new RemovedMessage(messageVersion, senderId, fileId, chunkNo);
	}

}