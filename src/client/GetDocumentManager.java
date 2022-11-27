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
		
		DocumentList ecampusDocumentList = getDocumentList();
		
		ecampusDocumentList.addDocument("소프트웨어 아키텍처", "강의자료1.pdf");
		ecampusDocumentList.addDocument("소프트웨어 아키텍처", "강의자료2.pdf");
		ecampusDocumentList.addDocument("네트워크 프로그래밍", "강의자료3.pdf");
		
		DocumentList missingDocumentList = findMissingDocument(ecampusDocumentList, "강의자료");
		
		for(int i = 0; i < missingDocumentList.length(); i++) {
			System.out.println(missingDocumentList.getCourse(i) + ": " + missingDocumentList.getDocumentName(i));
		}
	}
	
	
	
	private DocumentList findMissingDocument(DocumentList ecampusDocumentList, String documentType) {
		// course별로 ecampus 파일들을 1차적으로 분류한다.
		HashMap<String, DocumentList> classifiedDocumentList = classifyDocumentListByCourse(ecampusDocumentList);
		
		// 비교 대상 파일들이 담긴 디렉토리를 골라서 documentTypeDirectory에 마찬가지로 course별로 넣는다.
		HashMap<String, Directory> classifiedDocumentTypeDirectory = classifyDocumentTypeDirectoryByCourse(root, documentType);
		
		// classifiedDocumentTypeDirectory와 classifiedDocumentList의 key값은 과목이름이다. -> key값을 비교하면 존재하지 않는 과목 디렉토리를 알아낼 수 있다.
		// 존재하지 않는 과목 디렉토리를 생성한다.
		for(String courseName : classifiedDocumentList.keySet()) {
			if(!classifiedDocumentTypeDirectory.containsKey(courseName)) {
				createCourseDirectoryTree(courseName, new String[] { documentType });
			}
		}
		
		// 위 정보를 바탕으로 존재하지 않는 문서 list를 작성한다.
		DocumentList missingDocumentList = findMissingDocumentImpl(classifiedDocumentList, classifiedDocumentTypeDirectory);
		
		
		return missingDocumentList;
	}
	
	private DocumentList findMissingDocumentImpl(HashMap<String, DocumentList> classifiedDocumentList,
			HashMap<String, Directory> classifiedDocumentTypeDirectory) {
		
		DocumentList missingDocumentList = new DocumentList();
		
		for(String courseName : classifiedDocumentList.keySet()) {
			if(classifiedDocumentTypeDirectory.containsKey(courseName)) {
				// 과목 디렉토리가 존재하는 경우에, 해당 과목 디렉토리를 탐색하여 대응되는 파일이 존재하는지 검사한뒤 존재하지 않으면 missingDocumentList에 추가한다.
				DocumentList courseDocument = classifiedDocumentList.get(courseName);
				for(int i = 0; i < courseDocument.length(); i++) {
					String fileName = courseDocument.getDocumentName(i);
					
					if(!classifiedDocumentTypeDirectory.get(courseName).hasFile(fileName))
						missingDocumentList.addDocument(courseName, fileName);
				}
			}
			else {
				// 과목 디렉토리가 존재하지 않는 경우는 전부다 missingDocumentList에 추가하기
				DocumentList courseDocument = classifiedDocumentList.get(courseName);
				for(int i = 0; i < courseDocument.length(); i++) {
					missingDocumentList.addDocument(courseName, courseDocument.getDocumentName(i));
				}
			}
		}
		return missingDocumentList;
	}

	private void createCourseDirectoryTree(String courseName, String documentTypes[]) {
		root.createSubDirectory(courseName);
		
		Iterator iterator = root.getSubDirectoryIterator();
		while(iterator.hasNext()) {
			Directory dir = (Directory) iterator.next();
			if(dir.getName().equals(courseName)) {
				for(String type : documentTypes) {
					dir.createSubDirectory(type);
				}
			}
		}
	}

	private HashMap<String, Directory> classifyDocumentTypeDirectoryByCourse(Directory root, String documentType) {
		HashMap<String, Directory> documentTypeDirectory = new HashMap<String, Directory>();
		
		Iterator courseDirectoryIterator = root.getSubDirectoryIterator();
		while(courseDirectoryIterator.hasNext()) {
			Directory courseDirectory = (Directory) courseDirectoryIterator.next();
			Iterator docTypeIterator = courseDirectory.getSubDirectoryIterator();
			
			while(docTypeIterator.hasNext()) {
				Directory docTypeDirectory = (Directory) docTypeIterator.next();
				
				if(docTypeDirectory.getName().equals(documentType))
					documentTypeDirectory.put(courseDirectory.getName(), docTypeDirectory);
			}
		}
		return documentTypeDirectory;
	}
	
	private HashMap<String, DocumentList> classifyDocumentListByCourse(DocumentList documentList) {
		HashMap<String, DocumentList> classifiedList = new HashMap<String, DocumentList>();
		for(int i = 0; i < documentList.length(); i++) {
			String courseName = documentList.getCourse(i);
			String docName = documentList.getDocumentName(i);
			if(classifiedList.containsKey(courseName)) {
				classifiedList.get(courseName).addDocument(courseName, docName);
			}
			else {
				DocumentList listByCourse = new DocumentList();
				listByCourse.addDocument(courseName, docName);
				classifiedList.put(courseName, listByCourse);
			}
		}
		return classifiedList;
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
		return new DocumentList();
	}
	
	private int getDocument() {
		return 0;
	}
}
