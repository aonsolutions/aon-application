package com.code.aon.hyperview.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.SAXException;

public class HyperViewParser {

	private static String RULES = "hyperview_digester_rules.xml";

	public HyperView parse(InputStream input) throws IOException, SAXException {
		URL rules = HyperViewParser.class.getResource(RULES);
		Digester digester = DigesterLoader.createDigester(rules);
		digester.setUseContextClassLoader(true);
		HyperView xmlWorkstation = (HyperView) digester.parse(input);
		return xmlWorkstation;
	}
	
	//TODO DELETE -- Test method
	public static void main(String[] args) throws IOException, SAXException {
		FileInputStream fis = new FileInputStream(
				"C:\\Documents and Settings\\ecastellano\\Configuración local\\Temp\\Suplierszzz37650.exhvw.xml");
		HyperViewParser parser = new HyperViewParser();
		HyperView hv = parser.parse(fis);
		System.out.println(hv);
	}
}
