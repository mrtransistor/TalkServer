import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;


public class TalkServer {
	
	/**serialVersionUID */
	private static final long serialVersionUID = 1L;
	

	private JFrame chatGUI;
	/**Eingabefeld des Chatfensters */
	private JTextField userText; 
	/**Chatfenster */
	private JTextArea chatWindow;
	/**Send Button */
	private JButton buttonSend;
	/**Verschüsseltes Senden Button */
	private JButton buttonEncryptedSend;
	/**Fire Button */
	private JButton buttonFire;
	/** Ausgabestream für Netzwerkübertragung*/
	private ObjectOutputStream transmitConnectionInfoToClient;
	/** Eingangsstream für Netzwerkübertragung */
	private ObjectInputStream input;
	/**ServerSocket Object */
	private ServerSocket serverConnectionListener = null;
	/** ClientSocket Object*/
	private Socket connection = null;
	/**Arrays der Sessions zwischen Server und Client*/
	private Socket[] sessionArray = new Socket[10];
	
	//Entscheidung über Sender
	volatile boolean sender = false;
	//Entscheidung für crypted Send
	volatile boolean xSender = false;
	volatile String sendMessage = "";
	
	//Globaler SubKey
	BigInteger globalKey;
	
	/** Array verfügbarer Ports */
	boolean[] availablePorts = {true,true,true,true,true,true,true,true,true,true};
	/**KryptoServer - Kryptomodul für Datenübertragung*/
	RSAModule rsaModule;
	
	/** */
	AskUserYesNo killHostPrompt;
	/** Erzeugt Objekt der Klasse TalkServer, welches alle Funktionalitäten  
	 * der Klasse zur Verfügung stellt und explizit die Anwenderoberfläche erzeugt.
	 * Der Konstruktor erzeugt auch ein neues Objekt der Klasse KryptoServer 
	 * welches die Verschlüsselung während der Übertragung übernimmt und weitere 
	 * Kryptofunktionalitäten liefert 
	 * @param void - keine
	 */
	public TalkServer() {
		
		rsaModule= new RSAModule(); //Kryptomodul erzeugen
		System.out.println("e: " + rsaModule.e);
		System.out.println("d: " + rsaModule.d);
		System.out.println("n: " + rsaModule.n);
		drawServerGui();
		drawAdminWindow();
		startServer();
	}
	
	JTextArea printAreaAdminBox;
	JScrollPane textScroll;
	

