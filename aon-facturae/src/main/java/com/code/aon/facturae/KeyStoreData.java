package com.code.aon.facturae;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import com.esferalia.aon.watson.error.AonCoreException;

public class KeyStoreData {

	private KeyStore keystore;

	private String alias;
	
	private char[] password;
	
	public KeyStoreData( KeyStore keystore, String alias, char[] password ) {
		this.keystore = keystore;
		this.alias = alias;
		this.password = password;
	}

	public X509Certificate getX509Certificate() throws AonCoreException {
		X509Certificate x509certificate = null;
		try {
			Certificate certificate = this.keystore.getCertificate(this.alias);
			if ( certificate instanceof X509Certificate ) {
				x509certificate = (X509Certificate) certificate;
			}			
		} catch ( KeyStoreException e ) {
			throw new AonCoreException(e.getMessage(), e);
		}
		return x509certificate;
	}	
	
	public PrivateKey getPrivateKey() throws AonCoreException {
		try {
			return (PrivateKey) (keystore.getKey(this.alias, password));
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new AonCoreException(e.getMessage(), e);
		}
	}

	public Provider getProvider() {
		return this.keystore.getProvider();
	}
	
}
