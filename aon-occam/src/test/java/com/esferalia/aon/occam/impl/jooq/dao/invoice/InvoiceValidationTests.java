package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.LinkedList;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice.InvoiceTextPrinter;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


class InvoiceValidationTests extends AbstractOccamTest {

	@Test
	void testValidationSaveNullDomain() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.DOMAIN);
	}
	
	@Test
	void testValidationSaveEmptyDomain() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setDomain(0);
		AonCoreException e1 = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e1.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.DOMAIN);
	}
	
	@Test
	void testValidationSaveEmptyIssueDate() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setIssueDate(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.ISSUE_DATE);
	}

	@Test
	void testValidationSaveEmptyTaxDate() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setTaxDate(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.TAX_DATE);
	}
	
		
	@Test
	void testValidationSaveEmptyInvoiceType() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setType(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.TYPE);
	}
	
	
	@Test
	void testValidationSaveEmptyRegistry() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setRegistry(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.REGISTRY);
	}

	@Test
	void testValidationSaveEmptyScope() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setScope(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.SCOPE);
	}

	@Test
	void testValidationSaveEmptyScopeId() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setScope(new Scope());
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.SCOPE);
		
	}

	@Test
	void testValidationSaveNullReferenceCode() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		invoice.setReferenceCode(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.REFERENCE_CODE);
	}
	
	@Test
	void testValidationSaveEmptyReferenceCode() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		invoice.setReferenceCode("");
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.REFERENCE_CODE);
	}
	
	@Test
	void testValidationSaveEmptyTransaction() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setTransaction(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C001, InvoiceErrorKey.TRANSACTION);
	}
	
	@Test
	void testValidationDuplicatedSeriesNumber() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		Invoice newInvoice = InvoiceDAO.save(ctx, invoice);
		assertNotNull(newInvoice);
		assertFalse( AonStringUtils.isBlank( newInvoice.getReferenceCode()) );
		
		assertDoesNotThrow( () -> InvoiceValidation.validate(ctx, newInvoice));
		
		newInvoice.setId(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, newInvoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C005, InvoiceErrorKey.DUPLICATED_SERIES_NUMBER);
	}
	
	@Test
	void testValidationDuplicatedSeriesNumber2() {
		Invoice invoice = InvoiceFaker.getSalesNational(ctx);
		invoice.setSeries(null);
		Invoice newInvoice = InvoiceDAO.save(ctx, invoice);
		assertNotNull(newInvoice);
		assertFalse( AonStringUtils.isBlank( newInvoice.getReferenceCode()) );
		
		assertDoesNotThrow( () -> InvoiceValidation.validate(ctx, newInvoice));		
		
		newInvoice.setId(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, newInvoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C005, InvoiceErrorKey.DUPLICATED_SERIES_NUMBER);
	}


	@Test
	void testValidationDuplicatedReferenceCode() {
		Invoice invoice = InvoiceFaker.getPurchaseNational(ctx);
		Invoice newInvoice = InvoiceDAO.save(ctx, invoice);
		assertNotNull(newInvoice);
		newInvoice.setId(null)
			.setSeries(null)
			.setNumber(0);
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, newInvoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C006, InvoiceErrorKey.DUPLICATED_REFERENCE_CODE );
	}
	
	@Test
	void testValidationDeadline() {
		Date deadline = ctx.getConfiguration().getOperationsDeadline();
		try {
			Invoice invoice = InvoiceFaker.getRandom(ctx);
			Date newDeadline =  AonDateUtils.addDays(invoice.getIssueDate(), 1 );
			ctx.getConfiguration().setOperationsDeadline( newDeadline );
			AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
			assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
			assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C007, InvoiceErrorKey.ISSUE_DATE );
		} finally {
			ctx.getConfiguration().setOperationsDeadline( deadline);
		}
		
	}
	
	@Test
	void testValidationCheckPast() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		Date issueDate = AonDateUtils.addYears( new Date(), -11 );  
		invoice.setIssueDate( issueDate );
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C008, InvoiceErrorKey.ISSUE_DATE );
	}
	
	@Test
	void testValidationCheckFuture() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		Date issueDate = AonDateUtils.addYears( new Date(), 2 );  
		invoice.setIssueDate( issueDate );
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C008, InvoiceErrorKey.ISSUE_DATE );
	}
	
	@Test
	void testValidationCheckNullFinances() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setFinances(null);
		assertDoesNotThrow(() -> InvoiceValidation.validate(ctx, invoice));
	}

	@Test
	void testValidationCheckEmptyFinances() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setFinances( new LinkedList<>() );
		assertDoesNotThrow(() -> InvoiceValidation.validate(ctx, invoice));
	}

	@Test
	void testValidationCheckFinanceTotal() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		AonCollectionUtils.stream(invoice.getFinances())
			.findFirst()
			.map(f -> f.setAmount(f.getAmount() + 1));
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validate(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C020, InvoiceErrorKey.FINANCE_TOTAL_AMOUNT );
	}

	@Test
	void testValidationCheckFinanceRemovedTotal() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.addFinance(new Finance()
			.setDueDate(invoice.getIssueDate())
			.setPayment(invoice.isNotSales()) 
			.setFinanceStatus(FinanceStatus.PENDING)	
			.setRemoved(true)
			.setAmount(5000));
		assertDoesNotThrow(() -> InvoiceValidation.validate(ctx, invoice));
	}

	@Test
	void testValidationRectifiedDeletion() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setRectificationType( RectificationType.RECTIFIED );
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validateDeletion(ctx, invoice));
		assertEquals(AonError.INVOICE_SAVE_DELETE_ERROR.getMessage(), e.getMessage());
		assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C050, InvoiceErrorKey.GENERIC );
	}

	@Test
	void testValidationNotRectifiedDeletion() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		invoice.setRectificationType( RectificationType.NONE );
		assertDoesNotThrow(() -> InvoiceValidation.validateDeletion(ctx, invoice));
	}

	@Test
	void testValidationAlcatraz() {
		Invoice invoice = InvoiceFaker.getRandom(ctx);
		Invoice newInvoice = assertDoesNotThrow(() -> InvoiceDAO.save(ctx, invoice));
		try {
			ctx.getDslContext()
				.insertInto(ALCATRAZ)
				.set(ALCATRAZ.DOMAIN, newInvoice.getDomain())
				.set(ALCATRAZ.INVOICE, newInvoice.getId())
				.execute();
			AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceValidation.validateDeletion(ctx, invoice));
			assertEquals(AonError.INVOICE_SAVE_DELETE_ERROR.getMessage(), e.getMessage());
			assertInvoiceErrorMessage(invoice, InvoiceErrorMessages.C056, InvoiceErrorKey.GENERIC );
		} finally {
			ctx.getDslContext()
				.deleteFrom(ALCATRAZ)
				.where(ALCATRAZ.INVOICE.eq(newInvoice.getId()))
				.execute();
		}
		
	}
	
	private void assertInvoiceErrorMessage(Invoice invoice, InvoiceErrorMessages msg, InvoiceErrorKey key) {
		Asserts.assertNotEmptyCollection("Empty Messages", invoice.getMessages());
		InvoiceTextPrinter.printMessages(System.out, invoice);
		MatcherAssert.assertThat(invoice.getMessages(), hasItem(
			allOf(
				hasProperty("code", equalTo(msg.toString()))
				,hasProperty("context", hasProperty("key",is( key )))
				)
			));
	}
	
	
}
