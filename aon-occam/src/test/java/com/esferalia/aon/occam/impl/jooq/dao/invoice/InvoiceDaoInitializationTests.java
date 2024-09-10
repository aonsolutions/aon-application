package com.esferalia.aon.occam.impl.jooq.dao.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceDaoInitializationTests extends AbstractOccamTest {

	@Test
	void testEmptyDomain() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDAO.initialize(ctx, null , InvoiceType.SALES, 1) );
		assertEquals(AonError.INVOICE_EMPTY_DOMAIN.getMessage(),e.getMessage());
	}

	@Test
	void testEmptyType() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDAO.initialize(ctx, DOMAIN_ID , null, 1) );
		assertEquals(AonError.INVOICE_EMPTY_TYPE.getMessage(),e.getMessage());
	}

	@Test
	void testEmptyRegistry() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDAO.initialize(ctx, DOMAIN_ID , InvoiceType.SALES, null) );
		assertEquals(AonError.INVOICE_EMPTY_REGISTRY.getMessage(),e.getMessage());
	}

	@Test
	void testEmptyWorkplace() {
		// Force default workplace to be null
		AonConfiguration config = ctx.getConfiguration();
		config.setWorkplaces(null);
		
		AonCoreException e = assertThrows(AonCoreException.class, () -> InvoiceDAO.initialize(ctx, DOMAIN_ID , InvoiceType.SALES, 1) );
		assertEquals(AonError.EMPTY_WORKPLACE.getMessage(),e.getMessage());
	}

	@RepeatedTest( 10 )
	void testRandomInitialize() {
		InvoiceType type = AonRandom.getRandomInvoiceType();
		Integer registry = AonRandom.getRandomInvoiceRegistry( ctx, type);
		EnterpriseActivity activity = AonRandom.getRandomActivity( ctx, getConfiguration());
		Date issueDate = AonRandom.gt(10)?getTestDate():null;
		Integer activityId = Optional.ofNullable(activity).map(a -> a.getId()).orElse(null);
		Invoice invoice = AonRandom.gt(5)
			?InvoiceDAO.initialize(ctx, getOccam().getDomain(), type, registry, issueDate, activityId)
			:InvoiceDAO.initialize(ctx, getOccam().getDomain(), type, registry);
		assertNotNull(invoice);
		assertEquals( type, invoice.getType());
		assertEquals( registry, invoice.getRegistry());
		type.visit(invoice, new IInvoiceTypeVisitor<Void>() {
			
			private void commonAsserts(Registry reg) {
				Asserts.assertsRegistryData( reg, invoice);
			}
			
			@Override
			public Void visitPurchase(Invoice invoice) {
				Supplier reg = SupplierDAO.get(ctx, registry);
				commonAsserts(reg);
				Asserts.assertEqualsScope( reg.getScope(), invoice.getScope());
				assertEquals( reg.isWithholding(), invoice.isWithholding());			
				assertEquals( reg.isWithholdingFarmer(), invoice.isWithholdingFarmer());
				assertFalse( invoice.isService() );
				return null;
			}

			@Override
			public Void visitSales(Invoice invoice) {
				Customer reg = CustomerDAO.get(ctx, registry);
				commonAsserts(reg);
				Asserts.assertEqualsScope( reg.getScope(), invoice.getScope());
				assertFalse( invoice.isService() );
				assertFalse( invoice.isWithholdingFarmer() );
				return null;
			}
			
			public Creditor assertCreditor(Invoice invoice) {
				Creditor reg = CreditorDAO.get(ctx, registry);
				commonAsserts(reg);
				Asserts.assertEqualsScope( reg.getScope(), invoice.getScope());
				return reg;
			}

			@Override
			public Void visitExpenses(Invoice invoice) {
				Creditor reg = assertCreditor(invoice);
				assertEquals( reg.isWithholding(), invoice.isWithholding());			
				assertFalse( invoice.isWithholdingFarmer() );
				assertTrue( invoice.isService() );
				return null;
			}

			@Override
			public Void visitUndeductible(Invoice invoice) {
				assertCreditor(invoice);
				assertTrue( invoice.isService() );
				assertFalse( invoice.isWithholding() );
				assertFalse( invoice.isWithholdingFarmer() );
				assertFalse( invoice.isVatAccrualPayment() );
				return null;
			}
		});
	}

}
