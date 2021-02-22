package com.esferalia.aon.occam.test.faker;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.github.javafaker.Faker;

public class InvoiceFaker {
	private static Faker faker = new Faker(new Locale("es"));
	
	public static Invoice get( AONContext ctx, AonConfiguration config ) {
		Invoice invoice = new Invoice();
		invoice.setDomain(ctx.getDomainId());
		Date issueDate = faker.date().past(10, TimeUnit.DAYS);
		invoice.setIssueDate( issueDate );
		invoice.setTaxDate( issueDate );
		invoice.setType( AonRandom.randomEnum(InvoiceType.class) );
		invoice.getType().visit(invoice, new IInvoiceTypeVisitor() {
			
			private void fillRegistryData(Invoice invoice, Registry reg) {
				invoice.setRegistry(reg.getId());
				invoice.setRegistryDocumentType(reg.getDocumentType());
				invoice.setRegistryDocumentCountry(reg.getDocumentCountry());
				invoice.setRegistryDocument(reg.getDocument());
				invoice.setRegistryName(reg.getName());
			}
			
			@Override
			public void visitSales(Invoice invoice) {
				Customer customer = AonRandom.getCustomer( ctx );
				fillRegistryData(invoice, customer);
				invoice.setScope(new Scope().setId( customer.getScope() ));
				invoice.setTransaction( customer.getTransaction() );
				invoice.setService( faker.random().nextInt(0, 100) > 90);
				invoice.setVatAccrualPayment(invoice.isNational() && config.getCompany().isVatAccrualPayment());
				invoice.setSeries(config.getDefaultInvoiceSeries());
				invoice.setNumber( InvoiceDAO.getNextNumber(ctx, new Byte[]{invoice.getType().value()}, invoice.getSeries()));
				invoice.setSurcharge(customer.isSurcharge());
				invoice.setWithholding(customer.isWithholding() && config.getCompany().isWithholding());
				invoice.setWithholdingFarmer(false);
			}
			
			@Override
			public void visitPurchase(Invoice invoice) {
				Supplier supplier = AonRandom.getRandomSupplier( ctx );
				fillRegistryData(invoice, supplier);
				invoice.setScope(new Scope().setId( supplier.getScope() ));
				Short tr = supplier.getTransaction();
				invoice.setTransaction( InvoiceTransactionType.safeValueOf( tr==null?(byte) 0:tr.byteValue() ));
				invoice.setService( faker.random().nextInt(0, 100) > 90);
				invoice.setVatAccrualPayment(invoice.isNational() && supplier.getVatAccrualPayment() == 1);
				invoice.setSurcharge(config.getCompany().isSurcharge());
				invoice.setWithholding(supplier.getWithholding() == 1);
				invoice.setWithholdingFarmer(supplier.getWithholdingFarmer() == 1);
			}
			
			@Override
			public void visitExpenses(Invoice invoice) {
				Creditor creditor = AonRandom.getRandomCreditor( ctx );
				invoice.setScope(new Scope().setId( creditor.getScope() ));
				fillRegistryData(invoice, creditor);
				invoice.setTransaction( creditor.getTransaction() );
				invoice.setService( true );
				invoice.setVatAccrualPayment(invoice.isNational() && creditor.isVatAccrualPayment());
				invoice.setSurcharge(false);
				invoice.setWithholding(creditor.isWithholding());
				invoice.setWithholdingFarmer(false);
			}
			@Override
			public void visitUndeductible(Invoice invoice) {
				visitExpenses(invoice);
				invoice.setSurcharge(false);
				invoice.setWithholding(false);
				invoice.setWithholdingFarmer(false);
				invoice.setVatAccrualPayment(false);
				invoice.setService( true );
			}
		});
		EnterpriseActivity activity = AonRandom.getRandomActivity(ctx);
		invoice.setActivity(activity==null?null:activity.getId());
		
		
		return invoice;
	}
}

