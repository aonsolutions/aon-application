package net.aonsolutions.invofox.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.util.AonArrayUtils;

import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.OCRResult;
import net.aonsolutions.invofox.aon.OCRInvoiceBuilder;
import net.aonsolutions.invofox.json.OCRInvoiceJSON;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRSeverity;

class InvofoxToAonTestCase {
	
	@Test
	void getValidDocument() {
		// String documentId = "648088d6c632f4000891fa82";
		// String documentId = "648991a57e00c10008d56923"; // BIP & DRIVE
		String companyId = "6480556355f159000abb18eb";
		String[] companyDocuments = {
//			 "ESB66941873" // TRANSLOGIA DEVELOPMENT SL] [33802.24]
//			,"ESB82435074" // TOLEDO Y ASOCIADOS ASESORIA Y GESTION SL] [223.85]
//			,"ESW0013547E" // BNP PARIBAS LEASE GROUP SA SUCURSAL EN ESPAÑA] [120.94]
//			,"ESA86969607" // BIP & DRIVE E D E SA] [2.11]
//			,"ESA82018474" // TELEFONICA DE ESPAÑA SA] [87.99]
//			,"ESB87539284" // VODAFONE SERVICIOS SL] [105.5]
//			,"ESB01016344" // TERMOFUEL SL] [556.65]
//			,"ESA08431090" // NATURGY IBERIA SA] [-428.46]
//			,"ESA01314319" // B K CONSULTING ABOGADOS & ASESORES SA] [314.2]
//			,
			 "ESA63422141" // VUELING AIRLINES SA] [163.98]
//			,"ESA82009812" // ORANGE ESPAGNE SA] [112.99]
//			,"ESF20033361" // EROSKI SOCIEDAD COOPERATIVA] [5.65]
//			,"ESW0185696B" // AMAZON WEB SERVICES EMEA SARL SUCURSAL EN ESPAÑA] [1609.64]
		};
		OCRDocumentsParams params = OCRDocumentsParams.get()
				.withCompany( companyId );
		OCRDocumentsResponse docsResponse = OCRInvofox.getDocuments(params);
		assertNotNull(docsResponse);
		assertTrue(docsResponse.getHttpCode().isPresent());
		assertEquals( 200, docsResponse.getHttpCode().get());
		assertTrue(docsResponse.getDocuments().isPresent());
		List<OCRDocument> documents = docsResponse.getDocuments().get();
		
		documents.stream()
			.filter( d -> d.getData().isPresent() )
			.filter( d -> d.getId().isPresent() )
			.filter( d -> AonArrayUtils.constains(companyDocuments, d.getData().get().getIssuerDocument())
					|| AonArrayUtils.constains(companyDocuments, d.getData().get().getRecipientDocument()) )
			.forEach( optDocument -> {
				OCRSeverity state = optDocument.getPublicState().get();
				System.out.println( "************************************** " );
				System.out.println( "**************** STATE *************** " );
				System.out.println( "************************************** " );
				System.out.println( state );
				System.out.println( );
				
				String documentId = optDocument.getId().get();
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
		});

	}

	private static Occam getOccam() {
		return new Occam()
			.setDomainName("occamtest.aonsolutions.test")
			.setDomain(47)
			.setUser("admin");
	}
		
}



