package net.aonsolutions.core.tgss.creta.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class NamespaceFilterHandler implements ContentHandler {

	private static final Map<String,String> MAPPINGS = new HashMap<String,String>() {
		{
			//put("http://www.seg-social.es/creta/esquemas/V110/Respuesta", "http://www.seg-social.es/creta/esquemas/V120/Respuesta");
			put("http://www.seg-social.es/creta/esquemas/V110/Respuesta", "http://www.seg-social.es/creta/esquemas/V130/Respuesta");
			put("http://www.seg-social.es/creta/esquemas/V120/Respuesta", "http://www.seg-social.es/creta/esquemas/V130/Respuesta");

			put("http://www.seg-social.es/creta/esquemas/V100/TrabajadoresTramos", "http://www.seg-social.es/creta/esquemas/V110/TrabajadoresTramos");
		}
	};
	
	ContentHandler _handler;

	public NamespaceFilterHandler(ContentHandler ch) {
		_handler = ch;
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (!qName.equals(localName)) {
			// If the element name has a namespace prefix (like 'ns1:foo'),
			// then we want to replace it with the localName (which would be just foo').
			// Also, leave the URI blank, since thats also namespace related.
			_handler.startElement("", localName, localName, atts);
		} else {
			_handler.startElement(MAPPINGS.getOrDefault(uri, uri), localName, qName, atts);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		_handler.characters(ch, start, length);
	}

	public void endDocument() throws SAXException {
		_handler.endDocument();
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		_handler.endElement(uri, localName, qName);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		_handler.endPrefixMapping(prefix);
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		_handler.ignorableWhitespace(ch, start, length);
	}

	public void processingInstruction(String target, String data) throws SAXException {
		_handler.processingInstruction(target, data);
	}

	public void setDocumentLocator(Locator locator) {
		_handler.setDocumentLocator(locator);
	}

	public void skippedEntity(String name) throws SAXException {
		_handler.skippedEntity(name);
	}

	public void startDocument() throws SAXException {
		_handler.startDocument();
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		_handler.startPrefixMapping(prefix, uri);
	}

}
