package com.esferalia.aon.occam.test.fiscal.invoice;

import java.text.MessageFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InsertRandomInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = AonDateUtils.getYear( getTestDate() );
		int times = AonRandom.getInt(1, 10);
		for (int count = 0; count < times; count++) {
			Invoice invoice = null;
			if (AonRandom.gt(90)) {
				invoice = AonRandom.generateRandomRetentionInvoice(ctx,getOccam(),AonRandom.getRandomWithholdingType());
			} else {
				if (AonRandom.gt(20)) {
					InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( AonRandom.getRandomYearDay( year ) );
					invoice = InvoiceFaker.getRandomNotSales(params);
				} else {
					InvoiceFakerParams params = new InvoiceFakerParams(ctx).setIssueDate( new Date( ) );
					invoice = InvoiceFaker.getRandomSales(params);
				}
			}
			invoice = AON.insertInvoice(getOccam(),invoice);
			AccountingInvoiceDAO.saveFinances(ctx, invoice);
			if (invoice.isVatAccrualPayment() && AonCollectionUtils.isNotEmpty(invoice.getFinances()) && AonRandom.gt(40)) {
				Finance finance = AonRandom.get(invoice.getFinances());
				if (finance != null && finance.getId() != null) {
					FinanceTracking tracking = new FinanceTracking()
							.setDomain(invoice.getDomain())
							.setFinance(finance)
							.setTrackingDate( AonRandom.getFutureDate(finance.getDueDate()) )
							.setAmount(finance.getAmount() );
					FinanceTrackingDAO.pay(ctx, tracking);
				}
			}
			System.out.println(MessageFormat.format("\t\t ["
					+ AonStringUtils.repeat("-", count)
					+ AonStringUtils.repeat(" ", times - count)+"] "
					+ AonMathUtils.round( count * 100 / times)
					+ " %"
					,times));
			count++;
		}
		System.out.println(MessageFormat.format("\t\t ["
				+ AonStringUtils.repeat("-", times)
				+ "] ("
				+ times + " facturas creadas.)"
				,times));
		Assert.assertTrue( 
			AON.getInvoiceHeaders(ctx, p -> p.getDomainProperty().eq(DOMAIN_ID), 0, 1)
				.findFirst()
				.isPresent()
		);
	}

}
