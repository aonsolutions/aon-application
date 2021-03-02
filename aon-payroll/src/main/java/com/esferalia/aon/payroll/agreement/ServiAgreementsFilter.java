package com.esferalia.aon.payroll.agreement;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ServiAgreementsFilter {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static String SERVIAGREEMENTS = "aconvenios";
	
	
	public static Map<String, String> getServiAgreementsMap(boolean isConvenios) {
		
		InputStream is = ServiAgreementsFilter.class.getResourceAsStream(SERVIAGREEMENTS + ".xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder;
	    Document document = null;
		
	    try {
			
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(is);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
		return getServiAgreementsMap(document, isConvenios);
	}

	private static Map<String, String> getServiAgreementsMap(Document document, boolean isConvenios) {
		Map<String, String> serviAgreementsMap = new HashMap<String, String>();
		serviAgreementsMap.put("00000000000000 - AON SOLUTIONS CONVENIO GENERAL",  "a0000000");
		serviAgreementsMap.put("00000000000000 - AON SOLUTIONS CONVENIO B" + String.valueOf("\u00C1") +"SICO",  "a0000001");
		serviAgreementsMap.put("00000000000000 - AON SOLUTIONS CONVENIO EST" + String.valueOf("\u00C1") + "NDAR",  "a0000002");
		
		if(isConvenios) {
		
			NodeList rowList = document.getElementsByTagName("row");
			
			for(int i=0; i < rowList.getLength(); i++) {
				Node rowNode = rowList.item(i);
	
		        if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
	
		            Element rowElement = (Element) rowNode;
		            
		            String type = rowElement.getElementsByTagName("Tipo").item(0).getTextContent();
		            String scope = rowElement.getElementsByTagName("Ambito").item(0).getTextContent();
		            String territory = rowElement.getElementsByTagName("Territorio").item(0).getTextContent();
		            String agreement = rowElement.getElementsByTagName("Convenio").item(0).getTextContent();
		            String validity = rowElement.getElementsByTagName("Vigencia").item(0).getTextContent();
		            String codeDuration = rowElement.getElementsByTagName("Codigo_y_duracion").item(0).getTextContent();
		            String documentCode = rowElement.getElementsByTagName("Documento").item(0).getTextContent();
		            String serviCode = AonStringUtils.split(documentCode, '.')[0].toLowerCase();
		            String publication = rowElement.getElementsByTagName("Publicacion").item(0).getTextContent();
		            String process = rowElement.getElementsByTagName("Proceso").item(0).getTextContent();
		            String ssCode = rowElement.getElementsByTagName("Codigo").item(0).getTextContent();
		            String lastUpdateStr = rowElement.getElementsByTagName("F_ultima").item(0).getTextContent();
		            Date lastUpdate = null;
		            try {
						lastUpdate = dateFormat.parse(lastUpdateStr);
					} catch (ParseException e) {}
		            
		           
		            serviAgreementsMap.put(ssCode.trim() + " - " + agreement.trim() + " (" + scope.trim() + " - " + territory.trim() + ")", serviCode);
		        }
			}
		}
		
		return serviAgreementsMap;
	}
	
	public static void main(String[] args) {
		ServiAgreementsFilter.getServiAgreementsMap(true);
	}
	
}
