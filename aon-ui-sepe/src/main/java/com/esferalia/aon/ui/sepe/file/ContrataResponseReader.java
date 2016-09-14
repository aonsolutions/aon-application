package com.esferalia.aon.ui.sepe.file;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO100TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO130TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO150TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO200TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO230TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO250TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO300TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO330TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO350TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO401TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO402TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO403TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO410TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO420TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO421TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO430TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO441TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO450TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO501TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO502TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO503TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO510TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO520TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO530TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO540TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO541TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO550TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO970TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO980TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.ENVIO990TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.FICHEROCONTRATOS;
import com.esferalia.aon.sepe.api.contrata.contratos.RESPUESTACONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.ENVIOTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.FICHEROPRORROGAS;
import com.esferalia.aon.sepe.api.contrata.prorrogas.RESPUESTAPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO109TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO139TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO189TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO209TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO239TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO289TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO309TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO339TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.ENVIO389TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.FICHEROTRANSFORMACIONES;
import com.esferalia.aon.sepe.api.contrata.transformaciones.RESPUESTATRANSFORMACIONTYPE;

public class ContrataResponseReader {
	
	private final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.contratos";
	private final String CONTRATA_PRORROGAS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.prorrogas";
	private final String CONTRATA_TRANSFORMACIONES_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.transformaciones";
	
	
	public FICHEROCONTRATOS ficheroContratos;
	public FICHEROPRORROGAS ficheroProrrogas;
	public FICHEROTRANSFORMACIONES ficheroTransformaciones;
	
	public FICHEROCONTRATOS getFicheroContratos() {
		return ficheroContratos;
	}

	public void setFicheroContratos(FICHEROCONTRATOS ficheroContratos) {
		this.ficheroContratos = ficheroContratos;
	}

	public FICHEROPRORROGAS getFicheroProrrogas() {
		return ficheroProrrogas;
	}

	public void setFicheroProrrogas(FICHEROPRORROGAS ficheroProrrogas) {
		this.ficheroProrrogas = ficheroProrrogas;
	}
	
	public FICHEROTRANSFORMACIONES getFicheroTransformaciones() {
		return ficheroTransformaciones;
	}
	
	public void setFicheroTransformacines(FICHEROTRANSFORMACIONES ficheroTransformaciones) {
		this.ficheroTransformaciones = ficheroTransformaciones;
	}

	public void readContratoFile(InputStream input) throws IOException, JAXBException, SAXException, ParserConfigurationException{
		try {
			input.reset();
				
			JAXBContext jc = JAXBContext.newInstance(CONTRATA_CONTRATOS_MODEL_PATH);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
			reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
			SAXSource source = new SAXSource(xmlFilter, new InputSource(input));
			
			ficheroContratos = (FICHEROCONTRATOS) unmarshaller.unmarshal(source);
				
		} finally {
			input.close();
		}
	}

	public void readProrrogaFile(InputStream input) throws IOException, JAXBException, SAXException, ParserConfigurationException{
		try {
			input.reset();
			
			JAXBContext jc = JAXBContext.newInstance(CONTRATA_PRORROGAS_MODEL_PATH);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
			reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
			SAXSource source = new SAXSource(xmlFilter, new InputSource(input));
			
			ficheroProrrogas = (FICHEROPRORROGAS) unmarshaller.unmarshal(source);
			
		} finally {
			input.close();
		}
	}
	
	public void readTrasformacionFile(InputStream input) throws IOException, JAXBException, SAXException, ParserConfigurationException{
		try {
			input.reset();
			
			JAXBContext jc = JAXBContext.newInstance(CONTRATA_TRANSFORMACIONES_MODEL_PATH);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
			reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
			SAXSource source = new SAXSource(xmlFilter, new InputSource(input));
			
			ficheroTransformaciones = (FICHEROTRANSFORMACIONES) unmarshaller.unmarshal(source);
			
		} finally {
			input.close();
		}
	}
	
