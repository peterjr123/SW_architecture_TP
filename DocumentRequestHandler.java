package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.DocumentList;
import logic.GetDocumentService;


public class DocumentRequestHandler {
	private static final Logger logger = Logger.getLogger("MyHTTPServer");
	private final int port;
	private final GetDocumentService service;
	private DocumentResponseSender rs;
	
	private PrintWriter out;
	private BufferedReader in;
	private ObjectOutputStream oos;
	public DocumentRequestHandler(int port, GetDocumentService service) {
		this.port = port;
		this.service = service;
		this.rs = new DocumentResponseSender();
	}
	
	private void listen() {
		try (ServerSocket server = new ServerSocket(this.port)) {
			while (true) {
				try(Socket connection = server.accept()) {
					InputStream is = connection.getInputStream();
					OutputStream os = connection.getOutputStream();
					this.oos = new ObjectOutputStream(os);
					this.in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					this.out = new PrintWriter(os);
					StringBuilder s = new StringBuilder();
					String tmp;
					String request;
					
					while ((tmp = in.readLine()) != null) {
						s.append(tmp+"\r\n");
					}
					request = s.toString();
					
					if (request.indexOf("HTTP/") != -1) {
						if(request.indexOf("POST /login")!=-1) {
							String requestBody = request.split("\r\n\r\n")[1];
							String[] info = requestBody.split("/");
							String id = info[0];
							String pw = info[1];
							Boolean result = service.login(id, pw);
							if(result) {
								rs.setSuccessLoginResponse(out);
							}else {
								rs.setFailedResponse(out);
							}
						}if(request.indexOf("GET /")!=-1) {
							String requestParam = request.split(" ")[1].replace("/", "");
							DocumentList documentlist = service.getDocumentList(requestParam);
							rs.setGetListResponse(oos, documentlist);
							// 만약 null인지 구분해서 response를 보내려면 client에서 object 클래스를 받고 그게 String message인지, documnetList인지 확인
						}
					}
					
					
				} catch (IOException ex) {
					logger.log(Level.WARNING, "Exception accepting connection", ex);
				} catch (RuntimeException ex) {
					logger.log(Level.SEVERE, "Unexpected error", ex);
				}
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Could not start server", ex);
		}
	}
	
	public static void main(String[] args) {
		int port = 80;
		GetDocumentService service = new GetDocumentService();
		DocumentRequestHandler handler = new DocumentRequestHandler(port,service);
		handler.listen();

	}
	
}
