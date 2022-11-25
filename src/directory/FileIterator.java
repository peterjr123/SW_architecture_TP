package directory;

import java.util.ArrayList;

public class FileIterator implements Iterator {
	private ArrayList<String> fileList = new ArrayList<String>();
	private int currentIdx = 0;
	
	public FileIterator(ArrayList<String> fileList) {
		this.fileList = fileList;
	}

	@Override
	public boolean hasNext() {
		return currentIdx < fileList.size();
	}

	@Override
	public String next() {
		// TODO Auto-generated method stub
		++currentIdx;
		return fileList.get(currentIdx);
	}
}
