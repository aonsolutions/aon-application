package net.aonsolutions.occam.impl.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceTextPrinter;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;

class InsertRandomInvoicesTest extends AbstractOccamImplTest {

	@Test
	void test() {
		//int times = AonRandom.integer(1, 50);
		int times = 1;
		for (int count = 0; count < times; count++) {
			Invoice inv = null;
			if (AonRandom.gt(90)) {
				inv = InvoiceFaker.getRandomRetentionInvoice(ctx,DOMAIN_ID,AonRandom.getEnum(WithholdingType.class));
			} else {
				inv = InvoiceFaker.getRandom(ctx, DOMAIN_ID);
			}
			InvoiceTextPrinter.print(inv);
			// Invoice invoice = 
			inv = InvoiceHandler.validate(ctx, inv);
			Invoice saved = InvoiceHandler.saveAndGet(ctx, DOMAIN_ID, inv);
			System.out.println(MessageFormat.format("\t\t [{0}{1}] {2} %"
				,AonStringUtils.repeat("-", count)
				,AonStringUtils.repeat(" ", times - count)
				,AonMathUtils.round( count * 100 / times)
			));
			AonAsserts.assertClassEquals(inv, saved);
			count++;
		}
		System.out.println(MessageFormat.format("\t\t [{0}] ({1} facturas creadas.)"
			,AonStringUtils.repeat("-", times)
			,times));
		assertTrue( 
			InvoiceHeaderHandler.stream(ctx, DOMAIN_ID, p -> p.getDomainProperty().eq(DOMAIN_ID))
				.findFirst()
				.isPresent()
		);
	}

}
