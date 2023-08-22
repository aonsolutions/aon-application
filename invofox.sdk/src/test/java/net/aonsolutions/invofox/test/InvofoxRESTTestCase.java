package net.aonsolutions.invofox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
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
import net.aonsolutions.invofox.model.OCRLoginTokenResponse;
import net.aonsolutions.invofox.model.OCRSeverity;
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
	@Disabled("Disabled due 403 ")
	void markAsExported() {
		String documentId = "648991a5226c11000964a87b";
		OCRDocumentResponse response = OCRInvofox.markAsExported(documentId);
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getDocument().isPresent());
		assertTrue(response.getDocument().get().getId().isPresent());
		assertEquals( documentId, response.getDocument().get().getId().get());
		assertTrue(response.getDocument().get().getPublicState().isPresent());
		assertEquals( OCRSeverity.exported, response.getDocument().get().getPublicState().get());
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
		
		response.getDocuments().get()
			.stream()
			.filter( d ->
				d.getData()
				.flatMap( i -> i.getRecipientTaxId() )
				.flatMap( n -> n.getValue() )
				.filter( doc -> "ESB01487271".equals(doc))
				.isPresent()
			)
			.forEach( d -> {
				System.out.println(
					d.getCompany().orElse("<NO COMP>")
					+ " " + 
					d.getId().orElse("<NO ID>")
					+ " [Issuer: " +
					d.getData()
						.flatMap( i -> i.getIssuerTaxId() )
						.flatMap( n -> n.getValue() )
					.orElse("<NO NAME>")
					+ ", "
					+ d.getData()
						.flatMap( i -> i.getIssuerName() )
						.flatMap( n -> n.getValue() )
					.orElse("<NO NAME>")
					+ "]"
					+ " [" +
					d.getData()
						.flatMap( i -> i.getTotalAmount() )
						.flatMap( n -> n.getValue() )
					.orElse(null)
					+ "]"
				);
			});
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
		
	@Test
	void getLoginnToken() {
		OCRLoginTokenResponse response = OCRInvofox.getLoginToken();
		
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getLoginToken().isPresent());
		assertTrue(response.getLoginToken().get().getToken().isPresent());
	}
}
