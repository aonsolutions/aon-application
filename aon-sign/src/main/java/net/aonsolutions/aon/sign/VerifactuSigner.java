package net.aonsolutions.aon.sign;

import java.util.Properties;

import com.esferalia.aon.occam.api.model.Certificate;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import net.aonsolutions.aon.sign.exception.AonSignerException;
import net.aonsolutions.aon.sign.params.AonXAdESExtraParams;

public class VerifactuSigner extends XadesSigner {
	
	private static final String SHA_1 = "SHA1";
	
	private static final AdESPolicy POLICY_VERIFACTU = new AdESPolicy(
		"https://sede.administracion.gob.es/politica_de_firma_anexo_1.pdf",
		"G7roucf600+f03r/o0bAOQ6WAs0=",
		SHA_1,
		null
	);
	
	public static VerifactuSigner getInstance() {
		return new VerifactuSigner();
	}
	
	public byte[] sign(Certificate certificate, byte[] data) throws AonSignerException {
		return sign(certificate, data, getTbaiExtraParams());
	}
	
	public Properties getTbaiExtraParams() {
    	final Properties xParams = new Properties();
    	xParams.setProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
     	xParams.setProperty(AonXAdESExtraParams.SIGNER_CLAIMED_ROLES, "emisor");
    	xParams.putAll(POLICY_VERIFACTU.asExtraParams());
    	return xParams;
    }
}
