
public class header {

	String MessageType;
	String Version;
	String SenderId;
	String FileId;
	String ChunckNo;
	String ReplicationDeg;
	
	public String getMessageType() {
		return MessageType;
	}
	
	public void setMessageType(String messageType) {
		MessageType = messageType;
	}
	
	public String getVersion() {
		return Version;
	}
	
	public void setVersion(String version) {
		Version = version;
	}
	
	public String getSenderId() {
		return SenderId;
	}
	
	public void setSenderId(String senderId) {
		SenderId = senderId;
	}
	
	public String getFileId() {
		return FileId;
	}
	
	public void setFileId(String fileId) {
		FileId = fileId;
	}
	
	public String getChunckNo() {
		return ChunckNo;
	}
	
	public void setChunckNo(String chunckNo) {
		ChunckNo = chunckNo;
	}
	
	public String getReplicationDeg() {
		return ReplicationDeg;
	}
	
	
	public void setReplicationDeg(String replicationDeg) {
		ReplicationDeg = replicationDeg;
	}
	
}
