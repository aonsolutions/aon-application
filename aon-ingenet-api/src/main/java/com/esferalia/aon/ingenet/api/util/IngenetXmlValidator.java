package com.esferalia.aon.ingenet.api.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class IngenetXmlValidator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SCHEMA_FILE_NAME_ALBARANES = "EsquemaAlbaranes.xsd";

	public static final String SCHEMA_FILE_NAME_CONSULTA_ELABORACIONES = "EsquemaConsultaElaboraciones.xsd";

	public static final String SCHEMA_FILE_NAME_RESPUESTA_ELABORACIONES = "EsquemaRespuestaElaboraciones.xsd";

	/*
	 * JAXB
	 */
	public static Object extractValue(String xml, Class<?> clazz)
			throws IOException {
		InputStream inputStream = null;
		try {
			byte[] bytes = xml.getBytes("UTF-8");
			inputStream = new ByteArrayInputStream(bytes);
			String contextPath = clazz.getPackage().getName();
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setEventHandler(new IngenetValidationEventHandler());
			return unmarshaller.unmarshal(inputStream);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public static String convertToXml(Object source, Class<?>... type) {
		String result;
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(type);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.marshal(source, sw);
			result = sw.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private static class IngenetValidationEventHandler implements
			ValidationEventHandler {
		public boolean handleEvent(ValidationEvent ve) {
			if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
					|| ve.getSeverity() == ValidationEvent.ERROR) {
				ValidationEventLocator locator = ve.getLocator();
				// Print message from valdation event
				System.out.println("Invalid value: " + locator.getURL());
				System.out.println("Error: " + ve.getMessage());
				// Output line and column number
				System.out.println("Error at column "
						+ locator.getColumnNumber() + ", line "
						+ locator.getLineNumber());
			}
			return true;
		}
	}

	public static void validateXmlPattern(InputStream xmlStream,
			String schemaFileName) throws IOException, SAXException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, "META-INF/schemas", schemaFileName);
		// SchemaFactory sf =
		// SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		SchemaFactory sf = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = sf.newSchema(urls[0]);
		StreamSource source = new StreamSource(xmlStream);
		Validator validator = schema.newValidator();
		validator.validate(source);
	}

}
