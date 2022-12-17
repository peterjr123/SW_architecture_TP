package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

import data.DocumentList;

public class ClientProxy {
	private DocumentRequestSender requestSender;
	private ResponseParser responseparser;
	
	private int port = 3333;
	private String serverURL = "localhost";
	
	public ClientProxy() {
		this.requestSender = new DocumentRequestSender(this.serverURL);
		this.responseparser = new ResponseParser();
	}
	
	private String loginImpl(BufferedReader readStream, PrintWriter writeStream, String id, String password) throws IOException {

		// server에게 request 보냄.
		requestSender.sendLoginRequest(writeStream, id, password);
		
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
			
			Boolean result = responseparser.loginResponseParser(response);
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
		// server에게 request 보냄.
			requestSender.sendDocumentListRequest(writeStream, documentType); //documenttype :강의자료... etc
			
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
			
			documentlist = responseparser.getDocumentResponseParser(response);
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
	
	private void downloadSingleDocument(PrintWriter writeStream, BufferedInputStream readFile, BufferedOutputStream writeFile, String document, String destination) throws IOException {
		
		File saveDirectory = new File(destination);
		File saveFile = new File(saveDirectory, document);
		
		String requestBody = document;
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

	public void downloadDocuments(DocumentList documentList, String destination) {
		for(int i = 0; i < documentList.length(); i++) {
			Socket socket = null;
			PrintWriter writeStream = null;
			BufferedInputStream readFile = null;
			BufferedOutputStream writeFile = null;
			
			try {
				socket = new Socket("localhost", 3333);
				writeStream = new PrintWriter(socket.getOutputStream());
				readFile = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
				String documentName = documentList.getDocumentName(i);
				downloadSingleDocument(writeStream, readFile, writeFile, documentName, destination);
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
