import java.math.BigInteger;
import java.security.CryptoPrimitive;

public class KryptoServer {
	
	private int subKey;
	
	public KryptoServer(BigInteger subKey) {
		this.subKey = Integer.parseInt(subKey.toString());
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
		
	}//End of File