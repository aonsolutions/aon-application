package net.aonsolutions.aon.sign;

import java.util.Properties;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import net.aonsolutions.aon.sign.exception.AonSignerException;
import net.aonsolutions.aon.sign.params.AonXAdESExtraParams;

public class TbaiSigner extends XadesSigner {
	
	private static final String SHA_256 = "SHA256";

	private static final AdESPolicy POLICY_TBAI_GIPUZKOA = new AdESPolicy(
		"https://www.gipuzkoa.eus/ticketbai/sinadura",
		"6NrKAm60o7u62FUQwzZew24ra2ve9PRQYwC21AM6In0=",
		SHA_256,
		null
	);
	
	private static final AdESPolicy POLICY_TBAI_BIZKAIA = new AdESPolicy(
		"https://www.batuz.eus/fitxategiak/batuz/ticketbai/sinadura_elektronikoaren_zehaztapenak_especificaciones_de_la_firma_electronica_v1_0.pdf",
		"Quzn98x3PMbSHwbUzaj5f5KOpiH0u8bvmwbbbNkO9Es=",
		SHA_256,
		null
	);
	
	private static final AdESPolicy POLICY_TBAI_ARABA = new AdESPolicy(
		"https://ticketbai.araba.eus/tbai/sinadura/",
		"iOgvkX7/yHIDRRiPy/LYQ0UUn7QV8/11D1BFbs8yMuQ=",
		SHA_256,
		null
	);
	
	public static TbaiSigner getInstance() {
		return new TbaiSigner();
	}
	
	public byte[] sign(TbaiConfiguration tbai, byte[] data) throws AonSignerException {
		return sign(tbai.getCertificate(), data, getTbaiExtraParams(tbai));
	}
	
	public Properties getTbaiExtraParams(TbaiConfiguration tbai) {
    	final Properties xParams = new Properties();
    	xParams.setProperty(XAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
     	xParams.setProperty(AonXAdESExtraParams.SIGNER_CLAIMED_ROLES, "emisor");
    	xParams.putAll(getPolicyTbai(tbai).asExtraParams());
    	return xParams;
    }
	
	private AdESPolicy getPolicyTbai(TbaiConfiguration tbai) {
	  	if(tbai.isAraba()) {
	   		return POLICY_TBAI_ARABA;
	   	} else if(tbai.isBizkaia()) {
	   		return POLICY_TBAI_BIZKAIA;
	   	} else return POLICY_TBAI_GIPUZKOA;    		
	}
}
