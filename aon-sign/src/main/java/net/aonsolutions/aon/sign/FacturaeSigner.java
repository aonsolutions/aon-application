package net.aonsolutions.aon.sign;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Properties;

import com.esferalia.aon.occam.api.model.Certificate;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import net.aonsolutions.aon.sign.exception.AonSignerException;

public class FacturaeSigner extends Signer {

	public static FacturaeSigner getInstance() {
		return new FacturaeSigner();
	}
	
    private static final AOFacturaESigner SIGNER = new AOFacturaESigner();
	
    public byte[] sign(Certificate certificate, byte[] data) throws AonSignerException {
		return sign(certificate, data, new Properties());
	}
	
	public byte[] sign(Certificate certificate, byte[] data, Properties extraParams) throws AonSignerException {
		try {
			KeyStore keyStore = getKeyStore(certificate);
			String alias = getAlias(keyStore);
			
			return SIGNER.sign(data,
					AOSignConstants.SIGN_ALGORITHM_SHA512WITHRSA,
					getPrivateKey(keyStore, certificate, alias), 
					getCertificateChain(keyStore, alias),
					extraParams);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException 
				| IOException | UnrecoverableEntryException | AOException e) {
			e.printStackTrace();
			throw new AonSignerException();
		}
	}
}
