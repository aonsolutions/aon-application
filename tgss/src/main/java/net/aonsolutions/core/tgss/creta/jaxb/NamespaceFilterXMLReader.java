package net.aonsolutions.core.tgss.creta.jaxb;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class NamespaceFilterXMLReader implements XMLReader {

	private XMLReader _reader;

	public NamespaceFilterXMLReader() throws SAXException, ParserConfigurationException {
		SAXParserFactory parserFactory;
		parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		// there is no point in asking a validation because
		// there is no guarantee that the document will come with
		// a proper schemaLocation.
		parserFactory.setValidating(false);
		_reader = parserFactory.newSAXParser().getXMLReader();
	}

	public ContentHandler getContentHandler() {
		return _reader.getContentHandler();
	}

	public DTDHandler getDTDHandler() {
		return _reader.getDTDHandler();
	}

	public EntityResolver getEntityResolver() {
		return _reader.getEntityResolver();
	}

	public ErrorHandler getErrorHandler() {
		return _reader.getErrorHandler();
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return _reader.getFeature(name);
	}

	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return _reader.getProperty(name);
	}

	public void parse(InputSource input) throws IOException, SAXException {
		_reader.parse(input);
	}

	public void parse(String systemId) throws IOException, SAXException {
		_reader.parse(systemId);
	}

	public void setContentHandler(ContentHandler handler) {
		// This is jaxb inserting its sax-&gt;jaxb connector
		_reader.setContentHandler(new NamespaceFilterHandler(handler));
	}

	public void setDTDHandler(DTDHandler handler) {
		_reader.setDTDHandler(handler);
	}

	public void setEntityResolver(EntityResolver resolver) {
		_reader.setEntityResolver(resolver);
	}

	public void setErrorHandler(ErrorHandler handler) {
		_reader.setErrorHandler(handler);
	}

	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		_reader.setFeature(name, value);
	}

	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		_reader.setProperty(name, value);
	}

}