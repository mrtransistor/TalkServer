import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class AskGui {
	JFrame askFrame;
	JLabel askLabel;
	JTextField answerTextField;
	String answerOfUser = "";
	String tempNameOfFrame = "";
	String questionString = "";
	String sessionPw;
	String defaultValue = "";
	
	public AskGui(String nameOfFrame, String questionToUser, String defaultValue) {
		tempNameOfFrame = nameOfFrame;
		questionString = questionToUser;
		System.out.println("Huhu:" + defaultValue);
		this.defaultValue = defaultValue;
		sessionPw = askUser();

	}
	
	public String askUser() {
			System.out.println("Leer?" + answerOfUser.isEmpty());
			askFrame = new JFrame(tempNameOfFrame);
			askFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			askLabel = new JLabel(questionString);
			answerTextField = new JTextField();
			answerTextField.setText(this.defaultValue);
			answerTextField.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent event){
							answerOfUser = answerTextField.getText();
							//System.out.println("IP: " + hostAddress);
							answerTextField.setText("Danke für ihre Eingabe");
							answerTextField.setEditable(false);
							askFrame.dispose();
						}
					});
			askFrame.add(askLabel,BorderLayout.NORTH);
			askFrame.add(answerTextField, BorderLayout.SOUTH);
			askFrame.setSize(350, 150);
			askFrame.pack();
			askFrame.setLocation(175, 150);
			askFrame.setVisible(true);
			System.out.println(askFrame.isActive());
			while(answerOfUser.isEmpty()) { //System.out.println("Leer: " + hostAddress.isEmpty());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			return answerOfUser;
	}
}
