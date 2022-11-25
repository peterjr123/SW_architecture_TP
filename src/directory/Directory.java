package directory;
import java.io.File;
import java.util.ArrayList;

public class Directory {
	private ArrayList<Directory> subDirectories = new ArrayList<Directory>();
	private String name;
	private String path;
	
	public Directory(String path) throws DirectoryNotFoundException {
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
	
	private boolean createSubDirectoryImpl(String name) throws DirectoryNotFoundException {
		File newDirectory = new File(this.path, name);
		if(!newDirectory.exists() && !newDirectory.isFile()) {
			if(newDirectory.mkdir()) {
				addExistSubDirectory(new Directory(newDirectory.getPath()));
				return true;
			}
		}
		return false;
	}
	
	public boolean createSubDirectory(String name) {
		boolean result = false;
		try {
			result = createSubDirectoryImpl(name);
		}
		catch(DirectoryNotFoundException ex) {
			ex.printStackTrace();
		}
		return result;
	}
	
	public DirectoryIterator getSubDirectoryIterator() {
		return new DirectoryIterator(subDirectories);
	}
	
	public FileIterator getSubFileIterator() {
		File currFile = new File(path);
		File subFiles[] = currFile.listFiles();
		ArrayList<String> subFileName = new ArrayList<String>();
		
		for(int i = 0; i < subFiles.length; i++) {
			if(!(subFiles[i].isDirectory())) { // 현 디렉토리의 하위 파일들에 대해서만 처리
				subFileName.add(subFiles[i].getName());
			}
		}
		FileIterator fileIterator = new FileIterator(subFileName);
		
		return fileIterator;
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
