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
	
	/** Array verfügbarer Ports */
	boolean[] availablePorts = {true,true,true,true,true,true,true,true,true,true};
	/**KryptoServer - Kryptomodul für Datenübertragung*/
	KryptoServer cryptoModule;
	
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
		
		cryptoModule= new KryptoServer(); //Kryptomodul erzeugen
		
		//cryptoModule.rounds = (int) (Math.random() * 4 + 1); Verzicht auf Server
		System.out.println("Rounds: " + cryptoModule.rounds);
		drawServerGui();
	}
	
	/**
	 * private void drawServerGui() zeichnet ServerGui und wartet auf IOStreams
	 */
	private void drawServerGui() {
		
		JFrame chatGUI = new JFrame("ChatServer");
		chatGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userText = new JTextField();
		userText.setEditable(false);
		//userText.setSize(550, 30);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
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
				sendMessage(userText.getText()); // Eingabe holen
				userText.setText(""); //reset Textfeld
				}
			});
		        
		//Encrypted SendButton
        buttonEncryptedSend = new JButton("crypt Send");
        buttonEncryptedSend.addActionListener( new ActionListener() {
        	public void actionPerformed( ActionEvent event ) {		  			 
		  			sendMessageEncrypted(userText.getText()); // Eingabe holen
		  			userText.setText(""); //reset Texteingabefeld
		  		  }
		  	} );
		        
        //Encrypted Fire Fire Fire Fire
		buttonFire = new JButton("Fire");
		buttonFire.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent event ) {
		    	sendMessage(userText.getText() ); // Eingabe holen
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
				serverConnectionListener = new ServerSocket(3333,10);
				
					while(true) {
						showMessage("\nauf verbindung warten..."); 
						Socket clientSocket = serverConnectionListener.accept();
						clientProcessingPool.submit(new ClientTask(clientSocket));
						/**@Override*/	//Später Ausgaben von ShowMessage in VerbinundungsdatensAnzeigefeld ausgeben lassen
						showMessage("\nverbunden zu " + clientSocket.getInetAddress().getHostAddress());	
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
	
	
	
	private class ClientTask implements Runnable {
		
		private final Socket clientSocket;
		private ClientTask(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		public void run() {
			/**@Override Clientfunktionalität zu Server */
			try {
				ObjectInputStream sessionInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
				ObjectOutputStream sessionOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
				sessionOutputStream.writeObject("Test");
				sessionOutputStream.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				whileSharingData();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
	/**
	 * @return boolean - true or false entscheidet über Verbleib des Servers(true = an, false = aus)
	 * @throws IOException
	 */
	private boolean whileSharingData() throws IOException {
		String message = "Sie sind jetzt verbunden";
		hiddenSend();
		sendMessage(message);
		try {
			cryptoModule.setSubKey(Integer.parseInt(cryptoModule.privateKeyDecrypt(new BigInteger((String) input.readObject()), cryptoModule.d, cryptoModule.n).toString()));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ableToType(true);
		
		do{
			try {
				message = (String) input.readObject();
			}catch(ClassNotFoundException classNotFoundException) {
				
			}catch(EOFException eofException) {
				showMessage("\n Verbindungsfehler.");
				return true;
			}
			if(message.startsWith("cr1")) {
				message = cryptoModule.decryptMessage(message.substring(3), cryptoModule.rounds);
				showMessage(message);
			}else{
				showMessage(message);	
			}  //abschalten des servers nach Kondition
			writeLogFile(message+"\n",new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
		System.out.println(message.substring(26));
		}while(!message.substring(22).equals("killclient"));
		sendMessage("killclient");
		return true; //Server überlebt-> auf neue Verbindung warten
	}
	
	//IOStreams und ClientConnection schließen
	private void closeCrap() {
		showMessage("\nshutting down connections...");
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
			transmitConnectionInfoToClient.writeObject(cryptoModule.startRSA() + "+" + cryptoModule.n + cryptoModule.rounds);
			transmitConnectionInfoToClient.flush();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//Nachricht senden
	public void sendMessage(String message) {
		try{
			transmitConnectionInfoToClient.writeObject("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message);
			transmitConnectionInfoToClient.flush();
			writeLogFile("server[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//Nachricht senden
	public void sendMessageEncrypted(String message) {
			
		try{
			transmitConnectionInfoToClient.writeObject("cr1" + cryptoModule.encryptMessage("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n" + message, cryptoModule.rounds));
			transmitConnectionInfoToClient.flush();
			writeLogFile("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			showMessage("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
		
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
