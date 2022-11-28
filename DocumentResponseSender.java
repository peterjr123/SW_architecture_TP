package network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import data.DocumentList;

public class DocumentResponseSender {
	
	//로그인 요청에 대한 response
	public void setSuccessLoginResponse(PrintWriter out) {
		out.println("HTTP/1.1 200 OK");
		out.println();
		out.flush();
	}
	
	//documentlist 요청에 대한 response
	public void setGetListResponse(ObjectOutputStream oos, DocumentList documentlist) throws IOException {
		oos.writeObject(documentlist);
	}
	
	// 요청 실패시 response
	public void setFailedResponse(PrintWriter out) {
		out.println("HTTP/1.1 400 Bad Request");
		out.println();
		out.flush();
	}
	

}
