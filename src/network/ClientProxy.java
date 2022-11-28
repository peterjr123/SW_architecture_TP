package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import data.DocumentList;

public class ClientProxy {
	private DocumentRequestSender drs;
	private BufferedReader br;
	private PrintWriter pw;
	private ObjectInputStream ois;
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	
	private int port = 80;
	private String serverURL = "localhost";
		
	public ClientProxy() {
		this.drs = new DocumentRequestSender(this.serverURL);
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
			ois = new ObjectInputStream(socket.getInputStream());
			pw = new PrintWriter(socket.getOutputStream());

			// server에게 request 보냄.
			drs.sendDocumentListRequest(pw, documentType); //documenttype :강의자료... etc
			socket.shutdownOutput();
			
			DocumentList documentlist = (DocumentList)ois.readObject();
			
			if(documentlist != null) {
				return documentlist;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
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
	
	public void downloadDocument(String name, String dest) {
		Socket socket = null;
		try {
			socket = new Socket(serverURL, port);
			pw = new PrintWriter(socket.getOutputStream());
			
			// server에게 request 보냄. request 보낼 때 string(json)으로 보낼까?
			drs.sendDocumentRequest(pw, name); 
			socket.shutdownOutput();
			
			File saveDir = new File(dest);
			File saveFile = new File(saveDir,name);
			
			/*
			 * 파일 받아오면 올바른 경로에 파일 넣기.
			 * */
			bos = new BufferedOutputStream(new FileOutputStream(saveFile));
			bis = new BufferedInputStream(new DataInputStream(socket.getInputStream()));
			byte[] temp = new byte[1024];
			int length = 0;
			
			while((length = bis.read(temp)) > 0){
				bos.write(temp , 0, length); //지정한 경로에 받아오기.
			}
			bos.flush();

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
	}
	
	

}
