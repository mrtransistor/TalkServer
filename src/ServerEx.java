import javax.swing.JFrame;

public class ServerEx {

	public static void main(String[] args) {
		TalkServer tester = new TalkServer();
		tester.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tester.startServer();
	}
}

