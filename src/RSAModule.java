import java.math.BigInteger;


public class RSAModule {
	
	private BigInteger  choose1;
	private BigInteger choose2;
	public int rounds = 0;
	public BigInteger n = new BigInteger("0"); //Öffentlicher Schlüssel n
	public BigInteger e;  
	BigInteger d; 
	private final int[] primeArray = {11093, 11113, 11117, 11119, 11131, 11149, 11159, 11161, 11171, 11173, 11177, 11197, 11213, 11239,	
									  15107, 15121, 15131, 15137, 15139, 15149, 15161, 15173, 15187, 15193, 15199, 15217, 15227, 15233,
									  17011, 17021, 17027, 17029, 17033, 17041, 17047, 17053, 17077, 17093, 17099, 17107, 17117, 17123,
									  18089, 18097, 18119, 18121, 18127, 18131, 18133, 18143, 18149, 18169, 18181, 18191, 18199, 18211,
									  18217, 18223, 18229, 18233, 18251, 18253, 18257, 18269, 18287, 18289, 18301, 18307, 18311, 18313};
	private int subKey;
	
	public RSAModule() {
		startRSA();
	}
	/**
	 * public void getTwoRandomPrimes() erzeugt zwei Zufallszahlen und gibt diese
	 * an die globalen Variablen this.choose1 und this.choose2  
	 */
	public void getTwoRandomPrimes(){
		do{
			choose1 = new BigInteger(intToString(primeArray[(int) ((Math.random() * primeArray.length))]));
			choose2 = new BigInteger(intToString(primeArray[(int) ((Math.random() * primeArray.length))]));
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
        //System.out.println("n: " + n + " e: " + e + " d: " + d);
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
		return message.modPow(e, n); 
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
