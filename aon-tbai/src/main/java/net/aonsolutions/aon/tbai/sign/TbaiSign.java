package net.aonsolutions.aon.tbai.sign;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.watson.server.AonDateUtils;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AdESPolicy;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import net.aonsolutions.aon.tbai.CRC8;
import ticketbai.emision.TicketBai;


public class TbaiSign {
	
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

    private static final AOSigner XADES_SIGNER = new AOXAdESSigner();
	
	public byte[] sign(TbaiConfiguration tbai, byte[] data) {
		try {
			KeyStore keyStore = getKeyStore(tbai.getCertificate());
			String alias = getAlias(keyStore);
			
			return XADES_SIGNER.sign(data,
					AOSignConstants.SIGN_ALGORITHM_SHA256WITHRSA,
					getPrivateKey(keyStore, tbai.getCertificate(), alias), 
					getCertificateChain(keyStore, alias),
					getTbaiExtraParams(tbai));
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException 
				| IOException | UnrecoverableEntryException | AOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private PrivateKey getPrivateKey(KeyStore keyStore, Certificate cert, String alias) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey) keyStore.getKey(alias, cert.getPassword().toCharArray());
	}
	
	private java.security.cert.Certificate[] getCertificateChain(KeyStore keyStore, String alias) throws KeyStoreException{
		return keyStore.getCertificateChain(alias);
	}
	
	private KeyStore getKeyStore(Certificate cert) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		ByteArrayInputStream keyData = new ByteArrayInputStream(cert.getData());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(keyData, cert.getPassword().toCharArray());
		return keyStore;
	}
	
	private String getAlias(KeyStore keyStore) throws KeyStoreException {
		Enumeration enumas = keyStore.aliases();
		String alias = null;
		while (enumas.hasMoreElements()) {
			alias = (String) enumas.nextElement();
		}
		return alias;
	}

    public Properties getTbaiExtraParams(TbaiConfiguration tbai) {
    	final Properties xParams = new Properties();
    	xParams.setProperty(AonXAdESExtraParams.FORMAT, AOSignConstants.SIGN_FORMAT_XADES_ENVELOPED);
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
    
    public String buildTbaiId(TicketBai tbai, String sign) throws UnsupportedEncodingException {
    	String dateStr = tbai.getFactura().getCabeceraFactura().getFechaExpedicionFactura();
    	Date date = AonDateUtils.parse(dateStr, "dd-MM-yyyy");
    	String tbaiId = "TBAI-" + tbai.getSujetos().getEmisor().getNIF() 
    		+ "-" + AonDateUtils.format(date, "ddMMyy")
    		+ "-" + sign.substring(0, 13) 
    		+ "-";
    	String crc = CRC8.calculate(tbaiId);
    	return tbaiId + crc;
    }
    
    public String getSign(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		Document doc = getDocument(data);
		return doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
    }
    
    public Document getDocument(byte[] data) throws ParserConfigurationException, SAXException, IOException {
		InputStream is = new ByteArrayInputStream(data);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		return doc;
	}

}
