package net.aonsolutions.infovox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.infovox.OCRInvofox;
import net.aonsolutions.infovox.json.OCRCompaniesResponseJSON;
import net.aonsolutions.infovox.json.OCRDocumentResponseJSON;
import net.aonsolutions.infovox.json.OCRDocumentsResponseJSON;
import net.aonsolutions.infovox.json.OCRErrorJSON;
import net.aonsolutions.infovox.model.OCRCompaniesResponse;
import net.aonsolutions.infovox.model.OCRDocumentResponse;
import net.aonsolutions.infovox.model.OCRDocumentsResponse;
import net.aonsolutions.infovox.model.OCRError;

class InfovoxRESTTestCase {
	
	@Test
	void getInvalidDocument() {
		String documentId = "DOCUMENTO_NO_VALIDO";
		OCRDocumentResponse response = OCRInvofox.getDocument(documentId);
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 400, response.getHttpCode().get());
		assertTrue(response.getError().isPresent());
		assertFalse(response.getDocument().isPresent());
		OCRError ocrError = response.getError().get(); 
		assertTrue(ocrError.getCode().isPresent());
		assertEquals( "ERR_WRONG_PARAM", ocrError.getCode().get());
		System.out.println( OCRErrorJSON.to(ocrError).toString(1) );
	}
	
	@Test
	void getValidDocument() {
		String documentId = "648088d6c632f4000891fa82";
		OCRDocumentResponse response = OCRInvofox.getDocument(documentId);
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getDocument().isPresent());
		assertTrue(response.getDocument().get().getId().isPresent());
		assertEquals( documentId, response.getDocument().get().getId().get());
		System.out.println( OCRDocumentResponseJSON.to(response).toString(1) );
	}
	
	@Test
	void getDocuments() {
		OCRDocumentsResponse response = OCRInvofox.getDocuments();
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getDocuments().isPresent());
		assertNotEquals(0 , response.getDocuments().get().size());
		System.out.println( OCRDocumentsResponseJSON.to(response).toString(1) );
		System.out.println( "Documents ..: " + response.getDocuments().get().size() );
	}
	
	@Test
	void getCompanies() {
		OCRCompaniesResponse response = OCRInvofox.getCompanies();
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getCompanies().isPresent());
		assertNotEquals(0 , response.getCompanies().get().size());
		System.out.println( OCRCompaniesResponseJSON.to(response).toString(1) );
		System.out.println( "Companies..: " + response.getCompanies().get().size() );
	}
	
	
}