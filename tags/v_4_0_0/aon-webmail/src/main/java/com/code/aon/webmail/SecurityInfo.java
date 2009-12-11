package com.code.aon.webmail;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityInfo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityInfo.class);

	private KeyStore keystore;
	
	private String alias;
	
	private String password;
	
	public SecurityInfo(KeyStore keystore, String alias, String password) {
		this.keystore = keystore;
		this.alias = alias;
		this.password = password;
	}

	public SecurityInfo(KeyStore keystore, String password) {
		this( keystore, getDefaultAlias(keystore), password );
	}
	
	private static String getDefaultAlias( KeyStore keystore ) {
		Enumeration<String> aliases;
		try {
			aliases = keystore.aliases();
			while (aliases.hasMoreElements()) {
				return aliases.nextElement();
			}
		} catch (KeyStoreException e) {
			LOGGER.error(e.getMessage(), e );
		}
		return null;
	}
	
	public KeyStore getKeystore() {
		return keystore;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPassword() {
		return password;
	}

	public Certificate[] getCertificateChain() throws KeyStoreException {
		return keystore.getCertificateChain(alias);
	}
	
	public PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey)keystore.getKey(alias, password.toCharArray());
	}
	
}
