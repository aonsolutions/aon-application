package com.code.aon.conexflow;

import java.math.BigInteger;
import java.security.Security;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.esferalia.aon.watson.server.codec.binary.AonHex;

public class ClaveMAC {
	private static final Logger LOGGER  = Logger.getLogger(ClaveMAC.class.getName());

	static Cipher cipher = null;
	static Cipher cipher2 = null;
	static int ctLength = 0;
	static SecretKeySpec key2 = null;
	static SecretKeySpec key3 = null;
		
	/*Encripta un texto dado con la clave pasada como parametro */
	public static byte[] encriptaTexto(byte[] input, SecretKeySpec key)  throws Throwable {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
			ctLength = cipher.update(input, 0, input.length, cipherText, 0);
			ctLength += cipher.doFinal(cipherText, ctLength);
			return cipherText;
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} 
	}
     
	/*Desencripta un texto dado con la clave pasada como parametro */
	public static byte[] desencriptaTexto(byte[] cipherText, SecretKeySpec key)  throws Throwable {
		try {
			cipher2.init(Cipher.DECRYPT_MODE, key);
			byte[] plainText = new byte[cipher2.getOutputSize(ctLength)];
			int ptLength = cipher2.update(cipherText, 0, ctLength, plainText, 0);
			ptLength += cipher2.doFinal(plainText, ptLength);
			return plainText;
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} 
	}
     
	/* Realiza una XOR de Arrays de Bytes */
	public static byte[] ejecutaXORArrayBytes (byte[] cipherText, byte[] input2)  throws Throwable {
		try {
			byte[] resultadoFinal = new byte[8];
			for (int i=0; i<8;i++) {
				resultadoFinal[i] =  (byte) (cipherText[i] ^ input2[i]);
			}
			
			return resultadoFinal;
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw e;
		} 
	}
	
	/* Convierte el valor del String directamente en bytes */
	private static byte[]  StringHex2ArrayBytes(String sHex) {
		int len = sHex.length();
		
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(sHex.charAt(i), 16) << 4)
					+ Character.digit(sHex.charAt(i+1), 16));
		}	
		return data;
	}	

	public static String dameClaveMAC(String cad, String claveIzquierda, String claveDerecha)  throws Throwable {
		try {  
			/* PRIMER PASO: Añadimos el Proveedor BounceCastle 
			 * */
			
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());        
			cipher = Cipher.getInstance("DES/ECB/NoPadding", "BC");
			cipher2 = Cipher.getInstance("DES/ECB/NoPadding", "BC");
			
			/* SEGUNDO PASO: Parseo de los datos de entrada.
			 * NOTA.- Se sobreentiende que las claves están en HEXADECIMAL cuando se recojan. Al estilo
			 * 193D59719B1B2662. Si no fuera así, habría que calcular su formato en HEX, al igual que se hace con el texto a parsear (String.format)
			 * */
			
			byte[] data = StringHex2ArrayBytes(claveIzquierda);
			key3 = new SecretKeySpec(data, "DES");
			
			byte[] data2 = StringHex2ArrayBytes(claveDerecha);
			key2 = new SecretKeySpec(data2, "DES");
			cad = String.format("%x", new BigInteger(cad.getBytes("ascii"))) + "80";
		     
			/* TERCER PASO: Calculo del Padding de los datos a parsear
			 * NOTA.- Existen numerosos 'Padding' que se pueden añadir al 'CIPHER', y seguramente haya uno que exactamente haga esto,
			 * pero los que he probado daban resultados distintos y me he decantado por hacerlo a mano y que el CIPHER no realice ningún tipo de Padding.
			 * */
		        
			if (cad.length() % 16 != 0) {
				for (int i = (cad.length() % 16); i<16; i++) {
					cad = cad + "0";
		        }
			}
		      
			/* CUARTO PASO: División por bloques
			 * /Una vez realizado el Padding a manubrio, lo dividimos en bloque de 8 bytes (16 caracteres) para poder realizarlo dinámicamente
			 * */
			
			Vector<String> vEntrada = new Vector<String>();
			String tmp = cad;
			int numeroBloques = cad.length() / 16;
			for (int i=0;i< numeroBloques ;i++) {
				vEntrada.add(i,tmp.substring(0,16));
				
				if (i < numeroBloques -1 ) {
					tmp =tmp.substring(16,tmp.length());
				}
			}
			
			/* QUINTO PASO: Ejecución del algortimo
			 * Por cada bloque:
			 * 	- Se encripta con la parte A (izquierda) de la clave
			 *  - Se realiza una XOR con el siguiente bloque
			 *  Hasta que se terminen los bloques
			 *   - Encriptamos resultados con parte A.
			 *  - Se desencripta el resultado final con la parte B (derecha) de la clave
			 *  - Se vuelve a encriptar con la parte A de la clave
			 * */
			
			byte[] cipherText = StringHex2ArrayBytes(vEntrada.elementAt(0)); // Inicializamos el resultado con el primer elemento.
			for (int i=0; i<vEntrada.size()-1;i++){ //Encriptamos + XOR de todas las partes intermedias
				cipherText  = encriptaTexto (cipherText,key3);
				cipherText = ejecutaXORArrayBytes(cipherText,StringHex2ArrayBytes(vEntrada.elementAt(i+1)));
			}
			
			//Encriptamos A + Desencriptamos B + Encriptamos A
			cipherText  = encriptaTexto (cipherText,key3);
			cipherText  = desencriptaTexto (cipherText,key2);
			cipherText  = encriptaTexto (cipherText,key3);
			  
			/* SEXTO PASO: Enviamos los 4 bytes de la izquierda */
			return  new String(AonHex.encodeHex(cipherText)).substring(0,8);
	  	} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
	  		throw e;	
	  	}
	}
	   
	public static void main(String[] args) {
		String text = "N000000010057000011082015092503465892763422331800000000000000000155";
		String mac = "";
		try {
			//Claves de prueba --> 193D59719B1B2662 A8DB8A2EF1223B4E
			//Claves de produccion --> 76BD80DE8054E28A 47E3C3722D99B1BC
			mac = ClaveMAC.dameClaveMAC(text, "76BD80DE8054E28A", "47E3C3722D99B1BC");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println(mac);
		Calendar c = Calendar.getInstance();
		System.out.println(c.get(Calendar.DATE));
		System.out.println(c.get(Calendar.MONTH)+1);
		System.out.println(c.get(Calendar.YEAR));
	}
}
