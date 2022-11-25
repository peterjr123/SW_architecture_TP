package directory;

import java.util.ArrayList;

public class DirectoryIterator implements Iterator{
	private ArrayList<Directory> subDirectories;
	private int currentIndex = 0;
	
	public DirectoryIterator(ArrayList<Directory> subDirectories) {
		this.subDirectories = subDirectories;
	}

	@Override
	public boolean hasNext() {
		return currentIndex < subDirectories.size();
	}

	@Override
	public Directory next() {
		currentIndex++;
		return subDirectories.get(currentIndex - 1);
		
	}

}
