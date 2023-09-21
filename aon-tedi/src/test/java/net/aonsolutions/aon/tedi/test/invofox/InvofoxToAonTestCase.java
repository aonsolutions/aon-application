package net.aonsolutions.aon.tedi.test.invofox;


import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonArrayUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.aon.tedi.invofox.OCRToAon;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;

public class InvofoxToAonTestCase {
	
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
			.map( optDocument -> OCRInvofox.getDocument(optDocument.getId().get()) )
			.map( docResponse -> {
				assertNotNull(docResponse);
				assertTrue(docResponse.getHttpCode().isPresent());
				assertEquals( 200, docResponse.getHttpCode().get());
				assertTrue(docResponse.getDocument().isPresent());
				return OCRToAon.toAON(getOccam(), docResponse);
			})
			.filter( pair -> pair.getLeft() != null)
			.map( pair -> pair.getLeft())
			.forEach( invoice -> {
				AONContext ctx = AONContext.getAONContext(getOccam());
					
				double headerTax = ctx.getDslContext().select( INVOICE.VAT_QUOTA )
					.from(INVOICE)
					.where(INVOICE.ID.eq(invoice.getId()))
					.fetch()
					.stream()
					.mapToDouble( r -> r.getValue(INVOICE.VAT_QUOTA) )
					.findFirst()
					.orElse(-1.0);
					
				double invoiceTax = ctx.getDslContext().select( INVOICE_TAX.QUOTA )
					.from(INVOICE)
					.innerJoin( INVOICE_DETAIL).on( INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
					.innerJoin( INVOICE_TAX).on( INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
					.where(INVOICE.ID.eq(invoice.getId()))
					.and( INVOICE_TAX.TAX_TYPE.eq( TaxType.VAT.value() ) )
					.fetch()
					.stream()
					.mapToDouble( r -> r.getValue(INVOICE_TAX.QUOTA) )
					.sum();
				headerTax = AonMathUtils.round(headerTax);
				invoiceTax = AonMathUtils.round(invoiceTax);
				assertEquals( headerTax, invoiceTax);
		});

	}

	private static Occam getOccam() {
		return new Occam()
			.setDomainName("occamtest.aonsolutions.test")
			.setDomain(1)
			.setUser("admin");
	}
		
}



