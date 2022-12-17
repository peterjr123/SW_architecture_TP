package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import data.DocumentList;

public class ClientProxy {
	private DocumentRequestSender requestSender;
	private ResponseParser responseParser;
	
	private int port = 3333;
	private String serverURL = "localhost";

	public static void main(String[] args) {
		ClientProxy service = new ClientProxy();
		boolean result = service.login("qqazws7", "lookat159~");
		System.out.println("로그인 결과: " + result);
		
//		service.downloadDocument(null, "C:\\Users\\joon\\vscode-workspace\\python-workspace\\software_architecture\\test");
//		service.downloadDocuments(null, "C:\\Users\\82103\\Desktop\\client");
		
		DocumentList list = service.getDocumentList("강의자료");
		for(int i = 0; i < list.length(); i++) {
			System.out.println(list.getCourse(i) + ": " + list.getDocumentName(i));
		}
	}
	
	public ClientProxy() {
		this.requestSender = new DocumentRequestSender(this.serverURL);
		this.responseParser = new ResponseParser();
	}
	
	private String loginImpl(BufferedReader readStream, PrintWriter writeStream, 
								String id, String password) throws IOException {
		// clients send request to server with id/password
		String requestBody = id+"/"+password;
		requestSender.sendLoginRequest(writeStream, requestBody);
		
		String line = null;
		StringBuilder strBuilder = new StringBuilder();
		while ((line = readStream.readLine()) != null) {
			strBuilder.append(line + "\r\n");
		}
		return strBuilder.toString();
	}
	
	public boolean login(String id, String password) {
		Socket socket = null;
		BufferedReader readStream = null;
		PrintWriter writeStream = null;
		
		try {
			socket = new Socket(serverURL, port);
			readStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writeStream = new PrintWriter(socket.getOutputStream());
			
			String response = loginImpl(readStream, writeStream, id, password);
			Boolean result = responseParser.loginResponseParser(response);
			return result;
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (readStream != null)
					readStream.close();

				if (readStream != null)
					readStream.close();
				
				if (socket != null)
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private String getDocumentListImpl(BufferedReader readStream, PrintWriter writeStream, String documentType) throws IOException {
			// clients send request to server with document type
			requestSender.sendDocumentListRequest(writeStream, documentType); //document type :강의자료... etc
			
			String line = null;
			StringBuilder strBuilder = new StringBuilder();
			while ((line = readStream.readLine()) != null) {
				strBuilder.append(line + "\r\n");
			}
			return strBuilder.toString();
	}
	
	public DocumentList getDocumentList(String documentType) {
		Socket socket = null;
		DocumentList documentlist = null;
		BufferedReader readStream = null;
		PrintWriter writeStream = null;
		
		try {
			socket = new Socket(serverURL, port);
			readStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writeStream = new PrintWriter(socket.getOutputStream());
			
			String response = getDocumentListImpl(readStream, writeStream, documentType);
			documentlist = responseParser.getDocumentResponseParser(response);
			return documentlist;

		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			try {
				if (readStream != null)
					readStream.close();

				if (writeStream != null)
					writeStream.close();

				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;

	}
	
	@SuppressWarnings("resource")
	private void getSingleDocument(PrintWriter writeStream, BufferedInputStream readFile, BufferedOutputStream writeFile, 
					String courseName, String documentName, String destination) throws IOException {
		File saveDirectory = new File(destination);
		File saveFile = new File(saveDirectory, documentName);
		String requestBody = courseName+"/"+documentName;
		// client sends request to server with missing document
		requestSender.sendDocumentRequest(writeStream, requestBody); 
		writeFile = new BufferedOutputStream(new FileOutputStream(saveFile));
		
		byte[] temp = new byte[1024];
		int length = 0;
		
		while((length = readFile.read(temp)) > 0){
			writeFile.write(temp , 0, length);
		}
		System.out.println("파일 다운로드 완료: "+destination);
		writeFile.flush();
		
	}

	public void getDocuments(DocumentList documentList, String destination) {

		for(int i = 0; i < documentList.length(); i++) {
			Socket socket = null;
			PrintWriter writeStream = null;
			BufferedInputStream readFile = null;
			BufferedOutputStream writeFile = null;
			
			try {
				socket = new Socket("localhost", 3333);
				writeStream = new PrintWriter(socket.getOutputStream());
				readFile = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
				String courseName = documentList.getCourse(i);
				String documentName = documentList.getDocumentName(i);
				
				getSingleDocument(writeStream, readFile, writeFile, courseName, documentName, destination);
			} catch (IOException e1) {
				e1.printStackTrace();
			}finally {
				try {
					if (writeStream != null)
						writeStream.close();
					
					if (readFile != null)
						readFile.close();
					
					if (writeFile != null)
						writeFile.close();
					
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
