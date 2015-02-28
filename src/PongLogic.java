import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.Timer;
import javax.swing.text.StyledEditorKit.ForegroundAction;


public class PongLogic implements Runnable, ActionListener {
	
	//PongServerModul
	ServerSocket pongServer;
	//Groeße des Paddles
	Point paddleSize;
	//Schrittweite des Paddles
	int paddleSpeed;
	//Position des Paddles
	Point ballPosition;
	//Richtung des Paddles
	Point ballDirection;
	//Groeße des Spielballs
	Point ballSize;
	//AktualisierungsRate
	int frameRate;
	//Schrittweite des Balls
	int gameSpeed;
	//Punktestaende der Clients
	int score1 = 0;
	int score2 = 0;
	//Verbindungen zu Clients
	Socket client1;
	Socket client2;
	//Zugriff auf GUI fuer Admin relevante Nachrichten
	TalkServer GUI;
	//Positionen der Spieler
	private Point PositionPlayerOne = new Point(10,100);
	private Point PositionPlayerTwo = new Point(425, 100);
	//AusgabeStrom zu Clients
	private ObjectOutputStream outputStreamClient1;
	private ObjectOutputStream outputStreamClient2;
	//Eingabestrom zu Clients
	private ObjectInputStream inputStreamClient1;
	private ObjectInputStream inputStreamClient2;
	//Spielernamen
	private String player;
	private String opponent;
	//EingabenFelder fuer Nachrichten der Clienten
	int inputClient1;
	int inputClient2;
	//Richtung der ClientPaddles
	int stepClient1 = 0;
	int stepClient2 = 0;
	
