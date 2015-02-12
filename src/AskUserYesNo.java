import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class AskUserYesNo {
	
	private String globalQuestion = "";
	private String globalNameOfFrame = "";
	boolean getKillGui = true;
	boolean getBool = true;
	
	public AskUserYesNo(String question, String nameOfFrame) {
		globalNameOfFrame = nameOfFrame;
		globalQuestion = question;
	}
	
	
	public boolean printGui() {
		
		JFrame askFrame = new JFrame(globalNameOfFrame);
		JLabel askLabel = new JLabel(globalQuestion);
		JButton yesButton = new JButton("Ja");
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				getKillGui = false;
				getBool = true;
			}
		});
		JButton noButton = new JButton("Nein");
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				getKillGui = false;
				getBool = false;
			}
		});
    	JPanel buttonPanel = new JPanel();
    	buttonPanel.setLayout( new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    	buttonPanel.add(yesButton);
    	buttonPanel.add(noButton);
    	buttonPanel.setVisible(true);
    	askFrame.add(askLabel, BorderLayout.PAGE_START);
    	askFrame.add(buttonPanel, BorderLayout.PAGE_END);
    	askFrame.setSize(175, 50);
    	askFrame.setLocation(250, 250);
    	askFrame.setVisible(true);
		while(getKillGui) {
			System.out.println("warten....");
		}
		askFrame.dispose();
		return getBool;
	}
	

}
