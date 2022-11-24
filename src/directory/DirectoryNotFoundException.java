package directory;

public class DirectoryNotFoundException extends RuntimeException{
	public DirectoryNotFoundException() {
		
	}
	public DirectoryNotFoundException(String message) {
		super(message);
	}
}
