package net.aonsolutions.occam.impl.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.FinanceTracking;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceTextPrinter;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;

class InsertRandomInvoicesTest extends AbstractOccamImplTest {

	@Test
	void test() {
		int times = AonRandom.integer(1, 50);
		// int times = 1;
		for (int count = 0; count < times; count++) {
			Invoice inv = null;
			if (AonRandom.gt(90)) {
				inv = InvoiceFaker.getRandomRetentionInvoice(ctx,DOMAIN_ID,AonRandom.getEnum(WithholdingType.class));
			} else {
				inv = InvoiceFaker.getRandom(ctx, DOMAIN_ID);
			}
			InvoiceTextPrinter.print(inv);
			Invoice invoice = InvoiceHandler.save(ctx, DOMAIN_ID, inv);
//			if (invoice.isVatAccrualPayment() && AonCollectionUtils.isNotEmpty(invoice.getFinances()) && AonRandom.gt(40)) {
//				AonRandom.random(invoice.getFinances())
//				.ifPresent(finance -> {
//					FinanceTracking tracking = new FinanceTracking()
//						.setDomain(invoice.getDomain())
//						.setFinance(finance)
//						.setTrackingDate( AonRandom.getFutureDate(finance.getDueDate()) )
//						.setAmount(finance.getAmount() );
//					FinanceTrackingHandler.pay(ctx, DOMAIN_ID, tracking);
//				});
//			}
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
		assertTrue( 
			InvoiceHeaderHandler.stream(ctx, DOMAIN_ID, p -> p.getDomainProperty().eq(DOMAIN_ID))
				.findFirst()
				.isPresent()
		);
	}

}
