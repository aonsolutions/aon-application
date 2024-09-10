package com.esferalia.aon.occam.impl.jooq.dao.invoice;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;


class InvoiceValidationTests extends AbstractOccamTest {

	@Test
	void testValidationSaveEmptyDomain() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}
	
	@Test
	void testValidationSaveEmptyIssueDate() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setIssueDate(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_DATE.getMessage(), e.getMessage());
	}

	@Test
	void testValidationSaveEmptyTaxDate() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setTaxDate(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_TAX_DATE.getMessage(), e.getMessage());
	}
	
		
	@Test
	void testValidationSaveEmptyInvoiceType() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setType(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_TYPE.getMessage(), e.getMessage());
	}
	
	
	@Test
	void testValidationSaveEmptyRegistry() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setRegistry(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_REGISTRY.getMessage(), e.getMessage());
	}

	@Test
	void testValidationSaveEmptyScope() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_SCOPE.getMessage(), e.getMessage());
	}

	@Test
	void testValidationSaveEmptyReferenceCode() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		invoice.setReferenceCode(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_REFERENCE_CODE.getMessage(), e.getMessage());
		
		invoice.setReferenceCode("");
		AonCoreException e1 = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_REFERENCE_CODE.getMessage(), e1.getMessage());
	}
	
	@Test
	void testValidationSaveEmptyTransaction() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setTransaction(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_EMPTY_TRANSACTION.getMessage(), e.getMessage());
	}
	
	@Test
	void testValidationDuplicatedSeriesNumber() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		Invoice newInvoice = InvoiceDAO.save(ctx, invoice);
		assertNotNull(newInvoice);
		assertFalse( AonStringUtils.isBlank( newInvoice.getReferenceCode()) );
		
		newInvoice.setId(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, newInvoice));
		assertEquals(AonError.INVOICE_DUPLICATED_SERIES_NUMBER.getMessage()
			+ "["+ (AonStringUtils.isBlank(newInvoice.getSeries())? "" : (newInvoice.getSeries() + "/") + newInvoice.getNumber()) +"]"
			, e.getMessage());
	}
	
}
