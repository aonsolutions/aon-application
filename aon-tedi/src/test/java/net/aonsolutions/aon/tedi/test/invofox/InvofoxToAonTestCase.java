package net.aonsolutions.aon.tedi.test.invofox;


import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilder;
import net.aonsolutions.aon.tedi.invofox.OCRResult;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
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
			 "ESB66941873" // TRANSLOGIA DEVELOPMENT SL] [33802.24]
			,"ES16301423N" // JULEN SOPELANA GORDO
			,"ESB82435074" // TOLEDO Y ASOCIADOS ASESORIA Y GESTION SL] [223.85]
			,"ESW0013547E" // BNP PARIBAS LEASE GROUP SA SUCURSAL EN ESPAÑA] [120.94]
			,"ESA86969607" // BIP & DRIVE E D E SA] [2.11]
			,"ESA82018474" // TELEFONICA DE ESPAÑA SA] [87.99]
			,"ESB87539284" // VODAFONE SERVICIOS SL] [105.5]
			,"ESB01016344" // TERMOFUEL SL] [556.65]
			,"ESA08431090" // NATURGY IBERIA SA] [-428.46]
			,"ESA01314319" // B K CONSULTING ABOGADOS & ASESORES SA] [314.2]
			,"ESA63422141" // VUELING AIRLINES SA] [163.98]
			,"ESA82009812" // ORANGE ESPAGNE SA] [112.99]
			,"ESF20033361" // EROSKI SOCIEDAD COOPERATIVA] [5.65]
			,"ESW0185696B" // AMAZON WEB SERVICES EMEA SARL SUCURSAL EN ESPAÑA] [1609.64]
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
			.map( d ->{
				System.out.println( d.getData().get().getIssuerDocument() );
				return d;
			})
			.filter( d -> AonArrayUtils.constains(companyDocuments, d.getData().get().getIssuerDocument())
					|| AonArrayUtils.constains(companyDocuments, d.getData().get().getRecipientDocument()) )
			.forEach( optDocument -> {
				OCRSeverity state = optDocument.getPublicState().get();
				System.out.println( "**************** STATE *************** " );
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
				
				// System.out.println( "**************************************** " );
				// System.out.println( "**************** SOURCE **************** " );
				// System.out.println( "**************************************** " );
				// System.out.println();
				// System.out.println( OCRInvoiceJSON.to(result.getOCRInvoice()).toString(1) );
				// System.out.println();
				
				// System.out.println( "**************************************** " );
				// System.out.println( "**************** INVOICE *************** " );
				// System.out.println( "**************************************** " );
				// System.out.println();
				// System.out.println( InvoiceJSON.toJSON(result.getInvoice()).toString(1) );
				
				Invoice invoice = result.getInvoice();
				System.out.println( "\t************ ATTEMPT TO SAVE INVOICE" );
				try {
					
					Invoice duplicated = AON.getInvoice( getOccam(), p -> p.getReferenceCodeProperty().eq( invoice.getReferenceCode()));
					if (duplicated != null && duplicated.getId() != null) {
						System.out.println( "\t\t************ INVOICE DELETED ************** " );
						AON.deleteInvoice(getOccam(), duplicated.getId());
					}
					System.out.println( "\t\t************ INVOICE SAVED ************** " );
					
					Invoice saved = AON.insertInvoice( getOccam() , invoice );
					
					double headerTax = ctx.getDslContext().select( INVOICE.VAT_QUOTA )
						.from(INVOICE)
						.where(INVOICE.ID.eq(saved.getId()))
						.fetch()
						.stream()
						.mapToDouble( r -> r.getValue(INVOICE.VAT_QUOTA) )
						.findFirst()
						.orElse(-1.0);
					
					double invoiceTax = ctx.getDslContext().select( INVOICE_TAX.QUOTA )
						.from(INVOICE)
						.innerJoin( INVOICE_DETAIL).on( INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
						.innerJoin( INVOICE_TAX).on( INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
						.where(INVOICE.ID.eq(saved.getId()))
						.and( INVOICE_TAX.TAX_TYPE.eq( TaxType.VAT.value() ) )
						.fetch()
						.stream()
						.mapToDouble( r -> r.getValue(INVOICE_TAX.QUOTA) )
						.sum();
					headerTax = AonMathUtils.round(headerTax);
					invoiceTax = AonMathUtils.round(invoiceTax);
					assertEquals( headerTax, invoiceTax);
					
				} catch( Exception e ) {
					e.printStackTrace();
					try {
						Rawdoc rawdoc = new Rawdoc()
							.setDomain( invoice.getDomain() )
							.setNature( RawdocNature.INVOICE )
							.setType( invoice.isSales()?RawdocType.OUTPUT:RawdocType.INPUT)
							.setStatus( RawdocStatus.INBOX )
							.setJson( InvoiceJSON.toJSON(result.getInvoice()).toString() );
						AON.rawdocSave( getOccam() , rawdoc );
						System.out.println( "\t\t************ RAWDOC SAVED ************** " );

					} catch( Exception e1 ) {
						e1.printStackTrace();
						System.out.println( "\t\t************ RAWDOC NOT SAVED ************** " );
					}
				}
				
		});

	}

	private static Occam getOccam() {
		return new Occam()
			.setDomainName("occamtest.aonsolutions.test")
			.setDomain(47)
			.setUser("admin");
	}
		
}



