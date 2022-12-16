package client;

import java.util.Scanner;

public class Console {
	private Scanner scanner = new Scanner(System.in);
	
	public int selectJob() {
		System.out.println("수행할 작업의 번호를 입력하세요.");
		System.out.println("1) 문서 다운로드");
		System.out.println("2) 동영상 자동재생");
		
		Scanner userInput = new Scanner(System.in);
		int selectedJob = userInput.nextInt();
		
		return selectedJob;
	}
	public String getNewRootDirectoryPath() {
		System.out.println("문서가 다운로드될 경로를 입력해 주십시오: ");
		String newPath = scanner.nextLine();
		System.out.println();
		return newPath;
	}
	
	public String confirmRootDirectoryPath(String currentPath) {
		System.out.println("문서 다운로드 경로: " + currentPath);
		System.out.println("해당 경로에 문서가 다운로드 됩니다.");
		System.out.print("해당 경로로 진행하시겠습니까? (Y/N) : ");
		String userConfirm = scanner.nextLine();
		System.out.println();
		
		if(userConfirm == "N" || userConfirm == "n") {
			return getNewRootDirectoryPath();
		}
		return currentPath;
	}
	
	public void cannotFindRootDirectory(String path) {
		System.out.println("ERROR: 디렉토리 경로가 알맞은지 확인해주세요.");
		System.out.println("경로: " + path);
		System.out.println();
		System.out.println("프로그램을 재시작하고, 새로운 경로를 입력해 주세요.");
	}

}
