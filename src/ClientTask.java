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
	 *Ein- und Ausgaben an die GUI und an die verbundenen Clients weiter. 
	 * @author mrt & sticklobot
	 *
	 */
	public class ClientTask extends Thread  {
		/** Port mit Clienverbindung */
		private final Socket client;
		/**Talk Server Object */
		private final TalkServer server;
		/**ObjectOutputStream für RSA Uebermittelung */
		private ObjectOutputStream exclusiveOutputStreamToClient;
		
		/**InputStream für Clientverindung */
		ObjectInputStream sessionInputStream;
		/**KryptoServer clientKrypto*/
		KryptoServer clientKrypto;
		//Eingelesene Nachricht
		String inputMessage;
		//verwendeter Username wird auf default gesetzt falls keine erfolgreiche Eingabe des Users 
		String userName = "default";
		//Game
		String opponentsName = "";
		
		/** Konstruktor */
		public ClientTask(TalkServer serverObject, Socket clientSocket, ObjectOutputStream exclusiveOutputStream) {
			//clientObject übergeben an globales Feld
			this.client = clientSocket;
			//serverObject übergeben an globales Feld
			this.server = serverObject;
			//OutputStream übergeben an globales Feld
			this.exclusiveOutputStreamToClient = exclusiveOutputStream;
			System.out.println("Paralleles Objekt des Clienten: " + this.toString());
			start();
		}
		
		/**
		 * Führt jeweilge Clientverbindung (Input/Output) parallel zum Hauptprogramm aus
		 */
		public void run() throws RuntimeException {
				
				try {
					//neuen Inputstream erzeugen
					sessionInputStream = new ObjectInputStream(client.getInputStream());
					String input = "";
					String publicKey = "";
					try {
						//PublicKey und Username einlesen von ClientConnection
						input = (String) sessionInputStream.readObject();
						System.out.println("input" + input);
						//PublicKey extrahieren
						publicKey = input.substring(0, input.indexOf(':'));
						//Benutzernamen extrahieren
						userName = input.substring(publicKey.length()+1);
						//Name mit OutputStream assoziiert in Hashtable ablegen
						server.NameToClient.put(userName, client);
						System.out.println("Keys: " + server.NameToClient.get(userName));
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//PublicKey anzeigen lassen
					server.adminWindow.showMessageAdmin("PublicKey von " + client.getLocalAddress() + " : " + publicKey);
					
					//Passwortabfrage
					if(!server.sessionPW.equals("")) {
						System.out.println("sessionPw: " + server.sessionPW);
						//Client auffordern ein PW zu schicken BEFEHL: ::pw::
						exclusiveOutputStreamToClient.writeObject("::pw::"+ server.pwRSA.e.toString() + "&" + server.pwRSA.n.toString());
						exclusiveOutputStreamToClient.flush();
						System.out.println("aufforderung geschickt");
						try {
							
							//Passwort von Client entgegennehmen
							input = (String) sessionInputStream.readObject();
							//PW entschlüsseln
							input = server.pwRSA.privateKeyDecrypt(new BigInteger(input)).toString();
							//ASCII-Codes in Buchstbaen umwandeln - Vorangestellte 1 nicht mit übersetzen.
							input = dezimalAsciiStringToLetters(input.substring(1));
						}catch(ClassNotFoundException classNotFoundException) {
						System.out.println("Fehler bei Passwort einlesen");
						}
						//Passwort korrekt ? true -> Verschlüsselung wird eingerichtet
						if(input.equals(server.sessionPW)) {
							server.adminWindow.showMessageAdmin(userName + " - Passworteingabe korrekt" );
							//Subkey verschlüsseln und an Client übermitteln
							exclusiveOutputStreamToClient.writeObject((publicKeyEncrypt(new BigInteger(String.valueOf(server.serverKrypto.getSubKey())), new BigInteger(publicKey.substring(0, publicKey.indexOf('&'))), new BigInteger(publicKey.substring(publicKey.indexOf('&')+1)))).toString());
							exclusiveOutputStreamToClient.flush();
							server.adminWindow.showMessageAdmin("Subkey: " + server.serverKrypto.getSubKey() + " an " + userName + ":" + client.getInetAddress().getHostAddress());

							//Chatten mit Client ab hier - Programm verharrt in whileChatting()
							try{
							whilechatting();
							}catch(SocketException socketException) {
								System.out.println("Verbindung tot - client: " + client.toString());
								sessionInputStream.close();
								server.removeConnection(client);
							}
							//VERBINDUNG ZUM CLIENT BEENDEN
							sessionInputStream.close();
							server.removeConnection(client);
							server.NameToClient.remove(userName);
							//showMessageAdmin("Verbindung beendet");
						}else {
							server.adminWindow.showMessageAdmin("Falsche Passworteingabe des Users: " + userName + ":" + client.getInetAddress().getCanonicalHostName());
							//VERBINDUNG ZUM CLIENT BEENDEN
							sessionInputStream.close();
							server.removeConnection(client);
							server.NameToClient.remove(userName);
							//showMessageAdmin("Verbindung beendet");
						}
					}
				} catch (IOException e) {
					System.out.println("Fehler bei Aufbau von Inputstream");
					e.printStackTrace();
				}
			//Verbindung beendet anzeigen	
			server.adminWindow.showMessageAdmin("Verbindung zu " + client.toString().substring(client.toString().indexOf('/')+1, client.toString().indexOf(',')) + " ist erloschen");
		}
		
		/**Verwaltet den Programmfluß waehrend des Chatvorgangs.
		 * d.h. nimmt Nachrichten entgegen, leitet diese weiter und gibt diese zur 
		 * Ausgabe weiter. Das Ende dieser Funktion 
		 * zieht das Ende der Verbindung zum Client nach sich.
		 * @return boolean - true or false entscheidet über Verbleib des Servers(true = an, false = aus)
		 * @throws IOException
		 */
		private synchronized void whilechatting() throws IOException  {	
			boolean alive = true;
			String tempForeignClientName = "";
			String tempResponse;
			String tempGameName = "";
			String decryptedInfo = "";
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
					//VERSCHLUESSELTE BEHANDLUNG
					//Entschluesseln der Nachricht zum anzeigen
					decryptedInfo = server.serverKrypto.decryptMessage(inputMessage.substring(3),1);
					tempForeignClientName = decryptedInfo.substring(16 + userName.length(), decryptedInfo.indexOf('$'));
					if(server.NameToClient.containsKey(tempForeignClientName)) {
						tempGameName = decryptedInfo.substring(16+userName.length() + tempForeignClientName.length() + 1);
						switch(tempGameName) {
							case "pong" : {
								server.sendToExplicitClient((Socket) server.NameToClient.get(tempForeignClientName), userName+"$pong");
								
							}break;
						}
					}else {
						//Nachricht unverschluesselt anzeigen
						server.chatGui.showMessage(decryptedInfo);
						//Nachricht an alle Clients weiterreichen und anzeigen
						server.sendToAll(inputMessage);
					}
				}else{ //UNVERSCHLUESSELTE BEHANDLUNG
					
					if(inputMessage.length() > 5 && (inputMessage.startsWith("@play") || inputMessage.startsWith("@response")) ) {
						if(inputMessage.startsWith("@play")) {
							tempForeignClientName = inputMessage.substring(5, inputMessage.indexOf('$'));
							System.out.println("foreigClientName: " + tempForeignClientName);
							if(server.NameToClient.containsKey(tempForeignClientName)) {
								tempGameName = inputMessage.substring(inputMessage.indexOf('$')+1);
								System.out.println("GameName:" + tempGameName);
								switch(tempGameName) {
									case "pong" : {
									server.sendToExplicitClient((Socket) server.NameToClient.get(tempForeignClientName), "@play" + userName+"$pong");
									}break;
								}
							}
						}
						
						if(inputMessage.startsWith("@response")) {
							tempResponse = inputMessage.substring(9, inputMessage.indexOf('$'));
							System.out.println("tempResponse: " + tempResponse);
							if(tempResponse.equals("true")) {
								opponentsName = inputMessage.substring(inputMessage.indexOf('&')+1);
								System.out.println("opponentsName: " + opponentsName);
								inputMessage = inputMessage.substring(0, inputMessage.indexOf('&')) + userName;
								server.sendToExplicitClient((Socket) server.NameToClient.get(opponentsName), inputMessage);
								//SPIELSTARTEN "PONG"
							}else {
								opponentsName = inputMessage.substring(inputMessage.indexOf('&')+1);
								server.sendToExplicitClient((Socket) server.NameToClient.get(opponentsName), inputMessage);
							}
						}
					}else {	
						//Nachricht unerverschluesselt anzeigen
						server.chatGui.showMessage(inputMessage);
						//Nachricht an alle Clients weiterreichen und anzeigen
						server.sendToAll(inputMessage);
						}
				}
					
			}while(alive);	
		}
		
		/**
		 * 
		 * @param message
		 * @return
		 */
		public String dezimalAsciiStringToLetters(String message) {
			
			String clear = "";
			//i=1 -> erstes Zeichen ("1") überspringen, und dann 000 - 255 in Buchstaben umwandeln
			for (int i = 0; i < message.length()-2; i += 3) {
				//In char umwandeln und an clear anhängeno
				clear += (char) Integer.parseInt(message.substring(i, i+3));
			}
			//System.out.println("clear; " + clear);
			return clear;
			
		}
		
		/**
		 * public BigInteger publicKeyEncrypt(BigInteger message, BigInteger e, BigInteger n)
		 * entschluesselt die uebertragenen Daten mit 'message^e mod n'
		 * @param message
		 * @param e
		 * @param n
		 * @return entschluesselte Nachricht - BigInteger
		 */
		public BigInteger publicKeyEncrypt(BigInteger message, BigInteger e, BigInteger n) {
			return message.modPow(e, n); 
		}
	
	}