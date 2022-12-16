package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import data.DocumentList;

public class ClientProxy {
	private DocumentRequestSender requestSender;
	private RequestParser requestparser;
	
	private BufferedReader readStream;
	private PrintWriter writeStream;
	private BufferedOutputStream writeFile;
	private BufferedInputStream readFile;
	
	private int port = 3333;
	private String serverURL = "localhost";

	public static void main(String[] args) {
		ClientProxy service = new ClientProxy();
		boolean result = service.login("qqazws7", "lookat159~");
		System.out.println("로그인 결과: " + result);
		
//		service.downloadDocument(null, "C:\\Users\\joon\\vscode-workspace\\python-workspace\\software_architecture\\test");
		service.downloadDocuments(null, "C:\\Users\\82103\\Desktop\\client");
		
//		DocumentList list = service.getDocumentList("강의자료");
//		for(int i = 0; i < list.length(); i++) {
//			System.out.println(list.getCourse(i) + ": " + list.getDocumentName(i));
//		}
	}
	
	public ClientProxy() {
		this.requestSender = new DocumentRequestSender(this.serverURL);
		this.requestparser = new RequestParser();
	}
	
	public boolean login(String id, String password) {
		Socket socket = null;
		
		try {
			socket = new Socket(serverURL, port);
			readStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writeStream = new PrintWriter(socket.getOutputStream());

			// server에게 request 보냄.
			requestSender.sendLoginRequest(writeStream, id, password);

			socket.shutdownOutput();
			
			String line = null;
			StringBuilder strBuilder = new StringBuilder();
			while ((line = readStream.readLine()) != null) {
				strBuilder.append(line + "\r\n");
			}
			String response = strBuilder.toString();
			
			Boolean result = requestparser.loginResponseParser(response);
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
	
	public DocumentList getDocumentList(String documentType) {
		Socket socket = null;
		DocumentList documentlist = null;
		
		try {
			socket = new Socket(serverURL, port);
			readStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			writeStream = new PrintWriter(socket.getOutputStream());

			// server에게 request 보냄.
			requestSender.sendDocumentListRequest(writeStream, documentType); //documenttype :강의자료... etc
			socket.shutdownOutput();
			
			String line = null;
			StringBuilder strBuilder = new StringBuilder();
			while ((line = readStream.readLine()) != null) {
				strBuilder.append(line + "\r\n");
			}
			String response = strBuilder.toString();
			
			documentlist = requestparser.getDocumentResponseParser(response);
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
	
	private void downloadSingleDocument(Socket socket, String document, String destination) throws IOException {
		File saveDirectory = new File(destination);
		File saveFile = new File(saveDirectory, document);
		
		writeStream = new PrintWriter(socket.getOutputStream());
		String requestBody = document;
		requestSender.sendDocumentRequest(writeStream, requestBody); 
		socket.shutdownOutput();
	
		readFile = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
		writeFile = new BufferedOutputStream(new FileOutputStream(saveFile));
		
		byte[] temp = new byte[1024];
		int length = 0;
		
		while((length = readFile.read(temp)) > 0){
			writeFile.write(temp , 0, length);
		}
		System.out.println("파일 다운로드 완료: "+destination);
		writeFile.flush();
	}

	public void downloadDocuments(String document, String destination) {
		Socket socket = null;

		ArrayList<String> materials = new ArrayList<String>();
		materials.add("03-Architecture-Design Principles(15)-v1.pdf");
		materials.add("07-JPattV1-Ch5-Partitioning Patterns V03-221011.pdf");
		materials.add("Chapter3 Threads.key.pdf");
		
		for(int i = 0; i < materials.size(); i++) {
			try {
				socket = new Socket("localhost", 3333);
				downloadSingleDocument(socket, document, destination);
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
