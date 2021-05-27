package com.esferalia.aon.ui.sepe.file;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RespuestaCertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.RespuestaCertificadoEmpresa.Resultado;

public class CertificadosResponseReader {
	
	private final String CERTIFICADOS_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.certificados.certificadoEmpresa";
	
	
	public RespuestaCertificadoEmpresa fichero;
	
	public RespuestaCertificadoEmpresa getFicheroCertificado() {
		return fichero;
	}

	public void setFicheroCertificado(RespuestaCertificadoEmpresa fichero) {
		this.fichero = fichero;
	}

	public void readFile(InputStream input) throws IOException, JAXBException, SAXException, ParserConfigurationException{
		
		try {
			input.reset();
				
			JAXBContext jc = JAXBContext.newInstance(CERTIFICADOS_CONTRATOS_MODEL_PATH);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
			reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
			SAXSource source = new SAXSource(xmlFilter, new InputSource(input));
			
			fichero = (RespuestaCertificadoEmpresa) unmarshaller.unmarshal(source);
				
		} finally {
			input.close();
		}
	}
	
	public Resultado getRepuestaCertificado(Object envioType){
		((RespuestaCertificadoEmpresa) envioType).getHuellaDigital();
		return ((RespuestaCertificadoEmpresa) envioType).getResultado();
	}

	
	///////////////////////////////////////////////////
	// AUXILIARES 
	///////////////////////////////////////////////////
	
	
	public class XMLNamespaceFilter extends XMLFilterImpl {
	    public XMLNamespaceFilter(XMLReader arg0) {
	       super(arg0);
	    }
	    @Override
	    public void startElement(String uri, String localName,
	                             String qName, Attributes attributes)
	                             throws SAXException {
//	       super.startElement(<required namespace>, localName, qName, attributes);
	       super.startElement("", localName, qName, attributes);
	    }
	}
	
}

