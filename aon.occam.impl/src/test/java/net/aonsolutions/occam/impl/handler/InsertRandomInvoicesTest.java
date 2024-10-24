package net.aonsolutions.occam.impl.handler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.AonAsserts;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.InvoiceBreakdown;
import net.aonsolutions.occam.api.model.type.WithholdingType;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;

class InsertRandomInvoicesTest extends AbstractOccamImplTest {

	@Test
	void test() {
		int times = AonRandom.integer(1, 50);
//		int times = 1;
		for (int count = 0; count < times; count++) {
			Invoice inv = null;
			if (AonRandom.gt(90)) {
				inv = InvoiceFaker.getRandomRetentionInvoice(ctx,DOMAIN_ID,AonRandom.getEnum(WithholdingType.class));
			} else {
				inv = InvoiceFaker.getRandom(ctx, DOMAIN_ID);
			}
//			System.out.println( " **************************************");
//			System.out.println( " **************************************");
//			System.out.println( " ************************** [TO SAVE] *");
//			System.out.println( " **************************************");
//			InvoiceTextPrinter.print(inv);
			// Invoice invoice = 
			inv = InvoiceHandler.validate(ctx, DOMAIN_ID, inv);
			Invoice saved = InvoiceHandler.saveAndGet(ctx, DOMAIN_ID, inv);
//			System.out.println();
//			System.out.println();
//			System.out.println( " **************************************");
//			System.out.println( " **************************************");
//			System.out.println( " **************************** [SAVED] *");
//			System.out.println( " **************************************");
//			InvoiceTextPrinter.print(saved);
			System.out.println(MessageFormat.format("\t\t [{0}{1}] {2} %"
				,AonStringUtils.repeat("-", count)
				,AonStringUtils.repeat(" ", times - count)
				,AonMathUtils.round( count * 100 / times)
			));
			AonAsserts.assertClassEquals(
				 inv.getTaxBreakdown().map(tb -> tb.getInvoiceWithholding().orElse(null)).orElse(null)
				,saved.getTaxBreakdown().map(tb -> tb.getInvoiceWithholding()).orElse(null).orElse(null)
			);			
			LinkedList<InvoiceBreakdown> vats1 = inv.vatStream().collect(Collectors.toCollection(LinkedList::new)); 
			LinkedList<InvoiceBreakdown> vats2 = saved.vatStream().collect(Collectors.toCollection(LinkedList::new));
			AonAsserts.assertClassEquals(vats1, vats2);
			
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
