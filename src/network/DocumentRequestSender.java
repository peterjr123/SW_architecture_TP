package network;

import java.io.IOException;
import java.io.PrintWriter;

public class DocumentRequestSender {
	
	private String host;
	
	public DocumentRequestSender(String serverURL) {
		this.host = serverURL;
	}
	
	public void sendLoginRequest(PrintWriter pw, String id, String password) {
		pw.println("POST /login HTTP/1.1");
		pw.println("Host: "+host);
		pw.println();
		pw.println(id+"/"+password);  //requestbody
		pw.flush();
	}
	
	public void sendDocumentListRequest(PrintWriter pw,String requestbody) {
		pw.println("GET /documentlist HTTP/1.1"); 
		pw.println("Host: "+host);
		pw.println();
		pw.print(requestbody);
		pw.flush();
	}
	
	
	public void sendDocumentRequest(PrintWriter pw, String requestBody) throws IOException {
		pw.println("GET /document HTTP/1.1"); 
		pw.println("Host: "+host);
		pw.println();
		pw.print(requestBody);
		pw.flush();
	}
}
