package Protocol;

public class GetChunkMessage  extends Message{
	Version messageVersion;
	SenderId senderId;
	char[] fileId;
	int chunkNo;

	public GetChunkMessage(Version messageVersion, SenderId senderId,
			char[] fileId, int chunkNo) {
		this.messageVersion = messageVersion;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
	}

}