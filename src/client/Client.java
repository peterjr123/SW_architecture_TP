package client;
import java.util.Scanner;

import network.ClientProxy;

public class Client {
	private static ClientProxy service = new ClientProxy();
	private static Console console = new Console();
	
	public static void main(String args[]) {
		int selectedJob = console.selectJob();
		
		selectJob(selectedJob);
	}
	
	private static void selectJob(int jobNum) {
		if(jobNum == 1) {
			requestUserLogin();
			
			GetDocumentManager manager = new GetDocumentManager(service);
			manager.getDocument();
		}
		else if(jobNum == 2) {
			// TODO: 새로운 manager를 생성해서 간단한 기능을 보여주기
		}
		else {
			
		}
	}
	
	private static void requestUserLogin() {
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
		System.out.println();
	}
}
