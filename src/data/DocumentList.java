package data;

import java.util.ArrayList;

public class DocumentList {
	private ArrayList<String> documentCourseName = new ArrayList<String>();
	private ArrayList<String> documentName = new ArrayList<String>();
	
	public void addDocument(String course, String name) {
		documentCourseName.add(course);
		documentName.add(name);
	}
	
	public int length() {
		return documentCourseName.size();
	}
	
	public String getCourse(int idx) {
		return documentCourseName.get(idx);
	}
	
	public String getDocumentName(int idx) {
		return documentName.get(idx);
	}
}
