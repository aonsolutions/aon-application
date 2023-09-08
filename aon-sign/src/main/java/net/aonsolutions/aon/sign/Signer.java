package net.aonsolutions.aon.sign;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import com.esferalia.aon.occam.api.model.Certificate;

public class Signer {

	protected PrivateKey getPrivateKey(KeyStore keyStore, Certificate cert, String alias) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey) keyStore.getKey(alias, cert.getPassword().toCharArray());
	}
	
	protected java.security.cert.Certificate[] getCertificateChain(KeyStore keyStore, String alias) throws KeyStoreException{
		return keyStore.getCertificateChain(alias);
	}
	
	protected KeyStore getKeyStore(Certificate cert) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		ByteArrayInputStream keyData = new ByteArrayInputStream(cert.getData());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(keyData, cert.getPassword().toCharArray());
		return keyStore;
	}
	
	protected String getAlias(KeyStore keyStore) throws KeyStoreException {
		Enumeration<String> enumas = keyStore.aliases();
		String alias = null;
		while (enumas.hasMoreElements()) {
			alias = enumas.nextElement();
		}
		return alias;
	}
}
