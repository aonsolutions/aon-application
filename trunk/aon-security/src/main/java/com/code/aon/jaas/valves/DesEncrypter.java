/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Encrypting a File or Stream with DES.
 * Encrypting and decrypting files or streams using DES. 
 * The class is created with a key and can be used repeatedly to encrypt and decrypt streams 
 * using that key. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 09/11/2007
 *
 */
public class DesEncrypter {

	/** DesEncrypter Log */
	private static final Log LOGGER = LogFactory.getLog( DesEncrypter.class.getName() );

	private static final byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };
    // Iteration count
	private static final int iterationCount = 19;

	Cipher ecipher;
	Cipher dcipher;
	// Buffer used to transport the bytes from one stream to another
	byte[] buf = new byte[1024];
	 // 8-byte Salt
      
	public DesEncrypter(SecretKey key) {
		try {
			ecipher = Cipher.getInstance(key.getAlgorithm());
	        dcipher = Cipher.getInstance(key.getAlgorithm());

	        // Prepare the parameter to the ciphers
	        AlgorithmParameterSpec paramSpec = new PBEParameterSpec( salt, iterationCount );

			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (java.security.InvalidAlgorithmParameterException e) {
			LOGGER.warn( e.getMessage() );
		} catch (javax.crypto.NoSuchPaddingException e) {
			LOGGER.warn( e.getMessage() );
		} catch (java.security.NoSuchAlgorithmException e) {
			LOGGER.warn( e.getMessage() );
		} catch (java.security.InvalidKeyException e) {
			LOGGER.warn( e.getMessage() );
		}
	}

	public void encrypt(InputStream in, OutputStream out) {
		try {
			// Bytes written to out will be encrypted
			out = new CipherOutputStream( out, ecipher );
  
			// Read in the cleartext bytes and write to out to encrypt
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		} catch (java.io.IOException e) {
			LOGGER.warn( e.getMessage() );
		}
	}

	public void decrypt(InputStream in, OutputStream out) {
		try {
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);
  
			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;
			while ((numRead = in.read(buf)) >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		} catch (java.io.IOException e) {
			LOGGER.warn( e.getMessage() );
		}
	}

	public SealedObject encrypt(Serializable ser) {
		try {
			//Seal (encrypt) the object
	        return new SealedObject( ser, ecipher );
		} catch (java.io.IOException e) {
			LOGGER.warn( e.getMessage() );
	    } catch (javax.crypto.IllegalBlockSizeException e) {
			LOGGER.warn( e.getMessage() );
	    }
	    return null;
	}

	public Object decrypt(SealedObject so) {
        try {
            // Unseal (decrypt) the class
			return so.getObject( dcipher );
		} catch (IllegalBlockSizeException e) {
			LOGGER.warn( e.getMessage() );
		} catch (BadPaddingException e) {
			LOGGER.warn( e.getMessage() );
		} catch (IOException e) {
			LOGGER.warn( e.getMessage() );
		} catch (ClassNotFoundException e) {
			LOGGER.warn( e.getMessage() );
		}
		return null;
	}

	public static final SecretKey getSecretKeyInstance(String passPhrase) 
				throws InvalidKeySpecException, NoSuchAlgorithmException {
		// Create the key
		KeySpec keySpec = new PBEKeySpec( passPhrase.toCharArray(), DesEncrypter.salt, DesEncrypter.iterationCount );
		return SecretKeyFactory.getInstance( "PBEWithMD5AndDES" ).generateSecret( keySpec );
	}

}
