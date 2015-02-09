import java.math.BigInteger;

public class KryptoServer {
	
	private  BigInteger  choose1;
	private  BigInteger choose2;
	public int rounds = 0;
	public BigInteger n = new BigInteger("0"); //Öffentlicher Schlüssel n
	private BigInteger e;  
	BigInteger d; 
	private final int[] primeArray = {11093, 11113, 11117, 11119, 11131, 11149, 11159, 11161, 11171, 11173, 11177, 11197, 11213, 11239,	
									  15107, 15121, 15131, 15137, 15139, 15149, 15161, 15173, 15187, 15193, 15199, 15217, 15227, 15233,
									  17011, 17021, 17027, 17029, 17033, 17041, 17047, 17053, 17077, 17093, 17099, 17107, 17117, 17123,
									  18089, 18097, 18119, 18121, 18127, 18131, 18133, 18143, 18149, 18169, 18181, 18191, 18199, 18211,
									  18217, 18223, 18229, 18233, 18251, 18253, 18257, 18269, 18287, 18289, 18301, 18307, 18311, 18313};
	private int subKey;
	
	public KryptoServer() {
		subKey = 3;
	}
	/**
	 * setSubKey(int value) setzt den Wert der privaten subKey Variable
	 * @param value
	 */
	public void setSubKey(int value) {
		subKey = value;
	}
	
	/**
	 * public String intToString(int integer) wandelt einen Integerwert in entsprechende
	 * Stringrepräsentation
	 * @param integer
	 * @return - Stringdarstellung eines Integerwerts
	 */
	public String intToString(int integer) {
		Integer meinInteger = new Integer(integer);
        return meinInteger.toString(); 
	}
	/**
	 * public String intArrayToString(int[] integerArray) konsumiert ein Array aus 
	 * Integerwerten und wandelt diesen in eine entsprechende Reprästentation 
	 * als String um
	 * @param integerArray
	 * @return - Stringdarstellung eines IntegerArrays
	 */
	public String intArrayToString(int[] integerArray) {
		
		String returnMessage = "";
		for(int i = 0; i < integerArray.length; i++) {
			returnMessage += intToString(integerArray[i]);
			//System.out.println(returnMessage);
		}
		return returnMessage;
	}
	/**
	 * public char[] Parser(String message) konsumiert einen String
	 * und wandelt diesen in einen CharArray bestehend aus den Chars des Strings
	 * @param message
	 * @return - CharArray aus String
	 */
	public char[] Parser(String message){
		return message.toCharArray();
	}
	/**
	 * private String substituteCharsEncrypt(char[] messageArray) konsumiert ein CharArray
	 * und gibt eine Stringrepräsentation des verschobenen Alphabets zurück
	 * @param messageArray
	 * @return - substitutedString
	 */
	private String substituteCharsEncrypt(char[] messageArray) {
		String cipher = "";
		int position;
		for(int i = 0; i < messageArray.length; i++) {
			if((255 - (int) messageArray[i] <= subKey)) { // wenn subkey größer als verbleibende Buchstaben im Alphabet
				//System.out.println("subkey1: " + subKey);
				position = subKey -(255 - (int) messageArray[i]);
			}
			else{ position = (int) messageArray[i] + subKey; }
			cipher += (char) position;
		}
		return cipher;
	}
	
	/**
	 * public String encryptMessage(String message, int repeat) konsumiert die zu 
	 * verschlüsselnde Nachricht, und die Häufigkeit der Durchgänge und führt 
	 * Substitution auf Message mit der Wiederholungsanzahl die repeat übergibt durch
	 * @param message
	 * @param repeat
	 * @return - final substitutedString
	 */
	public String encryptMessage(String message, int repeat) {
		if(repeat-1 > 0) {
			return encryptMessage(substituteCharsEncrypt(Parser(message)), repeat-1);
		}
		return substituteCharsEncrypt(Parser(message));
	}
	
	/**
	 * public String[] stringToStringArray(String string) konsumiert einen Strings und
	 * wandelt diesen in ein StringArray um z.B. abc -> [a],[b],[c]
	 * @param string
	 * @return - StringArray
	 */
	public String[] stringToStringArray(String string)  {
			
		char[] charArrayOfString = string.toCharArray();			
		String[] message = new String[charArrayOfString.length];	
			for(int i = 0; i < charArrayOfString.length; i++) {
				message[i] = String.valueOf(charArrayOfString[i]);
			}
		return message;
	}
		
	/**
	 * public int[] stringArrayToIntegerArray(String[] string) konsumiert
	 * StringArray und wandelt dieses in ein IntegerArray um.
	 * z.B. ["12"],["13"],["14"] -> [12],[13],[14]
	 * @param string
	 * @return - IntegerArray
	 */
	public int[] stringArrayToIntegerArray(String[] string) {
		int[] compareArray = new int[string.length];
		for(int i = 0; i < string.length; i++) {
			compareArray[i] = Integer.parseInt(string[i]);
		}
		return compareArray;	
	}
		
