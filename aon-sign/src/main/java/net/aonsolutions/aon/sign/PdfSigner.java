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
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import net.aonsolutions.aon.sign.exception.AonSignerException;

public class PdfSigner extends Signer {

	public static PdfSigner getInstance() {
		return new PdfSigner();
	}
	
    private static final AOSigner SIGNER = new AOPDFSigner();
//	private static final String IZENPE_TSA_URL = "http://ocsp.izenpe.com:8093"; 
//	private static final String ACCV_TSA_URL = "http://tss.accv.es:8318/tsa";
	private static final String CATCERT_TSA_URL = "http://psis.catcert.net/psis/catcert/tsp";
	
	public byte[] sign(Certificate certificate, byte[] data) throws AonSignerException {
		return sign(certificate, data, getExtraParams());
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
	
    public Properties getExtraParams() {
    	final Properties xParams = new Properties();
    	xParams.put("tsaURL", CATCERT_TSA_URL);
    	xParams.put("tsType", TsaParams.TS_SIGN);
    	return xParams;
    }
	
}
