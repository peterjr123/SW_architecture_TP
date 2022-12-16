package network;

import java.io.IOException;
import java.io.PrintWriter;

public class DocumentRequestSender {
	
	private String host;
	
	public DocumentRequestSender(String serverURL) {
		this.host = serverURL;
	}
	
	public void sendLoginRequest(PrintWriter writeStream, String id, String password) {
		writeStream.println("POST /login HTTP/1.1");
		writeStream.println("Host: "+host);
		writeStream.println();
		writeStream.println(id+"/"+password);  //requestbody
		writeStream.flush();
	}
	
	public void sendDocumentListRequest(PrintWriter writeStream,String requestbody) {
		writeStream.println("GET /documentlist HTTP/1.1"); 
		writeStream.println("Host: "+host);
		writeStream.println();
		writeStream.print(requestbody);
		writeStream.flush();
	}
	
	
	public void sendDocumentRequest(PrintWriter writeStream, String requestBody) throws IOException {
		writeStream.println("GET /document HTTP/1.1"); 
		writeStream.println("Host: "+host);
		writeStream.println();
		writeStream.print(requestBody);
		writeStream.flush();
	}
}
