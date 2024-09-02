package com.esferalia.aon.occam.test.fiscal.invoice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.text.NumberFormat;

import org.junit.jupiter.api.Test;

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
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InsertRandomInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		int year = AonDateUtils.getYear( getTestDate() );
		int times = AonRandom.getInt(1, 10);

		for (int count = 0; count < times; count++) {
			Invoice invoice = null;
			if (AonRandom.gt(90)) {
				invoice = AonRandom.generateRandomRetentionInvoice(ctx,getOccam(),getConfiguration(),AonRandom.getRandomWithholdingType());
			} else {
				InvoiceFakerParams params = new InvoiceFakerParams(ctx,getConfiguration()).setIssueDate( AonRandom.getRandomYearDay( year ) );
				invoice = InvoiceFaker.getRandom(params);
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
			System.out.println(MessageFormat.format("\t\t [{0}% {1}{2}]"
					,AonStringUtils.rightPad(AonMathUtils.round( count * 100 / times,2), 6)
					,AonStringUtils.repeat("-", count)
					,AonStringUtils.repeat(" ", times - count)
					));
			count++;
		}
		System.out.println(MessageFormat.format("\t\t [100.00% {0}]"
				,AonStringUtils.repeat("-", times)
				));
		assertTrue( 
			AON.getInvoiceHeaders(ctx, p -> p.getDomainProperty().eq(DOMAIN_ID), 0, 1)
				.findFirst()
				.isPresent()
		);
	}

}
