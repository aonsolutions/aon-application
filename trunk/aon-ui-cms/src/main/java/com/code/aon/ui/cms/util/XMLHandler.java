package com.code.aon.ui.cms.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {

	private Stack<String> stack = new Stack<String>();
	private String data = new String();
	private HashMap<String, String> map = new HashMap<String, String>();

	public void startElement(String namespaceUri, String localName, String qualifiedName, Attributes attributes) throws SAXException {
		stack.push(qualifiedName);
	}
	public void endElement(String namespaceUri, String localName, String qualifiedName) throws SAXException {
		if (!"".equals(data.trim())){
			Iterator<String> iter = stack.iterator();
			String path = "";
			while (iter.hasNext()){
				path += (path==""?"":"_") + iter.next();
			}
			if (map.get(path)==null) map.put(path,data);
			else map.put(path,map.get(path)+"|"+data);
		}
		data = "";
		stack.pop();
	}
	
	public void characters(char[] chars, int startIndex, int endIndex) {
		data += new String(chars, startIndex, endIndex);
	}

	public HashMap<String, String> getDataMap(){
		return map;
	}
}
