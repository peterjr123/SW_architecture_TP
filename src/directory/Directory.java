package directory;
import java.io.File;
import java.util.ArrayList;

public class Directory {
	private ArrayList<Directory> subDirectories = new ArrayList<Directory>();
	private String name;
	private String path;
	
	public Directory(String path) {
		File directory = new File(path);
		if(directory.exists() && directory.isDirectory()) {
			this.name = directory.getName();
			this.path = path;
		}
		else {
			throw new DirectoryNotFoundException("cannot find directory of given path");
		}
		
		// 하위 디렉토리 생성 
		File[] fileList = directory.listFiles();
		if(fileList != null) {
			for(int i = 0; i < fileList.length; i++) {
				if(fileList[i].isDirectory()) {
					Directory subDirectory = new Directory(fileList[i].getPath());
					addExistSubDirectory(subDirectory);
				}
			}
		}
	}
	
	
	public boolean createSubDirectory(String name) {
		File newDirectory = new File(this.path, name);
		if(!newDirectory.exists() && !newDirectory.isFile()) {
			if(newDirectory.mkdir()) {
				addExistSubDirectory(new Directory(newDirectory.getPath()));
				return true;
			}
		}
		return false;
	}
	
	public DirectoryIterator getSubDirectoryIterator() {
		return new DirectoryIterator(subDirectories);
	}

	
	private void addExistSubDirectory(Directory subDirectory) {
		// TODO Auto-generated method stub
		subDirectories.add(subDirectory);
	}
	
	// Getters
	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
	
	// Override methods
	@Override
	public String toString() {
		String str;
		if(subDirectories.isEmpty()) {
			str = this.name;
		}
		else {
			str = this.name + ": ";
			str += subDirectories.toString();
		}
		
		return str;
	}
}
