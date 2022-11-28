package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import data.DocumentList;

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
	
	public void sendDocumentListRequest(PrintWriter pw,String documentType) {
		pw.println("GET /"+documentType+" HTTP/1.1"); 
		pw.println("Host: "+host);
		pw.println();
		pw.flush();
	}
	
	public void sendDocumentRequest(PrintWriter pw, String name) throws IOException {
		pw.println("GET /"+name+" HTTP/1.1"); 
		pw.println("Host: "+host);
		pw.println();
		pw.flush();
	}
}
