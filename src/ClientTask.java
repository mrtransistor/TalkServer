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
		/**InputStream für Clientverindung */
		ObjectInputStream sessionInputStream;
		/**Outputstream für Clientverbindung */
		ObjectOutputStream sessionOutputStream;
		/**KryptoServer clientKrypto*/
		KryptoServer clientKrypto;
		String inputMessage;
		
		/** Konstruktor */
		public ClientTask(TalkServer serverObject, Socket clientSocket) {
			this.client = clientSocket;
			this.server = serverObject;
			System.out.println("Paralleles Objekt des Clienten: " + this.toString());
			start();
		}
		
		public void run() throws RuntimeException {
				
				try {
					//neuen Inputstream erzeugen
					sessionInputStream = new ObjectInputStream(client.getInputStream());					
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
			//ableToType(true);
			do{
				//server.sendToAll(server.chatGui.serverMessage);
				try {
					// speichert eingelesen Nachricht 
					inputMessage = (String) sessionInputStream.readObject();
					System.out.println("IM: " + inputMessage);
					//TODO AN SERVER SCHICKEN
				}catch(ClassNotFoundException classNotFoundException) {
					
				}catch(SocketException socketException){
					//showMessageAdmin("\n"+clientSocket.getLocalAddress().getCanonicalHostName() + " hat Connection gekillt\nAbbruchkriterium: SocketException\n" );
				}catch(EOFException eofException) {
					//showMessageAdmin("\nVerbindungsfehler zu " + clientSocket.getLocalAddress().getHostAddress());
				}
				server.sendToAll(inputMessage);
				//writeLogFile(inputMessage + "\n",new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
			//System.out.println(inputMessage.substring(26));
				try {
					inputMessage.substring(22).equals("killclient"); 
					}catch (NullPointerException np){
					System.out.println("leere Nachricht oder Verbindung tot - client:" + client.toString());
					break;
					}
			}while(!inputMessage.substring(22).equals("killclient"));	
		}
		
		/**
		 * public void sendMessage(String message) throws IOException.
		 * Sie konsumiert einen String (Nachricht die versendet werden soll)
		 * Der übergebende String wird über den aktuellen (Server:Client(n)] OutputStream
		 * verschickt. 
		 * @param message
		 */
		public void sendMessage(String message) {
			try{
				sessionOutputStream.writeObject("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message);
				sessionOutputStream.flush();
				//writeLogFile("server[" + new java.util.Date().toString().substring(4,16) + "]:\n"+ message + "\n", new File("/home/mrtransistor/workspace/InputOutputInterface/src/logFile.log"));
				//showMessage("server[" + new java.util.Date().toString().substring(4,16) + "]:\n" + message);
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
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