package net.aonsolutions.aon.tbai;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;

public class SignXml {

	
	public static String sign(TbaiConfiguration tbaiConfiguration, InputStream doc, OutputStream out) {
		try {
			XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

			Transform envelopedTransform = xmlSignatureFactory.newTransform(Transform.ENVELOPED , (XMLStructure) null);
			DigestMethod sha1DigestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA1 , null);

			// Crear elemento de referencia
			Reference reference = xmlSignatureFactory.newReference("" ,
					sha1DigestMethod ,
					Collections.singletonList(envelopedTransform),
					null , null);

			SignatureMethod rsaSignatureMethod = xmlSignatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1 , null);
			CanonicalizationMethod canonicalizationMethod = xmlSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE ,
					(XMLStructure) null);
			// Crear elemento SignedInfo
			SignedInfo signedInfo = xmlSignatureFactory.newSignedInfo(
					canonicalizationMethod ,
					rsaSignatureMethod,
					Collections.singletonList(reference));

			// Crea un par de claves
			ByteArrayInputStream keyData = new ByteArrayInputStream(tbaiConfiguration.getCertificate().getCertificate());
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(keyData, tbaiConfiguration.getCertificate().getPassword().toCharArray());
			
			// Obtener alias
			Enumeration enumas = keyStore.aliases();
			String alias = null;
			while (enumas.hasMoreElements()) {
				alias = (String) enumas.nextElement();
			}

			// Prepara el par de llaves
			Key key = keyStore.getKey(alias, tbaiConfiguration.getCertificate().getPassword().toCharArray());
			KeyPair keyPair = null;
			if (key instanceof PrivateKey) {
				Certificate cert = keyStore.getCertificate(alias);
				PublicKey publicKey = cert.getPublicKey();
				keyPair = new KeyPair(publicKey, (PrivateKey) key);
			}

			X509Certificate x509 = (X509Certificate) keyStore.getCertificate(alias);

			KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
			List x509Content = new ArrayList();
			x509Content.add(x509);
			x509Content.add(x509.getSubjectX500Principal().getName());
			X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			Document document = documentBuilderFactory.newDocumentBuilder().
					parse(doc);
			DOMSignContext domSignContext = new DOMSignContext(keyPair.getPrivate() , document.getDocumentElement());
			
			// Crear elemento de firma
			XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo , keyInfo);
			xmlSignature.sign(domSignContext);

			System.out.println(Base64.getEncoder().encodeToString(xmlSignature.getSignatureValue().getValue()).substring(0, 100));
			// Salida del archivo procesado
	            
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(document) , new StreamResult(out));
	            
			return Base64.getEncoder().encodeToString(xmlSignature.getSignatureValue().getValue()).substring(0, 100);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	private void validate() {
		try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);

            Document document = documentBuilderFactory.newDocumentBuilder().
                    parse(new FileInputStream(""));
            NodeList nodeList = document.getElementsByTagNameNS(XMLSignature.XMLNS , "Signature");

            if(nodeList.getLength() == 0){
                throw new Exception("Cannot find Signature element");
            }

            DOMValidateContext validateContext = new DOMValidateContext(new X509KeySelector() , nodeList.item(0));

            XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
            XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(validateContext);

            boolean result = xmlSignature.validate(validateContext);

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public class X509KeySelector extends KeySelector {
	    @Override
	    public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method, XMLCryptoContext context)
	            throws KeySelectorException {

	        Iterator keyInfos = keyInfo.getContent().iterator();
	        while (keyInfos.hasNext()) {
	            XMLStructure xmlStructure = (XMLStructure) keyInfos.next();
	            if (!(xmlStructure instanceof X509Data)) {
	                continue;
	            }

	            X509Data x509Data = (X509Data) xmlStructure;

	            Iterator x509s = x509Data.getContent().iterator();
	            while (x509s.hasNext()) {
	                Object object = x509s.next();
	                if (!(object instanceof X509Certificate)) {
	                    continue;
	                }

	                final PublicKey publicKey = ((X509Certificate) object).getPublicKey();

	                if (algEquals(method.getAlgorithm() , publicKey.getAlgorithm())) {
	                    return new KeySelectorResult() {
	                        @Override
	                        public Key getKey() {
	                            return publicKey;
	                        }
	                    };
	                }
	            }
	        }
	        throw new KeySelectorException("No key found");
	    }


	    private boolean algEquals(String algURI , String algName){
	        if (algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)
	                || algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
	            return true;
	        }
	        return false;
	    }
	}
	
}
