package network;

import data.DocumentList;

public class RequestParser {
	
	// 올바른 Response일 때만 로그인 이벤트 처리
	public boolean loginResponseParser(String response) {
		
		if (response.indexOf("HTTP/") != -1) {
			if (response.indexOf("200 OK") != -1) {
				return true;
			}
		}
		return false;
	}
	
	public DocumentList getDocumentResponseParser(String response) {
		DocumentList documentlist = new DocumentList();
		
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
	}
}
