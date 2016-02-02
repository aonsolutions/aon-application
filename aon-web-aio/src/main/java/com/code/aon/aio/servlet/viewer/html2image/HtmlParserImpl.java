package com.code.aon.aio.servlet.viewer.html2image;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class HtmlParserImpl implements HtmlParser {
	private DOMParser domParser;
	private Document document;

	public HtmlParserImpl() {
		domParser = new DOMParser(new HTMLConfiguration());
		try {
			domParser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
		} catch (SAXNotRecognizedException e) {
			throw new ParseException("Can't create HtmlParserImpl", e);
		} catch (SAXNotSupportedException e) {
			throw new ParseException("Can't create HtmlParserImpl", e);
		}
	}

	@Override
	public DOMParser getDomParser() {
		return domParser;
	}

	@Override
	public void setDomParser(DOMParser domParser) {
		this.domParser = domParser;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public void load(Reader reader) {
		try {
			domParser.parse(new InputSource(reader));
			document = domParser.getDocument();
		} catch (SAXException e) {
			throw new ParseException("SAXException while parsing HTML.", e);
		} catch (IOException e) {
			throw new ParseException("IOException while parsing HTML.", e);
		} finally {
			try {
				reader.close();
			} catch (IOException ignore) {
			}
		}
	}
	
	@Override
	public void loadHtml(String html) {
		load(new StringReader(html));
	}
}
