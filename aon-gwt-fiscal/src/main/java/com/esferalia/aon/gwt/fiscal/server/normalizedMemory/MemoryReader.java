package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Memory;


public class MemoryReader {

	private final static String MODEL_PATH = "com.esferalia.aon.gwt.fiscal.server.normalizedMemory.xml";
	
	public void readXml(Memory memory) throws AonSQLException {
		try {
			JAXBContext context = JAXBContext.newInstance(MODEL_PATH);
				
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(new MemoryValidationEventHandler());
			
			InputStream input = new ByteArrayInputStream(memory.getData());
			Object o = unmarshaller.unmarshal(input);
			
			
//			IContratoType contratoType = (IContratoType) contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
//			this.params = new ContrataContratoParams();
//			completeContratosParams(contratoType, (ContrataContratoParams) params);
			
		} catch (JAXBException e) {
			String msg = "Error al obtener el contexto de Contrat@ para contratos";
//			AonUtil.addErrorMessage(msg);
//			AonUtil.addErrorMessage(e.toString());
			throw new AonSQLException(e);
		}
		
	}
	
	
	
	public class MemoryValidationEventHandler implements ValidationEventHandler {
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
}
