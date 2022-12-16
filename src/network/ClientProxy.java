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
import java.util.ArrayList;

import data.DocumentList;

public class ClientProxy {
	private DocumentRequestSender drs;
	private BufferedReader br;
	private PrintWriter pw;
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	
	private int port = 3333;
	private String serverURL = "localhost";
	private DocumentList documentlist;
	private RequestParser requestparser;
	private String DocumentType = "";
	
	public static void main(String[] args) {
		ClientProxy service = new ClientProxy();
		boolean result = service.login("peterjr123", "peterjr123!");
		System.out.println("로그인 결과: " + result);
		
//		service.downloadDocument(null, "C:\\Users\\joon\\eclipse-workspace\\SoftwareArchitectureTeamproject");
//		service.downloadDocument(null, "C:\\Users\\82103\\Desktop\\client");
		
		DocumentList list = service.getDocumentList("강의자료");
		for(int i = 0; i < list.length(); i++) {
			System.out.println(list.getCourse(i) + ": " + list.getDocumentName(i));
		}
	}
	
	public ClientProxy() {
		this.drs = new DocumentRequestSender(this.serverURL);
		this.documentlist = new DocumentList();
		this.requestparser = new RequestParser();
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
			
			boolean result = requestparser.loginResponseParser(response);
			return result;
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
		
		this.DocumentType = documentType;
		
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(socket.getOutputStream());

			// server에게 request 보냄.
			drs.sendDocumentListRequest(pw, this.DocumentType); //documenttype :강의자료... etc
			socket.shutdownOutput();
			
			String line = null;
			StringBuilder s = new StringBuilder();

			while ((line = br.readLine()) != null) {
				s.append(line + "\r\n");
			}

			String response = s.toString();
			
			
			this.documentlist = requestparser.getDocumentResponseParser(response);
			
			return this.documentlist;

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
	

	// dest: C: ~ 건국대학교/4-2
	// dest + 과목(변수)/강의자료/material(변수)
	
	public void downloadDocument(DocumentList documentlist, String dest) {
		Socket socket = null;
		File saveDir = null;
		File saveFile = null;
		
		DocumentList dl = documentlist;
		
		ArrayList<String> materials = new ArrayList<String>();
		materials.add("03-Architecture-Design Principles(15)-v1.pdf");
//		materials.add("07-JPattV1-Ch5-Partitioning Patterns V03-221011.pdf");
//		materials.add("Chapter3 Threads.key.pdf");
		
		for(int i = 0; i < materials.size(); i++) {
			try {
				socket = new Socket("localhost", 3333);
				pw = new PrintWriter(socket.getOutputStream());
				
				String requestBody = materials.get(i);
				
				drs.sendDocumentRequest(pw, requestBody); 
				socket.shutdownOutput();
				
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				
				String destination = dest;
				saveDir = new File(destination);
				saveFile = new File(saveDir, materials.get(i));
				bis = new BufferedInputStream(dis);
				bos = new BufferedOutputStream(new FileOutputStream(saveFile));
				
				byte[] temp = new byte[1024];
				int length = 0;
				
				while((length = bis.read(temp)) > 0){
					bos.write(temp , 0, length); //지정한 경로에 받아오기.
				}
				System.out.println("파일 다운로드 완료: "+dest);
				bos.flush();
				
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
		}
	}

}