	/**
	 * public String decryptMessage(String message, int rounds) konsumiert die zu 
	 * entschlüsselnde Nachricht, und die Häufigkeit der Durchgänge und führt 
	 * Substitution auf Message mit der Wiederholungsanzahl die repeat übergibt durch
	 * @param cipherMessage
	 * @param rounds
	 * @return
	 */
	public String decryptMessage(String cipherMessage, int rounds) {
		if(rounds-1 > 0) {
			return decryptMessage(deSubstitute(cipherMessage.toCharArray()), rounds-1);
			}
		return   deSubstitute(cipherMessage.toCharArray());
	}
	/**
	 * private String deSubstitute(char[] cipherArray) konsumiert ein CharArray
	 * und Wiederholungen der Substitution und wendet dies an
	 * @param cipherArray
	 * @return - cleartext
	 */
	private String deSubstitute(char[] cipherArray) {
		int position = 0;
		String clearText = "";
		for(int i = 0; i < cipherArray.length; i++) {	
			//calc position
			if(cipherArray[i] < subKey) {  // wenn subkey größer als verbleibende Buchstaben im Alphabet
				position = 255 -(subKey - cipherArray[i]);
			}
			else{ 
				position = cipherArray[i] - subKey; 			}
				clearText += (char) position; 				// -1 -> Verschiebung von Alphabet zu ArrayCounter
			}
			return clearText;
	}
	/**
	 * public void getTwoRandomPrimes() erzeugt zwei Zufallszahlen und setzt gibt diese
	 * an die globalen Variablen this.choose1 und this.choose2  
	 */
	public void getTwoRandomPrimes(){
		do{
			choose1 = new BigInteger(intToString(primeArray[(int) ((Math.random() * primeArray.length))]));
			choose2 = new BigInteger(intToString(primeArray[(int) ((Math.random() * primeArray.length))]));
			}while(choose1 == choose2);	
			System.out.println(choose1 + "  |  " + choose2);
		}
	/**
	 * private void calcN(BigInteger p, BigInteger q) konsumiert zwei Primzahlen vom Typ
	 * BigInteger und berechnet deren Produkt (n) und gibt diesen Wert an this.n
	 */
	private void calcN(BigInteger p, BigInteger q){
		n =  p.multiply(q);
		}
	/**
	 * public void calcE(BigInteger phi_N) konsumiert phi(n) und errechnet 
	 * damit einen Teil des publicKeys (e) und setzt this.e
	 * @param phi_N
	 */
	public void calcE(BigInteger phi_N) {		
		BigInteger ggT;
		BigInteger i = new BigInteger("1000");
		do{
			i= i.add(BigInteger.ONE);
			ggT = phi_N.gcd(i);
		}while(!ggT.equals(BigInteger.ONE));
		e = i; //set public Key 
		}
	/**
	 * public void calcD(BigInteger phi_N, BigInteger e) konsumiert phi(n) und 
	 * e (publicKey) und errechnet den privateKey (d) und setzt this.d
	 * @param phi_N
	 * @param e
	 */
	public void calcD(BigInteger phi_N, BigInteger e) {
			
			BigInteger result =  null;
			BigInteger i = new BigInteger("2");
			
			while(!(i.multiply(phi_N).add(BigInteger.ONE).remainder(e).equals(BigInteger.ZERO))) {
				
				i = i.add(BigInteger.ONE);
				result = (i.multiply(phi_N).add(BigInteger.ONE)).divide(e);
			}
			System.out.println("D: " + result);
			d = result; //return D
		}
	/**
	 * public void startRSA() startet notwendige Funktionalitäten des RSA-Systems
	 */
	public BigInteger startRSA(){		 
		getTwoRandomPrimes();
	    calcN(choose1, choose2);
        BigInteger phi_N = (choose1.subtract(BigInteger.ONE)).multiply( choose2.subtract( BigInteger.ONE) );
        calcE(phi_N);
	    //d = (phi_N * s  + 1) / e
        calcD(phi_N, e);
        System.out.println("n: " + n + " e: " + e + " d: " + d);
	    //System.out.println("Cipher: " + message.modPow(e, n));
        //System.out.println("Message: " + message.modPow(e,n).modPow(d, n));
	    System.out.println("e: " + e + " n: " + n);
	    return e;
		}
	/**
	 * public BigInteger privateKeyDecrypt(BigInteger message, BigInteger e, BigInteger n) entchlüsselt mit 
	 * entsprechendem privateKey verschlüsselte Nachricht
	 * @param message
	 * @param e
	 * @param n
	 * @return
	 */
	public BigInteger privateKeyDecrypt(BigInteger message, BigInteger e, BigInteger n) {
		return message.modPow(e, n); 
		}
		
	}//End of File