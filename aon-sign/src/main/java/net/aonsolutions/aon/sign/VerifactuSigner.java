package net.aonsolutions.aon.sign;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.aonsolutions.AonSecret;

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
	
	public byte[] signWithoutTransform(Certificate certificate, byte[] data) throws AonSignerException {
		return sign(certificate, data, getNoTransformExtraParams());
	}

	public Properties getTbaiExtraParams() {
    	final Properties xParams = new Properties();
    	xParams.setProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
     	xParams.setProperty(AonXAdESExtraParams.SIGNER_CLAIMED_ROLES, "emisor");
    	xParams.putAll(POLICY_VERIFACTU.asExtraParams());
    	return xParams;
    }

	public Properties getNoTransformExtraParams() {
		Properties xParams = getTbaiExtraParams();
    	xParams.setProperty(XAdESExtraParams.AVOID_XPATH_EXTRA_TRANSFORMS_ON_ENVELOPED, Boolean.TRUE.toString());
    	return xParams;
    }
	
	public static void main(String[] args) throws FileNotFoundException, IOException, AonSignerException {
		try ( InputStream is = new FileInputStream(args[0]);
				OutputStream os = new FileOutputStream(args[0]+".sign") ) {
			byte [] data = VerifactuSigner.getInstance().signWithoutTransform(AonSecret.getAonCert(), is.readAllBytes());
			os.write(data);
		} 
	}

}
