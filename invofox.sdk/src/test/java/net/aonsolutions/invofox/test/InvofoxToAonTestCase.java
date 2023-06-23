package net.aonsolutions.invofox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Occam;

import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.OCRInvoiceBuilder;
import net.aonsolutions.invofox.OCRResult;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;

class InvofoxToAonTestCase {
	
	@Test
	void getValidDocument() {
		String documentId = "648088d6c632f4000891fa82";
		OCRDocumentResponse response = OCRInvofox.getDocument(documentId);
		assertNotNull(response);
		assertTrue(response.getHttpCode().isPresent());
		assertEquals( 200, response.getHttpCode().get());
		assertTrue(response.getDocument().isPresent());
		OCRDocument document = response.getDocument().get();
		AONContext ctx = AONContext.getAONContext(getOccam());
		OCRResult result = OCRInvoiceBuilder.toInvoice(ctx, document );
		assertNotNull(result);
		assertNotNull(result.getInvoice());
		assertNotNull(result.getInvoice().getIssueDate());
		assertNotNull(result.getInvoice().getType());
	}

	private static Occam getOccam() {
		return new Occam()
			.setDomainName("occamtest.aonsolutions.test")
			.setDomain(1)
			.setUser("admin");
	}
		
}