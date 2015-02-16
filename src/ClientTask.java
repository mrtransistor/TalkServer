import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


/**
	 *Die Klasse ClientTask ist parallel ausführbar und stellt die gesamte
	 *Server-Client[Thread] Funktionalität zur Verfügung. Die Klasse leitet 
	 *Ein- und Ausgaben an die GUI weiter. 
	 * @author mrt & sticklobot
	 *
	 */
	public class ClientTask extends Thread  {
		/** Port mit Clienverbindung */
		private final Socket client;
		/**Talk Server Object */
		private final TalkServer server;
		/**ObjectOutputStream für RSA Uebermittelung */
		ObjectOutputStream exclusiveOutputStreamToClient;
		/**InputStream für Clientverindung */
		ObjectInputStream sessionInputStream;
		/**KryptoServer clientKrypto*/
		KryptoServer clientKrypto;
		String inputMessage;
		String userName = "default";
		
		/** Konstruktor */
		public ClientTask(TalkServer serverObject, Socket clientSocket, ObjectOutputStream exclusiveOutputStream) {
			this.client = clientSocket;
			this.server = serverObject;
			this.exclusiveOutputStreamToClient = exclusiveOutputStream;
			System.out.println("Paralleles Objekt des Clienten: " + this.toString());
			start();
		}
		
		public void run() throws RuntimeException {
				
				try {
					//neuen Inputstream erzeugen
					sessionInputStream = new ObjectInputStream(client.getInputStream());
					String input = "";
					String name = "";
					String publicKey = "";
					try {
						input = (String) sessionInputStream.readObject();
						System.out.println("input" + input);
						publicKey = input.substring(0, input.indexOf(':'));
						name = input.substring(publicKey.length()+1);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//SubKey Verschuesseln und uebertragen
					server.adminWindow.showMessageAdmin("PublicKey von " + name + " : " + publicKey);
					exclusiveOutputStreamToClient.writeObject((publicKeyEncrypt(new BigInteger(String.valueOf(server.serverKrypto.getSubKey())), new BigInteger(publicKey.substring(0, publicKey.indexOf('&'))), new BigInteger(publicKey.substring(publicKey.indexOf('&')+1)))).toString());
					exclusiveOutputStreamToClient.flush();
					//exclusiveOutputStreamToClient.close();	
					server.adminWindow.showMessageAdmin("Subkey: " + server.serverKrypto.getSubKey() + " an " + name + ":" + client.getInetAddress().getHostAddress());

					//Chatten mit Client
					try{
					whileSharingData();
					}catch(SocketException socketException) {
						System.out.println("Verbindung tot - client: " + client.toString());
					}
					//VERBINDUNG ZUM CLIENT BEENDEN
					sessionInputStream.close();
					server.removeConnection(client);
					//showMessageAdmin("Verbindung beendet");
				} catch (IOException e) {
					System.out.println("Fehler bei Aufbau von Inputstream");
					e.printStackTrace();
				}
			server.adminWindow.showMessageAdmin("Verbindung zu " + client.toString().substring(client.toString().indexOf('/')+1, client.toString().indexOf(',')) + " ist erloschen");
		}
		
		/**
		 * @return boolean - true or false entscheidet über Verbleib des Servers(true = an, false = aus)
		 * @throws IOException
		 */
		private synchronized void whileSharingData() throws IOException  {	
			boolean alive = true;
			//ableToType(true);
			do{
				try {
					// Nachricht einlesen und Zwischenspeichern
					inputMessage = (String) sessionInputStream.readObject();
				}catch(ClassNotFoundException classNotFoundException) {
					System.out.println(classNotFoundException);
				}catch(SocketException socketException){
					server.adminWindow.showMessageAdmin("\n"+client.getLocalAddress().getCanonicalHostName() + " hat Connection gekillt\nAbbruchkriterium: SocketException\n" );
				}catch(EOFException eofException) {
					server.adminWindow.showMessageAdmin("\nVerbindungsfehler zu " + client.getLocalAddress().getHostAddress());
				}
				
				//TODO LOG DATEI SCHREIBEN writeLogFile(inputMessage + "\n",new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				//Nachricht verschlüsselt ? 
				if(inputMessage.startsWith("cr1")) {
					//Entschluesseln der Nachricht
					inputMessage = server.serverKrypto.decryptMessage(inputMessage.substring(3),1);
				}
				//Nachricht nur an Clients weiterreichen, wenn keine Kill Aufforderung gesendet wurde
				if(inputMessage.substring(16 + userName.length()).equals("killclient")) {
					alive = false;
				}else{
					//Nachricht an alle Clients weiterreichen und anzeigen
					server.sendToAll(inputMessage);
				}
				
			}while(alive);	
		}
		
		/**
		 * 
		 * @param message
		 * @param e
		 * @param n
		 * @return
		 */
		public BigInteger publicKeyEncrypt(BigInteger message, BigInteger e, BigInteger n) {
			return message.modPow(e, n); 
		}
		
		/**
		 * public void sendMessageEncrypted(String message) throws IOException.
		 * Sie konsumiert einen String (Nachricht die versendet werden soll)
		 * Der übergebende String wird vom aktullen (Server:Client(n)] KryptoModul
		 * verschlüsselt und über den aktuellen (Server:Client(n)] OutputStream
		 * verschickt.  
		 * 
		 * @param message
		 *
		public void sendMessageEncrypted(String message) throws IOException {
				sessionOutputStream.writeObject("cr1" + clientKrypto.encryptMessage("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n" + message, rsaModule.rounds));
				sessionOutputStream.flush();
				//writeLogFile("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				//showMessage("server[-c- " + new java.util.Date().toString().substring(4,16) + "]:\n"+ message);
		}*/
	
	}