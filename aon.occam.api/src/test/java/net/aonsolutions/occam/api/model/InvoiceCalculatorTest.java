package net.aonsolutions.occam.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.WithholdingType;

class InvoiceCalculatorTest {
	
	@Test
	void testInvoiceBuilder() {
		Invoice invoice = new InvoiceBuilder()
			.invoice( new Invoice() )
				.setIssueDate( new Date() )
				.setType(InvoiceType.PURCHASE)
				.setTransaction(InvoiceTransactionType.NATIONAL)
				.setSurcharge(true)
				.setRegistryDocumentType( DocumentType.NIF )
				.setRegistryDocumentCountry( Country.ES )
				.setRegistryDocument("B01555999")
				.setRegistryName("La tienda de la esquina, S.L.")
			.addDetail()
				.setDescription("SUMINISTROS")
				.setQuantity(1.0)
				.setPrice(157.87)
				.setVatPercent(21.0)
				.setSurchargePercent(5.2)
			.addDetail()
				.setDescription("GOLOSINAS")
				.setQuantity(7.0)
				.setPrice(1.5)
				.setVatPercent(10.0)
				.setSurchargePercent(1.4)
			.addDetail()
				.setDescription("CERVEZAS")
				.setQuantity(2.0)
				.setPrice(2.25)
				.setVatPercent(4.0)
				.setSurchargePercent(0.5)
			.addDetail()
				.setDescription("SUPLIDOS")
				.setQuantity(1.0)
				.setPrice(500)
				.setPrepayment( true )
			.setWithholdingType( WithholdingType.PROFESSIONAL)
			.setWithholdingPercent( 15.0 )
			.build()
		;
		assertNotNull(invoice); 
//		InvoiceTextPrinter.print(invoice);
	}
	
	@Test
	void testBasicReverseCalculateInvoice() {
		Invoice invoice = new InvoiceBuilder()
			.invoice( new Invoice() )
			.setType(InvoiceType.SALES)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.addDetail()
				.setVatPercent(21.0)
			.build()
		;
		InvoiceCalculator.reverseCalculate(invoice, 121);
		assertEquals(121, invoice.getHeader().getTotal());
		InvoiceDetail detail = invoice.detailStream().findFirst().orElse(null);
		assertNotNull(detail);
		
		assertEquals(1.0, detail.getQuantity());
		assertEquals(100.0, detail.getPrice());		
		assertEquals(100.0, detail.getTaxableBase());
		InvoiceTax vat = detail.getVatTax().orElse( null );
		assertNotNull(vat);
		assertEquals(21.0, vat.getPercentage());
		assertEquals(100.0, vat.getBase());
		assertEquals(21.0, vat.getQuota());
		assertFalse(vat.isQuotaEdited());
		
		assertEquals(0.0, vat.getSurcharge());
		assertEquals(0.0, vat.getSurchargeQuota());
		assertFalse(vat.isSurchargeQuotaEdited());
		
		assertEquals(100.0, vat.getDeductiblePercent());
		assertEquals(21.0, vat.getDeductibleQuota());
		assertFalse(vat.isDeductibleQuotaEdited());
		
	}

	@Test
	void testReverseCalculateWithWithholdingInvoice() {
		Invoice invoice = new InvoiceBuilder()
			.invoice( new Invoice() )
			.setType(InvoiceType.SALES)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setWithholdingPercent(15.0)
			.setWithholdingType( WithholdingType.PROFESSIONAL)
			.addDetail()
				.setVatPercent(21.0)
			.build()
		;
		InvoiceCalculator.reverseCalculate(invoice, 121);
		assertEquals(121, invoice.getHeader().getTotal());
		InvoiceDetail detail = invoice.detailStream().findFirst().orElse(null);
		assertNotNull(detail);
		
		assertEquals(1.0, detail.getQuantity());
		assertEquals(114.15, detail.getPrice());		
		assertEquals(114.15, detail.getTaxableBase());
		InvoiceTax vat = detail.getVatTax().orElse( null );
		assertNotNull(vat);
		assertEquals(21.0, vat.getPercentage());
		assertEquals(114.15, vat.getBase());
		assertEquals(23.97, vat.getQuota());
		assertFalse(vat.isQuotaEdited());
		
		assertEquals(0.0, vat.getSurcharge());
		assertEquals(0.0, vat.getSurchargeQuota());
		assertFalse(vat.isSurchargeQuotaEdited());
		
		assertEquals(100.0, vat.getDeductiblePercent());
		assertEquals(23.97, vat.getDeductibleQuota());
		assertFalse(vat.isDeductibleQuotaEdited());
		
		InvoiceWithholding iw = invoice.getWithholding().orElse(null);
		assertNotNull(iw);
		assertEquals(15.0, iw.getPercentage());
		assertEquals(17.12, iw.getQuota());
		assertSame(WithholdingType.PROFESSIONAL, iw.getWithholdingType());
		assertEquals(0.0, vat.getDirectTaxPercent());
		assertEquals(17.12, iw.getDeductibleQuota());
	}
	
	@Test
	void testCalculateWithWithholdingFarmerInvoice() {
		Invoice invoice = new InvoiceBuilder()
			.invoice( new Invoice() )
			.setType(InvoiceType.SALES)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setWithholdingPercent(2.0)
			.setWithholdingType( WithholdingType.FARMER)
			.addDetail()
				.setVatPercent(12.0)
				.setPrice(1000)
				.setQuantity(1)
			.build()
		;
		InvoiceCalculator.calculate(invoice);
		
		InvoiceDetail detail = invoice.detailStream().findFirst().orElse(null);
		assertNotNull(detail);
		
		assertEquals(1.0, detail.getQuantity());
		assertEquals(1000.0, detail.getPrice());		
		assertEquals(1000.0, detail.getTaxableBase());
		InvoiceTax vat = detail.getVatTax().orElse( null );
		assertNotNull(vat);
		assertEquals(12.0, vat.getPercentage());
		assertEquals(1000.0, vat.getBase());
		assertEquals(120.0, vat.getQuota());
		assertFalse(vat.isQuotaEdited());
		
		assertEquals(0.0, vat.getSurcharge());
		assertEquals(0.0, vat.getSurchargeQuota());
		assertFalse(vat.isSurchargeQuotaEdited());
		
		assertEquals(100.0, vat.getDeductiblePercent());
		assertEquals(120.0, vat.getDeductibleQuota());
		assertFalse(vat.isDeductibleQuotaEdited());
		
		InvoiceWithholding iw = invoice.getWithholding().orElse(null);
		assertNotNull(iw);
		assertEquals(2.0, iw.getPercentage());
		assertEquals(22.40, iw.getQuota());
		assertSame(WithholdingType.FARMER, iw.getWithholdingType());
		assertEquals(0.0, vat.getDirectTaxPercent());
		assertEquals(22.40, iw.getDeductibleQuota());
		
		assertEquals(1097.6, invoice.getHeader().getTotal());
	}
	
	@Test
	void testReverseCalculateWithWithholdingFarmerInvoice() {
		Invoice invoice = new InvoiceBuilder()
			.invoice( new Invoice() )
			.setType(InvoiceType.SALES)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setWithholdingPercent(2.0)
			.setWithholdingType( WithholdingType.FARMER)
			.addDetail()
				.setVatPercent(12.0)
			.build()
		;
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceCalculator.reverseCalculate(invoice, 1097.6));
		assertEquals(AonError.INVOICE_CALC_REV_FARMER.getMessage(), e.getMessage());
	}
}






