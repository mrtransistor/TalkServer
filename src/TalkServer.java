import java.math.BigInteger;
import java.awt.*;

import javax.management.MXBean;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class TalkServer {
	
	/**serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/**ServerSocket Object */
	private ServerSocket serverConnectionListener;
	//Hashtabelle der die Verbundenen Clients hält
	public Hashtable outputStreams = new Hashtable();
	//Object zur ServerGui Generierung.
	ServerGui chatGui;
	//Object zur AdminWindow Generierung
	AdminWindow adminWindow;
	//Server Kryptomodul
	KryptoServer serverKrypto;
	//Liste der angemeldeten Clientnamen
	List<String> nameList = new ArrayList<String>();
	//Globaler SubKey
	BigInteger globalKey;
	//set PasswordGUI 
	AskGui passwordGUI;
	/** */
	AskUserYesNo killHostPrompt;
	//session Pw
	String sessionPW;
	//Hashtable haelt Namen assoziiert mit jeweiligem Outputstream
	public Hashtable NameToClient = new Hashtable();
	//RSA-Modul zur verschuesselten PW-Uebermittelung
	RSAModule pwRSA ;
	//serverPortCounter
	int newServerListenPort;
	
	
	/**ExecutorService für ClientThreads*/
	//final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
	
	
	/** Erzeugt Objekt der Klasse TalkServer, welches alle Funktionalitäten  
	 * der Klasse zur Verfügung stellt und explizit die Anwenderoberfläche erzeugt.
	 * Der Konstruktor erzeugt auch ein neues Objekt der Klasse KryptoServer 
	 * welches die Verschlüsselung während der Übertragung übernimmt und weitere 
	 * Kryptofunktionalitäten liefert 
	 * @param void - keine
	 */
	public TalkServer(int serverListenPort) throws IOException {
		//Password setzen
		passwordGUI = new AskGui("Passwort setzen", "Session Passwort(leer = kein PW):", "test");
		//session Passwort holen
		sessionPW = passwordGUI.answerOfUser;
		passwordGUI = null;
		//ServerGui zeichnen
		chatGui = new ServerGui(); 
		//ServerlistenPort setzen
		this.newServerListenPort = serverListenPort+1;
		//AdminWindow zeichnen
		adminWindow = new AdminWindow();
		//Passwort anzeigen
		adminWindow.showMessageAdmin("Passwort für diese Session: " + sessionPW);
		//RSA Modul fuer verschuesselte PW-Uebermittelung initialisieren
		pwRSA = new RSAModule();
		//Modul fuer Verschluesselun der Nachrichtenübertragungen
		serverKrypto = new KryptoServer(adminWindow);
		//ListenThread für Verbindungen zu Server
		aufVerbindungWarten(serverListenPort);
		
	}

	/**aufVerbindungenWarten() lauscht auf dem Serversocket bis eine Verbindung vom 
	 * Client aufgebaut wird. Stellt dann die Verbindung her und gibt bei Erfolg 
	 * eine Nachricht im Chatfenster aus (chatWindow)
	 * @return void - keine
	*/ 
	private void aufVerbindungWarten(final int serverListenPort)  throws IOException {
			//ServerSocket erstellen -> Server bereit für Verbindungen auf Port	
			serverConnectionListener = new ServerSocket(serverListenPort,10);
			adminWindow.showMessageAdmin("Server lauscht für immer ;) auf\nneue Connections? ok."); 
				while(true) {
					//AUF VERBINDUNG ZU CLIENT LAUSCHEN bis accept() feuert
					Socket clientSocket = serverConnectionListener.accept();
					chatGui.ableToType(true);
					//Outputstream für Datenübertragung zu Client
					ObjectOutputStream outputStreamToClient = new ObjectOutputStream(clientSocket.getOutputStream());
					//Stream speichern und asoziiert mit Clientverbindung
					outputStreams.put(clientSocket, outputStreamToClient);
					/**@Override*/	//Später Ausgaben von ShowMessage in VerbinundungsdatensAnzeigefeld ausgeben lassen
					adminWindow.showMessageAdmin("\nverbunden zu " + clientSocket.getInetAddress().getHostAddress());	
					//writeLogFile("\nverbunden zu " + clientSocket.getInetAddress().getHostAddress(), new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
					adminWindow.showMessageAdmin("auf weitere Verbindungen warten...");
					new ClientTask(this, clientSocket, outputStreamToClient);
				}
			}	
	
	/**
	 * Gibt Auflistung verfügbarer Elemente in outputStreams zurueck 
	 * @return Aufzaehlung der Elemente in outputStreams
	 */
	Enumeration getOutputStreams() {
	return outputStreams.elements();
	}

	/**
	 * public void sendToAll(String message) throws IOException konsumiert
	 * Nachricht vom Typ String und flusht diese an alle Clients über Verbindung
	 * aus outputStreams vom Typ Enumeration
	 * @param message
	 * @throws IOException
	 */
	public void sendToAll(String message) throws IOException {
		//Outputstream for flushing to Clients
		ObjectOutputStream os;
		//Synchronisiert um Zugriff auf outputStreams mit anderen Threads zu verhindern z.B. deleteConnection(Socket connectionToKill)
		synchronized(outputStreams) {
			//System.out.println("Elemente vorhanden: " + getOutputStreams().hasMoreElements());
			//Alle OutputStreams der Clients durchgehen und senden
			for(Enumeration e = getOutputStreams(); e.hasMoreElements();) {
				//Outputstream erhalten
				os = (ObjectOutputStream) e.nextElement();
				//Nachricht senden
				os.writeObject(message);
				os.flush();
			}
		}
	}
	
	/**
	 * 
	 * @param client1
	 * @param client2
	 * @param message
	 * @throws IOException
	 */
	public void sendToGamingClients(Socket client1, Socket client2, String message) throws IOException {
				//Outputstream for flushing to Clients
				ObjectOutputStream os;
				//Synchronisiert um Zugriff auf outputStreams mit anderen Threads zu verhindern z.B. deleteConnection(Socket connectionToKill)
				synchronized(outputStreams) {
						os = (ObjectOutputStream) outputStreams.get(client1);
						//Spieldaten an Client1 uebertragen
						os.writeObject(message);
						os.flush();
						//Spieldaten an Client2 uebertragen
						os = (ObjectOutputStream) outputStreams.get(client2);
						os.writeObject(message);
						os.flush();
					}
	}
	
	/**
	 * public void sendToAll(String message) throws IOException konsumiert
	 * Nachricht vom Typ String und flusht diese an alle Clients über Verbindung
	 * aus outputStreams vom Typ Enumeration
	 * @param message
	 * @throws IOException
	 */
	public void sendToExplicitClient(Socket client, String message) throws IOException {
		//Outputstream for flushing to Clients
		ObjectOutputStream os;
		//Synchronisiert um Zugriff auf outputStreams mit anderen Threads zu verhindern z.B. deleteConnection(Socket connectionToKill)
		synchronized(outputStreams) {
				os = (ObjectOutputStream) outputStreams.get(client);
				//Daten an expliziten Client senden
				os.writeObject(message);
				os.flush();
			}
	}
	
	
	/**
	 * void removeConnection(Socket connectionToKill) throws IOException konsumiert
	 * Clientverbindung vom Typ Socket und löscht diese aus outputStreams und schließ die Verbindung
	 * @param connectionToKill
	 * @throws IOException
	 */
	void removeConnection(Socket connectionToKill) throws IOException {
		//Synchronisiert um Zugriff auf outputStreams mit anderen Threads zu verhindern z.B. sendToAll(String message)
		synchronized (outputStreams) {
			//Clientverbindung aus Clientliste löschen
			outputStreams.remove(connectionToKill);
			//Clientverbindung schließen
			connectionToKill.close();
		}
	}

	/**Schreibt Logfile mit übergebenen Daten
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
	
	/**Startet des gesamte Programm (optional)
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//Kann Argument aus Kommandozeile entgegennehmen -> Port auf dem gelauscht wird
		int serverListenPort = 3336;
		//startet Server mit übergebenem Port
		new TalkServer(serverListenPort);
	}
	
	
}
