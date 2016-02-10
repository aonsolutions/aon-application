package com.esferalia.aon.gwt.viewer.server.html2Image;

import java.io.Reader;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;

public interface HtmlParser extends DocumentHolder{
	
	DOMParser getDomParser();

	void setDomParser(DOMParser domParser);

	void setDocument(Document document);

	void load(Reader reader);

	void loadHtml(String html);

}
