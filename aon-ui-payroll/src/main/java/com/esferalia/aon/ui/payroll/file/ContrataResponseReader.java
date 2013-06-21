package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.event.AbortProcessingException;
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

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
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

public class ContrataResponseReader {
	
	private final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.contratos";
	
	
	public FICHEROCONTRATOS ficheroContratos;
	
	public FICHEROCONTRATOS getFicheroContratos() {
		return ficheroContratos;
	}

	public void setFicheroContratos(FICHEROCONTRATOS ficheroContratos) {
		this.ficheroContratos = ficheroContratos;
	}

	public void readFile(InputStream input) throws ManagerBeanException, IOException{
		
		try {
			input.reset();
				
			JAXBContext jc = JAXBContext.newInstance(CONTRATA_CONTRATOS_MODEL_PATH);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();
			XMLFilterImpl xmlFilter = new XMLNamespaceFilter(reader);
			reader.setContentHandler(unmarshaller.getUnmarshallerHandler());
			SAXSource source = new SAXSource(xmlFilter, new InputSource(input));
			
			FICHEROCONTRATOS ficheroContratos = (FICHEROCONTRATOS) unmarshaller.unmarshal(source);
			ficheroContratos.getCONTRATOSPROCESADOS();
			ficheroContratos.getESTADOFICHERO();
			ficheroContratos.getNUMEROPROCESADOS();
			
			setFicheroContratos(ficheroContratos);
				
		} catch (JAXBException e) {
			String msg = "Error al obtener los datos del documento xml de contrata";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
			throw new AbortProcessingException(msg, e);
		} catch (SAXException e) {
			String msg = "Error al obtener los datos del documento xml de contrata";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
			throw new AbortProcessingException(msg, e);
		} catch (ParserConfigurationException e) {
			String msg = "Error al obtener los datos del documento xml de contrata";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
			throw new AbortProcessingException(msg, e);
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