	/**
	 * Generiert das ServerObjekt eines Pongspiels zwischen 2 Spielern.
	 * Verwaltet Berechnungen und verteilt notwendige KonfigurationsInformationen
	 * an verbundene Clients.
	 * 
	 * @param GUI
	 * @param newServerPort
	 * @param player
	 * @param opponent
	 */
	public PongLogic(TalkServer GUI, int newServerPort, String player, String opponent)   {
		this.GUI = GUI;
		System.out.println("newServerPort: " + newServerPort);
		try {
			this.pongServer = new ServerSocket(newServerPort, 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Server fehlstart");
			e.printStackTrace();
		}
		this.player = player;
		this.opponent = opponent;
		GUI.adminWindow.showMessageAdmin("Pong zwischen " + player +  " und " + opponent + " gestartet");
		System.out.println("Server: PongLogic laueft...");

	}
	
	/**
	 * 
	 * @return
	 */
	public String generateTransmittableParameters() {
		
		
		
		return "";
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void waitForConnection()  {
		System.out.println("auf Clients warten...");
		try {
			client1 = pongServer.accept();
			System.out.println("Client1 ist verbunden.");
			client2 = pongServer.accept();
			System.out.println("Client2 ist verbunden \nSpiel kann losgeshen! ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * IOStreams starten und konfigurieren
	 * @throws ClassNotFoundException
	 */
		private void setupStreams() throws ClassNotFoundException {
			try {
				//OutputStrom client1
				outputStreamClient1 = new ObjectOutputStream(client1.getOutputStream());
				outputStreamClient1.flush();
				//OutputStrom client2
				outputStreamClient2 = new ObjectOutputStream(client2.getOutputStream());
				outputStreamClient2.flush();
				
			} catch (IOException e) {
				System.out.println("Fehler beim Senden");
				e.printStackTrace();
			}
			try {
				//Inputstrom zu Client1
				inputStreamClient1 = new ObjectInputStream(client1.getInputStream());
				//Inputstrom zu Client2
				inputStreamClient2 = new ObjectInputStream(client2.getInputStream());
			} catch (IOException e) {
				System.out.println("Fehler beim empfangen");
				e.printStackTrace();
			}
			System.out.println("IOStreams eingerichtet");
			//showMessage("\n iostreams eingerichtet\n");
		}
	
	
	/**
	 * 
	 * @return
	 */
	private int[] generateStartParameters() {
	
		paddleSize = new Point(10, 35);
		ballPosition = new Point(225,150);
		ballSize = new Point(10,10);
		frameRate = 1000/50;
		gameSpeed = 1;
		paddleSpeed = 5;
		PositionPlayerOne = new Point(10,100);
		PositionPlayerTwo = new Point(425,100);
		ballDirection = getRandomStartDirection();
		
		return new int[] { ballDirection.x, ballDirection.y, ballPosition.x, ballPosition.y, ballSize.x, ballSize.y, 
						   frameRate, gameSpeed, PositionPlayerOne.x, PositionPlayerOne.y, PositionPlayerTwo.x, PositionPlayerTwo.y,
						   paddleSize.x, paddleSize.y, paddleSpeed, score1, score2};

	}
	
	/**
	 * 
	 * @return
	 */
	public Point getRandomStartDirection(){
    	Point newDirection = new Point();
    	if(Math.random() > 0.5){
    		newDirection.x = gameSpeed;
    	} else {
    		newDirection.x = -gameSpeed;
    	}
    	if(Math.random() < 0.5){
    		newDirection.y = gameSpeed;
    	} else {
    		newDirection.y = -gameSpeed;
    	}
    	
    	return newDirection;
    }
	
	 /**
	 * @throws IOException 
	  * 
	  */
	 public void whileGaming() throws IOException{
		 	Point[] configData = new Point[4];
		 	try {
		 		//Steuerdaten empfangen
				inputClient1 = (int)inputStreamClient1.readObject();
				inputClient2 = (int)inputStreamClient2.readObject();
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Fehler beim Nachrichten einlesen");
				e.printStackTrace();
			}
	    	
		 	System.out.println("INPUTCLIENT1: " + inputClient1);
		 	System.out.println("INPUTCLIENT2: " + inputClient2);

		 	//Richtung des paddle merken
		 	stepClient1 = inputClient1;
		 	stepClient2 = inputClient2;
		 	
	        //Bewegung PaddleClient1 
	        switch(stepClient1) {
	        	//PaddleClient1 nach Oben!
	        	case 1 : { 
	        		if (PositionPlayerOne.y-paddleSpeed > 0) {
	        			PositionPlayerOne.y -= paddleSpeed;
	        		}
	        		stepClient1 = 0;
	        	}break;
	        	//PaddleClient1 nach Unten!
	        	case -1 : {
	        		if (PositionPlayerOne.y + paddleSpeed + paddleSize.y < 300) {
	        			PositionPlayerOne.y += paddleSpeed;
	        		}
	        		stepClient1 = 0;
	        	}break;
	        	// Ansonsten gar nichts unternehmen!
	        }   
	        
	        //Bewegung PaddleClient2
	        switch(stepClient2) {
        		//PaddleClient2 nach Oben!
        		case 1 : { 
        			if (PositionPlayerTwo.y-paddleSpeed > 0) {
        				PositionPlayerTwo.y -= paddleSpeed;
        			}
	        		stepClient2 = 0;
        		}break;
        		//PaddleClient2 nach Unten!
        		case -1 : {
        			if (PositionPlayerOne.y + paddleSpeed + paddleSize.y < 300) {
        				PositionPlayerTwo.y += paddleSpeed;
        			}
	        		stepClient2 = 0;
        		}break;
        		// Ansonsten gar nichts unternehmen!
	        }
		 	
		 	//Notwendige Variablen generieren
	        int nextBallLeft = ballPosition.x + ballDirection.x;
	        int nextBallTop = ballPosition.y + ballDirection.y;
	        
	        int nextBallRight = ballPosition.x + ballSize.x + ballDirection.x;
	        int nextBallBottom = ballPosition.y + ballSize.x + ballDirection.y;
	        
	        int playerOneRight = PositionPlayerOne.x+ paddleSize.x;
	        int playerOneTop = PositionPlayerOne.y;
	        int playerOneBottom = PositionPlayerOne.y + paddleSize.y;
	        
	        float playerTwoLeft = PositionPlayerTwo.x+ paddleSize.x;
	        float playerTwoTop = PositionPlayerTwo.y;
	        float playerTwoBottom = PositionPlayerTwo.y + paddleSize.y;
	        
	        //ball bounces off top and bottom of screen
	        if (nextBallTop < 0 || nextBallBottom > 300) {
	            ballDirection.y *= -1;
	        }

	      //will the ball go off the left side?
	        if (nextBallLeft < playerOneRight) { 
	            //is it going to miss the paddle?
	            if (nextBallTop > playerOneBottom || nextBallBottom < playerOneTop) {

	                System.out.println("PLAYER TWO SCORED");
	                score2 += 1;
	                ballPosition.x = 225;
	                ballPosition.y = 150;
	            }
	            else {
	                ballDirection.x *= -1;
	            }
	        }
	        
	        //will the ball go off the right side?
	        if (nextBallRight > playerTwoLeft) {
	            //is it going to miss the paddle?
	            if (nextBallTop > playerTwoBottom || nextBallBottom < playerTwoTop) {

	                System.out.println("PLAYER ONE SCORED");
	                score1 += 1;
	                ballPosition.x = 225;
	                ballPosition.y = 150;
	            }
	            else {
	                ballDirection.x *= -1;
	            }
	        }	        
	        //Ballposition aktualisieren
	        ballPosition.x += ballDirection.x;
	        ballPosition.y += ballDirection.y;
	        System.out.println("BALLPOS: " + ballPosition);
	        //ConfigData zum Versenden formatieren
	    	configData[0] = new Point(ballPosition); 
	        configData[1] = new Point(PositionPlayerOne);
	        configData[2] = new Point(PositionPlayerTwo);
	    	configData[3] = new Point(score1, score2);
	    	    	    	
	        //BallPositionen und Paddle Positionen verschicken
	        sendToClients(configData);
	    }
	 
	 	/**
	 	 * relevante Spieldaten an Clients verschicken
	 	 * 
	 	 * @param configData
	 	 * @throws IOException 
	 	 */
	 	public void sendToClients(Point[] configData) throws IOException {
	 		System.out.println("SEND: " + configData[0]  + configData[1]  + configData[2]  + configData[3]);
	 		//Spieldaten an Client1 senden 
	 		outputStreamClient1.writeObject(configData);
	 		outputStreamClient1.flush();
	 		//Spieldaten an Client2 senden
	 		outputStreamClient2.writeObject(configData);
	 		outputStreamClient2.flush();		
	 	}
	 
	 
	 
	 	/**
	 	 * 
	 	 */
	 	public void actionPerformed(ActionEvent arg0) {
			try {
				whileGaming();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	 	
	 	/**
	 	 * @throws IOException 
	 	 * 
	 	 */
	 	private void transmitStartConfig(int[] startConfig) throws IOException {
	 		
	 		System.out.println("Startkonfiguration uebermitteln");
	 		//Startconfig an client1 uebertragen
	 		outputStreamClient1.writeObject(startConfig);
	 		outputStreamClient1.flush();
	 		//Startconfig an client2 uebertragen
	 		outputStreamClient2.writeObject(startConfig);
	 		outputStreamClient2.flush();
	 	}

		/**
		 * 
		 */
		public void run() {
			//Input & Output Datenstroeme anlegen
			try {
				waitForConnection();
				setupStreams();
			//StartKonfig erzeugen
			int[] startConfig;
			startConfig = generateStartParameters();
			//StartKonfiguration uebermitteln
			transmitStartConfig(startConfig);
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Fehler bei Setupstreams");
				e.printStackTrace();
			}
			
			System.out.println("Timer startet...");
			//While Gaming - aktualisiert das spiel 60mal pro Sekunde
	        Timer timer = new Timer(frameRate, this);
	        timer.start();
	        //Alles am Leben erhalten bis Timer Modul beendet
	        while(timer.isRunning()) {
	        	
	        }
	        System.out.println("Spiel beendet");
	        try {
				outputStreamClient1.close();
				outputStreamClient2.close();
				inputStreamClient1.close();
				inputStreamClient2.close();
				client1.close();
				client2.close();
				pongServer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}

		

}
