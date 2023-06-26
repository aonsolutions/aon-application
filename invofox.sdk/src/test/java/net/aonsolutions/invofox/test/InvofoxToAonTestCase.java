package net.aonsolutions.invofox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.OCRResult;
import net.aonsolutions.invofox.aon.OCRInvoiceBuilder;
import net.aonsolutions.invofox.json.OCRInvoiceJSON;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;

class InvofoxToAonTestCase {
	
	@Test
	void getValidDocument() {
		// String documentId = "648088d6c632f4000891fa82";
		// String documentId = "648991a57e00c10008d56923"; // BIP & DRIVE
		String companyId = "6480556355f159000abb18eb";
		String companyDocument = "ESA86969607";
		OCRDocumentsParams params = OCRDocumentsParams.get()
				.withCompany( companyId );
				
		OCRDocumentsResponse docsResponse = OCRInvofox.getDocuments(params);
		assertNotNull(docsResponse);
		assertTrue(docsResponse.getHttpCode().isPresent());
		assertEquals( 200, docsResponse.getHttpCode().get());
		assertTrue(docsResponse.getDocuments().isPresent());
		List<OCRDocument> documents = docsResponse.getDocuments().get();
		Optional<OCRDocument> optDocument =  documents.stream()
			.filter( d -> d.getData().isPresent() )
			.filter( d -> AonStringUtils.equals(companyDocument,d.getData().get().getIssuerDocument() )
					|| AonStringUtils.equals(companyDocument,d.getData().get().getRecipientDocument() ) )
			.findFirst()
		;
		assertTrue(optDocument.isPresent());
		assertTrue(optDocument.get().getId().isPresent());
		String documentId = optDocument.get().getId().get();
		OCRDocumentResponse docResponse = OCRInvofox.getDocument(documentId);
		assertNotNull(docResponse);
		assertTrue(docResponse.getHttpCode().isPresent());
		assertEquals( 200, docResponse.getHttpCode().get());
		assertTrue(docResponse.getDocument().isPresent());
		
		AONContext ctx = AONContext.getAONContext(getOccam());
		OCRResult result = OCRInvoiceBuilder.toInvoice(ctx, docResponse.getDocument().get() );
		assertNotNull(result);
		assertNotNull(result.getInvoice());
		assertNotNull(result.getInvoice().getIssueDate());
		assertNotNull(result.getInvoice().getType());
		
		System.out.println( "**************************************** " );
		System.out.println( "**************** SOURCE **************** " );
		System.out.println( "**************************************** " );
		System.out.println();
		System.out.println( OCRInvoiceJSON.to(result.getOCRInvoice()).toString(1) );
		System.out.println();
		
		System.out.println( "**************************************** " );
		System.out.println( "**************** INVOICE *************** " );
		System.out.println( "**************************************** " );
		System.out.println();
		System.out.println( InvoiceJSON.toJSON(result.getInvoice()).toString(1) );
		
	}

	private static Occam getOccam() {
		return new Occam()
			.setDomainName("occamtest.aonsolutions.test")
			.setDomain(47)
			.setUser("admin");
	}
		
}