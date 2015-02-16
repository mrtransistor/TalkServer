import java.math.BigInteger;
import java.awt.*;

import javax.management.MXBean;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class TalkServer {
	
	/**serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/**ServerSocket Object */
	private ServerSocket serverConnectionListener;
	//Hashtabelle der die Verbundenen Clients hält
	private Hashtable outputStreams = new Hashtable();
	//Object zur ServerGui Generierung.
	ServerGui chatGui;
	//Object zur AdminWindow Generierung
	AdminWindow adminWindow;
	//Server Kryptomodul
	KryptoServer serverKrypto;
	//Globaler SubKey
	BigInteger globalKey;
	
	/** */
	AskUserYesNo killHostPrompt;
	
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
		
		serverKrypto = new KryptoServer();
		
		//ServerGui zeichnen
		chatGui = new ServerGui(); 
		//AdminWindow zeichnen
		adminWindow = new AdminWindow();
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
					//adminWindow.showMessageAdmin("\nWarten auf Username...");
					//writeLogFile("\nverbunden zu " + clientSocket.getInetAddress().getHostAddress(), new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
					adminWindow.showMessageAdmin("auf weitere Verindungen warten...");
					new ClientTask(this, clientSocket, outputStreamToClient);
				}
			}	
	//Auflistung aller Outputstreams, eine für jeden client
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
			//Nachricht anzeigen n Chatgui
			chatGui.showMessage(message);
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
	
	/**
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
