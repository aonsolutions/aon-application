package com.esferalia.aon.dex.process;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.dex.IDataLoadConstants;
import com.esferalia.aon.dex.shared.DateTimeAdapter;
import com.esferalia.aon.dex.shared.PosShiftDexResponse;

public class CommonLoadManager implements IDataLoadConstants {

	private DateTimeAdapter dateTimeAdapter;

	protected DateTimeAdapter getDateTimeAdapter() {
		if (dateTimeAdapter == null) {
			dateTimeAdapter = new DateTimeAdapter();
		}
		return dateTimeAdapter;
	}

	protected String documentSuccess(int numRegsOk, int numRegsDup) {
		PosShiftDexResponse response = new PosShiftDexResponse();
		response.setOperation(OPERATION_OK);
		response.setNumRegsOk(numRegsOk);
		response.setNumRegsDup(numRegsDup);

		return getJaxbDocument(response);
	}

	protected String documentError(String message, int numRegsOk, int numRegsDup) {
		PosShiftDexResponse response = new PosShiftDexResponse();
		response.setOperation(OPERATION_KO);
		response.setError((message!=null) ? message : "NULL");
		response.setNumRegsOk(numRegsOk);
		response.setNumRegsDup(numRegsDup);

		return getJaxbDocument(response);
	}

	protected String getJaxbDocument(Object jaxbElement) {
		try {
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(jaxbElement.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.encoding", "ISO-8859-1");
			marshaller.marshal(jaxbElement, writer);
			return StringUtils.trim(writer.toString());
		} catch (JAXBException ex) {
			return ERROR;
		}
	}

}