package com.esferalia.aon.occam.test.finance.invoice;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice.InvoiceTextPrinter;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;


public class InvoiceDAOTest extends AbstractOccamTest {
	
	@Test
	@RepeatedTest( 10 )
	public void testRandomInitialize() {
		InvoiceType type = AonRandom.getRandomInvoiceType();
		Integer registry = type.visit(null, new IInvoiceTypeVisitor<Integer>() {
			@Override
			public Integer visitPurchase(Invoice invoice) {
				return SupplierDAO.getRandom(ctx, p -> p.getIdProperty().ge(0) ).getId();
			}

			@Override
			public Integer visitSales(Invoice invoice) {
				return CustomerDAO.getRandom(ctx, p -> p.getIdProperty().ge(0) ).getId();
			}

			@Override
			public Integer visitExpenses(Invoice invoice) {
				return CreditorDAO.getRandom(ctx, p -> p.getIdProperty().ge(0) ).getId();
			}

			@Override
			public Integer visitUndeductible(Invoice invoice) {
				return visitExpenses(invoice);
			}
		});
		Invoice invoice = InvoiceDAO.initialize(ctx, getOccam().getDomain(), getConfiguration(), type, registry, getTestDate());
		assertNotNull(invoice);
		assertEquals( type, invoice.getType());
		if ( invoice.isNotSales() ) {
			type.visit(invoice, new IInvoiceTypeVisitor<Void>() {
				@Override
				public Void visitPurchase(Invoice invoice) {
					Supplier reg = SupplierDAO.get(ctx, registry);
					assertEquals( reg.getId(), invoice.getRegistry() );
					assertEquals( reg.getDocumentType(), invoice.getRegistryDocumentType());
					assertEquals( reg.getDocumentCountry(), invoice.getRegistryDocumentCountry());
					assertEquals( reg.getDocument(), invoice.getRegistryDocument());
					assertEquals( reg.getName(), invoice.getRegistryName());
					Asserts.assertEqualsScope( reg.getScope(), invoice.getScope());
					assertEquals( reg.isWithholding(), invoice.isWithholding());			
					assertEquals( reg.isWithholdingFarmer(), invoice.isWithholdingFarmer());
					assertFalse( invoice.isService() );
					return null;
				}

				@Override
				public Void visitSales(Invoice invoice) {
					Customer reg = CustomerDAO.get(ctx, registry);
					assertEquals( reg.getId(), invoice.getRegistry() );
					assertEquals( reg.getDocumentType(), invoice.getRegistryDocumentType());
					assertEquals( reg.getDocumentCountry(), invoice.getRegistryDocumentCountry());
					assertEquals( reg.getDocument(), invoice.getRegistryDocument());
					assertEquals( reg.getName(), invoice.getRegistryName());
					Asserts.assertEqualsScope( reg.getScope(), invoice.getScope());
					assertFalse( invoice.isService() );
					assertFalse( invoice.isWithholdingFarmer() );
					return null;
				}
				
				public Creditor assertCreditor(Invoice invoice) {
					Creditor reg = CreditorDAO.get(ctx, registry);
					assertEquals( reg.getId(), invoice.getRegistry() );
					assertEquals( reg.getDocumentType(), invoice.getRegistryDocumentType());
					assertEquals( reg.getDocumentCountry(), invoice.getRegistryDocumentCountry());
					assertEquals( reg.getDocument(), invoice.getRegistryDocument());
					assertEquals( reg.getName(), invoice.getRegistryName());
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
		
		InvoiceTextPrinter.print( invoice );
	}

	// @Test
	public void testFullPreviousMethod() {
		Date dateFrom = AonDateUtils.getYearFirstDay(2020);
		Date dateTo = AonDateUtils.getYearFirstDay(2023);
		Date date = AonRandom.getRangeDate(dateFrom, dateTo);
		MutableInt x = new MutableInt(0);
		InvoiceDAO.getInvoiceStream( ctx, p -> p.getIdProperty().ge(0)
				.and( p.getDomainProperty().eq(getOccam().getDomain()))
				.and( p.getRectificationTypeProperty().eq(RectificationType.NORMAL_RECTIFIER.value()))
				.and( p.getEndIssueDateProperty().ge(date)))
			.limit( 1 )
			.forEach( i -> {
				x.add(1);
				System.out.println(x.getValue() + " --- " + i.getId());
				if (1627205 == i.getId()) {
					System.out.println( "AQUI");
				}
				Invoice oldInvoice = InvoiceDAO.getFullInvoice(ctx, i.getId() );
				System.out.println();
				System.out.println( " --------------------------------------[OLD]");
				System.out.println();
				InvoiceTextPrinter.print( oldInvoice );
//					System.out.println( " --------------------------------------[JSON]");
//					System.out.println( InvoiceJSON.toJSON(oldInvoice).toString(1) );
				
				System.out.println( " --------------------------------------[NEW]");
				System.out.println();
				Invoice newInvoice = InvoiceDAO.getFull(ctx, i.getId() ).orElse(null);
				InvoiceTextPrinter.print( newInvoice );
//					System.out.println( " --------------------------------------[JSON]");
//					System.out.println( InvoiceJSON.toJSON(oldInvoice).toString(1) );
				
				System.out.println();
				Asserts.assertEqualsFullInvoice( oldInvoice, newInvoice );
			});
	}

}
