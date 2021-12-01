package net.aonsolutions.aon.tbai.sign;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Properties;

import com.esferalia.aon.occam.api.model.security.Certificate;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.signers.xades.AOXAdESSigner;


public class TbaiSign {

	private static final AdESPolicy POLICY_TBAI_GIPUZKOA = new AdESPolicy(
		"https://www.gipuzkoa.eus/ticketbai/sinadura", //$NON-NLS-1$
		"6NrKAm60o7u62FUQwzZew24ra2ve9PRQYwC21AM6In0=", //$NON-NLS-1$
		"SHA256", //$NON-NLS-1$
		null
	);    

    private static final AOSigner XADES_SIGNER = new AOXAdESSigner();
	
	public static byte[] sign(Certificate cert, byte[] data) {
		try {
			
			KeyStore keyStore = getKeyStore(cert);
			String alias = getAlias(keyStore);
			
			return XADES_SIGNER.sign(data,
					AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA,
					getPrivateKey(keyStore, cert, alias), 
					getCertificateChain(keyStore, alias),
					getTbaiExtraParams());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		} catch (AOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static PrivateKey getPrivateKey(KeyStore keyStore, Certificate cert, String alias) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey) keyStore.getKey(alias, cert.getPassword().toCharArray());
	}
	
	private static java.security.cert.Certificate[] getCertificateChain(KeyStore keyStore, String alias) throws KeyStoreException{
		return keyStore.getCertificateChain(alias);
	}
	
	private static KeyStore getKeyStore(Certificate cert) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		ByteArrayInputStream keyData = new ByteArrayInputStream(cert.getCertificate());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(keyData, cert.getPassword().toCharArray());
		return keyStore;
	}
	
	private static String getAlias(KeyStore keyStore) throws KeyStoreException {
		Enumeration enumas = keyStore.aliases();
		String alias = null;
		while (enumas.hasMoreElements()) {
			alias = (String) enumas.nextElement();
		}
		return alias;
	}

    public static Properties getTbaiExtraParams() {
    	final Properties xParams = new Properties();
    	xParams.setProperty(AonXAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
    	xParams.setProperty(AonXAdESExtraParams.SIGNER_CLAIMED_ROLES, "emisor");
    	xParams.putAll(POLICY_TBAI_GIPUZKOA.asExtraParams());
    	return xParams;
    }
	
		
}
