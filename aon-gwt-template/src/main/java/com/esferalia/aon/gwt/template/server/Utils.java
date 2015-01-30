package com.esferalia.aon.gwt.template.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.gwt.template.shared.TemplateInfo;

public class Utils {

	public static TemplateInfo readxml(File fXmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	 
		doc.getDocumentElement().normalize();
	 	 
		Element root = doc.getDocumentElement();
		String type = root.getAttribute("type");
		
		NodeList nList = doc.getElementsByTagName("column");
	 
		Vector<String> columns = new Vector<String>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			String column = nList.item(temp).getTextContent();
			columns.add(column);
		}
		TemplateInfo ti = new TemplateInfo();
		ti.setType(type);
		ti.setColumns(columns);
		
		return ti;
	}
	
	public static byte[] newXmlFile(TemplateInfo ti) {
		/*<?xml version="1.0" encoding="UTF-8" standalone="no"?>
		<template name="lalala" type="product">
			<columns>
				<column id = 1>

				</column>
				.....
				<column id = N>

				</column>
			</columns>
		</template>*/
		byte[] b = null;
		  try {
	 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("template");
			doc.appendChild(rootElement);
	 
			// set attribute to staff element
			Attr attr = doc.createAttribute("name");
			attr.setValue(ti.getName());
			
			Attr attr2 = doc.createAttribute("type");
			attr2.setValue(ti.getType());
			
			rootElement.setAttributeNode(attr);
			rootElement.setAttributeNode(attr2);
	 
			// shorten way
			// staff.setAttribute("id", "1");
	 
			// firstname elements
			Element columns = doc.createElement("columns");
			rootElement.appendChild(columns);
	 
			for (String value : ti.getColumns()) {
				Element column = doc.createElement("column");
				columns.appendChild(column);
				column.appendChild(doc.createTextNode(value));
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
				
			
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			StreamResult result = new StreamResult(bos);
	 
			transformer.transform(source, result);
			
			b = bos.toByteArray();
			
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		  return b;
	}
	
}
