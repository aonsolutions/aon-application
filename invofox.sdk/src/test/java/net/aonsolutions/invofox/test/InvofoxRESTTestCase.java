package net.aonsolutions.invofox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.aonsolutions.invofox.OCRCompanyParams;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.json.OCRCompanyJSON;
import net.aonsolutions.invofox.json.OCRCompanyResponseJSON;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRCompanyResponse;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRError;
import net.aonsolutions.invofox.model.OCRType;

class InvofoxRESTTestCase {
	
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
	}
	
	@Test
	void getDocuments() {
		OCRDocumentsResponse response = OCRInvofox.getDocuments(
				OCRDocumentsParams.get().withType(OCRType.invoice) );
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getDocuments().isPresent());
		assertNotEquals(0 , response.getDocuments().get().size());
	}
	
	@Test
	void createCompany() {
		OCRCompany company = OCRFaker.getCompany();
		System.out.println( OCRCompanyJSON.to(company).toString(1) );
		OCRCompanyResponse response = OCRInvofox.postCompany( company );
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertTrue( response.getHttpCode().get() == 200 
			|| response.getHttpCode().get() == 201
			|| response.getHttpCode().get() == 409, OCRCompanyResponseJSON.to(response).toString());
//		if ( response.getHttpCode().get() == 200 || response.getHttpCode().get() == 201) {
//			assertTrue(response.getCompany().isPresent());
//			assertNotEquals(0 , response.getCompany().get().getId());
//		} else {
//			assertTrue(response.getError().isPresent());
//			assertTrue(response.getError().get().getCode().isPresent());
//			System.out.println( response.getError().get().getCode().orElse(null) );
//		}
	}

	@Test
	void getCompanies() {
		OCRCompaniesResponse response = OCRInvofox.getCompanies( OCRCompanyParams.get().withTaxId("B01487271") );
		
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getCompanies().isPresent());
		assertNotEquals(0 , response.getCompanies().get().size());
	}
	
	@Test
	void getCompanyInvoices() {
		OCRDocumentsResponse response = OCRInvofox.getCompanyInvoices( "B01487271" );
		
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getDocuments().isPresent());
		assertNotEquals(0 , response.getDocuments().get().size());
	}
		
}