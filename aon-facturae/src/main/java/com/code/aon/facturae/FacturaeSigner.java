package com.code.aon.facturae;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.watson.error.AonCoreException;

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.DataToSign.XADES_X_TYPES;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.role.SimpleClaimedRole;
import es.mityc.javasign.EnumFormatoFirma;
import es.mityc.javasign.xml.refs.AllXMLToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
import net.aonsolutions.aon.sign.exception.AonSignerException;

public class FacturaeSigner {
	
	private Document getDocument( byte[] data ) throws AonCoreException  {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setIgnoringElementContentWhitespace(true);					
			DocumentBuilder builder = factory.newDocumentBuilder();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			document = builder.parse(in);			
		} catch ( IOException | SAXException | ParserConfigurationException e ) {
			throw new AonCoreException(e.getMessage(), e);
		}
		return document;
	}	

	private byte[] getDocumentData( Document document ) throws AonCoreException  {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(out);
			Source input = new DOMSource(document);
			transformer.transform(input, output);
		} catch ( TransformerFactoryConfigurationError | TransformerException e ) {
			throw new AonCoreException(e.getMessage(), e);
		}
		return out.toByteArray();
	}	
	
	public byte[] sign(Certificate certificate, byte[] data) throws AonSignerException {
		return net.aonsolutions.aon.sign.FacturaeSigner.getInstance()
			.sign(certificate, data);
	}
	
	public Document sign(KeyStoreData store, Document doc) throws AonCoreException {
		int providerPos = -1;
		Provider provider = store.getProvider();
		try {
			if (provider != null) {
				providerPos = Security.addProvider(provider);
			}
			
            FirmaXML sxml = new FirmaXML();
            
            PrivateKey pk = store.getPrivateKey();
            
            DataToSign dataToSign = new DataToSign();
            dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
            dataToSign.setXAdESXType(XADES_X_TYPES.TYPE_1);
            
            dataToSign.setEsquema(XAdESSchemas.XAdES_132);
            	
            dataToSign.setXMLEncoding("UTF-8");
            
            dataToSign.setAddPolicy(true);
            dataToSign.setPolicyKey("facturae31");

            dataToSign.addClaimedRol(new SimpleClaimedRole("emisor"));
            dataToSign.setEnveloped(true);
            dataToSign.addObject(new ObjectToSign(new AllXMLToSign(), "Factura electrónica", null, "text/xml", null));
            dataToSign.setDocument(doc);
            
            Object[] res = sxml.signFile(store.getX509Certificate(), dataToSign, pk, provider);

            return (Document) res[0];
        } catch (Throwable t) {
            throw new AonCoreException(t);
        } finally {
        	if ( (provider != null) && (providerPos != -1) ) {
        		Security.removeProvider(provider.getName());
        	}       	
        }
	}
	
	public byte[] sign( KeyStoreData store, byte[] data ) throws AonCoreException {
		Document document = getDocument(data);
		if ( document != null ) {
			Document result = sign(store, document);
			return getDocumentData(result);
		}
		return null;
	}
	
}
