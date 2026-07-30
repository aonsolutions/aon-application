package com.esferalia.aon.occam.test.finance.invoice;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class InvoiceDataTest extends AbstractOccamTest {

	@Test
	public void test() {
		InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate(AonRandom.today());
		Invoice invoice = InvoiceFaker.getRandom(params);
		invoice = AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, invoice);
		
		Domain domain = new Domain().setName(DOMAIN_NAME).setId(DOMAIN_ID);
		User user = new User().setLogin(USER);

		// EMPTY INVOICE_DATA
		emptyInvoiceData(domain, user);
		
		// INVOICE_DATA WITHOUT DOMAIN
		invoiceDataWithoutDomain(domain, user, invoice);
		
		// INVOICE_DATA WITHOUT INVOICE
		invoiceDataWithoutInvoice(domain, user, invoice);
		
		// INVOICE_DATA WITHOUT NAME
		invoiceDataWithoutName(domain, user, invoice);
		
		// INVOICE_DATA WITHOUT VALUE
		invoiceDataWithoutValue(domain, user, invoice);
		
		// INVOICE_DATA WITHOUT START_DATE
		invoiceDataWithoutStartDate(domain, user, invoice);

		// INVOICE_DATA WITHOUT END_DATE
		invoiceDataWithoutEndDate(domain, user, invoice);
		
		// COMPLETE INVOICE_DATA
		completeInvoiceData(domain, user, invoice);
	}

	private void emptyInvoiceData(Domain domain, User user) {
		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.saveInvoiceData(domain, user, new InvoiceData()));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}
	
	private void invoiceDataWithoutDomain(Domain domain, User user, Invoice invoice) {
		InvoiceData invoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		invoiceData.setDomain(null);

		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.saveInvoiceData(domain, user, invoiceData));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());	
	}
	
	private void invoiceDataWithoutInvoice(Domain domain, User user, Invoice invoice) {
		InvoiceData invoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		invoiceData.setInvoice(null);

		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.saveInvoiceData(domain, user, invoiceData));
		assertEquals(AonError.EMPTY_DATA.format("invoice"), e.getMessage());
	}
	
	private void invoiceDataWithoutName(Domain domain, User user, Invoice invoice) {
		InvoiceData invoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		invoiceData.setName(null);

		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.saveInvoiceData(domain, user, invoiceData));
		assertEquals(AonError.EMPTY_DATA.format("name"), e.getMessage());
	}
	
	
	private void invoiceDataWithoutValue(Domain domain, User user, Invoice invoice) {
		InvoiceData invoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		invoiceData.setValue(null);

		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.saveInvoiceData(domain, user, invoiceData));
		assertEquals(AonError.EMPTY_DATA.format("value"), e.getMessage());
	}
	
	private void invoiceDataWithoutStartDate(Domain domain, User user, Invoice invoice) {
		InvoiceData invoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		invoiceData.setStartDate(null);

		InvoiceData savedInvoiceData = AON.saveInvoiceData(domain, user, invoiceData);
		assertNotNull(savedInvoiceData.getId());
		assertNotNull(savedInvoiceData.getStartDate());
	}
	
	private void invoiceDataWithoutEndDate(Domain domain, User user, Invoice invoice) {
		InvoiceData invoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		invoiceData.setEndDate(null);

		InvoiceData savedInvoiceData = AON.saveInvoiceData(domain, user, invoiceData);
		assertNotNull(savedInvoiceData.getId());
		assertNull(savedInvoiceData.getEndDate());
	}
	
	private void completeInvoiceData(Domain domain, User user, Invoice invoice) {
		InvoiceData completeInvoiceData = AonFaker.getInvoiceData(domain.getId(), invoice.getId());
		InvoiceData completedInvoiceData = AON.saveInvoiceData(domain, user, completeInvoiceData);
		assertNotNull(completedInvoiceData.getId());

		InvoiceData selectCompleteInvoiceData = AON.getInvoiceData(domain, user, f -> f.getIdProperty().eq(completedInvoiceData.getId()));
		Asserts.assertEqualsInvoiceData(completedInvoiceData, selectCompleteInvoiceData);
		
		InvoiceData updateInvoiceData = completeInvoiceData.setValue("UPDATED VALUE");
		InvoiceData updatedInvoiceData = AON.saveInvoiceData(domain, user, updateInvoiceData);
		Asserts.assertEqualsInvoiceData(updateInvoiceData, updatedInvoiceData);
		InvoiceData selectUpdatedInvoiceData = AON.getInvoiceData(domain, user, f -> f.getIdProperty().eq(completeInvoiceData.getId()));
		Asserts.assertEqualsInvoiceData(updatedInvoiceData, selectUpdatedInvoiceData);	
	}
	
	
}
