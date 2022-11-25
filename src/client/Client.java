package client;
import java.util.Scanner;

import network.ClientProxy;

public class Client {
	private static ClientProxy service = new ClientProxy();
	
	public static void main(String args[]) {
		System.out.println("수행할 작업의 번호를 입력하세요.");
		System.out.println("1) 문서 다운로드");
		System.out.println("2) 동영상 자동재생");
		
		Scanner userInput = new Scanner(System.in);
		int selectedJob = userInput.nextInt();
		
		if(selectedJob == 1) {
			requestUserLogin();
		}
		
		userInput.close();
	}
	
	public static void requestUserLogin() {
		Scanner userInput = new Scanner(System.in);
		
		System.out.println("해당 기능은 ecampus로그인이 필요합니다.");
		System.out.println("ecampus ID와 PW를 입력해주세요.");
		
		System.out.print("ID: ");
		String id = userInput.nextLine();
		System.out.print("PW: ");
		String pw = userInput.nextLine();
		
		if(service.login(id, pw)) {
			System.out.println("로그인에 성공했습니다.");
		}
		else {
			System.out.println("로그인에 실패하셨습니다.");
			System.out.println();
			requestUserLogin();
		}
	}
}
