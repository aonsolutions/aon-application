package com.code.aon.ui.audabridge;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class AudaBridgeTest extends TestCase {
	
	private static String ACCESS_KEY = "67210648";
	private static String USER_KEY = "127958";
	private static String WAN ="WANES96000001573";
	
/*
	public void testWanRequest() throws Exception {
		try {
			AudaBridgeManager manager = new AudaBridgeManager();
			String resp = manager.getWanRequest(ACCESS_KEY, "0000001569", "7FB522");
			System.out.println( "OK.: " + resp );
		} catch (AudaBridgeException e) {
			System.out.println("PDF: " +  e.getMessage());
		}
	}

	public void testPDFRequest() throws Exception {
		try {
			AudaBridgeManager manager = new AudaBridgeManager();
			manager.getPDFRequest(ACCESS_KEY, "WANES09000001587");
		} catch (AudaBridgeException e) {
			System.out.println("PDF: " +  e.getMessage());
		}
	}

	public void testAuditCalculationRequest() throws Exception {
		try {
			AudaBridgeManager manager = new AudaBridgeManager();
			manager.getAuditCalculationRequest(ACCESS_KEY, "WANES96000001573");
		} catch (AudaBridgeException e) {
			System.out.println("XML: " +  e.getMessage());
		}
	}
	
	public void testXMLRequest() throws Exception {
		try {
			AudaBridgeManager manager = new AudaBridgeManager();
			manager.getXMLRequest(ACCESS_KEY, "WANES10000001569");
		} catch (AudaBridgeException e) {
			System.out.println("XML: " +  e.getMessage());
		}
	}


	public void testCreateXMLRequest() throws Exception {
		CreateAssessmentRequest request = getRequest();
		StringWriter writer = new StringWriter();
		request.createXMLRequest(writer);
		System.out.println( writer.toString());
	}
	
	private CreateAssessmentRequest getRequest() {
		CreateAssessmentRequest request = new CreateAssessmentRequest();
		request.setReferenceNumber("2001/0000001");
		request.setCustomerId(USER_KEY);
		request.setPaintRate(25.5);
		request.setBodyworkRate(25.5);
		request.setLabourRate(25.5);
		request.setRegistration("1234ABC");
		request.setOdometer(12485);
		return request;
	}

	public void testCreateAssessment() throws Exception {
		AudaBridgeManager manager = new AudaBridgeManager();
		String wan = manager.createAssessmentRequest(ACCESS_KEY, getRequest());
		System.out.println( "WAN ..: " + wan);
	}

	public void testAssessmentToken() throws Exception {
		AudaBridgeManager manager = new AudaBridgeManager();
		String token = manager.getToken(ACCESS_KEY, WAN);
		System.out.println( "Token..: " + token);
	}
*/
	public void testParseCreateAssessmentResponse() throws Exception {
		String result = 
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>"
			+"<Message>"
			+"    <Header>"
			+"        <MessageTypeIdentifier>CreateAssessmentResponse</MessageTypeIdentifier>"
			+"        <MessageCreatedDate>2011-06-29T13:15:29.849+02:00</MessageCreatedDate>"
			+"    </Header>"
			+"    <Body>"
			+"        <CreateAssessmentResponse>"
			+"            <Wan>WANES96000001573</Wan>"
			+"        </CreateAssessmentResponse>"
			+"    </Body>"
			+"</Message>";	
		ByteArrayInputStream input = new ByteArrayInputStream(result.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(input);
		NodeList nodes = document.getElementsByTagName("Wan" );
		if (nodes != null && nodes.getLength() > 0) {
			Node wanNode = nodes.item(0);
			System.out.println( wanNode.getTextContent());
		} else {
			fail("No se encuentra wan");	
		}
	}
}
