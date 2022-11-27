package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import data.DocumentList;
import directory.Directory;
import directory.DirectoryNotFoundException;
import directory.Iterator;
import network.ClientProxy;

public class GetDocumentManager {
	private Directory root;
	private ClientProxy service;
	
	public GetDocumentManager(ClientProxy service) {
		this.service = service;
	}
	
	public void start() {
		setDirectory("./테스트용/");
		
//		DocumentList ecampusDocumentList = getDocumentList();
		DocumentList ecampusDocumentList = new DocumentList();
		
		ecampusDocumentList.addDocument("소프트웨어 아키텍처", "강의자료1.pdf");
		ecampusDocumentList.addDocument("소프트웨어 아키텍처", "강의자료2.pdf");
		ecampusDocumentList.addDocument("네트워크 프로그래밍", "강의자료3.pdf");
		
		DocumentList missingDocumentList = findMissingDocument(ecampusDocumentList, "강의자료");
		
		for(int i = 0; i < missingDocumentList.length(); i++) {
			System.out.println(missingDocumentList.getCourse(i) + ": " + missingDocumentList.getDocumentName(i));
		}
	}
	
	private DocumentList findMissingDocument(DocumentList ecampusDocumentList, String documentType) {
		Iterator courseDirectoryIterator = root.getSubDirectoryIterator();
		HashMap<String, Directory> targetFileDirectory = new HashMap<String, Directory>();
		DocumentList missingDocumentList = new DocumentList();
		
		// course별로 파일들을 1차적으로 분류한다.
		HashMap<String, DocumentList> classifiedList = new HashMap<String, DocumentList>();
		for(int i = 0; i < ecampusDocumentList.length(); i++) {
			String courseName = ecampusDocumentList.getCourse(i);
			String docName = ecampusDocumentList.getDocumentName(i);
			if(classifiedList.containsKey(courseName)) {
				classifiedList.get(courseName).addDocument(courseName, docName);
			}
			else {
				DocumentList listByCourse = new DocumentList();
				listByCourse.addDocument(courseName, docName);
				classifiedList.put(courseName, listByCourse);
			}
		}
		
		// 비교 대상 파일들이 담긴 디렉토리를 골라서 targetFileDirectory에 마찬가지로 course별로 넣는다.
		while(courseDirectoryIterator.hasNext()) {
			Directory courseDirectory = (Directory) courseDirectoryIterator.next();
			Iterator docTypeIterator = courseDirectory.getSubDirectoryIterator();
			while(docTypeIterator.hasNext()) {
				Directory docTypeDirectory = (Directory) docTypeIterator.next();
				
				if(docTypeDirectory.getName().equals(documentType))
					targetFileDirectory.put(courseDirectory.getName(), docTypeDirectory);
			}
		}
		
		// 모든 ecampus document에 대해서 targetFileDirectory안의 file 중 동일한 파일이 있는지 검사한다.
		// 검사결과 동일한 파일이 존재하지 않으면 missingFileDocumentList에 추가한다.
		for(String courseName : classifiedList.keySet()) {
			if(targetFileDirectory.containsKey(courseName)) {
				DocumentList courseDocument = classifiedList.get(courseName);
				for(int i = 0; i < courseDocument.length(); i++) {
					String fileName = courseDocument.getDocumentName(i);
					Iterator files = targetFileDirectory.get(courseName).getSubFileIterator();
					boolean hasFile = false;
					while(files.hasNext()) {
						if(((String)files.next()).equals(fileName)) {
							hasFile = true;
							break;
						}
					}
					
					if(!hasFile)
						missingDocumentList.addDocument(courseName, fileName);
				}
			}
			else {
				// 디렉토리를 생성하고, missingDocumentList에 추가하기
				root.createSubDirectory(courseName);
				Iterator iterator = root.getSubDirectoryIterator();
				while(iterator.hasNext()) {
					Directory dir = (Directory) iterator.next();
					if(dir.getName().equals(courseName)) {
						dir.createSubDirectory(documentType);
					}
				}
				
				DocumentList courseDocument = classifiedList.get(courseName);
				for(int i = 0; i < courseDocument.length(); i++) {
					missingDocumentList.addDocument(courseName, courseDocument.getDocumentName(i));
				}
			}
		}
		
		return missingDocumentList;
	}

	private boolean setDirectory(String rootDirectoryPath) {
		try {
			root = new Directory(rootDirectoryPath);
			return true;
		} catch (DirectoryNotFoundException e) {
			return false;
		}
	}
	
	private DocumentList getDocumentList() {
		return null;
	}
	
	private int getDocument() {
		return 0;
	}
}
