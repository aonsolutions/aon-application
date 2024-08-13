
package com.esferalia.aon.occam.test.finance.invoice;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.InvoiceRecorder;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice.InvoiceTextPrinter;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.accounting.entry.AccountEntryPrinter;
import com.esferalia.aon.occam.test.faker.AonRandom;


public class AccountingInvoiceDAOTest extends AbstractOccamTest {

	@Test
//	@Repeat( 10 )
	public void testNew() {
		AonConfiguration config = ConfigurationDAO.getAccountingConfiguration(ctx);
		Integer mainActivityId = Optional.ofNullable( config.getMainActivity() ).map(a -> a.getId()).orElse(null);
		Date issueDate = AonRandom.getPastDate(-1);
		AccountingRegistry ar = AonRandom.getAccountingRegistry(ctx);
		if (ar != null) {
			InvoiceType type = ar.getType().getInvoiceType();
			if (type == InvoiceType.EXPENSES && AonRandom.gt(80)) {
				type = InvoiceType.UNDEDUCTIBLE;
			}
			AccountingInvoice ai = AccountingInvoiceDAO.initializeInvoice(ctx, config, type, ar, mainActivityId, issueDate);
			// ai.setAccountEntry(getEntryBase(ctx, config,ai));
			double total = AonRandom.getDouble(-1, -1000, 50000, 2);
			InvoiceCalculator.reverseCalculate(ai.getInvoice(), total );
			Invoice invoice = ai.getInvoice();
			ai.setAccountEntry(InvoiceRecorder.getInvoiceEntry(ctx, invoice ));
			
			Account expAccount = invoice.getType().visit(invoice, new IInvoiceTypeVisitor<Account>() {

				@Override public Account visitPurchase(Invoice invoice) {
					return AonRandom.getAccount(ctx,-1, p -> p.getEntryEnabledProperty().eq((byte) 1 ).and(p.getCodeProperty().like("600%")));
				}

				@Override
				public Account visitSales(Invoice invoice) {
					return AonRandom.getAccount(ctx,-1, p -> p.getEntryEnabledProperty().eq((byte) 1 ).and(p.getCodeProperty().like("700%")));
				}

				@Override
				public Account visitExpenses(Invoice invoice) {
					return AonRandom.getAccount(ctx,-1, p -> p.getEntryEnabledProperty().eq((byte) 1 ).and(p.getCodeProperty().like("62%")));
				}

				@Override
				public Account visitUndeductible(Invoice invoice) {
					return visitExpenses(invoice);
				}
				
			});
			
			ai.getInvoice()
				.getFirstDetail()
				.orElseThrow(() -> new IllegalStateException("No hay detalles"))
				.setExpAccount(expAccount);
			ai.setAccountEntry(InvoiceRecorder.getInvoiceEntry(ctx, ai.getInvoice() ));
			
			System.out.println( "**************** [FACTURA] ****************" );
			InvoiceTextPrinter.print(ai.getInvoice());
			System.out.println( "***************** [APUNTE] ***************" );
			AccountEntryPrinter.print( System.out , ai.getAccountEntry());
			
			
			Asserts.assertEqualsDouble("Total", total, ai.getTotalInvoice());
		}
	}



}
