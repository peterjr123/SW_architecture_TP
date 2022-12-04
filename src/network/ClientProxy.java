package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import data.DocumentList;

public class ClientProxy {
	private DocumentRequestSender drs;
	private BufferedReader br;
	private PrintWriter pw;
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	
	private int port = 80;
	private String serverURL = "localhost";
	private DocumentList documentlist;
	
	public ClientProxy() {
		this.drs = new DocumentRequestSender(this.serverURL);
		this.documentlist = new DocumentList();
	}
	
	public DocumentList jsonToDocumentList(String course, String name) {
		return null;
		
	}
	public boolean login(String id, String password) {
		
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			// server에게 request 보냄.
			drs.sendLoginRequest(pw, id,password);

			socket.shutdownOutput();
			
			
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();

			// 올바른 Response일 때만 로그인 이벤트 처리
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					return true;
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}
	
	public DocumentList getDocumentList(String documentType) {
		
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			// server에게 request 보냄.
			drs.sendDocumentListRequest(pw, documentType); //documenttype :강의자료... etc
			socket.shutdownOutput();
			
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();
			
	
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					if(response.indexOf("Content-Length: 0") == -1) { //body가 있으면 
						String responsebody  = response.split("\r\n\r\n")[1];
						String[] data_list = responsebody.split("\n");
						for(String data : data_list) {
							String course = data.split("/")[0];
							String material = data.split("/")[1].strip();
							documentlist.addDocument(course, material);
						}
					}
				}
			}
			
			return documentlist;

		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			try {
				if (br != null)
					br.close();

				if (pw != null)
					pw.close();

				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;

	}
	
	//DocumentList 객체를 string으로 바꿔서 requestBody에 담는다.
	private String requestBodyBuilder(DocumentList documentlist, String dest) {
		StringBuilder sb = new StringBuilder();
		sb.append(dest+"\r\n");
		for(int i = 0; i < documentlist.length(); i++) {
			String course = documentlist.getCourse(i);
			String material = documentlist.getDocumentName(i);
			sb.append(course+"/"+material+"\r\n"); //소프트웨어 아키텍처/09-JPattV1-Ch7-Structural Patterns V02-221010\r\n
		}		
		String requestBody = sb.toString();
		return requestBody;
	}
	
	
	// 없는 과목에 대한 DocumentList 객체를 받는다.
	// dest: C: ~ 건국대학교/4-2/
	// 과목(변수)/강의자료/material(변수)
	
	public Boolean downloadDocument(DocumentList documentlist, String dest) {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);
			pw = new PrintWriter(socket.getOutputStream());
			
			String requestBody = requestBodyBuilder(documentlist ,dest);
			drs.sendDocumentRequest(pw, requestBody); 
			socket.shutdownOutput();
			
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();

			// 파일이 잘 받아 졌으면 true 리턴.
			if (response.indexOf("HTTP/") != -1) {
				if (response.indexOf("200 OK") != -1) {
					return true;
				}
			}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			try {

				if (pw != null)
					pw.close();
				
				if (bis != null)
					bis.close();
				
				if (bos != null)
					bos.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
