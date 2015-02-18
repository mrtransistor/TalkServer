import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;



public class ServerGui {
	
	public JFrame chatGUI;
	/**Eingabefeld des Chatfensters */
	private JTextField userText; 
	/**Chatfenster */
	private JTextPane chatWindow;
	/**Send Button */
	private JButton buttonSend;
	/**Verschüsseltes Senden Button */
	private JButton buttonEncryptedSend;
	/**Fire Button */
	private JButton buttonFire;
	public String serverMessage;
	
	public ServerGui() {
		drawServerGui();
	}
	
	private void serverSend(final String message) {
		new Runnable() {
			public void run() {
				serverMessage = message;
			}
		};
		
	}
	
	/**
	 * private void drawServerGui() zeichnet ServerGui und wartet auf IOStreams
	 */
	private void drawServerGui() {
		
		chatGUI = new JFrame("ChatServer");
		chatGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userText = new JTextField();
		userText.setEditable(false);
		//userText.setSize(550, 30);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event){
						showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + userText.getText());
						userText.setText("");
					}
				}
		);
		//Chatwindow erzeugen
		chatWindow = new JTextPane();
		chatWindow.setContentType("text/html");
		chatWindow.setEditorKit(new HTMLEditorKit());
		chatWindow.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatWindow.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		//Send Button
		buttonSend = new JButton( "Send" );
		buttonSend.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				//userText.getText(); // Eingabe holen
				System.out.println("sendMessage: " + userText.getText());
				showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + userText.getText());
				serverSend("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + userText.getText());
				userText.setText(""); //reset Textfeld
				}
			});
		        
		//Encrypted SendButton
        buttonEncryptedSend = new JButton("crypt Send");
        buttonEncryptedSend.addActionListener( new ActionListener() {
        	public void actionPerformed( ActionEvent event ) {		 
        			showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + userText.getText());
		  			userText.setText(""); //reset Texteingabefeld
		  		  }
		  	} );
		        
        //Encrypted Fire Fire Fire Fire
		buttonFire = new JButton("Fire");
		buttonFire.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
		    	//sendMessage(userText.getText() ); // Eingabe holen
		    	userText.setText(""); //reset Texteingabefeld
		    	}
		    });
		
		JPanel buttonPanel = new JPanel();
    	buttonPanel.setLayout( new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    	buttonPanel.add(buttonSend);
    	buttonPanel.add(buttonEncryptedSend);
    	buttonPanel.add(buttonFire);
    	buttonPanel.setVisible(true);
		    	
    	//EingabeFeld hinzufügen zu JFrame
    	chatGUI.add(userText, BorderLayout.PAGE_START);
    	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,buttonPanel,new JScrollPane(chatWindow) );
		splitPane.setDividerLocation(115);
		chatGUI.add(splitPane, BorderLayout.CENTER);  //ChatWindow hinzufügen
		chatGUI.setLocation(175, 175);
		chatGUI.setSize(550, 280);
		chatGUI.setResizable(false);
		chatGUI.setVisible(true);
	}

	/**
	 * Eingaben des Users freischalten/abschalten
	 * @param tof
	 */
	public void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tof); //true/false übergeben an Textfeldstatus
					}
				}
		);
	}
	/**
	 * Nachricht in ChatWindow anzeigen
	 * @param text
	 */
	public void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run()  {
						//Dokument aus chatwindow holen und ablegen
						Document doc  = chatWindow.getDocument();
						try {
							//Mittels HTMLEditorkit doc (Dokument) als HTMLDocument am Ende (doc.getLength()) des Dokmuentes anhängen.
							new HTMLEditorKit().insertHTML((HTMLDocument) doc, doc.getLength(), text, 0,0, null);
						} catch (BadLocationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			
					}
				}
			);
	}
	
}