	/**
	 * Zeichne Adminbox
	 */
	private void drawAdminWindow() {
		JFrame adminFrame = new JFrame("AdminBox");
		adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		printAreaAdminBox = new JTextArea();
		printAreaAdminBox.setSize(300, 200);
		printAreaAdminBox.setVisible(true);
		textScroll = new JScrollPane(printAreaAdminBox);
		textScroll.setVisible(true);
		adminFrame.add(textScroll);
		adminFrame.setSize(300, 200);
		adminFrame.setLocation(725, 265);
		adminFrame.setVisible(true);
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
						//sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		//Chatwindow erzeugen
		chatWindow = new JTextArea();
		//chatWindow.setSize(550, 200);
				
		//Send Button
		buttonSend = new JButton( "Send" );
		buttonSend.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
				sendMessage = userText.getText(); // Eingabe holen
				sender = true;
				System.out.println("sendMessage: " + sendMessage);
				//showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + userText.getText());
				userText.setText(""); //reset Textfeld
				}
			});
		        
		//Encrypted SendButton
        buttonEncryptedSend = new JButton("crypt Send");
        buttonEncryptedSend.addActionListener( new ActionListener() {
        	public void actionPerformed( ActionEvent event ) {		 
        			sendMessage = userText.getText();
		  			xSender = true;
        			//sendMessage = "";
		  			//sendMessageEncrypted(userText.getText()); // Eingabe holen
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
		chatGUI.setVisible(true);
	}
	
	/** startServer() startet die eigentliche Funktionalität des Servers.
	 * Die Funktion läuft endlos bis die Schleife per Usereingabe false wird.
	 * @param - keine
	 * @return void - keine
	 */
	public void startServer() {
		boolean alive = true;
		try {
			aufVerbindungWarten();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*startIOStreams();*/
		//whileSharingData();
		while(true) { }							
	//showMessage("Server Shutdown... bye");
	}
	
	/**aufVerbindungenWarten() lauscht auf dem Serversocket bis eine Verbindung vom 
	 * Client aufgebaut wird. Stellt dann die Verbindung her und gibt bei Erfolg 
	 * eine Nachricht im Chatfenster aus (chatWindow)
	 * @return void - keine
	*/ 
	private void aufVerbindungWarten()  throws IOException {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		
		Runnable serverListener = new Runnable() {
			public void run() {
				try {
				serverConnectionListener = new ServerSocket(3336,10);
				
					while(true) {
						showMessageAdmin("\nauf verbindung warten..."); 
						Socket clientSocket = serverConnectionListener.accept();
						clientProcessingPool.submit(new ClientTask(clientSocket));
						/**@Override*/	//Später Ausgaben von ShowMessage in VerbinundungsdatensAnzeigefeld ausgeben lassen
						showMessageAdmin("\nverbunden zu " + clientSocket.getInetAddress().getHostAddress());	
						System.out.println("verbunden");
						writeLogFile("\nverbunden zu " + clientSocket.getInetAddress().getHostAddress(), new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		};
		Thread ListenerThread = new Thread(serverListener);
		ListenerThread.start();	
	}

	
	/**
	 *Die Klasse ClientTask ist parallel ausführbar und stellt die gesamte
	 *Server-Client[Thread] Funktionalität zur Verfügung. Die Klasse leitet 
	 *Ein- und Ausgaben an die GUI weiter. 
	 * @author mrt & sticklobot
	 *
	 */
	private class ClientTask implements Runnable {
		/** Port mit Clienverbindung */
		private final Socket clientSocket;
		/**InputStream für Clientverindung */
		ObjectInputStream sessionInputStream;
		/**Outputstream für Clientverbindung */
		ObjectOutputStream sessionOutputStream;
		/**KryptoServer clientKrypto*/
		KryptoServer clientKrypto;
		String inputMessage;
		String outputMessage;
		
		/** Konstruktor */
		private ClientTask(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		public void run() throws RuntimeException {
			boolean schalter = true;
			//STREAMS ERSTELLEN UND RSA-KRYPTO TAUSCHT SUBKEY AUS
				setupStreamsAndKrypto();
				Thread outputThread = new Thread(new OutputStreamTask());
				outputThread.start();
			//WHILE CHATTING
				while(schalter) {
					try {
						schalter = whileSharingData();
						System.out.println("Schalter:" + schalter);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			//VERBINDUNG ZUM CLIENT BEENDEN
			try {
				clientSocket.close();
				sessionInputStream.close();
				sessionOutputStream.close();
				showMessageAdmin("Verbindung beendet");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("run() beendet");
		}
		
		private void setupStreamsAndKrypto() {
			try {
				sessionInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
				sessionOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
				sessionOutputStream.writeObject(rsaModule.e + "+" + rsaModule.n + rsaModule.rounds);
				sessionOutputStream.flush();
				String name = (String) sessionInputStream.readObject();
				BigInteger subKey = rsaModule.privateKeyDecrypt(new BigInteger(name.substring(0, name.indexOf('|'))), rsaModule.d, rsaModule.n);
				name = name.substring(name.indexOf('|')+1); 
				System.out.println("name: "+ name);
				System.out.println("SubKey: " + subKey);
				clientKrypto = new KryptoServer(subKey) ;
				sessionOutputStream.writeObject("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + "Sie sind jetzt verbunden.");
				sessionOutputStream.flush();
				showMessageAdmin("Sie sind verbunden...");
				ableToType(true);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Nachricht senden
		public void sendMessage(String message) {
			try{
				sessionOutputStream.writeObject("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message);
				sessionOutputStream.flush();
				//writeLogFile("server[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message);
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
		
		/**
		 * @return boolean - true or false entscheidet über Verbleib des Servers(true = an, false = aus)
		 * @throws IOException
		 */
		private boolean whileSharingData() throws IOException {	
			ableToType(true);
			do{
				try {
					inputMessage = (String) sessionInputStream.readObject();
				}catch(ClassNotFoundException classNotFoundException) {
					
				}catch(EOFException eofException) {
					showMessageAdmin("\nVerbindungsfehler zu " + clientSocket.getLocalAddress().getHostAddress());
					return false;
				}
				if(inputMessage.startsWith("cr1")) {
					inputMessage = clientKrypto.decryptMessage(inputMessage.substring(3), rsaModule.rounds);
					showMessage(inputMessage);
				}else{
					showMessage(inputMessage);	
				}  //abschalten des servers nach Kondition
				writeLogFile(inputMessage + "\n",new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			System.out.println(inputMessage.substring(26));
			}while(!inputMessage.substring(22).equals("killclient"));
			sendMessage("killclient");
			return true; //Server überlebt-> auf neue Verbindung warten
		}
		
		//Nachricht senden
		public void sendMessageEncrypted(String message) {
				
			try{
				sessionOutputStream.writeObject("cr1" + clientKrypto.encryptMessage("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n" + message, rsaModule.rounds));
				sessionOutputStream.flush();
				writeLogFile("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				showMessage("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
			
		}
		
		private class OutputStreamTask implements Runnable {
			public OutputStreamTask() {
			}
			
			public void run() {
				//System.out.println("TOT");
				boolean schalter = true;
				while(schalter) {
					if(sender){
						//System.out.println("inrun: " + sendMessage);
						sendMessage(sendMessage);
						sender = false;
					}
					if(xSender) {
						sendMessageEncrypted(sendMessage);
						xSender = false;
					}
				}
				
			}
		}
	
	}
	
	
	
	/**Erzeut einenOutput und einen Inputstream der den Datenaustausch
	 * zum Client managt
	 * @return void - keine
	 * @throws IOException
	 */
	private void startIOStreams() throws IOException {
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\niostreams eingerichtet\n");
		showMessage("---------Konversation beginnt--------");
	}
	
	
	//IOStreams und ClientConnection schließen
	private void closeCrap() {
		showMessageAdmin("\nshutting down connections...");
		ableToType(false);
		try{
			transmitConnectionInfoToClient.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void hiddenSend() {
		try{
			transmitConnectionInfoToClient.writeObject(rsaModule.e + "+" + rsaModule.n + rsaModule.rounds);
			transmitConnectionInfoToClient.flush();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void showMessageAdmin(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						printAreaAdminBox.append("\n" + text);
					}
				}
			);
	}
	
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append("\n" + text);
					}
				}
			);
	}
	
	//Eingaben des Users freischalten/abschalten
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tof); //true/false übergeben an Textfeldstatus
					}
				}
		);
	}
	
	public String getConsoleLine() {
		
		InputStreamReader inputS = new InputStreamReader(System.in); //InputStream für Tastatureingabe
		BufferedReader buffI = new BufferedReader(inputS);  // BufferedReader für Tastatureingabe	
		String localMessage = "";
		try {
			localMessage = buffI.readLine();
			inputS.close(); 
			buffI.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return localMessage;
	}
	
	/**
	 * 
	 * @param s
	 * @param file
	 */
	public void writeLogFile(String s, File file) {			
		try {
			Writer stringWriter = new FileWriter(file, true);		//Neuer FileWriter
			stringWriter.write(s);							//String in file schreiben
			stringWriter.close();							//FileWriter schliessen
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
