import java.math.BigInteger;
import java.util.Random;


public class RSAModule {
	
	private BigInteger  choose1;
	private BigInteger choose2;
	public int rounds = 0;
	public BigInteger n = new BigInteger("0"); //Öffentlicher Schlüssel n
	public BigInteger e;  
	BigInteger d; 
	
	public RSAModule() {
		startRSA();
	}
	/**
	 * public void getTwoRandomPrimes() erzeugt zwei Zufallszahlen und gibt diese
	 * an die globalen Variablen this.choose1 und this.choose2  
	 */
	public void getTwoRandomPrimes(){
		do{
			choose1 = BigInteger.probablePrime(128, new Random());
			choose2 = BigInteger.probablePrime(128, new Random());
			}while(choose1 == choose2);	
			//System.out.println(choose1 + "  |  " + choose2);
		}
	/**
	 * private void calcN(BigInteger p, BigInteger q) konsumiert zwei Primzahlen vom Typ
	 * BigInteger und berechnet deren Produkt (n) und gibt diesen Wert an this.n
	 */
	private void calcN(){
		n =  choose1.multiply(choose2);
		}
	/**
	 * public void calcE(BigInteger phi_N) konsumiert phi(n) und errechnet 
	 * damit einen Teil des publicKeys (e) und setzt this.e
	 * @param phi_N
	 */
	public void calcE(BigInteger phi_N) {		
		BigInteger ggT;
		BigInteger i = new BigInteger("100000");
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
	public void calcD(BigInteger phi_N) {
			
			BigInteger result =  null;
			BigInteger i = new BigInteger("2");
			
			while(!(i.multiply(phi_N).add(BigInteger.ONE).remainder(e).equals(BigInteger.ZERO))) {
				
				i = i.add(BigInteger.ONE);
				result = (i.multiply(phi_N).add(BigInteger.ONE)).divide(e);
			}
			//System.out.println("D: " + result);
			d = result; //return D
		}
	/**
	 * public void startRSA() startet notwendige Funktionalitäten des RSA-Systems
	 */
	public void startRSA(){		 
		getTwoRandomPrimes();
	    calcN();
        BigInteger phi_N = (choose1.subtract(BigInteger.ONE)).multiply( choose2.subtract( BigInteger.ONE) );
        calcE(phi_N);
        calcD(phi_N);
        System.out.println("n: " + n + " e: " + e + " d: " + d);
	    //System.out.println("e: " + e + " n: " + n);
		}
	/**
	 * public BigInteger privateKeyDecrypt(BigInteger message, BigInteger e, BigInteger n) entchlüsselt mit 
	 * entsprechendem privateKey verschlüsselte Nachricht
	 * @param message
	 * @param e
	 * @param n
	 * @return
	 */
	public BigInteger privateKeyDecrypt(BigInteger message) {
		return message.modPow(d, n); 
		}
	
	/**
	 * 
	 * @param message
	 * @param e
	 * @param n
	 * @return
	 */
	public BigInteger publicKeyEncrypt(BigInteger message) {
		return message.modPow(e, n); 
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

}