	public RESPUESTACONTRATOTYPE getRepuestaContrato(Object envioType){
		if(envioType instanceof ENVIO100TYPE){
			return ((ENVIO100TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO130TYPE){
			return ((ENVIO130TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO150TYPE){
			return ((ENVIO150TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO200TYPE){
			return ((ENVIO200TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO230TYPE){
			return ((ENVIO230TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO250TYPE){
			return ((ENVIO250TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO300TYPE){
			return ((ENVIO300TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO330TYPE){
			return ((ENVIO330TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO350TYPE){
			return ((ENVIO350TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO401TYPE){
			return ((ENVIO401TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO402TYPE){
			return ((ENVIO402TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO403TYPE){
			return ((ENVIO403TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO410TYPE){
			return ((ENVIO410TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO420TYPE){
			return ((ENVIO420TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO421TYPE){
			return ((ENVIO421TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO430TYPE){
			return ((ENVIO430TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO441TYPE){
			return ((ENVIO441TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO450TYPE){
			return ((ENVIO450TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO501TYPE){
			return ((ENVIO501TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO502TYPE){
			return ((ENVIO502TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO503TYPE){
			return ((ENVIO503TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO510TYPE){
			return ((ENVIO510TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO520TYPE){
			return ((ENVIO520TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO530TYPE){
			return ((ENVIO530TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO540TYPE){
			return ((ENVIO540TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO541TYPE){
			return ((ENVIO541TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO550TYPE){
			return ((ENVIO550TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO970TYPE){
			return ((ENVIO970TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO980TYPE){
			return ((ENVIO980TYPE) envioType).getRESPUESTACONTRATO();
		} else if(envioType instanceof ENVIO990TYPE){
			return ((ENVIO990TYPE) envioType).getRESPUESTACONTRATO();
		}
		return null;
	}

	public IContratoType getContratoType(Object envioType){
		if(envioType instanceof ENVIO100TYPE){
			return ((ENVIO100TYPE) envioType).getCONTRATO100();
		} else if(envioType instanceof ENVIO130TYPE){
			return ((ENVIO130TYPE) envioType).getCONTRATO130();
		} else if(envioType instanceof ENVIO150TYPE){
			return ((ENVIO150TYPE) envioType).getCONTRATO150();
		} else if(envioType instanceof ENVIO200TYPE){
			return ((ENVIO200TYPE) envioType).getCONTRATO200();
		} else if(envioType instanceof ENVIO230TYPE){
			return ((ENVIO230TYPE) envioType).getCONTRATO230();
		} else if(envioType instanceof ENVIO250TYPE){
			return ((ENVIO250TYPE) envioType).getCONTRATO250();
		} else if(envioType instanceof ENVIO300TYPE){
			return ((ENVIO300TYPE) envioType).getCONTRATO300();
		} else if(envioType instanceof ENVIO330TYPE){
			return ((ENVIO330TYPE) envioType).getCONTRATO330();
		} else if(envioType instanceof ENVIO350TYPE){
			return ((ENVIO350TYPE) envioType).getCONTRATO350();
		} else if(envioType instanceof ENVIO401TYPE){
			return ((ENVIO401TYPE) envioType).getCONTRATO401();
		} else if(envioType instanceof ENVIO402TYPE){
			return ((ENVIO402TYPE) envioType).getCONTRATO402();
		} else if(envioType instanceof ENVIO403TYPE){
			return ((ENVIO403TYPE) envioType).getCONTRATO403();
		} else if(envioType instanceof ENVIO410TYPE){
			return ((ENVIO410TYPE) envioType).getCONTRATO410();
		} else if(envioType instanceof ENVIO420TYPE){
			return ((ENVIO420TYPE) envioType).getCONTRATO420();
		} else if(envioType instanceof ENVIO421TYPE){
			return ((ENVIO421TYPE) envioType).getCONTRATO421();
		} else if(envioType instanceof ENVIO430TYPE){
			return ((ENVIO430TYPE) envioType).getCONTRATO430();
		} else if(envioType instanceof ENVIO441TYPE){
			return ((ENVIO441TYPE) envioType).getCONTRATO441();
		} else if(envioType instanceof ENVIO450TYPE){
			return ((ENVIO450TYPE) envioType).getCONTRATO450();
		} else if(envioType instanceof ENVIO501TYPE){
			return ((ENVIO501TYPE) envioType).getCONTRATO501();
		} else if(envioType instanceof ENVIO502TYPE){
			return ((ENVIO502TYPE) envioType).getCONTRATO502();
		} else if(envioType instanceof ENVIO503TYPE){
			return ((ENVIO503TYPE) envioType).getCONTRATO503();
		} else if(envioType instanceof ENVIO510TYPE){
			return ((ENVIO510TYPE) envioType).getCONTRATO510();
		} else if(envioType instanceof ENVIO520TYPE){
			return ((ENVIO520TYPE) envioType).getCONTRATO520();
		} else if(envioType instanceof ENVIO530TYPE){
			return ((ENVIO530TYPE) envioType).getCONTRATO530();
		} else if(envioType instanceof ENVIO540TYPE){
			return ((ENVIO540TYPE) envioType).getCONTRATO540();
		} else if(envioType instanceof ENVIO541TYPE){
			return ((ENVIO541TYPE) envioType).getCONTRATO541();
		} else if(envioType instanceof ENVIO550TYPE){
			return ((ENVIO550TYPE) envioType).getCONTRATO550();
		} else if(envioType instanceof ENVIO970TYPE){
			return ((ENVIO970TYPE) envioType).getCONTRATO970();
		} else if(envioType instanceof ENVIO980TYPE){
			return ((ENVIO980TYPE) envioType).getCONTRATO980();
		} else if(envioType instanceof ENVIO990TYPE){
			return ((ENVIO990TYPE) envioType).getCONTRATO990();
		}
		return null;
	}
	
	public RESPUESTAPRORROGATYPE getRepuestaProrroga(Object envioType){
		if(envioType instanceof ENVIOTYPE){
			return ((ENVIOTYPE) envioType).getRESPUESTAPRORROGA();
		}
		return null;
	}
	
	public RESPUESTATRANSFORMACIONTYPE getRepuestaTransformacion(Object envioType){
		if(envioType instanceof ENVIO109TYPE){
			return ((ENVIO109TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO139TYPE){
			return ((ENVIO139TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO189TYPE){
			return ((ENVIO189TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO209TYPE){
			return ((ENVIO209TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO239TYPE){
			return ((ENVIO239TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO289TYPE){
			return ((ENVIO289TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO309TYPE){
			return ((ENVIO309TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO339TYPE){
			return ((ENVIO339TYPE) envioType).getRESPUESTATRANSFORMACION();
		} else if(envioType instanceof ENVIO389TYPE){
			return ((ENVIO389TYPE) envioType).getRESPUESTATRANSFORMACION();
		}
		return null;
	}

	public ITransformacionType getTransformacionType(Object envioType){
		if(envioType instanceof ENVIO109TYPE){
			return ((ENVIO109TYPE) envioType).getTRANSFORMACION109();
		} else if(envioType instanceof ENVIO139TYPE){
			return ((ENVIO139TYPE) envioType).getTRANSFORMACION139();
		} else if(envioType instanceof ENVIO189TYPE){
			return ((ENVIO189TYPE) envioType).getTRANSFORMACION189();
		} else if(envioType instanceof ENVIO209TYPE){
			return ((ENVIO209TYPE) envioType).getTRANSFORMACION209();
		} else if(envioType instanceof ENVIO239TYPE){
			return ((ENVIO239TYPE) envioType).getTRANSFORMACION239();
		} else if(envioType instanceof ENVIO289TYPE){
			return ((ENVIO289TYPE) envioType).getTRANSFORMACION289();
		} else if(envioType instanceof ENVIO309TYPE){
			return ((ENVIO309TYPE) envioType).getTRANSFORMACION309();
		} else if(envioType instanceof ENVIO339TYPE){
			return ((ENVIO339TYPE) envioType).getTRANSFORMACION339();
		} else if(envioType instanceof ENVIO389TYPE){
			return ((ENVIO389TYPE) envioType).getTRANSFORMACION389();
		}
		return null;
	}
	
	
	///////////////////////////////////////////////////
	// AUXILIARES 
	///////////////////////////////////////////////////
	
	public class ContractValidationEventHandler implements ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR || ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid booking document: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}
	
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

